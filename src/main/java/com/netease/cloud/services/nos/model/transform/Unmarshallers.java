package com.netease.cloud.services.nos.model.transform;

import java.io.InputStream;
import java.util.List;

import com.netease.cloud.services.nos.internal.DeleteObjectsResponse;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration;
import com.netease.cloud.services.nos.model.GetBucketDefault404Result;
import com.netease.cloud.services.nos.model.GetBucketStatsResult;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.transform.XmlResponsesSaxParser.CompleteMultipartUploadHandler;
import com.netease.cloud.transform.Unmarshaller;

/**
 * Collection of unmarshallers for Nos XML responses.
 */
public class Unmarshallers {

	/**
	 * Unmarshaller for the ListBuckets XML response.
	 */
	public static final class ListBucketsUnmarshaller implements Unmarshaller<List<Bucket>, InputStream> {
		public List<Bucket> unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseListMyBucketsResponse(in).getBuckets();
		}
	}


	/**
	 * Unmarshaller for the ListObjects XML response.
	 */
	public static final class ListObjectsUnmarshaller implements Unmarshaller<ObjectListing, InputStream> {
		public ObjectListing unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseListBucketObjectsResponse(in).getObjectListing();
		}
	}


	/**
	 * Unmarshaller for the BucketLocation XML response.
	 */
	public static final class BucketLocationUnmarshaller implements Unmarshaller<String, InputStream> {
		public String unmarshall(InputStream in) throws Exception {
			String location = new XmlResponsesSaxParser().parseBucketLocationResponse(in);

			/*
			 * Nos treats the US location differently, and assumes that if the
			 * reported location is null, then it's a US bucket.
			 */
			if (location == null)
				location = "HZ";

			return location;
		}
	}

	/**
	 * Unmarshaller for the a direct InputStream response.
	 */
	public static final class InputStreamUnmarshaller implements Unmarshaller<InputStream, InputStream> {
		public InputStream unmarshall(InputStream in) throws Exception {
			return in;
		}
	}


	public static final class CompleteMultipartUploadResultUnmarshaller implements
			Unmarshaller<CompleteMultipartUploadHandler, InputStream> {
		public CompleteMultipartUploadHandler unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseCompleteMultipartUploadResponse(in);
		}
	}

	public static final class InitiateMultipartUploadResultUnmarshaller implements
			Unmarshaller<InitiateMultipartUploadResult, InputStream> {
		public InitiateMultipartUploadResult unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseInitiateMultipartUploadResponse(in)
					.getInitiateMultipartUploadResult();
		}
	}

	public static final class ListMultipartUploadsResultUnmarshaller implements
			Unmarshaller<MultipartUploadListing, InputStream> {
		public MultipartUploadListing unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseListMultipartUploadsResponse(in).getListMultipartUploadsResult();
		}
	}

	public static final class ListPartsResultUnmarshaller implements Unmarshaller<PartListing, InputStream> {
		public PartListing unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseListPartsResponse(in).getListPartsResult();
		}
	}

	public static final class DeleteObjectsResultUnmarshaller implements
			Unmarshaller<DeleteObjectsResponse, InputStream> {

		public DeleteObjectsResponse unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseDeletedObjectsResult(in).getDeleteObjectResult();
		}
	}
	
	public static final class GetBucketStatsUnmarshaller implements
			Unmarshaller<GetBucketStatsResult, InputStream> {

		@Override
		public GetBucketStatsResult unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseGetBucketStats(in).getResult();
		}
		
	}
	
	public static final class GetBucketDefault404Unmarshaller implements
			Unmarshaller<GetBucketDefault404Result, InputStream> {
		public GetBucketDefault404Result unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseGetBucketDefault404Response(in).getResult();
		}
	}
    
    public static final class BucketLifecycleConfigurationUnmarshaller implements
    	Unmarshaller<BucketLifecycleConfiguration, InputStream> {

    	public BucketLifecycleConfiguration unmarshall(InputStream in) throws Exception {
    		return new XmlResponsesSaxParser().parseBucketLifecycleConfigurationResponse(in).getConfiguration();
    	}
    }
   
}
