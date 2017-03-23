package com.lombardrisk.pages;

import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;

public class RetrieveDialog extends AbstractPage implements IReturnDialog{
	
	private Form form;
	public RetrieveDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager,Form form) {
		super(webDriverWrapper,testDataManager);
		this.form=form;
	}

	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("fird.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("fird.closeDialog").click();
		loadingDlg();
	}
	
	/**
	 * select regulator, entity, process date, form with version, return true if exists
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
		public Boolean selectInfo() throws Exception
		{
			Boolean flag=false;
			String group=form.getEntity();
			String formAndVersion=form.getName()+" "+form.getVersion();
			String processDate=form.getProcessDate();
			
			waitThat("fird.title").toBeVisible();
			if(group != null)
			{
				logger.info("select entity:" + group);
				flag=selectIt(element("fird.selectGroup"),group);
				form.setEntity(getRealText(element("fird.selectGroup"),group));
				if (processDate != null && flag)
				{
					logger.info("select process date:" + processDate);
					element("fird.selectProcessDate").input(processDate);
					loadingDlg();
					selectDate(processDate);
					if(formAndVersion != null && flag)
					{
						logger.info("select form:" + formAndVersion);
						flag=selectIt(element("fird.selectForm"),formAndVersion);
						String tmp=getRealText(element("fird.selectForm"),formAndVersion).trim();
						form.setName(tmp.substring(0, tmp.lastIndexOf(" ")));
						if(flag && element("fird.messages").isPresent() && element("fird.messages").isDisplayed())
						{
							logger.info("error Messages:"+getAllInnerText(element("fird.messagesDetail")));
							flag=false;
						}
					}
				}
				
			}
			
			loadingDlg();
			if(!flag)
			{
				closeThisPage();
				logger.info("can't do retrieve, cancel it.");
			}
			return flag;
		}
		
		/**
		 * click ok button and if retrieve successfully return FormInstancePage, others return null.
		 * @author kun shen
		 * @return
		 * @throws Exception
		 */
		public FormInstancePage clickOK() throws Exception
		{
			ListPage listPage=null;
			FormInstancePage fip=null;
			String status=doRetrieve();
			
			if(status.equalsIgnoreCase("pass"))
			{
				listPage=new ListPage(getWebDriverWrapper(),getTestDataManager());
				listPage.selectFormInfo(form);
				int id=listPage.search(form);
				if(id>-1)
				{
					fip=listPage.openFormInstance(form);
					if(!fip.isThisPage())
					{
						fip=null;
					}
				}
			}
			logger.info("retrieve status:"+status);
			return fip;
		}

		/**
		 * search job status(pass,fail:...,error:job timeout,error:job null,error:job cannot do retrieve)
		 * @return
		 * @throws Exception
		 */
		public String doRetrieve() throws Exception
		{
			String status=null;
			//ListPage listPage=null;
			logger.info("click retrieve button");
			element("fird.ok").click();
			loadingDlg();
			//error messages display
			if(element("fird.messages").isDisplayed())
			{
				status="error:job cannot do retrieve. "+getAllInnerText(element("fird.messagesDetail"));
				element("fird.cancel").click();
				loadingDlg();
			}else
			{
				loadingDlg();
				JobResultDialog jrd=new JobResultDialog(getWebDriverWrapper(),getTestDataManager());
				//String jobStartTimeLabel=jrd.jobStartTime();
				
				String jobRunType="RetrieveJob";
				String jobName=DBInfo.getRegulatorPrefix(form.getRegulator())+"|"+form.getEntity()+"|"+form.getName()+"|"+form.getVersion().substring(1);
				
				/*listPage=new ListPage(getWebDriverWrapper());
				JobManagerPage jobManagerPage=listPage.clickJobManager();
				if(jobManagerPage!=null)
				{
					status=jobManagerPage.search(name,form.getProcessDate(),runType,jobStartTimeLabel);
					long jobStartTime=System.currentTimeMillis();
					while(status==null || !(status.startsWith("FAILURE") || status.equalsIgnoreCase("SUCCESS")))
					{
						refreshPage();
						loadingDlg();
						status=jobManagerPage.search(name,form.getProcessDate(),runType,jobStartTimeLabel);
						long jobEndTime=System.currentTimeMillis();
						if(jobEndTime-jobStartTime>600000)//wait 10min
						{
							status="error:job timeout";
							break;
						}
					}
					logger.info("job status:"+status);
					jobManagerPage.closeThisPage();
				}*/
				
				status=jrd.waitJobResult(jobName, form.getProcessDate(), jobRunType);
				jrd=null;
			}
			
			return status;
		}
}
