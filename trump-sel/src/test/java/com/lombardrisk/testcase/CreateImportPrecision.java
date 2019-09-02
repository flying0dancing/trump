package com.lombardrisk.testcase;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.IReturnDialog;
import com.lombardrisk.pages.ImportFileFormDialog;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class CreateImportPrecision extends TestManager implements IExecFuncFolder{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**create new form, and then import adjustment, check some values in UI , if these values are checked as pass or fail, not as error, then all values in UI will be checked.<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, importFile, expectationFile<br>
	 * scenario file optional columns: deleteExistent, expiration, cloneData, initToZero<br>
	 * special instruction: when this method used in retrieve form and do precision check, the column importFile's value must be "retrieve".
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void createImportPrecision(Form form)
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
						IReturnDialog iReturnDialog=listPage.returnDialog(form);
						if(iReturnDialog!=null && iReturnDialog.selectInfo())
						{
							formInstancePage=iReturnDialog.clickOK();
							if(formInstancePage!=null)
							{
								ImportFileFormDialog importFileFormDlg=formInstancePage.importFileForm();
								Boolean importFlag=false;
								if(importFileFormDlg!=null)
								{
									String errorMessage=importFileFormDlg.uploadFile();
									if(errorMessage==null)
									{
										importFlag=importFileFormDlg.importFile();
									}else
									{
										form.setExecutionStatus("error: "+errorMessage);
									}
								}
								
								if(importFlag)
								{
									String status=Comparison.compareWithUIDisplayOneByOne(formInstancePage, form, 1);
									//add compare all display values.
									if(!status.startsWith("error"))
									{
										status=Comparison.compareWithUIDisplay(formInstancePage, form);
									}
									form.setExecutionStatus(status);
									formInstancePage.closeThisPage();
								}else
								{
									form.setExecutionStatus("fail on click import on import file dialog in open form instance");
								}
								
							}else
							{
								form.setExecutionStatus("fail on open form");
							}
						}else
						{
							form.setExecutionStatus("fail on create new form or retrieve form or compute form.");
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
		}
		addReportLink(UIDISPLAY,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		logger.info("method[createImportPrecision] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	
}
