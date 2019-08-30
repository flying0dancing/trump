package com.lombardrisk.testcase;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.ExportToFiles;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class ExportToRegulator extends TestManager implements IExecFuncFolder{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**
	 * click export to regulator button in opened form instance, and then export download file, and then store and uncompress files at <i>result</i>\download\<i>regulator</i>(exportToRegulator).<br><br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, Transmission.fileType, Transmission.module, expectationFile<br>
	 * scenario file optional columns: Transmission.fileType, Transmission.framework, Transmission.taxonomy, Transmission.compressType, expiration<br>
	 * when Transmission.module are contains many modules, Transmission.fileType is essential.<br>
	 * <p><b>notes:</b> return "PRA110" cannot be worked in this function(only export to xbrl)</p>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkInForm(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			FormInstancePage formInstancePage=null;
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					if(listPage.selectFormInfo(form))
					{
						formInstancePage=listPage.openFormInstance(form);
						if(formInstancePage!=null)
						{
							/*int failCount=formInstancePage.validationNow();
							if(failCount>0)
							{
								form.setExecutionStatus("fail on having "+failCount+" validation failures");
							}else
							{	
							}*/
							List<String> status=ExportToFiles.exportToRegulator(formInstancePage, form);
							/*if(formInstancePage.isThisPage())
							{
								formInstancePage.unlockForm();
							}else
							{
								formInstancePage=listPage.openFormInstance(form);
								formInstancePage.unlockForm();
							}*/
							if(status.size()==1)
							{
								form.setExecutionStatus(status.get(0));
							}else
							{
								String s="";
								Boolean totalStatus=true;
								for(String t:status)
								{
									if(t.toLowerCase().startsWith("pass")){}else{totalStatus=false;}
									s=s+t+System.getProperty("line.separator");
								}
								if(totalStatus)
								{
									form.setExecutionStatus(s);
								}else
								{
									form.setExecutionStatus("fail:"+s);
								}
							}
							
						}else
						{
							form.setExecutionStatus("fail on open form instance in list page");
						}
					}else
					{
						form.setExecutionStatus("fail on select form in list page");
					}
					
				}else
				{
					form.setExecutionStatus("fail: cannot get list page");
				}
			
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			finally
			{
				try
				{
					if(formInstancePage!=null && formInstancePage.isThisPage())
					{
						formInstancePage.closeThisPage();
					}
				}catch(Exception e)
				{
					logger.error(e.getMessage());
					form.setExecutionStatus("error:"+e.getMessage());
				}
				
			}
		}
		logger.info("[method]checkInForm "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**
	 * click export to regulator button in dashboard, and then export download file, and then store and uncompress files at <i>result</i>\download\<i>regulator</i>(exportToRegulator).<br><br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, Transmission.fileType, Transmission.module, expectationFile<br>
	 * scenario file optional columns: Transmission.fileType, Transmission.framework, Transmission.taxonomy, Transmission.compressType, expiration<br>
	 * when Transmission.module are contains many modules, Transmission.fileType is essential.<br>
	 * <p><b>notes:</b> return "PRA110" cannot be worked in this function(only export to xbrl)</p>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkInDashBoard(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					List<String> status=ExportToFiles.exportToRegulator(listPage,form);
					/*String s="";
					for(String t:status)
					{
						s=s+t+System.getProperty("line.separator");
					}
					form.setExecutionStatus(s);*/
					if(status.size()==1)
					{
						form.setExecutionStatus(status.get(0));
					}else
					{
						String s="";
						Boolean totalStatus=true;
						for(String t:status)
						{
							if(t.toLowerCase().startsWith("pass")){}else{totalStatus=false;}
							s=s+t+System.getProperty("line.separator");
						}
						if(totalStatus)
						{
							form.setExecutionStatus(s);
						}else
						{
							form.setExecutionStatus("fail:"+s);
						}
					}
				}else
				{
					form.setExecutionStatus("fail: cannot get list page");
				}
			
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			
		}
		logger.info("[method]checkInDashBoard "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**
	 * click export to regulator button in opened form instance, and then export download file(lock form before, and then unlock form), and then store and uncompress files at <i>result</i>\download\<i>regulator</i>(exportToRegulator).<br><br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, Transmission.fileType, Transmission.module, expectationFile<br>
	 * scenario file optional columns: Transmission.fileType, Transmission.framework, Transmission.taxonomy, Transmission.compressType, expiration<br>
	 * <b>special instruction</b>: when Transmission.module are contains many modules, Transmission.fileType is essential. more files in expectationFile are joined by semicolon(;), the name of return FRY2052A's expected files have to end with COMMENT.xml or combine.xml, the name of other returns's expected files have to contains <i>returnName_returnVersion</i>.
	 * <p><b>notes:</b> return "PRA110" cannot be worked in this function(only export to xbrl)</p>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkInFormWithLockVal(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			FormInstancePage formInstancePage=null;
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					if(listPage.selectFormInfo(form))
					{
						formInstancePage=listPage.openFormInstance(form);
						if(formInstancePage!=null)
						{
							int failCount=formInstancePage.validationNow();
							String failValidation=""; 
							if(failCount>0)
							{
								failValidation="-- Having "+failCount+" validation failures";
							}else
							{}
							formInstancePage.lockForm();//lockform
							List<String> status=ExportToFiles.exportToRegulator(formInstancePage, form);
							/*if(formInstancePage.isThisPage())
							{
								formInstancePage.unlockForm();
							}else
							{
								formInstancePage=listPage.openFormInstance(form);
								formInstancePage.unlockForm();
							}*/
							if(status.size()==1)
							{
								form.setExecutionStatus(status.get(0));
							}else
							{
								String s="";
								Boolean totalStatus=true;
								for(String t:status)
								{
									if(t.toLowerCase().startsWith("pass")){}else{totalStatus=false;}
									s=s+t+System.getProperty("line.separator");
								}
								s=s+failValidation;
								if(totalStatus)
								{
									form.setExecutionStatus(s);
								}else
								{
									form.setExecutionStatus("fail:"+s);
								}
							}
							
						}else
						{
							form.setExecutionStatus("fail on open form instance in list page");
						}
					}else
					{
						form.setExecutionStatus("fail on select form in list page");
					}
					
				}else
				{
					form.setExecutionStatus("fail: cannot get list page");
				}
			
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			finally
			{
				try
				{
					if(formInstancePage!=null && formInstancePage.isThisPage())
					{
						formInstancePage.closeThisPage();
					}
				}catch(Exception e)
				{
					logger.error(e.getMessage());
					form.setExecutionStatus("error:"+e.getMessage());
				}
				
			}
		}
		logger.info("[method]checkInFormWithLockVal "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**
	 * click export to regulator button in opened form instance, and then click force Submit button.<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run<br>
	 * scenario file optional columns: expiration<br>
	 * @since 2018/9/18
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkDirectSubmit(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			FormInstancePage formInstancePage=null;
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					if(listPage.selectFormInfo(form))
					{
						formInstancePage=listPage.openFormInstance(form);
						if(formInstancePage!=null)
						{
							List<String> status=ExportToFiles.exportToRegulator_DirectSubmit(formInstancePage, form);
							
							if(status.size()==1)
							{
								form.setExecutionStatus(status.get(0));
							}else
							{
								String s="";
								Boolean totalStatus=true;
								for(String t:status)
								{
									if(t.toLowerCase().startsWith("pass")){}else{totalStatus=false;}
									s=s+t+System.getProperty("line.separator");
								}
								if(totalStatus)
								{
									form.setExecutionStatus(s);
								}else
								{
									form.setExecutionStatus("fail:"+s);
								}
							}
							
						}else
						{
							form.setExecutionStatus("fail on open form instance in list page");
						}
					}else
					{
						form.setExecutionStatus("fail on select form in list page");
					}
					
				}else
				{
					form.setExecutionStatus("fail: cannot get list page");
				}
			
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			finally
			{
				try
				{
					if(formInstancePage!=null && formInstancePage.isThisPage())
					{
						formInstancePage.closeThisPage();
					}
				}catch(Exception e)
				{
					logger.error(e.getMessage());
					form.setExecutionStatus("error:"+e.getMessage());
				}
				
			}
		}
		logger.info("[method]checkDirectSubmit "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	
}
