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

	public Boolean selectInfo() throws Exception {
		IWebElementWrapper aTitleEle=element("mrf.title");
		if(aTitleEle.isPresent() && aTitleEle.isDisplayed()){
			return selectInfo2019();
		}else{
			return selectInfoBefore2019();
		}
	}
	/**
	 * select form, process date, abort on failure, return true if exists
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
		public Boolean selectInfoBefore2019() throws Exception
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

	public Boolean selectInfo2019() throws Exception
	{
		Boolean flag=false;
		String group=form.getRetrieveGroup();
		String processDate=form.getProcessDate();
		form.setName(group); //reset form's group to name
		form.setVersion("");
		if(StringUtils.isNotBlank(group))
		{
			flag=selectIt(element("mrf.selectGroup"),group);
			if(flag && StringUtils.isNotBlank(processDate))
			{
				element("mrf.referenceDate").input(processDate);
				loadingDlg(null,5);//loadingDlg(1000);
				selectDate(processDate);
				flag=true;
			}
			if(StringUtils.isNotBlank(form.getAbortOnFailure()))
			{
				if(form.getAbortOnFailure().equalsIgnoreCase("N") && element("mrf.abortOnFail").isPresent())
				{
					element("mrf.abortOnFail").click();
				}
				if(form.getAbortOnFailure().equalsIgnoreCase("Y") && !element("mrf.abortOnFail").isPresent())
				{
					element("mrf.tickAbortOnFail").click();
				}
				loadingDlg(null,5);//loadingDlg();
			}
		}
		loadingDlg(null,5);//loadingDlg();
		if(!flag)
		{
			IWebElementWrapper elt=element("mrf.cancel");
			if(elt.isPresent() && elt.isDisplayed())
			{
				elt.click(); // close Run Dialog
			}
			loadingDlg(null,5);//loadingDlg();
			logger.info("can't run retrieve group, cancel it.");
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
		IWebElementWrapper aTitleEle=element("mrf.title");
		if(aTitleEle.isPresent() && aTitleEle.isDisplayed()){
			element("mrf.ok").click();
		}else{
			element("egrd.ok").click();
		}
		loadingDlg(element("jrd.title"),200); //wait for Job Result Dialog visible

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
