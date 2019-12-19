package com.lombardrisk.pages;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.PageBase;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.google.common.base.Strings;
import com.lombardrisk.commons.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Refactored by Kun Shen on 1/29/16
 */
public abstract class AbstractPage extends PageBase
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static String dateFormat=getFormat();
	protected static String simpleDateFormat=getSimpleDateFormat();
	protected static boolean httpDownload = Boolean.parseBoolean(PropHelper.getProperty("download.enable").trim());//old one
	protected final static String LOCKNAME="TP.lock";
	protected String downloadFolder;
	private ITestDataManager testDataManager;
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
	protected List<String> largeForms= Arrays.asList("MAS649");
	/**
	 * 
	 * @param webDriverWrapper
	 */
	public AbstractPage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
		super(webDriverWrapper);
		this.setTestDataManager(testDataManager);
		if(PropHelper.ENABLE_FILE_DOWNLOAD)
		{
			/*downloadFolder=new File(PropHelper.DOWNLOAD_FOLDER).getAbsolutePath();
			if(!new File(PropHelper.DOWNLOAD_FOLDER).exists())
			{
				FileUtil.createDirectory(downloadFolder);
			}*/
			downloadFolder=PropHelper.DOWNLOAD_FOLDER;
		}else
		{
			downloadFolder=System.getProperty("user.home")+System.getProperty("file.separator")+"downloads"+System.getProperty("file.separator");
		}
		if(!new File(downloadFolder).exists())
		{
			FileUtil.createDirectory(downloadFolder);
		}
		if(new File(downloadFolder+System.getProperty("file.separator")+LOCKNAME).exists())
		{
			new File(downloadFolder+System.getProperty("file.separator")+LOCKNAME).delete();
		}
	}





	
	/**
	 * Wait for ajax dialog disappear
	 * replace by loadingDlg(null,5)
	 * @throws Exception
	 */
	@Deprecated
	protected void loadingDlg() throws Exception
	{
		logger.info("wait loading disappear");
		waitThat().timeout(1800);
		waitThat("abstract.ajaxstatusDlg").toBeInvisible();
		waitThat().timeout(1900);
	}
	/**
	 * Wait for ajax dialog disappear
	 * replace by loadingDlg(null,15)
	 * @throws Exception
	 */
	@Deprecated
	protected void loadingDlg(long timeout) throws Exception
	{
		logger.info("wait loading disappear");
		waitThat().timeout(timeout);
		waitThat("abstract.ajaxstatusDlg").toBeInvisible();
		waitThat().timeout(2000);
	}
	/**
	 * Wait for ajax dialog disappear, and some element appear, element could be null
	 * @param element it can be null, or some element needs to be waiting displayed
	 * @param setT timeout(milliseconds)
	 * @throws Exception
	 */
	protected void loadingDlg(IWebElementWrapper element,int setT) throws Exception
	{
		logger.info("wait loading disappear, or element appear");
		waitThat().timeout(2000);
		if(setT<10){setT=10;}
		while((element("abstract.ajaxstatusDlg").isDisplayed()||(element!=null && !element.isDisplayed())) && setT>0)
		{
			waitThat().timeout(1000);
			setT--;
		}
	}
	/**
	 * Wait for ajax dialog disappear, or some element disappear, element could not be null
	 * @param element it can not be null, or some element needs to be waiting disappeared
	 * @param setT timeout(milliseconds)
	 * @throws Exception
	 */
	protected void loadingDlgDis(IWebElementWrapper element,int setT) throws Exception
	{
		logger.info("wait loading disappear, some element disappear");
		waitThat().timeout(2000);
		if(setT<10){setT=10;}
		while((element("abstract.ajaxstatusDlg").isDisplayed()||(element!=null && element.isDisplayed())) && setT>0)
		{
			waitThat().timeout(1000);
			setT--;
		}
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
	 * uniform date to format MM/DD/YYYY
	 * @param date
	 * @return
	 * @throws Exception
	 */
	protected String uniformDate(String date, String format) throws Exception
	{
		String year = null;
		String month = null;
		String day = null;
		
		if (dateFormat.equalsIgnoreCase("en_GB"))
		{
			day = date.substring(0, 2);
			month = date.substring(3, 5);
			year = date.substring(6);
		}else if (dateFormat.equalsIgnoreCase("zh_CN"))
		{
			year = date.substring(0, 4);
			month = date.substring(5, 7);
			day = date.substring(8);
		}else//dateFormat.equalsIgnoreCase("en_US") || dateFormat.equals("")
		{
			month = date.substring(0, 2);
			day = date.substring(3, 5);
			year = date.substring(6);
		}
		String returnDate=month+"/"+day+"/"+year;
		if(format.equalsIgnoreCase("MM/DD/YYYY"))
		{
			returnDate=month+"/"+day+"/"+year;
		}
		if(format.equalsIgnoreCase("YYYYMMDD"))
		{
			returnDate=year+month+day;
		}
		if(format.equalsIgnoreCase("DD/MM/YYYY"))
		{
			returnDate=day+"/"+month+"/"+year;
		}
		
		return returnDate;
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
		
		if (dateFormat.equalsIgnoreCase("en_GB"))
		{
			day = date.substring(0, 2);
			month = date.substring(3, 5);
			year = date.substring(6);
		}else if (dateFormat.equalsIgnoreCase("zh_CN"))
		{
			year = date.substring(0, 4);
			month = date.substring(5, 7);
			day = date.substring(8);
		}else//dateFormat.equalsIgnoreCase("en_US") || dateFormat.equals("")
		{
			month = date.substring(0, 2);
			day = date.substring(3, 5);
			year = date.substring(6);
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
				loadingDlg(null,5);//loadingDlg();
			}else
			{
				
				Assert.fail("cannot select year:"+year);
			}
		}else
		{
			Assert.fail("cannot select month:"+month);
		}
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
		long latestFileLockedTime=new File(dir+System.getProperty("file.separator")+LOCKNAME).lastModified();
		boolean flag = true;
		int backcount=60;
		while(flag)
		{
			fileName = FileUtil.getLatestFile(dir,"",".ini;.lock",latestFileLockedTime);
			long latestFileTime=new File(fileName).lastModified();
			if(backcount<=0){
				flag=false;
			}
			if(latestFileLockedTime>=latestFileTime)
			{
				logger.info("downloading...");
				Thread.sleep(3000);
				backcount--;
				continue;
			}else
			{
				if(!fileName.endsWith(".tmp") && !fileName.endsWith(".crdownload") && !fileName.endsWith(".part"))
				{
					flag = false;
					logger.info("download file "+fileName);
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
				if(fileName_Prefix.toLowerCase().endsWith(".xbrl")){//for xbrl.gz or xbrl.zip file
					fileName_Prefix=fileName_Prefix.substring(0,fileName_Prefix.lastIndexOf("."));
				}
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
		if (!element.getSelectedText().equalsIgnoreCase(it))
		{
			try{
				element.selectByVisibleText(it.trim());
				loadingDlg(null,15);//loadingDlg(15000);
			}catch(Exception e){}
			finally{
				if(element.getSelectedText().equalsIgnoreCase(it.trim()))
				{
					flag=true;
				}else
				{
					List<String> list=element.getAllOptionTexts();
					if(list.contains(it.trim()))
					{
						element.selectByVisibleText(it.trim());
						loadingDlg(null,5);//loadingDlg();
						flag=true;
					}else
					{
						for(String item:list)
						{
							if(it.trim().equalsIgnoreCase(item.trim()))
							{
								element.selectByVisibleText(item);
								loadingDlg(null,5);//loadingDlg();
								flag=true;
								break;
							}
						}
					}
					
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
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected Boolean lockDownloadDir(String downloadDirectory) throws InterruptedException, IOException
	{
		Boolean lockNow=false;
		Boolean lock=true;
		
		if(PropHelper.ENABLE_FILE_DOWNLOAD){return true;}
		
		if(new File(downloadDirectory).isDirectory())
		{
			File lockFile=new File(downloadDirectory+System.getProperty("file.separator")+LOCKNAME);
			
			while(true)
			{
				if(!lockFile.exists())
				{
					lock=false;
					break;
				}
				Thread.sleep(1500);
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
		
		if(PropHelper.ENABLE_FILE_DOWNLOAD){return true;}
		
		if(new File(downloadDirectory).isDirectory())
		{
			File lockFile=new File(downloadDirectory+System.getProperty("file.separator")+LOCKNAME);
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
			if(exportedFile!=null)
			{
				String oldName = new File(exportedFile).getName();
				String fileName = TestCaseManager.getTestCase().getDefaultDownloadFileName();
				if(fileName.contains("filename")){
					fileName=fileName.substring(0,fileName.indexOf("filename")).trim();
				}
				filePath=FileUtil.renameFile(downloadFolder, oldName, fileName);
			}
			else//"export to data-schedule","Export to XSLT-Combine" use this part
			{
				filePath=downloadFile(downloadFolder);
			}
		}
		else
		{
			filePath = downloadFile(downloadFolder);
		}
		
		return filePath;
	}
	
	public void refreshPage() throws Exception
	{
		getWebDriverWrapper().navigate().refresh();
		waitForPageLoaded();
	}
	public void backwardPage() throws Exception
	{
		getWebDriverWrapper().navigate().backward();
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
	
	protected static String getSimpleDateFormat()
	{
		String simpleDateFormatStr="MM/dd/yyyy HH:mm:ss";
		if (dateFormat.equalsIgnoreCase("en_GB"))
		{
			simpleDateFormatStr="dd/MM/yyyy HH:mm:ss";
		}else if (dateFormat.equalsIgnoreCase("zh_CN"))
		{
			simpleDateFormatStr="yyyy-MM-dd HH:mm:ss";
		}
		return simpleDateFormatStr;
	}
	
	protected static String getFormat()
	{
		String format=PropHelper.getProperty("Regional.language")==null?"en_US":PropHelper.getProperty("Regional.language").trim();
		return format;
	}
	
	protected Date transformStringToDate(String dateStr)
	{
		SimpleDateFormat sdf=new SimpleDateFormat(simpleDateFormat);
		Date date=null;
		try
		{
			date=sdf.parse(dateStr);
			
		}catch (ParseException e)
		{
			logger.info("error: transform String to Date failed.["+e.getMessage()+"]");
		}
		return date;
	}
	
	protected Boolean getTipMessageStatus()
	{
		Boolean flag=true;
		try
		{
			IWebElementWrapper element=element("abstract.message");
			if(element.isPresent() && element.isDisplayed())
			{
				String msgType=element("abstract.messageType").getInnerText();
				String tip=element.getInnerText();
				if(Strings.isNullOrEmpty(msgType) || msgType.equalsIgnoreCase("info"))
				{
					logger.info(tip);
				}else
				{
					logger.error(tip);
					flag=false;
				}
				loadingDlg(null,15);//loadingDlg(10000);
				waitThat("abstract.message").toBeInvisible();
			}
		}catch(Exception e)
		{
			logger.info("message disappears too fast.");
		}
		
		return flag;
	}

	public ITestDataManager getTestDataManager() {
		return testDataManager;
	}

	public void setTestDataManager(ITestDataManager testDataManager) {
		this.testDataManager = testDataManager;
	}
	
	public Boolean applyScaleRadio(String importDialogueName,String applyScaleMode) throws Exception
	{
		Boolean flag=true;
		if(StringUtils.isNotBlank(applyScaleMode))
		{
			String mode=applyScaleMode;
			if(mode.equalsIgnoreCase("y"))
			{
				logger.info("click radio \"Scaled\"");
				element("abstract.applayScale",importDialogueName,"true").click();
				loadingDlg(null,5);//loadingDlg();
				if(!element("abstract.applayScale_status",importDialogueName,"Scaled").isPresent()){
					flag=false;
					logger.error("fail to select radio \"Scaled\"");
				}
			}else if(mode.equalsIgnoreCase("n"))
			{
				logger.info("click radio \"No scale\"");
				element("abstract.applayScale",importDialogueName,"false").click();
				loadingDlg(null,5);//loadingDlg();
				if(!element("abstract.applayScale_status",importDialogueName,"No Scale").isPresent()){
					flag=false;
					logger.error("fail to select radio \"No scale\"");
				}
			}else{
				flag=false;
				logger.warn("wrong value in column applyScale, should be y or n, also could be empty.");
			}
			
		}else{
			if(element("abstract.applayScale_status",importDialogueName,"No Scale").isPresent()){
				logger.info("use default setting \"No scale\"");
			}else{
				logger.info("use default setting \"Scaled\"");
			}
			
		}
		
		return flag;
	}

	public void loadingAndWaitLargeForms(String formName,int times) throws Exception {
		if(largeForms.contains(formName)){
			for(int i=0;i<times;i++){
				loadingDlg(null,50);
				waitThat().timeout(10000);
			}
		}
		loadingDlg(null,50);
	}

	/**
	 * this for loading element at its first index
	 * @param element
	 * @return
	 * @throws Exception
	 */
	protected Boolean loadFormFilter(IWebElementWrapper element) throws Exception
	{
		Boolean flag=false;
		List<String> list=element.getAllOptionTexts();
		String firstOpt=list.get(0);
		int times=0;
		while(times<=10){
			logger.info("loading form filter");
			if(element.getSelectedText().equalsIgnoreCase(firstOpt)){
				flag=true;
				break;
			}
			waitThat().timeout(4000);
			times++;
		}
		return flag;

	}

	/**
	 * select it and wait refreshElt refreshed at first index.
	 * @param element
	 * @param it
	 * @param refreshElt
	 * @return
	 * @throws Exception
	 */
	protected Boolean selectIt(IWebElementWrapper element,String it,IWebElementWrapper refreshElt) throws Exception
	{
		Boolean flag=false;
		if(it==null || it.trim().equals("")){return false;}
		if (!element.getSelectedText().equalsIgnoreCase(it))
		{
			try{
				element.selectByVisibleText(it.trim());
				loadingDlg(null,15);
				loadFormFilter(refreshElt);
			}catch(Exception e){}
			finally{
				if(element.getSelectedText().equalsIgnoreCase(it.trim()))
				{
					flag=true;
				}else
				{
					List<String> list=element.getAllOptionTexts();
					if(list.contains(it.trim()))
					{
						element.selectByVisibleText(it.trim());
						loadingDlg(null,5);//loadingDlg();
						flag=loadFormFilter(refreshElt);
					}else
					{
						for(String item:list)
						{
							if(it.trim().equalsIgnoreCase(item.trim()))
							{
								element.selectByVisibleText(item);
								loadingDlg(null,5);//loadingDlg();
								flag=loadFormFilter(refreshElt);
								break;
							}
						}
					}

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
}
