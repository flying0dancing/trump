package com.lombardrisk.test;

import org.yiwan.webcore.util.PropHelper;

public interface IComFolder extends IConfigCmd{
	 //String TARGET_FOLDER =System.getProperty("resultFolder")==null?PropHelper.getProperty("test.target").trim().replace("\\", "/"):System.getProperty("resultFolder").replace("\\", "/");
	 String TARGET_FOLDER =ICC_RESULTFOLDER==null?PropHelper.getProperty("test.target").trim().replace("\\", "/"):ICC_RESULTFOLDER.replace("\\", "/");
	 String SOURCE_FOLDER=ICC_SOURCEFOLDER==null?PropHelper.getProperty("test.source").trim().replace("\\", "/"):ICC_SOURCEFOLDER.replace("\\", "/");

	 String SOURCE_EXPECTATION_FOLDER=SOURCE_FOLDER+"/expectation/";
	 String SOURCE_IMPORT_FOLDER = SOURCE_FOLDER+"/import/";
	 String SOURCE_SCENARIOS_FOLDER=ICC_SCENARIOSFOLDER.equals("")?PropHelper.getProperty("test.scenarios").trim().replace("\\", "/")+"/":ICC_SCENARIOSFOLDER;
	 String SOURCE_LOG_FOLDER=PropHelper.RESULT_FOLDER+PropHelper.LOG_FOLDER;
	 String SOURCE_SCREENSHOT_FOLDER=PropHelper.RESULT_FOLDER+PropHelper.SCREENSHOT_FOLDER;
	  
	 String TARGET_LOG_FOLDER = TARGET_FOLDER+"/"+PropHelper.LOG_FOLDER;
	 String TARGET_SCREENSHOT_FOLDER=TARGET_FOLDER+"/"+PropHelper.SCREENSHOT_FOLDER;
	 String TARGET_SCENARIOS_FOLDER=TARGET_FOLDER+"/scenarios-result/";
	 String TARGET_EXPECTATION_FOLDER = TARGET_FOLDER+"/expectation/";
	 String TARGET_IMPORT_FOLDER = TARGET_FOLDER+"/import/";
	 String TARGET_DOWNLOAD_FOLDER = TARGET_FOLDER+"/download/";
	 
	 String PARAMETER_SCENARIOS_NAME="scenarioName";
	 String PARAMETER_SCENARIOS_SHEET="scenarioSheet";
	 
	 String SOURCE_TRANSMISSION_PROPERTY=".transmissionProperties.csv";
}
