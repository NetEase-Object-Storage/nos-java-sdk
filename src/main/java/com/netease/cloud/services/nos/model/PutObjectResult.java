package com.netease.cloud.services.nos.model;

import java.util.Date;

import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.internal.ObjectExpirationResult;

/**
 * Contains the data returned by  Nos from the <code>putObject</code>
 * operation.
 * Use this request to access information about the new object created from the
 * <code>putObject</code> request, such as its ETag and optional version ID.
 * 
 * @see Nos#putObject(String, String, java.io.File)
 * @see Nos#putObject(String, String, java.io.InputStream, ObjectMetadata)
 * @see Nos#putObject(PutObjectRequest)
 */
public class PutObjectResult implements ObjectExpirationResult {

    /**
     * The version ID of the new, uploaded object. This field will only be
     * present if object versioning has been enabled for the bucket to which the
     * object was uploaded.
     */
    //private String versionId;

    /** The ETag value of the new object */
    private String eTag;
    
    /** The time this object expires, or null if it has no expiration */
    private Date expirationTime;
    
    /** The expiration rule for this object */
    private String expirationTimeRuleId;
    
    /** The object name for this object **/
    private String objectName;
    
    /** The callback return Code **/
    private int callbackRetCode;
    
    /** The callback return Message **/
    private String callbackRetMessage;

    /**
     * Gets the optional version ID of the newly uploaded object. This field will 
     * be set only if object versioning is enabled for the bucket the
     * object was uploaded to.
     * 
     * @return The optional version ID of the newly uploaded object.
     * 
     * @see PutObjectResult#setVersionId(String)
     */
    /*public String getVersionId() {
        return versionId;
    }*/

    
    /**
     * Sets the optional version ID of the newly uploaded object.
     * 
     * @param versionId
     *            The optional version ID of the newly uploaded object.
     *            
     * @see PutObjectResult#getVersionId()        
     */
    /*public void setVersionId(String versionId) {
        this.versionId = versionId;
    }*/

    /**
     * Gets the ETag value for the newly created object.
     * 
     * @return The ETag value for the new object.
     * 
     * @see PutObjectResult#setETag(String)
     */
    public String getETag() {
        return eTag;
    }

    /**
     * Sets the ETag value for the new object that was created from the
     * associated <code>putObject</code> request.
     * 
     * @param eTag
     *            The ETag value for the new object.
     *            
     * @see PutObjectResult#getETag()           
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }


    /**
     * Returns the expiration time for this object, or null if it doesn't expire.
     */
    public Date getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the expiration time for the object.
     * 
     * @param expirationTime
     *            The expiration time for the object.
     */
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * 
     * object's expiration, or null if it doesn't expire.
     */
    public String getExpirationTimeRuleId() {
        return expirationTimeRuleId;
    }

    /**
     *
     * expiration
     * 
     * @param expirationTimeRuleId
     *            The rule ID for this object's expiration
     */
    public void setExpirationTimeRuleId(String expirationTimeRuleId) {
        this.expirationTimeRuleId = expirationTimeRuleId;
    }


	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}


	public int getCallbackRetCode() {
		return callbackRetCode;
	}


	public void setCallbackRetCode(int callbackRetCode) {
		this.callbackRetCode = callbackRetCode;
	}


	public String getCallbackRetMessage() {
		return callbackRetMessage;
	}


	public void setCallbackRetMessage(String callbackRetMessage) {
		this.callbackRetMessage = callbackRetMessage;
	}
    
}
