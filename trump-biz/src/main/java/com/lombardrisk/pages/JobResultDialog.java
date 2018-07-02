package com.lombardrisk.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

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
			loadingDlg();
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
		long timeout=600000;//10 min
		long refreshWaittime=30000; //0.5 min
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
			loadingDlg(element("filf.jobManager"),20);
		}
		if(element("egfp.title").isPresent() && element("egfp.title").isDisplayed())
		{
			timeout=1200000;//20 min
			refreshWaittime=60000; //1 min
		}
		IWebElementWrapper element=element("filf.jobManager");  
		if(element.isPresent() && element.isDisplayed())
		{
			element.click();
			loadingDlg(element("fjmlf.backToDashBoard"),20);
			JobManagerPage jobManagerPage=new JobManagerPage(getWebDriverWrapper(),getTestDataManager());
			if(jobManagerPage!=null && jobStartTimeLabel!=null)
			{
				status=jobManagerPage.search(jobName,jobReferenceDate,jobStartTimeLabel,refreshWaittime*2);
				long jobStartTime=System.currentTimeMillis();
				while(status==null || !(status.toUpperCase().startsWith("FAILURE") || status.equalsIgnoreCase("SUCCESS")))
				{
					refreshPage();
					loadingDlg();
					status=jobManagerPage.search(jobName,jobReferenceDate,jobStartTimeLabel,refreshWaittime);
					long jobEndTime=System.currentTimeMillis();
					if(jobEndTime-jobStartTime>timeout)
					{
						status="error:job timeout";
						break;
					}
				}
				logger.info("job status:"+status);
				jobManagerPage.closeThisPage();
			}
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
