package com.earthshake.electricjump;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.sun.org.glassfish.external.statistics.annotations.Reset;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser.GotItemQuantityCB;
import com.swarmconnect.SwarmActivity;
import com.swarmconnect.SwarmApplication;
import com.swarmconnect.SwarmUser;
import com.swarmconnect.SwarmUserInventory;



public class EJGame extends Game {
	
	SpriteBatch 			batch;


	AssetManager			manager;
	BitmapFont				font,font2,font3;
	
	Preferences				prefs;
	
	public final static int WIDTH = 720;
	public final static int HEIGHT = 1280;
	public static String title = "Electric Jump";
//	public int 				revives;
	public boolean			preferSwarm;
	public boolean			firstTime;
	public final ActionResolver resolver;
	public boolean 			ads;
	public int 				gameOverCount;
	public int 				swarmConsumableCount;

	
	public EJGame(ActionResolver resolver){
		this.resolver = resolver;
	}
	
	@Override
	public void create () {
		
		swarmConsumableCount = 0;
		
		batch = new SpriteBatch();
		font  = new BitmapFont(Gdx.files.internal("font/robo_large.fnt"));
		font2  = new BitmapFont(Gdx.files.internal("font/robo_small.fnt"));
		font3  = new BitmapFont(Gdx.files.internal("font/robo_score.fnt"));
		manager = new AssetManager();
		
		
		prefs = Gdx.app.getPreferences("ElectricJumpPreferences");
		if(!prefs.contains("firstTime"))
			prefs.putBoolean("firstTime", true);
		
		if(!prefs.contains("preferSwarm"))
			prefs.putBoolean("preferSwarm", false);
		
		if(!prefs.contains("numOfRevives")){
			prefs.putInteger("numOfRevives", 3);
		}
		if(!prefs.contains("ads"))
			prefs.putBoolean("ads", true);
		
		prefs.flush();
		
		gameOverCount = 0;
		firstTime = prefs.getBoolean("firstTime");
		preferSwarm = prefs.getBoolean("preferSwarm");

		ads = prefs.getBoolean("ads");
		this.setScreen(new SplashScreen(this));
		
		if(preferSwarm)
			resolver.initSwarm();
		
		setSwarmInventory();
	}

	@Override
	public void render () {
		super.render();
		
    
	}
	
	@Override
	public void dispose(){
		font.dispose();
		font2.dispose();
		font3.dispose();
		batch.dispose();
		manager.dispose();
		prefs.flush();
	}
	
	public void setSwarmInventory(){
		GotItemQuantityCB reviveCheckCallback = new GotItemQuantityCB() {
		    public void containsItem(int quantity) {
		        if (quantity > 0) {
		            // user has the item in his/her inventory at the specified quantity
		        	Gdx.app.log("Swarm", "Inventory has revive" + Integer.toString(quantity));

		        }
		        swarmConsumableCount = quantity;
		    }
		};
		
		if(Swarm.isLoggedIn()){
			Swarm.user.getItemQuantity(1841, reviveCheckCallback);
		}
		
		GotItemQuantityCB adsCheckCallback = new GotItemQuantityCB() {
		    public void containsItem(int quantity) {
		        if (quantity > 0) {
		            // user has the item in his/her inventory at the specified quantity
		        	Gdx.app.log("Swarm", "Don't show ads");
		        	ads = false;
		        	prefs.putBoolean("ads", false);
		        	prefs.flush();
		        }

		    }
		};
		
		if(Swarm.isLoggedIn()){
			Swarm.user.getItemQuantity(1845, adsCheckCallback);
		}
	}
	
	public boolean spriteTouched(Sprite sprite, Vector3 pos){
		float spriteX = sprite.getBoundingRectangle().x;
		float spriteY = sprite.getBoundingRectangle().y;
		float spriteW = sprite.getBoundingRectangle().width;
		float spriteH = sprite.getBoundingRectangle().height;
		
		if(pos.x >= spriteX && pos.x <= spriteX + spriteW && pos.y>=spriteY && pos.y <=spriteY+spriteH){
			return true;
		}else
			return false;

	};
	
	
	
	public void SpriteMoveBy(Sprite sprite, float amount,  boolean axisX){
		
		if(axisX){
			sprite.setPosition(sprite.getX() + amount, sprite.getY());
		}
		
		else{	
			sprite.setPosition(sprite.getX(), sprite.getY() +amount);
		}
	
	};
	
	GotItemQuantityCB healthPotionCheckCallback = new GotItemQuantityCB() {
	    public void containsItem(int quantity) {
	        if (quantity > 0) {
	            // user has the item in his/her inventory at the specified quantity
	         
	        }
	    }
	};

	
}


