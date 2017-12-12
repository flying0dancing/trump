package com.lombardrisk.commons;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtUtil
{
	final static Logger logger=LoggerFactory.getLogger(TxtUtil.class);
	
	@Deprecated
	public static boolean searchInTxt(File txtFile, String keyWord)
	{
		boolean findKeyWord = false;
		try
		{
			String encoding = "UTF-16";
			if (txtFile.isFile() && txtFile.exists())
			{
				InputStreamReader read = new InputStreamReader(new FileInputStream(txtFile), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null)
				{
					if (lineTxt.indexOf(keyWord) != -1)
					{
						findKeyWord = true;
						break;
					}
				}
				read.close();
			}
		}
		catch (Exception e)
		{

		}

		return findKeyWord;
	}

	public static List<String> getFileContent(File filePath)
	{
		List<String> strList = new ArrayList<String>();
		String encoding = "UTF-8";
		try
		{
			InputStreamReader read = new InputStreamReader(new FileInputStream(filePath), encoding);
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null)
			{
				strList.add(line);
			}
			reader.close();
			read.close();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return strList;
	}

	public static String getAllContent(File filePath)
	{
		String content = "";
		/*
		 * for(String row:getFileContent(filePath)) {
		 * content=content+row+"\r\n"; }
		 */
		List<String> contents = getFileContent(filePath);
		for (int i = 0; i < contents.size(); i++)
		{
			if (i < contents.size() - 1)
				content = content + contents.get(i) + "\r";
			else
				content = content + contents.get(i);
		}
		return content;
	}

	@Deprecated
	public static String listFileByRow(File filePath, Integer row)
	{
		List<String> strList = getFileContent(filePath);
		int size = strList.size();
		if (size >= (row - 1))
			return strList.get(row - 1);
		else
			return "";

	}

	@Deprecated
	public static List<String> listFileByRegionRow(File filePath, Integer startLine, Integer endLine)
	{
		List<String> strList = getFileContent(filePath);
		List<String> regionList = new ArrayList<String>();
		int size = strList.size();
		if (size >= (endLine - 1))
		{
			for (int i = startLine; i <= endLine; i++)
				regionList.add(strList.get(i - 1));
		}
		return regionList;
	}
	@Deprecated
	public static void writeToTxt(File file, String text) throws Exception
	{
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
		fw.append(text);
		fw.newLine();
		fw.flush();
		fw.close();
	}
	
	
	/**
	 * 
	 * @param fileFullName
	 * @param partitionStr
	 * @return sorted file's full name
	 */
	public static String sortTxtContentToNewFileByName(String fileFullName,String partitionStr,String ignoreCount,String newFilePath)
	{
		String newFileFullName=null;
		File file=new File(fileFullName);
		BufferedReader reader=null;
		List<String> list=new ArrayList<String>();
		if(file.exists() && file.isFile())
		{
			try{
				String encodestr=getCharsetName(fileFullName);
				//reader=new BufferedReader(new FileReader(file));
				reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),encodestr));
				String line=null;
				if(StringUtils.isBlank(partitionStr))
				{
					reader.readLine();//ignore first line
					while((line=reader.readLine())!=null)
					{
						list.add(line+"\r\n");
					}
				}else
				{
					while((line=reader.readLine())!=null)
					{
						String[] lines=line.split(partitionStr);
						int ignoreNo=Integer.parseInt(ignoreCount);
						if(ignoreNo<0 || ignoreNo>lines.length)
						{
							logger.error("ignore number should between 0 and {} in *.transmissionProperties.csv",lines.length);
						}else
						{
							for(int i=ignoreNo;i<lines.length;i++)
							{
								list.add(lines[i]+"\r\n");
							}
						}
						
					}
				}
				
				reader.close();
				
				Collections.sort(list);
				newFileFullName=FileUtil.createNewFileWithSuffix(fileFullName,"_sort",newFilePath);
				writeTxtContentFromList(list,newFileFullName,encodestr);
				
			}catch(Exception e)
			{
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
		}
		
		return newFileFullName;
		
	}
	
	public static void writeTxtContentFromList(List<String> list,String fileFullName,String charsetName)
	{
		if(list!=null)
		{
			try {
				logger.info("write list into text.");
				FileOutputStream outStream=new FileOutputStream(fileFullName);
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream,charsetName)));
				for(String str:list)
				{
					writer.write(str);
					writer.flush();
				}
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
	public static String getCharsetName(String fileFullName)
	{
		String code=null;
		try {
			BufferedInputStream bin=new BufferedInputStream(new FileInputStream(fileFullName));
			int p=(bin.read()<<8)+bin.read();
			switch(p)
			{
			case 0xefbb:  
	            code = "UTF-8";  
	            break;  
	        case 0xfffe:  
	            code = "Unicode";  
	            break;  
	        case 0xfeff:  
	            code = "Unicode";  //"UTF-16BE"
	            break;  
	        default:  
	            code = "GBK"; 
			}
			bin.close();
			logger.info("charset name:{}",code);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return code;
	}
	

}
