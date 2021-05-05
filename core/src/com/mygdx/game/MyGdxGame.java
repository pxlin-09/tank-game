// MyGdxGame.java
// Jin Hao Dong & Alex Lin
/* This program is a simple game of tanks. Where two tanks are placed on opposite side of the screen.
 * The tank on the left hand side is control by the user using the following key:
 * Left Arrow - move horizontally left
 * Right Arrow - move horizontally right
 * (The tank cannot move out of the screen nor over the middle to the opponent's side)
 * Up Arrow - increase projectile angle
 * Down Arrow - decrease projectile angle
 * (The tank cannot shoot backwards nor downwards, i.e. can only shoot between horizontally straight to vertically up towards the opponent)
 * Space - hold to increase shooting magnitude, release to shoot
 * 
 * There are six enemy tanks. The goal of the game is for user to shoot down all six tanks in separate rounds.
 * All tanks obtain the following stats:
 * Health - initial health of 3, tank is destroyed when health bar is empty
 * Energy - initial energy of 0, add 1 energy each round, once reach 3 a special attack is shot and energy bar empties
 * (Stats are displayed with a health/energy bar on the top of the screen for both user and computer)
 * A tank has a head and a base, when hit base the tank lose 1 health, when hit head the tank lose 2 health;
 * When the special attack hits the tank, the tank is destroyed no matter its health and place of collision.
 * 
 * User and computer take turns shooting each other, computers can also move, adjust shooting angle/magnitude and do special attack.
 * 
 * User loses if their tank gets destroyed during the game, and is brought to a death screen.
 * User wins the game by defeating all enemy tanks and is brought to a victory screen.
 * Intro screen has nothing but a game title and an instruction telling the user to press "enter" to start playing.
 * 
 * Following environmental factors/obstacles are relevant in the game:
 * Gravity - so the bullet will fall towards ground, forming a parabola path.
 * Wind - affects bullet's horizontal velocity. There are four types of wind: strong left, weak left, weak right, strong right, each is indicated with a flag.
 * Wall - in the middle of the screen, with random height and relatively random position; bullet cannot go through wall.
 * Black Hole - in relatively random position on the map, bullet near black hole is pulled to its center and drops completely vertical downward with no horizontal velocity.
 * 
 * Some Detailed Features of the Game (that are not mentioned above):
 * - Two background music are used for intro and game screen. (Musics are played in loops)
 * - Sound effects are used when bullet fires, hits a tank, destroys a tank, hits wall, and hits black hole.
 * - User and computer each have unique face icon to show their current feeling (state of health).
 * - Multiple background pictures are randomly selected for each game.
 * - Multiple tank colors are randomly selected for each game. (User have to defeat all different color tanks in random order to beat the game)
 * - Stats for projectile angle and magnitude are displayed on screen.
 * - The number of tanks the user destroyed is displayed in death screen.
 * - Wall's position & height is changed in every game.
 * - Black Hole's position is changed in every round.
 * - Tanks shoot out bullets in multiple colors, special attack is indicated by an orange bullet with black cross.
 * - User's tank can move in infinite distance but cannot move after shooting and during computer's turn.
 * - Computer's tank only moves in certain & random distance & direction during its turn.
 * - The computer's shot is aimed within a range, making the computer having a certain accuracy. 
 * - The computer's shot is not affected by wind (just for the sake of simplicity).
 */
