package com.lombardrisk.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;























import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.test.ITestDataManager;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.IWebDriverWrapper.IWebElementWrapper;

import com.lombardrisk.commons.FileUtil;
import com.lombardrisk.test.IComFolder;
import com.lombardrisk.test.IExecFuncFolder;
import com.lombardrisk.test.TestDataManager;
import com.lombardrisk.test.pojo.*;

public class FormInstancePage extends AbstractPage implements IComFolder,IExecFuncFolder,IExportTo
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Form form;
	private String loginUser;
	private DBInfo dBInfo;
	public FormInstancePage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager)
	{
		super(webDriverWrapper,testDataManager);
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}
	
	public FormInstancePage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form)
	{
		super(webDriverWrapper,testDataManager);
		this.form=form;
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}
	
	public FormInstancePage(IWebDriverWrapper webDriverWrapper,ITestDataManager testDataManager, Form form,String loginUser)
	{
		super(webDriverWrapper,testDataManager);
		this.form=form;
		this.setLoginUser(loginUser);
		this.setDBInfo(((TestDataManager)getTestDataManager()).getDBInfo());
	}
	
	
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	
	public DBInfo getDBInfo() {
		return dBInfo;
	}
	public void setDBInfo(DBInfo dBInfo) {
		this.dBInfo = dBInfo;
	}
	
	/**
	 * It will get all cells' vale in all form pages and instances, and saved in designated regulator (UIDisplay) folder,<br> file name format is <i>name_version_entity_simpleprocessdate(suffix)</i>.csv
	 * @author kun shen
	 * @throws Exception
	 */
	public String getAllCellsValue(String regulatorFolder) throws Exception
	{
		String regulator=form.getRegulator();
		String formName=form.getName();
		String processDateSimple=form.getProcessDate().replace("/", "").replace("-", "");
		String fileFullName=null;
		//List<String> fileFullNames=new ArrayList<String>();
		List<String> pageInstanceCodes= new ArrayList<String>(Arrays.asList("CSL","SGD","USD","1","2","3","4","5","6","7","8","9","10"));
		try{
			//String regulatorFolder=TARGET_DOWNLOAD_FOLDER+regulator+"("+UIDISPLAY+")/";
			if(!new File(regulatorFolder).exists())
			{
				FileUtil.createDirectory(regulatorFolder);
			}
			
			fileFullName=regulatorFolder+formName+"_"+form.getVersion()+"_"+form.getEntity()+"_"+processDateSimple+".csv";
			File fileToWrite=new File(fileFullName);
			if(fileToWrite.exists())
			{
				//TODO rename fileFullName
				fileFullName=regulatorFolder+FileUtil.addSuffixToFile(fileToWrite);
				fileToWrite=new File(fileFullName);
			}
			if(!fileToWrite.exists()){fileToWrite.createNewFile();}
			
			int pageCount = (int) element("fipf.pageTab").getRowCount();
			for (int pageIndex = 1; pageIndex <= pageCount; pageIndex++)
			{
				String pageName=element("fipf.pageName", String.valueOf(pageIndex)).getInnerText().trim();
				if(!this.selectPage(String.valueOf(pageIndex),true)){break;}
				logger.info("current page[" + pageName+"]");
				
				List<String> instanceLabels=this.getAllInstances();
				for(String instanceLabel:instanceLabels)
				{
					logger.info("current instance[" + instanceLabel+"]");
					this.selectInstance(instanceLabel);
					String instanceCode=instanceLabel;
					if(!pageInstanceCodes.contains(instanceLabel))//instanceLabel.equals("1")
					{
						instanceCode=getDBInfo().getInstance(dBInfo.getConnectedDB(),regulator, formName, form.getVersion().substring(1),pageName,instanceLabel,DBInfo.InstanceType.LABEL);
						if(instanceCode.equals("-1")){instanceCode=instanceLabel;}
					}
					
					//String fileFullName=regulatorFolder+formName+"_"+form.getVersion()+"_"+instanceCode+"_"+form.getEntity()+"_"+processDateSimple+".csv";
					
					long begin=System.currentTimeMillis();
					//get all normal cells
					StringBuffer strBuffer=getNormalCells(instanceCode);
		    		FileUtil.writeContent(fileToWrite,strBuffer.toString());
		    		logger.info("getNormalCells used time[seconds]:"+(System.currentTimeMillis()-begin)/1000.00F);
		    		strBuffer.setLength(0);//clear strBuffer
		    		begin=System.currentTimeMillis();
					//get all extend grid cells
		    		strBuffer=getExtendGridCells(instanceCode);
		    		FileUtil.writeContent(fileToWrite,strBuffer.toString());
		    		logger.info("getExtendGridCells used time[seconds]:"+(System.currentTimeMillis()-begin)/1000.00F);
		    		strBuffer.setLength(0);//clear strBuffer
		    		logger.info("save UIDisplay values into file " + fileFullName);
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			Runtime.getRuntime().gc();
		}
	    return fileFullName;
	}
	
/**
 * get all normal cells value and store them in StringBuffer
 * @author kun shen
 * @param instanceCode
 * @return
 * @throws Exception
 */
private StringBuffer getNormalCells(String instanceCode) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	String lineSeparator=System.getProperty("line.separator");
	//get all normal cells
	if(element("fipf.getCells").isPresent())	
	{
		String cellValue,checked;
		String blankStr="";
		String OneStr="1";
		String ZeroStr="0";
		String NullStr="null";
		String cellType=null;
		List<IWebElementWrapper> elements=element("fipf.getCells").getAllMatchedElements();
		for (IWebElementWrapper element:elements)
		{
			 cellValue=blankStr;
    		 cellType=element.getAttribute("type").trim();
    		 if(cellType.equalsIgnoreCase("textarea"))
    		 {
    			 cellValue=element.getInnerText();
    			 cellValue=cellValue.substring(0, cellValue.lastIndexOf("\n"));
    		 }else if(cellType.equalsIgnoreCase("text"))
    		 {
    			 cellValue=element.getAttribute("value").trim();
    			 cellValue=cellValue.equalsIgnoreCase(NullStr)?blankStr:cellValue;
    		 } else if(cellType.equalsIgnoreCase("checkbox"))
    		 {
    			 checked=element.getAttribute("checked");
    			 if(checked!=null && (checked.equalsIgnoreCase("true")||checked.equalsIgnoreCase("checked")))
    			 {
    				 cellValue=OneStr;
    			 }else
    			 {
    				 cellValue=ZeroStr;
    			 }
    		 }
    		 strBuffer.append(element.getAttribute("id")+","+","+instanceCode+",\""+cellValue+"\""+lineSeparator); 
		}
		
	}
	return strBuffer;
}

/**
 * get all normal cells value and store them in StringBuffer
 * @author kun shen
 * @param instanceCode
 * @return
 * @throws Exception
 */
private StringBuffer getNormalCells(String instanceCode,String cellName) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	String lineSeparator=System.getProperty("line.separator");
	//get all normal cells
	if(element("fipf.getOneCell",cellName,cellName).isPresent())	
	{
		List<IWebElementWrapper> elements=element("fipf.getOneCell",cellName,cellName).getAllMatchedElements();
		for (IWebElementWrapper element:elements)
		{
    		 String cellValue=null;
    		 String cellType=element.getAttribute("type").trim();
    		 if(cellType.equalsIgnoreCase("textarea"))
    		 {
    			 cellValue=element.getInnerText();
    			 cellValue=cellValue.substring(0, cellValue.lastIndexOf("\n"));
    		 }else if(cellType.equalsIgnoreCase("text"))
    		 {
    			 cellValue=element.getAttribute("value").trim();
    		 } else if(cellType.equalsIgnoreCase("checkbox"))
    		 {
    			 String checked=element.getAttribute("checked");
    			 if(checked!=null && (checked.equalsIgnoreCase("true")||checked.equalsIgnoreCase("checked")))
    			 {
    				 cellValue="1";
    			 }else
    			 {
    				 cellValue="0";
    			 }
    		 }
    		 cellValue=cellValue.equalsIgnoreCase("null")?"":cellValue;
    
    		 strBuffer.append(element.getAttribute("id")+","+","+instanceCode+",\""+cellValue+"\""+lineSeparator);
    		 
		}
	}
	return strBuffer;
}

