package com.netease.cloud.services.nos.internal;

import java.util.ArrayList;
import java.util.List;

import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException;
import com.netease.cloud.services.nos.model.DeleteObjectsResult.DeletedObject;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException.DeleteError;

/**
 * Service response for deleteObjects API call. Not exposed to clients directly,
 * but broken up into two classes to differentiate normal and exceptional
 * completion of the API.
 * 
 * @see DeleteObjectsResult
 * @see MultiObjectDeleteException
 * @see NosClient#deleteObjects(com.netease.cloud.services.nos.model.DeleteObjectsRequest)
 */
public class DeleteObjectsResponse {

    public DeleteObjectsResponse(List<DeletedObject> deletedObjects, List<DeleteError> errors) {
        this.deletedObjects = deletedObjects;
        this.errors = errors;
    }

    public List<DeletedObject> getDeletedObjects() {
        return deletedObjects;
    }

    public void setDeletedObjects(List<DeletedObject> deletedObjects) {
        this.deletedObjects = deletedObjects;
    }

    public List<DeleteError> getErrors() {
        return errors;
    }

    public void setErrors(List<DeleteError> errors) {
        this.errors = errors;
    }

    private List<DeletedObject> deletedObjects = new ArrayList<DeletedObject>();
    private List<DeleteError> errors = new ArrayList<DeleteError>();
}
