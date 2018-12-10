package com.lombardrisk.pages;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;

public class ExecutionGroupsFormPage extends AbstractPage {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	private DBInfo dBInfo;
	public ExecutionGroupsFormPage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager,Form form) {
		super(webDriverWrapper,testDataManager);
		this.form=form;
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}

	public DBInfo getDBInfo() {
		return dBInfo;
	}
	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("egfp.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("abstract.clickDashboard").click(); //return back to Dashboard
		loadingDlg(null,10);//loadingDlg(8000);
	}
	
	/**
	 * select form, process date, abort on failure, return true if exists
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
		public Boolean selectInfo() throws Exception
		{
			Boolean flag=false;
			String group=form.getRetrieveGroup();
			String processDate=form.getProcessDate();
			form.setName(group); //reset form's group to name
			form.setVersion(""); 
			if(StringUtils.isNotBlank(group))
			{
				IWebElementWrapper aGroupEle=element("egfp.runAGroup",group);
				if(aGroupEle.isPresent() && aGroupEle.isDisplayed()){
					logger.info("find retrieve group:" + group);
					aGroupEle.click();
					loadingDlg(null,5);//loadingDlg();
					waitThat("egrd.title").toBeVisible(); // wait Run Dialog visible
					if(StringUtils.isNotBlank(processDate))
					{
						element("egrd.referenceDate").input(processDate);
						loadingDlg(null,5);//loadingDlg(1000);
						selectDate(processDate);
						flag=true;
					}
					if(StringUtils.isNotBlank(form.getAbortOnFailure()))
					{
						if(form.getAbortOnFailure().equalsIgnoreCase("N") && element("egrd.abortOnFail").isPresent())
						{
							element("egrd.abortOnFail").click();
						}
						if(form.getAbortOnFailure().equalsIgnoreCase("Y") && !element("egrd.abortOnFail").isPresent())
						{
							element("egrd.tickAbortOnFail").click();
						}
						loadingDlg(null,5);//loadingDlg();
					}
				}
			}
			loadingDlg(null,5);//loadingDlg();
			if(!flag)
			{
				IWebElementWrapper element=element("egrd.cancel");
				if(element.isPresent() && element.isDisplayed())
				{
					element("egrd.cancel").click(); // close Run Dialog
				}
				loadingDlg(null,5);//loadingDlg();
				logger.info("can't run retrieve group, cancel it.");
				closeThisPage();
			}
			return flag;
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
			logger.info("click run button");
			element("egrd.ok").click();
			loadingDlg(element("jrd.title"),20); //wait for Job Result Dialog visible
			
			if(element("jrd.title").isPresent() && element("jrd.title").isDisplayed())
			{
				JobResultDialog jrd=new JobResultDialog(getWebDriverWrapper(),getTestDataManager());
				String jobRunType="RetrieveGroupJob";
				String jobName=getDBInfo().getRegulatorPrefix(form.getRegulator())+"|"+form.getRetrieveGroup();
				status=jrd.waitJobResult(jobName, form.getProcessDate(), jobRunType);
				jrd=null;
			}
			
			return status;
		}
}
