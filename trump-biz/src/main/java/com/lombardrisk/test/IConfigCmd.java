package com.lombardrisk.test;

public interface IConfigCmd {
	String ICC_RESULTFOLDER=System.getProperties().containsKey("resultFolder")?System.getProperty("resultFolder"):null;
	Boolean ICCB_UPDATESOURCE=System.getProperties().containsKey("updateSource");//updateSource,xmlFileName,reportOutput
	Boolean ICCB_RERUN=System.getProperties().containsKey("rerun");

}
