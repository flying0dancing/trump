package com.lombardrisk.test;

import com.google.common.base.Strings;

public interface IConfigCmd {
	String ICC_SOURCEFOLDER=System.getProperties().containsKey("srcFolder")?System.getProperty("srcFolder"):null;

	String ICC_RESULTFOLDER=System.getProperties().containsKey("resultFolder")?System.getProperty("resultFolder"):null;
	Boolean ICCB_UPDATESOURCE=System.getProperties().containsKey("updateSource");//updateSource,xmlFileName,reportOutput
	Boolean ICCB_RERUN=System.getProperties().containsKey("rerun");//rerun
	String ICC_RERUNCONTENT=ICCB_RERUN?(Strings.isNullOrEmpty(System.getProperty("rerun"))?"all":System.getProperty("rerun").toLowerCase()):null;

	Boolean ICCB_SCENARIO_XML=System.getProperties().containsKey("xmlFileName");
	String ICC_SCENARIO_XML=ICCB_SCENARIO_XML?System.getProperty("xmlFileName").replace("\\", "/"):"";
	Integer ICC_SCENARIO_INDEX=ICC_SCENARIO_XML.lastIndexOf('/');
	String ICC_SCENARIOSFOLDER=ICC_SCENARIO_XML.substring(0,ICC_SCENARIO_INDEX+1);

	Boolean ICCB_GOON=System.getProperties().containsKey("go");//go

}
