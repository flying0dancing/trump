package com.lombardrisk.pages;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.PageBase;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.test.DBInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Refactored by Leo Tu on 1/29/16
 */
public abstract class AbstractPage extends PageBase
{
	protected static String format=DBInfo.getLanguage();
	protected static boolean httpDownload = Boolean.parseBoolean(PropHelper.getProperty("download.enable").trim());
	protected final static String LOCKNAME="TP.lock";
	protected String downloadFolder;
	public enum Month
	{
		JANUARY("Jan",1),FEBRUARY("Feb",2),MARCH("Mar",3),APRIL("Apr",4),MAY("May",5),JUNE("Jun",6),JULY("Jul",7),AUGUST("Aug",8),SEPTEMBER("Sep",9),OCTOBER("Oct",10),NOVEMBER("Nov",11),DECEMBER("Dec",12);
		private int monthNumber;
		private String monthText;
		private Month(String monthText,int monthNumber)
		{
			this.monthText=monthText;
			this.monthNumber=monthNumber;
		}
		public String getMonthText()
		{
			return monthText;
		}
		public int getMonthNumber()
		{
			return monthNumber;
		}
		public void setMonthText(String monthText)
		{
			this.monthText=monthText;
		}
		public void setMonthNumber(int monthNumber)
		{
			this.monthNumber=monthNumber;
		}
		
	}
	