/**
 * get all extendGridCells
 * @author kun shen
 * @param instanceCode
 * @return
 * @throws Exception
 */
private StringBuffer getExtendGridCells(String instanceCode) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	//get all extend grid cells
	if(element("fipf.getGridTbody").isPresent())
	{
		//get all grid extend tables
		List<IWebElementWrapper> gridTbodyElements=element("fipf.getGridTbody").getAllMatchedElements();
		int gridTbodyCount=gridTbodyElements.size();
		for(int i=0;i<gridTbodyCount;i++)
		{
			String tbodyId=gridTbodyElements.get(i).getAttribute("id");
			String gridPrefix=tbodyId.trim().replace("formInstContentForm:", "").replace("_data", "");
			//System.out.println("gridTable: "+gridPrefix);
			IWebElementWrapper gridBarFirstPointer=element("fipf.firstPageSta2",gridPrefix);
			if(gridBarFirstPointer.isPresent())
			{
				if(!gridBarFirstPointer.getAttribute("class").contains("ui-state-disabled"))
				{
					gridBarFirstPointer.click();
					loadingDlg();	
				}
				IWebElementWrapper nextPageBar=element("fipf.nextPageSta2",gridPrefix);
				while(nextPageBar.isDisplayed() && nextPageBar.getAttribute("tabindex").equals("0"))
				{
					strBuffer.append(getGridCells(instanceCode,tbodyId,gridPrefix).toString());
					
					nextPageBar.click();
					loadingDlg();
					nextPageBar=element("fipf.nextPageSta2",gridPrefix);
				}
			}
			
			strBuffer.append(getGridCells(instanceCode,tbodyId,gridPrefix).toString());
			
		}
	}
	return strBuffer;
}

/**
 * get current grid table's all grid cells, not used in other class.
 * @author kun shen
 * @param instanceCode
 * @param tbodyId
 * @param gridPrefix
 * @return
 * @throws Exception
 */
