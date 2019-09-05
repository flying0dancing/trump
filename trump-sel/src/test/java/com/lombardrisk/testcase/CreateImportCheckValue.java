package com.lombardrisk.testcase;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.CreateNewReturnDialog;
import com.lombardrisk.pages.CreateNewReturnFromExcelDialog;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ImportFileFormDialog;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class CreateImportCheckValue extends TestManager implements IExecFuncFolder{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**create new form with deleted existent or not, and then import adjustment, check some values in UI at last<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, importFile, expectationFile<br>
	 * scenario file optional columns: deleteExistent, expiration, cloneData, initToZero<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNewImportCheckValue(Form form)
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
								ImportFileFormDialog importFileFormDlg=formInstancePage.importFileForm();
								if(importFileFormDlg!=null)
								{
									String errorMessage=importFileFormDlg.uploadFile();
									if(errorMessage==null)
									{
										Boolean importFlag=importFileFormDlg.importFile();
										if(importFlag)
										{
											String status=Comparison.compareWithUIDisplayOneByOne(formInstancePage, form, 1);
											
											form.setExecutionStatus(status);
											if(formInstancePage.isThisPage())
											{
												formInstancePage.closeThisPage();
											}
											
										}else
										{
											form.setExecutionStatus("fail on click import on import file dialog in open form instance");
										}
									}else
									{
										form.setExecutionStatus("error on upload file: "+errorMessage);
									}
								}else
								{
									form.setExecutionStatus("fail on import file in open form instance");
								}
								
								if(formInstancePage.isThisPage())
								{
									formInstancePage.closeThisPage();
								}
								
							}else
							{
								form.setExecutionStatus("fail on create new form");
								
							}
							
						}else
						{
							form.setExecutionStatus("fail on create new form");
							
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
			logger.info("method[createNewImportCheckValue] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}
		
		addReportLink(UIDISPLAY,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**
	 * create new form from excel, and then check some values in UI.<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, importFile, expectationFile<br>
	 * scenario file optional columns: deleteExistent, expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createNewFromExcelCheckValue(Form form)
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
						CreateNewReturnFromExcelDialog createNewFromExcel=listPage.createNewReturnFromExcel(form);
						if(createNewFromExcel!=null && createNewFromExcel.isThisPage())
						{
							String errorTxt=createNewFromExcel.uploadFile();
							if(errorTxt==null)
							{
								formInstancePage=createNewFromExcel.importFile();
								if(formInstancePage!=null)
								{
									String status=Comparison.compareWithUIDisplayOneByOne(formInstancePage, form, 3);
									
									form.setExecutionStatus(status);
									if(formInstancePage.isThisPage())
									{
										formInstancePage.closeThisPage();
									}
									
								}else
								{
									form.setExecutionStatus("fail on import file from excel");
								}
							}else
							{
								form.setExecutionStatus("error on upload file:"+errorTxt);
							}
						}else
						{
							form.setExecutionStatus("fail on create new form from excel");
							
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
			logger.info("method[createNewFromExcelCheckValue] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}
		
		addReportLink(UIDISPLAY,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	
	}
	
	
}
