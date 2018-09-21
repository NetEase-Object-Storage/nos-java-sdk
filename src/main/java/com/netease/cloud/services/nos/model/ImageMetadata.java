package com.netease.cloud.services.nos.model;

public class ImageMetadata {
	private String imageType;
	private String imageOrientation;
	private int imageWidth;
	private int imageHeight;
	
	public String getImageType(){
		return this.imageType;
	}
	
	public long getImageWidth(){
		return this.imageWidth;
	}
	
	public long getImageHeight(){
		return this.imageHeight;
	}
	
	public void setImageType(String imageType){
		this.imageType = imageType;
	}
	
	public void setImageWidth(int imageWidth){
		this.imageWidth = imageWidth;
	}
	
	public void setImageHeight(int imageHeight){
		this.imageHeight = imageHeight;
	}
	
	public void setImageOrientation(String imageOrientation) {
		this.imageOrientation = imageOrientation;
	}
	
	public String getImageOrientation() {
		return imageOrientation;
	}
	
}