package com.mygdx.game; // gdx packages and gdx imports

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	
	public static final int USER = 1; // determines if it is user's tank
    public static final int CPU = -1; // or computer's tank
	public static final double GRAVITY = 0.15; // magnitude of gravity
	  
	// setup variables
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	BitmapFont font; 
	
	// images
	Texture windFlags[] = new Texture[4]; // four states of wind flag
	Texture uImg, uGun, cImg, cGun, redPic; // images
	Texture backgrounds[];
	Texture[] bullets = new Texture[6];
	Texture barPicL;
	Texture barPicR;
	Texture yellowPic;
	Texture wallPic;
	Texture holePic;
	Texture introPic;
	Texture deathPic;
	Texture victoryPic;
	Texture[] userFaces;
	Texture[] cpuFaces;
	
	// class objects
	Bullet bullet = null;
	Wind wind = new Wind();
	Random rand = new Random();
	ArrayList<Character> userTanks = new ArrayList<Character>(); // character objects
	ArrayList<Character> cpuTanks = new ArrayList<Character>(); // 6 tank objects of different color
	Character user, cpu, character;
	Wall wall;
	Blackhole hole;
	
	// variables
	int turn = USER; // user goes first
	int score = 0; // number of computer's tank destroyed
	int backgroundSelect = rand.nextInt(7); // select a random background picture
	int cpuDirect = rand.nextInt(600)+800; // how much computer's tank will move in each round
	int holeAngle = 0;
	double cpuShotPower; // magnitude of the bullet shot of computer's tank
	double gunLength;
	double tankWidth;
	double tankHeight;
	double cpuAngle;
	double shotPower = 0;
	boolean cpuMoved = false;
	boolean teleport = false;
	String surface = "intro"; // the current screen
	
	// audios
	Music gameMusic;
	Music introMusic;
	Sound fireSound; 
	Sound destroySound;
	Sound hitSound;
	Sound missSound;
	Sound teleportSound;
	
	public Texture resize(Texture srcTexture,int newWidth,int newHeight) {
	// This method resizes a texture by changing it into a pixmap, resize, then convert back to texture
		if (!srcTexture.getTextureData().isPrepared()) {
			srcTexture.getTextureData().prepare();
		}
		Pixmap srcPixmap = srcTexture.getTextureData().consumePixmap();
		Pixmap newPixmap = new Pixmap(newWidth,newHeight, srcPixmap.getFormat());	// create new pixmap with desired size
		
		newPixmap.drawPixmap(srcPixmap, // copy picture from source pixmap to new pixmap
		        0, 0, srcPixmap.getWidth(), srcPixmap.getHeight(),
		        0, 0, newPixmap.getWidth(), newPixmap.getHeight());
		
		Texture newTexture = new Texture(newPixmap);	// the output texture
		srcPixmap.dispose();
		newPixmap.dispose();
		return newTexture;
	}
	
	public void drawBackground(int n) {
	// this method draws the background image
		batch.draw(backgrounds[n], 0, 0, 1500, 500);
	}
	
	public void drawTanks() {
	// this method draws the user's tank and computer's tank on to the screen
		Texture userGun = user.getGun(), cpuGun = cpu.getGun();
		TextureRegion userGunR = new TextureRegion(userGun,0,0,userGun.getWidth(),userGun.getHeight()), // make a texture region to rotate when drawing
					  cpuGunR = new TextureRegion(cpuGun,0,0,cpuGun.getWidth(),cpuGun.getHeight());
		
		
		batch.draw(userGunR, user.getX(), user.getY(), userGun.getWidth()/2,userGun.getHeight()/2, userGun.getWidth(), userGun.getHeight(),(float)1,(float)1, user.getAngleInDegrees());		
		batch.draw(cpuGunR, cpu.getX(), cpu.getY(), cpuGun.getWidth()/2,cpuGun.getHeight()/2, cpuGun.getWidth(), cpuGun.getHeight(),(float)1,(float)1, cpu.getAngleInDegrees());
		
		batch.draw(user.getTank(), user.getX(), user.getY(),user.getTank().getWidth(),user.getTank().getHeight());
		batch.draw(cpu.getTank(), cpu.getX(), cpu.getY(),cpu.getTank().getWidth(),cpu.getTank().getHeight());
		
	}
	
	public void drawHole(int holeAngle) { 
	// this method draws the image of the black hole
		TextureRegion holePicR = new TextureRegion(holePic,0,0,holePic.getWidth(),holePic.getHeight());
		batch.draw(holePicR, hole.getX() - 50, hole.getY() - 50, hole.getSize()/2,hole.getSize()/2, hole.getSize(), hole.getSize(),(float)1,(float)1, holeAngle);		

	}
	public void drawBullet() {
	// This methods draws the bullet fired by the tank
		if(bullet != null) {
			batch.draw(bullet.getImg(),bullet.getX(),bullet.getY(),bullet.getImg().getWidth(),bullet.getImg().getHeight());
		}
	}
	
	public void drawWindFlag() {
	// This method draws the wind flag according to the direction and speed of wind
		if(wind.getSpeed() > 2.5) {
			batch.draw(windFlags[0], 1500/2-windFlags[0].getWidth()/2, 500 - windFlags[0].getHeight());
		}
		else if(wind.getSpeed() >= 0) {
			batch.draw(windFlags[1], 1500/2-windFlags[1].getWidth()/2, 500 - windFlags[1].getHeight());
		}
		else if(wind.getSpeed() < -2.5) {
			batch.draw(windFlags[3], 1500/2-windFlags[3].getWidth()/2, 500 - windFlags[3].getHeight());
		}
		else if(wind.getSpeed() < 0) {
			batch.draw(windFlags[2], 1500/2-windFlags[2].getWidth()/2, 500 - windFlags[2].getHeight());
		}
		
	}
	
	public void userDied(Character user, ArrayList<Character> cpus) {
	// this method resets the state of all computer's tank once user is defeated
		user.reset();
		for(int i = 0; i < cpus.size(); i++) {
			cpu.reset();
		}
	}
	
	@Override
	public void create () {
	// This method creates the screen, and loads the images and sets up various various variables
		Gdx.graphics.setWindowedMode(1500, 500); // the window will be a long rectangle
		batch = new SpriteBatch();
		
		try { // try and accepting all images
			backgrounds = new Texture[]{new Texture("background/background0.jpg"), new Texture("background/background1.jpg"), new Texture("background/background2.jpg"), 
										new Texture("background/background3.png"), new Texture("background/background4.jpg"), new Texture("background/background5.jpg"),
										new Texture("background/background6.jpg"), new Texture("background/background7.png")};
			introPic = new Texture("intro.png");
			deathPic = new Texture("death.png");
			
			victoryPic = new Texture("victory.png");
			wallPic = new Texture("wall.png");
			wall = new Wall(700+100, 0, 30, 50 + rand.nextInt(100));
			hole = new Blackhole(100 + rand.nextInt(1200), 100 + rand.nextInt(250), 120);
			holePic = new Texture("blackhole.png");
			userFaces = new Texture[]{new Texture("face/user3.png"), new Texture("face/user2.png"), new Texture("face/user1.jpg"), new Texture("face/user0.jpg")};
			cpuFaces = new Texture[]{new Texture("face/cpu3.png"), new Texture("face/cpu2.png"), new Texture("face/cpu1.jpg"), new Texture("face/cpu0.png")};
			redPic = new Texture("red.png");
			barPicL = new Texture("barL.png"); 
			barPicR = new Texture("barR.png"); 
			yellowPic = new Texture("yellow.png"); 
			
			for(int i = 0; i < 4; i++) {
				windFlags[i] = new Texture(String.format("windFlag/flag_%d.png",i));
				windFlags[i] = resize(windFlags[i], windFlags[i].getWidth()/3, windFlags[i].getHeight()/3);
			}
			
			for(int i = 0; i < 6; i++) {
				bullets[i] = new Texture(String.format("bullet/bullet_%d.png", i));
				bullets[i] = resize(bullets[i], bullets[i].getWidth()/15, bullets[i].getHeight()/15);
			}
			
			for(int i = 0; i < 6; i++) {
			// loads each picture for each character
				uImg = new Texture((String.format("userTanks/tank%d.png", i)));
				uImg = resize(uImg,uImg.getWidth()/5,uImg.getHeight()/5);
				
				uGun = new Texture((String.format("userTanks/tank%d.png", i+6)));
				uGun = resize(uGun,uGun.getWidth()/5,uGun.getHeight()/5);
				
				cImg = new Texture((String.format("cpuTanks/tank%d.png", i)));
				cImg = resize(cImg,cImg.getWidth()/5,cImg.getHeight()/5);
				
				cGun = new Texture((String.format("cpuTanks/tank%d.png", i+6)));
				cGun = resize(cGun,cGun.getWidth()/5,cGun.getHeight()/5);
				
				tankWidth = uImg.getWidth();
				tankHeight = uImg.getHeight();
				gunLength = 213/5;
				
				userTanks.add(new Character(3, 0, 0, USER));
				cpuTanks.add(new Character(3, 0, 0, CPU));
				userTanks.get(i).setImg(uImg, uGun); // All the tanks will be stored in the list
				cpuTanks.get(i).setImg(cImg, cGun);
			}
		}
		catch(Exception e) {
			System.out.println("image not found");
		}
		
		try { // try and accepting all audios
			gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Game Music.mp3"));
			introMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Intro Music.mp3"));
			introMusic.setLooping(true);
			introMusic.play();
			fireSound = Gdx.audio.newSound(Gdx.files.internal("audio/Tank Fire.mp3")); 
			destroySound = Gdx.audio.newSound(Gdx.files.internal("audio/Tank Down.mp3"));
			hitSound = Gdx.audio.newSound(Gdx.files.internal("audio/Tank Hit.mp3"));
			missSound = Gdx.audio.newSound(Gdx.files.internal("audio/Tank Miss.mp3"));
			teleportSound = Gdx.audio.newSound(Gdx.files.internal("audio/Teleport.mp3"));
		}
		catch(Exception e) {
			System.out.println("music not found");
		}
		
		user = userTanks.get(rand.nextInt(6)); // randomly select a tank color for user
		cpu = cpuTanks.get(rand.nextInt(6)); // randomly select the first tank color of computer
		user.randSpot(); // randomly sets a spot for user and computer
		cpu.randSpot();
		
		Gdx.input.setInputProcessor(this);
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.getData().setScale(.7f);
		font.setColor(1, 1, 1, 1);
		 
	}

	@Override
	public void render () {
	// this method run in a loop and is the main section that processes the game
		if (surface == "intro") { // intro screen
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			batch.draw(introPic, 0, 0, 1500, 500); // draws intro picture
			
			batch.end();
			if (pressedEnter()) { // if the user pressed enter key, the game begins
				introMusic.stop();
				gameMusic.setLooping(true);
				gameMusic.play();
				surface = "battle";
			}
		}
		else if (surface == "battle") { // battle screen
			if(turn == USER) // in user's turn
			{	
				// check for user's key input
				if(pressedUpArrow()&& bullet==null) // rise tank gun when pressed up arrow key
				{
					user.userRotateLeft(); 
				}
				else if(pressedDownArrow()&& bullet==null) // lower tank gun when pressed down arrow key
				{
					user.userRotateRight();
				}
				
				if (pressedLeftArrow()&& bullet==null) // moves tank left when pressed left arrow key
				{
					user.moveLeft();
				}
				else if (pressedRightArrow() && bullet==null) // moves tank right when pressed right arrow key
				{
					user.moveRight();
				}
				
				if(pressedSpace() && bullet==null) // shoots the bullet when pressed space key
				{
					if(shotPower < 20)
					{
						shotPower += 0.5;
					}
				}
				
				if(bullet != null) // if the bullet is flying in air
				{
					bullet.fly(GRAVITY); // update its position by its movement
					
					if (bullet.collideHead(user)) // if bullet hits the head of user's tank
					{
						user.getHit(cpu.getEnergy()); // deals two damage to user
						user.getHit(cpu.getEnergy());
						
						bullet.endFly(); // the bullet stops flying
						hitSound.play();
					}
					else if (bullet.collideBase(user)) // if bullet hits the base of user's tank
					{
						user.getHit(cpu.getEnergy());
						
						bullet.endFly();
						hitSound.play();
					}				
					else if (bullet.collideHead(cpu)) // if bullet hits the head of computer's tank
					{	
						cpu.getHit(user.getEnergy());
						cpu.getHit(user.getEnergy());
						
						bullet.endFly();
						hitSound.play();
					}
					else if (bullet.collideBase(cpu)) // if bullet hits the base of computer's tank
					{
						cpu.getHit(user.getEnergy());
						
						bullet.endFly();
						hitSound.play();
					}
					
					else if (bullet.collideWall(wall)) { // if bullet hits the wall
						bullet.endFly(); // bullet stops, cannot move any further
						missSound.play();
					}
					
					else if (bullet.collideBlackhole(hole)) { // if bullet hits the black hole
						if (!teleport) { // teleport the bullet only once
							bullet.changePos(hole.getX(), hole.getY()); // teleport the bullet to the center of the black hole
							bullet.clearMovement(); // and clear its velocity so it will drop straight down
							teleport = true;
							teleportSound.play();
						}
					}
					
					else if(!bullet.inAir()) // if the bullet is not flying
					{
						bullet = null; // clear the bullet
						wind = new Wind(); // setup a new wind
						cpuDirect = rand.nextInt(570)+830; // setup computer tank's movement
						teleport = false;
					}
					
					if(bullet == null)
					{
						turn = CPU; // end user's turn
					}
					if(cpu.getHealth() <= 0) // if computer's tank is destroyed
					{ 
						cpuTanks.remove(cpu); // grabs a new tank from the remaining colors
						cpu = cpuTanks.get(rand.nextInt(cpuTanks.size()));
						cpu.randSpot();
						destroySound.play();
						score += 1;
						if (score == 6) { // the user wins if this is the last tank
							surface = "victory";
						}
					}
					
					if(user.getHealth() <= 0) // if user's tank is destroyed
					{ 	
						destroySound.play();
						surface = "death"; // end the game
					}
				}
			}
			else // computer's turn
			{	
				if (cpu.getX()!= cpuDirect) // moves computer's tank to the assigned position
				{
					if (cpu.getX()< cpuDirect) // move the tank right if needed
					{
						cpu.cpuMoveRight(1);
						if (cpu.getX() == cpuDirect) { // once finish moving, determines an angle
							cpuAim(60); // and magnitude to shoot the bullet so it contains a relatively good accuracy
						}
					}
					else // move the tank left if needed
					{
						cpu.cpuMoveLeft(1);
						if (cpu.getX() == cpuDirect) {
							cpuAim(60);
						}
					}
					cpuMoved = true;
				}
				else
				{
					if(cpuAngle < cpu.getAngleInDegrees()) // aim tank's gun at the assigned angle
					{
						cpu.cpuRotateRight(); // rise gun if needed
						if(cpuAngle >= cpu.getAngleInDegrees())
						{
							cpu.setAngle(cpuAngle);
						}
					}
					else if(cpuAngle > cpu.getAngleInDegrees())
					{
						cpu.cpuRotateLeft(); // lowers gun if needed
						if(cpuAngle <= cpu.getAngleInDegrees())
						{
							cpu.setAngle(cpuAngle);
						}
					}
					else
					{
						if(bullet == null)
						{
							bullet = new Bullet(cpu.getAngle(),cpu.getX()+tankWidth/2-gunLength*Math.cos(cpu.getAngle()), cpu.getY()+tankHeight/2-gunLength*Math.sin(cpu.getAngle()));
							fireSound.play();
							if (cpu.getEnergy() == 3) {
								bullet.setImg(bullets[5]);
							}
							else {
								bullet.setImg(bullets[rand.nextInt(5)]);
							}
							bullet.cpuShot(-cpuShotPower); // shots the bullet with assigned magnitude
							if (cpu.getEnergy() == 3) {
								cpu.clearEnergy();
							}
							else {
								cpu.energyUp();
							}
						}
						else
						{	
							
							bullet.fly(GRAVITY); // works the same way as in user's turn
							if (bullet.collideHead(user)) 
							{
								user.getHit(cpu.getEnergy());
								user.getHit(cpu.getEnergy());
								
								bullet = null;
								hitSound.play();
							}
							else if (bullet.collideBase(user)) 
							{
								user.getHit(cpu.getEnergy());
								
								bullet = null;
								hitSound.play();
							}
							
							else if (bullet.collideHead(cpu)) 
							{
								cpu.getHit(user.getEnergy());
								cpu.getHit(user.getEnergy());
								
								bullet = null;
								hitSound.play();
							}
							else if (bullet.collideBase(cpu)) 
							{
								cpu.getHit(user.getEnergy());
								
								bullet = null;
								hitSound.play();
							}
							
							else if (bullet.collideWall(wall)) {
								bullet.endFly();
								missSound.play();
							}
							
							else if (bullet.collideBlackhole(hole)) {
								if (!teleport) {
									bullet.changePos(hole.getX(), hole.getY());
									bullet.clearMovement();
									teleport = true;
									teleportSound.play();
								}
							}
							
							else if(!bullet.inAir())
							{
								bullet = null;
								wind = new Wind();
								teleport = false;
								hole = new Blackhole(100 + rand.nextInt(1200), 100 + rand.nextInt(250), 120);
							}
							
							if(bullet == null)
							{
								turn = USER; // end computer's turn
							}
							
							if(cpu.getHealth() <= 0)
							{ 
								cpuTanks.remove(cpu);
								cpu = cpuTanks.get(rand.nextInt(cpuTanks.size()));
								cpu.randSpot();
								destroySound.play();
								score += 1;
								if (score == 6) {
									surface = "victory";
								}
							}
							
							if(user.getHealth() <= 0)
							{ 	
								userDied(user, cpuTanks);
								user = userTanks.get(rand.nextInt(userTanks.size()));
								user.randSpot();
								destroySound.play();
								surface = "death";
							}
						}
					}
				}
					cpuMoved = false;
			}
			
			// draw images
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			drawBackground(backgroundSelect); // draws background
			
			drawTanks(); // draws tank, bullet, and wind flag
			drawBullet();
			drawWindFlag();
			
			font.draw(batch, "Power: "+ shotPower, user.getX()-18, 100); // show shooting magnitude
			font.draw(batch,"Angle: "+ (float)Math.round(user.getAngleInDegrees()*10)/10, user.getX()-18, 90); // show shooting angle
			drawHole(holeAngle++); // draw the rotating black hole
			batch.draw(wallPic, wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight()); // draws the wall
			drawBar((int)user.getHealth(), user.getEnergy(), (int)cpu.getHealth(), cpu.getEnergy(), barPicL, barPicR, redPic, yellowPic); // draws health bar
			
			batch.end();
			
			// draws a bar displaying the magnitude of the shot
			shapeRenderer.begin(ShapeType.Line);
		    shapeRenderer.rect(user.getX()-8, user.getY()+5, 4, 40, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
		    shapeRenderer.end();
		    
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.rect(user.getX()-8, user.getY()+5, 4, (float)shotPower*2, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
		    shapeRenderer.end();
		}
		else if (surface == "death") { // defeat screen
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			batch.draw(deathPic, 0, 0, 1500, 500); // draws the defeat screen
			font.getData().setScale(2f);
			font.draw(batch, "You have destroyed "+ score + " enemy tanks!", 500,100); // show the number of computer tank destroyed
			font.getData().setScale(.7f);
			batch.end();
		}
		else { // victory screen
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
		
			batch.draw(victoryPic, 0, 0, 1500, 500); // draws the victory screen
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
	// this method disposes some variables when ending the program
		batch.dispose();
		user.getTank().dispose();
		cpu.getTank().dispose();
	}
	
	public boolean pressedEnter() {
	// this method determines if the user has pressed the enter key
		if(Gdx.input.isKeyPressed(Input.Keys.ENTER))
		{
			return true;
		}
		return false;
	}
	
	public boolean pressedLeftArrow() {
	// this method determines if the user has pressed the left arrow key
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			return true;
		}
		return false;
	}
	
	public boolean pressedRightArrow() {
	// this method determines if the user has pressed the right arrow key
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			return true;
		}
		return false;
	}
	
	public boolean pressedUpArrow() {
		// this method determines if the user has pressed the up arrow key
		if(Gdx.input.isKeyPressed(Input.Keys.UP))
		{
			return true;
		}
		return false;
	}
	
	public boolean pressedDownArrow() {
	// this method determines if the user has pressed the down key
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			return true;
		}
		return false;
	}
	
	public boolean pressedSpace() {
	// this method determines if the user has pressed the space key
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			return true;
		}
		return false;
	}
	
	public void cpuAim(int factor) {
	/* This method determines the magnitude and angle of computer's shot
	 * so the bullet will always land near the user's tank.
	 * The size of the range can be adjust using input variables factor.
	 */
		int p = 1 + rand.nextInt(20); // power 1 - 20
		double a = rand.nextInt(51)+30; // angle 30 - 80
		double t = Math.abs(p * Math.sin(a) / (GRAVITY * 0.5)); // time
		double d = Math.abs(p * Math.cos(a) * t); // distance
		double r1 = cpu.getX() - user.getX() - factor; // range
		double r2 = cpu.getX() - user.getX() + factor;
		while (!(d >= r1) || !(d <= r2)) { // continue to recreate until a qualified angle & magnitude is found 
			p = 1 + rand.nextInt(20); // power 1 - 20
			a = rand.nextInt(51)+30; // angle 30 - 80
			t = Math.abs(p * Math.sin(a) / (GRAVITY * 0.5)); // time
			d = Math.abs(p * Math.cos(a) * t); // distance
			r1 = cpu.getX() - user.getX() - factor; // range
			r2 = cpu.getX() - user.getX() + factor;
		}
		cpuShotPower = p; // change the computer's shot angle & magnitude by changing its variables
		cpuAngle = a * (-1);
	}
	
	public void drawBar(int userHealth, int userPower, int cpuHealth, int cpuPower, Texture barPicL, Texture barPicR, Texture redPic, Texture yellowPic) {
	// this method draws a information bar that shows the health, energy, and "feeling" of the tanks for user and computer
		
		// user
		if (userHealth == 3) {
			batch.draw(redPic, 100, 475, 315, 30);
			batch.draw(redPic, 400, 485, 30, 10);
			batch.draw(userFaces[0], 0, 450, 100, 50);
		}
		else if (userHealth == 2) {
			batch.draw(redPic, 100, 475, 240, 30);
			batch.draw(userFaces[1], 0, 450, 100, 50);
		}
		else if (userHealth == 1) {
			batch.draw(redPic, 100, 475, 145, 30);
			batch.draw(userFaces[2], 0, 450, 100, 50);
		}
		else {
			batch.draw(userFaces[3], 0, 450, 100, 50);
		}
		
		if (userPower == 3) {
			batch.draw(yellowPic, 100, 455, 248, 20);
			batch.draw(yellowPic, 340, 460, 16, 10);
		}
		if (userPower == 2) {
			batch.draw(yellowPic, 100, 455, 178, 20);
		}
		if (userPower == 1) {
			batch.draw(yellowPic, 100, 455, 123, 20);
		}
		
		// computer
		if (cpuHealth == 3) {
			batch.draw(redPic, 1085, 475, 320, 30);
			batch.draw(redPic, 1067, 485, 25, 10);
			batch.draw(cpuFaces[0], 1410, 455, 80, 50);
		}
		else if (cpuHealth == 2) {
			batch.draw(redPic, 1165, 475, 240, 30);
			batch.draw(cpuFaces[1], 1410, 455, 80, 50);
		}
		else if (cpuHealth == 1) {
			batch.draw(redPic, 1250, 475, 140, 30);
			batch.draw(cpuFaces[2], 1410, 455, 80, 50);
		}
		else {
			batch.draw(cpuFaces[2], 1410, 455, 80, 50);
			batch.draw(cpuFaces[3], 1410, 455, 80, 50);
		}
		
		if (cpuPower == 3) {
			batch.draw(yellowPic, 1160, 455, 240, 20);
			batch.draw(yellowPic, 1144, 460, 28, 10);
		}
		if (cpuPower == 2) {
			batch.draw(yellowPic, 1220, 455, 168, 20);
		}
		if (cpuPower == 1) {
			batch.draw(yellowPic, 1275, 455, 118, 20);
		}
		
		batch.draw(barPicL, 0, 450, 460, 50); // frame pictures
		batch.draw(barPicR, 1040, 450, 460, 50);
	}
	
	@Override
	public boolean keyUp(int keycode) {
	// this method determines if the user has released the space key
		if(turn == USER)
		{
			if(keycode == Input.Keys.SPACE && bullet == null) // space key is released and there is no existing bullet
			{	
				// a bullet is shot
				bullet = new Bullet(user.getAngle(),user.getX()+tankWidth/2+gunLength*Math.cos(user.getAngle()), user.getY()+tankHeight/2+gunLength*Math.sin(user.getAngle()));
				fireSound.play();
				if (user.getEnergy() == 3) { // if it is a special shot
					bullet.setImg(bullets[5]); // draws a special bullet image
				}
				else { // otherwise the bullet image is balls of random color
					bullet.setImg(bullets[rand.nextInt(5)]);
				}
				bullet.userShot(shotPower, wind.getSpeed());
				shotPower = 0;
				
				if (user.getEnergy() == 3) { // clears user's energy if the special bullet has released
					user.clearEnergy();
				}
				else { // otherwise increase the energy by 1
					user.energyUp();
				}
			}
		}
		return false;
	}
	
	// these methods are generated but are not used
	@Override
	public boolean keyDown(int keycode) {
		//TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}