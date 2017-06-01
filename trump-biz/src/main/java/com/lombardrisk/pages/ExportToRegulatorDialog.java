package com.lombardrisk.pages;

import java.util.ArrayList;
import java.util.List;

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
			if(title.equalsIgnoreCase("Export to DataSchedule"))
			{
				flag=clickExportToDataSchedule();
			}else
			{
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
			if(element("td.exportButton",title).isEnabled())
			{
				logger.info("click export button");
				
				if (PropHelper.ENABLE_FILE_DOWNLOAD)
				{
					TestCaseManager.getTestCase().startTransaction("");
					TestCaseManager.getTestCase().setPrepareToDownload(true);
					TestCaseManager.getTestCase().setDownloadFileFormat(FileFormat.BINARY);
					
					element("td.exportButton",title).click();
					loadingDlg();
					
					if(element("abstract.message").isPresent())
					{
						logger.error(element("abstract.message").getInnerText());
						waitThat("abstract.message").toBeInvisible();
						flag=false;
					}
					TestCaseManager.getTestCase().stopTransaction();
					
				}
				else
				{
					element("td.exportButton",title).click();
					loadingDlg();
					if(element("abstract.message").isPresent()){logger.error(element("abstract.message").getInnerText());waitThat("abstract.message").toBeInvisible();flag=false;}
				}
				//click log buttkon
				if(element("td.logButton").isDisplayed() && element("td.logButton").isEnabled())
				{
					element("td.logButton").click();
					loadingDlg();
				}
				/*if(messageFlag){return flag;}
				String a=getLatestFile(downloadFolder);
				String b=a.substring(a.lastIndexOf(System.getProperty("file.separator"))+1);
				if(b.equalsIgnoreCase(LOCKNAME))
				{
					logger.error("error: not find download file.");
				}else
				{
					flag=true;
				}	*/
			}else
			{
				logger.error("error: export button is disable.");
			}
		}else
		{
			logger.error("error: no records found.");
		}
		return flag;
	}
	

	/**
	 * click export button, return true if export without errors, others return false.
	 * @author kun shen
	 * @throws Exception
	 */
	private Boolean clickExportToDataSchedule() throws Exception
	{
		Boolean flag=true;
		
		if(!element("td.noRecordsFound").isDisplayed())
		{
			if(element("td.exportButton",title).isEnabled())
			{
				logger.info("click export button");
				
				element("td.exportButton",title).click();
				loadingDlg();
				if(element("abstract.message").isPresent())
				{
					logger.info(element("abstract.message").getInnerText());
					waitThat("abstract.message").toBeInvisible(); 
					flag=false;
				}
				
				//click log button
				if(element("td.logButton").isDisplayed() && element("td.logButton").isEnabled())
				{
					element("td.logButton").click();
					loadingDlg();
				}
				
				if(!flag){return flag;}
				
				String jobRunType="ExportJob";
				String prefixOfRegulator=getDBInfo().getRegulatorPrefix(form.getRegulator());
				String jobName=prefixOfRegulator+"|"+form.getEntity()+"|"+form.getName()+"|"+form.getVersion().substring(1);
				
				JobResultDialog jrd=new JobResultDialog(getWebDriverWrapper(),getTestDataManager());
				lockDownloadDir(downloadFolder);//relock it after new jobResultDialog
				String status=jrd.waitJobResult(jobName, form.getProcessDate(), jobRunType);
				
				IWebElementWrapper _exportFileLoation=element("filf.exportFileLoation", form.getEntity(),form.getName(),form.getVersion().substring(1),form.getProcessDate());
				if(_exportFileLoation.isPresent() && _exportFileLoation.isDisplayed())
				{
					_exportFileLoation.click();
					loadingDlg();
					ExportedFileLocationDialog efld=new ExportedFileLocationDialog(getWebDriverWrapper(),getTestDataManager());
					String downloadFileName_Server=efld.exportedFileName();
					getDownloadFromServerToLocalSSH(prefixOfRegulator,status,downloadFileName_Server);
				}
							
				
			}else
			{
				logger.error("error: export button is disable.");
			}
		}else
		{
			logger.error("error: no records found.");
		}
		return flag;
	}
	//TODO judge status with FAILURE: Export file failed XML schema validation!
	private void getDownloadFromServerToLocalSSH(String prefixOfRegulator,String statusType, String downloadFileName_Server) throws Exception
	{
		logger.info("start downloading export file from server to local.");
		String statusTypeL=statusType.toLowerCase();
		ServerInfo serverInfo=new ServerInfo(getDBInfo().getApplicationServer_Key());
		String processDate=uniformDate(form.getProcessDate(),"YYYYMMDD");
		String downloadPath_Server=serverInfo.getDownloadPath()+"/Submission/"+prefixOfRegulator+"/"+form.getEntity()+"/"+processDate+"/";
		if(statusTypeL.startsWith("fail"))
		{
			downloadPath_Server=serverInfo.getDownloadPath()+"/Submission/"+prefixOfRegulator+"/"+form.getEntity()+"/"+processDate+"/ValidationErrors/";
			downloadFileName_Server=prefixOfRegulator+"_"+form.getEntity()+"_"+form.getTransmission().getModule()+"_"+processDate+"*";
			
		}
		if(serverInfo.getDownloadPath().contains("\\"))
		{
			downloadPath_Server=downloadPath_Server.replace("/", "\\");
		}
		if(statusTypeL.startsWith("fail") || statusTypeL.startsWith("pass"))
		{
			logger.info("start downloading file from server: name["+downloadPath_Server+"]");
			JschUtil.connect(serverInfo.getUser(), serverInfo.getPassword(), serverInfo.getHost(), serverInfo.getPort());
			JschUtil.downloadFileToLocal(downloadPath_Server+downloadFileName_Server, downloadFolder);
			JschUtil.close();
			logger.info("stop downloading export file from server to local.");
		}else
		{
			logger.error("error: no export file exists in server.");
		}
		
	}
	
}