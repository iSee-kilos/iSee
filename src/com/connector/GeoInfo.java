package com.connector;

public class GeoInfo {
	@com.google.gson.annotations.SerializedName("id")
	private String mId;
	
	@com.google.gson.annotations.SerializedName("latitude")
	private double mLatitude;
	
	@com.google.gson.annotations.SerializedName("longtitude")
	private double mLongtitude;
	
	@com.google.gson.annotations.SerializedName("confirmed")
	private boolean mConfirmed;
	
	@com.google.gson.annotations.SerializedName("like")
	private int mLike;
	
	public GeoInfo(){
	}
	
	public GeoInfo(double latitude, double longtitude){
		mLatitude = latitude;
		mLongtitude = longtitude;
		mConfirmed = false;
		mLike = 0;
	}
	
	public String getId(){
		return mId;
	}
	
	public void setId(String id){
		mId = id;
	}
	
	public double getLatitude(){
		return mLatitude;
	}
	
	public void setLatitude(double latitude){
		mLatitude = latitude;
	}
	
	public double getLongtitude(){
		return mLongtitude;
	}
	
	public void setLongtitude(double longtitude){
		mLongtitude = longtitude;
	} 
	
	public boolean getConfirmed(){
		return mConfirmed;
	}
	
	public void setConfirmed(boolean confirmed){
		mConfirmed = confirmed;
	}
	
	public int getLike(){
		return mLike;
	}
	
	public void setLike(int like){
		mLike = like;
	}
}
