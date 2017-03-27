package com.lombardrisk.test;

import java.util.List;

import com.lombardrisk.test.pojo.DBInfo;

/**
 * Create by Leo Tu on Jun 26, 2015
 */
public class DBQuery
{
	private DBHelper dh;
	//private static DBInfo dBInfo;
	public DBQuery(DBInfo dBInfo)
	{
		setDBQuery(dBInfo);
	}
	public void setDBQuery(DBInfo dBInfo)
	{
		
		if (dBInfo.getDatabaseServer_Driver().equalsIgnoreCase("oracle"))
		{
			if (dBInfo.getConnectedDB().equalsIgnoreCase("ar"))
				dh = new DBHelper("oracle", dBInfo.getIp_ar(), dBInfo.getSid_ar(), dBInfo.getDatabaseServer_Schema());
			else
				dh = new DBHelper("oracle", dBInfo.getIp_ar(), dBInfo.getSid_ar(), dBInfo.getToolSetDB_Schema());
		}
		else
		{
			if (dBInfo.getConnectedDB().equalsIgnoreCase("ar"))
				dh = new DBHelper("sqlserver", dBInfo.getDatabaseServer_host(), dBInfo.getDatabaseServer_Schema());
			else
				dh = new DBHelper("sqlserver", dBInfo.getToolSetDB_host(), dBInfo.getToolSetDB_Schema());
		}
	}
	
	public String queryRecord(String sql)
	{
		dh.connect();
		String rst = dh.query(sql);
		dh.close();
		return rst;
	}
	
	public List<String> queryRecords(String sql)
	{
		dh.connect();
		List<String> rst = dh.queryRecords(sql);
		dh.close();
		return rst;
	}
	
/*
	public static String queryRecordSpecDB(String dbName, String sql)
	{
		if (dBInfo.getDatabaseServer_Driver().equalsIgnoreCase("oracle"))
		{
			dh = new DBHelper("oracle", dBInfo.getIp_ar(), dBInfo.getSid_ar(), dbName);
		}
		else
		{
			dh = new DBHelper("sqlserver", dBInfo.getDatabaseServer_host(), dbName);
		}
		dh.connect();
		String rst = dh.query(sql);
		dh.close();
		return rst;
	}

	*/
	

	public int update(String sql)
	{
		dh.connect();
		int rst = dh.update(sql);
		dh.close();
		return rst;
	}

	public void updateSourceVew(String sql)
	{
		dh = new DBHelper("oracle", "172.20.20.49", "ora12c", "SCHD1_DMP");
		dh.connect();
		dh.update(sql);
		dh.close();
	}
	
}
