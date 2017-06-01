package com.lombardrisk.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.PropHelper;

import com.lombardrisk.commons.ExcelUtil;
import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.test.pojo.Form;
import com.lombardrisk.commons.TxtUtil;

public class Comparison implements IComFolder,IExecFuncFolder
{

	private final static Logger logger = LoggerFactory.getLogger(Comparison.class);
	private Comparison(){}
	
	/**compare expectation with UI display values, return compare result(pass, fail, error:...). <br>
	 * @author kun shen
	 * @param formInstancePage
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public static String compareWithUIDisplay(FormInstancePage formInstancePage,Form form) throws Exception
	{
		logger.info("Begin verify \"cells in UI pages\"");
		String testRstFlag=null;
		long begin=System.currentTimeMillis();
		int indexOfCellName=0;
		int indexOfRowId=1;
		int indexOfInstance=2;
		int indexOfExpectedValue=4;
		int indexOfActualValue=5;
		int indexOfTestResult=8;
		String regulator=form.getRegulator();
		String downloadFolder=TARGET_DOWNLOAD_FOLDER+regulator+"("+UIDISPLAY+")/";
		String expectationFolder=TARGET_EXPECTATION_FOLDER+regulator+"/";
		//String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		if(!new File(downloadFolder).exists())
		{
			FileUtil.createDirectory(downloadFolder);
		}
		String fileFullPath=formInstancePage.getAllCellsValue(downloadFolder);
		if(fileFullPath==null)
		{
			testRstFlag="error:cannot find file in UIDisplay folder[ "+downloadFolder+"].";
			logger.error(testRstFlag);
		}else
		{
			long begin_Comparison=System.currentTimeMillis();
			String reslutFolder=expectationFolder+UIDISPLAY+"/";
			String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
			form.setExec_ExpectationFile(newFileName);
			String newFilePath=reslutFolder+newFileName;
			File newFile=new File(newFilePath);
			Workbook xwb =ExcelUtil.openWorkbook(newFile);
			int amt=ExcelUtil.getRowNum(xwb, null);
			
			//String searchedFilePrefix=downloadFolder+form.getName()+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple;
			String searchedFileName=fileFullPath;
			logger.info("comparison used download file ["+searchedFileName+"]");
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getValueFromRow(xwb,null,i);
				if(expectedValueValueList==null || expectedValueValueList.get(0)==null || expectedValueValueList.get(0).trim().equals("")){continue;}
				String cellName = expectedValueValueList.get(indexOfCellName).trim();
				String rowID = expectedValueValueList.get(indexOfRowId).trim();
				String instanceCode = (expectedValueValueList.get(indexOfInstance).trim().equals("")) ? null : expectedValueValueList.get(indexOfInstance).trim();
				String expectedValueRP = expectedValueValueList.get(indexOfExpectedValue).trim();
				
				String identifier=cellName+","+rowID+","+instanceCode+",";
				
				
				String actualValue=FileUtil.serachIdentifierInRow(searchedFileName,identifier);
				
				if(actualValue==null)
				{
					ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "Cannot find cell");
					testRstFlag="fail";
				}else
				{
					ExcelUtil.editCell(xwb, null, i, indexOfActualValue, actualValue);
					if(expectedValueRP.equals(actualValue))
					{
						ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "pass");
						if(testRstFlag==null)
						{
							testRstFlag="pass";
						}
					}else
					{
						ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "fail");
						if(testRstFlag==null || testRstFlag.equals("pass"))
						{
							testRstFlag="fail";
						}
					}
				}
				if(i%5000==0){ExcelUtil.saveWorkbook(newFile, xwb);}
			}
			
			ExcelUtil.saveWorkbook(newFile, xwb);
			logger.info("Expectation File:"+newFile+" size:"+newFile.length()/1024+"KB, row count:"+String.valueOf(amt));
			
			long end=System.currentTimeMillis();
			if(testRstFlag==null){testRstFlag="error: no expectation value.";}
			logger.info("comparison used time[seconds]:"+(end-begin_Comparison)/1000.00F);
			logger.info("totally, used time[seconds]:"+(end-begin)/1000.00F +" result:"+testRstFlag);

		}
		Runtime.getRuntime().gc();
		return testRstFlag;
	}
	/** compare with UI display value, and check value one by one, return compare result(pass, fail, error:abc)<br>
	 *column index start from 0..., indexOfCellName=0,indexOfRowId=1,indexOfInstance=2,indexOfExpectedValue=4,indexOfActualValue=5,indexOfTestResult=8
	 *<br>
	 * @author kun shen
	 * @param formInstancePage
	 * @param form
	 * @param compareCount any integer
	 * @return compare result(pass, fail, error)
	 * @throws Exception
	 */
	public static String compareWithUIDisplayOneByOne(FormInstancePage formInstancePage,Form form, int compareCount) throws Exception
	{
		String testRstFlag=null;
		int indexOfCellName=0;
		int indexOfRowId=1;
		int indexOfInstance=2;
		int indexOfExpectedValue=4;
		int indexOfActualValue=5;
		int indexOfTestResult=8;
		testRstFlag=compareWithUIDisplayOneByOne(formInstancePage,form,compareCount,indexOfCellName,indexOfRowId,indexOfInstance,indexOfExpectedValue,indexOfActualValue,indexOfTestResult);
		return testRstFlag;
	}
	
