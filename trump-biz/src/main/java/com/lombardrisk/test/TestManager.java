package com.lombardrisk.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.annotations.Parameters;
import org.testng.xml.*;
import org.yiwan.webcore.test.TestBase;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;

import com.google.common.base.Strings;
import com.lombardrisk.commons.*;
import com.lombardrisk.pages.HomePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;


public class TestManager extends TestBase implements IComFolder {
	private final Logger logger = LoggerFactory.getLogger(TestManager.class);
	private ListPage listPage;
	private  int indexAppServer;
	private  int indexDBServer;
	private  int indexToolsetDBServer;
	private static final long startSuiteTime=System.currentTimeMillis();
	private String logName;
	private DBInfo dBInfo;
	private static long totalPass=0;
	private static long totalSkip=0;
	private static long totalFail=0;
	private static Map<String,String> rerunTestMap=new LinkedHashMap<String,String>();
	private List<String> dateFormats=Arrays.asList("zh_CN","en_GB","en_US");
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
	  public void afterSuite(XmlTest xmlTest,ITestContext context) throws IOException { 
		 ///Set<ITestResult> failTests=context.getFailedTests().getAllResults();
		 //Set<ITestResult> passTests=context.getPassedTests().getAllResults();
		 //Set<ITestResult> skipTests=context.getSkippedTests().getAllResults();
		 float totalTime=(System.currentTimeMillis()-startSuiteTime)/60000.00F;
		 report("suite end. Totally used[minutes]: "+totalTime+"\n");
		 //String content="Tests run: "+(totalPass+totalFail+totalSkip)+", Pass: "+totalPass+", Failures: "+totalFail+", Skipped:"+totalSkip+", Time elapsed:"+(System.currentTimeMillis()-startSuiteTime)/60000.00F+"min";
		/* if(!ICCB_RERUN ||(ICCB_RERUN && totalFail==0 && totalSkip==0 && totalPass!=0))
		 {}*/
		 MailUtil.sendARResultMail(TARGET_FOLDER,xmlTest.getSuite().getFileName(),totalPass,totalFail,totalSkip,totalTime,ICCB_RERUN);
		 String failXml=TARGET_SCENARIOS_FOLDER+xmlTest.getSuite().getName()+"_fail.xml";
		 
		 File failedtestsXml=new File(failXml);
		 if(!failedtestsXml.exists() && (totalFail>0 || totalSkip>0))
		 {
			 XmlSuite rerunSuite=new XmlSuite();
			 rerunSuite.setFileName(failXml);
			 XmlSuite currentSuite=context.getCurrentXmlTest().getSuite();
			 String suiteName=currentSuite.getName();
			 rerunSuite.setName(suiteName);
			 rerunSuite.setVerbose(currentSuite.getVerbose());
			 rerunSuite.setThreadCount(currentSuite.getThreadCount());
			 rerunSuite.setParallel(currentSuite.getParallel());
			 rerunSuite.setParameters(currentSuite.getAllParameters());
			 
			 /*if(failedtestsXml.exists())
			 {
				 FileInputStream fin=new FileInputStream(failedtestsXml);
				 rerunSuite=new SuiteXmlParser().parse(failXml, fin, true);
			 }*/
			 
			 int rerunTestAcc=1;
			 
			 Iterator<Entry<String, String>> iterM=rerunTestMap.entrySet().iterator();
			 while(iterM.hasNext())
			 {
				 Map.Entry<String, String> entryM=(Map.Entry<String, String>)iterM.next();
				 String iterMKey=entryM.getKey();
				 String iterMValue=entryM.getValue();
				 String[] iterMKeyArr=iterMKey.split("\\+");
				 String[] iterMValueArr=iterMValue.split("\\+");
				 String iterMClass=iterMValueArr[0];
				 String iterMMethod=iterMValueArr[1];
				 String iterMParameterSCENARIOS_SHEET=iterMValueArr[2];
				 String iterMTestName=iterMKeyArr[1];
				 
				 XmlTest rerunTest=new XmlTest();
				 List<XmlClass> rerunClasses=new ArrayList<XmlClass>();
				 XmlClass rerunClass=new XmlClass(iterMClass);
				 List<XmlInclude> rerunXmlIncludes=new ArrayList<XmlInclude>();
				 rerunXmlIncludes.add(new XmlInclude(iterMMethod));
				 rerunClass.setIncludedMethods(rerunXmlIncludes);
				 rerunClasses.add(rerunClass);
				 rerunTest.setClasses(rerunClasses);
				 rerunTest.addParameter(PARAMETER_SCENARIOS_NAME, iterMKey);
				 rerunTest.addParameter(PARAMETER_SCENARIOS_SHEET, iterMParameterSCENARIOS_SHEET);
				 rerunTest.setName(iterMTestName);
				 for(XmlTest testTmp:rerunSuite.getTests())
				 {
					 if(iterMTestName.equalsIgnoreCase(testTmp.getName()) && iterMKey.equalsIgnoreCase(testTmp.getParameter(PARAMETER_SCENARIOS_NAME)))
					 {
						 rerunTest.setName(testTmp.getName()+rerunTestAcc);
						 rerunTestAcc++;
					 }
				 }
				 rerunSuite.addTest(rerunTest);
			 }
			 FileUtil.writeContentToEmptyFile(new File(failXml),  rerunSuite.toXml());
		 }
		 //reset it for next suite
		 totalPass=0;
		 totalSkip=0;
		 totalFail=0;
		 rerunTestMap=new LinkedHashMap<String,String>();
	  }
	