private StringBuffer getGridCells(String instanceCode,String tbodyId,String gridPrefix) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	String lineSeparator=System.getProperty("line.separator");
	//next used in for(int row=0;row<gridRowCount;row++)
	//String data_ri;@deprecated, replace by rowId
	String data_rk;
	//next used in for(IWebElementWrapper gridCellElement:gridCellElements)
	String id,cellName,cellValue,checked;
	String blankStr="";
	String OneStr="1";
	String ZeroStr="0";
	String NullStr="null";
	String cellType=null;
	String rowId=null;
	//print this grid tables's rows
	//get ./tr
    List<IWebElementWrapper> gridRowElements=element("fipf.getGridTr",tbodyId).getAllMatchedElements();
    int gridRowCount=gridRowElements.size();
    for(int row=0;row<gridRowCount;row++)
    {
    	//data_ri=gridRowElements.get(row).getAttribute("data-ri");
    	data_rk=gridRowElements.get(row).getAttribute("data-rk");
    	//get ./td/input
    	List<IWebElementWrapper> gridCellElements=element("fipf.getGridCells",tbodyId,String.valueOf(row+1),tbodyId,String.valueOf(row+1)).getAllMatchedElements();
    	for(IWebElementWrapper gridCellElement:gridCellElements)
    	 {
    		 id=gridCellElement.getAttribute("id");
    		 cellName=id.replace(gridPrefix+data_rk, "");
    		 cellValue=blankStr;
    		 cellType=gridCellElement.getAttribute("type").trim();
    		 if(cellType.equalsIgnoreCase("textarea"))
    		 {
    			 cellValue=gridCellElement.getInnerText();
    			 cellValue=cellValue.substring(0, cellValue.lastIndexOf("\n"));
    		 }else if(cellType.equalsIgnoreCase("text"))
    		 {
    			 cellValue=gridCellElement.getAttribute("value").trim();
    			 cellValue=cellValue.equalsIgnoreCase(NullStr)?blankStr:cellValue;
    		 }else if(cellType.equalsIgnoreCase("checkbox"))
    		 {
    			 checked=gridCellElement.getAttribute("checked").trim();
    			 if(checked!=null && (checked.equalsIgnoreCase("true")||checked.equalsIgnoreCase("checked")))
    			 {
    				 cellValue=OneStr;
    			 }else
    			 {
    				 cellValue=ZeroStr;
    			 }
    		 }
    		 rowId=element("fipf.getGridCells1st",tbodyId,String.valueOf(row+1)).getAttribute("value").trim();
    		 strBuffer.append(cellName+","+rowId+","+instanceCode+",\""+cellValue+"\""+lineSeparator);
    		 //strBuffer.append(cellName+","+(Integer.parseInt(data_ri)+1)+","+instanceCode+",\""+cellValue+"\""+lineSeparator);
    	 }
    	
    	
    }
    return strBuffer;
}

/**
 * get all extendGridCells by cellName
 * @author kun shen
 * @param instanceCode
 * @return
 * @throws Exception
 */
private StringBuffer getExtendGridCells(String instanceCode,String cellName) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	//get all extend grid cells
	if(element("fipf.getGridTbody").isPresent())
	{
		//get all grid extend tables
		List<IWebElementWrapper> gridTbodyElements=element("fipf.getGridTbody").getAllMatchedElements();
		int gridTbodyCount=gridTbodyElements.size();
		for(int i=0;i<gridTbodyCount;i++)
		{
			String tbodyId=gridTbodyElements.get(i).getAttribute("id");
			String gridPrefix=tbodyId.trim().replace("formInstContentForm:", "").replace("_data", "");
			//System.out.println("gridTable: "+gridPrefix);
			IWebElementWrapper gridBarFirstPointer=element("fipf.firstPageSta2",gridPrefix);
			if(gridBarFirstPointer.isPresent())
			{
				if(!gridBarFirstPointer.getAttribute("class").contains("ui-state-disabled"))
				{
					gridBarFirstPointer.click();
					loadingDlg();	
				}
				IWebElementWrapper nextPageBar=element("fipf.nextPageSta2",gridPrefix);
				while(nextPageBar.isDisplayed() && nextPageBar.getAttribute("tabindex").equals("0"))
				{
					strBuffer.append(getGridCells(instanceCode,tbodyId,gridPrefix,cellName).toString());
					
					nextPageBar.click();
					loadingDlg();
					nextPageBar=element("fipf.nextPageSta2",gridPrefix);
				}
			}
			
			strBuffer.append(getGridCells(instanceCode,tbodyId,gridPrefix,cellName).toString());
			
		}
	}
	return strBuffer;
}


/**
 * get current grid table's all grid cells by cellName, not used in other class.
 * @author kun shen
 * @param instanceCode
 * @param tbodyId
 * @param gridPrefix
 * @return
 * @throws Exception
 */
