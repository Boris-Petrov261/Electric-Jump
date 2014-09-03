package com.earthshake.electricjump.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.earthshake.electricjump.ActionResolver;
import com.earthshake.electricjump.EJGame;


public class DesktopLauncher implements ActionResolver{
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 580;
		config.height = 1000;
	//	config.width = EJGame.WIDTH/2;
	//	config.height = EJGame.HEIGHT/2;
		config.title = EJGame.title;
		
		new LwjglApplication(new EJGame(new DesktopLauncher()), config);
	}

	@Override
	public void showAds(boolean show) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initSwarm() {
		// TODO Auto-generated method stub
		Gdx.app.log("asd", "sad");
	}

	@Override
	public void swarmSetActive(boolean setActive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startSmartWallAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startLandPageAd() {
		// TODO Auto-generated method stub
		
	}
}
