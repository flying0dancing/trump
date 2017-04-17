package com.lombardrisk.pages;

import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;


public class ExportedFileLocationDialog extends AbstractPage {

	public ExportedFileLocationDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager) {
		super(webDriverWrapper,testDataManager);
		
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("efld.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("efld.ok").click();
		loadingDlg();
	}
	
	public String exportedFileName() throws Exception
	{
		String _exportedFileName=null;
		if(isThisPage())
		{
			IWebElementWrapper _exportFileLoation=element("efld.exportedFilePath");
			if(_exportFileLoation.isPresent())
			{
				String _exportedFilePath=_exportFileLoation.getInnerText();
				if(_exportedFilePath!=null && !_exportedFilePath.trim().equals(""))
				{
					
					int _pathIdentifier1=_exportedFilePath.trim().lastIndexOf("/");
					int _pathIdentifier2=_exportedFilePath.trim().lastIndexOf("\\");
					int _pathIdentifier=_pathIdentifier1;
					if(_pathIdentifier2>=_pathIdentifier1){_pathIdentifier=_pathIdentifier2;}
					
					_exportedFileName=_exportedFilePath.trim().substring(_pathIdentifier+1);
					logger.info("exported file name:"+_exportedFileName);
					
				}
			}else
			{
				logger.info("element \"efld.exportedFilePath\" isn't present.");
			}
			
			closeThisPage();
		}
		return _exportedFileName;
	}
	
	
	
}
