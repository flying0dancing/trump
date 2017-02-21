package com.lombardrisk.test;


import java.util.List;

import org.yiwan.webcore.test.pojo.TestEnvironment;

public class DBInfo{
	public enum InstanceType{CODE,LABEL;}
	private static String applicationServer_Name;
	private static String applicationServer_UserName;
	private static String applicationServer_Password;
	private static String applicationServer_Url;
	private static String applicationServer_Key;
	
	private static String databaseServer_Name;
	private static String databaseServer_Driver;
	private static String databaseServer_host;
	private static String databaseServer_Schema;
	private static String databaseServer_UserName;
	private static String databaseServer_Password;
	
	private static String toolSetDatabaseServer_Name;
	private static String toolSetDatabaseServer_Driver;
	private static String toolSetDatabaseServer_host;
	private static String toolSetDatabaseServer_Schema;
	private static String toolSetDatabaseServer_UserName;
	private static String toolSetDatabaseServer_Password;
	
	private static String connectedDB;
	private static String ip_ar;
	private static String ip_toolset;
	private static String sid_ar;
	private static String sid_toolset;
	private static TestEnvironment testEnv;
	private static int indexAppServer;
	private static int indexDBServer;
	private static int indexToolsetDBServer;
	
	private DBInfo(){}
	/*
	{
    "applicationServers": [
      {
        "id": 0,
        "name": "default",
        "url": "http://172.20.30.75:8085",
        "username": "admin",
        "password": "password"
      }
    ],
    "databaseServers": [
      {
        "id": 0,
        "name": "ar_database",
        "driver": "oracle",
        "host": "172.20.31.239@ora12c",
        "schema": "AR_AUTO_TOOLSET_OWNER"
      },
      {
        "id": 1,
        "name": "toolset_database",
        "driver": "oracle",
        "host": "172.20.20.49@ora12c",
        "schema": "ECR_HKMA_AUTO_SYSTEM"
      },
      {
       "id": 0,
        "name": null,
        "driver": null,
        "url": null,
        "host": null,
        "port": null,
        "version": null,
        "instance": null,
        "schema": null,
        "dump": null,
        "username": null,
        "password": null,}
    ]
  }
	*/
	
