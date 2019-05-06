package com.lombardrisk.test;

import org.yiwan.webcore.util.PropHelper;

public interface IComFolder extends IConfigCmd{
	 //String TARGET_FOLDER =System.getProperty("resultFolder")==null?PropHelper.getProperty("test.target").trim().replace("\\", "/"):System.getProperty("resultFolder").replace("\\", "/");
	 String TARGET_FOLDER =ICC_RESULTFOLDER==null?PropHelper.getProperty("test.target").trim().replace("\\", "/"):ICC_RESULTFOLDER.replace("\\", "/");
	 String SOURCE_FOLDER=ICC_SOURCE_POOL==null?PropHelper.getProperty("test.source").trim().replace("\\", "/"):ICC_SOURCE_POOL;
	 String SOURCE_EXPECTATION_FOLDER=SOURCE_FOLDER+"/expectation/";
	 String SOURCE_IMPORT_FOLDER = SOURCE_FOLDER+"/import/";
	 String SOURCE_SCENARIOS_FOLDER=ICC_SCENARIOS_POOL==null?PropHelper.getProperty("test.scenarios").trim().replace("\\", "/")+"/":ICC_SCENARIOS_POOL;
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

	 //mail
     String MAIL_HOST=ICC_MAIL_HOST==null?PropHelper.getProperty("mail.host")==null?PropHelper.getProperty("mail.host"):"10.17.3.150":ICC_MAIL_HOST;
     String MAIL_PORT=ICC_MAIL_PORT==null?PropHelper.getProperty("mail.port")==null?PropHelper.getProperty("mail.port"):"25":ICC_MAIL_PORT;
     String MAIL_SENDER_NAME=PropHelper.getProperty("mail.sender.name");
     String MAIL_SENDER_ADDRESS=ICC_MAIL_SENDER_ADDRESS==null?PropHelper.getProperty("mail.sender.address")==null?PropHelper.getProperty("mail.sender.address"):"Prod.Auto@lombardrisk.com":ICC_MAIL_SENDER_ADDRESS;
     String MAIL_SENDER_PASSWORD=ICC_MAIL_SENDER_PASSWORD==null?PropHelper.getProperty("mail.sender.password")==null?PropHelper.getProperty("mail.sender.password"):"Password1":ICC_MAIL_SENDER_PASSWORD;
     String MAIL_RECEIVER_TO=ICC_MAIL_RECEIVER_TO==null?PropHelper.getProperty("mail.receiver.to.list")==null?PropHelper.getProperty("mail.receiver.to.list"):null:ICC_MAIL_RECEIVER_TO;
     String MAIL_RECEIVER_CC=ICC_MAIL_RECEIVER_CC==null?PropHelper.getProperty("mail.receiver.cc.list")==null?PropHelper.getProperty("mail.receiver.cc.list"):null:ICC_MAIL_RECEIVER_CC;
     String MAIL_RECEIVER_BCC=PropHelper.getProperty("mail.receiver.bcc.list");
     String MAIL_SUBJECT_PREFIX=ICC_MAIL_SUBJECT_PREFIX==null?PropHelper.getProperty("mail.subject.prefix")==null?PropHelper.getProperty("mail.subject.prefix"):"trump:":ICC_MAIL_SUBJECT_PREFIX;
     String MAIL_MSG_TEXT=PropHelper.getProperty("mail.msg.text");

}