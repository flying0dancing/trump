package com.lombardrisk.pages;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.IComFolder;

public class ForceSubmitCommonDialog extends AbstractPage implements IComFolder{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ForceSubmitCommonDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager) {
		super(webDriverWrapper,testDataManager);
	}
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("fscd.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("fscd.cancel").click();
		loadingDlg();
	}
	
	/**
	 * type comment
	 * @return
	 * @throws Exception
	 */
	public Boolean typeSubmitComment() throws Exception
	{
		Boolean flag=false;
		IWebElementWrapper element=element("fscd.commentTextarea");
		if(element.isDisplayed())
		{
			logger.info("typing \"Force Submit by automation\" on \"Force Submit Dialog\"");
			element.type("Force Submit by automation");
			loadingDlg();
			
			flag=true;
		}
		return flag;
	}
	
	/**
	 * click submit button
	 * @return
	 * @throws Exception
	 */
	public void clickSubmit() throws Exception
	{
		logger.info("clicking submit button on \"Force Submit Dialog\"");
		element("fscd.submit").click();
		loadingDlg();
		waitThat("fscd.title").toBeInvisible();
	}
	
	public void clickDataScheduleSubmit() throws Exception
	{
		logger.info("clicking submit button on \"Force Submit Dialog\"");
		element("fscd.DataSchedulesubmit").click();
		loadingDlg();
	}
}
