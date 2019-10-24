package com.lombardrisk.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.pojo.Form;
import com.lombardrisk.commons.FileUtil;

public class FormInstanceBottomPage  extends AbstractPage implements IComFolder,IExecFuncFolder{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	public FormInstanceBottomPage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form) {
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
		flag=element("fidf.bottomPage").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("fidf.close").click();
		loadingDlg(null,5);//loadingDlg();
	}
	
	/** click "validation" link in left panel and then export/download validation rules.<br> If validation rules doesn't exist, return null.<br> If validation rules exists, download it and rename it with form's name_version_entity_processdate
	 * <br>close this bottom page before return values.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportValidation() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("VALIDATION");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			closeThisPage();//close bottom page after export.
			return null;
		}
		//String sourceFileFullPath=exportToFile();
		String sourceFileFullPath=downloadFile(System.getProperty("user.home")+System.getProperty("file.separator")+"downloads"+System.getProperty("file.separator"));//update for agile reporter v1.16.2
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTVALIDATION+")/");
		unlockDownloadDir(downloadFolder);
		
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String destFileName=form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
		String tmp="_"+form.getName()+"_";
		if(!destFileFullPath.contains(tmp)){//add for agile reporter v1.16.2;agile reporter v1.16.2 can download files with names.
			destFileFullPath=FileUtil.renameFile(destFileFullPath, destFileName);
		}
		
		
		closeThisPage();//close bottom page after export.
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	
	/** click "problems" link in left panel and then export/download validation rules.<br> If problems rules doesn't exist, return null.<br> If problems rules exists, download it and rename it with form's name_version_entity_processdate
	 * <br>close this bottom page before return values.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportProblems() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("PROBLEMS");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			closeThisPage();//close bottom page after export.
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTPROBLEMS+")/");
		unlockDownloadDir(downloadFolder);
		
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String destFileName=form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
		destFileFullPath=FileUtil.renameFile(destFileFullPath, destFileName);
		
		closeThisPage();//close bottom page after export.
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	
	/**
	 * click link text in left panel, ADJUSTMENTS/VALIDATION/PROBLEMS/ALLOCATIONS
	 * @param text
	 * @throws Exception
	 */
	private void clickLinkText(String text) throws Exception
	{
		loadingDlg(null,5);//loadingDlg(3000);
		element("fidf.linkText",text).click();
		loadingDlg(null,5);//loadingDlg();
	}
	
	/** click "export" button at the bottom page of form list page. return false and don't click "export" button if no records are found.
	 * @author kun shen
	 * @param text
	 * @return
	 * @throws Exception
	 */
	private Boolean clickExportToFile(String text) throws Exception
	{
		Boolean flag=true;
		clickLinkText(text);
		loadingDlg(null,5);//loadingDlg(3000);
		//logger.info("\"level\" select all \"Status\"");
		String js = "document.getElementById('formInstDetailFooterTabView:validationForm:result').getElementsByTagName('option')[0].click();";
		executeScript(js);
		if(element("fidf.valResultStatus").isDisplayed()){//add in agile reporter version 1.16.2, for select level=Status
			//selectIt(element("fidf.valResultStatus"),"Status");//<Kun:this function not work on this element, why> use next one instead
			logger.info("\"level\" select all \"Status\"");
			selectIt(element("fidf.valResultStatus"),"Status");
		}
		
		if(element("fidf.noRecordsFound").isDisplayed())
		{
			logger.error("error: no records found to export.");
			flag=false;
		}else
		{
			if (PropHelper.ENABLE_FILE_DOWNLOAD)
			{
				TestCaseManager.getTestCase().startTransaction("");
				TestCaseManager.getTestCase().setPrepareToDownload(true);
				clickExportImg();
				TestCaseManager.getTestCase().stopTransaction();
			}
			else
			{
				clickExportImg();
			}
		}
		return flag;
	}

	private void clickExportImg() throws Exception {
		try{
			element("fidf.export","a").click();
		}catch (Exception e){
			element("fidf.export","button").click();//for rules larger than 1000
			loadingDlg(null,5);
			element("vewd.export").click();
			loadingDlg(null,3000);
		}
		loadingDlg(null,5);
	}
}
