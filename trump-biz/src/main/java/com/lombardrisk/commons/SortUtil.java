package com.lombardrisk.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortUtil {
	final static Logger logger=LoggerFactory.getLogger(SortUtil.class);
	
	public static String allocateSortType(String transPropFullPath,String formName,String fileFullName,String newFilePath)
    {
		logger.info("allocate sort type");
    	String newFileFullName=null;
    	File file=new File(fileFullName);
    	if(file!=null && file.exists() && file.isFile())
    	{
    		String fileName=file.getName();
    		int lastComma=file.getName().lastIndexOf(".");
    		if(lastComma!=-1)
    		{
    			String fileType=fileName.substring(lastComma+1);
    			List<List<String>> transProps=searchTransmissionProperties(transPropFullPath, formName, fileType);
    			if(transProps.size()==0)
    			{logger.info("form not found in "+transPropFullPath);}
    			else
    			{
    				if(fileType.equalsIgnoreCase("txt"))
        			{
        				newFileFullName=TxtUtil.sortTxtContentToNewFileByName(fileFullName, transProps.get(0).get(0),transProps.get(1).get(0),newFilePath);
        			}
        			if(fileType.equalsIgnoreCase("xml") || fileType.equalsIgnoreCase("xbrl"))
    				{
        				newFileFullName=Dom4jUtil.sortXmlContentToNewFileByName(fileFullName,transProps.get(0),transProps.get(1),newFilePath);
    				}
        			if(fileType.equalsIgnoreCase("xls"))
    				{
    					
    				}
    			}
    			
    		}
    	}else
    	{
    		logger.info("argument:file is null");
    	}
    	return newFileFullName;
    }
    
    public static List<List<String>> searchTransmissionProperties(String transPropFullPath,String formName,String fileType)
    {
    	logger.info("search transmission properties");
    	List<List<String>> parts=new ArrayList<List<String>>();
    	List<String> list=null;
    	//String suffix=".transmissionProperties.csv";
    	File transProp=new File(transPropFullPath);
    	if(transProp.isFile())
    	{
    		BufferedReader reader=null;
    		try {
    			
    			reader=new BufferedReader(new FileReader(transProp));
    			
    			String regex="\"?"+formName+"\"?,\"?"+fileType+"\"?,\"?([^\"]*)\"?,\"?([^\"]*)\"?";//ReturnName,FileType,limit(split by space),IgnoreCount from one to it.
				Pattern pattern=Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				
				String line=null;
				reader.readLine();
				while((line=reader.readLine())!=null)
				{
					Matcher m=pattern.matcher(line);
					if(m.find())
					{
						int i=1;
						while(i<=m.groupCount())
						{
							String gi=m.group(i);
							if(gi.matches(".*\\s.*"))
							{
								String[] g1Arr=gi.split("\\s");
								list=Arrays.asList(g1Arr);
							}else
							{
								list=new ArrayList<String>();
								list.add(gi);
							}
							logger.debug(gi);
							parts.add(list);
							
							i++;
						}
						break;
					}
				}
				reader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally
			{
				if(reader!=null)
				{
					try{
						reader.close();
					}catch(IOException e1)
					{}
				}
			}
    	}else
    	{
    		logger.info("File Not Find:transmission properties:{}",transPropFullPath);
    	}
    	
    	return parts;
    }
}