private StringBuffer getGridCells(String instanceCode,String tbodyId,String gridPrefix,String cellName) throws Exception
{
	StringBuffer strBuffer=new StringBuffer();
	String lineSeparator=System.getProperty("line.separator");
	String rowId=null;
	//print this grid tables's rows
	//get ./tr
    List<IWebElementWrapper> gridRowElements=element("fipf.getGridTr",tbodyId).getAllMatchedElements();
    int gridRowCount=gridRowElements.size();
    for(int row=0;row<gridRowCount;row++)
    {
    	//String data_ri=gridRowElements.get(row).getAttribute("data-ri");//deprecated, replace by rowId
    	String data_rk=gridRowElements.get(row).getAttribute("data-rk");
    	//get ./td/input
    	List<IWebElementWrapper> gridCellElements=element("fipf.getOneInGridCells",tbodyId,gridPrefix+data_rk+cellName,tbodyId,gridPrefix+data_rk+cellName).getAllMatchedElements();
    	for(IWebElementWrapper gridCellElement:gridCellElements)
    	 {
    		 String cellValue=null;
    		 String cellType=gridCellElement.getAttribute("type").trim();
    		 if(cellType.equalsIgnoreCase("textarea"))
    		 {
    			 cellValue=gridCellElement.getInnerText();
    			 cellValue=cellValue.substring(0, cellValue.lastIndexOf("\n"));
    		 }else if(cellType.equalsIgnoreCase("text"))
    		 {
    			 cellValue=gridCellElement.getAttribute("value").trim();
    		 }else if(cellType.equalsIgnoreCase("checkbox"))
    		 {
    			 String checked=gridCellElement.getAttribute("checked").trim();
    			 if(checked!=null && (checked.equalsIgnoreCase("true")||checked.equalsIgnoreCase("checked")))
    			 {
    				 cellValue="1";
    			 }else
    			 {
    				 cellValue="0";
    			 }
    		 }
    		 cellValue=cellValue.equalsIgnoreCase("null")?"":cellValue;
    		 //String cellreadonly=gridCellElement.getAttribute("readonly")==null?"false":"true" ;
    		 rowId=element("fipf.getGridCells1st",tbodyId,String.valueOf(row+1)).getAttribute("value").trim();
    		 strBuffer.append(cellName+","+rowId+","+instanceCode+",\""+cellValue+"\""+lineSeparator);
    		 //strBuffer.append(cellName+","+(Integer.parseInt(data_ri)+1)+","+instanceCode+",\""+cellValue+"\""+lineSeparator);

    	 }
    }
    return strBuffer;
}



	public List<String> getAllInstances() throws Exception
	{
		List<String> instances = new ArrayList<String>();
		int amt = (int) element("fipf.instaceTab").getRowCount();
		for (int i = 1; i <= amt; i++)
		{
			instances.add(element("fipf.instace", String.valueOf(i)).getAttribute("data-label"));
		}
		return instances;
	}
	
	/**
	 * select instance label in UI
	 * 
	 * @param instanceLabel
	 * @throws Exception
	 */
	public void selectInstance(String instanceLabel) throws Exception
	{
		if (instanceLabel != null)
		{
			if (!getCurrentPageInstance().equalsIgnoreCase(instanceLabel))
			{
				logger.info("select instance " + instanceLabel);
				element("fipf.curInst").click();
				loadingDlg();
				element("fipf.selectInstace", instanceLabel).click();
				loadingDlg();
			}

		}

	}
	
	/**
	 * get current instance
	 * 
	 * @return instance
	 * @throws Exception
	 */
	public String getCurrentPageInstance() throws Exception
	{
		return element("fipf.curInst").getInnerText();

	}
	
	/**
	 * select page in UI by page index<br>
	 * @author kun shen
	 * @param pageIndex begin from 1...
	 * @param skippedAjaxError true for skipped ajax error, false for logged that
	 * @return true for selected, false for not selected.
	 * @throws Exception
	 */
	public Boolean selectPage(String pageIndex,Boolean skippedAjaxError) throws Exception
	{
		Boolean selected=false;
		do
		{
			loadingDlg();
			logger.info("Trying to click page index[" + pageIndex+"]");
			element("fipf.pageName", pageIndex).click();
			loadingDlg();
			
			//if fp.ajaxErrorBtn selected=false
			if(element("fipf.ajaxErrorBtn").isPresent())
			{
				if(skippedAjaxError)
				{
					logger.warn("occurr ajax error, and skipped it.");
					element("fipf.ajaxErrorBtn").click();
					break;
				}else
				{
					logger.error("occurr ajax error.");
					//assertThat("fp.ajaxErrorBtn");
					break;
				}
			}else
			{
				if(element("fipf.pageLocalRow", pageIndex).getAttribute("class").contains("ui-state-highlight"))
				{logger.info("Click page index[" + pageIndex+"]");selected=true;break;}
			}
		}while(!element("fipf.pageLocalRow", pageIndex).getAttribute("class").contains("ui-state-highlight"));
		return selected;
	}
	
	/**
	 * if this page is this page, return true, others return false.
	 * @return
	 * @throws Exception 
	 */
	public Boolean isThisPage() throws Exception
	{
		Boolean flag=false;
		/*StringBuffer strBuffer=new StringBuffer("");
		if(element("fipf.formInstTitleLabels").isPresent())
		{
			List<IWebElementWrapper> elements=element("fipf.formInstTitleLabels").getAllMatchedElements();
			for(IWebElementWrapper element:elements)
			{
				strBuffer.append(element.getInnerText());
			}
			String title=strBuffer.toString().trim().toLowerCase();
			if(!title.equals("") && title.contains(form.getName().toLowerCase()) && title.contains(form.getVersion().toLowerCase()) && title.contains(form.getEntity().toLowerCase()) && title.contains(form.getProcessDate()))
			{
				flag=true;
			}
		}*/
		
		flag=element("fipf.formInstTitle",form.getName()+" "+form.getVersion(),form.getRegulator()+" / "+form.getEntity(),form.getProcessDate()).isPresent();
		if(flag)
		{
			
			if(!element("fipf.pageTab").isPresent())
			{
				logger.info("can't open form instance");
				super.getWebDriverWrapper().navigate().backward();
				return false;
			}
			loadingDlg(element("fipf.pageTab"),10);
			
			if(element("abstract.ajaxstatusDlg").isDisplayed())
			{
				logger.info("still loading");
				super.getWebDriverWrapper().navigate().backward();
				return false;
			}
			loadingDlg();
			waitForPageLoaded();
			if (element("fipf.warnConfirmBtn").isDisplayed())
			{
				element("fipf.warnConfirmBtn").click();
				loadingDlg(element("fipf.form"),100);
			}
			waitForPageLoaded();
		}
		
		
		return flag;
	}
	
	public void closeThisPage() throws Exception
	{
		if (element("fipf.form").isDisplayed())
		{
			if (element("abstract.message").isDisplayed())
				waitThat("abstract.message").toBeInvisible();
			if (element("fipf.importDlgmodal").isDisplayed())
				waitThat("fipf.importDlgmodal").toBeInvisible();

			if (element("fipf.close").isDisplayed())
			{
				logger.info("Close form");
				element("fipf.close").click();
				loadingDlg();
				waitThat("fipf.close").toBeInvisible();
				loadingDlg();
			}
			if (element("fipf.close").isDisplayed())
			{
				logger.info("can't close form, navigate to backward.");
				super.getWebDriverWrapper().navigate().backward();
				loadingDlg();
			}
		}
	}

	/**
	 * select page by page name<br>
	 * @author kun shen
	 * @param pageName
	 * @return
	 * @throws Exception
	 */
	protected String selectPage(String pageName) throws Exception
	{
		String ReturnPageName=null;
		if(pageName!=null)
		{
			int pageCount = (int) element("fipf.pageTab").getRowCount();
			for (int pageIndex = 1; pageIndex <= pageCount; pageIndex++)
			{
				String pageNameInUI=element("fipf.pageName", String.valueOf(pageIndex)).getInnerText();
				if(pageName.trim().equalsIgnoreCase(pageNameInUI.trim()))
				{
					if(selectPage(String.valueOf(pageIndex),true)){ReturnPageName=pageName;}
					break;
				}
			}
		}
		return ReturnPageName;

	}
	/**
	 * select instance by instance code<br>
	 * @author kun shen
	 * @param regulator
	 * @param formName
	 * @param formVersion
	 * @param pageName
	 * @param instanceCode
	 * @return
	 * @throws Exception
	 */
	protected String selectInstance(String regulator,String formName, String formVersion,String pageName,String instanceCode) throws Exception
	{
		String instanceLabel=null;
		if (regulator!=null && formName!=null && formVersion!=null && pageName != null)
		{
			if(instanceCode == null || instanceCode.equals("")){instanceCode="1";}
			instanceLabel=getDBInfo().getInstance(dBInfo.getConnectedDB(),regulator, formName, formVersion,pageName,instanceCode,DBInfo.InstanceType.CODE);
			if(instanceLabel==null)
			{
				logger.info("error: not find instance label in DB with code["+instanceCode+"]");
			}else if(instanceLabel.equals("-1"))
			{
				instanceLabel=instanceCode;
			}
			
			this.selectInstance(instanceLabel);
		}
		return instanceLabel;
	}
	/**
	 * get cell display value in UI<br> return cell display value if success, return "fail:..."if fail, return null if not find.<br>
	 * @author kun shen
	 * @param regulator
	 * @param formName
	 * @param formVersion
	 * @param cellName
	 * @param rowId
	 * @param instanceCode
	 * @return
	 * @throws Exception
	 */
	public String getCellValueInUI(String regulator,String formName, String formVersion,String cellName, String rowId,String instanceCode)throws Exception
	{
		String lineSeparator=System.getProperty("line.separator");
		String result=null;
		if(rowId!=null && rowId.trim().equals("")){rowId=null;}
		List<String> pageNames=getDBInfo().getPageName(dBInfo.getConnectedDB(),regulator, formName, formVersion, cellName, rowId);
		for(String pageName: pageNames)
		{
			String pageNameT=selectPage(pageName);
			if(pageNameT!=null)
			{
				logger.info("current page[" + pageNameT+"]");
				String instanceLabel=selectInstance(regulator,formName, formVersion, pageNameT,instanceCode);
				if(instanceLabel==null){break;}
				if(rowId==null || rowId.trim().equals("")){rowId="";}
				String tmp=cellName+","+rowId+","+instanceCode+",";
				String strCells=null;
				if(rowId.equals(""))
				{
					strCells=getNormalCells(instanceCode,cellName).toString();
				}else
				{
					strCells=getExtendGridCells(instanceCode,cellName).toString();
				}
				Pattern p = Pattern.compile("^"+tmp+"\""+"(.*)"+"\"$", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
				Matcher m = p.matcher(strCells);
				if(m.find())
				{
					result=m.group(1);
				}
							
			}
			if(result!=null){break;}
		}
		
		return result;
	}
	/**
	 * return true is locked, return false is unlocked.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean getFormStatus() throws Exception
	{
		Boolean flag=false;
		if(element("fipf.unlockBtn").isPresent())
		{
			flag=true;
		}
		return flag;
	}
	/**
	 * lock form if the form is unlocked.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean lockForm() throws Exception
	{
		return changeFormStatus(element("fipf.lockBtn"),element("fipf.unlockBtn"));
	}
	
	/**
	 * unlock form is the form is locked.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean unlockForm() throws Exception
	{
		Boolean flag=changeFormStatus(element("fipf.unlockBtn"),element("fipf.lockBtn"));
		int times=2;
		while(!flag && times>0)
		{
			super.refreshPage();
			flag=changeFormStatus(element("fipf.unlockBtn"),element("fipf.lockBtn"));
			times--;
		}
		return flag;
	}
	
	/**
	 * change form status from lock to unlock or from unlock to lock.
	 * @author kun shen
	 * @param element1
	 * @param element2
	 * @return
	 * @throws Exception
	 */
	private Boolean changeFormStatus(IWebElementWrapper element1,IWebElementWrapper element2) throws Exception
	{
		Boolean flag=false;
		if(element1.isPresent())
		{
			element1.click();
			waitThat().timeout(1000);
			loadingDlg();
			//TODO
			List<IWebElementWrapper> elements=element("uolwd.ok").getAllMatchedElements();
			for(IWebElementWrapper element:elements)
			{
				if(element.isDisplayed())
				{
					/*String id=element.getAttribute("id");
					id=id.substring(id.lastIndexOf(":")).replace("Confirm", "");//result:":unlock" or ":lock"
*/					element.click();
					logger.info(element("uolwd.Comment").getInnerText());
					break;
				}
			}
			loadingDlg();
			getTipMessageStatus();
		}
		if(element2.isPresent())
		{
			flag=true;
		}
		return flag;
	}
	/**
	 * import file in opening form instance. Return ImportFileFormDialog if successfully, others return null
	 * @author kun shen
	 * @param form
	 * @return
	 * @throws Exception 
	 */
	public ImportFileFormDialog importFileForm() throws Exception
	{
		ImportFileFormDialog importFileDlg=null;
		String importFile=form.getImportFile();
		if(element("fipf.lockBtn").isPresent() && importFile!=null && importFile.indexOf(".")>0)
		{
			String importFileFullName=TARGET_IMPORT_FOLDER+form.getRegulator()+"/"+form.getImportFile();
			if(new File(importFileFullName).exists())
			{
				element("fipf.adjust_button").click();
				waitThat("fipf.importAdjustLog").toBeVisible();
				element("fipf.importAdjustLog").click();
				loadingDlg();
				importFileDlg=new ImportFileFormDialog(getWebDriverWrapper(),getTestDataManager(),form);
				if(!importFileDlg.isThisPage())
				{
					importFileDlg=null;
				}
			}else
			{
				logger.info("no exist import file");
			}
			
		}
		return importFileDlg;
	}
	
	
	/**
	 * click export to file button, and click "Export To PDF" to download file.<br> If successfully return full file path, others return null.<br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.0<br>
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportToPDF() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("Export To PDF");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOPDF+")/");
		unlockDownloadDir(downloadFolder);
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	
	/**
	 * click export to file button, and click "Export To Excel(Apply Scale)" to download file.<br> If successfully return full file path, others return null.<br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.1<br>
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportToExcelApplyScale() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("Export To Excel (Apply Scale)");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOEXCELAPPLYSCALE+")/");
		unlockDownloadDir(downloadFolder);
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	
	/**
	 * click export to file button, and click "Export To Excel(No Scale)" to download file.<br> If successfully return full file path, others return null.<br>
	 * it suits for AgileREPORTER version greater than or equal 1.15.1<br>
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportToExcelNoScale() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("Export To Excel (No Scale)");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOEXCELNOSCALE+")/");
		unlockDownloadDir(downloadFolder);
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	/**
	 * click export to file button, and click "Export To Excel" to download file.<br> If successfully return full file path, others return null.<br>
	 * it suits for AgileREPORTER version less than or equal 1.15.0<br>
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportToExcel() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("Export To Excel");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOEXCEL+")/");
		unlockDownloadDir(downloadFolder);
		Runtime.getRuntime().gc();
		return destFileFullPath;
	}
	/**
	 * click export to file button, and click "Export To CSV" to download file.<br> If successfully return full file path, others return null.
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public String exportToCSV() throws Exception
	{
		Boolean flag=false;
		if(lockDownloadDir(downloadFolder))
		{
			flag=clickExportToFile("Export To CSV");
		}
		if(!flag)
		{
			unlockDownloadDir(downloadFolder);
			return null;
		}
		String sourceFileFullPath=exportToFile();
		String destFileFullPath=moveDownloadFileToExpectedFolder(sourceFileFullPath,TARGET_DOWNLOAD_FOLDER+form.getRegulator()+"("+EXPORTTOCSV+")/");
		unlockDownloadDir(downloadFolder);
		Runtime.getRuntime().gc();
		return destFileFullPath;
	
	}
	/**
	 * click export to file button, and click "Export To Excel" or "Export To CSV" or ... to download file.<br>
	 * return true if click export button without error. others return false. 
	 * @author kun shen
	 * @param type
	 * @throws Exception
	 */
	private Boolean clickExportToFile(String type) throws Exception
	{
		Boolean flag=true;
		
		element("fipf.exportToFile_button").click();
		//fipf.exportToFile_menuList
		List<String> menuTxtList=element("fipf.exportToFile_menuList").getAllInnerTexts();
		for(String txt : menuTxtList)
		{
			if(txt.equalsIgnoreCase(type)){type=txt;break;}
		}
		waitThat("fipf.exportToFile_menu",type).toBeVisible();
		if (PropHelper.ENABLE_FILE_DOWNLOAD)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("fipf.exportToFile_menu",type).click();
			loadingDlg();
			loadingDlg();
			flag=getTipMessageStatus();
			TestCaseManager.getTestCase().stopTransaction();
			
		}
		else
		{
			element("fipf.exportToFile_menu",type).click();
			loadingDlg();
			loadingDlg();
			flag=getTipMessageStatus();
		}
		
		return flag;
	}
	
		
	/**
	 * export to regulator in form instance page
	 * @return
	 * @throws Exception
	 */
	public ExportToRegulatorDialog exportToRegulator(Form form) throws Exception
	{
		ExportToRegulatorDialog td=null;
		Boolean flag=true;
		
		element("fipf.exportToFile4Fed_button").click();
		loadingDlg();
		String title=null;
		String fileType=form.getTransmission().getFileType();

		String liTxt=null;
		flag=false;//reset flag for finding matched Export to something
		int count=element("fipf.exportToFile_li").getNumberOfMatches();
		int i=1;
		for(;i<=count;i++)
		{
			liTxt=element("fipf.exportToFile_li_Txt",String.valueOf(i)).getInnerText();
			if(liTxt==null|| liTxt.trim().equals("")){continue;}
			if(fileType==null || fileType.trim().equals(""))
			{
				if(liTxt.trim().toUpperCase().endsWith(form.getName().toUpperCase()))
				{
					flag=true;
					break;
				}else if(liTxt.trim().toUpperCase().endsWith(form.getName().toUpperCase()+"_"+form.getVersion().toUpperCase()))
				{
					flag=true;
					break;
				}
			}else 
			{
				if(liTxt.trim().toUpperCase().endsWith(fileType.trim().toUpperCase()))
				{
					flag=true;
					break;
				}
			}
			
		}
		if(flag && liTxt!=null) //found matched Export to something
		{
			String js = "document.getElementById('formHeader:exportToFile4Fed_menu').getElementsByTagName('ul')[0].getElementsByTagName('li')["+String.valueOf(i-1)+"].getElementsByTagName('a')[0].getElementsByTagName('span')[0].click();";
			executeScript(js);
			if(liTxt.toUpperCase().contains("XBRL")){waitThat().timeout(2000);}
			loadingDlg();
			/*IWebElementWrapper element=element("td.transmitDialog4FedTitle");
			if(element.isDisplayed())
			{
				title=element.getInnerText();
				td=new ExportToRegulatorDialog(getWebDriverWrapper(),form,title);
			}else
			{
				element=element("td.transmitDialogTitle");
				if(element.isDisplayed())
				{
					title=element.getInnerText();
					td=new ExportToRegulatorDialog(getWebDriverWrapper(),form,title);
				}
			}*/
			
			//reuse this part of select displayed transmit dialog from 20170113
			List<IWebElementWrapper> elements=element("td.transmitDialogTitles").getAllMatchedElements();
			for(IWebElementWrapper element:elements)
			{
				if(element.isDisplayed())
				{
					title=element.getInnerText();
					td=new ExportToRegulatorDialog(getWebDriverWrapper(),getTestDataManager(),form,title);
					break;
				}
			}
			
		}else{
			element("fipf.exportToFile4Fed_button").click();
			loadingDlg();
		}
		
		
		return td;
	
	}
	
	/**
	 * click Adjustments->View adjustment log, if error occurs return null.
	 * @author kun shen
	 * @return FormInstanceBottomPage
	 * @throws Exception
	 */
	public FormInstanceBottomPage viewAdjustmentLog() throws Exception
	{
		FormInstanceBottomPage fibp=null;
		element("fipf.adjust_button").click();
		waitThat("fipf.checkAdjustLog").toBeVisible();
		element("fipf.checkAdjustLog").click();
		loadingDlg(3000);
		//loadingDlg(element("fidf.bottomPage"));
		if(element("fidf.bottomPage").isDisplayed())
		{
			fibp=new FormInstanceBottomPage(getWebDriverWrapper(),getTestDataManager(),form);
		}
		
		return fibp;
	}
	
	/**
	 * check status of "Live Validation". Return true if it is live, return false if it isn't live.
	 * @author kun shen
	 * @return
	 * @throws Exception 
	 */
	public Boolean validationLiveStatus() throws Exception
	{
		Boolean flag=false;
		if(element("fipf.doValidationBtn").isPresent())
		{
			flag=true;
		}
		return flag;
	}
	
	/**
	 * click "Live Validation". Return true if it is live, return false if it isn't live.
	 * @author kun shen
	 * @throws Exception
	 */
	public Boolean clickValidationLive() throws Exception
	{
		Boolean flag=false;
		if(element("fipf.doValidationBtn").isPresent())
		{
			element("fipf.doValidationBtn").click();//not live
			loadingDlg();
		}else
		{
			if(element("fipf.unDoValidationBtn").isPresent())
			{
				element("fipf.unDoValidationBtn").click();//live
				loadingDlg();
			}
		}
		if(element("fipf.doValidationBtn").isPresent())
		{
			flag=true;
		}
		return flag;
	}
	
	/**
	 * click "Live Validation" if it isn't live. return true if it is live, others return false.
	 * @author kun shen
	 * @return
	 * @throws Exception 
	 */
	public Boolean validationLive() throws Exception
	{
		Boolean flag=false;
		if(element("fipf.unDoValidationBtn").isPresent())
		{
			element("fipf.unDoValidationBtn").click();
			loadingDlg();
		}
		if(element("fipf.doValidationBtn").isPresent())
		{
			flag=true;
		}
		return flag;
	}
	
	/**
	 * click "Validate Now" button in menu, if this button is enabled,click it and return true. others return false.
	 * @author kun shen
	 * @throws Exception
	 */
	public int validationNow() throws Exception
	{
		int failNum=0;
		if(element("fipf.validateNowBtn").isEnabled())
		{
			element("fipf.validateNowBtn").click();
			//loadingDlg();
			loadingDlg(element("fipf.validateNowBtn"),30);
			String failCount=element("fipf.validateFails").getInnerText();
			
			if(failCount!=null)
			{
				failCount=failCount.trim();
				if(!failCount.trim().equals("")&& !failCount.trim().equals("0") && failCount.trim().matches("^\\d+$"))
				{
					failNum=Integer.parseInt(failCount.trim());
				}
				
			}
			
		}
		return failNum;
	}
	
	/**
	 * invoke clickSomeWorkflow and then click "ready for approval"
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean clickReadyForApproval() throws Exception
	{
		/*Boolean flag=getFormStatus();
		if(!flag)
		{
			if(lockForm())
			{
				flag=clickSomeWorkflow("Ready for approval");
			}
		}*/
		return clickSomeWorkflow("Ready for approval");
	}
	
	/**
	 * invoke clickSomeWorkflow and then click "Approve"
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean clickApproval() throws Exception
	{
		String createdUser=getUserWhoCreatedIt();
		HomePage hp=null;
		ListPage lp=null;
		FormInstancePage fip=null;
		
		Boolean returnFlag=false;
		if(createdUser!=null)
		{
			clickSomeWorkflow("Approve");
			//confirm dialog
			IWebElementWrapper approveComment=element("wkacf.Comment");
			
			if(approveComment.isDisplayed())
			{
				approveComment.type("approved by automation");
				loadingDlg();
				element("wkacf.ok").click();
				loadingDlg();
				getTipMessageStatus();
			}

			if(checkWorkflowInDB().equalsIgnoreCase("ATTESTED"))
			{
				//returnFlag=true;
				if(!getLoginUser().equalsIgnoreCase(dBInfo.getApplicationServer_UserName()))
				{
					this.closeThisPage();
					lp=new ListPage(getWebDriverWrapper(),getTestDataManager());
					if(lp.isThisPage())
					{
						hp=lp.logout();
						if(hp.isThisPage())
						{
							lp=hp.login(dBInfo.getApplicationServer_UserName(), dBInfo.getApplicationServer_UserPassword());
							if(lp==null)
							{
								logger.error("error: cannot login home page.");
							}else{
								if(lp.isThisPage())
								{
									if(lp.selectFormInfo(form))
									{
										fip=lp.openFormInstance(form);
										this.setLoginUser(fip.getLoginUser());
										if(isThisPage())
										{
											returnFlag=true;
										}
									}
								}
							}
							
						}else
						{
							logger.error("error: cannot access home page.");
						}
						
					}else
					{
						logger.error("error: cannot access dashboard page.");
					}
				}else
				{
					returnFlag=true;
				}
			}else
			{
				if(getLoginUser().equalsIgnoreCase(createdUser))
				{
					this.closeThisPage();
					lp=new ListPage(getWebDriverWrapper(),getTestDataManager());
					if(lp.isThisPage())
					{
						hp=lp.logout();
						if(hp.isThisPage())
						{
							lp=hp.login(PropHelper.getProperty("test.approver.user"), PropHelper.getProperty("test.approver.password"));
							if(lp==null)
							{
								logger.error("error: cannot login home page.");
							}else
							{
								if(lp.isThisPage())
								{
									if(lp.selectFormInfo(form))
									{
										fip=lp.openFormInstance(form);
										this.setLoginUser(fip.getLoginUser());
										if(isThisPage())
										{
											returnFlag=clickApproval();//invoke itself
										}
										
									}
								}
							}
							
						}else
						{
							logger.info("cannot access home page.");
						}
						
					}else
					{
						logger.info("cannot access dashboard page.");
					}
					
				}
			}
		}
		
		return returnFlag;
	}
	
	/**
	 * invoke clickSomeWorkflow and click "Reject"
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	public Boolean clickReject() throws Exception
	{
		//TODO add more complex
		return clickSomeWorkflow("Reject");
	}
	
	
	/**
	 * invoke clickSomeWorkflow and click "View workflow log", get the user's name who create it. return null if no user log
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	protected String getUserWhoCreatedIt() throws Exception
	{
		String processDate=uniformDate(form.getProcessDate(),"MM/DD/YYYY");
		String userName=getDBInfo().getFormInstanceCreatedBy(form.getRegulator(),form.getName(),form.getVersion().substring(1),processDate);
		return userName;
	}
	
	
	/**
	 * click some workflow for attestation, return false if message occurs.
	 * @author kun shen
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private Boolean clickSomeWorkflow(String type) throws Exception
	{
		Boolean flag=true;
		logger.info("click \""+type+"\"");
		element("fipf.workflow_button").click();
		waitThat("fipf.workflow_menu").toBeVisible();
		List<String> menuTxtList=element("fipf.workflow_menu").getAllInnerTexts();
		for(String txt : menuTxtList)
		{
			if(txt.equalsIgnoreCase(type)){type=txt;break;}
		}
		if(element("fipf.workflow_menuList",type).isPresent())
		{
			element("fipf.workflow_menuList",type).click();
			loadingDlg();
			List<IWebElementWrapper> elements=element("fipf.workflow_CommentDialogComments").getAllMatchedElements();
			for(IWebElementWrapper element:elements)
			{
				if(element.isDisplayed())
				{
					element.type("click \""+type+"\" by automation");
					loadingDlg();
					break;
				}
			}
			elements=element("fipf.workflow_CommentDialogOKs").getAllMatchedElements();
			for(IWebElementWrapper element:elements)
			{
				if(element.isDisplayed())
				{
					element.click();
					loadingDlg();
					break;
				}
			}
			loadingDlg();
			flag=getTipMessageStatus();
		}else
		{
			logger.info("fipf.workflow_menuList doesn't contains \""+type+"\"");
			loadingDlg();
			element("fipf.workflow_button").click();
			waitThat("fipf.workflow_menu").toBeInvisible();
		}
		return flag;
	}
	
	/**
	 * check some workflow in UI for attestation, return true if the type exists.
	 * @author kun shen
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private Boolean checkSomeWorkflow(String type) throws Exception
	{
		Boolean flag=false;
		element("fipf.workflow_button").click();
		waitThat("fipf.workflow_menu").toBeVisible();
		if(element("fipf.workflow_menuList",type).isPresent())
		{
			flag=true;
		}
		element("fipf.workflow_button").click();
		return flag;
	}
	
	/**
	 * check some workflow in UI for attestation, return status.(READY_FOR_ATTESTATION,ATTESTED)
	 * @author kun shen
	 * @return
	 * @throws Exception
	 */
	private String checkWorkflowInDB() throws Exception
	{
		String processDate=uniformDate(form.getProcessDate(),"MM/DD/YYYY");
		String attestedStatus=getDBInfo().getFormInstanceAttestedStatus(form.getRegulator(),form.getName(),form.getVersion().substring(1),processDate);
		return attestedStatus;
	}

	
}
