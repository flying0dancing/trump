package com.lombardrisk.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.ListPage;
import com.lombardrisk.pages.RetrieveDialog;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class RetrieveForm extends TestManager{

	/**
	 * retrieve form<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, deleteExistent<br>
	 * scenario file may contains these columns: expiration<br>
	 * special instruction: when the retrieved form exists, and this form will not be retrieved.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void retrieveForm(Form form)
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
						RetrieveDialog retrieveDialog=listPage.retrieveReturn(form);
						if(retrieveDialog!=null && retrieveDialog.selectInfo())
						{
							String status=retrieveDialog.doRetrieve();
							form.setExecutionStatus(status);
							
						}else
						{
							form.setExecutionStatus("fail on select information on retrieve dialog");
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
			
		}else
		{
			form.setExecutionStatus("skip");
		}
		
		Assert.assertTrue(form.getExecutionStatus().equalsIgnoreCase("pass") || form.getExecutionStatus().equalsIgnoreCase("skip"));
	}
}
