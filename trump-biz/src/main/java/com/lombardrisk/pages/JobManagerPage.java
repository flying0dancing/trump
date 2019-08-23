package com.lombardrisk.pages;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;


/**
 * @author kun shen
 * create at 2016-12-08
 */
public class JobManagerPage extends AbstractPage
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 * @param webDriverWrapper
	 */
	public JobManagerPage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
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
		flag=element("fjmlf.formJobManagerListTable").isPresent();
		
		return flag;
	}

	public void closeThisPage() throws Exception
	{
		element("fjmlf.backToDashBoard").click();
		loadingDlg(null,5);//loadingDlg();
	}
	
	/**
	 * search job status(NULL;SUCCESS,FAILURE:...)
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param started
	 * @return
	 * @throws Exception
	 */
	public String search(String name,String referenceDate,String started) throws Exception
	{
		return getExtendGridCells(name,referenceDate, started,20000);
	}
	/**
	 * search job status(NULL;SUCCESS,FAILURE:...)
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param started
	 * @return
	 * @throws Exception
	 */
	public String search(String name,String referenceDate,String started,long refreshTimeoutMilliseconds) throws Exception
	{
		return getExtendGridCells(name,referenceDate, started,refreshTimeoutMilliseconds);
	}
	/**
	 * search job status(NULL;SUCCESS,FAILURE:...)
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param started
	 * @param refreshTimeoutMilliseconds
	 * @return
	 * @throws Exception
	 */
	private String getExtendGridCells(String name,String referenceDate,String started,long refreshTimeoutMilliseconds) throws Exception
	{
		String status=null;
		IWebElementWrapper gridBarFirstPointer=element("ficmptd.firstPageSta");
		if(gridBarFirstPointer.isPresent())
		{
			if(!gridBarFirstPointer.getAttribute("class").contains("ui-state-disabled"))
			{
				gridBarFirstPointer.click();
				loadingDlg(null,25);//loadingDlg();
			}
			
			status=getGridCells(name,referenceDate,started);
			while(status!=null)
			{
				if((status.startsWith("BLOCK")||status.equalsIgnoreCase("SUCCESS") || status.startsWith("FAILURE")))
				{
					break;
				}
				if(status.equalsIgnoreCase("IN PROGRESS"))
				{
					waitThat().timeout(refreshTimeoutMilliseconds);
					refreshPage();
					status=getGridCells(name,referenceDate,started);
				}
			}
			if(status==null)
			{
				IWebElementWrapper nextPageBar=element("ficmptd.nextPageSta");
				while(nextPageBar.isDisplayed() && !nextPageBar.getAttribute("class").contains("ui-state-disabled"))
				{	
					status=getGridCells(name,referenceDate,started);
					if(status!=null && (status.startsWith("BLOCK")||status.equalsIgnoreCase("SUCCESS") || status.startsWith("FAILURE")))
					{
						break;
					}
					if(status!=null && status.equalsIgnoreCase("IN PROGRESS"))
					{
						waitThat().timeout(refreshTimeoutMilliseconds);
						refreshPage();
						continue;
					}
					nextPageBar.click();
					loadingDlg(null,5);//loadingDlg();
					nextPageBar=element("ficmptd.nextPageSta");
				}
			}
			
		}
		if(status==null)
		{
			status=getGridCells(name,referenceDate,started);
		}
		
		return status;
	}
	
	/**
	 * return the status(FAILURE:***,SUCCESS,IN PROGRESS,null), if status='FAILURE:' and appending status message.
	 * @author kun shen
	 * @param name
	 * @param referenceDate
	 * @param jobStartedDate job start time
	 * @return
	 * @throws Exception
	 */
	private String getGridCells(String name,String referenceDate,String jobStartedDate) throws Exception
	{
		String status=null;
		//String started_StartsWith=started.substring(0, started.length()-2);
		IWebElementWrapper _getstartDate=element("ficmptd.getstartDate",name,referenceDate);
		if(!_getstartDate.isPresent())
		{
			return null;
		}
		String startDate=_getstartDate.getInnerText();
		
		Date __jobStarted=transformStringToDate(jobStartedDate);
		Date __startDate=transformStringToDate(startDate);
		if(__jobStarted!=null && __startDate!=null)
		{
			long startDateL=__startDate.getTime();
			long jobStartedL=__jobStarted.getTime();
			if(startDateL>=jobStartedL)
			{
				IWebElementWrapper _getStatus=element("ficmptd.getStatus",name,referenceDate,startDate);
				if(!_getStatus.isPresent())
				{
					return null;
				}
				status=_getStatus.getInnerText();
				logger.info("current job status:"+status);
			}
		}
		if(status==null){return null;}
		
		if(status.equalsIgnoreCase("FAILURE"))
		{
			IWebElementWrapper _getStatusMsg=element("ficmptd.getStatusMessage",name,referenceDate,startDate);
			if(!_getStatusMsg.isPresent())
			{
				return null;
			}
			status="FAILURE:"+_getStatusMsg.getInnerText();
		}
		if(status.trim().equals(""))
		{
			status=null;
		}
		return status;
	}
	
}
