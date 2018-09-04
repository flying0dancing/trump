package com.lombardrisk.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yiwan.webcore.test.ITestDataManager;

import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;
import com.lombardrisk.test.pojo.ServerInfo;


public class TestDataManager implements ITestDataManager{

	private HashMap<String,List<Form>> formsMap;
    private DBInfo dBInfo;
	private ServerInfo serverInfo;
	public TestDataManager(int indexAppServer,  int indexDBServer,  int indexToolsetDBServer) {
		formsMap=new HashMap<String,List<Form>>();
		setDBInfo(new DBInfo(indexAppServer, indexDBServer, indexToolsetDBServer));
		setServerInfo(new ServerInfo(getDBInfo().getApplicationServer_Key()));
    }
	public HashMap<String,List<Form>> getFormsMap() {
		return formsMap;
	}
	public void setFormsMap(HashMap<String,List<Form>> formsMap) {
		this.formsMap = formsMap;
	}
	
	public void setFormsMap(String key,Form form) {
		List<Form> formsVal=null;
		if(formsMap.containsKey(key))
		{
			formsVal=formsMap.get(key);
		}else
		{
			formsVal=new ArrayList<Form>();
		}
		formsVal.add(form);
		formsMap.put(key, formsVal);
	}

	public DBInfo getDBInfo() {
		return dBInfo;
	}

	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
	}
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
}
