package com.lombardrisk.pages;

import java.io.File;

import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.pojo.Form;

public class CreateNewReturnFromExcelDialog extends AbstractPage implements IComFolder{
	private Form form;
	private String type="createFromExcelForm";
	
	public CreateNewReturnFromExcelDialog(IWebDriverWrapper webDriverWrapper, Form form) {
		super(webDriverWrapper);
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
		flag=element("cfed.createFromExcelDialogTitle").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("cfed.closeDialog").click();
		loadingDlg();
	}
	/**
	 * click update button and upload file. it will return null if no error occurs, return errorMessage if contains any error
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String uploadFile() throws Exception
	{
		String errorTxt=null;
		logger.info("Execute js script");
		String js = "document.getElementById('" + type + ":importFileUpload').getElementsByTagName('div')[0].getElementsByTagName('span')[0].className='';";
		executeScript(js);
		String importFileFullName=TARGET_IMPORT_FOLDER+form.getRegulator()+"/"+form.getImportFile();
		if(new File(importFileFullName).exists())
		{
			element("abstract.importFileUpload_input",type).type(importFileFullName);
			errorTxt=uploadFileError(type);
			if(errorTxt!=null)
			{
				closeThisPage();
			}
		}else
		{
			errorTxt="no exist import file";
			closeThisPage();
		}
		if(errorTxt!=null)
		{
			logger.info("click upload button(error message):"+errorTxt);
		}
		return errorTxt;
	}
	
	/**
	 * click import adjustment menu list, and in opening dialog click import(+) button and import file.<br> If import successfully return FormInstancePage, otherwise return null.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public FormInstancePage importFile() throws Exception
	{
		FormInstancePage fip=null;
		Boolean flag=false;
		if (element("cfed.uploadFileName", type).isDisplayed())
		{
			if (element("cfed.uploadFileName", type).getInnerText().equalsIgnoreCase(form.getImportFile()))
				{flag=true;}
		}
		
		if(flag)
		{
			IWebElementWrapper uploadFileInitToZero=element("abstract.uploadFileInitToZero",type);
			if(form.getInitToZero()!=null && form.getInitToZero().equalsIgnoreCase("Y") && uploadFileInitToZero.isDisplayed())
			{
				if(!element("abstract.uploadFileInitToZeroChecked",type).isPresent())
				{uploadFileInitToZero.click();}
			}
			//difference
			IWebElementWrapper listimportBtn=element("cfed.listimportBtn",type);
			if(listimportBtn.isEnabled())
			{
				logger.info("click import button");
				listimportBtn.click();
				loadingDlg();
				String type2=type.substring(0, type.length()-4);
				IWebElementWrapper replaceconfirm=element("cfed.replaceconfirm",type2);
				if( replaceconfirm.isDisplayed())
				{
					replaceconfirm.click();
					loadingDlg();
				}
				IWebElementWrapper confirmBtn=element("cfed.confirmBtn");
				if(confirmBtn.isDisplayed())
				{
					confirmBtn.click();
					loadingDlg();
					waitThat("fipf.adjustmentLogTable_data").toBePresent();
					if(element("fipf.adjustmentLogTable_data").isPresent())
					{
						fip=new FormInstancePage(getWebDriverWrapper(),form);
						if(!fip.isThisPage())
						{
							fip.closeThisPage();//for these tester open a wrong form instance.
							waitThat().timeout(3000);
							fip=null;
						}
					}
				}
			}else
			{
				logger.info("can't click import button");
				flag=false;
				closeThisPage();
			}
		}
		return fip;
	}
	
	
}
