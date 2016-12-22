package com.lombardrisk.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lombardrisk.pages.ExportToRegulatorDialog;
import com.lombardrisk.pages.IExportTo;
import com.lombardrisk.test.pojo.Form;

public class ExportToFiles {
	private ExportToFiles(){}
	private final static Logger logger = LoggerFactory.getLogger(ExportToFiles.class);
	public static List<String> exportToRegulator(IExportTo exportTo,Form form) 
	{
		List<String> status=new ArrayList<String>();
		ExportToRegulatorDialog exportToRegulator=null;
		try
		{
			String moduleTmp=form.getTransmission().getModule();
			String[] modules=null;
			if(moduleTmp!=null && !moduleTmp.equals(""))
			{
				modules=moduleTmp.split(";");
			}else
			{
				modules=new String[]{moduleTmp};
			}
				
			for(String module:modules)
			{
				exportToRegulator=exportTo.exportToRegulator(form);
				if(exportToRegulator!=null)
				{
					form.getTransmission().setModule(module);
					if(exportToRegulator.selectInfo())
					{
						List<String> exportedFileFullPaths=exportToRegulator.export();//debug changed to List<String> filePaths
						if(exportedFileFullPaths!=null)
						{
							for(String exportedFileFullPath:exportedFileFullPaths)
							{
								if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
								{
									String str=Comparison.compareWithExportedToRegulator(form, exportedFileFullPath);
									status.add(str);
									logger.info(str);
								}else
								{
									status.add("fail: on open not existed file");
									logger.info("fail: on open not existed file");
								}
							}
						}
						else
						{
							String str="error:no exported file.";
							status.add(str);
							logger.info(str);
						}
						
					}else
					{
						status.add("fail: cannot select information in export to regulator dialog");
						logger.info("fail: cannot select information in export to regulator dialog");
					}
					if(exportToRegulator.isThisPage())
					{
						exportToRegulator.closeThisPage();
					}
				}else
				{
					status.add("fail: cannot select and click export to regulator menu list");
					logger.info("fail: cannot select and click export to regulator menu list");
				}
			}
			form.getTransmission().setModule(moduleTmp);
		}catch(Exception e)
		{
			status.add("error:"+e.getMessage());
			logger.info("error:"+e.getMessage());
		}finally
		{
			try
			{
				if(exportToRegulator!=null && exportToRegulator.isThisPage())
				{
					exportToRegulator.closeThisPage();
				}
				
			}catch(Exception e)
			{
				status.add("error:"+e.getMessage());
				logger.info("error:"+e.getMessage());
			}
		}
		
		return status;
	}
	
}
