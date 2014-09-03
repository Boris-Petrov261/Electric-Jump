package com.earthshake.electricjump;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sun.corba.se.spi.resolver.Resolver;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser.GotItemQuantityCB;

public class SplashScreen implements Screen{
	
	private EJGame 					game;
	private Texture 				splashImage;
	OrthographicCamera 				cam;
	Stage 							stage;
	TextureParameter 				param;
	
	
	public SplashScreen(EJGame gam) {
		game = gam;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void show() {
		
		game.setSwarmInventory();
		
		param 				= new TextureParameter();		
		param.genMipMaps	= true;
		param.minFilter		= TextureFilter.MipMapLinearNearest;
		param.magFilter		= TextureFilter.Nearest;
		
		splashImage			= new Texture(Gdx.files.internal("textures/loading.png"));
		
		//set assets to load
		
		game.manager.load("music/ouroboros.ogg", 			Music.class);
		game.manager.load("textures/buttons_misc.pack",		TextureAtlas.class);
		game.manager.load("textures/menu_background.jpg", 	Texture.class, param);
		game.manager.load("textures/game_back_1.jpg", 		Texture.class, param);
		game.manager.load("textures/game_back_2.jpg", 		Texture.class, param);
		if(!game.preferSwarm)
			game.manager.load("textures/swarm.png",			Texture.class, param);
		game.manager.load("music/beep1.wav", 				Sound.class);
		game.manager.load("music/beep2.mp3", 				Sound.class);
		
	
		cam = new OrthographicCamera();
		cam.setToOrtho(false, game.WIDTH, game.HEIGHT);
		
		
		
		
		if(game.ads&&!game.firstTime)
			game.resolver.startSmartWallAd();
		
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		game.batch.setProjectionMatrix(cam.combined);
		
		game.batch.begin();
		game.batch.draw(splashImage, 80, 620);
		game.batch.end();
		
		///Execute once assets are loaded
		if(game.manager.update()){
			game.setScreen(new MainMenuScreen(game));
		}
		
	}

	@Override
	public void resize(int width, int height) {
		
		
	}

	

	@Override
	public void hide() {
		splashImage.dispose();
		
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
		splashImage.dispose();
	}


}
