package com.lombardrisk.pages;


import java.util.List;

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
		List<IWebElementWrapper> elements=element("fscd.title").getAllMatchedElements();
		for(IWebElementWrapper element:elements)
		{
			if(element.isDisplayed()){
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		List<IWebElementWrapper> elements=element("fscd.cancel").getAllMatchedElements();
		for(IWebElementWrapper element:elements)
		{
			if(element.isDisplayed()){
				element.click();
				loadingDlg(null,5);//loadingDlg();
				break;
			}
		}
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
			loadingDlg(null,5);//loadingDlg();
			
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
		loadingDlg(null,20);
		waitThat("fscd.title").toBeInvisible();
	}
	
	public void clickDataScheduleSubmit() throws Exception
	{
		logger.info("clicking dataschedule submit button on \"Force Submit Dialog\"");
		element("fscd.DataSchedulesubmit").click();
		loadingDlg(null,5);//loadingDlg();
	}
	
	public void clickXbrlSubmit() throws Exception
	{
		logger.info("clicking xbrl submit button on \"Force Submit Dialog\"");
		element("fscd.submitXBRL").click();
		loadingDlg(null,20);
		waitThat("fscd.title").toBeInvisible();
	}
}
