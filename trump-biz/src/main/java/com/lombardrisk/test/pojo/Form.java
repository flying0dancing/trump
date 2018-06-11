package com.lombardrisk.test.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Form {
private String name;
private String version;
private String regulator;
private String allowNull;
private String entity;
private String processDate;
private String expiration;
private String run;
private String cloneData;
private String initToZero;
private String deleteExistent;
private Transmission transmission;
private String importFile;
private String expectationFile;
private String executionStatus;
private String exec_ExpectationFile;
private int frequency;
private String translate;
private String retrieveGroup;
private String abortOnFailure;
private String importMode;
private String applyScale;

public String getName()
{return name;}

public void setName(String name)
{this.name=name;}

public String getVersion()
{return version;}

public void setVersion(String version)
{this.version=version;}

public String getRegulator()
{return regulator;}

public void setRegulator(String regulator)
{this.regulator=regulator;}

public String getAllowNull()
{return allowNull;}

public void setAllowNull(String allowNull)
{this.allowNull=allowNull;}


public String getEntity()
{return entity;}

public void setEntity(String entity)
{this.entity=entity;}


public String getProcessDate()
{return processDate;}

public void setProcessDate(String processDate)
{this.processDate=processDate;}


public String getInitToZero()
{return initToZero;}

public void setInitToZero(String initToZero)
{this.initToZero=initToZero;}

public String getDeleteExistent()
{return deleteExistent;}

public void setDeleteExistent(String deleteExistent)
{this.deleteExistent=deleteExistent;}

public Transmission getTransmission()
{return transmission;}

public void setTransmission(Transmission transmission)
{this.transmission=transmission;}

public String getRun()
{return run;}

public void setRun(String run)
{this.run=run;}

public String getCloneData()
{return cloneData;}

public void setCloneData(String cloneData)
{this.cloneData=cloneData;}

public String getExpiration()
{return expiration;}

public void setExpiration(String expiration)
{this.expiration=expiration;}


public String getImportFile()
{return importFile;}

public void setImportFile(String importFile)
{this.importFile=importFile;}

public String getExpectationFile()
{return expectationFile;}

public void setExpectationFile(String expectationFile)
{this.expectationFile=expectationFile;}

public String getExecutionStatus()
{return executionStatus;}

public void setExecutionStatus(String executionStatus)
{this.executionStatus=executionStatus;}

public String getExec_ExpectationFile()
{return exec_ExpectationFile;}

public void setExec_ExpectationFile(String exec_ExpectationFile)
{this.exec_ExpectationFile=exec_ExpectationFile;}

public String getTranslate() 
{return translate;}

public void setTranslate(String translate)
{this.translate = translate;}

public String getRetrieveGroup() 
{ return retrieveGroup;}

public void setRetrieveGroup(String retrieveGroup) 
{this.retrieveGroup = retrieveGroup;}

public String getAbortOnFailure() 
{return abortOnFailure;}

public void setAbortOnFailure(String abortOnFailure) 
{this.abortOnFailure = abortOnFailure;}

public String getImportMode() 
{return importMode;}

public void setImportMode(String importMode) 
{this.importMode = importMode;}


public String getApplyScale() 
{return applyScale;}

public void setApplyScale(String applyScale) 
{this.applyScale = applyScale;}

public ArrayList<String> getAttributeList()
{
	ArrayList<String> attributes=new ArrayList<String>();
	attributes.add("name");
	attributes.add("version");
	attributes.add("regulator");
	attributes.add("allowNull");
	
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

public int getRunFrequency()
{return frequency;}

public void setRunFrequency(int frequency)
{this.frequency=frequency;}

public void accumulateRunFrequency()
{
	setRunFrequency(getRunFrequency()+1);
}

}