	/**
	 * 
	 * @param webDriverWrapper
	 */
	public AbstractPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
		if(PropHelper.ENABLE_FILE_DOWNLOAD)
		{
			downloadFolder=new File(PropHelper.DOWNLOAD_FOLDER).getAbsolutePath();
			if(!new File(PropHelper.DOWNLOAD_FOLDER).exists())
			{
				FileUtil.createDirectory(downloadFolder);
			}
		}else
		{
			downloadFolder=System.getProperty("user.home")+"/downloads/";
		}
		if(new File(downloadFolder+"/"+LOCKNAME).exists())
		{
			new File(downloadFolder+"/"+LOCKNAME).delete();
		}
	}

	/**
	 * Get all files in specific folder
	 * 
	 * @param path
	 * @param files
	 * @return All files(List)
	 */
	protected static List<File> getFiles(String path, List<File> files)
	{
		File realFile = new File(path);
		if (realFile.isDirectory())
		{
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles)
			{
				if (file.isDirectory())
				{
					getFiles(file.getAbsolutePath(), files);
				}
				else
				{
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * Sort files by modified time
	 * 
	 * @param path
	 * @return Files(List)
	 */
	protected static List<File> sortFileByModifiedTime(String path)
	{

		List<File> list = getFiles(path, new ArrayList<File>());

		if (list != null && list.size() > 0)
		{
			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile)
				{
					if (file.lastModified() < newFile.lastModified())
					{
						return 1;
					}
					else if (file.lastModified() == newFile.lastModified())
					{
						return 0;
					}
					else
					{
						return -1;
					}

				}
			});

		}

		return list;
	}

	/**
	 * Get latest file in specific folder
	 * 
	 * @param path
	 * @return Latest file
	 */
	protected static String getLatestFile(String path)
	{
		List<File> files = sortFileByModifiedTime(path);
		try
		{
			return files.get(0).toString();
		}
		catch (Exception e)
		{
			return "";
		}
	}

	

	/**
	 * Wait for ajax dialog disappear
	 * 
	 * @throws Exception
	 */
	@Deprecated
	protected void waitStatusDlg() throws Exception
	{
		waitThat("ap.ajaxstatusDlg").toBeInvisible();
		waitThat("ap.ajaxstatusDlg2").toBeInvisible();
		Thread.sleep(500);
	}
	
	/**
	 * Wait for ajax dialog disappear
	 * 
	 * @throws Exception
	 */
	protected void loadingDlg() throws Exception
	{
		waitThat().timeout(2000);
		waitThat("abstract.ajaxstatusDlg").toBeInvisible();
		waitThat().timeout(2000);
	}
	/**
	 * Wait for ajax dialog disappear
	 * 
	 * @throws Exception
	 */
	protected void loadingDlg(long timeout) throws Exception
	{
		waitThat().timeout(2000);
		waitThat("abstract.ajaxstatusDlg").toBeInvisible();
		waitThat().timeout(timeout);
	}
	/**
	 * Wait for elements loaded
	 * 
	 * @throws Exception
	 */
	protected void waitForPageLoaded() throws Exception
	{
		String js = "return document.readyState";
		boolean rst = executeScript(js).equals("complete");
		while (!rst)
		{
			//logger.info("Current status is[" + executeScript(js) + "],loading...");
			logger.info("Current status is loading...");
			Thread.sleep(300);
			rst = executeScript(js).equals("complete");
		}
		Thread.sleep(300);
	}

	/**
	 * Wait for dropList Loaded
	 * 
	 * @param locator
	 * @throws Exception
	 */
	@Deprecated
	protected void waitForDropListLoaded(String locator) throws Exception
	{
		long init = System.currentTimeMillis();
		int i = 0;
		while (i < 5)
		{
			try
			{
				if (element(locator).getAllOptions().size() == 0)
				{

				}
				long cur = System.currentTimeMillis();
				if ((cur - init) / 1000 > 5)
					break;
			}
			catch (StaleElementReferenceException e)
			{
				i++;
				logger.info("Try again");
			}
		}

	}

	
	/**
	 * Click EnterKey
	 * 
	 * @throws Exception
	 */
	@Deprecated
	protected void clickEnterKey() throws Exception
	{
		actions().sendKeys(Keys.ENTER).perform();
		Thread.sleep(500);
	}

	/**
	 * Select date
	 * @author kun shen
	 * @param date
	 * @throws Exception
	 */
	protected void selectDate(String date) throws Exception
	{
		String year = null;
		String month = null;
		String day = null;
		int monthNumber=0;
		//String format=PropHelper.getProperty("Regional.language").trim();
		if(format==null || format.trim().equals("")){
			format=PropHelper.getProperty("Regional.language")==null?"":PropHelper.getProperty("Regional.language").trim();
		}
		if (format.equalsIgnoreCase("en_US") || format.equals(""))
		{
			month = date.substring(0, 2);
			day = date.substring(3, 5);
			year = date.substring(6);
		}else if (format.equalsIgnoreCase("en_GB"))
		{
			day = date.substring(0, 2);
			month = date.substring(3, 5);
			year = date.substring(6);
		}else if (format.equalsIgnoreCase("zh_CN"))
		{
			year = date.substring(0, 4);
			month = date.substring(5, 7);
			day = date.substring(8);
		}
		if (day.startsWith("0"))
		{
			day = day.substring(1);
		}
		if(month.startsWith("0"))
		{
			monthNumber=Integer.valueOf(month.substring(1));
		}
		else
		{
			monthNumber=Integer.valueOf(month);
		}
		for(Month m:Month.values())
		{
			if(m.getMonthNumber()==monthNumber)
			{
				month=m.getMonthText();
				break;
			}
		}

		if(selectIt(element("abstract.calendar.month"),month))
		{
			if(selectIt(element("abstract.calendar.year"),year))
			{
				element("abstract.calendar.day", day,day).click();
				waitThat("abstract.calendar.day", day,day).toBeInvisible();
				loadingDlg();
			}else
			{
				Assert.fail("cannot select year:"+year);
			}
		}else
		{
			Assert.fail("cannot select month:"+month);
		}
	}


	/**
	 * Click current date
	 * 
	 * @throws Exception
	 */
	@Deprecated
	protected void clickCurrentDate() throws Exception
	{
		for (int r = 1; r <= 6; r++)
		{
			boolean clicked = false;
			for (int c = 1; c <= 7; c++)
			{
				String[] list =
				{ String.valueOf(r), String.valueOf(c) };
				if (element("ap.calendar", list).getAttribute("class").contains("ui-datepicker-current-day"))
				{
					element("ap.calendar", list).click();
					clicked = true;
					break;
				}
			}
			if (clicked)
				break;
		}
	}

	
	/**
	 * get downloaded file
	 * @author Leo Tu
	 * @param exportType
	 * @param LatestFileName
	 * @param dir
	 * @return downloaded file
	 * @throws Exception
	 */
	@Deprecated
	protected String downloadFile(String exportType, String LatestFileName, String dir) throws Exception
	{
		if (exportType == null)
			exportType = "";
		String filePath = null;
		if (dir == null)
			return null;
		String fileName = null;
		boolean flag = true;
		long statTime = System.currentTimeMillis();

		while (flag)
		{
			fileName = getLatestFile(dir);
			long curTime = System.currentTimeMillis();
			if (!fileName.equalsIgnoreCase(LatestFileName) && !fileName.endsWith(".tmp") && !fileName.endsWith(".crdownload"))
			{
				flag = false;
			}
			else if ((curTime - statTime) / 1000 > 300)
			{
				flag = false;
				fileName = null;
			}
			else
			{
				logger.info("Downloading");
				waitThat().timeout(5000);
			}
		}
		if (exportType.toLowerCase().startsWith("ds"))
		{
			filePath = fileName;
		}
		else
		{
			File exportedFile = new File("target\\result\\data\\download\\" + new File(fileName).getName());
			if (exportedFile.exists())
				exportedFile.delete();
			if (fileName != null)
				FileUtils.copyFile(new File(fileName), exportedFile);
			filePath = exportedFile.getAbsolutePath();
		}
		Thread.sleep(3000);

		return filePath;
	}
	/** download file return download file
	 * @author kun shen
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	protected String downloadFile(String dir) throws Exception
	{
		if (dir == null)
			return null;
		String fileName = null;
		long latestFileLockedTime=new File(dir+"/"+LOCKNAME).lastModified();
		boolean flag = true;
		
		while(flag)
		{
			fileName = getLatestFile(dir);
			long latestFileTime=new File(fileName).lastModified();
			if(latestFileLockedTime==latestFileTime)
			{
				logger.info("Downloading");
				Thread.sleep(1000);
				continue;
			}else
			{
				if(!fileName.endsWith(".tmp") && !fileName.endsWith(".crdownload"))
				{
					flag = false;
				}
			}
		}
		
		return fileName;
	}
/** move file to expectation download folder
 * @author kun shen
 * @param fileFullPath
 * @param destFolder
 * @return
 * @throws Exception
 */
	protected String moveDownloadFileToExpectedFolder(String fileFullPath, String destFolder) throws Exception
	{
		String destFileFullPath=null;
		if(fileFullPath!=null && destFolder!=null)
		{
			File sourceFile=new File(fileFullPath);
			if(sourceFile.exists())
			{
				if(!new File(destFolder).isDirectory())
				{
					FileUtil.createDirectory(destFolder);
				}
				String fileName=sourceFile.getName();
				String fileName_Prefix=fileName.substring(0, fileName.lastIndexOf("."));
				String fileName_Prefix_Tmp=fileName_Prefix;
				String fileName_Suffix=fileName.replace(fileName_Prefix, "");
				
				int i=1;
				while(new File(destFolder+fileName_Prefix_Tmp+fileName_Suffix).exists())
				{
					fileName_Prefix_Tmp=fileName_Prefix+"("+String.valueOf(i)+")";
					i++;
				}
				File destFile=new File(destFolder+fileName_Prefix_Tmp+fileName_Suffix);
				FileUtils.moveFile(sourceFile, destFile);
				destFileFullPath=destFile.getAbsolutePath();
			}
		}
		return destFileFullPath;
	}

	/**
	 * rename file
	 * @author Leo Tu
	 * @param path
	 * @param oldname
	 * @param newname
	 */
	protected String  renameFile(String path, String oldname, String newname)
	{
		String fileFullPath=null;
		Boolean flag=true;
		if (!oldname.equals(newname))
		{
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			if (!oldfile.exists())
			{
				return null;
			}
			if (newfile.exists())
			{
				logger.warn("The file already exist, old file will be deleted");
				flag=newfile.delete();
			}
			if(flag)
			{
				if(oldfile.renameTo(newfile))
				{
					fileFullPath=newfile.getAbsolutePath();
				}
			}
			
		}
		else
		{
			logger.error("New file name is same with old one!");
		}
		return fileFullPath;
	}
	
	/**
	 * rename file
	 * @author kun shen
	 * @param path
	 * @param oldname
	 * @param newname
	 */
	protected String  renameFile(String oldFileFullPath, String newNameWithoutSuffix)
	{
		String newFileFullPath=null;
		File oldFile=new File(oldFileFullPath);
		if(oldFile.exists())
		{
			String fileName=oldFile.getName();
			String fileName_Prefix=fileName.substring(0, fileName.lastIndexOf("."));
			String fileName_Prefix_Tmp=newNameWithoutSuffix+"_"+fileName_Prefix;
			String fileName_Suffix=fileName.replace(fileName_Prefix, "");
			String filePath=oldFileFullPath.replace(fileName, "");
			newFileFullPath=filePath+fileName_Prefix_Tmp+fileName_Suffix;
			int i=1;
			while(new File(newFileFullPath).exists())
			{
				fileName_Prefix_Tmp=newNameWithoutSuffix+"_"+fileName_Prefix+"("+String.valueOf(i)+")";
				newFileFullPath=filePath+fileName_Prefix_Tmp+fileName_Suffix;
				i++;
			}
		}else
		{
			logger.error("No File Found "+oldFileFullPath);
		}
		
		return newFileFullPath;
	}
	
	/**
	 * select options in select element. 
	 * @author kun shen
	 * @param element select element
	 * @param it the searched option value
	 * @return return true if selected, return false may be the option doens't exist or the select element is wrong.
	 * @throws Exception
	 */
	protected Boolean selectIt(IWebElementWrapper element,String it) throws Exception
	{
		Boolean flag=false;
		if(it==null || it.trim().equals("")){return false;}
		if (!element.getSelectedText().equals(it))
		{
			List<String> list=element.getAllOptionTexts();
			for(String item:list)
			{
				if(it.trim().equalsIgnoreCase(item.trim()))
				{
					element.selectByVisibleText(item);
					loadingDlg();
					flag=true;
					break;
				}
			}
		}else
		{flag=true;}
		
		if(!flag)
		{
			logger.error("cannot find "+it);
		}

		return flag;
		
	}
	
	protected String uploadFileError(String type) throws Exception
	{
		StringBuffer stringBuffer=new StringBuffer();
		String lineSeparator=System.getProperty("line.separator");
		Thread.sleep(500);
		waitThat("abstract.importFileUploading",type).toBeInvisible();
		if(element("abstract.importFileUploadMessge1",type).isPresent())
		{
			List<IWebElementWrapper> elements=element("abstract.importFileUploadMessge1Txt",type).getAllMatchedElements();
			int errCount=elements.size();
			for(int i=0;i<errCount;i++)
			{
				List<IWebElementWrapper> elementsDetail=element("abstract.importFileUploadMessge1Detail",type,String.valueOf(i+1)).getAllMatchedElements();
				for(IWebElementWrapper element:elementsDetail)
				{
					stringBuffer.append(element.getInnerText()+" ");
				}
				stringBuffer.append(lineSeparator);
			}
		}
		if(element("abstract.importFileUploadMessge2",type).isPresent())
		{

			List<IWebElementWrapper> elements=element("abstract.importFileUploadMessge2Txt",type).getAllMatchedElements();
			int errCount=elements.size();
			for(int i=0;i<errCount;i++)
			{
				List<IWebElementWrapper> elementsDetail=element("abstract.importFileUploadMessge2Detail",type,String.valueOf(i+1)).getAllMatchedElements();
				for(IWebElementWrapper element:elementsDetail)
				{
					stringBuffer.append(element.getInnerText()+" ");
				}
				stringBuffer.append(lineSeparator);
			}
		}
		String errorText=element("abstract.errorTextarea",type).getInnerText().trim();
		if(errorText!=null && errorText.length()>0)
		{
			stringBuffer.append(errorText);
		}
		String returnValue=stringBuffer.toString().trim();
		if(returnValue.equals(""))
		{
			returnValue=null;
		}
		return returnValue;
	}
	/** lock download folder and create a lock.  return true for set a lock, return false for not set a lock.
	 * @author kun shen
	 * @param downloadDirectory
	 * @param timeout seconds
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Deprecated
	protected Boolean lockDownloadDir(String downloadDirectory, int timeout) throws InterruptedException, IOException
	{
		Boolean lockNow=false;
		Boolean lock=true;
		if(new File(downloadDirectory).isDirectory())
		{
			int count=0;
			File lockFile=new File(downloadDirectory+"/"+LOCKNAME);
			//waiting 3 minutes
			if(timeout<180){timeout=180;}
			while(count<=timeout)
			{
				if(!lockFile.exists())
				{
					lock=false;
					break;
				}
				Thread.sleep(1000);
				count++;
			}
			
			if(!lock)
			{
				lockNow=lockFile.createNewFile();
			}
			
		}
		return lockNow;
	}
	
	/** lock download folder and create a lock.  return true for set a lock, return false for not set a lock.
	 * @author kun shen
	 * @param downloadDirectory
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected Boolean lockDownloadDir(String downloadDirectory) throws InterruptedException, IOException
	{
		Boolean lockNow=false;
		Boolean lock=true;
		if(new File(downloadDirectory).isDirectory())
		{
			File lockFile=new File(downloadDirectory+"/"+LOCKNAME);
			
			while(true)
			{
				if(!lockFile.exists())
				{
					lock=false;
					break;
				}
				Thread.sleep(500);
			}
			
			if(!lock)
			{
				lockNow=lockFile.createNewFile();
			}
			
		}
		return lockNow;
	}
	/**unclock Download folder return true if unlock successfully, others return false.
	 * @author kun shen
	 * @param downloadDirectory
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected Boolean unlockDownloadDir(String downloadDirectory) throws InterruptedException, IOException
	{
		Boolean unlock=false;

		if(new File(downloadDirectory).isDirectory())
		{
			File lockFile=new File(downloadDirectory+"/"+LOCKNAME);
			if(lockFile.exists())
			{
				while(lockFile.exists())
				{
					if(lockFile.delete())
					{
						unlock=true;
						break;
					}
					Thread.sleep(1000);
				}
			}else
			{
				unlock=true;
			}
			
		}
		return unlock;
	}

	/**
	 * get download file in original download folder.<br> If successfully return full file path, others return null.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	protected String exportToFile() throws Exception
	{
		String filePath=null;
		if (PropHelper.ENABLE_FILE_DOWNLOAD)
		{
			String exportedFile = TestCaseManager.getTestCase().getDownloadFile();
			String oldName = new File(exportedFile).getName();
			String fileName = TestCaseManager.getTestCase().getDefaultDownloadFileName();
			filePath=renameFile(downloadFolder, oldName, fileName);
		}
		else
		{
			filePath = downloadFile(downloadFolder);
		}
		
		return filePath;
	}
	
	protected void refreshPage() throws Exception
	{
		getWebDriverWrapper().navigate().refresh();
		waitForPageLoaded();
	}
	
	/**
	 * for case-insensitive
	 * @author kun shen
	 * @param element
	 * @param importText
	 * @return
	 */
	protected String getRealText(IWebElementWrapper element,String importText)
	{
		String label=null;
		Boolean flag=false;
		List<IWebElementWrapper> elements=element.getAllMatchedElements();
		for(IWebElementWrapper elementT:elements)
		{
			label=elementT.getInnerText();
			Pattern p = Pattern.compile("("+importText+")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
			Matcher m = p.matcher(label);
			if(m.find())
			{
				label=m.group(1);
				flag=true;
				break;
			}
			
		}
		
		if(!flag){label=importText;}
		return label;
	}
	
	/**
	 * get element's all inner text, and then concat them in one string.
	 * @param element
	 * @return
	 * @throws Exception
	 */
	protected String getAllInnerText(IWebElementWrapper element) throws Exception
	{
		List<String> messages=element.getAllInnerTexts();
		StringBuffer messageText=new StringBuffer("");
		if(messages.size()>0)
		{
			for(String message:messages)
			{
				messageText.append(message.trim());
			}
		}
		return messageText.toString();
	}
}
