package com.lombardrisk.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ImportFileFormDialog;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.*;
import com.lombardrisk.test.pojo.Form;

public class ImportForm extends TestManager{
	
	/**
	 * only import into existed form <br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, importFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void importForm(Form form)
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
					flag=listPage.selectFormInfo(form);
					if(flag)
					{
						formInstancePage=listPage.openFormInstance(form);
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
										form.setExecutionStatus("pass");

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
							form.setExecutionStatus("fail on open form instance");
						}
					}
					else
					{
						form.setExecutionStatus("fail on select form in dashboard page");
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
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
}
