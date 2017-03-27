package com.lombardrisk.test;
import java.io.File;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.*;

import com.lombardrisk.commons.Dom4jUtil;
import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.pojo.Form;


public class FormsDataProvider implements IComFolder{
	private static List<Form> forms;
	protected FormsDataProvider(){}
	@DataProvider(name="FormInstances")
	public static Object[][] dataProviderForms(ITestContext context)
	{
		String formsFullPath=null;
		String scenarioName=context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME);
		if(scenarioName==null || scenarioName.trim().equals(""))
		{
			Reporter.log("parameter scenarioName is null or empty.<br>");
		}else
		{
			formsFullPath=SOURCE_SCENARIOS_FOLDER+scenarioName.trim();
		}
		if(formsFullPath==null || formsFullPath.equals("")){formsFullPath="suites/forms.xml";}
		File formsFile=new File(formsFullPath);
		if(formsFile.exists())
		{
			String tmp=formsFile.getAbsolutePath();
			if(tmp.endsWith(".xml"))
			{
				forms=Dom4jUtil.getForms(tmp);
			}else if(tmp.endsWith(".xlsx") || tmp.endsWith(".xls") )
			{
				forms=ExcelUtil.getForms(tmp,context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_SHEET));
			}
			else
			{
				String logInfo="scenario file type is wrong, can't do analysis."+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
				Reporter.log(logInfo+"<br>");
				Assert.fail(logInfo);
				return null;
			}
		}else
		{
			String logInfo="scenario file is incorrect."+formsFullPath;
			Reporter.log(logInfo+"<br>");
			Assert.fail(logInfo);
			return null;
		}
		Object[][] data=null;
		int formCount=forms.size();
		if(formCount>0)
		{
			data=new Object[forms.size()][];
			for(int i=0;i<formCount;i++)
			{
				if(forms.get(i).getVersion()!=null){forms.get(i).setVersion(forms.get(i).getVersion().toLowerCase());}
				if(forms.get(i).getProcessDate().length()==9){forms.get(i).setProcessDate("0"+forms.get(i).getProcessDate());}
				forms.get(i).setExec_ExpectationFile(null);
				forms.get(i).setExecutionStatus(null);
				data[i]=new Object[]{forms.get(i)};
			}
		}
		return data;
	}
	
	public static List<Form> getForms()
	{
		return forms;
	}
	
	public static int getFormSize()
	{
		return forms.size();
	}
	
	

}