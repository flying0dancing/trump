package com.lombardrisk.pages;

import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.DBInfo;

public class JobResultDialog extends AbstractPage {

	public JobResultDialog(IWebDriverWrapper webDriverWrapper) {
		super(webDriverWrapper);
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
			fip=new FormInstancePage(getWebDriverWrapper());
			fip.closeThisPage();
			fip=null;
		}
		if(element("filf.formInstanceListForm").isDisplayed())
		{
			waitThat().timeout(PropHelper.TIMEOUT_INTERVAL*1000);
			listPage=new ListPage(getWebDriverWrapper());
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
