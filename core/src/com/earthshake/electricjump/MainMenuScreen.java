package com.earthshake.electricjump;




import sun.util.logging.resources.logging;

import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmStore;
import com.swarmconnect.SwarmStoreItem;
import com.swarmconnect.SwarmUserInventory;
import com.swarmconnect.SwarmActiveUser.GotItemQuantityCB;




public class MainMenuScreen implements Screen{
	
	private EJGame 			game;
	
	TextureAtlas 			buttons;
	Texture					menuTexture;
	Texture					swarmTexture;
	
	OrthographicCamera 		cam;
	Stage					stage;
	
	public Music 			track;
	
	
	
	private Image 			menuButtonHowTo, 
							menuButtonPlay, 
							menuButtonHighscores,
							menuButtonRate,
							menuButtonCredits,
							menuButtonMore,
							menuTitle;
	
	private Sprite			menu_background;
	
	SequenceAction			_inSeq1, _inSeq2, _inSeq3, _inSeq4, _inSeq5, _inSeq6, _inSeq7,
							_outSeq1,_outSeq2,_outSeq3,_outSeq4,_outSeq5,_outSeq6, _outSeq7;
							
	private float			menuTitleDelay,
							buttonAppearTime,
							buttonMenuCenterX,
							buttonHowTo_Y,
							buttonPlay_Y,
							buttonHighscores_Y,
							spritesAlphaColor;
	
	boolean					showHowTo,
							showCredits,
							soundOn,
							showSwarm;
	
	private Sprite			redButton,
							greenButton,
							boltButton,
							timeButton,
							imgTouch,
							imgDontTouch,
							imgCross,
							line;
							
	
	
