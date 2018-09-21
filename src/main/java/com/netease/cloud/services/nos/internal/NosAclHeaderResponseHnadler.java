package com.netease.cloud.services.nos.internal;


import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.model.CannedAccessControlList;

public class NosAclHeaderResponseHnadler extends AbstractNosResponseHandler<CannedAccessControlList> {

	@Override
	public WebServiceResponse<CannedAccessControlList> handle(HttpResponse response) throws Exception {
		// TODO Auto-generated method stub
		WebServiceResponse<CannedAccessControlList> awsResponse = new WebServiceResponse<CannedAccessControlList>();
		String acl = response.getHeaders().get(Headers.NOS_CANNED_ACL);
		for (CannedAccessControlList aclEnum : CannedAccessControlList.values()) {
			if (aclEnum.toString().equals(acl)) {
				awsResponse.setResult(aclEnum);
			}
		}
		return awsResponse;

	}
}
