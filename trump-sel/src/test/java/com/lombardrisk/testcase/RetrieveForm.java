package com.lombardrisk.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.ExecutionGroupsFormPage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.pages.RetrieveDialog;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class RetrieveForm extends TestManager{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * retrieve form<br>
	 * scenario file required columns: name, version, regulator, entity, processDate, run, deleteExistent, translate<br>
	 * scenario file optional columns: expiration<br>
	 * special instruction: when the retrieved form exists and deleteExistent not set to Y, and this form will not be retrieved.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void retrieveForm(Form form)
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
			logger.info("method[retrieveForm] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**
	 * retrieve Multiple forms<br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.9<br><br>
	 * scenario file required columns: retrieveGroup, abortOnFailure, regulator, processDate, run<br>
	 * scenario file optional columns: expiration<br>
	 * special instruction: when abortOnFailure is null, trump will not click this check box, using its default settings.
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void retrieveMultipleForms(Form form)
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
					ExecutionGroupsFormPage retrieveGroupPage=listPage.retrieveMultiReturns(form);
					if(retrieveGroupPage!=null && retrieveGroupPage.selectInfo())
					{
						String status=retrieveGroupPage.doRetrieve();
						form.setExecutionStatus(status);
					}else
					{
						form.setExecutionStatus("fail on select information on retrieve group page");
					}					
				}
				
			}catch(Exception e)
			{
				logger.error(e.getMessage());
				form.setExecutionStatus("error:"+e.getMessage());
			}
			logger.info("method[retrieveMultipleForms] "+FormsDataProvider.getTotalFormCount()+form.toLog());
		}

		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
}
