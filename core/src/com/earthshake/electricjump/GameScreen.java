package com.earthshake.electricjump;


import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.SwarmStoreItem;
import com.swarmconnect.SwarmUserInventory;

public class GameScreen implements Screen{
	EJGame 					game;
	
	Texture					gameBackground1,
							gameBackground2;
							
	
	
	Sound					beep1,beep2;
	
	//contains most of the sprites required for rendering game objects
	TextureAtlas			buttons;
	
	//sprites for the game background
	Sprite					gameBack_1,
							gameBack_2;
	
	//sprites for the buttons 
	Sprite 					greenButton,
							redButton,
							boltButton,
							timeButton,
							twentyButton,
							fiftyButton,
							hundredButton,
							reviveButton,
							resetButton,
							submitButton,
							gameOverReviveBtn,
							swarmHeartImg,
							yes,
							no,
							buy;

	Image					gameOverScreen;
	Stage					stage;
	
	//camera for the screen
	OrthographicCamera 		cam;
	
	Array<Sprite>			buttonSprites;
	Array<Rectangle>		rectLanes;
	private IntArray		greenButtonIndex;
	
	//this is the amount of pixels the game backgrounds and buttons will move when user touches the screen
	private float			distanceToMove;
	// needed to make a smooth translation of the object position
	private float			moveSpeed;
	//how much the objects have moved thus far
	private float			tempDistanceMoved;
	
	private int				gameScore,
							numOfTouchEvents,
							laneHit,
							scoreAdd;  //Keep track of how many times user has touched the screen
	
	private float			buttonPositionX,buttonPositionXNextOffset,
							buttonPositionY,buttonPositionYNextOffset,
							buttonSize,
							timeLeft,
							scaleGameOverButtons,
							fontAlpha,fontScale,fontMove;
	
	Pool<Sprite> 			sprites;
	
	ParticleEffect			effect;
	
	//This boolean will track the movement of the screen and the game objects when user has touched the screen
	private boolean			stopMoving,
							showGameOver,
							reverseFontAnimation,
							timeButtonClickedFont;
	
	//Generator for random numbers 
	private Random			generator;
	
	private boolean			showReviveScreen;
	
	
	enum GameState{
		Initial,
		Paused,
		Playing,
		GameOver;
	};
	GameState 				state;
	
	
	
	
	public GameScreen(EJGame gam) {
		game = gam;
	}

	@SuppressWarnings("static-access")
	@Override
	public void show() {
		game.setSwarmInventory();
		
		Gdx.input.setCatchBackKey(true);
		
		
		
		
		//Particle
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("textures/particle.p"), Gdx.files.internal("textures/"));
				
		//get textures and assign texture regions to sprites
		buttons					= game.manager.get("textures/buttons_misc.pack", 	TextureAtlas.class);
		gameBackground1			= game.manager.get("textures/game_back_1.jpg", 		Texture.class);
		gameBackground2			= game.manager.get("textures/game_back_2.jpg", 		Texture.class);
		
		beep1					= game.manager.get("music/beep1.wav", 				Sound.class);
		beep2					= game.manager.get("music/beep2.mp3", 				Sound.class);
	
		redButton				= buttons.createSprite("red_button");			
		greenButton 			= buttons.createSprite("green_button");
		boltButton				= buttons.createSprite("bolt_button");
		twentyButton			= buttons.createSprite("twenty_button");			
		fiftyButton 			= buttons.createSprite("fifty_button");
		hundredButton			= buttons.createSprite("hundred_button");
		timeButton				= buttons.createSprite("time_button");
		reviveButton			= buttons.createSprite("revive_button");
		resetButton				= buttons.createSprite("button_reset_game");
		submitButton			= buttons.createSprite("button_submit_score");
		gameOverReviveBtn		= buttons.createSprite("button_revive");
		swarmHeartImg			= buttons.createSprite("swarm_heart");
		
		yes						= buttons.createSprite("yes");
		no						= buttons.createSprite("no");
		buy						= buttons.createSprite("buy");
		
		resetButton.setScale(0f);
		submitButton.setScale(0f);
		gameOverReviveBtn.setScale(0f);
		resetButton.setPosition(138, 580);
		submitButton.setPosition(355, 580);
		gameOverReviveBtn.setPosition(246, 650);
		swarmHeartImg.setPosition(260, 755);
		yes.setPosition(136, 675);
		no.setPosition(289, 675);
		buy.setPosition(445, 675);
		
