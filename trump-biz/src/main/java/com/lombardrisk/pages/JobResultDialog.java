package com.lombardrisk.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

public class JobResultDialog extends AbstractPage {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	public JobResultDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager) {
		super(webDriverWrapper,testDataManager);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("jrd.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("jrd.closeDialog").click();
		loadingDlg();
	}
	
	public String jobStartTime() throws Exception
	{
		String jobStartTimeLabel=null;
		if(element("jrd.title").isDisplayed())
		{
			if(element("jrd.startedTimeLabel").isPresent())
			{
				jobStartTimeLabel=element("jrd.startedTimeLabel").getInnerText();
				if(jobStartTimeLabel!=null && !jobStartTimeLabel.trim().equals(""))
				{
					jobStartTimeLabel=jobStartTimeLabel.trim().replace("Started: ", "");
					logger.info("job started:"+jobStartTimeLabel);
					
				}
			}else
			{
				logger.info("job is already running");
			}
			/*if(jobStartTimeLabel==null)
			{
				element("jrd.ok").click();
				closeThisPage();
			}else
			{
				element("jrd.ok").click();
				waitThat("jrd.title").toBeInvisible();
				loadingDlg();
			}*/
			element("jrd.ok").click();
			waitThat("jrd.title").toBeInvisible();
			loadingDlg();
			
		}
		return jobStartTimeLabel;
	}
	
	/**
	 * search job status(pass,fail:...,error:job timeout,error:job null)
	 * @return
	 * @throws Exception
	 */
	public String waitJobResult(String jobName,String jobReferenceDate, String jobRunType) throws Exception
	{
		String status=null;
		FormInstancePage fip=null;
		ListPage listPage=null;
		loadingDlg();
		logger.info("job name["+jobName+"], type["+jobRunType+"]");
		String jobStartTimeLabel=jobStartTime();
		if(element("fipf.formInstTitleLabels").isDisplayed())
		{
			ForceSubmitCommonDialog forceSubmit=new ForceSubmitCommonDialog(getWebDriverWrapper(),getTestDataManager());//new add on 2017.10.31 for ar1.15.6-b1293 
			if(forceSubmit.isThisPage())
			{
				forceSubmit.closeThisPage();
			}
			loadingDlg();
			fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager());
			fip.unlockForm();//new add on 2017.10.31 for ar1.15.6-b1293 //TODO
			fip.closeThisPage();
			fip=null;
		}
		if(element("filf.formInstanceListForm").isDisplayed())
		{
			waitThat().timeout(10000);
			listPage=new ListPage(getWebDriverWrapper(),getTestDataManager());
			JobManagerPage jobManagerPage=listPage.clickJobManager();
			if(jobManagerPage!=null && jobStartTimeLabel!=null)
			{
				status=jobManagerPage.search(jobName,jobReferenceDate,jobRunType,jobStartTimeLabel);
				long jobStartTime=System.currentTimeMillis();
				while(status==null || !(status.startsWith("FAILURE") || status.equalsIgnoreCase("SUCCESS")))
				{
					refreshPage();
					loadingDlg();
					status=jobManagerPage.search(jobName,jobReferenceDate,jobRunType,jobStartTimeLabel);
					long jobEndTime=System.currentTimeMillis();
					if(jobEndTime-jobStartTime>600000)//wait 10min
					{
						status="error:job timeout";
						break;
					}
				}
				logger.info("job status:"+status);
				jobManagerPage.closeThisPage();
			}
			listPage=null;
		}
		
		if(status==null)
		{
			status="error:job null";
		}else if(status.equalsIgnoreCase("SUCCESS"))
		{
			status="pass";
		}else if(status.startsWith("FAILURE"))
		{
			status=status.replace("FAILURE", "fail");
		}

		return status;
	}
	
	
	
}
