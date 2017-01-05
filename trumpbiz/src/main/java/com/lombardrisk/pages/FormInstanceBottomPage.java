package com.lombardrisk.pages;

import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.pojo.Form;

public class FormInstanceBottomPage  extends AbstractPage implements IComFolder,IExecFuncFolder{
	private Form form;
	
	public FormInstanceBottomPage(IWebDriverWrapper webDriverWrapper, Form form) {
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
		flag=element("fidf.bottomPage").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("fidf.close").click();
		loadingDlg();
	}
	
	
	public String exportValidation() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExport("VALIDATION");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTVALIDATION+")/");
		unlockDownloadDir(downloadFolder);
		
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String destFileName=form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
		renameFile(destFileFullPath, destFileName);
		
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	
	public String exportProblems() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExport("PROBLEMS");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTPROBLEMS+")/");
		unlockDownloadDir(downloadFolder);
		
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String destFileName=form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
		renameFile(destFileFullPath, destFileName);
		
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
	private Boolean clickExport(String text) throws Exception
	{
		Boolean flag=true;
		clickLinkText(text);
		loadingDlg();
		if(element("fidf.noRecordsFound").isDisplayed())
		{
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