		scaleGameOverButtons	= 0f;
		
		gameBack_1				= new Sprite(gameBackground1);
		gameBack_2				= new Sprite(gameBackground2);
	
		//Dynamic Array for the button objects 
		buttonSprites 			= new Array<Sprite>();
		rectLanes 				= new Array<Rectangle>();
		greenButtonIndex 		= new IntArray(4);
		
		
		
		
		//set default values for sprites and game objects
		gameBack_2.setPosition(0, 1280);
		
		
		//Set the game state at the start. User is required to touch the screen
		state 					= GameState.Initial;
		game.font.setColor(1, 1, 1, 1);
		generator 				= new Random(System.currentTimeMillis());
		
		//Camera creation
		cam = new OrthographicCamera();
		cam.setToOrtho(false, game.WIDTH, game.HEIGHT);
		
		
		gameOverScreen			= new Image(buttons.findRegion("game_over_screen"));
		stage 					= new Stage(new StretchViewport(game.WIDTH, game.HEIGHT,cam));
		stage.addActor(gameOverScreen);
		 
		game.font.setScale(1);
		game.font2.setScale(1);
		game.font3.setScale(1);
		
		//Assign values to variables
		
		distanceToMove				= 320;
		moveSpeed					= -40; 
		tempDistanceMoved			= 0;
		gameScore					= 0;
		numOfTouchEvents			= 0;
		buttonPositionX				= 63;
		buttonPositionY				= 106;
		buttonPositionXNextOffset	= 54;
		buttonPositionYNextOffset	= 210;
		buttonSize					= 110;
		stopMoving					= true;
		timeLeft					= 20;
		scoreAdd					= 0;
		fontAlpha					= 0;
		fontScale					= 0.00001f;
		fontMove					= game.HEIGHT - game.font3.getBounds("5").height;
		reverseFontAnimation		= false;
		timeButtonClickedFont		= false;
		showReviveScreen			= false;
		
		rectLanes.add(new Rectangle(0, 0, game.WIDTH/4, game.HEIGHT));
		rectLanes.add(new Rectangle(game.WIDTH/4, 0, game.WIDTH/4, game.HEIGHT));
		rectLanes.add(new Rectangle(game.WIDTH/2, 0, game.WIDTH/4, game.HEIGHT));
		rectLanes.add(new Rectangle(game.WIDTH/4 + game.WIDTH/2, 0, game.WIDTH/4, game.HEIGHT));
		
		//fill the array of buttons with 16 sprites
		for(int i = 0; i<5; i++){
			for(int j=0; j<4; j++){
				int randomInt2 = generator.nextInt(4)+1;
				Sprite temp;
				if(randomInt2 != 4)
					temp = new Sprite(redButton);
				else
					temp = new Sprite(boltButton);
				temp.setPosition(buttonPositionX + (buttonPositionXNextOffset+buttonSize)*j,
						buttonPositionY + (buttonPositionYNextOffset+buttonSize)*i);
				buttonSprites.add(temp);
				
			}
		}
		
		//set some of the sprites to have the greenbutton image which the user needs to press;
		for(int index = 0; index <5; index++){
			int randomInt = generator.nextInt(4)+1;
			buttonSprites.get((index*4 + randomInt)-1).setRegion(greenButton);
			greenButtonIndex.add(randomInt);
		}
		
	
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cam.update();
		
		
		//if user presses back key on his phone return to main menu
		
		if(Gdx.input.isKeyPressed(Keys.BACK))
			game.setScreen(new MainMenuScreen(game));
		
		//batch rendering
		game.batch.setProjectionMatrix(cam.combined);
		game.batch.begin();
		
		gameBack_1.draw(game.batch);
		gameBack_2.draw(game.batch);	
	
		if(state == GameState.Playing){
			initPlayingState();
			
			for(Sprite sprite:buttonSprites)
				sprite.draw(game.batch);
			
			game.font3.setColor(1, 1, 1, 1);
			game.font3.setScale(1);
			game.font3.draw(game.batch, Integer.toString(gameScore), game.WIDTH - game.font3.getBounds(Integer.toString(gameScore)).width, game.HEIGHT);
			
			if(timeLeft<5){
				game.font3.setColor(Color.RED);
				game.font3.draw(game.batch, Float.toString(timeLeft).substring(0, 4), 20, game.HEIGHT);
				game.font3.setColor(Color.WHITE);
			}else
				game.font3.draw(game.batch, Float.toString(timeLeft).substring(0, 4), 20, game.HEIGHT);
			
			//iterate through the array of sprites in order to draw them
			
			playScoreAnimation(scoreAdd);
		}
		
		
		
