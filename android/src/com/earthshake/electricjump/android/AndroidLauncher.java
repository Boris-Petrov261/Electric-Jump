package com.earthshake.electricjump.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.earthshake.electricjump.ActionResolver;
import com.earthshake.electricjump.EJGame;
import com.mhvd.pycp200191.AdListener;

import com.mhvd.pycp200191.AdListener.MraidAdListener;
//import com.mhvd.pycp200191.AdListener.BannerAdListener;
import com.mhvd.pycp200191.AdView;
import com.mhvd.pycp200191.MA;
//import com.mhvd.pycp200191.Prm;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmStore;

public class AndroidLauncher extends AndroidApplication implements ActionResolver{
	
	MA ma = null;
	//Prm ma = null;
	    
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		
		
		 if(ma==null) 
			 ma=new MA(this, adCallbackListener, false);
		 
//		 if(ma==null) 
//			 ma=new Prm(this, adCallbackListener, false);
		
		 RelativeLayout layout = new RelativeLayout(this);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

	        View gameView = initializeForView(new EJGame(this), config);

	        RelativeLayout.LayoutParams adParams = new
	        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

	      

	        layout.addView(gameView);
	        
	        setContentView(layout);
		
		//Swarm.setActive(this);
	}
	
	
	
	public void onResume(){
		super.onResume();
//		Swarm.setActive(this);
//		
//		Swarm.init(this, 12847, "e511008266b2e97a8d7fe00ae537c69d");
	}
	
	public void onPause(){
		super.onPause();
		//Swarm.setInactive(this);
	}


	@Override
	public void showAds(boolean show) {
		// TODO Auto-generated method stub
		
	}
			


	@Override
	public void initSwarm() {
		// TODO Auto-generated method stub
		Swarm.setActive(this);
		
		Swarm.init(this, 12847, "e511008266b2e97a8d7fe00ae537c69d");
	}


	@Override
	public void swarmSetActive(boolean setActive) {
		// TODO Auto-generated method stub
		if(setActive)
			Swarm.setActive(this);
		else
			Swarm.setInactive(this);
	}



	@Override
	public void startSmartWallAd() {
		// TODO Auto-generated method stub
	
		//ma.runSmartWallAd();
		ma.callSmartWallAd();
		
		Log.v("shit", "smartwall");
	}
	
	@Override
	public void startLandPageAd() {
		// TODO Auto-generated method stub
	
		
		ma.callLandingPageAd();
		
		Log.v("shit", "land");
	}
	
	 AdListener adCallbackListener=new AdListener(){
	        @Override
	        public void onSDKIntegrationError(String message){
	            //Here you will receive message from SDK if it detects any integration issue.
	            Log.w("Airpush", "onSDKIntegrationError() "+message);
	        }
	        public void onSmartWallAdShowing(){
	            // This will be called by SDK when it's showing any of the SmartWall ad.
	            Log.w("Airpush", "onSmartWallAdShowing()");
	            Log.v("ad showed", "asd");
	        }
	        @Override
	        public void onSmartWallAdClosed(){
	            // This will be called by SDK when the SmartWall ad is closed.
	            Log.w("Airpush", "onSmartWallAdClosed()");
	        }
	        @Override
	        public void onAdError(String message){
	            //This will get called if any error occurred during ad serving.
	            Log.w("Airpush", "onAdError() "+message);
	        }
	        @Override
	        public void onAdCached(AdType arg0){
	            //This will get called when an ad is cached. 
	            Log.w("Airpush", "onAdCached() "+arg0.toString());
	        }
	        @Override
	        public void noAdAvailableListener(){ 
	            //this will get called when ad is not available 
	            Log.w("Airpush", "noAdAvailableListener()");
	        }
	     };

	     MraidAdListener adlistener = new MraidAdListener(){
	        @Override
	        public void onAdClickListener(){
	            //This will get called when ad is clicked.
	            Log.w("Airpush", "onAdClickListener()");
	        }
	        @Override
	        public void onAdLoadedListener(){
	            //This will get called when an ad has loaded.
	            Log.w("Airpush", "onAdLoadedListener()");
	        }
	        @Override
	        public void onAdLoadingListener(){
	            //This will get called when a rich media ad is loading.
	            Log.w("Airpush", "onAdLoadingListener()");
	        }
	        @Override
	        public void onAdExpandedListner(){
	            //This will get called when an ad is showing on a user's screen. This may cover the whole UI.
	            Log.w("Airpush", "onAdExpandedListner()");
	        }
	        @Override
	        public void onCloseListener(){
	            //This will get called when an ad is closing/resizing from an expanded state.
	            Log.w("Airpush", "onCloseListener()");
	        }
	        @Override
	        public void onErrorListener(String message){
	            //This will get called when any error has occurred. This will also get called if the SDK notices any integration mistakes.
	            Log.w("Airpush", message);
	        }
	        @Override
	        public void noAdAvailableListener(){
	            //this will get called when ad is not available 
	            Log.w("Airpush", "noAdAvailableListener()");
	        }
	    };
}