	public static void setDBInfo()
	{
		try
		{
			testEnv=TestManager.getTestEnv();
			int countOfDBServers=testEnv.getDatabaseServers().size();
			indexAppServer=TestManager.getIndexAppServer();
			applicationServer_Name=testEnv.getApplicationServer(indexAppServer).getName();
			applicationServer_UserName=testEnv.getApplicationServer(indexAppServer).getUsername();
			applicationServer_Password=testEnv.getApplicationServer(indexAppServer).getPassword();
			applicationServer_Url=testEnv.getApplicationServer(indexAppServer).getUrl();
			applicationServer_Key=testEnv.getApplicationServer(indexAppServer).getKey();
			indexDBServer=TestManager.getIndexDBServer();
			databaseServer_Name=testEnv.getDatabaseServer(indexDBServer).getName();
			databaseServer_Driver=testEnv.getDatabaseServer(indexDBServer).getDriver();
			databaseServer_host=testEnv.getDatabaseServer(indexDBServer).getHost();
			databaseServer_Schema=testEnv.getDatabaseServer(indexDBServer).getSchema();
			databaseServer_UserName=testEnv.getDatabaseServer(indexDBServer).getUsername();
			databaseServer_Password=testEnv.getDatabaseServer(indexDBServer).getPassword();
			indexToolsetDBServer=TestManager.getIndexToolsetDBServer();
			if(countOfDBServers>1 && indexToolsetDBServer>=0 && indexDBServer!=indexToolsetDBServer && indexToolsetDBServer<countOfDBServers && testEnv.getDatabaseServer(indexToolsetDBServer)!=null && testEnv.getDatabaseServer(indexToolsetDBServer).getName().toLowerCase().contains("toolset"))
			{
				toolSetDatabaseServer_Name=testEnv.getDatabaseServer(indexToolsetDBServer).getName();
				toolSetDatabaseServer_Driver=testEnv.getDatabaseServer(indexToolsetDBServer).getDriver();
				toolSetDatabaseServer_host=testEnv.getDatabaseServer(indexToolsetDBServer).getHost();
				toolSetDatabaseServer_Schema=testEnv.getDatabaseServer(indexToolsetDBServer).getSchema();
				toolSetDatabaseServer_UserName=testEnv.getDatabaseServer(indexToolsetDBServer).getUsername();
				toolSetDatabaseServer_Password=testEnv.getDatabaseServer(indexToolsetDBServer).getPassword();
			}
			
			if(toolSetDatabaseServer_Name==null)
			{connectedDB="ar"; }
			else{connectedDB="toolset";}
			
			if(databaseServer_Driver!=null && databaseServer_Driver.equalsIgnoreCase("oracle"))
			{
				String[] str=databaseServer_host.split("@");
				ip_ar=str[0];
				sid_ar=str[1];
			}
			if(toolSetDatabaseServer_Driver!=null && toolSetDatabaseServer_Driver.equalsIgnoreCase("oracle"))
			{
				String[] str=toolSetDatabaseServer_host.split("@");
				ip_toolset=str[0];
				sid_toolset=str[1];
			}
			DBQuery.setDBQuery();
			System.out.println("used applicationServer_Url: "+applicationServer_Url);
			System.out.println("used indexAppServer(id): "+indexAppServer+" indexDBServer(id):"+indexDBServer+" indexToolsetDBServer(id):"+indexToolsetDBServer);
			
		}catch(InterruptedException interruptedException)
		{
			interruptedException.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static String getConnectedDB()
	{return connectedDB;}
	public static String getIp_ar()
	{return ip_ar;}
	public static String getSid_ar()
	{return sid_ar;}
	public static String getIp_toolset()
	{return ip_toolset;}
	public static String getSid_toolset()
	{return sid_toolset;}
	public static String getApplicationServer_Name()
	{return applicationServer_Name;}
	
	public static String getApplicationServer_UserName()
	{return applicationServer_UserName;}

	public static String getApplicationServer_Password()
	{return applicationServer_Password;}
	
	public static String getApplicationServer_Url()
	{return applicationServer_Url;}
	
	public static String getApplicationServer_Key() 
	{	return applicationServer_Key;}
	
	public static String getDatabaseServer_Name()
	{return databaseServer_Name;}
	
	public static String getDatabaseServer_Driver()
	{return databaseServer_Driver;}
	
	public static String getDatabaseServer_host()
	{return databaseServer_host;}
	
	public static String getDatabaseServer_Schema()
	{return databaseServer_Schema;}
	
	public static String getDatabaseServer_UserName()
	{return databaseServer_UserName;}
	
	public static String getDatabaseServer_Password()
	{return databaseServer_Password;}
	
	public static String getToolSetDB_Name()
	{return toolSetDatabaseServer_Name;}
	
	public static String getToolSetDB_Driver()
	{return toolSetDatabaseServer_Driver;}
	
	public static String getToolSetDB_host()
	{return toolSetDatabaseServer_host;}
	
	public static String getToolSetDB_Schema()
	{return toolSetDatabaseServer_Schema;}
	
	public static String getToolSetDB_UserName()
	{return toolSetDatabaseServer_UserName;}
	
	public static String getToolSetDB_Password()
	{return toolSetDatabaseServer_Password;}
	
	/**
	 * get regulator prefix for toolset
	 * 
	 * @param Regulator
	 * @return prefix
	 */
	public static String getToolsetRegPrefix(String regulator)
	{
		if (regulator.equalsIgnoreCase("European Common Reporting"))
			return "ECR";
		else if (regulator.equalsIgnoreCase("Hong Kong Monetary Authority"))
			return "HKMA";
		else
			return "";
	}
	
	/**
	 * get Regulator DESCRIPTION list
	 * @return
	 */
	public static List<String> getRegulatorDescription()
	{
		String SQL="SELECT \"DESCRIPTION\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"STATUS\"='A' ";
		return DBQuery.queryRecords(SQL);
	}
	
	/**
	 * get Regulator Prefix like HKMA/FED/MAS
	 * @author kun shen
	 * @param regulator
	 * @return
	 */
	public static String getRegulatorPrefix(String regulator)
	{
		String SQL = "SELECT \"PREFIX\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "'  AND \"STATUS\"='A' ";
		return DBQuery.queryRecord(SQL);

	}
	
	
	/**
	 * get Regulator IDRange Start
	 * 
	 * @param regulator
	 * @return IDRangeStart
	 */
	public static String getRegulatorIDRangeStart(String regulator)
	{
		String SQL = "SELECT \"ID_RANGE_START\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "'  AND \"STATUS\"='A' ";
		return DBQuery.queryRecord(SQL);

	}
	
	/**
	 * get Regulator IDRange End
	 * 
	 * @param Regulator
	 * @return IDRangeEnd
	 */
	public static String getRegulatorIDRangEnd(String regulator)
	{
		String SQL = "SELECT \"ID_RANGE_END\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "' AND \"STATUS\"='A'  ";
		return DBQuery.queryRecord(SQL);
	}
	
	/**
	 * get page name from database server
	 * @param regulator
	 * @param form
	 * @param version
	 * @param cellName
	 * @param extendCell null means previous cellName is not extendCell, not null means previous cellName is extendCell
	 * @return
	 */
	public static List<String> getPageName(String regulator, String form, String version, String cellName, String extendCell)
	{
		String SQL = "";
		String refTable = "";
		if (connectedDB.equalsIgnoreCase("ar"))
		{
			String ID_Start = getRegulatorIDRangeStart(regulator);
			String ID_End = getRegulatorIDRangEnd(regulator);
			if (extendCell == null)
				refTable = "CFG_RPT_Ref";
			else
				refTable = "CFG_RPT_GridRef";
			SQL = "select \"PageName\" from \"CFG_RPT_List\" " + "where \"ReturnId\" IN(SELECT \"ReturnId\" FROM \"CFG_RPT_Rets\" where \"Return\"='" + form + "' AND \"Version\"=" + version
					+ " and \"ID\" between " + ID_Start + " and " + ID_End + ") " + "and \"TabName\" in (select \"TabName\" from \"" + refTable + "\" "
					+ "where \"ReturnId\" IN(SELECT \"ReturnId\" FROM \"CFG_RPT_Rets\" where \"Return\"='" + form + "' AND \"Version\"=" + version + " and \"ID\" between " + ID_Start + " and "
					+ ID_End + ") " + "and \"Item\"='" + cellName + "') and \"ID\" between " + ID_Start + " and " + ID_End;

		}
		else if (connectedDB.equalsIgnoreCase("toolset"))
		{
			String regPrefix = getToolsetRegPrefix(regulator);
			if (extendCell == null)
				refTable = regPrefix + "Ref";
			else
				refTable = regPrefix + "GridRef";
			SQL = "select \"PageName\" from \"" + regPrefix + "List\" " + "where \"ReturnId\" IN(SELECT \"ReturnId\" FROM \"" + regPrefix + "Rets\" where \"Return\"='" + form + "' "
					+ "and \"TabName\" in (select \"TabName\" from \"" + refTable + "\" " + "where \"ReturnId\" IN(SELECT \"ReturnId\" FROM \"" + regPrefix + "Rets\" where \"Return\"='" + form
					+ "' " + "and \"Version\"='" + version + "') and \"Item\"='" + cellName + "'))";

		}
		return DBQuery.queryRecords(SQL);
	}
	
	/**
	 * get user who created this form instance, if no result in database, return null.
	 * @author kun shen
	 * @param regulator
	 * @param form
	 * @param version
	 * @param processDate
	 * @return
	 */
	public static String getFormInstanceCreatedBy(String regulator,String form,String version,String processDate)
	{
		String SQL="";
		String config_Prefix = getRegulatorPrefix(regulator);
		//processDate format mm/dd/yyyy
		SQL="select \"CREATED_BY\" from \"FIN_FORM_INSTANCE\" where \"EDITION_STATUS\"='ACTIVE' and \"FORM_CODE\"='"+form+"' and \"FORM_VERSION\"='"+version+"' and \"REFERENCE_DATE\"= to_date('"+processDate+"', 'mm/dd/yyyy')  and \"CONFIG_PREFIX\"='"+config_Prefix+"'";
		
		return DBQuery.queryRecord(SQL);
		
	}
	
	/**
	 * get ATTESTATION_STATUS of this form instance
	 * @author kun shen
	 * @param regulator
	 * @param form
	 * @param version
	 * @param processDate
	 * @return
	 */
	public static String getFormInstanceAttestedStatus(String regulator,String form,String version,String processDate)
	{
		String SQL="";
		String config_Prefix = getRegulatorPrefix(regulator);
		//processDate format mm/dd/yyyy
		SQL="select \"ATTESTATION_STATUS\" from \"FIN_FORM_INSTANCE\" where \"EDITION_STATUS\"='ACTIVE' and \"FORM_CODE\"='"+form+"' and \"FORM_VERSION\"='"+version+"' and \"REFERENCE_DATE\"= to_date('"+processDate+"', 'mm/dd/yyyy')  and \"CONFIG_PREFIX\"='"+config_Prefix+"'";
		
		return DBQuery.queryRecord(SQL);
		
	}
	
	/**
	 * get InstCode(instanceCode) or InstDescription(instanceLabel) from database server.
	 * @author kun shen
	 * @param regulator
	 * @param form
	 * @param version
	 * @param pageName in web page
	 * @param instanceCodeOrLabel in web page
	 * @param instanceType CODE or LABEL identify previous parameter is instanceCode or instanceLabel
	 * @return InstCode, if return -1 means not exists InstCode
	 */
	public static String getInstance(String regulator, String form, String version,String pageName,String instanceCodeOrLabel,InstanceType instanceType)
	{
		String SQL="";
		if (connectedDB.equalsIgnoreCase("ar"))
		{
			String ID_Start = getRegulatorIDRangeStart(regulator);
			String ID_End = getRegulatorIDRangEnd(regulator);
			SQL="select \"InstSetId\" from \"CFG_RPT_List\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"PageName\"='"+pageName+"' and \"ReturnId\" in (select \"ReturnId\" from \"CFG_RPT_Rets\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"Return\"='"+form+"' and \"Version\"="+version+")";
			String instSetId=DBQuery.queryRecord(SQL).trim();
			if(instSetId.equals("-1"))
			{
				return instSetId;
			}
			else
			{
				if(instanceType==InstanceType.CODE)
				{
					SQL="select \"InstDescription\" from \"CFG_RPT_Instances\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"InstSetId\"="+instSetId+" and \"InstCode\"='"+instanceCodeOrLabel+"'";
				}else
				{
					SQL="select \"InstCode\" from \"CFG_RPT_Instances\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"InstSetId\"="+instSetId+" and \"InstDescription\"='"+instanceCodeOrLabel+"'";
				}
			}
			
		}else if (connectedDB.equalsIgnoreCase("toolset"))
		{
			String regPrefix = getToolsetRegPrefix(regulator);
			SQL="select DISTINCT b.\"InstSetId\" from \""+regPrefix+"Rets\" a inner join \""+regPrefix+"List\" b on a.\"ReturnId\"=b.\"ReturnId\" "
					+ "where a.\"Return\"='"+form+"' and a.\"Version\"='"+version+"'";
			String instSetId=DBQuery.queryRecord(SQL).trim();
			if(instSetId.equals("-1"))
			{
				return instSetId;
			}
			else
			{
				if(instanceType==InstanceType.CODE)
				{
					SQL="select \"InstDescription\" from \""+regPrefix+"Instances\" where \"InstSetId\"='"+instSetId+"' and \"InstCode\"='"+instanceCodeOrLabel+"'";
				}else
				{
					SQL="select \"InstCode\" from \""+regPrefix+"Instances\" where \"InstSetId\"='"+instSetId+"' and \"InstDescription\"='"+instanceCodeOrLabel+"'";
				}
			}
		}
		return DBQuery.queryRecord(SQL);
	}
/**
 * get language from database
 * the default userName is application server's username.
 * @return language
 */
	public static String getLanguage()
	{
		String language="";
		String userName=applicationServer_UserName;
		// update user language
		String SQL = "SELECT MAX(\"ID\") FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and upper(\"PREFERENCE_NAME\")='LANGUAGE'";
		
		String id = DBQuery.queryRecord(SQL);
		SQL = "SELECT \"PREFERENCE_CODE\" FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and \"ID\"=" + id;
		language = DBQuery.queryRecord(SQL);
		return language;
	}
	/***
	 * get language from database
	 * @param userName
	 * @return language
	 */
	public static String getLanguage(String userName)
	{
		String language="";
		// update user language
		String SQL = "SELECT MAX(\"ID\") FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and upper(\"PREFERENCE_NAME\")='LANGUAGE'";
		
		String id = DBQuery.queryRecord(SQL);
		SQL = "SELECT \"PREFERENCE_CODE\" FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and \"ID\"=" + id;
		language = DBQuery.queryRecord(SQL);
		return language;
	}

	

	

	
}
