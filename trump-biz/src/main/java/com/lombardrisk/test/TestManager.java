package com.lombardrisk.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.yiwan.webcore.test.TestBase;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

import com.lombardrisk.commons.Dom4jUtil;
import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.pages.HomePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;


public class TestManager extends TestBase implements IComFolder {
	
	private ListPage listPage;
	private  int indexAppServer;
	private  int indexDBServer;
	private  int indexToolsetDBServer;
	private static long startSuiteTime;
	private String logName;
	private DBInfo dBInfo;
	public ListPage getListPage()
	{
		return listPage;
	}
	
	 @BeforeSuite
	  public void beforeSuite() {
		  startSuiteTime=System.currentTimeMillis();
		  //delete older test target folder, and create news.
		  try
		  {
			  /*if(FileUtil.checkDirectory(TARGET_FOLDER))
			  {
				  logger.info(" delete directory:"+TARGET_FOLDER);
				  FileUtil.deleteDirectory(TARGET_FOLDER);
			  }
			  logger.info(" create directory:"+TARGET_FOLDER);
			  FileUtil.createDirectory(TARGET_FOLDER);
			  logger.info(" create directory:"+TARGET_SCENARIOS_FOLDER);
			  FileUtil.createDirectory(TARGET_SCENARIOS_FOLDER);
			  logger.info(" create directory:"+TARGET_DOWNLOAD_FOLDER);
			  FileUtil.createDirectory(TARGET_DOWNLOAD_FOLDER);
			  logger.info(" create directory:"+TARGET_LOG_FOLDER);
			  FileUtil.createDirectory(TARGET_LOG_FOLDER);//
			  logger.info(" copy *.xsl and *.css to directory:"+TARGET_SCENARIOS_FOLDER);
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,".xsl", TARGET_SCENARIOS_FOLDER);
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,".css", TARGET_SCENARIOS_FOLDER);*/
			  
			  //create new result folder and subfolders if need.
			  if(!FileUtil.checkDirectory(TARGET_FOLDER))
			  {
				  logger.info(" create directory:"+TARGET_FOLDER);
				  FileUtil.createDirectory(TARGET_FOLDER);
			  }
			  if(!FileUtil.checkDirectory(TARGET_SCENARIOS_FOLDER))
			  {
				  logger.info(" create directory:"+TARGET_SCENARIOS_FOLDER);
				  FileUtil.createDirectory(TARGET_SCENARIOS_FOLDER);
			  }
			  if(!FileUtil.checkDirectory(TARGET_DOWNLOAD_FOLDER))
			  {
				  logger.info(" create directory:"+TARGET_DOWNLOAD_FOLDER);
				  FileUtil.createDirectory(TARGET_DOWNLOAD_FOLDER);
			  }
			  if(!FileUtil.checkDirectory(TARGET_LOG_FOLDER))
			  {
				  logger.info(" create directory:"+TARGET_LOG_FOLDER);
				  FileUtil.createDirectory(TARGET_LOG_FOLDER);
			  }
			  
			  logger.info(" copy *.xsl and *.css to directory:"+TARGET_SCENARIOS_FOLDER);
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,".xsl", TARGET_SCENARIOS_FOLDER);
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,".css", TARGET_SCENARIOS_FOLDER);
			  
		  }catch(Exception e)
		  {
			  logger.error(e.getMessage());
			  Assert.fail("cannot create result folder or sub-folders."+TARGET_FOLDER);
		  }
		 
	  }
	 @AfterSuite
	  public void afterSuite() { 
	  }
	
	 @BeforeTest
	  public void beforeTest(ITestContext context) {
		 logger.info(context.getName() + " start testing!<br>");
	  }
	  
	  @AfterTest
	  public void afterTest(ITestContext context) throws Exception {
		 logger.info(context.getName() + " finish testing!<br>");
	  }
	  
	  @BeforeClass(dependsOnMethods="beforeClass")
	  @Parameters({"indexAppServers", "indexDBServers", "indexToolsetDBServers"})
	  public void beforeClassInTestManager(ITestContext context, @Optional String indexAppServers, @Optional String indexDBServers, @Optional String indexToolsetDBServers) throws Exception {
		  logger.info(getClass().getName()+" beforeClass-setUpTest running!"); 
		  indexAppServer=changeStringToInt(indexAppServers,0);
		  indexDBServer=changeStringToInt(indexDBServers,0);
		  indexToolsetDBServer=changeStringToInt(indexToolsetDBServers,0);
		  setLogName();
		  setScenarioId(getLogName());
		  
		  setUpTest();
		  TestCaseManager.offerTestEnvironment(getTestEnvironment());// add this for release testenvironment
		  super.setTestDataManager(new TestDataManager(indexAppServer, indexDBServer, indexToolsetDBServer));
		  setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
		  logger.info("Database Language:"+DBInfo.getLanguage(getDBInfo().getApplicationServer_UserName()));
		  List<String> regulators=DBInfo.getRegulatorDescription();
		  int copyCount=0;
		  logger.info("copy folders(start)");
		  for(String regulator:regulators)
		  {
			  logger.info("try to copying regulator folder \""+regulator+"\" to result folder");
			  if(new File(SOURCE_EXPECTATION_FOLDER+regulator).exists() && new File(SOURCE_IMPORT_FOLDER+regulator).exists())
			  {
				  if(!new File(TARGET_EXPECTATION_FOLDER+regulator).exists())
				  {
					  logger.info("copying folder "+new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
					  FileUtil.copyDirectory(new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath(), new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
				  }
				  if(!new File(TARGET_IMPORT_FOLDER+regulator).exists())
				  {
					  logger.info("copying folder "+new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
					  FileUtil.copyDirectory(new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath(), new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
				  }
				  if(new File(TARGET_EXPECTATION_FOLDER+regulator).exists() && new File(TARGET_IMPORT_FOLDER+regulator).exists())
				  {copyCount++;}
			  }
			  
		  }
		  logger.info("copyCount:"+copyCount);
		  if(copyCount==0)
		  {
			  FileUtil.copyDirectory(new File(SOURCE_FOLDER).getAbsolutePath(), new File(TARGET_FOLDER).getAbsolutePath());
		  }
		  logger.info("copy folders(done)");
		  
		  String server_Url=getDBInfo().getApplicationServer_Url();
		  getWebDriverWrapper().navigate().to(server_Url);
		  report(Helper.getTestReportStyle(server_Url, "open test server url ["+server_Url+"]"));
		   HomePage homePage=new HomePage(getWebDriverWrapper(),getTestDataManager());
			if(homePage.isThisPage())
			{
				listPage=homePage.loginAs(getDBInfo().getApplicationServer_UserName(), getDBInfo().getApplicationServer_UserPassword());
			}
			else
			{
				logger.error("error: cannot access to homePage");
				afterClassInTestManager(context);
				Assert.fail("cannot access to homePage");
			}
		  
	  }

	  @AfterClass
	  public void afterClassInTestManager(ITestContext context)  throws Exception{
		  logger.info(getClass().getName()+" afterClass-tearDownTest running!"); 
		  tearDownTest();
		  String detailsLogPath=context.getCurrentXmlTest().getSuite().getName()+"/"+context.getCurrentXmlTest().getName();
		  FileUtil.copyDirectory( new File(SOURCE_LOG_FOLDER+detailsLogPath).getAbsolutePath(), new File(TARGET_LOG_FOLDER+detailsLogPath).getAbsolutePath(),startSuiteTime);
		  FileUtil.copyDirectory( new File(SOURCE_SCREENSHOT_FOLDER).getAbsolutePath(), new File(TARGET_SCREENSHOT_FOLDER).getAbsolutePath(),startSuiteTime);
		  
	  }
	  

  @BeforeMethod
  public void beforeMethod(Method method) throws Exception 
  {
	  //logger.info(" beforeMethod("+method.getName()+") running!"); 
	  
  }
  
  @AfterMethod
  public void afterMethod(ITestResult result) throws Exception {
	ITestContext context=result.getTestContext();
	ITestNGMethod method=result.getMethod();
	 
	logger.info(" afterMethod("+method.getMethodName()+") running!"); 
	String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getMethodName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
	//int count=context.getFailedTests().size()+context.getPassedTests().size()+context.getSkippedTests().size();
    Form form=(Form)result.getParameters()[0];
    ((TestDataManager) getTestDataManager()).setFormsMap(resultFile, form);
	  if(method.getParameterInvocationCount()==method.getCurrentInvocationCount())
	  {
		  List<Form> forms=((TestDataManager) getTestDataManager()).getFormsMap().get(resultFile);
		  if(resultFile.endsWith(".xml"))
		  {
			  Dom4jUtil.writeFormsToMethodXml(forms, TARGET_SCENARIOS_FOLDER+resultFile, "formlist.xsl");
		  }
		  if(resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls"))
		  {
			  String scenarioSheet=context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_SHEET);
			  ExcelUtil.WriteFormsToExcel(forms, TARGET_SCENARIOS_FOLDER+resultFile,scenarioSheet);
			  ExcelUtil.WriteFormsToExcel(forms, TARGET_SCENARIOS_FOLDER+context.getCurrentXmlTest().getSuite().getName()+"_total.xlsx",scenarioSheet);
		  }
	  }
	  StringBuffer identifier=new StringBuffer(resultFile);
	  String scenarioSheet=context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_SHEET);
	  if(scenarioSheet!=null && (resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls")) )
	  {identifier.append("["+scenarioSheet+"]");}
	  identifier.append("-"+getDBInfo().getApplicationServer_Url().toLowerCase()+"-log:"+getLogName());
	  //Dom4jUtil.writeFormsToXml(identifier.toString(),FormsDataProvider.getForms(),TARGET_SCENARIOS_FOLDER+context.getCurrentXmlTest().getSuite().getName()+"_total.xml","formsTotal.xsl");
	  
	  Dom4jUtil.writeFormToXml(identifier.toString(),form,TARGET_SCENARIOS_FOLDER+context.getCurrentXmlTest().getSuite().getName()+"_total.xml","formsTotal.xsl");
  }
 

  public static int changeStringToInt(String str,int defaultInt)
  {
	  int i=defaultInt;
	  try
	  {
		  if(str==null || str.trim().equals(""))
		  {
			  i=defaultInt;
		  }else
		  {
			  i=Integer.parseInt(str);
		  }
		  
	  }catch(Exception e)
	  {
		  i=defaultInt;
	  }
	  finally
	  {
		  if(i<0){i=-1*i;}
	  }
	  return i;
  }

 public void setLogName()
 {
	 String logNameWithoutSuffix=this.getClass().getSimpleName().toLowerCase();
	 String simpleName=logNameWithoutSuffix;
	 int i=1;
	 
	 File logDir=new File(PropHelper.RESULT_FOLDER+PropHelper.LOG_FOLDER);
	 if(logDir!=null && logDir.isDirectory())
	 {
		 while(new File(PropHelper.RESULT_FOLDER+PropHelper.LOG_FOLDER+getSuiteTestSeparator()+"/"+logNameWithoutSuffix+".log").exists())
		 {
			 logNameWithoutSuffix=simpleName+"("+String.valueOf(i)+")";
			 i++;
		 }
	 }
	 
	 this.logName = logNameWithoutSuffix;
 }
 
 public String getLogName() {
		return logName;
	}

 public void addReportLink(String execFunctionFolder,String regulator,String expectationFile,String exec_ExpectationFile)
	{
		String expectationFolder=TARGET_EXPECTATION_FOLDER.replace(TARGET_FOLDER, "../..");// "../../expectation/";
		
		if(execFunctionFolder!=null && regulator!=null && expectationFile!=null )
		{
			
			if(exec_ExpectationFile!=null && !exec_ExpectationFile.trim().equals("") && new File(TARGET_EXPECTATION_FOLDER + regulator+"/"+execFunctionFolder+"/"+exec_ExpectationFile).exists())
			{
				report(Helper.getTestReportStyle(expectationFolder + regulator+"/"+execFunctionFolder+"/"+exec_ExpectationFile, "open actual result ["+exec_ExpectationFile+"]"));
			}
			else
			{
				report(Helper.getTestReportStyle(expectationFolder + regulator+"/"+expectationFile, "open expectation ["+expectationFile+"]"));
			}
		}
		
	}
 @Override
 public void tearDownTest() throws Exception {
     logger.info("teardown test after finishing feature id {}, scenario id {}", getFeatureId(), getScenarioId());
     /*if (recycleTestEnvironment) {
         TestCaseManager.offerTestEnvironment(testEnvironment);
         recycleTestEnvironment = false;
     }*/
     if (getProxyWrapper() != null) {
    	 getProxyWrapper().stop();
     }
     if (getWebDriverWrapper() != null) {
         try {
             closeAlerts();
             getWebDriverWrapper().quit();
         } catch (Exception ignored) {
             logger.error(ignored.getMessage(), ignored);
         }
     }
     getSoftAssertions().assertAll();
 }

 private void closeAlerts() {
     int acceptAlerts = 0;
     while (getWebDriverWrapper().alert().isPresent() && acceptAlerts++ < 10) {
    	 getWebDriverWrapper().alert().accept();
     }
 }

public DBInfo getDBInfo() {
	return dBInfo;
}

public void setDBInfo(DBInfo dBInfo) {
	this.dBInfo = dBInfo;
}

}
