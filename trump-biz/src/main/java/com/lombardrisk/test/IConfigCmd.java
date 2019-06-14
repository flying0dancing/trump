package com.lombardrisk.test;

import com.google.common.base.Strings;

public interface IConfigCmd {
	//adding for ci
	String ICC_TARGET_PATH=System.getProperties().containsKey("test.target")?System.getProperty("test.target").replace("\\", "/"):null;
	String ICC_SOURCE_POOL=System.getProperties().containsKey("test.source")?System.getProperty("test.source").replace("\\", "/"):null;
	String ICC_SCENARIOS_POOL=System.getProperties().containsKey("test.scenarios")?System.getProperty("test.scenarios").replace("\\", "/"):null;


	String ICC_MAIL_HOST=System.getProperties().containsKey("mail.host")?System.getProperty("mail.host"):null;
	String ICC_MAIL_PORT=System.getProperties().containsKey("mail.port")?System.getProperty("mail.port"):null;
	String ICC_MAIL_SENDER_ADDRESS=System.getProperties().containsKey("mail.sender.address")?System.getProperty("mail.sender.address"):null;
	String ICC_MAIL_SENDER_PASSWORD=System.getProperties().containsKey("mail.sender.password")?System.getProperty("mail.sender.password"):null;
	String ICC_MAIL_RECEIVER_TO=System.getProperties().containsKey("mail.receiver.to.list")?System.getProperty("mail.receiver.to.list"):null;
	String ICC_MAIL_RECEIVER_CC=System.getProperties().containsKey("mail.receiver.cc.list")?System.getProperty("mail.receiver.cc.list"):null;
	String ICC_MAIL_SUBJECT_PREFIX=System.getProperties().containsKey("mail.subject.prefix")?System.getProperty("mail.subject.prefix"):null;


	//base ones
	String ICC_RESULTFOLDER=ICC_TARGET_PATH==null?System.getProperties().containsKey("resultFolder")?System.getProperty("resultFolder"):null:ICC_TARGET_PATH;
	Boolean ICCB_UPDATESOURCE=System.getProperties().containsKey("updateSource");//updateSource,xmlFileName,reportOutput
	Boolean ICCB_RERUN=System.getProperties().containsKey("rerun");//rerun
	String ICC_RERUNCONTENT=ICCB_RERUN?(Strings.isNullOrEmpty(System.getProperty("rerun"))?"all":System.getProperty("rerun").toLowerCase()):null;



}
