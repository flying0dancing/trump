package com.lombardrisk.pages;

import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.DBInfo;

/**
 * Created by Kevin Ling on 2/16/15. Refactored by Leo Tu on 1/29/16
 */
public class HomePage extends AbstractPage 
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String loginUser;
	private DBInfo dBInfo;
	public String getLoginUser()
	{
		return loginUser;
	}
	public DBInfo getDBInfo() {
		return dBInfo;
	}
	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
	}
	public HomePage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
		super(webDriverWrapper,testDataManager);
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}
	
	public ListPage logon(String userName,String password) throws Exception
	{

		ListPage listPage = null;
		try
		{
			//HomePage homePage = new HomePage(getWebDriverWrapper());
			typeUsername(userName);
			typePassword(password);
			listPage = submitLogin();
			waitThat("lp.dashboard").toBeVisible();
			//assertThat("pp.userLabel").innerText().as("List Pag dashboard").containsIgnoringCase("hi "+userName);
		}
		catch (NoSuchElementException e)
		{
			if (element("hm.pageError").getInnerText().equals("This webpage is not available"))
			{
				logger.error("This webpage is not available, please check url and server status!");
				throw new RuntimeException("This webpage is not available");
			}

		}
		return listPage;
	}

	public void typeUsername(String userName) throws Exception
	{
		this.loginUser=userName;
		element("hm.name").input(userName);
	}

	public void typePassword(String password) throws Exception
	{
		element("hm.pwd").input(password);
	}

	public ListPage submitLogin() throws Exception
	{
		element("hm.login").click();

		if(element("hm.message").isPresent())
		{
			logger.error(element("hm.message").getInnerText());
			waitThat("hm.message").toBeInvisible();
			return null;
		}
		loadingDlg();
		waitForPageLoaded();
		return new ListPage(getWebDriverWrapper(),getTestDataManager());
	}

	public HomePage submitLoginExpectingFailure() throws Exception
	{
		element("hm.login").click();
		loadingDlg();
		return new HomePage(getWebDriverWrapper(),getTestDataManager());
	}

	public ListPage loginAs(String userName, String password) throws Exception
	{
		if(userName==null || userName.trim().equals("")){userName="admin";}
		if(password==null || password.trim().equals("")){password="password";}
		logger.info("Login AgileReporter with user [" + userName + "]");
		typeUsername(userName);
		typePassword(password);
		loadingDlg();
		ListPage listPage = submitLogin();
		//assertThat("pp.userLabel").innerText().containsIgnoringCase("hi "+userName);
		if(listPage.isThisPage())
		{
			String expectedLanguage=PropHelper.getProperty("Regional.language")==null?"":PropHelper.getProperty("Regional.language").trim();
			String acctualLanguage=getDBInfo().getLanguage(userName);
			if(acctualLanguage==null && expectedLanguage.equals("")){Assert.fail("cannot set language, Regional.language is empty in test.properties.");}
			  if ((acctualLanguage==null && !expectedLanguage.equals("")) || !acctualLanguage.trim().equalsIgnoreCase(expectedLanguage))
				{
				  	PreferencePage preferencePage = listPage.enterPreferencePage();
					preferencePage.selectLanguageByValue(expectedLanguage);
					logger.info("Note: The language is changed from: [" + acctualLanguage +"] to ["+expectedLanguage+"].");
				} 
		}else
		{
			listPage=null;
			Assert.fail("cannot access to Dashboard.");
		}
		 
		return listPage;
	}
	
	public ListPage login(String userName, String password) throws Exception
	{
		if(userName==null || userName.trim().equals("")){userName="admin";}
		if(password==null || password.trim().equals("")){password="password";}
		logger.info("Login AgileReporter with user [" + userName + "]");
		typeUsername(userName);
		typePassword(password);
		loadingDlg();
		ListPage listPage = submitLogin();
		//assertThat("pp.userLabel").innerText().containsIgnoringCase("hi "+userName);
		if(listPage==null)
		{
			return listPage;
		}
		if(listPage.isThisPage())
		{
			
		}else
		{
			listPage=null;
			Assert.fail("error: cannot access to Dashboard.");
		}
		 
		return listPage;
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		return element("hm.login").isPresent();
	}
	

}
