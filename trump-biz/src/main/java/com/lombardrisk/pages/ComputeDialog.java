package com.lombardrisk.pages;

import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.pojo.Form;

public class ComputeDialog extends AbstractPage implements IReturnDialog{
	
	private Form form;
	public ComputeDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager,Form form) {
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
		flag=element("ficmptd.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("ficmptd.closeDialog").click();
		loadingDlg();
	}
	
	/**
	 * select regulator, entity, process date, form with version, return true if exists
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
		public Boolean selectInfo() throws Exception
		{
			Boolean flag=false;
			String group=form.getEntity();
			String formAndVersion=form.getName()+" "+form.getVersion();
			String processDate=form.getProcessDate();
			
			waitThat("ficmptd.title").toBeVisible();
			if(group != null)
			{
				logger.info("select entity:" + group);
				flag=selectIt(element("ficmptd.selectGroup"),group);
				form.setEntity(getRealText(element("ficmptd.selectGroup"),group));
				if (processDate != null && flag)
				{
					logger.info("select process date:" + processDate);
					element("ficmptd.selectProcessDate").clear();
					element("ficmptd.selectProcessDate").input(processDate);
					loadingDlg();
					selectDate(processDate);
					if(formAndVersion != null && flag)
					{
						logger.info("select form:" + formAndVersion);
						flag=selectIt(element("ficmptd.selectForm"),formAndVersion);
						String tmp=getRealText(element("ficmptd.selectForm"),formAndVersion).trim();
						form.setName(tmp.substring(0, tmp.lastIndexOf(" ")));
						if(flag && element("ficmptd.messages").isPresent() && element("ficmptd.messages").isDisplayed())
						{
							flag=false;
						}
					}
				}
				
			}
			
			loadingDlg();
			if(!flag)
			{
				closeThisPage();
			}
			return flag;
		}
		
		/**
		 * click ok button and if compute successfully return FormInstancePage, others return null.
		 * @author kun shen
		 * @return
		 * @throws Exception
		 */
		public FormInstancePage clickOK() throws Exception
		{
			Boolean flag=false;
			FormInstancePage fip=null;
			logger.info("click compute button");
			element("ficmptd.ok").click();
			loadingDlg();
			if(element("ficmptd.messages").isDisplayed())
			{
				element("ficmptd.cancel").click();
				loadingDlg();
			}else
			{
				if(element("ficmptd.computeConfirm").isDisplayed())
				{
					element("ficmptd.computeConfirm").click();
					loadingDlg();
				}
				waitThat("ficmptd.title").toBeInvisible();
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