	/** compare with UI display value, and check value one by one, return compare result(pass, fail, error:...)
	 * <br>
	 * @author kun shen
	 * @param formInstancePage
	 * @param form
	 * @param compareCount any integer, if compareCount<=0, it will compare all rows in expectation file.
	 * @param indexOfCellName must be correct
	 * @param indexOfRowId
	 * @param indexOfInstance
	 * @param indexOfExpectedValue
	 * @param indexOfActualValue
	 * @param indexOfTestResult
	 * @return
	 * @throws Exception
	 */
	public static String compareWithUIDisplayOneByOne(FormInstancePage formInstancePage,Form form, int compareCount,int indexOfCellName,int indexOfRowId,int indexOfInstance,int indexOfExpectedValue,int indexOfActualValue,int indexOfTestResult) throws Exception
	{
		logger.info("Begin verify \"cells in UI pages one by one\"");
		String testRstFlag=null;
		/*int indexOfCellName=0;
		int indexOfRowId=1;
		int indexOfInstance=2;
		int indexOfExpectedValue=4;
		int indexOfActualValue=5;
		int indexOfTestResult=8;*/
		long begin=System.currentTimeMillis();
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER+regulator+"/";
		File expectedFile=new File(expectationFolder+form.getExpectationFile());
		if(expectedFile.exists() && compareCount!=0)
		{
			long begin_Comparison=System.currentTimeMillis();
			String reslutFolder=expectationFolder+UIDISPLAY+"/";
			String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
			
			form.setExec_ExpectationFile(newFileName);
			String newFilePath=reslutFolder+newFileName;
			File newFile=new File(newFilePath);
			
			Workbook xwb =ExcelUtil.openWorkbook(newFile);
			int amt = ExcelUtil.getRowNum(xwb, null);
			
			if(compareCount<0){compareCount=amt;}
			int count=1;
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getValueFromRow(xwb,null,i);
				if(expectedValueValueList==null || expectedValueValueList.get(0)==null || expectedValueValueList.get(0).trim().equals("")){continue;}
				
				String cellName = expectedValueValueList.get(indexOfCellName).trim();
				String rowID = expectedValueValueList.get(indexOfRowId).trim();
				String instance = (expectedValueValueList.get(indexOfInstance).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValue = expectedValueValueList.get(indexOfExpectedValue).trim();
				
				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ")=" + expectedValue);
				
				//modified by Kun Shen 
				String actualValue=formInstancePage.getCellValueInUI(regulator,form.getName(), form.getVersion().substring(1),cellName,  rowID,instance);
				if(actualValue==null)
				{
					ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "Cannot find cell");
					testRstFlag="error: cannot find cell";
				}else
				{
					logger.info("actual value="+ actualValue);
					ExcelUtil.editCell(xwb, null, i, indexOfActualValue, actualValue);
					if(expectedValue.equals(actualValue))
					{
						ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "pass");
						if(testRstFlag==null)
						{
							testRstFlag="pass";
						}
					}else
					{
						ExcelUtil.editCell(xwb, null, i, indexOfTestResult, "fail");
						if(testRstFlag==null || testRstFlag.equals("pass"))
						{
							testRstFlag="fail";
						}
						
					}
				}
				if(i%5000==0){ExcelUtil.saveWorkbook(newFile, xwb);}
				if(count==compareCount){break;}
				count++;
			}
			
			ExcelUtil.saveWorkbook(newFile, xwb);
			logger.info("Expectation File:"+newFile+" size:"+newFile.length()/1024+"KB, row count:"+String.valueOf(amt));
			logger.info("comparison used time[seconds]:"+(System.currentTimeMillis()-begin_Comparison)/1000.00F);
		}
		
		if(testRstFlag==null)
		{
			if(compareCount==0)
			{
				testRstFlag="error: no need compare.";
			}else
			{
				testRstFlag="error: no expectation value.";
			}
		}
		long end=System.currentTimeMillis();
		logger.info("totally, used time[seconds]:"+(end-begin)/1000.00F +" result:"+testRstFlag);
		Runtime.getRuntime().gc();
		return testRstFlag;
	}
	
	/**
	 * compare expected excel with "export to excel"/"export to excel(Apply Scale)"/"export to excel(No Scale)"'s download file, return values(pass,fail:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @param functionFolderName values:
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedExcel(Form form, String exportedFileFullPath,String functionFolderName) throws Exception
	{
		logger.info("Begin verify \"export to excel\" file");
		long begin = System.currentTimeMillis();
		String returnStatus=null;
		String cmdLine="";
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER+regulator+"/";
		File expectationFile = new File(expectationFolder+form.getExpectationFile());
		if(expectationFile.exists())
		{
			String reslutFolder=expectationFolder+functionFolderName+"/";
			
			String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
			form.setExec_ExpectationFile(newFileName);
			String newFilePath=reslutFolder+newFileName;
			
			File exportedFile = new File(exportedFileFullPath);
			
			if(exportedFile.exists())
			{
				logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
				String[] commons={ PropHelper.getProperty("path.GetCellValueFromExcel"), "\"" + exportedFileFullPath + "\"", "\"" + newFilePath + "\"", TARGET_LOG_FOLDER };
				cmdLine=PropHelper.getProperty("path.GetCellValueFromExcel")+" \"" + exportedFileFullPath + "\" \"" + newFilePath + "\" "+TARGET_LOG_FOLDER;
				logger.info(cmdLine);
				Process process = Runtime.getRuntime().exec(commons);
				process.waitFor();
				logger.info("Expectation File(new):"+newFilePath);
				File compareRstFile = new File(TARGET_LOG_FOLDER + "/queryCellValueRst.txt");
				if(compareRstFile.exists())
				{
					String rst = TxtUtil.getAllContent(compareRstFile).trim();
					if (!String.valueOf(rst.substring(0,1)).matches("[a-zA-Z0-9]"))
						rst = rst.substring(1);
					returnStatus=rst;
					compareRstFile.delete();
				}else
				{
					returnStatus="fail:File Not Find " +compareRstFile.getAbsolutePath();
				}
				
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		
		if(returnStatus.toLowerCase().startsWith("fail"))
		{
			returnStatus=returnStatus+System.getProperty("line.separator")+cmdLine;
		}
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		Runtime.getRuntime().gc();
		return returnStatus;
	}
	
	/**
	 * compare expected csv with "export to csv"'s download file(csv), return values(pass,fail,fail:...,error:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedCSV(Form form, String exportedFileFullPath) throws Exception
	{
		logger.info("Begin verify \"export to csv\" file");
		Runtime run=Runtime.getRuntime();
		long begin = System.currentTimeMillis();
		String returnStatus=null;
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER+regulator+"/";
		File expectationFile = new File(expectationFolder+form.getExpectationFile());
		if(expectationFile.exists())
		{
			File exportedFile = new File(exportedFileFullPath);
			if(exportedFile.exists())
			{
				String reslutFolder=expectationFolder+EXPORTTOCSV+"/";
				String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
				form.setExec_ExpectationFile(newFileName);
				String newFilePath=reslutFolder+newFileName;
				
				File newFile=new File(newFilePath);
				FileUtil.writeContentToEmptyFile(newFile,"");
				BufferedReader baselineReader=null;
				StringBuffer strBuffer=null;
				try
				{
					File newExportedFile=FileUtil.writeToNewFile(exportedFile,EXPORTTOCSV);
					logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
					logger.info("Exportation File(new):"+newExportedFile+" size:"+newExportedFile.length()/1024+"KB");
					baselineReader=new BufferedReader(new FileReader(expectationFile));
					strBuffer=new StringBuffer();
					String status=null;
					String baselineStr=null;
					
					while((baselineStr=baselineReader.readLine())!=null)
					{
						
						if(baselineStr.trim().equals("")||baselineStr.toLowerCase().contains("--dateformat")){continue;}
						String tmp=baselineStr.toLowerCase();
						if(tmp.contains("cellname")&& tmp.contains("pageinstance")&& tmp.contains("adjustmentvalue"))
						{
							strBuffer.append(baselineStr+",actualValue,result"+System.getProperty("line.separator"));
							continue;
						}
						status=FileUtil.findLineInCSV(newExportedFile, baselineStr);
						strBuffer.append(baselineStr+status+System.getProperty("line.separator"));
						if(returnStatus==null && !status.endsWith("\"pass\""))
						{
							returnStatus="fail";
						}
						if(status.startsWith("error"))
						{returnStatus="error";}
						if(strBuffer.length()>5000)
						{
							FileUtil.writeContent(newFile,strBuffer.toString());
							strBuffer.setLength(0);//clear strBuffer
						}
					}
					if(returnStatus==null)
					{
						returnStatus="pass";
					}
					
					FileUtil.writeContent(newFile, strBuffer.toString());	
					baselineReader.close();
					
					logger.info("Expectation File(new):"+newFile+" size:"+newFile.length()/1024+"KB");
				}catch(Exception e)
				{
					returnStatus="error:"+e.getMessage();
				}finally
				{
					run.gc();
					logger.info("(finally)used memory:"+(run.totalMemory()-run.freeMemory())/1024/1024+"MB, free memory:"+run.freeMemory()/1024/1024+"MB");
				}
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		return returnStatus;
	}

	/**
	 * compare exported file with "export to regulator"'s download file, return values(pass,fail,fail:...,error:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedToRegulator(Form form,String exportedFileFullPath) throws Exception
	{
		logger.info("Begin verify \"export to regulator\" files");
		long begin = System.currentTimeMillis();
		String returnStatus=null;
		String cmdLine="";
		String exportedFileName=new File(exportedFileFullPath).getName();
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER.replace("\\", "/").replace("/", System.getProperty("file.separator"))+regulator+System.getProperty("file.separator");
		String expectationFileName=null;
		List<String> expectationFiles=getExpectationFiles(form.getExpectationFile());
		if(expectationFiles!=null && expectationFiles.size()==1){expectationFileName=form.getExpectationFile();}
		if(expectationFiles!=null && expectationFiles.size()>1)
		{
			for(String exepc:expectationFiles)
			{
				String str=exportedFileName.substring(exportedFileName.indexOf("_"),exportedFileName.lastIndexOf("_")+1);
				str=str.replace("_"+form.getEntity()+"_", "_");//something like _xx_aa-ss_
				Pattern p = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(exepc);
				if(m.find())
				{
					expectationFileName=exepc;
					break;
				}
			}
		}
		
		File expectationFile = new File(expectationFolder+expectationFileName);
		if(expectationFile.exists())
		{
			String reslutFolder=expectationFolder+EXPORTTOREGULATOR+System.getProperty("file.separator");
			String reportName=expectationFileName.substring(0,expectationFileName.lastIndexOf("."))+".html";
			String newReportName=FileUtil.copyToNewFile(reslutFolder,reslutFolder,reportName);
			
			String exec_tmp=form.getExec_ExpectationFile();
			if(exec_tmp==null || exec_tmp.trim().equals(""))
			{
				form.setExec_ExpectationFile(newReportName);
			}else
			{
				form.setExec_ExpectationFile(exec_tmp+";"+newReportName);
			}
			
			String newReportPath=reslutFolder+newReportName;
			
			File exportedFile = new File(exportedFileFullPath);
			
			if(exportedFile.exists())
			{
				logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
				logger.info("Expectation File:"+newReportPath);
				String path_BComp=new File(System.getProperty("user.dir")).getParent().replace("\\", "/").replace("/", System.getProperty("file.separator"))+PropHelper.getProperty("path.BComp").replace("..", "").replace("\\", "/").replace("/", System.getProperty("file.separator"));
				
				cmdLine="\""+path_BComp+"GenerateReport.bat\" "+"\"" + exportedFileFullPath.replace("\\", "/").replace("/", System.getProperty("file.separator")) + "\" "+"\"" + expectationFolder+expectationFileName + "\" "+ "\""+newReportPath+"\"";
				
				logger.info(cmdLine);
				Process process = Runtime.getRuntime().exec(cmdLine);
				
				BufferedReader input=new BufferedReader(new InputStreamReader(process.getInputStream()));
				StringBuffer lines=new StringBuffer();
				String line=null;
				while((line=input.readLine())!=null)
				{
					lines.append(line.trim());
				}
				input.close();
				logger.info("subprocess result[code]:"+String.valueOf(process.waitFor()));
				
				process.destroy();
				returnStatus=lines.toString();
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		
		if(returnStatus.startsWith("fail"))
		{
			returnStatus=returnStatus+System.getProperty("line.separator")+cmdLine;
		}
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		Runtime.getRuntime().gc();
		return returnStatus;
	}
	
	/**
	 * get expectation files
	 * @author kun shen
	 * @param expectationFile
	 * @return
	 */
	private static List<String> getExpectationFiles(String expectationFile)
	{
		List<String> epectationFiles=null;
		if(expectationFile!=null && !expectationFile.trim().equals(""))
		{
			epectationFiles=new ArrayList<String>();
			if(expectationFile.contains(";"))
			{
				String[] tmpList=expectationFile.split(";");
				for(String tmp:tmpList)
				{
					if(tmp!=null && !tmp.trim().equals(""))
					{
						epectationFiles.add(tmp.trim());
					}
				}
			}else
			{
				epectationFiles.add(expectationFile);
			}
		}
		
		return epectationFiles;
	}
	
	/**
	 * compare expected pdf with "export to PDF"'s download file(pdf), return values(pass,fail,fail:...,error:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedPDF(Form form, String exportedFileFullPath) throws Exception
	{
		logger.info("Begin verify \"export to PDF\" file");
		long begin = System.currentTimeMillis();
		String cmdLine="";
		String returnStatus=null;
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER.replace("\\", "/").replace("/", System.getProperty("file.separator"))+regulator+System.getProperty("file.separator");
		String expectationFileName=form.getExpectationFile();
		File expectationFile = new File(expectationFolder+expectationFileName);
		if(expectationFile.exists())
		{
			String reslutFolder=expectationFolder+EXPORTTOPDF+System.getProperty("file.separator");
			String reportName=expectationFileName.substring(0,expectationFileName.lastIndexOf("."))+".html";
			String newReportName=FileUtil.copyToNewFile(reslutFolder,reslutFolder,reportName);
			
			form.setExec_ExpectationFile(newReportName);
			
			String newReportPath=reslutFolder+newReportName;
			
			File exportedFile = new File(exportedFileFullPath);
			if(exportedFile.exists())
			{
				logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
				logger.info("Expectation File:"+newReportPath);
				String path_BComp=new File(System.getProperty("user.dir")).getParent().replace("\\", "/").replace("/", System.getProperty("file.separator"))+PropHelper.getProperty("path.BComp").replace("..", "").replace("\\", "/").replace("/", System.getProperty("file.separator"));
				
				cmdLine="\""+path_BComp+"GenerateReport.bat\" "+"\"" + exportedFileFullPath.replace("\\", "/").replace("/", System.getProperty("file.separator")) + "\" "+"\"" + expectationFolder+expectationFileName + "\" "+ "\""+newReportPath+"\"";
				
				logger.info(cmdLine);
				Process process = Runtime.getRuntime().exec(cmdLine);
				
				BufferedReader input=new BufferedReader(new InputStreamReader(process.getInputStream()));
				StringBuffer lines=new StringBuffer();
				String line=null;
				while((line=input.readLine())!=null)
				{
					lines.append(line.trim());
				}
				input.close();
				logger.info("subprocess result[code]:"+String.valueOf(process.waitFor()));
				
				process.destroy();
				returnStatus=lines.toString();
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		return returnStatus;
	}
	
	/**
	 * compare expected validations with validation->"export"'s download file, return values(pass,fail:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedValidation(Form form, String exportedFileFullPath) throws Exception
	{
		logger.info("Begin verify \"export validation rules\"");
		long begin = System.currentTimeMillis();
		String returnStatus=null;
		String cmdLine="";
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER.replace("\\", "/").replace("/", System.getProperty("file.separator"))+regulator+System.getProperty("file.separator");
		File expectationFile = new File(expectationFolder+form.getExpectationFile());
		if(expectationFile.exists())
		{
			String reslutFolder=expectationFolder+EXPORTVALIDATION+System.getProperty("file.separator");
			
			String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
			form.setExec_ExpectationFile(newFileName);
			String newFilePath=reslutFolder+newFileName;
			
			File exportedFile = new File(exportedFileFullPath);
			
			if(exportedFile.exists())
			{
				logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
				logger.info("Expectation File:"+newFilePath);
				String[] commons={ PropHelper.getProperty("path.GetValidationResult"), "\"" + exportedFileFullPath + "\"", "\"" + newFilePath + "\"", TARGET_LOG_FOLDER, "Y" };
				cmdLine=PropHelper.getProperty("path.GetValidationResult")+" \"" + exportedFileFullPath + "\" \"" + newFilePath + "\" "+TARGET_LOG_FOLDER+" \"Y\"";
				logger.info(cmdLine);
				Process process = Runtime.getRuntime().exec(commons);
				process.waitFor();
				File compareRstFile = new File(TARGET_LOG_FOLDER + "/rule_compareRst.txt");
				if(compareRstFile.exists())
				{
					String rst = TxtUtil.getAllContent(compareRstFile).trim();
					if (!String.valueOf(rst.substring(0,1)).matches("[a-zA-Z0-9]"))
						rst = rst.substring(1).toLowerCase();//change to lower case.
					returnStatus=rst;
					compareRstFile.delete();
				}else
				{
					returnStatus="fail:File Not Find " +compareRstFile.getAbsolutePath();
				}
				
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		
		if(returnStatus.toLowerCase().startsWith("fail"))
		{
			returnStatus=returnStatus+System.getProperty("line.separator")+cmdLine;
		}
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		Runtime.getRuntime().gc();
		return returnStatus;
	}
	
	/**
	 * compare expected validations with problems->"export"'s download file, return values(pass,fail:...)<br>
	 * @author kun shen
	 * @param form
	 * @param exportedFileFullPath
	 * @return
	 * @throws Exception
	 */
	public static String compareWithExportedProblems(Form form, String exportedFileFullPath) throws Exception
	{
		logger.info("Begin verify \"export problems\"");
		long begin = System.currentTimeMillis();
		String returnStatus=null;
		String cmdLine="";
		String regulator=form.getRegulator();
		String expectationFolder=TARGET_EXPECTATION_FOLDER.replace("\\", "/").replace("/", System.getProperty("file.separator"))+regulator+System.getProperty("file.separator");
		File expectationFile = new File(expectationFolder+form.getExpectationFile());
		if(expectationFile.exists())
		{
			String reslutFolder=expectationFolder+EXPORTPROBLEMS+System.getProperty("file.separator");
			
			String newFileName=FileUtil.copyToNewFile(expectationFolder,reslutFolder,form.getExpectationFile());
			form.setExec_ExpectationFile(newFileName);
			String newFilePath=reslutFolder+newFileName;
			
			File exportedFile = new File(exportedFileFullPath);
			
			if(exportedFile.exists())
			{
				logger.info("Exportation File:"+exportedFile+" size:"+exportedFile.length()/1024+"KB");
				logger.info("Expectation File:"+newFilePath);
				String[] commons={ PropHelper.getProperty("path.GetProblemResult"), "\"" + exportedFileFullPath + "\"", "\"" + newFilePath + "\"", TARGET_LOG_FOLDER };
				cmdLine=PropHelper.getProperty("path.GetProblemResult")+" \"" + exportedFileFullPath + "\" \"" + newFilePath + "\" "+TARGET_LOG_FOLDER;
				logger.info(cmdLine);
				Process process = Runtime.getRuntime().exec(commons);
				process.waitFor();
				File compareRstFile = new File(TARGET_LOG_FOLDER + "/rule_compareRst.txt");
				if(compareRstFile.exists())
				{
					String rst = TxtUtil.getAllContent(compareRstFile).trim();
					if (!String.valueOf(rst.substring(0,1)).matches("[a-zA-Z0-9]"))
						rst = rst.substring(1).toLowerCase();//change to lower case.
					returnStatus=rst;
					compareRstFile.delete();
				}else
				{
					returnStatus="fail:File Not Find " +compareRstFile.getAbsolutePath();
				}
				
			}else
			{
				returnStatus="fail:File Not Find:"+exportedFile.getAbsolutePath();
			}
		}else
		{
			returnStatus="fail:File Not Find:"+expectationFile.getAbsolutePath();
		}
		
		
		if(returnStatus.toLowerCase().startsWith("fail"))
		{
			returnStatus=returnStatus+System.getProperty("line.separator")+cmdLine;
		}
		long end = System.currentTimeMillis();
		logger.info("used time[seconds]:"+(end-begin)/1000.00F +" result:"+returnStatus);
		Runtime.getRuntime().gc();
		return returnStatus;
	}
	
}