	//Constructor
	public MainMenuScreen(EJGame gam) {
		Gdx.input.setCatchBackKey(false);
		game = gam;
	}
	
	
	@SuppressWarnings("static-access")
	@Override
	public void show() {
		
		Gdx.app.log("Swarm Ads", Boolean.toString(game.ads));
		
		if(game.firstTime){
		game.firstTime = false;
		game.prefs.putBoolean("firstTime", false);
		game.prefs.flush();
		}
		game.setSwarmInventory();
		
//		Gdx.app.log("Swarm", "Inventory has revive" + Integer.toString(swarmReviveQuantity));
		
		
		buttons			= game.manager.get("textures/buttons_misc.pack", 	TextureAtlas.class);
		track			= game.manager.get("music/ouroboros.ogg",			Music.class);
		menuTexture		= game.manager.get("textures/menu_background.jpg", 	Texture.class);
		
		
		if(!game.preferSwarm)
			swarmTexture = game.manager.get("textures/swarm.png", Texture.class);
		
		//Sprites
		menu_background 		= new Sprite(menuTexture);
		redButton				= buttons.createSprite("red_button");			
		greenButton 			= buttons.createSprite("green_button");
		boltButton				= buttons.createSprite("bolt_button");
		timeButton				= buttons.createSprite("time_button");
		imgDontTouch			= buttons.createSprite("touch");
		imgTouch				= buttons.createSprite("touch");
		imgCross				= buttons.createSprite("cross");
		line					= buttons.createSprite("line");
		
		//Camera
		cam = new OrthographicCamera();
		cam.setToOrtho(false, game.WIDTH, game.HEIGHT);
						
		//Stage
		stage 					= new Stage(new StretchViewport(game.WIDTH, game.HEIGHT,cam));
		
		
		menuButtonHowTo			= new Image(buttons.findRegion("menu_button_howto"));
		menuButtonPlay			= new Image(buttons.findRegion("menu_button_play"));
		menuButtonHighscores	= new Image(buttons.findRegion("menu_button_highscores"));
		menuButtonRate			= new Image(buttons.findRegion("menu_button_rate"));
		menuButtonCredits		= new Image(buttons.findRegion("menu_button_credits"));
		menuButtonMore			= new Image(buttons.findRegion("menu_button_more"));
		menuTitle 				= new Image(buttons.findRegion("menu_title"));
		
		Gdx.input.setInputProcessor(stage);
		
		
		//set Variables
		menuTitleDelay			= 0.6f;
		buttonAppearTime		= 0.8f;
		buttonMenuCenterX		= (game.WIDTH - menuButtonPlay.getWidth())/2;
		buttonHowTo_Y			= 440;
		buttonPlay_Y			= 680;
		buttonHighscores_Y		= 200;
		showHowTo				= false;
		showCredits				= false;
		spritesAlphaColor		= 0f;
		showSwarm				= !game.preferSwarm;
		
		redButton.setPosition(395, 720);
		greenButton.setPosition(30, 720);
		boltButton.setPosition(585, 720);
		timeButton.setPosition(212, 720);
		imgDontTouch.setPosition(440, 340);
		imgTouch.setPosition(70, 340);
		imgCross.setPosition(430, 520);
		line.setPosition(353, 160);
		
		
		///////Declare actions///////////////////////
		//
		//
		//Intro actions
		_inSeq2					= new SequenceAction(sequence( moveTo(-game.WIDTH, buttonPlay_Y),
											delay(menuTitleDelay),
											moveTo(buttonMenuCenterX, buttonPlay_Y, buttonAppearTime, Interpolation.swingOut)));
		
		_inSeq1					= sequence( moveTo(game.WIDTH+245, buttonHowTo_Y),
											delay(menuTitleDelay),
								  			moveTo(buttonMenuCenterX, buttonHowTo_Y, buttonAppearTime,Interpolation.swingOut));

		_inSeq3					= sequence( moveTo(-game.WIDTH, buttonHighscores_Y),
											delay(menuTitleDelay),
											moveTo(buttonMenuCenterX, buttonHighscores_Y, buttonAppearTime,Interpolation.swingOut));
		
		_inSeq4					= sequence( moveTo(-game.WIDTH, 0),
											delay(menuTitleDelay),
								  			moveTo(0, 0, buttonAppearTime,Interpolation.swingOut));

		_inSeq5					= sequence( moveTo(game.WIDTH+245, 0),
											delay(menuTitleDelay),
											moveTo(game.WIDTH-110, 0, buttonAppearTime,Interpolation.swingOut));
		

		_inSeq6 				= sequence( moveTo((game.WIDTH-menuTitle.getWidth())/2,0),
											moveTo((game.WIDTH-menuTitle.getWidth())/2, game.HEIGHT - menuTitle.getHeight() - 50,
											menuTitleDelay, Interpolation.swingOut));
		
		_inSeq7					= sequence( moveTo((game.WIDTH-menuButtonCredits.getWidth())/2, -game.WIDTH),
											delay(menuTitleDelay),
											moveTo((game.WIDTH-menuButtonCredits.getWidth())/2, 0, buttonAppearTime,Interpolation.swingOut));
		
		//Outro actions
		
		_outSeq2				= sequence( moveTo(-game.WIDTH, buttonPlay_Y, buttonAppearTime, Interpolation.swingIn));

		_outSeq1				= sequence( moveTo(game.WIDTH+245, buttonHowTo_Y, buttonAppearTime,Interpolation.swingIn));

		_outSeq3				= sequence( moveTo(-game.WIDTH, buttonHighscores_Y, buttonAppearTime,Interpolation.swingIn));

		_outSeq4				= sequence( moveTo(-game.WIDTH, 0, buttonAppearTime,Interpolation.swingIn));

		_outSeq5				= sequence( moveTo(game.WIDTH+245, 0, buttonAppearTime,Interpolation.swingIn));

		_outSeq7				= sequence( moveTo((game.WIDTH-menuButtonCredits.getWidth())/2, -game.WIDTH, buttonAppearTime,Interpolation.swingIn));
		_outSeq6 				= sequence(moveTo((game.WIDTH-menuTitle.getWidth())/2, game.HEIGHT + menuTitle.getHeight(), 
											menuTitleDelay, Interpolation.swingOut),
											delay(buttonAppearTime),
											run(new Runnable(){
									            @Override
									            public void run() {
									                game.setScreen(new GameScreen(game));
									            }
									            
											}));
		
		/////////////////////////////////////
		///
		///
		//
		
		//Add actions to actors
		menuButtonHowTo.addAction(_inSeq1);
		menuButtonPlay.addAction(_inSeq2);
		menuButtonHighscores.addAction(_inSeq3);
		menuButtonRate.addAction(_inSeq4);
		menuButtonMore.addAction(_inSeq5);
		menuTitle.addAction(_inSeq6);
		menuButtonCredits.addAction(_inSeq7);
		
		//set InputListeners to actors
		setActorListeners();
		
		
		//add actors to stage
		stage.addActor(menuButtonHowTo);
		stage.addActor(menuButtonPlay);
		stage.addActor(menuButtonHighscores);
		stage.addActor(menuButtonRate);
		stage.addActor(menuButtonCredits);
		stage.addActor(menuButtonMore);
		stage.addActor(menuTitle);
		
	
		
		//Play track
		track.setLooping(true);
		track.setVolume(0.4f);
		track.play();
		
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(1,0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!game.preferSwarm){
			if(Swarm.isLoggedIn()){
				
				game.prefs.putBoolean("preferSwarm", true);
				game.prefs.flush();
				game.preferSwarm = true;
				game.resolver.swarmSetActive(true);
				Gdx.app.log("Swarm", "logged in");
				game.setSwarmInventory();
			}else
				Gdx.app.log("Swarm", "not logged in");
		}
		
		cam.update();
		//Make sure stage is being drawn
		
		
		spritesSetAlpha();
		
		
		//Batch renders
		game.batch.setProjectionMatrix(cam.combined);
		game.batch.begin();
		
		menu_background.draw(game.batch);
		
		
		if(showHowTo){
			redButton.draw(game.batch);
			greenButton.draw(game.batch);
			boltButton.draw(game.batch);
			timeButton.draw(game.batch);
			imgDontTouch.draw(game.batch);
			imgTouch.draw(game.batch);
			imgCross.draw(game.batch);
			line.draw(game.batch);
		}
		
		if(showCredits){
			game.font.draw(game.batch, "CREDITS", (game.WIDTH-game.font.getBounds("CREDITS").width)/2, 900);
			game.font2.drawWrapped(game.batch, "Programmer:\nBoris Petrov\nMusic:\nKevin Macleod\n(incompetech.com)", 0, 800, 720, HAlignment.CENTER);
		}
		
		game.batch.end();
		
		if(showHowTo){
			Gdx.input.setCatchBackKey(true);
			
			if(Gdx.input.isTouched()||Gdx.input.isKeyPressed(Keys.BACK)){
				menuButtonHowTo.addAction(moveTo(buttonMenuCenterX, buttonHowTo_Y, buttonAppearTime, Interpolation.swingOut));
				menuButtonPlay.addAction(moveTo(buttonMenuCenterX, buttonPlay_Y, buttonAppearTime, Interpolation.swingOut));
				menuButtonHighscores.addAction(moveTo(buttonMenuCenterX, buttonHighscores_Y, buttonAppearTime, Interpolation.swingOut));
				stage.act();
				showHowTo = false;
			}
		}
		
		if(showCredits){
			Gdx.input.setCatchBackKey(true);
			if(Gdx.input.justTouched()||Gdx.input.isKeyPressed(Keys.BACK)){
				menuButtonHowTo.addAction(moveTo(buttonMenuCenterX, buttonHowTo_Y, buttonAppearTime, Interpolation.swingOut));
				menuButtonPlay.addAction(moveTo(buttonMenuCenterX, buttonPlay_Y, buttonAppearTime, Interpolation.swingOut));
				menuButtonHighscores.addAction(moveTo(buttonMenuCenterX, buttonHighscores_Y, buttonAppearTime, Interpolation.swingOut));
				stage.act();
				showCredits = false;
			}
		}
		
		if(!showHowTo&&!showCredits)
			Gdx.input.setCatchBackKey(false);
		
		//Stage renders and actions
		if(showSwarm){
			game.batch.begin();
			game.batch.draw(swarmTexture, (game.WIDTH-swarmTexture.getWidth())/2, 500);
			if(Gdx.input.isTouched()){
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				cam.unproject(touchPos);
				
				if(touchPos.y>500 && touchPos.x<700){
					if(touchPos.x>50 && touchPos.x<360){
						
						game.resolver.initSwarm();
						showSwarm = false;
					}
					else if(touchPos.x>400 && touchPos.x<600)
						showSwarm = false;
				}
			}
			game.batch.end();
		}else{
		stage.act();
		stage.draw();
		}
		
		
		
	}
	
