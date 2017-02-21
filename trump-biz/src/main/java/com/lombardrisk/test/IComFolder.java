package com.lombardrisk.test;

import org.yiwan.webcore.util.PropHelper;

public interface IComFolder {
	 //String TARGET_FOLDER_TMP =System.getProperty("resultFolder")==null?System.setProperty("resultFolder", PropHelper.getProperty("test.target").trim()):System.getProperty("resultFolder");
	 String TARGET_FOLDER =System.getProperty("resultFolder")==null?PropHelper.getProperty("test.target").trim().replace("\\", "/"):System.getProperty("resultFolder").replace("\\", "/");
	 String SOURCE_FOLDER=PropHelper.getProperty("test.source").trim().replace("\\", "/");
	 String SOURCE_EXPECTATION_FOLDER=SOURCE_FOLDER+"/expectation/";
	 String SOURCE_IMPORT_FOLDER = SOURCE_FOLDER+"/import/";
	 String SOURCE_SCENARIOS_FOLDER=PropHelper.getProperty("test.scenarios").trim().replace("\\", "/")+"/";
	 String SOURCE_LOG_FOLDER=PropHelper.RESULT_FOLDER+PropHelper.LOG_FOLDER;
	 String SOURCE_SCREENSHOT_FOLDER=PropHelper.RESULT_FOLDER+PropHelper.SCREENSHOT_FOLDER;
	  
	 String TARGET_LOG_FOLDER = TARGET_FOLDER+"/"+PropHelper.LOG_FOLDER;
	 String TARGET_SCREENSHOT_FOLDER=TARGET_FOLDER+"/"+PropHelper.SCREENSHOT_FOLDER;
	 String TARGET_SCENARIOS_FOLDER=TARGET_FOLDER+"/scenarios-result/";
	 String TARGET_EXPECTATION_FOLDER = TARGET_FOLDER+"/expectation/";
	 String TARGET_IMPORT_FOLDER = TARGET_FOLDER+"/import/";
	 String TARGET_DOWNLOAD_FOLDER = TARGET_FOLDER+"/download/";
	 String PARAMETER_SCENARIOS_NAME="scenarioName";
	 
}
