package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class GetImageRequest extends WebServiceRequest{

	private String bucketName;

	private String key;


	/**
	 * the mode  of thumbnail
	 * 0  WidthxHeigh ( Ordinary thumbnail )
	 * 0  Widthx0  	  (set the width , the height is self-adaption)
	 * 0  0xHeight    (set the height, the width is self-adaption)
	 * 1  WidthyHeight see:nos user manual 
	 * 2  WidthzHeight see:nos user manual 
	 * 3  WidthwHeight see:nos user manual
	 */
	GetImageMode mode;
	/**
	 * the size of the thumbnail image want(resizeXxresizeY) 
	 */
	private int resizeX = -1;
	private int resizeY = -1;
	/**
	 * the crop param
	 */
	/**
	 * (cropX,cropY) is the begin point of the crop area.
	 */
	private int cropX = -1;
	private int cropY = -1;
	/**
	 * (cropWidth,cropHeight) crop size
	 */
	private int cropWidth = -1;
	private int cropHeight = -1;

	/**
	 * get the image according to the given pixel  range[100-16777216]
	 * e.g. if the origin image is 200X200 , given pixel=10000,the returned
	 * image size will be 100X100
	 */
	int pixel = -1;

	/**
	 * the quality of the image want. range [0-100], 0 represent low quality,
	 * 100 represent high quality
	 */
	int quality = -1;


	/**
	 * the type of the image want ,now "jpg","jpeg","png", "bmp", "webp" is support
	 */
	String type;

	/**
	 * the text wateMark to mark the returned image
	 * the text is encoded by "URLBase64",the max lenght is 64byte
	 * the image to be marked has size limit which higher than 200px
	 *  widther than 300px
	 */
	String waterMark;


	/**
	 * this param is valid if we use thumbnail&crop
	 * the NOS will first do thumbnail and then crop.the axis represent the crop location.
	 * range:[0-10] , 0 represent the top, 10 represent the bottom
	 * default:5
	 * */
	int axis = -1;
	
	/**
	 * to rotate the image by angle, which ranges [0, 360]
	 */
	int rotation = -1;
	
	/**
	 * if interlace takes action, the image would first show blurringly, then more and more clearly,  
	 * to show interlace, set this filed to 1, else set it to 0
	 */
	int interlace = -1;

	/**
	 * set the key and buckeName
	 * @param bucketName
	 * @param key
	 */
	public GetImageRequest(String bucketName, String key){
		this.bucketName = bucketName;
		this.key = key;

	}

	public String getBucketName(){
		return this.bucketName;
	}

	public String getKey(){
		return this.key;
	}


	public int getResizeX(){
		return this.resizeX;
	}

	public int getResizeY(){
		return this.resizeY;
	}

	public int getCropX(){
		return this.cropX;
	}

	public int getCropY(){
		return this.cropY;
	}

	public int getCropWidth(){
		return this.cropWidth;
	}

	public int getCropHeight(){
		return this.cropHeight;
	}

	public int getQuality(){
		return this.quality;
	}


	public String getType(){
		return this.type;
	}

	public String getWaterMark(){
		return this.waterMark;
	}

	public int getPixel(){
		return this.pixel;
	}

	public int getAxis(){
		return this.axis;
	}

	public GetImageMode getMode(){
		return this.mode;
	}
	
	public int getRotation() {
		return this.rotation;
	}
	
	public int getInterlace() {
		return this.interlace;
	}


	public void setResizeX(int resizeX){
		this.resizeX = resizeX;
	}

	public void setResizeY(int resizeY){
		this.resizeY = resizeY;
	}

	/**
	 * set resizeX x resizeY (if set -1, we will not encode the Resize param)
	 * @param resizeX
	 * @param resizeY
	 */
	public void setResizeXY(int resizeX, int resizeY){
		this.resizeX = resizeX;
		this.resizeY = resizeY;
	}

	/**
	 * (if set -1, we will not encode the param)
	 * @param cropX
	 */
	public void setCropX(int cropX){
		this.cropX = cropX;
	}
	/**
	 * (if set -1, we will not encode the param)
	 * @param cropY
	 */
	public void setCropY(int cropY){
		this.cropY = cropY;
	}
	/**
	 * (if set -1, we will not encode the param)
	 * @param cropWidth
	 */
	public void setCropWidth(int cropWidth){
		this.cropWidth = cropWidth;
	}
	/**
	 * (if set -1, we will not encode the param)
	 * @param cropHeight
	 */
	public void setCropHeight(int cropHeight){
		this.cropHeight = cropHeight;
	}
	/**
	 * (if one of param is  -1, we will not encode the crop param)
	 * @param cropX
	 */
	public void setCropParam(int cropX, int cropY, int cropWidth, int cropHeight){
		this.cropX = cropX;
		this.cropY = cropY;
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
	}
	/**
	 * check the parameters of crop
	 * @return
	 */
	public Boolean CheckCropParam(){
		if(-1 == this.cropX || -1 == this.cropY || -1 == this.cropWidth || -1 == this.cropHeight){
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * set the GetImageMode 
	 * 		mode= #GetImageMode.XMODE    --->600x600 
	 * 		mode= #GetImageMode.YMODE	 --->600y600  
	 * 		mode= #GetImageMode.XMODE	 --->600z600  
	 * 		detail:see nos_user_manual (查看nos用户手册，图片缩略部分)
	 * @param mode
	 */
	public void setMode(GetImageMode mode){
		this.mode = mode;
	}

	/**
	 * set the type of image you want,now we support "jpg/jpeg/png/bmp"
	 * @param type
	 */
	public void setType(String type){
		this.type = type;
	}

	/**
	 * set the quality of the image you want, valid value range[0-100],
	 * 0 is lowest quality,100 is the highest quality
	 * @param quality
	 */
	public void setQuality(int quality){
		this.quality =  quality;
	}
	
	/**
	 * set the character watermark(水印文件)(进行水印高度不低于200px 宽度不低于300px)
	 * @param waterMake
	 */
	public void  setWaterMark(String waterMake){
		this.waterMark = waterMake;
	}

	/**
	 * set the pixel, valid value  range is [100-16777216]
	 * is you set "-1",we will not encode the param
	 * @param pixel
	 */
	public void  setPixel(int pixel){
		this.pixel = pixel;
	}
	/**
	 * only valid for the GetImageMode.YMODE,valid value range is [0-10]
	 * 0 represent to "get the top part"   1 represent to "get the bottom part"
	 * @param axis
	 */
	public void  setAxis(int axis){
		this.axis = axis;
	}
	
	/**
	 * set the angle parameter
	 * @param rotation valid range [0, 360]
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	/**
	 * set the interlace parameter
	 * @param interlace to show interlace, set the parameter to 1, else 0
	 */
	public void setInterlace(int interlace) {
		this.interlace = interlace;
	}

}
