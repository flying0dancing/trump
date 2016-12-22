package com.lombardrisk.test;

import java.util.List;

/**
 * Create by Leo Tu on Jun 26, 2015
 */
public class DBQuery
{
	static DBHelper dh;
	private DBQuery(){}
	public static void setDBQuery()
	{
		
		if (DBInfo.getDatabaseServer_Driver().equalsIgnoreCase("oracle"))
		{
			if (DBInfo.getConnectedDB().equalsIgnoreCase("ar"))
				dh = new DBHelper("oracle", DBInfo.getIp_ar(), DBInfo.getSid_ar(), DBInfo.getDatabaseServer_Schema());
			else
				dh = new DBHelper("oracle", DBInfo.getIp_ar(), DBInfo.getSid_ar(), DBInfo.getToolSetDB_Schema());
		}
		else
		{
			if (DBInfo.getConnectedDB().equalsIgnoreCase("ar"))
				dh = new DBHelper("sqlserver", DBInfo.getDatabaseServer_host(), DBInfo.getDatabaseServer_Schema());
			else
				dh = new DBHelper("sqlserver", DBInfo.getToolSetDB_host(), DBInfo.getToolSetDB_Schema());
		}
	}
	
	public static String queryRecord(String sql)
	{
		dh.connect();
		String rst = dh.query(sql);
		dh.close();
		return rst;
	}
	
	public static List<String> queryRecords(String sql)
	{
		dh.connect();
		List<String> rst = dh.queryRecords(sql);
		dh.close();
		return rst;
	}
	

	public static String queryRecordSpecDB(String dbName, String sql)
	{
		if (DBInfo.getDatabaseServer_Driver().equalsIgnoreCase("oracle"))
		{
			dh = new DBHelper("oracle", DBInfo.getIp_ar(), DBInfo.getSid_ar(), dbName);
		}
		else
		{
			dh = new DBHelper("sqlserver", DBInfo.getDatabaseServer_host(), dbName);
		}
		dh.connect();
		String rst = dh.query(sql);
		dh.close();
		return rst;
	}

	
	

	public static int update(String sql)
	{
		dh.connect();
		int rst = dh.update(sql);
		dh.close();
		return rst;
	}

