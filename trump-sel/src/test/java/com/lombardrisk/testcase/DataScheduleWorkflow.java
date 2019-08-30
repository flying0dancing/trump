package com.lombardrisk.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class DataScheduleWorkflow extends TestManager{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**
	 * click "Ready for approval" and "Approval"<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run<br>
	 * scenario file optional columns: expiration<br>
	 * special instruction: use two properties for approver, test.approver.user=admin2 and test.approver.password=password, specify them in test.properties.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void dataScheduleApproval(Form form)
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
		}
		logger.info("[method]dataScheduleApproval "+FormsDataProvider.getTotalFormCount()+form.toLog());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
}
