package com.netease.cloud.services.nos.model;

public class VideoMetadata {
	private String container;
	private String encoder;
	private long size;
	private int duration;
	private int width;
	private int height;
	private int frameRate;
	private int videoBitrate;
	
	private String videoCodec;
	private String audioCodec;
	
	public String  getContainer(){
		return this.container;
	}
	public String  getEncoder(){
		return this.encoder;
	}

	public long  getSize(){
		return this.size;
	}
	
	public int  getDuration(){
		return this.duration;
	}
	
	public int  getWidth(){
		return this.width;
	}
	
	public int  getHeight(){
		return this.height;
	}
	

	public int  getFrameRate(){
		return this.frameRate;
	}	
	
	public int  getVideoBitrate(){
		return this.videoBitrate;
	}	
	
	public String getVideoCodec(){
		return this.videoCodec;
	}
	
	public String getAudioCodec(){
		return this.audioCodec;
	}
	
	
	
	
	public void  setContainer(String container){
		 this.container = container;
	}
	
	public void  setEncoder(String encoder){
		 this.encoder = encoder;
	}

	public void  setSize(long size){
		 this.size = size;
	}
	
	public void  setDuration(int duration){
		 this.duration = duration;
	}
	
	public void  setWidth(int width){
		 this.width = width;
	}
	
	public void  setHeight(int height){
		this.height =  height;
	}
	

	public void  setFrameRate(int frameRate){
		 this.frameRate = frameRate;
	}	
	
	public void  setVideoBitrate(int  videoBitrate){
		 this.videoBitrate = videoBitrate;
	}	
	
	public void setVideoCodec(String videoCodec){
		 this.videoCodec = videoCodec;
	}
	
	public void setAudioCodec(String audioCodec){
		 this.audioCodec = audioCodec;
	}
	
	
	
	
	
	
	
}
