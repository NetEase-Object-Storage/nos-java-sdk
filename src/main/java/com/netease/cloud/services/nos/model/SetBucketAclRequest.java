package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * Request object containing all the options for setting a bucket's Access Control List (ACL).
 */
public class SetBucketAclRequest extends WebServiceRequest {
	/** The name of the bucket whose ACL is being set. */
	private String bucketName;


	/** The canned ACL to apply to the specified bucket. */
	private CannedAccessControlList cannedAcl;


	/**
	 * Constructs a new SetBucketAclRequest object, ready to set the specified
	 * canned ACL on the specified bucket when this request is executed.
	 *
	 * @param bucketName
	 *            The name of the bucket whose ACL will be set by this request.
	 * @param acl
	 *            The Canned Access Control List to apply to the specified
	 *            bucket when this request is executed.
	 */
	public SetBucketAclRequest(String bucketName, CannedAccessControlList acl) {
		this.bucketName = bucketName;
		this.cannedAcl = acl;
	}
	
	public SetBucketAclRequest(String bucketName, String acl) {
		this.bucketName = bucketName;
		boolean isRightAcl= false;
		for(CannedAccessControlList canacl : CannedAccessControlList.values()){
			if(canacl.toString().equals(acl)){
				this.cannedAcl = canacl;
				isRightAcl = true;
				break;
			}
		}
		if(!isRightAcl){
			throw new IllegalArgumentException("Acl is out of bond");
		}
		
	}

	/**
	 * Returns the name of the bucket whose ACL will be modified by this request
	 * when executed.
	 *
	 * @return The name of the bucket whose ACL will be modified by this request
	 *         when executed.
	 */
	public String getBucketName() {
		return bucketName;
	}


	/**
	 * Returns the canned ACL to be applied to the specified bucket when this
	 * request is executed. A request can use either a custom ACL or a canned
	 * ACL, but not both.
	 *
	 * @return The canned ACL to be applied to the specified bucket when this
	 *         request is executed.
	 */
	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}
}
