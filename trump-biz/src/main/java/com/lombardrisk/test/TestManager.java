package com.lombardrisk.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
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
import org.testng.xml.XmlTest;
import org.yiwan.webcore.test.TestBase;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

import com.lombardrisk.commons.Dom4jUtil;
import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.commons.MailUtil;
import com.lombardrisk.pages.HomePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;


public class TestManager extends TestBase implements IComFolder {
	
	private ListPage listPage;
	private  int indexAppServer;
	private  int indexDBServer;
	private  int indexToolsetDBServer;
	private static final long startSuiteTime=System.currentTimeMillis();
	private String logName;
	private DBInfo dBInfo;
	private static long totalRun=0;
	private static long totalPass=0;
	private static long totalSkip=0;
	private static long totalFail=0;
	private static long totalError=0;
	public ListPage getListPage()
	{
		return listPage;
	}
	
	 @BeforeSuite
	  public void beforeSuite() {
		 report("suite start.");
		  try
		  {
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
			  FileUtil.copyFileToDirectory(SOURCE_SCENARIOS_FOLDER,"xsl;css", TARGET_SCENARIOS_FOLDER);
			  
			  
		  }catch(Exception e)
		  {
			  logger.error(e.getMessage());
			  Assert.fail("cannot create result folder or sub-folders."+TARGET_FOLDER);
		  }
		 
	  }
	 @AfterSuite
	  public void afterSuite(XmlTest xmlTest) { 
		 report("suite end. Totally used[seconds]: "+(System.currentTimeMillis()-startSuiteTime)/1000.00F+"\n");
		 String content="Tests run: "+totalRun+", Pass: "+totalPass+", Failures: "+totalFail+", Errors: "+totalError+", Skipped:"+totalSkip+", Time elapsed:"+(System.currentTimeMillis()-startSuiteTime)/60000.00F+"min";
		 MailUtil.sendARResultMail(TARGET_FOLDER,xmlTest.getSuite().getFileName(),content);
	  }
	
	 @BeforeTest
	  public void beforeTest(ITestContext context) {
		 logger.info(context.getName() + " start testing!");
	  }
	  
	  @AfterTest
	  public void afterTest(ITestContext context) throws Exception {
		 logger.info(context.getName() + " finish testing!");
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
		  TestCaseManager.offerTestEnvironment(getTestEnvironment());
		  super.setTestDataManager(new TestDataManager(indexAppServer, indexDBServer, indexToolsetDBServer));
		  setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
		  logger.info("Database Language:"+getDBInfo().getLanguage(getDBInfo().getApplicationServer_UserName()));
		  
		  List<String> regulators=getDBInfo().getRegulatorDescription();
		  try
		  {
			  
			  if(ICCB_UPDATESOURCE)
			  {
				  System.out.println("update source with -DupdateSource.");
				  logger.info("update source with -DupdateSource.");
				  int copyCount=0;
				  logger.info("copy folders(start)");
				  for(String regulator:regulators)
				  {
					  logger.info("try to copying regulator folder \""+regulator+"\" to result folder");
					  if(new File(SOURCE_EXPECTATION_FOLDER+regulator).exists() && new File(SOURCE_IMPORT_FOLDER+regulator).exists())
					  {
						  logger.info("copying folder "+new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
						  FileUtil.copyDirectory(new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath(), new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
						  logger.info("copying folder "+new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
						  FileUtil.copyDirectory(new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath(), new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
						 
						  if(new File(TARGET_EXPECTATION_FOLDER+regulator).exists() && new File(TARGET_IMPORT_FOLDER+regulator).exists())
						  {copyCount++;}
					  }
					  
				  }
				  if(copyCount==0)
				  {
					  FileUtil.copyDirectory(new File(SOURCE_FOLDER).getAbsolutePath(), new File(TARGET_FOLDER).getAbsolutePath());
				  }
				  logger.info("copy folders(done)");
			  }else
			  {
				  System.out.println("using existed(old) source. use -DupdateSource if you want to update source.");
				  logger.info("using existed(old) source. use -DupdateSource if you want to update source.");
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
				  if(copyCount==0)
				  {
					  FileUtil.copyDirectory(new File(SOURCE_FOLDER).getAbsolutePath(), new File(TARGET_FOLDER).getAbsolutePath());
				  }
				  logger.info("copy folders(done)");
			  }
		  }catch(Exception e){}
		  
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
	  synchronized(this)
	  {
		  ITestContext context=result.getTestContext();
			ITestNGMethod method=result.getMethod();
			logger.info(" afterMethod("+method.getMethodName()+") running!"); 
			String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getMethodName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
			//int count=context.getFailedTests().size()+context.getPassedTests().size()+context.getSkippedTests().size();
		    Form form=(Form)result.getParameters()[0];
		    totalRun++;
		    String formStatus=form.getExecutionStatus().toLowerCase();
		    if(formStatus.startsWith("pass"))
		    {
		    	totalPass++;
		    }else if(formStatus.startsWith("skip"))
		    {
		    	totalSkip++;
		    }else if(formStatus.startsWith("fail"))
		    {
		    	totalFail++;
		    }else if(formStatus.startsWith("error"))
		    {
		    	totalError++;
		    }
		    
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

public static long getTotalRun() {
return totalRun;
}

public static long getTotalPass() {
return totalPass;
}

public static long getTotalSkip() {
return totalSkip;
}

public static long getTotalFail() {
return totalFail;
}

public static long getTotalError() {
return totalError;
}



}
