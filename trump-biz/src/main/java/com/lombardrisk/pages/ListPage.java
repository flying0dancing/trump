package com.lombardrisk.pages;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.Form;


/**
 * @author kun shen
 * @version 20161124
 */
public class ListPage extends AbstractPage implements IExportTo
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	public String getLoginUser() throws Exception
	{
		String loginUser=element("fipf.lblUserName").getInnerText();
		loginUser=loginUser.replace("hi ", "");
		logger.info("loginUser is "+loginUser);
		return loginUser;
	}
	
	
	/**
	 * 
	 * @param webDriverWrapper
	 */
	public ListPage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
		super(webDriverWrapper,testDataManager);
	}

	/**
	 * export to regulator in list page
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public ExportToRegulatorDialog exportToRegulator(Form form) throws Exception
	{
		ExportToRegulatorDialog td=null;
		Boolean flag=selectIt(element("filf.regulator"),form.getRegulator());
		if(flag)
		{
			logger.info("click \"Export to Regulator Format\" button");
			element("filf.exportToFile_button").click();
			loadingDlg();
			String title=null;
			String fileType=form.getTransmission().getFileType();

			String liTxt=null;
			flag=false;//reset flag for finding matched Export to something
			int count=element("filf.exportToFile_li").getNumberOfMatches();
			int i=1;
			for(;i<=count;i++)
			{
				liTxt=element("filf.exportToFile_li_Txt",String.valueOf(i)).getInnerText();
				if(liTxt==null|| liTxt.trim().equals("")){continue;}
				if(fileType==null || fileType.trim().equals(""))
				{
					if(liTxt.trim().toUpperCase().endsWith(form.getName().toUpperCase()))
					{
						flag=true;
						break;
					}else if(liTxt.trim().toUpperCase().endsWith(form.getName().toUpperCase()+"_"+form.getVersion().toUpperCase()))
					{
						flag=true;
						break;
					}
				}else 
				{
					if(liTxt.trim().toUpperCase().endsWith(fileType.trim().toUpperCase()))
					{
						flag=true;
						break;
					}
				}
				
			}
			if(flag && liTxt!=null)
			{
				String js = "document.getElementById('FormInstImpExpMenu:exportToFile_menu').getElementsByTagName('ul')[0].getElementsByTagName('li')["+String.valueOf(i-1)+"].getElementsByTagName('a')[0].getElementsByTagName('span')[0].click();";
				executeScript(js);
				loadingDlg();
				//reuse this part of select displayed transmit dialog from 20170113
				List<IWebElementWrapper> elements=element("td.transmitDialogTitles").getAllMatchedElements();
				for(IWebElementWrapper element:elements)
				{
					if(element.isDisplayed())
					{
						title=element.getInnerText();
						td=new ExportToRegulatorDialog(getWebDriverWrapper(),getTestDataManager(),form,title);
						break;
					}
				}
				//unused this part of select displayed transmit dialog, comment at 20170113
				/*IWebElementWrapper element=element("td.transmitDialog4FedTitle");
				if(element.isDisplayed())
				{
					title=element.getInnerText();
					td=new ExportToRegulatorDialog(getWebDriverWrapper(),form,title);
				}else
				{
					element=element("td.transmitDialogTitle");
					if(element.isDisplayed())
					{
						title=element.getInnerText();
						td=new ExportToRegulatorDialog(getWebDriverWrapper(),form,title);
					}
				}*/
				
			}else{
				logger.info("click \"Export to Regulator Format\" button");
				element("filf.exportToFile_button").click();
				loadingDlg();
			}
			
		}
		
		return td;
	}
	
	/**
	 * enter preferencePage
	 * 
	 * @return PreferencePage
	 * @throws Exception
	 */
	public PreferencePage enterPreferencePage() throws Exception
	{
		logger.info("click \"hi $user\" button");
		element("fipf.lblUser_button").click();
		waitThat().timeout(1000);
		if(element("fipf.btnPreferences").isDisplayed())
		{
			logger.info("click \"Preferences\" button");
			element("fipf.btnPreferences").click();
			loadingDlg();
		}
		return new PreferencePage(getWebDriverWrapper(),getTestDataManager());
	}
	
	
	/**
	 * refresh page and select form in list page, return true if it exists.
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Boolean selectFormInfo(Form form) throws Exception
	{
		Boolean flag=false;
		String regulator=form.getRegulator();
		String group=form.getEntity();
		String formAndVersion=form.getName()+" "+form.getVersion();
		String processDate=form.getProcessDate();
						
		logger.info("select regulator:" + regulator);
		flag=selectIt(element("filf.regulator"),regulator);
		if(flag)
		{
			form.setRegulator(getRealText(element("filf.regulator"),regulator));
			logger.info("select entity:" + group);
			flag=selectIt(element("filf.selectGroup"),group);
			form.setEntity(getRealText(element("filf.selectGroup"),group));
			if(flag)
			{
				logger.info("select form:" + formAndVersion);
				flag=selectIt(element("filf.selectForm"),formAndVersion);
				String tmp=getRealText(element("filf.selectForm"),formAndVersion).trim();
				form.setName(tmp.substring(0, tmp.lastIndexOf(" ")));
				
				if (flag)
				{
					logger.info("select process date:" + processDate);
					flag=selectIt(element("filf.selectProcessDate"),processDate);
					if(!flag){return flag;}
				}
			}
		}
		loadingDlg();
		if(element("filf.noRecordsFound").isPresent() && element("filf.noRecordsFound").isDisplayed())
		{
			flag=false;
			return flag;
		}
		IWebElementWrapper element=element("filf.clickFormLink", form.getName(),form.getVersion().substring(1),form.getProcessDate());
		if(element!=null &&  element.isDisplayed())
		{
			flag=true;
		}else
		{
			refreshPage();
			flag=selectFormInfo(form,5);
		}
		
		return flag;
	}
	/**
	 * refresh page and select form in list page, return true if it exists.
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Boolean selectFormInfo(Form form,int times) throws Exception
	{
		Boolean flag=false;
		if(times<=0){return flag;}
		String regulator=form.getRegulator();
		String group=form.getEntity();
		String formAndVersion=form.getName()+" "+form.getVersion();
		String processDate=form.getProcessDate();
						
		logger.info("select regulator:" + regulator);
		flag=selectIt(element("filf.regulator"),regulator);
		if(flag)
		{
			form.setRegulator(getRealText(element("filf.regulator"),regulator));
			logger.info("select entity:" + group);
			flag=selectIt(element("filf.selectGroup"),group);
			form.setEntity(getRealText(element("filf.selectGroup"),group));
			if(flag)
			{
				logger.info("select form:" + formAndVersion);
				flag=selectIt(element("filf.selectForm"),formAndVersion);
				String tmp=getRealText(element("filf.selectForm"),formAndVersion).trim();
				form.setName(tmp.substring(0, tmp.lastIndexOf(" ")));
				
				if (flag)
				{
					logger.info("select process date:" + processDate);
					flag=selectIt(element("filf.selectProcessDate"),processDate);
					if(!flag){return flag;}
				}
			}
		}
		//loadingDlg();
		if(element("filf.noRecordsFound").isPresent() && element("filf.noRecordsFound").isDisplayed())
		{
			flag=false;
			return flag;
		}
		IWebElementWrapper element=element("filf.clickFormLink", form.getName(),form.getVersion().substring(1),form.getProcessDate());
		if(element!=null &&  element.isDisplayed())
		{
			flag=true;
		}else
		{
			refreshPage();
			times--;
			selectFormInfo(form,times);
		}
		
		return flag;
	}
	/**
	 * create new return(form) in selected regulator, and return new return page.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception 
	 */
	public CreateNewReturnDialog createNewReturn(Form form) throws Exception
	{
		CreateNewReturnDialog createNewReturn=null;

		Boolean flag=selectIt(element("filf.regulator"),form.getRegulator());
		if(flag)
		{
			element("filf.createNew").click();
			waitThat("filf.createNewEmptyLink").toBeVisible();
			element("filf.createNewEmptyLink").click();
			loadingDlg();
			createNewReturn=new CreateNewReturnDialog(getWebDriverWrapper(),getTestDataManager(),form);
		}
	
		return createNewReturn;
		
	}
	
	/**
	 * retrieve return(form) in selected regulator, and return retrieve return dialog page.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception 
	 */
	public RetrieveDialog retrieveReturn(Form form) throws Exception
	{
		RetrieveDialog dialog=null;

		Boolean flag=selectIt(element("filf.regulator"),form.getRegulator());
		if(flag)
		{
			if(element("filf.retrieve").isPresent() && element("filf.retrieve").isDisplayed())
			{
				element("filf.retrieve").click();
				loadingDlg();
				dialog=new RetrieveDialog(getWebDriverWrapper(),getTestDataManager(),form);
			}
			//added by AR1.15.7-b68 for Asia
			if(element("filf.retrieveMenu").isPresent() && element("filf.retrieveMenu").isDisplayed())
			{
				element("filf.retrieveMenu").click();
				waitThat("filf.retrieveSingle").toBeVisible();
				element("filf.retrieveSingle").click();
				loadingDlg();
				dialog=new RetrieveDialog(getWebDriverWrapper(),getTestDataManager(),form);
			}
			
		}
	
		return dialog;
		
	}
	
	/**
	 * retrieve return(form) in selected regulator, and return retrieve return dialog page.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception 
	 */
	public ComputeDialog computeReturn(Form form) throws Exception
	{
		ComputeDialog dialog=null;

		Boolean flag=selectIt(element("filf.regulator"),form.getRegulator());
		if(flag)
		{
			if(element("filf.compute").isPresent() && element("filf.compute").isDisplayed())
			{
				element("filf.compute").click();
				loadingDlg();
				dialog=new ComputeDialog(getWebDriverWrapper(),getTestDataManager(),form);
			}
		}
	
		return dialog;
		
	}
	
	/**
	 * create new return(form) from excel in selected regulator, and return new return from excel page, otherwise return null.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception 
	 */
	public CreateNewReturnFromExcelDialog createNewReturnFromExcel(Form form) throws Exception
	{
		CreateNewReturnFromExcelDialog createNewReturn=null;

		Boolean flag=false;
		logger.info("select regulator:" + form.getRegulator());
		flag=selectIt(element("filf.regulator"),form.getRegulator());
		if(flag)
		{
			element("filf.createNew").click();
			waitThat("filf.createNewFromExcelLink").toBeVisible();
			element("filf.createNewFromExcelLink").click();
			loadingDlg();
			createNewReturn=new CreateNewReturnFromExcelDialog(getWebDriverWrapper(),getTestDataManager(),form);
		}
		
		return createNewReturn;
	}
	/**
	 * delete existed form, if delete successfully or this form doesn't exist return true, others return false
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteExistedFormInstance(Form form) throws Exception
	{
		if(!selectFormInfo(form))
		{
			if(search(form)<0)
			{
				return true;
			}
		}
		Boolean flag=false;
		
		/*form.setEntity(getRealText(element("filf.clickFormLinkEntity"),form.getEntity()));		
		form.setName(getRealText(element("filf.clickFormLinkName"),form.getName()));*/
		
		IWebElementWrapper element=element("filf.deleteForm",form.getName(),form.getVersion().substring(1),form.getProcessDate());
		if(element!=null && element.isPresent())
		{
			logger.info("delete existed form instance");
			element.click();
			loadingDlg();
			IWebElementWrapper deleteReturnConfirm=element("filf.deleteReturnConfirm");
			
			if (deleteReturnConfirm!=null && deleteReturnConfirm.isDisplayed())
			{
				deleteReturnConfirm.click();
				loadingDlg();
				IWebElementWrapper deleteComment=element("drcfd.deleteComment");
				
				if(deleteComment!=null && deleteComment.isDisplayed())
				{
					deleteComment.type("delete by automation");
					loadingDlg();
					element("drcfd.deleteReturn").click();
					loadingDlg();
					flag=getTipMessageStatus();
					//flag=true;
				}
			}
			
			
		}else
		{
			logger.error("error: cannot delete form.");
		}
		
	
		
		return flag;
	}
	
	/**
	 * click form instance link and return FormInstancePage or null.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public FormInstancePage openFormInstance(Form form) throws Exception
	{
		FormInstancePage fip=null;
		String userName=getLoginUser();
		//form.setEntity(getRealText(element("filf.clickFormLinkEntity"),form.getEntity()));		
		//form.setName(getRealText(element("filf.clickFormLinkName"),form.getName()));
		
		IWebElementWrapper element=element("filf.clickFormLink",form.getName(),form.getVersion().substring(1),form.getProcessDate());
		if(element!=null &&  element.isDisplayed())
		{
			logger.info("open form instance");
			element.click();
			//
			//waitForPageLoaded();
			//loadingDlg();
			/*String[] bigReturns={"MAS610_D2","MAS610_F","MAS610_D4","FRY14ASUMM"};
			for(String bigRtn: bigReturns)
			{
				if(form.getName().equalsIgnoreCase(bigRtn))
				{
					while(element("abstract.ajaxstatusDlg").isDisplayed())
					{
						waitThat().timeout(10000);
					}
					break;
				}
			}*/
			loadingDlg(element("fipf.form"),200);
			waitThat("fipf.form").toBeVisible();
			fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager(),form,userName);
			if(!fip.isThisPage())
			{
				fip=null;
			}
		}else
		{
			logger.error("error: cannot find form link.");
		}
		
		return fip;
	}
	
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("filf.formInstanceListForm").isPresent();
		
		return flag;
	}

	/**
	 * if timeout refresh page and login again.
	 * @author kun shen
	 * @param listPage
	 * @throws Exception
	 */
	public void loginAfterTimeout(ListPage listPage) throws Exception
	{
		refreshPage();
		//TODO
		if(element("fipf.form").isPresent())
		{
			super.getWebDriverWrapper().navigate().backward();
		}
		if(!listPage.isThisPage())
		{
			if(element("hm.login").isPresent() && element("hm.login").isDisplayed())
			{
				HomePage hp=new HomePage(getWebDriverWrapper(),getTestDataManager());//TODO
				String loginUserName=((TestDataManager)getTestDataManager()).getDBInfo().getApplicationServer_UserName();
				String loginUserPassword=((TestDataManager)getTestDataManager()).getDBInfo().getApplicationServer_UserPassword();
				listPage=hp.loginAs(loginUserName, loginUserPassword);
			}
		}
		getTipMessageStatus();
	}
	
	/**
	 * get notification number, return 0 if no notification.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public int getNotification() throws Exception
	{
		String notification=element("filf.notification").getInnerText();
		int notificationCount=0;
		if(notification!=null && !notification.trim().equals("")){notificationCount=Integer.parseInt(notification);}
		return notificationCount;
	}
	
	/**
	 * choose which dialog will be clicked, depend on form.importFile.
	 * if form.importFile equals "compute", it will click compute return dialog.
	 * if form.importFile equals "retrieve", it will click retrieve return dialog.
	 * if form.importFile doesn't match above conditions, it will click create new return dialog.
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public IReturnDialog returnDialog(Form form) throws Exception
	{
		String importFile=form.getImportFile();
		IReturnDialog iReturnDlg=null;
		
		if(importFile.equalsIgnoreCase("retrieve"))
		{
			iReturnDlg=retrieveReturn(form);
		}
		if(importFile.equalsIgnoreCase("compute"))
		{
			iReturnDlg=computeReturn(form);
		}
		
		if(iReturnDlg==null)
		{
			iReturnDlg=createNewReturn(form);
		}
		
		return iReturnDlg;
	}
	
	protected JobManagerPage clickJobManager() throws Exception
	{
		JobManagerPage jmp=null;
		element("filf.jobManager").click();
		loadingDlg();
		jmp=new JobManagerPage(getWebDriverWrapper(),getTestDataManager());
		if(!jmp.isThisPage())
		{
			jmp=null;
		}
		return jmp;
	}
	
	/**
	 *  return row id if form existed, otherwise return -1
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception
	 */
	protected int search(Form form) throws Exception
	{
		String name=form.getName();
		String referenceDate=form.getProcessDate();
		String entity=form.getEntity();
		String version=form.getVersion().substring(1);
		int rowId=-1;
		form.setRegulator(getRealText(element("filf.regulator"),form.getRegulator()));
		//form.setEntity(getRealText(element("filf.clickFormLinkEntity"),form.getEntity()));		
		//form.setName(getRealText(element("filf.clickFormLinkName"),form.getName()));
		
		if(selectIt(element("filf.regulator"),form.getRegulator()))
		{
			IWebElementWrapper gridBarFirstPointer=element("filf.firstPageSta");
			if(gridBarFirstPointer.isPresent())
			{
				if(!gridBarFirstPointer.getAttribute("class").contains("ui-state-disabled"))
				{
					gridBarFirstPointer.click();
					loadingDlg();	
				}
				IWebElementWrapper nextPageBar=element("filf.nextPageSta");
				while(nextPageBar.isDisplayed() && !nextPageBar.getAttribute("class").contains("ui-state-disabled"))
				{
					rowId=getForm(name,version,referenceDate,entity);
					if(rowId>-1)
					{
						break;
					}
					nextPageBar.click();
					loadingDlg();
					nextPageBar=element("ficmptd.nextPageSta");
				}
			}
			if(rowId==-1)
			{
				rowId=getForm(name,version,referenceDate,entity);
			}
		}
		
		
		return rowId;
	}
	
	/**
	 * return row id if form existed, otherwise return -1
	 * @author kun shen
	 * @param name
	 * @param version
	 * @param referenceDate
	 * @param entity :deprecated from 2017.10.17 ARversion:1.15.6
	 * @return
	 * @throws Exception
	 */
	private int getForm(String name,String version,String referenceDate,String entity) throws Exception
	{
		int ri=-1;
		String data_ri=null;
		IWebElementWrapper element=element("filf.FormRow",name,version,referenceDate);
		if(element!=null && element.isPresent())
		{
			data_ri=element.getAttribute("data-ri");
		}
		if(data_ri!=null && !data_ri.equals(""))
		{
			ri=Integer.parseInt(data_ri);
		}
		return ri;
	}
	
	/**
	 * logout
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public HomePage logout() throws Exception
	{
		element("fipf.lblUser_button").click();
		waitThat().timeout(1000);
		if(element("fipf.btnLogout").isDisplayed())
		{
			element("fipf.btnLogout").click();
			loadingDlg();
		}
		return new HomePage(getWebDriverWrapper(),getTestDataManager());
	}
	
	public String approval1st() throws Exception
	{
		String status="No Approval Required";
		IWebElementWrapper element=element("filf.selectApproval");
		if(element!=null)
		{
			status=element.getInnerText();
		}
		return status;
	}
}
