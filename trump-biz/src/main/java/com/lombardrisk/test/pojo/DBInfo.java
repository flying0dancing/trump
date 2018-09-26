package com.lombardrisk.test.pojo;


import java.util.List;

import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.test.pojo.TestEnvironment;

import com.lombardrisk.test.DBQuery;

public class DBInfo{
	public enum InstanceType{CODE,LABEL;}
	private String applicationServer_Name;
	private String applicationServer_UserName;
	private String applicationServer_UserPassword;
	private String applicationServer_Url;
	private String applicationServer_Key;
	
	private String databaseServer_Name;
	private String databaseServer_Driver;
	private String databaseServer_host;
	private String databaseServer_Schema;
	private String databaseServer_UserName;
	private String databaseServer_UserPassword;
	
	private String toolSetDatabaseServer_Name;
	private String toolSetDatabaseServer_Driver;
	private String toolSetDatabaseServer_host;
	private String toolSetDatabaseServer_Schema;
	private String toolSetDatabaseServer_UserName;
	private String toolSetDatabaseServer_UserPassword;
	
	private String connectedDB;
	private String ip_ar;
	private String ip_toolset;
	private String sid_ar;
	private String sid_toolset;
	private TestEnvironment testEnv;
	private int indexAppServer;
	private int indexDBServer;
	private int indexToolsetDBServer;
	private DBQuery dBQuery;

	public DBInfo(int _indexAppServer, int _indexDBServer,int _indexToolsetDBServer)
	{
		setDBInfo(_indexAppServer, _indexDBServer,_indexToolsetDBServer);
	}
	
