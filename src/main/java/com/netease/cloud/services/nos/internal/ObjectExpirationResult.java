package com.netease.cloud.services.nos.internal;

import java.util.Date;

import com.netease.cloud.services.nos.Headers;

/**
 * Interface for service responses that receive the x-amz-expiration header.
 * 
 * @see Headers#EXPIRATION
 */
public interface ObjectExpirationResult {

	/**
	 * Returns the expiration date of the object, or null if the object is not
	 * configured to expire.
	 */
	public Date getExpirationTime();

	/**
	 * Sets the expiration date of the object.
	 * 
	 * @param expiration
	 *            The date the object will expire.
	 */
	public void setExpirationTime(Date expiration);

	/**
	 * Returns the bucket lifecycle configuration rule ID for the expiration of
	 * this object.
	 * 
	 */
	public String getExpirationTimeRuleId();

	/**
	 * Sets the bucket lifecycle configuration rule ID for the expiration of
	 * this object.
	 * 
	 * @param ruleId
	 *            The rule ID of this object's expiration configuration
	 */
	public void setExpirationTimeRuleId(String ruleId);

}
