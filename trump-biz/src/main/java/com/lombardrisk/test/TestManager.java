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
import org.yiwan.webcore.test.pojo.TestEnvironment;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

import com.lombardrisk.commons.Dom4jUtil;
import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.pages.HomePage;
import com.lombardrisk.pages.ListPage;
//import org.assertj.core.api.*;
//import static org.assertj.core.api.Assertions.*;

public class TestManager extends TestBase implements IComFolder {
	
	private static TestEnvironment testEnv;
	private static ListPage listPage;
	private static int indexAppServer;
	private static int indexDBServer;
	private static int indexToolsetDBServer;
	private static long startSuiteTime;

	public static TestEnvironment getTestEnv()
	{
		return testEnv;
	}
	
	public static ListPage getListPage()
	{
		return listPage;
	}
	
	public static int getIndexAppServer()
	{return indexAppServer;}

	public static int getIndexDBServer()
	{return indexDBServer;}
	
	public static int getIndexToolsetDBServer()
	{return indexToolsetDBServer;}
	
	 @BeforeSuite
	  public void beforeSuite() {
		  startSuiteTime=System.currentTimeMillis();
		  System.out.println(" beforeSuite running!");
		  Reporter.log(" beforeSuite running~~~~<br>");
		  //delete older test target folder, and create news.
		  try
		  {
			  if(FileUtil.checkDirectory(TARGET_FOLDER))
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
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,".css", TARGET_SCENARIOS_FOLDER);
			  
		  }catch(Exception e)
		  {
			  logger.error(e.getMessage());
			  Assert.fail("cannot delete or create result folder."+TARGET_FOLDER);
		  }
		 
	  }
	 @AfterSuite
	  public void afterSuite() {
		  System.out.println(" afterSuite running!"); 
		  Reporter.log(" afterSuite running~~~~<br>");
		  FileUtil.copyDirectory( new File(SOURCE_LOG_FOLDER).getAbsolutePath(), new File(TARGET_LOG_FOLDER).getAbsolutePath(),startSuiteTime);
		  
	  }
	
	 @BeforeTest
	 @Parameters({"indexAppServers", "indexDBServers", "indexToolsetDBServers"})
	  public void beforeTest( @Optional String indexAppServers, @Optional String indexDBServers, @Optional String indexToolsetDBServers) {
		  System.out.println(getClass().getName()+" beforeTest running!"); 
		  Reporter.log(getClass().getName() + " beforeTest running~~~~<br>");
		  indexAppServer=changeStringToInt(indexAppServers,0);
		  indexDBServer=changeStringToInt(indexDBServers,0);
		  indexToolsetDBServer=changeStringToInt(indexToolsetDBServers,0);
	  }
	  
	  @AfterTest
	  public void afterTest() throws Exception {
		  System.out.println(getClass().getName()+" afterTest running!"); 
	  }
	  
	  @BeforeClass(dependsOnMethods="beforeClass")
	  public void beforeClassInTestManager() throws Exception {
		  System.out.println(getClass().getName()+" beforeClass running!"); 
		  System.out.println(getClass().getName()+" setUpTest running!");
		  
		  setScenarioId(getLogName());
		  
		  setUpTest();
		  testEnv=super.getTestEnvironment();
		  DBInfo.setDBInfo();
		  List<String> regulators=DBInfo.getRegulatorDescription();
		  int copyCount=0;
		  logger.info("copy folders...");
		  for(String regulator:regulators)
		  {
			  logger.info("copy regulator folder \""+regulator+"\" to result folder");
			  if(new File(SOURCE_EXPECTATION_FOLDER+regulator).exists() && new File(SOURCE_IMPORT_FOLDER+regulator).exists())
			  {
				  if(!new File(TARGET_EXPECTATION_FOLDER+regulator).exists())
				  {
					  logger.info("copy folder "+new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
					  FileUtil.copyDirectory(new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath(), new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
				  }
				  if(!new File(TARGET_IMPORT_FOLDER+regulator).exists())
				  {
					  logger.info("copy folder "+new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
					  FileUtil.copyDirectory(new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath(), new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
				  }
				  copyCount++;
			  }
			  
		  }
		  if(copyCount==0)
		  {
			  FileUtil.copyDirectory(new File(SOURCE_FOLDER).getAbsolutePath(), new File(TARGET_FOLDER).getAbsolutePath());
		  }
		  logger.info("copy folders(done)");
		  Reporter.log("Database Language:"+DBInfo.getLanguage());
		  
		  getWebDriverWrapper().navigate().to(DBInfo.getApplicationServer_Url());
		  report(Helper.getTestReportStyle(DBInfo.getApplicationServer_Url(), "open test server url"));
		   HomePage homePage=new HomePage(getWebDriverWrapper());
			if(homePage.isThisPage())
			{
				listPage=homePage.loginAs(DBInfo.getApplicationServer_UserName(), DBInfo.getApplicationServer_Password());
			}
			else
			{
				Assert.fail("cannot access to homePage");
			}
		  
	  }

	  @AfterClass
	  public void afterClassInTestManager()  throws Exception{
		  logger.info(getClass().getName()+" afterClass running!"); 
		  logger.info(getClass().getName()+" tearDownTest running!"); 
		  tearDownTest();
	  }
	  

  @BeforeMethod
  public void beforeMethod(Method method) throws Exception {
	  logger.info(System.getProperty("line.separator")+getClass().getName()+" beforeMethod("+method.getName()+") running!"); 

	 /* getWebDriverWrapper().navigate().to(DBInfo.getApplicationServer_Url());
	  report(Helper.getTestReportStyle(DBInfo.getApplicationServer_Url(), "open test server url"));*/
	  /*listPage=new ListPage(getWebDriverWrapper());
	  if(!listPage.isThisPage())
	  {
		  HomePage homePage=new HomePage(getWebDriverWrapper());
			if(homePage.isThisPage())
			{
				listPage=homePage.loginAs(DBInfo.getApplicationServer_UserName(), DBInfo.getApplicationServer_Password());
			}
			else
			{
				Assert.fail("cannot access to homePage");
			}
	  }*/
	 
	  
  }
	  @AfterMethod
	  public void afterMethod(ITestResult result) throws Exception {
		ITestContext context=result.getTestContext();
		ITestNGMethod method=result.getMethod();
		logger.info(getClass().getName()+" afterMethod("+method.getMethodName()+") running!"); 
		String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getMethodName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
		//int count=context.getFailedTests().size()+context.getPassedTests().size()+context.getSkippedTests().size();

		  if(method.getParameterInvocationCount()==method.getCurrentInvocationCount())
		  {
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
		  
		 
	  }
 

  public int changeStringToInt(String str,int defaultInt)
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

 public String getLogName()
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
	 
	 return logNameWithoutSuffix;
 }
 
 public void addReportLink(String execFunctionFolder,String regulator,String expectationFile,String exec_ExpectationFile)
	{
		String expectationFolder="../../expectation/";
		if(execFunctionFolder!=null && regulator!=null && expectationFile!=null )
		{
			
			if(exec_ExpectationFile!=null && !exec_ExpectationFile.trim().equals("") && new File(TARGET_EXPECTATION_FOLDER + regulator+"/"+execFunctionFolder+"/"+exec_ExpectationFile).exists())
			{
				report(Helper.getTestReportStyle(expectationFolder + regulator+"/"+execFunctionFolder+"/"+exec_ExpectationFile, "open expected result file"));
			}
			else
			{
				report(Helper.getTestReportStyle(expectationFolder + regulator+"/"+expectationFile, "open expected file"));
			}
		}
		
	}
 


}