	private void spritesSetAlpha(){
		greenButton.setAlpha(spritesAlphaColor);
		redButton.setAlpha(spritesAlphaColor);
		boltButton.setAlpha(spritesAlphaColor);
		timeButton.setAlpha(spritesAlphaColor);
		imgDontTouch.setAlpha(spritesAlphaColor);
		imgTouch.setAlpha(spritesAlphaColor);
		imgCross.setAlpha(spritesAlphaColor);
		line.setAlpha(spritesAlphaColor);
		game.font.setColor(1,1,1,spritesAlphaColor);
		game.font2.setColor(1,1,1,spritesAlphaColor);
		
		if(showHowTo||showCredits){
			if(spritesAlphaColor<=1f)
				spritesAlphaColor +=0.05f;
		}else
			spritesAlphaColor = 0f;
	}
	
	private void setActorListeners(){
		menuButtonPlay.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				menuButtonHowTo.addAction(_outSeq1);
				menuButtonPlay.addAction(_outSeq2);
				menuButtonHighscores.addAction(_outSeq3);
				menuButtonRate.addAction(_outSeq4);
				menuButtonMore.addAction(_outSeq5);
				menuTitle.addAction(_outSeq6);
				menuButtonCredits.addAction(_outSeq7);
				stage.act();
				//game.setScreen(new GameScreen(game));
			}
			
			
		});
		
		menuButtonHowTo.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				menuButtonHowTo.addAction(moveTo(game.WIDTH+245, buttonHowTo_Y, buttonAppearTime,Interpolation.swingIn));
				menuButtonPlay.addAction(moveTo(-game.WIDTH, buttonPlay_Y, buttonAppearTime,Interpolation.swingIn));
				menuButtonHighscores.addAction(sequence(moveTo(-game.WIDTH, buttonHighscores_Y, buttonAppearTime,Interpolation.swingIn), run(new Runnable(){
									            @Override
									            public void run() {
									                showHowTo = true;
									            }
									            
											})));
			}
			
			
		});
		
		menuButtonHighscores.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Swarm.showLeaderboards();
			}
			
			
		});
		
		menuButtonRate.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.earthshake.electricjump.android");
			}
			
			
		});
		
		menuButtonMore.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				game.resolver.startLandPageAd();
			}
			
			
		});
		
		menuButtonCredits.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				
				menuButtonHowTo.addAction(moveTo(game.WIDTH+245, buttonHowTo_Y, buttonAppearTime,Interpolation.swingIn));
				menuButtonPlay.addAction(moveTo(-game.WIDTH, buttonPlay_Y, buttonAppearTime,Interpolation.swingIn));
				menuButtonHighscores.addAction(sequence(moveTo(-game.WIDTH, buttonHighscores_Y, buttonAppearTime,Interpolation.swingIn), run(new Runnable(){
									            @Override
									            public void run() {
									                showCredits = true;
									            }
									            
											})));
				
			}
		});
		
	}
	
	
	
	
	@Override
	public void resize(int width, int height) {
		 stage.getViewport().update(width, height, true);
	}

	

	@Override
	public void hide() {
		stage.dispose();
		game.prefs.flush();
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
		stage.dispose();
		game.prefs.flush();
	}
	
	
	

}
