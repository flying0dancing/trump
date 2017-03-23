package com.lombardrisk.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yiwan.webcore.test.ITestDataManager;

import com.lombardrisk.test.pojo.Form;


public class TestDataManager implements ITestDataManager{
	//private final static Logger logger = LoggerFactory.getLogger(TestDataManager.class);
	private HashMap<String,List<Form>> formsMap;

	public TestDataManager() {
		formsMap=new HashMap<String,List<Form>>();
    }
	
	public TestDataManager(int indexAppServer,  int indexDBServer,  int indexToolsetDBServer) {
		formsMap=new HashMap<String,List<Form>>();
		DBInfo.setDBInfo(indexAppServer, indexDBServer, indexToolsetDBServer);
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

}
