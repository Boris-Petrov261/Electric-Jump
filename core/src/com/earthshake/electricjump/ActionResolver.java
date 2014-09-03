package com.earthshake.electricjump;

public interface ActionResolver {
	public void showAds(boolean show);
	public void startSmartWallAd();
	public void startLandPageAd();
	public void initSwarm();
	public void swarmSetActive(boolean setActive);
}
