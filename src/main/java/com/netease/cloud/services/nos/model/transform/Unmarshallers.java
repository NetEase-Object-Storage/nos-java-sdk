package com.netease.cloud.services.nos.model.transform;

import java.io.InputStream;
import java.util.List;

import com.netease.cloud.services.nos.internal.DeleteObjectsResponse;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration;
import com.netease.cloud.services.nos.model.BucketVersioningConfiguration;
import com.netease.cloud.services.nos.model.DeduplicateResult;
import com.netease.cloud.services.nos.model.GetBucketDedupResult;
import com.netease.cloud.services.nos.model.GetBucketDefault404Result;
import com.netease.cloud.services.nos.model.GetBucketStatsResult;
import com.netease.cloud.services.nos.model.GetObjectVersionsResult;
import com.netease.cloud.services.nos.model.ImageMetadata;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.VersionListing;
import com.netease.cloud.services.nos.model.VideoMetadata;
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
	 * Unmarshaller for the BucketVersionConfiguration XML response.
	 */
	public static final class BucketVersioningConfigurationUnmarshaller implements
			Unmarshaller<BucketVersioningConfiguration, InputStream> {
		public BucketVersioningConfiguration unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseVersioningConfigurationResponse(in).getConfiguration();
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


	public static final class GetBucketDedupResultUnmarshaller implements
			Unmarshaller<GetBucketDedupResult, InputStream> {
		public GetBucketDedupResult unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseGetBucketDedupResponse(in).getResult();
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

	public static final class DeduplicateResultUnmarshaller implements Unmarshaller<DeduplicateResult, InputStream> {
		public DeduplicateResult unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseDeduplicateResponse(in).getResult();
		}
	}

	public static final class GetObejctVersionsResultUnmarshaller implements
			Unmarshaller<GetObjectVersionsResult, InputStream> {
		public GetObjectVersionsResult unmarshall(InputStream in) throws Exception {
			return new XmlResponsesSaxParser().parseGetObjectVersionsResponse(in).getObjectVersionsResult();
		}
	}
	
	
	

	   /**
     * Unmarshaller for the ListVersions XML response.
     */
    public static final class VersionListUnmarshaller implements
            Unmarshaller<VersionListing, InputStream> {
        public VersionListing unmarshall(InputStream in) throws Exception {
            return new XmlResponsesSaxParser()
                    .parseListVersionsResponse(in).getListing();
        }
    }
    
    public static final class BucketLifecycleConfigurationUnmarshaller implements
    	Unmarshaller<BucketLifecycleConfiguration, InputStream> {

    	public BucketLifecycleConfiguration unmarshall(InputStream in) throws Exception {
    		return new XmlResponsesSaxParser().parseBucketLifecycleConfigurationResponse(in).getConfiguration();
    	}
    }
    
    /**
     * Unmarshaller for the getImageMetaInfo XML response.
     */
    public static final class GetImageMetaInfoUnmarshaller implements
    	Unmarshaller<ImageMetadata, InputStream>{
    	public ImageMetadata unmarshall(InputStream in) throws Exception {
    		return new XmlResponsesSaxParser().parseGetImageMetaInfoResponse(in).getImageMetadata();
    	}
    	
    }
    /**
     * Unmarshaller for the GetVideoMetaInfo XML response.
     */
    public static final class GetVideoMetaInfoUnmarshaller implements
	Unmarshaller<VideoMetadata, InputStream>{
    	public VideoMetadata unmarshall(InputStream in) throws Exception {
    		return new XmlResponsesSaxParser().parseGetVideoMetaInfoResponse(in).getVideoMetadata();
    	}
    	
    }
    
}
