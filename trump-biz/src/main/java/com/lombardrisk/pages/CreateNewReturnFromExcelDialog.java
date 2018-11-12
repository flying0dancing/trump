package com.lombardrisk.pages;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.google.common.base.Strings;
import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.commons.JxlUtil;
import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;

public class CreateNewReturnFromExcelDialog extends AbstractPage implements IComFolder,IExecFuncFolder{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	private String type="createFromExcelForm";
	private DBInfo dBInfo;
	public CreateNewReturnFromExcelDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form) {
		super(webDriverWrapper,testDataManager);
		this.form=form;
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}
	public DBInfo getDBInfo() {
		return dBInfo;
	}
	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
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
	 * upload new file, file name according to args1(importFileFullName), and copy new file to import folder with args1(importFileFullName)'s file name.
	 * @param importFileFullName
	 * @return
	 */
	private String findNewFileForUpload(String importFileFullName)
	{
		String newFileFullPath=null;
		if(new File(importFileFullName).exists())
		{
			newFileFullPath=importFileFullName;
		}else
		{
			String newFile=null;
			String prefixFilter=form.getImportFile().lastIndexOf("(")==-1?form.getImportFile().substring(0, form.getImportFile().lastIndexOf(".")):form.getImportFile().substring(0, form.getImportFile().lastIndexOf("("));
			String filterStr=prefixFilter+"*"+form.getImportFile().substring(form.getImportFile().lastIndexOf("."));
			boolean flag=false;
			if(new File(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELNOSCALE+")").exists())
			{
				newFile=getLatestFile(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELNOSCALE+")"+"/", filterStr);
				if(new File(newFile).exists())
				{
					flag=true;
					newFileFullPath=newFile;
				}
			}
			if(!flag && new File(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCEL+")").exists())
			{
				newFile=getLatestFile(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCEL+")"+"/", filterStr);
				if(new File(newFile).exists())
				{
					flag=true;
					newFileFullPath=newFile;
				}
			}
			if(!flag && new File(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELAPPLYSCALE+")").exists())
			{
				newFile=getLatestFile(TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELAPPLYSCALE+")"+"/", filterStr);
				if(new File(newFile).exists())
				{
					flag=true;
					newFileFullPath=newFile;
				}
			}
			if(flag)
			{
				try {
					logger.info("copy file from "+newFileFullPath+" to " + importFileFullName);
					FileUtils.copyFile(new File(newFileFullPath), new File(importFileFullName));
					
					newFileFullPath=importFileFullName;
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		
		return newFileFullPath;
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
		if(Strings.isNullOrEmpty(form.getImportFile()) )
		{
			return "no exist import file";
		}
		String importFileFullName=TARGET_IMPORT_FOLDER+form.getRegulator()+"/"+form.getImportFile();//
		if(!new File(importFileFullName).exists())
		{
			//TODO: adding function for update date in importfile.
			importFileFullName=findNewFileForUpload(importFileFullName);
			if(importFileFullName!=null && new File(importFileFullName).exists())
			{
				ExcelUtil.updateCellsInExcel(importFileFullName, new String[][]{{"_ReportingDate","-1","-1", form.getProcessDate(),"date:"+dateFormat},{"_EntityCode","-1","-1", getDBInfo().getEntityCode(form.getRegulator(), form.getEntity()),null}});//update process date and entity by form's getProcessDate
			}else
			{
				return "no exist import file";
			}
		}
		
		logger.info("Execute js script");
		String js = "document.getElementById('" + type + ":importFileUpload').getElementsByTagName('div')[0].getElementsByTagName('span')[0].className='';";
		executeScript(js);

		if(importFileFullName!=null && new File(importFileFullName).exists())
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
			if (element("cfed.uploadFileName", type).getInnerText().equalsIgnoreCase(form.getImportFile())){
				flag=true;
			}else{
				logger.error("fail to upload file["+form.getImportFile()+"]");
			}
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
			if(flag){
				fip=clickImportButton();
			}else{
				closeThisPage();
			}
			
		}
		return fip;
	}
	
	private FormInstancePage clickImportButton() throws Exception{

		FormInstancePage fip=null;
		//click import button
		IWebElementWrapper listimportBtn;
		listimportBtn = element("cfed.listimportBtn",type);
		
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
				//add a judge for import successfully
				if(!element("fipf.pageTab").isPresent())
				{
					//flag=false;
					logger.error("can't open form instance");
					super.getWebDriverWrapper().navigate().backward();
				}else
				{
					waitThat("fipf.adjustmentLogTable_data").toBePresent();
					if(element("fipf.adjustmentLogTable_data").isPresent())
					{
						fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager(),form);
						if(!fip.isThisPage())
						{
							logger.info("open a wrong form instance, please check your excel file.");
							fip.closeThisPage();//for these tester open a wrong form instance.
							waitThat().timeout(3000);
							fip=null;
						}
					}
				}
				
			}
		}else
		{
			logger.error("fail to click import button");
			//flag=false;
			closeThisPage();
		}
		return fip;
	}
	
}
