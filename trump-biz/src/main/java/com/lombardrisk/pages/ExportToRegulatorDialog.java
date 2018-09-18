package com.lombardrisk.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.FileFormat;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.commons.JschUtil;
import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.DBInfo;
import com.lombardrisk.test.pojo.Form;
import com.lombardrisk.test.pojo.ServerInfo;

public class ExportToRegulatorDialog extends AbstractPage implements IComFolder,IExecFuncFolder{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	private String title;
	private DBInfo dBInfo;
	private ServerInfo serverInfo;
	public ExportToRegulatorDialog(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form,String title) {
		super(webDriverWrapper,testDataManager);
		this.form=form;
		this.title=title;
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
		this.setServerInfo(((TestDataManager)getTestDataManager()).getServerInfo());
	}
	
	public DBInfo getDBInfo() {
		return dBInfo;
	}
	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
	}
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		flag=element("td.transmitDialog",title).isDisplayed();
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		element("td.closeTransmitDialog",title).click();
		loadingDlg();
	}
	
	/**
	 * select export to regulator information.<br> If export button is enable to click, then return true. Others return false.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean selectInfo() throws Exception
	{
		Boolean flag=true;
		IWebElementWrapper element=null;
		String entity=form.getEntity();
		element=element("td.selectGroup",title);
		if(element.isDisplayed())
		{
			if (entity != null && !entity.equals(""))
			{
				logger.info("select entity:" + entity);
				flag=selectIt(element,entity);
			}
			element=null;
		}
		
		String processDate=form.getProcessDate();
		element=element("td.selectProcessDate",title);
		if(element.isDisplayed())
		{
			if (processDate != null && !processDate.equals(""))
			{
				logger.info("select process date:" + processDate);
				flag=selectIt(element,processDate);
			}
			element=null;
		}
		
		String framework=form.getTransmission().getFramework();
		element=element("td.selectFramework",title);
		if(element.isDisplayed())
		{
			if (framework!=null && !framework.equals(""))
			{
				logger.info("select transmission's framework:" + framework);
				flag=selectIt(element,framework);
			}
			element=null;
		}
		
		String taxonomy=form.getTransmission().getTaxonomy();
		element=element("td.selectTaxonomy",title);
		if(element.isDisplayed())
		{
			if (taxonomy!=null && !taxonomy.equals(""))
			{
				logger.info("select transmission's taxonomy:" + taxonomy);
				flag=selectIt(element,taxonomy);
			}
			element=null;
		}
		
		String module=this.form.getTransmission().getModule();
		element=element("td.selectModule",title);
		if(element.isDisplayed())
		{
			if (module!=null && !module.equals(""))
			{
				logger.info("select transmission's module:" + module);
				flag=selectIt(element,module);
			}
			element=null;
		}
		
		String compressType=form.getTransmission().getCompressType();
		element=element("td.compressTypeSelector",title);
		if(element.isDisplayed())
		{
			if (compressType!=null && !compressType.equals(""))
			{
				logger.info("select transmission's compress type:" + compressType);
				flag=selectIt(element,compressType);
			}else
			{
				logger.info("select transmission's default compress type: DZ");
				flag=selectIt(element,"GZ");
			}
			element=null;
		}
		
		loadingDlg();
		waitThat().timeout(2000);
		if(flag && !element("td.noRecordsFound").isDisplayed() && element("td.exportButton",title).isEnabled())
		{
			flag=true;
		}
		return flag;
	}
	
	
	
	/**
	 * click export button get download files.<br> If successfully return files' full path, others return null.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public List<String> export() throws Exception
	{
		List<String> unCompressDestFiles=null;
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			if(title.contains("Combine"))
			{
				logger.info("export to combine");
				flag=clickExportToDataSchedule(true);//TODO modify it according to requirements, may be i can zip them here.
			}else if(title.equalsIgnoreCase("Export to DataSchedule") || title.toLowerCase().contains("dataschedule"))
			{
				logger.info("export to DataSchedule");
				flag=clickExportToDataSchedule(false);
			}else
			{
				logger.info("export to regulator");
				flag=clickExport();
			}
			
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOREGULATOR+")/");
		unlockDownloadDir(downloadFolder);
		if(destFileFullPath!=null && !destFileFullPath.trim().equals(""))
		{
			if(destFileFullPath.lastIndexOf(".")>0 && destFileFullPath.lastIndexOf(System.getProperty("file.separator"))<destFileFullPath.lastIndexOf("."))
			{
				String destDir=destFileFullPath.substring(0,destFileFullPath.lastIndexOf("."))+System.getProperty("file.separator");
				
				FileUtil.createDirectory(destDir);
				unCompressDestFiles=FileUtil.unCompress(destFileFullPath, destDir);
				if(unCompressDestFiles!=null && unCompressDestFiles.size()>=1)
				{
					for(int i=0;i<unCompressDestFiles.size();i++)
					{
						unCompressDestFiles.set(i, destDir+unCompressDestFiles.get(i));
					}
				}else
				{
					FileUtil.deleteDirectory(destDir);
					unCompressDestFiles=new ArrayList<String>();
					unCompressDestFiles.add(destFileFullPath);
				}
			}else
			{
				unCompressDestFiles=new ArrayList<String>();
				unCompressDestFiles.add(destFileFullPath);
			}
			
		}
		
		
		Runtime.getRuntime().gc();
		return unCompressDestFiles;
	
	}
	

		public String clickDirectSubmit() throws Exception
		{
			Boolean flag=true;
			String message="";
			if(!element("td.noRecordsFound").isDisplayed())
			{
				selectFormInfoInDialogue();
				IWebElementWrapper exportEle=element("td.directSubmitButton",title);
				if(exportEle.isEnabled())
				{
					logger.info("click force direct Submit button");
					
					exportEle.click();
					loadingDlg();
					flag=getTipMessageStatus();
					ForceDirectSubmitDialog forceSubmit=new ForceDirectSubmitDialog(getWebDriverWrapper(),getTestDataManager());
					if(forceSubmit.isThisPage())
					{
						forceSubmit.typeSubmitComment();
						message=forceSubmit.clickSubmit();
						if(forceSubmit.isThisPage())
						{
							forceSubmit.closeThisPage();
						}
					}
					
					if(isThisPage())
					{
						closeThisPage();
					}				
					//click log buttkon
					if(element("td.logButton").isDisplayed() && element("td.logButton").isEnabled())
					{
						element("td.logButton").click();
						loadingDlg();
					}
					if(isThisPage())
					{
						closeThisPage();
					}
					
					if(element("fipf.formInstTitleLabels").isDisplayed())
					{
						FormInstancePage fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager());// unlock return
						fip.unlockForm();
						fip.closeThisPage();
						fip=null;
					}
					
				}else
				{
					logger.error("error: export button is disable.");
					flag=false;
				}
			}else
			{
				logger.error("error: no records found.");
				flag=false;
			}
			
			
			return message;
		
		}
		
	/**
	 * click export button, return true if export without errors, others return false.
	 * @author kun shen
	 * @throws Exception
	 */
	private Boolean clickExport() throws Exception
	{
		Boolean flag=true;
		
		if(!element("td.noRecordsFound").isDisplayed())
		{
			selectFormInfoInDialogue();
			IWebElementWrapper exportEle=element("td.exportButton",title);
			if(exportEle.isEnabled())
			{
				logger.info("click export/force button");
				/*exportEle.click();
				loadingDlg();
				flag=getTipMessageStatus();
				ForceSubmitCommonDialog forceSubmit=new ForceSubmitCommonDialog(getWebDriverWrapper(),getTestDataManager());
				forceSubmit.typeSubmitComment();*/
				if (PropHelper.ENABLE_FILE_DOWNLOAD)
				{
					String exportBtnId=exportEle.getAttribute("id");
					if(exportBtnId.contains("forceS"))//forceSubmitBtn, seperate from foceDirectSubmitBtn
					{
						exportEle.click();
						loadingDlg();
						flag=getTipMessageStatus();
						ForceSubmitCommonDialog forceSubmit=new ForceSubmitCommonDialog(getWebDriverWrapper(),getTestDataManager());
						forceSubmit.typeSubmitComment();
						TestCaseManager.getTestCase().startTransaction("");
						TestCaseManager.getTestCase().setPrepareToDownload(true);
						TestCaseManager.getTestCase().setDownloadFileFormat(FileFormat.BINARY);
						
						forceSubmit.clickSubmit();
						TestCaseManager.getTestCase().stopTransaction();
					}else
					{
						TestCaseManager.getTestCase().startTransaction("");
						TestCaseManager.getTestCase().setPrepareToDownload(true);
						TestCaseManager.getTestCase().setDownloadFileFormat(FileFormat.BINARY);
						
						exportEle.click();
						loadingDlg();
						flag=getTipMessageStatus();
						TestCaseManager.getTestCase().stopTransaction();
					}
					
				}
				else
				{
					exportEle.click();
					loadingDlg();
					flag=getTipMessageStatus();
					ForceSubmitCommonDialog forceSubmit=new ForceSubmitCommonDialog(getWebDriverWrapper(),getTestDataManager());
					if(forceSubmit.isThisPage())
					{
						forceSubmit.typeSubmitComment();
						forceSubmit.clickSubmit();
						if(forceSubmit.isThisPage())
						{
							forceSubmit.closeThisPage();
						}
					}
				}
				
				if(isThisPage())
				{
					closeThisPage();
				}				
				//click log buttkon
				if(element("td.logButton").isDisplayed() && element("td.logButton").isEnabled())
				{
					element("td.logButton").click();
					loadingDlg();
				}
				if(isThisPage())
				{
					closeThisPage();
				}
				
				if(element("fipf.formInstTitleLabels").isDisplayed())
				{
					FormInstancePage fip=new FormInstancePage(getWebDriverWrapper(),getTestDataManager());// unlock return
					fip.unlockForm();
					fip.closeThisPage();
					fip=null;
				}
				
			}else
			{
				logger.error("error: export button is disable.");
				flag=false;
			}
		}else
		{
			logger.error("error: no records found.");
			flag=false;
		}
		return flag;
	}
	

	/**
	 * click export button, return true if export without errors, others return false.
	 * @author kun shen
	 * @throws Exception
	 */
	private Boolean clickExportToDataSchedule(Boolean combined) throws Exception
	{
		Boolean flag=true;
		
		if(!element("td.noRecordsFound").isDisplayed())
		{
			selectFormInfoInDialogue();
			IWebElementWrapper exportEle=element("td.exportButton",title);
			if(exportEle.isEnabled())
			{
				logger.info("click export/force button");
				
				exportEle.click();
				loadingDlg();
				flag=getTipMessageStatus();
				ForceSubmitCommonDialog forceSubmit=new ForceSubmitCommonDialog(getWebDriverWrapper(),getTestDataManager());
				if(forceSubmit.isThisPage())
				{
					forceSubmit.typeSubmitComment();
					forceSubmit.clickDataScheduleSubmit();
				}
				//click log button
				if(element("td.logButton").isDisplayed() && element("td.logButton").isEnabled())
				{
					element("td.logButton").click();
					loadingDlg();
				}
				
				if(!flag){return flag;}
				
				String jobRunType="Export";//job start with Export, just used for logging
				String prefixOfRegulator=getDBInfo().getRegulatorPrefix(form.getRegulator());
				String jobName=prefixOfRegulator+"|"+form.getEntity()+"|"+form.getName()+"|"+form.getVersion().substring(1);
				
				JobResultDialog jrd=new JobResultDialog(getWebDriverWrapper(),getTestDataManager());
				lockDownloadDir(downloadFolder);//relock it after new jobResultDialog
				String status=jrd.waitJobResult(jobName, form.getProcessDate(), jobRunType);
				
				IWebElementWrapper _exportFileLoation=element("filf.exportFileLoation",form.getName(),form.getVersion().substring(1),form.getProcessDate());
				if(_exportFileLoation.isPresent() && _exportFileLoation.isDisplayed())
				{
					_exportFileLoation.click();
					loadingDlg();
					ExportedFileLocationDialog efld=new ExportedFileLocationDialog(getWebDriverWrapper(),getTestDataManager());
					String downloadFileName_Server=efld.exportedFileName();
					getDownloadFromServerToLocalSSH(prefixOfRegulator,status,downloadFileName_Server,combined);
					
				}else
				{
					if(status.startsWith("fail"))
					{
						getDownloadFromServerToLocalSSH(prefixOfRegulator,status,form.getExpectationFile(),combined);
					}else
					{
						logger.error("error: job failed and no exported file.");
						flag=false;
					}
					
				}
							
				if(forceSubmit.isThisPage())
				{forceSubmit.closeThisPage();}
			}else
			{
				logger.error("error: export button is disable.");
				flag=false;
			}
		}else
		{
			logger.error("error: no records found.");
			flag=false;
		}
		return flag;
	}
	//TODO judge status with FAILURE: Export file failed XML schema validation!
	private void getDownloadFromServerToLocalSSH(String prefixOfRegulator,String statusType, String downloadFileName_Server,Boolean combined) throws Exception
	{
		String returnZipFile=null;
		logger.info("start downloading export file from server to local.");
		String statusTypeL=statusType.toLowerCase();
		ServerInfo serverInfo=new ServerInfo(getDBInfo().getApplicationServer_Key());
		String processDate=uniformDate(form.getProcessDate(),"YYYYMMDD");
		String downloadPath_Server=serverInfo.getDownloadPath()+"/Submission/"+prefixOfRegulator+"/"+form.getEntity()+"/"+processDate+"/";
		Boolean searchCombinedFile=false;
		String downloadPath_combine_Server=downloadPath_Server+"combine/";
		String downloadPath_validationErrors_Server=downloadPath_Server+"ValidationErrors/";
		if(combined && form.getExpectationFile().toUpperCase().contains("COMBINE"))
		{
			searchCombinedFile=true;
			if(downloadFileName_Server.contains(";"))
			{
				String[] downloadFileArr_server=downloadFileName_Server.split(";");
				for(String tmp:downloadFileArr_server)
				{
					if(!tmp.toLowerCase().contains("combine"))
					{
						downloadFileName_Server=tmp;
						break;
					}
				}
			}
			
		}
		
		if(statusTypeL.startsWith("fail") && !searchCombinedFile)
		{
			downloadPath_Server=downloadPath_validationErrors_Server;			
		}
		if(serverInfo.getDownloadPath().contains("\\"))
		{
			downloadPath_Server=downloadPath_Server.replace("/", "\\");
			//download from Windows
			if(statusTypeL.startsWith("pass") || statusTypeL.startsWith("fail"))
			{
				logger.info("start downloading file from windows server: name["+downloadPath_Server+"]");
				FileUtil.copyToNewFile(downloadPath_Server,downloadFolder,downloadFileName_Server);
				logger.info("stop downloading export file from server to local.");
			}else
			{
				logger.error("error: no export file exists in windows server["+downloadPath_Server+"].");
			}
		}else
		{

			logger.info("start downloading file from Linux server: ["+downloadPath_Server+"]");
			if(JschUtil.connect(serverInfo.getUser(), serverInfo.getPassword(), serverInfo.getHost(), serverInfo.getPort()))
			{
				ArrayList<String> downloadFiles=new ArrayList<String>();
				
				if(searchCombinedFile){
					if(statusTypeL.startsWith("pass") && JschUtil.execCmd("cd "+downloadPath_combine_Server)==0 && JschUtil.downloadFileToLocal(downloadPath_combine_Server+downloadFileName_Server, downloadFolder)){
						//if(JschUtil.execCmd("mv -f "+downloadPath_combine_Server+downloadFileName_Server+" "+downloadPath_combine_Server+downloadFileName_combine_Server)==0 && JschUtil.downloadFileToLocal(downloadPath_combine_Server+downloadFileName_combine_Server, downloadFolder))
						String exportfile=exportToFile();
						String exportfileRename=FileUtil.createNewFileWithSuffix(exportfile,"_combine",null);
						new File(exportfile).renameTo(new File(exportfileRename));
						logger.info("download file(combine):"+exportfileRename);
						downloadFiles.add(exportfileRename);
						
					}
					if(statusTypeL.startsWith("fail") && JschUtil.execCmd("cd "+downloadPath_validationErrors_Server)==0 && JschUtil.downloadFileToLocal(downloadPath_validationErrors_Server+downloadFileName_Server, downloadFolder))
					{
						String exportfile=exportToFile();
						String exportfileRename=FileUtil.createNewFileWithSuffix(exportfile,"_combine",null);
						new File(exportfile).renameTo(new File(exportfileRename));
						logger.info("download file(ValidationErrors):"+exportfileRename);
						downloadFiles.add(exportfileRename);
						/*String downloadFileName_validationErrors_Server=downloadFileName_Server.substring(0,downloadFileName_Server.lastIndexOf(".")).concat("_combine.xml");
						if(JschUtil.execCmd("mv -f "+downloadPath_validationErrors_Server+downloadFileName_Server+" "+downloadPath_validationErrors_Server+downloadFileName_validationErrors_Server)==0 && JschUtil.downloadFileToLocal(downloadPath_validationErrors_Server+downloadFileName_validationErrors_Server, downloadFolder))
						{}*/
					}
				}
				
				if(JschUtil.execCmd("cd "+downloadPath_Server)==0){
					if(JschUtil.downloadFileToLocal(downloadPath_Server+downloadFileName_Server, downloadFolder)){
						String exportfile=exportToFile();
						returnZipFile=exportfile.substring(0,exportfile.lastIndexOf(".")).concat(".zip");
						logger.info("download file:"+exportfile);
						downloadFiles.add(exportfile);
					}else{
						if(searchCombinedFile && JschUtil.downloadFileToLocal(downloadPath_validationErrors_Server+downloadFileName_Server, downloadFolder)){
							String exportfile=exportToFile();
							returnZipFile=exportfile.substring(0,exportfile.lastIndexOf(".")).concat(".zip");
							logger.info("download file(combinesituation,no combined file with ValidationErrors):"+exportfile);
							downloadFiles.add(exportfile);
						}
					}
						
				}else{
					logger.error("error: no such directory["+downloadPath_Server+"] in remote linux server.");
				}
				JschUtil.close();
				logger.info("stop downloading export file from server to local.");
				if(downloadFiles.size()>1)
				{
					
					FileUtil.ZipFiles(downloadFiles, returnZipFile);
				}
				
			}else
			{
				logger.error("error: fail to connect to remote server.");
			}			
		
		}
		
	}
	
	private Boolean selectFormInfoInDialogue() throws Exception
	{
		Boolean flag=false;
		IWebElementWrapper element=element("td.selectCheckBox");
		String attribute=element.getAttribute("class");
		if(!attribute.contains("ui-state-active"))//span:ui-icon-check,div:ui-state-active
		{
			//element.checkByJavaScript(true);
			element.click();
			loadingDlg();
			flag=true;
		}else
		{
			flag=true;
		}
		
		return flag;
	}
	
}
