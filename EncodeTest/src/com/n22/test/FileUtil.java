package com.n22.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

	public static String readFile(String path) throws Exception{
	    StringBuffer str = new StringBuffer();   
	    BufferedReader in = null;   
	    File inputFile = null;     
	    String realPath =path; //ClassLoader.getSystemResource(path).getPath();
    	inputFile = new File(realPath);      
        in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "GBK"));   
        String line = null;   
        str = new StringBuffer((int) inputFile.length());   
        while ((line = in.readLine()) != null) {   
            str.append(line);   
        }   
        in.close();   		
	    return str.toString();
	}
	
	public static void fileWriter(String path,String fileName,String msg){
		File file=new File(path);
        if(!file.exists()){
            file.mkdirs(); 
        }
        
        File f=new File(path+File.separator+fileName+".txt");
        FileWriter fw = null;
        try {
			fw=new FileWriter(f);
			fw.write(msg);
			fw.close();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
	}
}
