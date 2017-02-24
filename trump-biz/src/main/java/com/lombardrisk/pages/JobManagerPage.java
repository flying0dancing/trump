package com.lombardrisk.pages;

import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;


/**
 * @author kun shen
 * create at 2016-12-08
 */
public class JobManagerPage extends AbstractPage
{
	/**
	 * 
	 * @param webDriverWrapper
	 */
	public JobManagerPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
	}

	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("fjmlf.formJobManagerListTable").isPresent();
		
		return flag;
	}

	public void closeThisPage() throws Exception
	{
		element("fjmlf.backToDashBoard").click();
		loadingDlg();
	}
	
	/**
	 * search job status(NULL;SUCCESS,FAILURE:...)
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param runType
	 * @param started
	 * @return
	 * @throws Exception
	 */
	public String search(String name,String referenceDate,String runType,String started) throws Exception
	{
		return getExtendGridCells(name,referenceDate,runType, started);
	}
	
	/**
	 * search job status(NULL;SUCCESS,FAILURE:...)
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param runType
	 * @param started
	 * @return
	 * @throws Exception
	 */
	private String getExtendGridCells(String name,String referenceDate,String runType,String started) throws Exception
	{
		String status=null;
		IWebElementWrapper gridBarFirstPointer=element("ficmptd.firstPageSta");
		if(gridBarFirstPointer.isPresent())
		{
			if(!gridBarFirstPointer.getAttribute("class").contains("ui-state-disabled"))
			{
				gridBarFirstPointer.click();
				loadingDlg();	
			}
			IWebElementWrapper nextPageBar=element("ficmptd.nextPageSta");
			while(nextPageBar.isDisplayed() && !nextPageBar.getAttribute("class").contains("ui-state-disabled"))
			{
				status=getGridCells(name,referenceDate,runType,started);
				if(status!=null && (status.equalsIgnoreCase("SUCCESS") || status.startsWith("FAILURE")))
				{
					break;
				}
				nextPageBar.click();
				loadingDlg();
				nextPageBar=element("ficmptd.nextPageSta");
			}
		}
		if(status==null)
		{
			status=getGridCells(name,referenceDate,runType,started);
		}
		
		return status;
	}
	
	/**
	 * return the status(FAILURE:***,SUCCESS,IN PROGRESS,null), if status='FAILURE:' and appending status message.
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param runType
	 * @param started
	 * @return
	 * @throws Exception
	 */
	private String getGridCells(String name,String referenceDate,String runType,String started) throws Exception
	{
		String started_StartsWith=started.substring(0, started.length()-1);
		String status=element("ficmptd.getStatus",name,referenceDate,runType,started_StartsWith).getInnerText();
		if(status!=null && status.equalsIgnoreCase("FAILURE"))
		{
			status="FAILURE:"+element("ficmptd.getStatusMessage",name,referenceDate,runType,started_StartsWith).getInnerText();
		}
		if(status!=null && status.trim().equals(""))
		{
			status=null;
		}
		return status;
	}
	
}
