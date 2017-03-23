package com.lombardrisk.pages;

import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;


/**
 * Created by Leo Tu on 8/2/16
 */
public class PreferencePage extends AbstractPage
{
	
	public PreferencePage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
		super(webDriverWrapper,testDataManager);
	}
	
	protected void saveSetting() throws Exception
	{
		element("prp.confirm").click();
	}

	protected void cancelSetting() throws Exception
	{
		element("prp.canel").click();
	}

	public void selectTimeZone(String timezone) throws Exception
	{
		element("prp.TZC").click();
		loadingDlg();
		element("prp.TZS").selectByVisibleText(timezone);
		element("prp.confirm").click();
		loadingDlg();
	}
	
	public void selectTimeZoneByValue(String timezone) throws Exception
	{
		element("prp.TZC").click();
		loadingDlg();
		element("prp.TZS").selectByValue(timezone);
		element("prp.confirm").click();
		loadingDlg();
	}

	public void selectLanguage(String language) throws Exception
	{
		element("prp.LC").click();
		loadingDlg();
		element("prp.LS").selectByVisibleText(language);
		element("prp.confirm").click();
		loadingDlg();
	}

	public void selectLanguageByValue(String language) throws Exception
	{
		element("prp.LC").click();
		loadingDlg();
		element("prp.LS").selectByValue(language);
		Thread.sleep(500);
		element("prp.confirm").click();
		loadingDlg();
	}

}
