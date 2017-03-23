package com.lombardrisk.pages;

import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.pojo.Form;

public class FormInstanceBottomPage  extends AbstractPage implements IComFolder,IExecFuncFolder{
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
		loadingDlg();
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
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTVALIDATION+")/");
		unlockDownloadDir(downloadFolder);
		
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String destFileName=form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
		destFileFullPath=renameFile(destFileFullPath, destFileName);
		
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
		destFileFullPath=renameFile(destFileFullPath, destFileName);
		
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
		element("fidf.linkText",text).click();
		loadingDlg();
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
		loadingDlg();
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
				element("fidf.export").click();
				loadingDlg();
				TestCaseManager.getTestCase().stopTransaction();
			}
			else
			{
				element("fidf.export").click();
				loadingDlg();
			}
		}
		return flag;
	}
}
