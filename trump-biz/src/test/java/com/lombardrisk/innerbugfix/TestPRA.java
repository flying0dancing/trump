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
		ExcelUtil.UpdateCellsInExcel(importFileFullPath, cells);
		
	}
	
	
}
