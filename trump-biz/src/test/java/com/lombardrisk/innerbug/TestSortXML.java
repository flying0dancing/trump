package com.lombardrisk.innerbug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yiwan.webcore.util.PropHelper;

import com.lombardrisk.commons.Dom4jUtil;
import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.IExecFuncFolder;

public class TestSortXML implements IExecFuncFolder{
	private final static Logger logger = LoggerFactory.getLogger(TestSortXML.class);
	
	@Test
	public void checkFormExist() throws Exception
	{
		logger.info("testing FFIEC030S for solving same element sorted issue, sorted by first attribute's value if element are same");
		String fileFullName="Z:\\ProductLine\\FED\\TestResults\\FED_1.14.2\\Auto\\1.14.2_AR1.16.0\\download\\US FED Reserve(ExportToRegulator)\\FED_2999_RepCentral-FFIEC030S_V1_20160331.XML";
		List<String> ignoreAttributes=new ArrayList<String>();
		ignoreAttributes.add("contextRef");
		ignoreAttributes.add("schemaRef");
		List<String> ignoreElements=new ArrayList<String>();
		ignoreElements.add("xml");
		String newFilePath="E:\\abc\\";
		String sortFileFullName=Dom4jUtil.sortXmlContentToNewFileByName(fileFullName,ignoreAttributes, ignoreElements,newFilePath);
		
		String expectedFile="Z:\\ProductLine\\FED\\TestResults\\FED_1.14.2\\Auto\\1.14.2_AR1.16.0\\expectation\\US FED Reserve\\FED_2999_RepCentral-FFIEC030S_V1_20181231.XML";
		String sorted_expectedFileFullName=Dom4jUtil.sortXmlContentToNewFileByName(expectedFile,ignoreAttributes, ignoreElements,newFilePath);
		String path_BComp=new File(System.getProperty("user.dir")).getParent().replace("\\", "/").replace("/", System.getProperty("file.separator"))+PropHelper.getProperty("path.BComp").replace("..", "").replace("\\", "/").replace("/", System.getProperty("file.separator"));
		String cmdLine="\""+path_BComp+"GenerateReport.bat\" "+"\"" + sortFileFullName.replace("\\", "/").replace("/", System.getProperty("file.separator")) + "\" "+"\"" + sorted_expectedFileFullName + "\" "+ "\""+"E:\\abc\\test.report.html"+"\"";
		String returnStatus=Comparison.getReturnStatus(cmdLine);
		Assert.assertEquals(returnStatus.substring(0,4), "pass");
	}
}
