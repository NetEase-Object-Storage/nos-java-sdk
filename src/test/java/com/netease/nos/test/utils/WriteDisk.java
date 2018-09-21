package com.netease.nos.test.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public  class WriteDisk {
	
	public static  void writeToDisk(InputStream in, String directory, String key, Boolean isCover) {
		OutputStream  outputStream = null;
		File file = new File(directory + File.separator + key);
		
		file.getParentFile().mkdirs();
		if (isCover || !isCover && !file.exists()) {
			try{
				file.createNewFile();
			}catch (IOException e){
				e.printStackTrace();
			}
		}	
		try{
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buffer = new byte[1024 * 10];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) > -1){
				outputStream.write(buffer, 0, bytesRead);
			}
		}catch(IOException e){
			System.out.println("read or write  error");
			e.printStackTrace();
		}finally{
			try{
				in.close();
			}catch(IOException e){
				System.out.println("inputstream close error");
				e.printStackTrace();
			}
			try{
				outputStream.close();
			}catch(IOException e){
				System.out.println("outputstream close error");
				e.printStackTrace();
			}
		}
		//return Boolean.TRUE; 
				
	}
}
