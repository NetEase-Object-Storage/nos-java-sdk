package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class VideoTranscodingRequest extends WebServiceRequest {
	/**
	 * the object key and the  object bucketname
	 */
	private String key;
	private String bucketName;
	
	/**
	 * the target video type wanted e.g flv、mp4、m3u8   nodefault,must assigned by user
	 */
	private String type;
	/**
	 * the target video resolution rate wanted e.g 1024x768 (resolutionXxresolutionY)  defualt:the origin resolution rate
	 */
	private int resolutionX=-1;
	private int resolutionY=-1;
	/**
	 * Video Frame Rate   e.g 24fps , 30fps default:24fps
	 */
	private int fps=-1;
	/**
	 * video coder & decoder
	 */
	private String vCodec;
	/**
	 * audio coder & decoder
	 */
	private String	aCodec;
	/**
	 * the start point of Transcoding  (second)
 	 */
	private int	offset=-1;
	/**
	 * the length of the target video you want  (second)
	 */
	private int lenght=-1;
	/**
	 * the crop param
	 */
	/**
	 * (left,top) is the begin point of the crop area.
	 */
	private Boolean defaultCrop = Boolean.TRUE;
	private int left = -1;
	private int top = -1;
	/**
	 * (right,bottom) is the end point of the crop area
	 */
	private int right = -1;
	private int bottom = -1;
	/**
	 * IOS video split time interval default: 10s
	 */
	private int segtime=-1;
	
	
	private String callBackURL;
	
	
	VideoTranscodingRequest(String key, String bucketName){
		this.key = key;
		this.bucketName = bucketName;
	}
	
	
	public String getKey(){
		return this.key;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public String getBucketName(){
		return this.bucketName;
	}
	
	public void setBucketName(String bucketName){
		this.bucketName = bucketName;
	}
	
	
	
	
	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public int getResolutionX(){
		return this.resolutionX;
	}
	public int getResolutionY(){
		return this.resolutionY;
	}
	
	public void setResolution(int resolutionX, int resolutionY){
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
	}
		
	public int getFps(){
		return this.fps;
	}
	
	public void setFps(int fps){
		this.fps = fps;
	}
		
	public String getVCodec(){
		return this.vCodec;
	}
	
	public void setVCodec(String vCodec){
		this.vCodec = vCodec;
	}
	
	public String getACodec(){
		return this.aCodec;
	}
	
	public void setACodec(String aCodec){
		this.aCodec = aCodec;
	}
	
	public int getOffset(){
		return this.offset;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public int getLength(){
		return this.lenght;
	}
	
	public void setLenght(int length){
		this.lenght = length;
	}
	
	
	public Boolean getDefaultCrop(){
		return this.defaultCrop;
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
	
	public void setCropParam(int left, int top, int right, int bottom){
		this.defaultCrop = Boolean.FALSE;  
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public Boolean checkCropParam(){
		if (this.left != -1 && this.top != -1 && this.right != -1 && this.bottom != -1){
			if (this.left >= this.right || this.top <= this.bottom){
				return Boolean.FALSE;
			}else{
				return Boolean.TRUE;
			}
		}else{
			return Boolean.FALSE;
		}
	}
	
	public String genCropString(){
		return this.left  + "_" + this.top + "_" + this.right + "_" +  this.bottom;
	}
	
	public void setSegtime(int segtime){
		this.segtime = segtime;
	}
	
	public int getSegtime(){
		return this.segtime;
	}
	
	public String getCallBackURL(){
		return this.callBackURL;
	}
	
	public void setCallBackURL(String callBackURL){
		this.callBackURL = callBackURL;
	}
	
}
	
	
	
	
	
	
	

