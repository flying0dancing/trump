package com.lombardrisk.pages;


import org.yiwan.webcore.web.IWebDriverWrapper;

import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;


public class WorkflowLogDialog extends AbstractPage implements IComFolder,IExecFuncFolder{
		
	
	public WorkflowLogDialog(IWebDriverWrapper webDriverWrapper) {
		super(webDriverWrapper);
		
		
	}
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("wkld.title").isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("wkld.close").click();
		loadingDlg();
	}
	
	public String getUserWhoReadyForApproval() throws Exception
	{
		//TODO need to invoke getCurrentInfo
		return "admin";
	}
	
	/**
	 * get all workflow's log from UI.
	 * @return
	 * @throws Exception
	 */
	public String[][] getCurrentInfo() throws Exception
	{
		String[][] info=null;
		if(element("wkld.noRecordsFound").isDisplayed())
		{
			logger.info("worklog: No records found.");
		}
		else
		{
			int rowNum=element("wkld.dataRow").getNumberOfMatches();
			int columnNum=element("wkld.dataCol").getNumberOfMatches();
			if(rowNum>0 && columnNum>0)
			{
				info=new String[rowNum][columnNum];
				for(int i=0;i<rowNum;i++)
				{
					for(int j=0;j<columnNum;j++)
					{
						info[i][j]=element("wkld.data",String.valueOf(i+1),String.valueOf(j+1)).getInnerText();
					}
				}
			}
		}
		
		
		return info;
	}
	
}
