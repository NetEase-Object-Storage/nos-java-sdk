package com.netease.cloud.services.nos.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netease.cloud.services.nos.model.DeleteObjectsResult.DeletedObject;

/**
 * Exception for partial or total failure of the multi-object delete API,
 * including the errors that occurred. For successfully deleted objects, refer
 * to {@link MultiObjectDeleteException#getDeletedObjects()}.
 */
public class MultiObjectDeleteException extends NOSException {

	private static final long serialVersionUID = -2004213552302446866L;

	private final List<DeleteError> errors = new ArrayList<DeleteError>();
	private final List<DeletedObject> deletedObjects = new ArrayList<DeleteObjectsResult.DeletedObject>();

	public MultiObjectDeleteException(Collection<DeleteError> errors, Collection<DeletedObject> deletedObjects) {
		super("One or more objects could not be deleted");
		this.deletedObjects.addAll(deletedObjects);
		this.errors.addAll(errors);
	}

	/**
	 * Returns the list of successfully deleted objects from this request. If
	 * {@link DeleteObjectsRequest#getQuiet()} is true, only error responses
	 * will be returned from Nos.
	 */
	public List<DeletedObject> getDeletedObjects() {
		return deletedObjects;
	}

	/**
	 * Returns the list of errors from the attempted delete operation.
	 */
	public List<DeleteError> getErrors() {
		return errors;
	}

	/**
	 * An error that occurred when deleting an object.
	 */
	static public class DeleteError {

		private String key;
		//private String versionId;
		private String code;
		private String message;

		/**
		 * Returns the key of the object that couldn't be deleted.
		 */
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * Returns the versionId of the object that couldn't be deleted.
		 */
		/*public String getVersionId() {
			return versionId;
		}

		public void setVersionId(String versionId) {
			this.versionId = versionId;
		}*/

		/**
		 * Returns the status code for the failed delete.
		 */
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		/**
		 * Returns a description of the failure.
		 */
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

}
