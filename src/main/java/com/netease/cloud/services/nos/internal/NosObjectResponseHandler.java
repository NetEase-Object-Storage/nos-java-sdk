package com.netease.cloud.services.nos.internal;


import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.NOSObjectInputStream;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.util.BinaryUtils;

/**
 * Nos HTTP response handler that knows how to pull Nos object content and
 * metadata out of an HTTP response and unmarshall it into an NOSObject object.
 */
public class NosObjectResponseHandler extends AbstractNosResponseHandler<NOSObject> {

    /**
     * @see com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http.HttpResponse)
     */
    public WebServiceResponse<NOSObject> handle(HttpResponse response) throws Exception {
        /*
         * TODO: It'd be nice to set the bucket name and key here, but the
         *       information isn't easy to pull out of the response/request
         *       currently.
         */
        NOSObject object = new NOSObject();
        ObjectMetadata metadata = object.getObjectMetadata();
        populateObjectMetadata(response, metadata);

        boolean hasServerSideCalculatedChecksum = !ServiceUtils.isMultipartUploadETag(metadata.getETag());
        boolean responseContainsEntireObject = response.getHeaders().get("Content-Range") == null;

        if (hasServerSideCalculatedChecksum && responseContainsEntireObject) {
            byte[] expectedChecksum = BinaryUtils.fromHex(metadata.getETag());
            object.setObjectContent(new NOSObjectInputStream(new ChecksumValidatingInputStream(response.getContent(),
                    expectedChecksum, object.getBucketName() + "/" + object.getKey()), response.getHttpRequest()));
        } else {
            object.setObjectContent(new NOSObjectInputStream(response.getContent(), response.getHttpRequest()));
        }

        WebServiceResponse<NOSObject> awsResponse = parseResponseMetadata(response);
        awsResponse.setResult(object);
        return awsResponse;
    }

    /**
     * Returns true, since the entire response isn't read while this response
     * handler handles the response. This enables us to keep the underlying HTTP
     * connection open, so that the caller can stream it off.
     *
     * @see com.netease.cloud.http.HttpResponseHandler#needsConnectionLeftOpen()
     */
    @Override
    public boolean needsConnectionLeftOpen() {
        return true;
    }        

}
