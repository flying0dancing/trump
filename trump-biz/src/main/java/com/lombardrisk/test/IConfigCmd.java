package com.lombardrisk.test;

import com.google.common.base.Strings;

public interface IConfigCmd {
	String ICC_RESULTFOLDER=System.getProperties().containsKey("resultFolder")?System.getProperty("resultFolder"):null;
	Boolean ICCB_UPDATESOURCE=System.getProperties().containsKey("updateSource");//updateSource,xmlFileName,reportOutput
	Boolean ICCB_RERUN=System.getProperties().containsKey("rerun");//rerun
	String ICC_RERUNCONTENT=ICCB_RERUN?(Strings.isNullOrEmpty(System.getProperty("rerun"))?"all":System.getProperty("rerun").toLowerCase()):null;


	//adding for ci
	String ICC_TESTQA_SOURCE_POOL=System.getProperties().containsKey("testqa.source.pool")?System.getProperty("testqa.source.pool").replace("\\", "/"):null;
	String ICC_TESTQA_SCENARIOS_POOL=System.getProperties().containsKey("testqa.scenarios.pool")?System.getProperty("testqa.scenarios.pool").replace("\\", "/"):null;
	String ICC_TESTQA_TARGET_PATH=System.getProperties().containsKey("testqa.target.path")?System.getProperty("testqa.target.path").replace("\\", "/"):null;

	String ICC_MAIL_HOSTNAME=System.getProperties().containsKey("mail.hostname")?System.getProperty("mail.hostname").replace("\\", "/"):null;
	String ICC_MAIL_PORT=System.getProperties().containsKey("mail.port")?System.getProperty("mail.port").replace("\\", "/"):null;
	String ICC_MAIL_SENDERADDRESS=System.getProperties().containsKey("mail.senderAddress")?System.getProperty("mail.senderAddress").replace("\\", "/"):null;
	String ICC_MAIL_SENDERPASSWORD=System.getProperties().containsKey("mail.senderPassword")?System.getProperty("mail.senderPassword").replace("\\", "/"):null;
	String ICC_MAIL_RECEIVER_TOLIST=System.getProperties().containsKey("mail.receiver.toList")?System.getProperty("mail.receiver.toList").replace("\\", "/"):null;
	String ICC_MAIL_RECEIVER_CCLIST=System.getProperties().containsKey("mail.receiver.ccList")?System.getProperty("mail.receiver.ccList").replace("\\", "/"):null;
	String ICC_MAIL_SUBJECT_PREFIX=System.getProperties().containsKey("mail.subject.prefix")?System.getProperty("mail.subject.prefix").replace("\\", "/"):null;

}
