package com.lombardrisk.innerbugfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.pojo.Form;

public class TestSRDD implements IExecFuncFolder{
	private final static Logger logger = LoggerFactory.getLogger(TestSRDD.class);
	
	@Deprecated
	//@Test
	public void csvCommaIssue_SRDD() throws Exception
	{
		logger.info("testing comma issue on CI_v1");
		String returnStatus="";
		Form form=new Form();
		form.setRegulator("Stats and Regulatory Data Div");
		form.setExpectationFile("C1_V1.csv");
		String exportedFileFullPath="Z:\\APAutomation\\results\\download\\Stats and Regulatory Data Div(ExportToCSV)\\MFSD_3999_C1_v1_20170701.csv";
		returnStatus=Comparison.compareWithExportedCSV(form, exportedFileFullPath,"ExportToSCV");
		Assert.assertEquals(returnStatus.substring(0,4), "pass");
	}
	
	@Deprecated
	//@Test
	public void csvCommaIssue_MAS() throws Exception
	{
		logger.info("testing comma issue on AMR2011W_v1");
		String returnStatus="";
		Form form=new Form();
		form.setRegulator("Monetary Authority of Singapore");
		form.setExpectationFile("AMR2011W_V1_0001_20190731.csv");
		String exportedFileFullPath="Z:\\APAutomation\\results\\download\\Monetary Authority of Singapore(ExportToCSV)\\MAS_0002_AMR2011W_v1_20190731.csv";
		returnStatus=Comparison.compareWithExportedCSV(form, exportedFileFullPath,"ExportToSCV");
		Assert.assertEquals(returnStatus.substring(0,4), "pass");
	}
}