	public void setDBInfo(int _indexAppServer, int _indexDBServer,int _indexToolsetDBServer)
	{
		try
		{
			indexAppServer=_indexAppServer;
			indexDBServer=_indexDBServer;
			indexToolsetDBServer=_indexToolsetDBServer;
			testEnv=TestCaseManager.pollTestEnvironment();
			while(testEnv==null)
			{
				TestCaseManager.offerTestEnvironment(testEnv);
				testEnv=TestCaseManager.pollTestEnvironment();
				Thread.sleep(10000);
			}
			
			
			int countOfDBServers=testEnv.getDatabaseServers().size();
			applicationServer_Name=testEnv.getApplicationServer(indexAppServer).getName();
			applicationServer_UserName=testEnv.getApplicationServer(indexAppServer).getUsername();
			applicationServer_UserPassword=testEnv.getApplicationServer(indexAppServer).getPassword();
			applicationServer_Url=testEnv.getApplicationServer(indexAppServer).getUrl();
			applicationServer_Key=testEnv.getApplicationServer(indexAppServer).getKey();
			//
			databaseServer_Name=testEnv.getDatabaseServer(indexDBServer).getName();
			databaseServer_Driver=testEnv.getDatabaseServer(indexDBServer).getDriver();
			databaseServer_host=testEnv.getDatabaseServer(indexDBServer).getHost();
			databaseServer_Schema=testEnv.getDatabaseServer(indexDBServer).getSchema();
			databaseServer_UserName=testEnv.getDatabaseServer(indexDBServer).getUsername();
			databaseServer_UserPassword=testEnv.getDatabaseServer(indexDBServer).getPassword();
			//
			if(countOfDBServers>1 && indexToolsetDBServer>=0 && indexDBServer!=indexToolsetDBServer && indexToolsetDBServer<countOfDBServers && testEnv.getDatabaseServer(indexToolsetDBServer)!=null && testEnv.getDatabaseServer(indexToolsetDBServer).getName().toLowerCase().contains("toolset"))
			{
				toolSetDatabaseServer_Name=testEnv.getDatabaseServer(indexToolsetDBServer).getName();
				toolSetDatabaseServer_Driver=testEnv.getDatabaseServer(indexToolsetDBServer).getDriver();
				toolSetDatabaseServer_host=testEnv.getDatabaseServer(indexToolsetDBServer).getHost();
				toolSetDatabaseServer_Schema=testEnv.getDatabaseServer(indexToolsetDBServer).getSchema();
				toolSetDatabaseServer_UserName=testEnv.getDatabaseServer(indexToolsetDBServer).getUsername();
				toolSetDatabaseServer_UserPassword=testEnv.getDatabaseServer(indexToolsetDBServer).getPassword();
			}
			if(testEnv!=null)
			{
				TestCaseManager.offerTestEnvironment(testEnv);
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
			setDBQuery(new DBQuery(this));
			
			
		}catch(InterruptedException interruptedException)
		{
			interruptedException.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public String getConnectedDB()
	{return connectedDB;}
	public String getIp_ar()
	{return ip_ar;}
	public String getSid_ar()
	{return sid_ar;}
	public String getIp_toolset()
	{return ip_toolset;}
	public String getSid_toolset()
	{return sid_toolset;}
	public String getApplicationServer_Name()
	{return applicationServer_Name;}
	
	public String getApplicationServer_UserName()
	{return applicationServer_UserName;}

	public String getApplicationServer_UserPassword()
	{return applicationServer_UserPassword;}
	
	public String getApplicationServer_Url()
	{return applicationServer_Url;}
	
	public String getApplicationServer_Key() 
	{	return applicationServer_Key;}
	
	public String getDatabaseServer_Name()
	{return databaseServer_Name;}
	
	public String getDatabaseServer_Driver()
	{return databaseServer_Driver;}
	
	public String getDatabaseServer_host()
	{return databaseServer_host;}
	
	public String getDatabaseServer_Schema()
	{return databaseServer_Schema;}
	
	public String getDatabaseServer_UserName()
	{return databaseServer_UserName;}
	
	public String getDatabaseServer_UserPassword()
	{return databaseServer_UserPassword;}
	
	public String getToolSetDB_Name()
	{return toolSetDatabaseServer_Name;}
	
	public String getToolSetDB_Driver()
	{return toolSetDatabaseServer_Driver;}
	
	public String getToolSetDB_host()
	{return toolSetDatabaseServer_host;}
	
	public String getToolSetDB_Schema()
	{return toolSetDatabaseServer_Schema;}
	
	public String getToolSetDB_UserName()
	{return toolSetDatabaseServer_UserName;}
	
	public String getToolSetDB_UserPassword()
	{return toolSetDatabaseServer_UserPassword;}
	
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
	public List<String> getRegulatorDescription()
	{
		String SQL="SELECT \"DESCRIPTION\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"STATUS\"='A' ";
		return getDBQuery().queryRecords(SQL);
	}
	
	/**
	 * get Regulator Prefix like HKMA/FED/MAS
	 * @author kun shen
	 * @param regulator
	 * @return
	 */
	public String getRegulatorPrefix(String regulator)
	{
		String SQL = "SELECT \"PREFIX\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "'  AND \"STATUS\"='A' ";
		return getDBQuery().queryRecord(SQL);

	}
	
	
	/**
	 * get Regulator IDRange Start
	 * 
	 * @param regulator
	 * @return IDRangeStart
	 */
	public String getRegulatorIDRangeStart(String regulator)
	{
		//SELECT "ID_RANGE_START" FROM "CFG_INSTALLED_CONFIGURATIONS" WHERE lower("DESCRIPTION")=lower('abc')
		String SQL = "SELECT \"ID_RANGE_START\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "'  AND \"STATUS\"='A' ";
		return getDBQuery().queryRecord(SQL);

	}
	
	/**
	 * get Regulator IDRange End
	 * 
	 * @param Regulator
	 * @return IDRangeEnd
	 */
	public String getRegulatorIDRangEnd(String regulator)
	{
		//SELECT "ID_RANGE_END" FROM "CFG_INSTALLED_CONFIGURATIONS" WHERE lower("DESCRIPTION")=lower('abc')
		String SQL = "SELECT \"ID_RANGE_END\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE lower(\"DESCRIPTION\")='" + regulator.toLowerCase() + "' AND \"STATUS\"='A'  ";
		return getDBQuery().queryRecord(SQL);
	}
	
	/**
	 * get entity code by regulator description and entity's name
	 * @param regulator
	 * @param entityName
	 * @return
	 */
	public String getEntityCode(String regulator,String entityName)
	{
		String SQL="";
		/*String ID_Start = getRegulatorIDRangeStart(regulator);
		String ID_End = getRegulatorIDRangEnd(regulator);
		SQL="select \"ENTITY_CODE\" from \"USR_NATIVE_ENTITY\" where \"ENTITY_NAME\"='"+entityName+"' and \"ID\" between "+ID_Start+" and "+ID_End;*/
		SQL="select \"ENTITY_CODE\" from \"USR_NATIVE_ENTITY\" where \"ENTITY_NAME\"='"+entityName+"'";
		return getDBQuery().queryRecord(SQL);
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
	public List<String> getPageName(String connectedDB,String regulator, String form, String version, String cellName, String extendCell)
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
		
		return getDBQuery().queryRecords(SQL);
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
	public String getFormInstanceCreatedBy(String regulator,String form,String version,String processDate)
	{
		String SQL="";
		String config_Prefix = getRegulatorPrefix(regulator);
		//processDate format mm/dd/yyyy
		SQL="select \"CREATED_BY\" from \"FIN_FORM_INSTANCE\" where \"EDITION_STATUS\"='ACTIVE' and \"FORM_CODE\"='"+form+"' and \"FORM_VERSION\"='"+version+"' and \"REFERENCE_DATE\"= to_date('"+processDate+"', 'mm/dd/yyyy')  and \"CONFIG_PREFIX\"='"+config_Prefix+"'";
		return getDBQuery().queryRecord(SQL);
		
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
	public String getFormInstanceAttestedStatus(String regulator,String form,String version,String processDate)
	{
		String SQL="";
		String config_Prefix = getRegulatorPrefix(regulator);
		//processDate format mm/dd/yyyy
		SQL="select \"ATTESTATION_STATUS\" from \"FIN_FORM_INSTANCE\" where \"EDITION_STATUS\"='ACTIVE' and \"FORM_CODE\"='"+form+"' and \"FORM_VERSION\"='"+version+"' and \"REFERENCE_DATE\"= to_date('"+processDate+"', 'mm/dd/yyyy')  and \"CONFIG_PREFIX\"='"+config_Prefix+"'";
		return getDBQuery().queryRecord(SQL);
		
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
	public String getInstance(String connectedDB,String regulator, String form, String version,String pageName,String instanceCodeOrLabel,InstanceType instanceType)
	{
		String SQL="";
		if (connectedDB.equalsIgnoreCase("ar"))
		{
			String ID_Start = getRegulatorIDRangeStart(regulator);
			String ID_End = getRegulatorIDRangEnd(regulator);
			//select "InstSetId" from "CFG_RPT_List" where "ID" BETWEEN 13000001 and 14000000 and "PageName"='P1 (R010-R6070~C6000-C080)' and "ReturnId" in (select "ReturnId" from "CFG_RPT_Rets" where "ID" BETWEEN 13000001 and 14000000 and "Return"='PRA110' and "Version"=1)
			SQL="select \"InstSetId\" from \"CFG_RPT_List\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"PageName\"='"+pageName+"' and \"ReturnId\" in (select \"ReturnId\" from \"CFG_RPT_Rets\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"Return\"='"+form+"' and \"Version\"="+version+")";
			String instSetId=getDBQuery().queryRecord(SQL).trim();
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
					//select "InstCode" from "CFG_RPT_Instances" where "ID" BETWEEN 13000001 and 14000000 and "InstSetId"=1 and "InstDescription"='Egyptian Pound'
					SQL="select \"InstCode\" from \"CFG_RPT_Instances\" where \"ID\" BETWEEN "+ID_Start+" and "+ID_End+" and \"InstSetId\"="+instSetId+" and \"InstDescription\"='"+instanceCodeOrLabel+"'";
				}
			}
			
		}else if (connectedDB.equalsIgnoreCase("toolset"))
		{
			String regPrefix = getToolsetRegPrefix(regulator);
			SQL="select DISTINCT b.\"InstSetId\" from \""+regPrefix+"Rets\" a inner join \""+regPrefix+"List\" b on a.\"ReturnId\"=b.\"ReturnId\" "
					+ "where a.\"Return\"='"+form+"' and a.\"Version\"='"+version+"'";
			String instSetId=getDBQuery().queryRecord(SQL).trim();
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
		return getDBQuery().queryRecord(SQL);
	}

	/***
	 * get language from database
	 * @param userName
	 * @return language
	 */
	public String getLanguage(String userName)
	{
		String language="";
		// update user language
		String SQL = "SELECT MAX(\"ID\") FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and upper(\"PREFERENCE_NAME\")='LANGUAGE'";
		String id = getDBQuery().queryRecord(SQL);
		SQL = "SELECT \"PREFERENCE_CODE\" FROM \"USR_PREFERENCE\" WHERE lower(\"USER_ID\")='" + userName.toLowerCase() + "' and \"ID\"=" + id;
		language = getDBQuery().queryRecord(SQL);
		return language;
	}

	public DBQuery getDBQuery() {
		return dBQuery;
	}

	public void setDBQuery(DBQuery dBQuery) {
		this.dBQuery = dBQuery;
	}

	public void resetDeActivateDate()
	{
		//String ID_Start = getRegulatorIDRangeStart(regulator);
		//String ID_End = getRegulatorIDRangEnd(regulator);
		//String SQL="update \"CFG_RPT_Rets\" set \"DeActivateDate\" =null where ID BETWEEN "+ID_Start+" and "+ID_End+" and \"DeActivateDate\" is not null ";
		String SQL="update \"CFG_RPT_Rets\" set \"DeActivateDate\" =null where \"DeActivateDate\" is not null ";
		int result=getDBQuery().update(SQL);
	}
}
