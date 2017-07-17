package com.lombardrisk.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.CreateNewReturnDialog;
import com.lombardrisk.pages.CreateNewReturnFromExcelDialog;
import com.lombardrisk.pages.FormInstancePage;

import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.*;
import com.lombardrisk.test.pojo.Form;

public class CreateNewForm extends TestManager{
	/**
	 * create new form with deleted existent or not<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run<br>
	 * scenario file may contains these columns: deleteExistent, expiration, cloneData, initToZero<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNew(Form form)
	{
		Boolean flag=true;
		if((form.getExpiration()==null ||!form.getExpiration().equalsIgnoreCase("Y")) && form.getRun()!=null && form.getRun().equalsIgnoreCase("Y"))
		{
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
		}else
		{
			form.setExecutionStatus("skip");
		}
		
		Assert.assertTrue(form.getExecutionStatus().equalsIgnoreCase("pass") || form.getExecutionStatus().equalsIgnoreCase("skip"));
	
	}
	
	/**
	 * create new from excel with deleted existent or not<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, importFile<br>
	 * scenario file may contains these columns: deleteExistent, expiration<br>
	 * if importFile does not exist in import folder, it will copy from download/<i>regulator<i>(EXPORTTOEXCELNOSCALE) folder to import folder automatically.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNewFromExcel(Form form)
	{
		Boolean flag=true;
		if((form.getExpiration()==null ||!form.getExpiration().equalsIgnoreCase("Y")) && form.getRun()!=null && form.getRun().equalsIgnoreCase("Y"))
		{
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
		}else
		{
			form.setExecutionStatus("skip");
		}
		
		Assert.assertTrue(form.getExecutionStatus().equalsIgnoreCase("pass") || form.getExecutionStatus().equalsIgnoreCase("skip"));
	}
	
	
}
