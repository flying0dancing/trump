package com.lombardrisk.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.CreateNewReturnDialog;
import com.lombardrisk.pages.CreateNewReturnFromExcelDialog;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.*;
import com.lombardrisk.test.pojo.Form;

public class CreateNewForm extends TestManager{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**
	 * create new form with deleted existent or not<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run<br>
	 * scenario file optional columns: deleteExistent, expiration, cloneData, initToZero<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNew(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			Boolean flag=true;
			FormInstancePage formInstancePage=null;
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					if(form.getDeleteExistent()!=null && form.getDeleteExistent().equalsIgnoreCase("Y"))
					{
						flag=listPage.deleteExistedFormInstance(form);
					}
					if(flag)
					{
						CreateNewReturnDialog createNewOne=listPage.createNewReturn(form);
						if(createNewOne!=null && createNewOne.selectInfo())
						{
							formInstancePage=createNewOne.create();
							if(formInstancePage!=null)
							{
								formInstancePage.closeThisPage();
								form.setExecutionStatus("pass");
							}else
							{
								form.setExecutionStatus("fail on create new form");
								
							}
							
						}else
						{
							form.setExecutionStatus("fail on select information");
							
						}
						
					}
					else
					{
						form.setExecutionStatus("fail on delete existed form");
					}
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
			logger.info("method[createNew] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	
	}
	
	/**
	 * create new from excel with deleted existent or not<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, importFile<br>
	 * scenario file optional columns: applyScale, deleteExistent, expiration<br>
	 * if importFile does not exist in import folder, it will copy from download/<i>regulator<i>(EXPORTTOEXCEL***) folder to import folder automatically.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNewFromExcel(Form form)
	{
		if(runIt(form.getExecutionStatus()))
		{
			form.accumulateRunFrequency();
			Boolean flag=true;
			try
			{
				ListPage listPage=super.getListPage();
				if(listPage!=null)
				{
					listPage.loginAfterTimeout(listPage);
					if(form.getDeleteExistent()!=null && form.getDeleteExistent().equalsIgnoreCase("Y"))
					{
						flag=listPage.deleteExistedFormInstance(form);
					}
					if(flag)
					{
						CreateNewReturnFromExcelDialog createNewFromExcel=listPage.createNewReturnFromExcel(form);
						if(createNewFromExcel!=null && createNewFromExcel.isThisPage())
						{
							String errorTxt=createNewFromExcel.uploadFile();
							if(errorTxt==null)
							{
								FormInstancePage formInstancePage=createNewFromExcel.importFile();
								if(formInstancePage!=null)
								{
									if(formInstancePage.isThisPage())
									{
										form.setExecutionStatus("pass");
									}else
									{
										form.setExecutionStatus("fail on imported on wrong excel(wrong entity or wrong date)");
									}
									formInstancePage.closeThisPage();
									
								}else
								{
									form.setExecutionStatus("fail on create new from excel");
									
								}
								
							}else
							{
								form.setExecutionStatus("error:"+errorTxt);
							}
						}else
						{
							form.setExecutionStatus("fail on create new return from excel dialog.");
						}
					}else
					{
						form.setExecutionStatus("fail on delete existed form");
					}
				}
				
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			logger.info("method[createNewFromExcel] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	
}
