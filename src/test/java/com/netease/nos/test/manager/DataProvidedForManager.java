package com.netease.nos.test.manager;

import org.testng.annotations.DataProvider;



public class DataProvidedForManager {
	
	@DataProvider
	public static Object[][] testUploadFile() {
		return new Object[][] { {"src/test/resources/apache.tar.rar" }, 
				                {"E:/testFiles/20M.txt"} };
	}	
 

}
