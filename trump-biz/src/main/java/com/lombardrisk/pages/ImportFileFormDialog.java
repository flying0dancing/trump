package com.lombardrisk.pages;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.pojo.Form;

public class ImportFileFormDialog extends AbstractPage implements IComFolder{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	private String type="importFileForm"; // "importFileForm" is dialog selected in return, "listImportFileForm" is dialog selected in dash board
	
	public ImportFileFormDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form) {
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
		flag=element("aifd.importFilesDialogTitle").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("aifd.closeDialog").click();
		loadingDlg(null,10);//loadingDlg();
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
			loadingDlgDis(element("aifd.checkUpload"),25);
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
			
			flag=super.applyScaleRadio(type,form.getApplyScale());
			if(!flag){
				closeThisPage();
				return flag;
			}
			flag=selectImportMode();//select importMode(override, additive)
			if(!flag){
				closeThisPage();
				return flag;
			}
			//difference
			IWebElementWrapper listimportBtn=element("aifd.importBtn",type);
			if(listimportBtn.isEnabled())
			{
				logger.info("click import button");
				listimportBtn.click();
				waitThat().timeout(5000);
				loadingDlg(null,100);

				//confirm to overwrite by hetty wu
				if(element("iacd.title").isDisplayed())
				{
					logger.info("click confirm button");
					element("iacd.confirm").click();
					loadingDlg(null,100);
				}
				//add a judge for import successfully
				if(!element("fipf.pageTab").isPresent())
				{
					flag=false;
					logger.error("can't open form instance");
					super.getWebDriverWrapper().navigate().backward();
				}
			}else
			{
				logger.error("can't click import button");
				flag=false;
				closeThisPage();
			}
			
		}
		if(isThisPage()){
			closeThisPage();
		}
		return flag;
	}
	
	public Boolean selectImportMode() throws Exception
	{
		Boolean flag=true;
		String str_replace="\"Replace existing return(if any)\"";
		String str_additive="\"Add to existing value (Numeric cells only)\"";
		if(StringUtils.isNotBlank(form.getImportMode()))
		{
			String mode=form.getImportMode().toLowerCase();
			if(mode.equals("override") || mode.equals("over") || mode.equals("y"))
			{
				logger.info("click radio "+str_replace);
				element("aifd.importMode",type,"Override").click();
				loadingDlg(null,5);//loadingDlg();
				if(!element("aifd.importMode_status",type,"1").isPresent()){
					flag=false;
					logger.error("fail to select radio"+str_replace);
				}
			}else if(mode.equals("additive") || mode.equals("add") || mode.equals("append")|| mode.equals("n"))
			{
				logger.info("click radio "+str_additive);
				element("aifd.importMode",type,"Additive").click();
				loadingDlg(null,5);//loadingDlg();
				if(!element("aifd.importMode_status",type,"3").isPresent()){
					flag=false;
					logger.error("fail to select radio"+str_additive);
				}
			}else{
				flag=false;
				logger.error("wrong value in column importMode, should be override or additive, also could be empty.");
			}
			
		}else{
			if(element("aifd.importMode_status",type,"3").isPresent()){
				logger.info("use default setting "+str_additive);
			}else{
				logger.info("use default setting "+str_replace);
			}
			
		}
		return flag;
	}
	
	
}
