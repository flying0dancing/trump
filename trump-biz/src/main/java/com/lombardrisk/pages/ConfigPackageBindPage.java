package com.lombardrisk.pages;

import com.lombardrisk.test.IComFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

public class ConfigPackageBindPage extends AbstractPage implements IComFolder{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ConfigPackageBindPage(IWebDriverWrapper webDriverWrapper, ITestDataManager testDataManager) {
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
		flag=element("cpbp.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("abstract.clickDashboard").click();
		loadingDlg(null,20);
	}


	public Boolean selectProduct(String prefix) throws Exception
	{
		Boolean flag=true;
		logger.info("select product prefix "+prefix);
		IWebElementWrapper elt=element("cpbp.selectPrefix",prefix.toUpperCase());
		if(elt.isPresent()){
			elt.click();
			loadingDlg(null,20);
		}else{
			logger.error("cannot select product prefix "+prefix);
			flag=false;
		}
		return flag;
	}

	public void clickClearETLCache() throws Exception
	{
		logger.info("click Clear ETL cache");
		element("cpbp.clearETL").click();
		ClearETLCacheDialog clearETL=new ClearETLCacheDialog(getWebDriverWrapper(),getTestDataManager());
		if(clearETL.isThisPage()){
			clearETL.clickOK();
		}
	}
	
	
}
