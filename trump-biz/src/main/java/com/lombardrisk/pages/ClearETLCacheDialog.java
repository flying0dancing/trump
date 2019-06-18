package com.lombardrisk.pages;

import com.lombardrisk.test.IComFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

public class ClearETLCacheDialog extends AbstractPage implements IComFolder{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ClearETLCacheDialog(IWebDriverWrapper webDriverWrapper, ITestDataManager testDataManager) {
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
		flag=element("cecd.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("cecd.close").click();
		loadingDlg(null,5);
	}
	

	
	/**
	 * click submit button, return submit message
	 * @return
	 * @throws Exception
	 */
	public void clickOK() throws Exception
	{
		element("cecd.OK").click();
		loadingDlg(null,5);
	}
	
	
}