		effect.draw(game.batch, delta);
		
		
		//game.font.draw(game.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 50, 500);
		//game.font.draw(game.batch, Integer.toString(greenButtonIndex.get(0)), 100, 1000);
		//game.font.draw(game.batch, Integer.toString(numOfTouchEvents), 100, 1000);
		
		game.batch.end();
		
		
//
//**********  START INITIAL STATE     *********************************************************************
//
		if(state == GameState.Initial){
			
			gameBack_1.setColor(Color.GRAY);
	
			game.batch.begin();
			game.font.drawMultiLine(game.batch, "TOUCH SCREEN TO\nSTART", 0, 600, 720, HAlignment.CENTER);
			game.batch.end();
	
			if(Gdx.input.justTouched()){
				gameBack_1.setColor(Color.WHITE);
				state = GameState.Playing;
			}
		}
		
//
//***********  END INITIAL STATE      ********************************************************************
//
		
//
//***********  START PLAYING STATE      ********************************************************************
//
		
		
		if(state == GameState.Playing){
			timeLeft -= delta;
			if(timeLeft < 0){
				setGameOverState();
			}
			
			processGameInput();
			
			//Move the game objects in order to progress the game
			if(!stopMoving){
				//move the background on each finger tap
				game.SpriteMoveBy(gameBack_1, moveSpeed, false);
				game.SpriteMoveBy(gameBack_2, moveSpeed, false);
				
				for(Sprite sprite:buttonSprites){
					game.SpriteMoveBy(sprite, moveSpeed, false);
				}
				
				//know when to stop the moving 
				tempDistanceMoved+=moveSpeed;
				if(tempDistanceMoved<=-distanceToMove){
					addSprites();
					removeSprites();
					stopMoving = true;
					laneHit = -1;
				}
				
				//repeating background image
				if(gameBack_1.getY()<=-1280)
					gameBack_1.setY(1280);
				if(gameBack_2.getY()<=-1280)
					gameBack_2.setY(1280);
				
			}
		}
		
//
//***********   END PLAYING STATE      ********************************************************************
//
		
//
//***********   START GAME OVER STATE   ******************************************************************
//
		
		if(state == GameState.GameOver){
			gameBack_1.setColor(Color.GRAY);
			gameBack_2.setColor(Color.GRAY);
			
			
			stage.act();
			stage.draw();
			
			if(Gdx.input.justTouched()){
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				cam.unproject(touchPos);
				
				if(!showReviveScreen){
					
					if(game.spriteTouched(gameOverReviveBtn, touchPos)){
						
						showReviveScreen = true;
						touchPos = new Vector3();
					}
						
				}
				
				if(game.spriteTouched(resetButton, touchPos))
					game.setScreen(new GameScreen(game));
				
				if(game.spriteTouched(submitButton, touchPos)){
					if(game.preferSwarm)
						SwarmLeaderboard.submitScoreAndShowLeaderboard(17511, (float) gameScore);
					else
						game.setScreen(new MainMenuScreen(game));
				}
				
				if(showReviveScreen){
				
					if(game.spriteTouched(yes, touchPos)){
						if(game.swarmConsumableCount+game.prefs.getInteger("numOfRevives")>0){
								
							if(game.prefs.getInteger("numOfRevives")>0){
								game.prefs.putInteger("numOfRevives", game.prefs.getInteger("numOfRevives")-1);
								game.prefs.flush();
							}
							else if(game.swarmConsumableCount>0&&game.preferSwarm)
								Swarm.user.consumeItem(1841);
							timeLeft += 10f;
							state = GameState.Playing;
								
						}else
							Swarm.showStore();
					}
					
					if(game.spriteTouched(no, touchPos)){
						showReviveScreen = false;
					}
					
					if(game.spriteTouched(buy, touchPos)){
						if(game.preferSwarm)
							Swarm.showStore();
						else
							game.setScreen(new MainMenuScreen(game));
					}
						
					
				}
					
				
			}
			
			if(showGameOver){
				if(scaleGameOverButtons < 1f)
					scaleGameOverButtons += 0.08f;
				resetButton.setScale(scaleGameOverButtons);
				submitButton.setScale(scaleGameOverButtons);
				gameOverReviveBtn.setScale(scaleGameOverButtons);
				
				game.font.setScale(scaleGameOverButtons);
				game.font2.setScale(scaleGameOverButtons);
				
				game.font.setColor(1, 1, 1, 0.8f);
				game.font2.setColor(1,1,1,0.8f);
			
				game.batch.begin();
				
				resetButton.draw(game.batch);
				submitButton.draw(game.batch);
				
				if(!showReviveScreen){
					game.font.draw(game.batch, "GAME OVER", (game.WIDTH - game.font.getBounds("GAME OVER").width)/2, 840);
					game.font2.draw(game.batch, "SCORE:" +  Integer.toString(gameScore), (game.WIDTH - game.font2.getBounds("SCORE:" +  Integer.toString(gameScore)).width)/2, 780);
					gameOverReviveBtn.draw(game.batch);
				}else{
					game.font2.draw(game.batch, "x", 350, 805);
					game.font.draw(game.batch, Integer.toString(game.prefs.getInteger("numOfRevives")+game.swarmConsumableCount), 395, 815);
					swarmHeartImg.draw(game.batch);
					
					yes.draw(game.batch);
					no.draw(game.batch);
					buy.draw(game.batch);
					
				}
				
				game.batch.end();
			
			}
			
		}
		
		
//
//************  END GAME OVER STATE      ******************************************************************
//
		
	}
	
