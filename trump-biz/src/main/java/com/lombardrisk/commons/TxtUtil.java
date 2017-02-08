package com.lombardrisk.commons;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtUtil
{
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

	public static String listFileByRow(File filePath, Integer row)
	{
		List<String> strList = getFileContent(filePath);
		int size = strList.size();
		if (size >= (row - 1))
			return strList.get(row - 1);
		else
			return "";

	}

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

	public static void writeToTxt(File file, String text) throws Exception
	{
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
		fw.append(text);
		fw.newLine();
		fw.flush();
		fw.close();
	}

}
