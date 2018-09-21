package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provide the data of vedio frame, used to frame image of the 
 * vedio due the offset and resize param 
 * </p>
 * 
 */
public class VideoFrameRequest extends WebServiceRequest {
	/**
	 * The name of the bucket containing the object's whose metadata is being
	 * retrieved.
	 */
	private String bucketName;
	/**
	 * The key of the object whose metadata is being retrieved.
	 */
	private String key;
	/**
	 * the tag indicate the nos to do vedio frame
	 * */
	//private Boolean vframe;
	
	/**
	 * the offset( the time of  image in the vedio (unit:second) )
	 * */
	private long offset = -1;
	/**
	 * the size of the resized image want(resizeXxresizeY) 
	 */
	private int resizeX = -1;
	private int resizeY = -1;
	
	/**
	 * the crop param
	 */
	/**
	 * (left,top) is the begin point of the crop area.
	 */
	/**
	 * if the defaltcorp is true ,we will cut the blackedging
	 */
	private Boolean defaultCrop = Boolean.TRUE;
	private int left = -1;
	private int top = -1;
	/**
	 * (right,bottom) is the end point of the crop area
	 */
	private int right = -1;
	private int bottom = -1;
	
	

	
	
	public VideoFrameRequest(String bucketName, String key){
		this.bucketName = bucketName;
		this.key = key;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public void setResizeX(int resizeX){
		this.resizeX = resizeX;
	}
	
	public void setResizeY(int resizeY){
		this.resizeY = resizeY;
	}
	
	public void setResizeXY(int resizeX, int resizeY){
		this.resizeX = resizeX;
		this.resizeY = resizeY;
	}
	
	public void setLeft(int left){
		this.left = left;
	}

	public void setTop(int top){
		this.top = top;
	}

	public void setRight(int right){
		this.right = right;
	}
	
	public void setBottom(int bottom){
		this.bottom = bottom;
	}
	
	public void setCropParam(int left, int top, int right, int bottom){
		this.defaultCrop = Boolean.FALSE;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public String getBucketName(){
		return this.bucketName;
	}
	
	public void setDefaultCrop(){
		this.defaultCrop = Boolean.TRUE;
	}
	
	public Boolean getDefaultCorp(){
		return this.defaultCrop;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public long getOffet(){
		return this.offset;
	}
	
	public int getResizeX(){
		return this.resizeX;
	}
	
	public int getResizeY(){
		return this.resizeY;
	}
	
	public int getLeft(){
		return this.left;
	}
	
	public int getTop(){
		return this.top;
	}
	
	public int getRight(){
		return this.right;
	}
	
	public int getBottom(){
		return this.bottom;
	}	
}
