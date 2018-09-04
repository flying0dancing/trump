package com.lombardrisk.commons;

public class EssentialOperation {
	private EssentialOperation(){}
	
	/**
	 * remove last file separator in path.
	 * @author kun shen
	 * @param path
	 * @return
	 */
	public static String removeLastFileSeparator(String path)
	{
		if(path.endsWith("/") || path.endsWith("\\"))
		{
			path=path.substring(0,path.length()-1);
		}	
		return path;
	}
}