//	
//**********   END OF RENDER METHOD       *******************************************************************
//
	
	
	
	//process input for the playing state
	public void processGameInput(){
		if(Gdx.input.justTouched()){
			Vector3 touchPos 	= new Vector3();
			Vector2 touch2D		= new Vector2();
			
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			cam.unproject(touchPos);
			
			touch2D.set(touchPos.x, touchPos.y);
			
			numOfTouchEvents++;
			
			fontAlpha =0f;
			fontScale =0.000001f;
			reverseFontAnimation = false;
			timeButtonClickedFont =false;
			
			//libgdx sucks so I have to do this to make no unwanted touch event occur from the user at the start of the game
			if(numOfTouchEvents >1&&stopMoving){
				
				Iterator<Rectangle> iterLanes = rectLanes.iterator();
				
				while(iterLanes.hasNext()){
					
					Rectangle rect = iterLanes.next();
					if(rect.contains(touch2D))
						laneHit = rectLanes.indexOf(rect, true) + 1;
						
				}
				
				if(greenButtonIndex.get(0) == laneHit){
					beep1.play();
					gameScore +=5;
					scoreAdd=5;
					effect.setPosition(buttonSprites.get(laneHit-1).getX(), buttonSprites.get(laneHit-1).getY());
					effect.start();
					
					greenButtonIndex.removeIndex(0);
					
					tempDistanceMoved = 0;
					stopMoving = false;
					
				}else{
					
					beep1.play();
					
					
					if(checkUVOverlap(buttonSprites.get(laneHit-1), reviveButton)){
						game.prefs.putInteger("numOfRevives", game.prefs.getInteger("numOfRevives")+1);
						game.prefs.flush();
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), hundredButton)){
						gameScore +=100;
						scoreAdd =100;
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), fiftyButton)){
						gameScore+=50;
						scoreAdd =50;
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), twentyButton)){
						gameScore+=20;
						scoreAdd =20;
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), timeButton)){
						timeLeft +=5.0f;
						gameScore+=10;
						scoreAdd =10;
						timeButtonClickedFont=true;
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), redButton)){
						setGameOverState();
					}else if(checkUVOverlap(buttonSprites.get(laneHit-1), boltButton)){
						setGameOverState();
					}	

						
					effect.setPosition(buttonSprites.get(laneHit-1).getX(), buttonSprites.get(laneHit-1).getY());
					effect.start();
					greenButtonIndex.removeIndex(0);
					
					
					
					
					tempDistanceMoved = 0;
					stopMoving = false;
					
				}
				
				
			}
			
		}
		
	}

	private void playScoreAnimation(int score){
		if(score==0)
			return;
		
		game.font3.setColor(1, 1, 1, fontAlpha);
		game.font3.setScale(fontScale);
		
		if(!reverseFontAnimation){
			if(fontAlpha<1f){
				fontAlpha+=0.1f;
				fontScale+=0.1f;
			}else
				reverseFontAnimation = true;
		}
		
		if(reverseFontAnimation){
			if(fontAlpha>0){
				fontAlpha-=0.1f;
				fontScale-=0.1f;
			}else{
				scoreAdd=0;
				reverseFontAnimation=false;
			}
		}
		
		game.font3.draw(game.batch, "+" + Integer.toString(score), game.WIDTH - game.font3.getBounds("+" + Integer.toString(score)).width, fontMove);
		if(timeButtonClickedFont)
			game.font3.draw(game.batch, "+5.00", 20, fontMove);
	}
	
	private void setGameOverState() {
		game.gameOverCount +=1;
		if(game.gameOverCount % 6 == 0){
			if(game.ads)
				game.resolver.startSmartWallAd();
		}
			
		
		beep2.play();
		gameOverScreen.addAction(sequence(moveTo(73, -250),
										moveTo(73,540,0.5f,Interpolation.swingOut),
										run(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												showGameOver = true;
											}
										})));
		
		
		
		if(gameScore>= 500)
			SwarmAchievement.unlock(21711);
		if(gameScore>= 1000)
			SwarmAchievement.unlock(21713);
		if(gameScore>= 1500)
			SwarmAchievement.unlock(21715);
		if(gameScore>= 2000)
			SwarmAchievement.unlock(21717);
		if(gameScore>= 2500)
			SwarmAchievement.unlock(21719);
		if(gameScore>= 3000)
			SwarmAchievement.unlock(21721);
		if(gameScore>= 10000)
			SwarmAchievement.unlock(21723);
		
		
		state=GameState.GameOver;
	}
	

	
	//adds 4 new sprites to the array 
	public void addSprites(){
		//green button index
		int gbIndex = generator.nextInt(4)+1;
		int specbIndex = generator.nextInt(4)+1;
		while(specbIndex == gbIndex)
			specbIndex = generator.nextInt(4)+1;
		
		
		
		//Spawn buttons
		for(int i = 0; i <4; i++){
			Sprite temp;
			int buttonChance = generator.nextInt(200);
			//spawn the sprite drawables. I know it's ugly fuck off
			if(i == gbIndex-1){	
				temp = new Sprite(greenButton);
			}else if(i == specbIndex-1){
				
				if(buttonChance == 0&&gameScore>1500){
					temp = new Sprite(reviveButton);
				}
				else if(buttonChance == 1&&gameScore>900){
					temp = new Sprite(hundredButton);
				}
				else if(buttonChance >1 && buttonChance <=4&&gameScore>500){
					temp = new Sprite(fiftyButton);
				}
				else if(buttonChance >5 && buttonChance <=10&&gameScore>200){
					temp = new Sprite(twentyButton);
				}
				else if(buttonChance >10 && buttonChance <=17){
					temp = new Sprite(timeButton);
				}
				else if(buttonChance>19 && buttonChance<135){
					temp = new Sprite(redButton);
				}
				else{
					temp = new Sprite(boltButton);
				}
			}else{
				if(buttonChance>15 && buttonChance<135)
					temp = new Sprite(redButton);
				else
					temp = new Sprite(boltButton);
				
			}
			
			temp.setPosition(buttonPositionX + (buttonPositionXNextOffset+buttonSize)*i,
					buttonPositionY + (buttonPositionYNextOffset+buttonSize)*4);
			buttonSprites.add(temp);
		}
		
		

		//buttonSprites.get(20+gbIndex-1).setRegion(greenButton);
		greenButtonIndex.add(gbIndex);
		
		
		
	}
	
	
	//removes Sprites from the array in order to free memory
	public void removeSprites(){
		buttonSprites.removeRange(0, 3);
	}

	
	
	
	
	
	
	
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
	}

	

	@Override
	public void hide() {
		stage.dispose();
		
	}

	@Override
	public void pause() {
		game.resolver.swarmSetActive(false);
		
	}

	@Override
	public void resume() {
		if(game.preferSwarm)
			game.resolver.initSwarm();
		game.setSwarmInventory();
	}

	@Override
	public void dispose() {
		game.manager.dispose();
		effect.dispose();
		game.prefs.flush();
		stage.dispose();
		
	}
	
	private boolean checkUVOverlap(Sprite sprite1, Sprite sprite2){
		if(sprite1.getU()==sprite2.getU()&&sprite1.getV()==sprite2.getV())
			return true;
		else
			return false;
	}
	
	private void initPlayingState(){
		gameBack_1.setColor(Color.WHITE);
		gameBack_2.setColor(Color.WHITE);
		scaleGameOverButtons = 0f;
		showReviveScreen=false;
		showGameOver=false;
		
	}
	
	

}

