package com.lombardrisk.pages;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.IComFolder;

public class ForceDirectSubmitDialog extends AbstractPage implements IComFolder{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ForceDirectSubmitDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager) {
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
		flag=element("fdsd.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("fdsd.cancel").click();
		loadingDlg(null,5);//loadingDlg();
	}
	
	/**
	 * type comment
	 * @return
	 * @throws Exception
	 */
	public Boolean typeSubmitComment() throws Exception
	{
		Boolean flag=false;
		IWebElementWrapper element=element("fdsd.commentTextarea");
		if(element.isDisplayed())
		{
			logger.info("typing \"Force Direct Submit by automation\" on \"Force Direct Submit Dialog\"");
			element.clear();
			element.type("Force Direct Submit by automation");
			loadingDlg(null,5);//loadingDlg();
			
			flag=true;
		}
		return flag;
	}
	
	/**
	 * click submit button, return submit message
	 * @return
	 * @throws Exception
	 */
	public String clickSubmit() throws Exception
	{
		logger.info("clicking submit button on \"Force Direct Submit Dialog\"");
		element("fdsd.submit").click();
		loadingDlg(element("fdsd.submitConfirmMsg"),1000);
		String msg=element("fdsd.submitConfirmMsg").getInnerText();
		logger.info(msg);
		loadingDlg(null,5);//loadingDlg();
		element("fdsd.submitConfirmOK").click();
		loadingDlg(null,5);//loadingDlg();
		return msg;
	}
	
	
}