	public static void updateSourceVew(String sql)
	{
		dh = new DBHelper("oracle", "172.20.20.49", "ora12c", "SCHD1_DMP");
		dh.connect();
		dh.update(sql);
		dh.close();
	}
	/*
	public static String getCellValeFromDB(String Regulator, String formCode, String version, String processDate, String Entity, String instance, String cellId, boolean isExtendCell, int rowKey)
	{
		String value = null;
		if (connectedDB.equalsIgnoreCase("ar"))
		{
			String month = processDate.substring(3, 5);
			String day = processDate.substring(0, 2);
			String year = processDate.substring(8, 10);
			switch (month)
			{
				case "01":
					month = "JAN";
					break;
				case "02":
					month = "FEB";
					break;
				case "03":
					month = "MAR";
					break;
				case "04":
					month = "APR";
					break;
				case "05":
					month = "MAY";
					break;
				case "06":
					month = "JUN";
					break;
				case "07":
					month = "JUL";
					break;
				case "08":
					month = "AUG";
					break;
				case "09":
					month = "SEP";
					break;
				case "10":
					month = "OCT";
					break;
				case "11":
					month = "NOV";
					break;
				case "12":
					month = "DEC";
					break;
			}

			if (month.startsWith("0"))
				month = month.substring(1);
			if (day.startsWith("0"))
				day = day.substring(1);

			String REFERENCE_DATE = null;
			if (DBType.equalsIgnoreCase("oracle"))
				REFERENCE_DATE = day + "-" + month + "-" + year + " 12.00.00.000000 AM";
			else
				REFERENCE_DATE = processDate.substring(6, 10) + "-" + processDate.substring(3, 5) + "-" + processDate.substring(0, 2);

			String SQL = "SELECT \"PREFIX\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "' AND \"STATUS\"='A' ";
			String prefix = queryRecordSpecDB(ownerSchema, SQL);

			SQL = "SELECT \"ID\" FROM \"FIN_FORM_INSTANCE\" WHERE \"CONFIG_PREFIX\"='" + prefix + "' AND \"EDITION_STATUS\"='ACTIVE' AND \"LAST_EDITOR\"='" + userName.toUpperCase()
					+ "' AND \"FORM_CODE\"='" + formCode + "' AND \"FORM_VERSION\"='" + version + "' AND \"REFERENCE_DATE\"='" + REFERENCE_DATE + "' ";
			String formID = queryRecord(SQL);

			SQL = "SELECT \"ID_RANGE_START\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "'  AND \"STATUS\"='A' ";
			String startID = queryRecord(SQL);

			SQL = "SELECT \"ID_RANGE_END\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "' AND \"STATUS\"='A'  ";
			String endID = queryRecord(SQL);

			SQL = "SELECT \"ReturnId\" FROM \"CFG_RPT_Rets\" WHERE \"ID\">='" + startID + "' AND \"ID\"<='" + endID + "' AND \"Return\"='" + formCode + "' AND \"Version\"=" + version + "  ";
			String returnId = queryRecord(SQL);

			if (!instance.matches("[0-9]+"))
			{
				SQL = "SELECT \"InstPageInst\" FROM \"CFG_RPT_Instances\" WHERE \"ID\">='" + startID + "' AND \"ID\"<='" + endID + "' AND \"InstDescription\"='" + instance + "'";
				instance = queryRecord(SQL);
			}
			if (!isExtendCell)
				SQL = "SELECT \"Type\" FROM \"CFG_RPT_Ref\" WHERE \"ID\">='" + startID + "' AND \"ID\"<='" + endID + "' AND  \"Item\"='" + cellId + "' and \"ReturnId\"=" + returnId + "";
			else
				SQL = "SELECT \"Type\" FROM \"CFG_RPT_GridRef\" WHERE \"ID\">='" + startID + "' AND \"ID\"<='" + endID + "' AND  \"Item\"='" + cellId + "' and \"ReturnId\"=" + returnId + "";
			String type = queryRecord(SQL);

			String queryItem = null;
			if (type.equalsIgnoreCase("D"))
				queryItem = "DATE_VALUE";
			else if (type.equalsIgnoreCase("C"))
				queryItem = "CHAR_VALUE";
			else
				queryItem = "NUMBER_VALUE";

			if (!isExtendCell)
			{
				SQL = "SELECT \"" + queryItem + "\" FROM \"FIN_CELL_INSTANCE\" WHERE \"FORM_INSTANCE_ID\"='" + formID + "' AND \"ITEM_CODE\"='" + cellId
						+ "' AND \"VERSION\"=0 and \"Z_AXIS_ORDINATE\"=" + instance + "";
				value = queryRecord(SQL);
			}
			else
			{
				SQL = "SELECT \"" + queryItem + "\" FROM \"FIN_CELL_INSTANCE\" WHERE \"FORM_INSTANCE_ID\"='" + formID + "' AND \"X_AXIS_ORDINATE\"='" + cellId
						+ "' AND \"Y_AXIS_ORDINATE\" IS NOT NULL AND \"VERSION\"=0 and \"Z_AXIS_ORDINATE\"=" + instance + "";
				List<String> values = queryRecords(SQL);
				if (rowKey - 49 > 0)
					value = values.get(rowKey - 1);
				else
					value = values.get(0);
			}

		}
		else
		{
			String SQL = "SELECT \"ID_RANGE_START\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "'  AND \"STATUS\"='A' ";
			String RegPrefix = queryRecordSpecDB(ownerSchema, SQL);
			SQL = "SELECT \"ReturnId\" FROM \"" + RegPrefix + "Rets\" WHERE \"Return\"='" + formCode + "' and \"Version\"='" + version + "'";
			String rerurnID = queryRecord(SQL);
			SQL = "SELECT \"Page\" FROM \"" + RegPrefix + "List\" WHERE \"ReturnId\"='" + rerurnID + "'";
			String page = (SQL);
			SQL = "SELECT \"EntityId\" FROM \"" + RegPrefix + "Grps\" WHERE \"Name\"='" + Entity + "'";
			String entityID = (SQL);
			SQL = "SELECT \"InstPageInst\" FROM \"" + RegPrefix + "Instances\" WHERE \"InstDescription\"='" + instance + "'";
			String pageInstance = queryRecord(SQL);

			if (!isExtendCell)
			{
				SQL = "SELECT \"" + cellId.toUpperCase() + "\" FROM \"" + page + "\" WHERE \"STBStatus\"='A' and \"EntityId\"='" + entityID + "' and \"PageInst\"='" + pageInstance + "'";
			}
			else
			{
				SQL = "SELECT \"" + cellId.toUpperCase() + "\" FROM \"" + page + "\" WHERE \"STBStatus\"='A' and \"EntityId\"='" + entityID + "' and \"PageInst\"='" + pageInstance + "' AND \""
						+ formCode + "INDEX\"='" + rowKey + "'";
			}

			value = queryRecord(SQL);
		}

		return value;

	}

	public static int getInstanceNum(String Regulator, String formCode, String version, String processDate, String Entity)
	{
		List<String> instances = new ArrayList<String>();
		if (connectedDB.equalsIgnoreCase("ar"))
		{
			String month, day, year;
			if (format.equalsIgnoreCase("en_GB"))
			{
				month = processDate.substring(3, 5);
				day = processDate.substring(0, 2);
				year = processDate.substring(8, 10);
			}
			else
			{
				month = processDate.substring(0, 2);
				day = processDate.substring(3, 5);
				year = processDate.substring(8, 10);
			}
			switch (month)
			{
				case "01":
					month = "JAN";
					break;
				case "02":
					month = "FEB";
					break;
				case "03":
					month = "MAR";
					break;
				case "04":
					month = "APR";
					break;
				case "05":
					month = "MAY";
					break;
				case "06":
					month = "JUN";
					break;
				case "07":
					month = "JUL";
					break;
				case "08":
					month = "AUG";
					break;
				case "09":
					month = "SEP";
					break;
				case "10":
					month = "OCT";
					break;
				case "11":
					month = "NOV";
					break;
				case "12":
					month = "DEC";
					break;
			}

			if (month.startsWith("0"))
				month = month.substring(1);
			if (day.startsWith("0"))
				day = day.substring(1);

			String REFERENCE_DATE = null;
			if (DBType.equalsIgnoreCase("oracle"))
				REFERENCE_DATE = day + "-" + month + "-" + year + " 12.00.00.000000 AM";
			else
				REFERENCE_DATE = processDate.substring(6, 10) + "-" + processDate.substring(3, 5) + "-" + processDate.substring(0, 2);

			String SQL = "SELECT \"PREFIX\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "' AND \"STATUS\"='A' ";
			String prefix = queryRecordSpecDB(ownerSchema, SQL);

			SQL = "SELECT \"ID\" FROM \"FIN_FORM_INSTANCE\" WHERE \"CONFIG_PREFIX\"='" + prefix + "' AND \"EDITION_STATUS\"='ACTIVE' AND \"LAST_EDITOR\"='" + userName.toUpperCase()
					+ "' AND \"FORM_CODE\"='" + formCode + "' AND \"FORM_VERSION\"='" + version + "' AND \"REFERENCE_DATE\"='" + REFERENCE_DATE + "' ";
			String formID = queryRecord(SQL);

			SQL = "SELECT DISTINCT(\"Z_AXIS_ORDINATE\") FROM \"FIN_CELL_INSTANCE\" WHERE \"FORM_INSTANCE_ID\"=" + formID;

			instances = queryRecords(SQL);

		}
		else
		{
			String SQL = "SELECT \"ID_RANGE_START\" FROM \"CFG_INSTALLED_CONFIGURATIONS\" WHERE \"DESCRIPTION\"='" + Regulator + "'  AND \"STATUS\"='A' ";
			String RegPrefix = queryRecordSpecDB(ownerSchema, SQL);
			SQL = "SELECT \"ReturnId\" FROM \"" + RegPrefix + "Rets\" WHERE \"Return\"='" + formCode + "' and \"Version\"='" + version + "'";
			String rerurnID = queryRecord(SQL);
			SQL = "SELECT \"Page\" FROM \"" + RegPrefix + "List\" WHERE \"ReturnId\"='" + rerurnID + "'";
			String page = (SQL);
			SQL = "SELECT \"EntityId\" FROM \"" + RegPrefix + "Grps\" WHERE \"Name\"='" + Entity + "'";
			String entityID = (SQL);

		}

		return instances.size();

	}
	*/

}
