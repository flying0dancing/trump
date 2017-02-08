package com.lombardrisk.pages;

import java.io.File;

import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.pojo.Form;

public class ImportFileFormDialog extends AbstractPage implements IComFolder{
	private Form form;
	private String type="importFileForm";
	
	public ImportFileFormDialog(IWebDriverWrapper webDriverWrapper, Form form) {
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
		flag=element("aifd.importFilesDialogTitle").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("aifd.closeDialog").click();
		loadingDlg();
	}
	
	/**
	 * click upload button and upload file. it will return null if no error occurs, return errorMessage and close import file dialog if contains any error
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
	 * click import button, return true if import file successfully, others return false and close import dialog.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean importFile() throws Exception
	{
		Boolean flag=false;
		if (element("aifd.uploadFileName", type).isDisplayed())
		{
			if (element("aifd.uploadFileName", type).getInnerText().equalsIgnoreCase(form.getImportFile()))
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
			IWebElementWrapper listimportBtn=element("aifd.importBtn",type);
			if(listimportBtn.isEnabled())
			{
				logger.info("click import button");
				listimportBtn.click();
				loadingDlg();
				
			}else
			{
				logger.info("can't click import button");
				flag=false;
				closeThisPage();
			}
		}
		return flag;
	}
	
	
}
