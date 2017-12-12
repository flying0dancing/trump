package com.lombardrisk.testcase;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstanceBottomPage;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.test.Comparison;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.FormsDataProvider;
import com.lombardrisk.test.TestManager;
import com.lombardrisk.test.pojo.Form;

public class CheckValue extends TestManager implements IExecFuncFolder{

	/**
	 * check form exists or not
	 * @author kun shen
	 * @param form
	 * @throws Exception
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	private void checkFormExist( Form form) throws Exception
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
					if(listPage.selectFormInfo(form))
					{
						form.setExecutionStatus("pass");
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
		}
		
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	
	}
	/** check display value in UI. <br>the cell which type is checkbox's value equals to 1 if checked, equals 0 if unchecked.<br>This method cannot checks hidden cells.<br> display UI values are stored at <i>result</i>\download\<i>regulator</i>(UIDisplay)<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 * @throws Exception
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkUIDisplay( Form form) throws Exception
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
							String status=Comparison.compareWithUIDisplay(formInstancePage, form);
							form.setExecutionStatus(status);
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
		
		addReportLink(UIDISPLAY,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	
	}
	
	/** check display value in UI(waste time method). <br>the cell which type is checkbox's value equals to 1 if checked, equals 0 if unchecked.<br>This method cannot checks hidden cells.<br> display UI values are stored at <i>result</i>\download\<i>regulator</i>(UIDisplay)<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 * @throws Exception
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkUIDisplayOneByOne( Form form) throws Exception
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
							String status=Comparison.compareWithUIDisplayOneByOne(formInstancePage, form, -1);
							form.setExecutionStatus(status);
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
		
		addReportLink(UIDISPLAY,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	
	}
	
	/**check download excel file. <br>if cell's expected value and actual value contain blanks at both ends, those blanks will be ignored.<br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(exportToExcel)<br><br>
	 * it suits for AgileREPORTER version less than or equal 1.15.0<br><br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkExportToExcel(Form form)
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
							String exportedFileFullPath=formInstancePage.exportToExcel();
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedExcel(form, exportedFileFullPath,EXPORTTOEXCEL);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file:"+exportedFileFullPath);
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
		addReportLink(EXPORTTOEXCEL,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**check download csv file. <br>if cell's expected value and actual value contain blanks at both ends, those blanks will be ignored.<br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(exportToCSV)<br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkExportToCSV(Form form)
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
							String exportedFileFullPath=formInstancePage.exportToCSV();
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedCSV(form, exportedFileFullPath);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file:"+exportedFileFullPath);
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
	
		addReportLink(EXPORTTOCSV,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
		
	}
	
	
	/**check download excel file. <br>if cell's expected value and actual value contain blanks at both ends, those blanks will be ignored.<br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(exportToExcelApplyScale)<br><br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.1<br><br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkExportToExcelApplyScale(Form form)
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
							String exportedFileFullPath=formInstancePage.exportToExcelApplyScale();
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedExcel(form, exportedFileFullPath,EXPORTTOEXCELAPPLYSCALE);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file:"+exportedFileFullPath);
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
		addReportLink(EXPORTTOEXCELAPPLYSCALE,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}
	
	/**check download excel file. <br>if cell's expected value and actual value contain blanks at both ends, those blanks will be ignored.<br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(exportToExcelNoScale)<br><br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.1<br><br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkExportToExcelNoScale(Form form)
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
							String exportedFileFullPath=formInstancePage.exportToExcelNoScale();
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedExcel(form, exportedFileFullPath,EXPORTTOEXCELNOSCALE);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file:"+exportedFileFullPath);
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
		addReportLink(EXPORTTOEXCELNOSCALE,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}

	/**check download PDF file. <br>if cell's expected value and actual value contain blanks at both ends, those blanks will be ignored.<br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(exportToPDF)<br><br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.0<br><br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkExportToPDF(Form form)
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
							String exportedFileFullPath=formInstancePage.exportToPDF();
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath!=null && new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedPDF(form, exportedFileFullPath);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file:"+exportedFileFullPath);
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
		addReportLink(EXPORTTOPDF,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
		
	}

	/**check download validation rules' file. <br> ignore expected value and actual value's case, case insensitive<br> download file store at <i>result</i>\download\<i>regulator</i>(ExportValidation)<br><br>
	 * it suits for AgileREPORTER version less than or equal 1.15.0<br><br>
	 * scenario file must contains these columns: name, version, regulator, entity, processDate, run, expectationFile<br>
	 * scenario file may contains these columns: expiration<br>
	 * @author kun shen
	 * @param form
	 */
	@Test(dataProvider="FormInstances",dataProviderClass=FormsDataProvider.class)
	public void checkValidation(Form form)
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
							if(!formInstancePage.validationLiveStatus()){formInstancePage.validationNow();}
							FormInstanceBottomPage formInstanceBottomPage=formInstancePage.viewAdjustmentLog();
							String exportedFileFullPath=null;
							if(formInstanceBottomPage!=null)
							{
								exportedFileFullPath=formInstanceBottomPage.exportValidation();
							}else
							{
								form.setExecutionStatus("fail on open form instance's bottom page");
							}
							if(formInstancePage.isThisPage())
							{
								formInstancePage.closeThisPage();
							}
							
							if(exportedFileFullPath==null)
							{
								form.setExecutionStatus("fail on no records found to export.");
							}else if(new File(exportedFileFullPath).exists())
							{
								String status=Comparison.compareWithExportedValidation(form, exportedFileFullPath);
								form.setExecutionStatus(status);
							}else
							{
								form.setExecutionStatus("fail on open not existed file."+exportedFileFullPath);
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
		addReportLink(EXPORTVALIDATION,form.getRegulator(),form.getExpectationFile(),form.getExec_ExpectationFile());
		Assert.assertEquals(form.getExecutionStatus().substring(0, 4), "pass");
	}

}
