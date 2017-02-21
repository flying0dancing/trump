package com.lombardrisk.testcase;


import org.testng.Reporter;

import org.testng.annotations.Test;


import com.lombardrisk.test.*;
import com.lombardrisk.test.pojo.Form;
/**
 * mock, not real test cases.
 * @author kun shen
 *
 */
public class Mock extends TestManager {
	//private static int formCount=0;
	
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void testMock( Form form) throws Exception
	{
		System.out.println(getClass().getName()+" a in testMock running~~~~!"); 
		Reporter.log(" in testValue running~~~~<br>");
		Reporter.log(form.toString());
		form.setExecutionStatus("skip");
		
		//formCount++;
		
	}
	
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void testMock3( Form form) throws Exception
	{
		System.out.println(getClass().getName()+" a in testMock3 running~~~~!"); 
		Reporter.log(" in testValue3 running~~~~<br>");
		Reporter.log(form.toString());
		
		//formCount++;
	
	}


	/*@AfterMethod
	  private void afterMethod(ITestResult result) {
		ITestContext context=result.getTestContext();
		ITestNGMethod method=result.getMethod();
		logger.info(getClass().getName()+" afterMethod("+method.getMethodName()+") running!"); 
		String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getMethodName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
		  
		  if(FormsDataProvider.getFormSize()==formCount)
		  {
			  formCount=0;
			  
			  if(resultFile.endsWith(".xml"))
			  {
				  Dom4jUtil.writeFormsToMethodXml(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+resultFile, "formlist.xsl");
			  }
			  if(resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls"))
			  {
				  ExcelUtil.WriteFormsToExcel(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+resultFile);
				  ExcelUtil.WriteFormsToExcel(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+"total.xlsx");
			  }
		  }
		  
		  Dom4jUtil.writeFormsToXml(resultFile,FormsDataProvider.getForms(),TARGET_SCENARIOS_FOLDER+"total.xml","formsTotal.xsl");
		  
	  }*/
	
	/*@AfterMethod
	  private void afterMethod(ITestContext context,ITestResult result,Method method) {
		logger.info(getClass().getName()+" afterMethod("+method.getName()+") running!"); 
		  String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
		  if(FormsDataProvider.getFormSize()==formCount)
		  {
			  formCount=0;
			  
			  if(resultFile.endsWith(".xml"))
			  {
				  Dom4jUtil.writeFormsToMethodXml(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+resultFile, "formlist.xsl");
			  }
			  if(resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls"))
			  {
				  ExcelUtil.WriteFormsToExcel(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+resultFile);
				  ExcelUtil.WriteFormsToExcel(FormsDataProvider.getForms(), TARGET_SCENARIOS_FOLDER+"total.xlsx");
			  }
			 
		  }
		  
		  Dom4jUtil.writeFormsToXml(resultFile,FormsDataProvider.getForms(),TARGET_SCENARIOS_FOLDER+"total.xml","formsTotal.xsl");
		  
	  }*/
}
