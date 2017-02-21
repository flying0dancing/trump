package com.lombardrisk.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class DataScheduleWorkflow extends TestManager{

	/**
	 * click "Ready for approval" and "Approval"<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run<br>
	 * scenario file may contains these columns: expiration<br>
	 * special instruction: use two properties for approver, test.approver.user=admin2 and test.approver.password=password, specify them in test.properties.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void DataScheduleApproval(Form form)
	{
		if((form.getExpiration()==null ||!form.getExpiration().equalsIgnoreCase("Y")) && form.getRun()!=null && form.getRun().equalsIgnoreCase("Y"))
		{
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
							
							if(formInstancePage.clickReadyForApproval())
							{
								if(formInstancePage.clickApproval())
								{
									form.setExecutionStatus("pass");
								}else
								{
									form.setExecutionStatus("fail on clicked \"Approval\"");
								}
							}else
							{
								form.setExecutionStatus("fail on clicked \"Ready for approval\"");
							}
							
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
						}else
						{
							form.setExecutionStatus("fail on open form instance");
						}
					}else
					{
						form.setExecutionStatus("fail on select form");
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
}
