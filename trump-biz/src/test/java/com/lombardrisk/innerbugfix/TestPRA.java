package com.lombardrisk.innerbugfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.pojo.Form;

public class TestPRA implements IExecFuncFolder{
	private final static Logger logger = LoggerFactory.getLogger(TestPRA.class);
	
	@Deprecated
	//@Test
	public void updateCellValue_PRA() throws Exception
	{
		logger.info("testing update excel cell in return PRA FRB002");
			
		String importFileFullPath="Z:\\ProductLine\\PRA\\Test Results\\1.2.0\\Auto\\PRA1.2.0.1_AR1.16.0b71\\import\\Prudential Regulation Authority\\PRA_3000_RFB001_v1_20190930.xlsx";
		String[][] cells=new String[][]{{"_ReportingDate","-1","-1", "31/12/2019","date:en_GB"},{"_EntityCode","-1","-1", "3000",null}};
		ExcelUtil.updateCellsInExcel(importFileFullPath, cells);
		
	}
	
	@Deprecated
	//@Test
	public void uiDisplay_excelCellStyleswasexceeded()
	{
		
		logger.info("testing excel Cell Styles was exceeded in return PRA PRA110 v1");
		Form form=new Form();
		form.setRegulator("Prudential Regulation Authority");
		form.setExpectationFile("PRA110_v1_ARDisplay.xlsx");
		String exportedFileFullPath="Z:\\APAutomation\\results\\download\\Prudential Regulation Authority(UIDisplay)\\PRA110_v1_3000_30092019(2).csv";
		int indexOfCellName=0;
		int indexOfRowId=1;
		int indexOfInstance=2;
		int indexOfExpectedValue=4;
		int indexOfActualValue=5;
		int indexOfTestResult=8;
		
		String a=Comparison.compareUIDisplayFile( form, exportedFileFullPath, indexOfCellName, indexOfRowId, indexOfInstance, indexOfExpectedValue, indexOfActualValue, indexOfTestResult);
		logger.info(a);
		
	}
	
	@Deprecated
	//@Test
	public void csvTotalResultIssue() throws Exception
	{
		logger.info("testing comma issue on PRA PRA110 v1");
		String returnStatus="";
		Form form=new Form();
		form.setRegulator("Prudential Regulation Authority");
		form.setExpectationFile("PRA110_V1.csv");
		String exportedFileFullPath="Z:\\APAutomation\\results\\download\\Prudential Regulation Authority(ExportToCSVNoScale)\\PRA_3000_PRA110_v1_20190930(2).csv";
		returnStatus=Comparison.compareWithExportedCSV(form, exportedFileFullPath,"ExportTocsvNoScale");
		Assert.assertEquals(returnStatus.substring(0,4), "pass");
	}
	
}