	 @BeforeTest
	  public void beforeTest(ITestContext context) {
		 logger.info(context.getName() + " start testing!");
	  }
	  
	  @AfterTest
	  public void afterTest(ITestContext context) throws Exception {
		 logger.info(context.getName() + " finish testing!");
		 /*if(passTests==null)
		 {
			 passTests=context.getPassedTests().getAllResults();
		 }else
		 {
			 passTests.addAll(context.getPassedTests().getAllResults());
		 }
		 if(failTests==null)
		 {
			 failTests=context.getFailedTests().getAllResults();
		 }else
		 {
			 failTests.addAll(context.getFailedTests().getAllResults());
		 }
		 if(skipTests==null)
		 {
			 skipTests=context.getSkippedTests().getAllResults();
		 }else
		 {
			 skipTests.addAll(context.getSkippedTests().getAllResults());
		 }*/
	  }
	  
	  @BeforeClass(dependsOnMethods="beforeClass")
	  @Parameters({"indexAppServers", "indexDBServers", "indexToolsetDBServers", "selectLanguage"})
	  public void beforeClassInTestManager(ITestContext context, @Optional String indexAppServers, @Optional String indexDBServers, @Optional String indexToolsetDBServers,@Optional String dateFormat) throws Exception {
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
		  if(StringUtils.isNotBlank(dateFormat)){
				dateFormat=dateFormat.trim();
				String prop_dateFormat=PropHelper.getProperty("Regional.language");
				if(!prop_dateFormat.equals(dateFormat) && dateFormats.contains(dateFormat)){
					System.setProperty("Regional.language",dateFormat);
				}
			}
		  getDBInfo().resetDeActivateDate();
		  logger.info("reset DeActivateDate to null in database's CFG_RPT_Rets table.");
		  List<String> regulators=getDBInfo().getRegulatorDescription();
		  try
		  {
			  
			  if(ICCB_UPDATESOURCE)
			  {
				  System.out.println("update source with -DupdateSource.");
				  logger.info("update source with -DupdateSource.");
			  }else
			  {
				  System.out.println("using existed(old) source. use -DupdateSource if you want to update source.");
				  logger.info("using existed(old) source. use -DupdateSource if you want to update source.");
			  }
			  int copyCount=0;
			  logger.info("copy folders(start)");
			  for(String regulator:regulators)
			  {
				  logger.info("try to copying regulator folder \""+regulator+"\" to result folder");
				  if(new File(SOURCE_EXPECTATION_FOLDER+regulator).exists() && new File(SOURCE_IMPORT_FOLDER+regulator).exists())
				  {
					 if(ICCB_UPDATESOURCE)
					 {
						 logger.info("copying folder "+new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
						 FileUtil.copyDirectory(new File(SOURCE_EXPECTATION_FOLDER+regulator).getAbsolutePath(), new File(TARGET_EXPECTATION_FOLDER+regulator).getAbsolutePath());
						 logger.info("copying folder "+new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath()+" to "+new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
						 FileUtil.copyDirectory(new File(SOURCE_IMPORT_FOLDER+regulator).getAbsolutePath(), new File(TARGET_IMPORT_FOLDER+regulator).getAbsolutePath());
						 
					 }else
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
			  //debug ci
			  logger.info(System.getProperty("server.info"));
			  logger.info(System.getProperty("test.source"));
			  logger.info(System.getProperty("test.scenarios"));
			  logger.info(System.getProperty("test.target"));
			  logger.info(System.getProperty("mail.host"));
			  logger.info(System.getProperty("mail.port"));
			  logger.info(System.getProperty("mail.sender.address"));
			  logger.info(System.getProperty("mail.sender.password"));
			  logger.info(System.getProperty("mail.receiver.to.list"));
			  logger.info(System.getProperty("mail.receiver.cc.list"));
			  logger.info(System.getProperty("mail.subject.prefix"));
			  logger.info(System.getProperty("xmlFileName"));
			  logger.info(System.getProperty("reportOutput"));
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
		  String detailsLogPath=context.getCurrentXmlTest().getSuite().getName()+"/"+context.getCurrentXmlTest().getName()+"/";
		  FileUtil.copyToNewFile(SOURCE_LOG_FOLDER+detailsLogPath,TARGET_LOG_FOLDER+detailsLogPath,getLogName()+".log");
		  //FileUtil.copyDirectory( new File(SOURCE_LOG_FOLDER+detailsLogPath).getAbsolutePath(), new File(TARGET_LOG_FOLDER+detailsLogPath).getAbsolutePath(),startSuiteTime,System.currentTimeMillis());
		  FileUtil.copyDirectory( new File(SOURCE_SCREENSHOT_FOLDER).getAbsolutePath(), new File(TARGET_SCREENSHOT_FOLDER).getAbsolutePath(),startSuiteTime,System.currentTimeMillis());

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
			String scenarioSheet=context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_SHEET);
			String resultFile=context.getCurrentXmlTest().getSuite().getName()+"+"+context.getCurrentXmlTest().getName()+"+"+getClass().getSimpleName()+"["+method.getMethodName()+"]+"+context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
			if(ICCB_RERUN)
			{
				resultFile=context.getCurrentXmlTest().getParameter(PARAMETER_SCENARIOS_NAME).trim();
			}
			
			//int count=context.getFailedTests().size()+context.getPassedTests().size()+context.getSkippedTests().size();
		    Form form=(Form)result.getParameters()[0];
		    /*totalRun++;*/
		    int resultStatus=result.getStatus();
		    logger.info("resultStatus:"+resultStatus);
		    if(resultStatus!=ITestResult.SUCCESS)
		    {
		    	if(rerunTestMap.size()==0 || !rerunTestMap.containsKey(resultFile))
		    	{
		    		if(resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls"))
		    		{
		    			rerunTestMap.put(resultFile, getClass().getName()+"+"+method.getMethodName()+"+"+scenarioSheet);
		    		}else
		    		{
		    			rerunTestMap.put(resultFile, getClass().getName()+"+"+method.getMethodName()+"+");
		    		}
		    		
		    	}
		    	if(resultStatus==ITestResult.FAILURE)
		    	{totalFail++;}
		    	else if(resultStatus==ITestResult.SKIP)
		    	{
		    		totalSkip++;
		    		form.setExecutionStatus("skip");
		    	}
		    }else
		    {
		    	totalPass++;
		    }
		    /*String formStatus=form.getExecutionStatus().toLowerCase();
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
		    }*/
		    
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
					  ExcelUtil.writeObjectsToExcel(forms, TARGET_SCENARIOS_FOLDER+resultFile,scenarioSheet,ICCB_RERUN);
					  ExcelUtil.writeObjectsToExcel(forms, TARGET_SCENARIOS_FOLDER+context.getCurrentXmlTest().getSuite().getName()+"_total.xlsx",scenarioSheet,ICCB_RERUN);
				  }
			  }
			  StringBuffer identifier=new StringBuffer(resultFile);
			  if(scenarioSheet!=null && (resultFile.endsWith(".xlsx")||resultFile.endsWith(".xls")) )
			  {identifier.append("["+scenarioSheet+"]");}
			  identifier.append("-"+getDBInfo().getApplicationServer_Url().toLowerCase()+"-log:");
			  //identifier.append("-"+getDBInfo().getApplicationServer_Url().toLowerCase()+"-log:"+getLogName());
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
return totalPass+totalSkip+totalFail;
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

/**
 * run test case or not
 * @param status
 * @return
 */
public Boolean runIt(String status)
{
  Boolean flag=false;//don't run it 
  if(Strings.isNullOrEmpty(status))
  {
	  return true;
  }
  if(ICCB_RERUN)
  {
	if(ICC_RERUNCONTENT.equals("all")) 
	{
		if(!status.startsWith("pass"))
		{
			flag=true;
		}
	}else
	{
		if(status.startsWith(ICC_RERUNCONTENT))
		{flag=true;}
	}
  }else
  {
	  flag=true;
  }
  return flag;
}

}
