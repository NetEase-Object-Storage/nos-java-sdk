package com.netease.nos.test.utils;

import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.internal.Constants;

public class TestHostConfig {

	public static String region = "HZ";
//	public static String NOS_HOST_NAME = "172.17.2.201:8182";
//	public static String NOS_HOST_NAME = "10.100.82.77:8182";
	
	public static void changeHost(){
//		Constants.NOS_HOST_NAME = "10.100.82.77:8182";
//		Constants.NOS_HOST_NAME = "114.113.199.19";
//		Constants.NOS_HOST_NAME = "nos.netease.com";
		//开发环境
		//Constants.NOS_HOST_NAME = "172.17.2.201:8182";
		//Constants.NOS_HOST_NAME = "172.17.2.63:8182";
		Constants.NOS_HOST_NAME = "localhost:8500";
		//Constants.NOS_HOST_NAME = "fs-4.photo.163.org:8182";
		//云计算联调
//		Constants.NOS_HOST_NAME = "114.113.199.19:8181";
		//locale
	//	Constants.NOS_HOST_NAME = "127.0.0.1:8500";
		//Constants.NOS_HOST_NAME = "172.17.2.201:";
		//test image
		//Constants.NOS_HOST_NAME ="192.168.146.172:8500";
	}
	
	
	public static void changeHost(NosClient client, String host){
//		Constants.NOS_HOST_NAME = "10.100.82.77:8182";
		client.setEndpoint(host);
	}
	/**
	 * 开发环境

NOS: 172.17.2.201:8182
DFS: 172.17.2.201:3163
DDB: 172.17.2.201:8881?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.2.201:4332/nos_node1?user=nos_test&password=nos_test

tomcat目录： inspur1:/home/dfs/tomcat-nos-proxy
部署目录： inspur1:/home/dfs/webroot-nos-proxy
更新脚本：inspur1:/home/dfs/nos-dev-deploy.sh
联调环境

NOS: 172.17.2.64:8182
DFS: 172.17.2.201:3163
DDB: 172.17.2.64:8881?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.2.201:4331/nos_node1?user=nos_test&password=nos_test

tomcat目录：fs-4:/home/dfs/tomcat-nos-proxy
部署目录：fs-4:/home/dfs/webroot-nos-proxy
更新脚本：fs-4:/home/dfs/nos-update.sh
功能测试

NOS: 172.17.0.185:8181
DFS: 172.17.2.201:3163
DDB: 172.17.0.185:8883?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.0.185:4331/nos_node1?user=nos_test&password=nos_test

tomcat目录：im16-10:/home/qatest/tomcat-nos-proxy
部署目录：im16-10:/home/qatest/webroot-nos-proxy
更新脚本：img16-10:/home/qatest/nos-test-deploy.sh
性能测试

nginx: 172.17.4.113
NOS: 172.17.4.114:8183
DFS: 172.17.4.114:9163
DDB: 172.17.4.113:8883?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.4.113:4331/nos_node1?user=nos_test&password=nos_test

tomcat目录：inspur114:/home/dfs/nos_perftest/tomcat-nos-proxy
部署目录：inspur114:/home/dfs/nos_perftest/webroot-nos-proxy
更新脚本：inspur114:/home/dfs/nos_perftest/deploy_nos_perftest.sh
试用环境
杭州分区

nginx: 172.17.2.63 / 115.236.113.63 （支持外网80端口访问，设置Hosts： 115.236.113.63 nos.netease.com）
NOS: 172.17.2.63:8182
DFS: 172.17.2.63:5555
DDB: 172.17.2.63:8881?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.2.63:4331/nos1?user=nos_test&password=nos_test

tomcat目录：fs-3.photo:/home/dfs/tomcat-nos-proxy
部署目录：fs-3.photo:/home/dfs/webroot-nos-proxy
更新脚本：fs-3.photo:/home/dfs/deploy-nos.sh
北京分区

NOS：10.100.82.77:8182
DFS: 10.100.82.77:5555
DDB: 10.100.82.79:8886?user=nos_bj&password=nos_bj
存储过程：jdbc:mysql://10.100.82.79:5332/nos1
单元测试的数据库

DDB: 172.17.2.201:8883?user=nos_test&password=nos_test
MySQL: jdbc:mysql://172.17.2.201:4431/nos_node1?user=nos_test&password=nos_test
OpenStack环境

NOS: 114.113.199.19:8181
DFS:10.120.35.129:5555
DDB:10.120.35.129:8888?user=nos_test&password=nos_test
存储过程：jdbc:mysql://10.120.35.129:4332/nos?user=nos_test&password=nos_test
认证服务器：http://115.236.113.57/rest/auth
Redis： 10.120.35.254:6379/10.120.35.129:6379
	 **/
}
