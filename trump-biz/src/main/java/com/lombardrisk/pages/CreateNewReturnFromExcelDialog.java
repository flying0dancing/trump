package com.lombardrisk.pages;

import java.io.File;

import com.lombardrisk.commons.FileUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.google.common.base.Strings;
import com.lombardrisk.commons.ExcelUtil;
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
		loadingDlg(null,5);//loadingDlg();
	}

	private String getDownloadFile(String downloadFolderPath,String filterStr){
		String newFileFullPath=null;
		if(new File(downloadFolderPath).exists())
		{
			String newFile=FileUtil.getLatestFile(downloadFolderPath, filterStr,"",-1);
			if(new File(newFile).exists())
			{
				newFileFullPath=newFile;
			}
		}
		return newFileFullPath;
	}

	/**
	 * upload new file, file name according to args1(importFileFullName), and copy new file to import folder with args1(importFileFullName)'s file name.
	 * @param importFileFullName
	 * @return
	 */
	private String findNewFileForUpload(String importFileFullName)
	{
		String newFileFullPath=null;

		String prefixFilter=form.getImportFile().lastIndexOf("(")==-1?form.getImportFile().substring(0, form.getImportFile().lastIndexOf(".")):form.getImportFile().substring(0, form.getImportFile().lastIndexOf("("));
		String filterStr=prefixFilter+"*"+form.getImportFile().substring(form.getImportFile().lastIndexOf("."));

		newFileFullPath=getDownloadFile( TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELNOSCALE+")"+"/", filterStr);
		if(newFileFullPath==null){
			newFileFullPath=getDownloadFile( TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCEL+")"+"/", filterStr);
		}
		if(newFileFullPath==null){
			newFileFullPath=getDownloadFile( TARGET_DOWNLOAD_FOLDER+"/"+form.getRegulator()+"("+EXPORTTOEXCELAPPLYSCALE+")"+"/", filterStr);
		}

		if(newFileFullPath!=null)
		{
			try {
				File file=new File(importFileFullName);
				if(file.exists()){
					logger.info("delete existed older file " + importFileFullName);
					file.delete();
				}
				logger.info("copy file from "+newFileFullPath+" to " + importFileFullName);
				FileUtils.copyFile(new File(newFileFullPath), new File(importFileFullName));

				newFileFullPath=importFileFullName;
			} catch (Exception e) {
				logger.error(e.getMessage());
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
			closeThisPage();
			return "no exist import file";
		}
		String importFileFullName=TARGET_IMPORT_FOLDER+form.getRegulator()+"/"+form.getImportFile();//

		String importFileFullNameFound=findNewFileForUpload(importFileFullName);
		if(importFileFullNameFound!=null && new File(importFileFullNameFound).exists())
		{
			ExcelUtil.updateCellsInExcel(importFileFullNameFound, new String[][]{{"_ReportingDate","-1","-1", form.getProcessDate(),"date:"+dateFormat},{"_EntityCode","-1","-1", getDBInfo().getEntityCode(form.getRegulator(), form.getEntity()),null}});//update process date and entity by form's getProcessDate
			importFileFullName=importFileFullNameFound;
		}else
		{
			if(!new File(importFileFullName).exists())
			{
				closeThisPage();
				return "no exist import file";
			}
		}

		logger.info("Execute js script");
		String js = "document.getElementById('" + type + ":importFileUpload').getElementsByTagName('div')[0].getElementsByTagName('span')[0].className='';";
		executeScript(js);

		element("abstract.importFileUpload_input",type).type(importFileFullName);
		loadingDlg(null,100);
		loadingDlgDis(element("cfed.uploadProgress"),100);
		loadingDlg(null,100);
		errorTxt=uploadFileError(type);
		if(errorTxt!=null)
		{
			logger.info("click upload button(error message):"+errorTxt);
			closeThisPage();
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
			loadingDlg(null,5);
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
			loadingDlg(null,100);//loadingDlg();
			String type2=type.substring(0, type.length()-4);
			IWebElementWrapper replaceconfirm=element("cfed.replaceconfirm",type2);
			if( replaceconfirm.isDisplayed())
			{
				replaceconfirm.click();
				loadingDlg(null,5);//loadingDlg();
			}
			IWebElementWrapper confirmBtn=element("cfed.confirmBtn");
			if(confirmBtn.isDisplayed())
			{
				confirmBtn.click();
				loadingDlg(null,5);//loadingDlg();
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
