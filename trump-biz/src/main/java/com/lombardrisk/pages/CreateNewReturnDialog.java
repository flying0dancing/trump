package com.lombardrisk.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.pojo.Form;

public class CreateNewReturnDialog extends AbstractPage implements IReturnDialog{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	public CreateNewReturnDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form)
	{
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
		flag=element("ficd.createNewReturnTitle").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("ficd.closeCreateNewReturn").click();
		loadingDlg();
	}
	
/**
 * select regulator, entity, form with version, process date, return true if exists
 * @author kun shen
 * @return
 * @throws Exception
 */
	public Boolean selectInfo() throws Exception
	{
		Boolean flag=false;
		String group=form.getEntity();
		String formAndVersion=form.getName()+" "+form.getVersion().toLowerCase();
		String processDate=form.getProcessDate();
		String cloneData=form.getCloneData();
		String initZero=form.getInitToZero();
		waitThat("ficd.createNewReturnTitle").toBeVisible();
		if(group != null)
		{
			logger.info("select entity:" + group);
			flag=selectIt(element("ficd.selectGroup"),group);
			form.setEntity(getRealText(element("ficd.selectGroup"),group));
			if (processDate != null && flag)
			{
				logger.info("select process date:" + processDate);
				element("ficd.selectProcessDate").input(processDate);
				loadingDlg();
				selectDate(processDate);
				if(formAndVersion != null && flag)
				{
					logger.info("select form:" + formAndVersion);
					flag=selectIt(element("ficd.selectForm"),formAndVersion);
					String tmp=getRealText(element("ficd.selectForm"),formAndVersion).trim();
					form.setName(tmp.substring(0, tmp.lastIndexOf(" ")));
					if(cloneData != null && !cloneData.trim().equals("")&& flag)
					{
						element("ficd.cloneCheck").click();
						loadingDlg();
						flag=selectIt(element("ficd.selectCloneDate"),cloneData);
					}else
					{
						if(element("ficd.initToZeroCheck").isDisplayed() && initZero != null && initZero.equalsIgnoreCase("Y") && flag)
						{
							logger.info("initial to zero:" + initZero);
							element("ficd.initToZeroCheck").click();
						}
					}
					
				}
			}
			
		}
		loadingDlg();
		waitThat().timeout(2000);
		if(!flag)
		{
			closeThisPage();
		}
		return flag;
	}	
/**
 * click create button and if create successfully return FormInstancePage, others return null.
 * @author kun shen
 * @return
 * @throws Exception
 */
public FormInstancePage create() throws Exception
{
	Boolean flag=false;
	FormInstancePage fip=null;
	logger.info("click create button");
	element("ficd.create").click();
	loadingDlg();
	if(element("ficd.messages").isDisplayed())
	{
		element("ficd.cancel").click();
		loadingDlg();
	}else
	{
		if(element("ficd.createconfirm").isDisplayed())
		{
			element("ficd.createconfirm").click();
			loadingDlg();
		}
		waitThat("ficd.createNewReturnTitle").toBeInvisible();
		waitThat("fipf.form").toBeVisible();
		
		flag=true;
	}
	if(flag)
	{
		
		fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager(),form);
		if(!fip.isThisPage())
		{
			fip=null;
		}
	}
	
	return fip;
}

/**
 * click create button and if create successfully return FormInstancePage, others return null.
 * @author kun shen
 * @return
 * @throws Exception
 */
public FormInstancePage clickOK() throws Exception
{
	Boolean flag=false;
	FormInstancePage fip=null;
	logger.info("click create button");
	element("ficd.create").click();
	loadingDlg();
	if(element("ficd.messages").isDisplayed())
	{
		element("ficd.cancel").click();
		loadingDlg();
	}else
	{
		if(element("ficd.createconfirm").isDisplayed())
		{
			element("ficd.createconfirm").click();
			loadingDlg();
		}
		waitThat("ficd.createNewReturnTitle").toBeInvisible();
		waitThat("fipf.form").toBeVisible();
		
		flag=true;
	}
	if(flag)
	{
		
		fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager(),form);
		if(!fip.isThisPage())
		{
			fip=null;
		}
	}
	
	return fip;
}

}
