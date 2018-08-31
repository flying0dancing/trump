package com.lombardrisk.test.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Transmission {
	private String fileType;
	private String framework;
	private String taxonomy;
	private String module;
	private String compressType;
	
	public String getFileType()
	{return fileType;}
	
	public void setFileType(String fileType)
	{this.fileType=fileType;}
	
	public String getFramework()
	{return framework;}
	
	public void setFramework(String framework)
	{this.framework=framework;}
	
	public String getTaxonomy()
	{return taxonomy;}
	
	public void setTaxonomy(String taxonomy)
	{this.taxonomy=taxonomy;}
	
	public String getModule()
	{return module;}
	
	public void setModule(String module)
	{this.module=module;}
	
	public String getCompressType()
	{return compressType;}
	
	public void setCompressType(String compressType)
	{this.compressType=compressType;}
	
	public ArrayList<String> getAttributeList()
	{
		ArrayList<String> attributes=new ArrayList<String>();
		Field[] fields=getClass().getDeclaredFields();
		for(Field field:fields)
		{
			attributes.add(field.getName());
		}
		return attributes;
	}
	
	public String toString()
	{
		StringBuffer stringBuffer=new StringBuffer();
		Field[] fields=getClass().getDeclaredFields();
		for(Field field:fields)
		{
			try {
				String value=null;
				Object obj=field.get(this);
				if(obj==null || obj.toString().trim().equals(""))
				{continue;}
				else value=field.get(this).toString();
				stringBuffer.append(field.getName()+"[" + value+"] ");
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

}
