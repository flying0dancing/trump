package com.lombardrisk.test;

import com.google.common.base.Strings;

public interface IConfigCmd {
	String ICC_RESULTFOLDER=System.getProperties().containsKey("resultFolder")?System.getProperty("resultFolder"):null;
	Boolean ICCB_UPDATESOURCE=System.getProperties().containsKey("updateSource");//updateSource,xmlFileName,reportOutput
	Boolean ICCB_RERUN=System.getProperties().containsKey("rerun");//rerun
	String ICC_RERUNCONTENT=ICCB_RERUN?(Strings.isNullOrEmpty(System.getProperty("rerun"))?"all":System.getProperty("rerun").toLowerCase()):null;

}
