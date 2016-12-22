package com.lombardrisk.pages;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

/**
 * Created by Leo Tu on 1/25/16
 */
public class AllocationPage extends AbstractPage
{

	public AllocationPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);

	}

	/**
	 * get
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSumCellValue() throws Exception
	{
		return element("ac.sumValue").getInnerText();
	}

	/**
	 * get sum rule expression
	 * 
	 * @return rule expression
	 * @throws Exception
	 */
	public String getSumRule() throws Exception
	{
		return element("ac.sumRule").getInnerText();
	}

	/**
	 * verify if exits sub item value
	 * 
	 * @param cellName
	 * @param cellValue
	 * @return true or false
	 * @throws Exception
	 */
	public boolean isSubItemValueExist(String cellName, String cellValue) throws Exception
	{
		boolean rst = false;
		int amt = (int) element("ac.allocation").getRowCount();
		for (int i = 0; i <= amt; i++)
		{
			if (element("ac.subCellName", String.valueOf(i)).getInnerText().equalsIgnoreCase(cellName))
			{
				if (element("ac.subCellValue", String.valueOf(i)).getInnerText().equalsIgnoreCase(cellValue))
				{
					rst = true;
					break;
				}
			}

		}
		return rst;
	}

	/**
	 * verify if exist item
	 * 
	 * @param CALCULATED
	 * @param CUSTOMER_CODE
	 * @param DRILL_REF
	 * @return true or false
	 * @throws Exception
	 */
	public boolean isAllocationItemValueExist(String CALCULATED, String CUSTOMER_CODE, String DRILL_REF) throws Exception
	{
		boolean find = false;
		int amt = (int) element("ac.allocationLeft").getRowCount();
		if (!element("ac.firstPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("ac.firstPage").click();
			waitStatusDlg();
		}
		boolean flag = true;
		while (flag)
		{

			for (int i = 1; i <= amt; i++)
			{
				if (element("ac.calculated", String.valueOf(i)).getInnerText().equals(CALCULATED))
				{
					if (element("ac.customerCode", String.valueOf(i)).getInnerText().equals(CUSTOMER_CODE))
					{
						if (element("ac.drillRef", String.valueOf(i)).getInnerText().equals(DRILL_REF))
						{
							find = true;
							flag = false;
							break;
						}
					}
				}
			}

			if (!find)
			{
				if (!element("ac.nextPage").getAttribute("class").contains("ui-state-disabled"))
				{
					element("ac.nextPage").click();
					waitStatusDlg();
					amt = (int) element("ac.allocationLeft").getRowCount();
				}
				else
				{
					flag = false;
				}
			}

		}
		return find;
	}

	/**
	 * export allocation
	 * 
	 * @return exported file
	 * @throws Exception
	 */
	public String exportAllocation() throws Exception
	{
		Thread.sleep(3000);
		waitStatusDlg();
		if (httpDownload)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("ac.export").click();
			TestCaseManager.getTestCase().stopTransaction();
			element("fp.hidDrillDownTable").click();
			waitStatusDlg();
			String exportedFile = TestCaseManager.getTestCase().getDownloadFile();
			String oldName = new File(exportedFile).getName();
			String path = new File(exportedFile).getAbsolutePath().replace(oldName, "");
			String fileName = TestCaseManager.getTestCase().getDefaultDownloadFileName();
			renameFile(path, oldName, fileName);
			return path + fileName;
		}
		else
		{
			String dir = FileUtils.getUserDirectoryPath() + "\\downloads";
			String latestFile = getLatestFile(dir);
			element("ac.export").click();
			waitStatusDlg();
			element("fp.hidDrillDownTable").click();
			waitStatusDlg();
			return downloadFile(null, latestFile, null);
		}

	}

	/**
	 * click cell link
	 * 
	 * @param cellName
	 * @throws Exception
	 */
	public void clickCellLink(String cellName) throws Exception
	{
		waitStatusDlg();
		element("ac.cellLink", cellName).click();
		waitStatusDlg();
		Thread.sleep(1000);
	}

}
