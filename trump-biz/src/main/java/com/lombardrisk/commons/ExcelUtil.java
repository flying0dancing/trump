package com.lombardrisk.commons;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lombardrisk.test.pojo.Form;
import com.lombardrisk.test.pojo.Transmission;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Create by Leo Tu on Jun 19, 2015
 */
public class ExcelUtil
{
	private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
	
	private ExcelUtil(){}
	public static int getColumnNums(File file, String sheetName) throws Exception
	{
		int nums = 0;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook xwb = WorkbookFactory.create(inp);
			Sheet sheet = null;
			if (sheetName == null)
				sheet = xwb.getSheetAt(0);
			else
				sheet = xwb.getSheet(sheetName);
			nums = sheet.getRow(0).getPhysicalNumberOfCells();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return nums;
	}

	public static int getRowAmts(File file, String sheetName) throws Exception
	{
		// logger.info("File is:"+file);
		int amt = 0;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workBook = WorkbookFactory.create(inp);
			Sheet sheet = null;
			if (sheetName != null)
			{
				sheet = workBook.getSheet(sheetName);
			}
			else
			{
				sheet = workBook.getSheetAt(0);
			}
			amt = sheet.getLastRowNum();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}
		// logger.info("There are " + amt + " records");
		return amt;
	}

	public static int getRowNums(File file, String sheetName) throws Exception
	{
		// logger.info("File is:"+file);
		int amt = 0;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workBook = WorkbookFactory.create(inp);
			Sheet sheet = null;
			if (sheetName != null)
			{
				sheet = workBook.getSheet(sheetName);
			}
			else
			{
				sheet = workBook.getSheetAt(0);
			}

			for (int i = 0; i < sheet.getLastRowNum(); i++)
			{
				Row row = sheet.getRow(i);
				try
				{
					row.getCell(0).setCellType(1);
				}
				catch (Exception e)
				{
				}
				if (row.getCell(0) == null)
				{
					break;
				}
				else if (row.getCell(0).getStringCellValue().equals(""))
				{
					break;
				}
				else
				{
					amt++;
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}
		// logger.info("There are " + amt + " records");
		return amt;
	}

	public static int getRowNums(File file, String sheetName, int columnID) throws Exception
	{
		// logger.info("File is:"+file+", sheet is:"+sheetName+" , column is:"+columnID);
		int amt = 0;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook xwb = WorkbookFactory.create(inp);
			Sheet sheet = null;
			if (sheetName == null)
				sheet = xwb.getSheetAt(0);
			else
				sheet = xwb.getSheet(sheetName);

			for (int i = 0; i < sheet.getLastRowNum(); i++)
			{
				try
				{
					Row row = sheet.getRow(i);
					try
					{
						row.getCell(0).setCellType(1);
					}
					catch (Exception e)
					{
					}
					if (row.getCell(columnID - 1).getStringCellValue().equals(""))
					{
						break;
					}
					else
					{
						amt++;
					}
				}
				catch (Exception e)
				{
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		logger.info("There are " + amt + " records");
		return amt;
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getSpecficColRowValueFromExcel(File file, String sheetName, int colStart, int rowIndex) throws Exception
	{
		// logger.info("File is:"+file+", sheet is:"+sheetName+" , column start:"+colStart+" ,  row start:"+rowIndex);
		ArrayList<String> rowVal = new ArrayList<String>();
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workbook = WorkbookFactory.create(inp);
			Sheet sheet = null;
			if (sheetName == null)
				sheet = workbook.getSheetAt(0);
			else
				sheet = workbook.getSheet(sheetName);
			Row row = sheet.getRow(rowIndex);
			Cell cell = null;
			int colAmt = sheet.getRow(0).getPhysicalNumberOfCells();
			String cellValue = null;
			for (int i = colStart - 1; i < colAmt; i++)
			{
				cell = row.getCell(i);
				if (cell != null)
				{
					cellValue=getCellValue(cell);
				}
				else
				{
					cellValue = "";
				}

				rowVal.add(cellValue);
			}

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return rowVal;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getRowValueFromExcel(File file, String sheetName, int rowIndex) throws Exception
	{
		// logger.info("File is:"+file+", sheet is:"+sheetName+" , row is:"+rowIndex);
		ArrayList<String> rowVal = new ArrayList<String>();
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook xwb = WorkbookFactory.create(inp);
			inp.close();
			
			Sheet sheet = null;
			if (sheetName == null)
				sheet = xwb.getSheetAt(0);
			else
				sheet = xwb.getSheet(sheetName);
			Row row = sheet.getRow(rowIndex);
			Cell cell = null;

			int colAmt = sheet.getRow(0).getPhysicalNumberOfCells();
			String cellValue = null;
			for (int i = 0; i < colAmt; i++)
			{
				cell = row.getCell(i);
				if (cell != null)
				{
					cellValue=getCellValue(cell);
				}
				else
					cellValue = "";

				rowVal.add(cellValue);
			}

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			
			return rowVal;
		}
	}

	public static ArrayList<List<String>> getExcelContent(File file, String sheetName, int startColumn, int startRow) throws Exception
	{
		ArrayList<List<String>> content = new ArrayList<List<String>>();
		int amt = getRowAmts(file, sheetName);
		for (int i = startRow; i <= amt; i++)
		{
			ArrayList<String> rowContent = ExcelUtil.getSpecficColRowValueFromExcel(file, sheetName, startColumn, i);
			content.add(rowContent);
		}
		return content;
	}

	public static String getCellValueForArbitrary(File file, String sheetName, String cellName) throws Exception
	{
		String cellValue = null;
		try
		{
			InputStream inp = new FileInputStream(file);
			Workbook workBook = WorkbookFactory.create(inp);
			Sheet sheet = workBook.getSheet(sheetName);
			int rowID = Integer.parseInt(cellName.substring(numericPos(cellName))) - 1;
			int colID = convertCoumnID(cellName.substring(0, numericPos(cellName)));
			Row row = null;
			Cell cell = null;
			row = sheet.getRow(rowID);
			cell = row.getCell(colID);

			if (cell != null)
			{
				cellValue=getCellValue(cell);
			}
			else
			{
				logger.info("Cannot find cell[" + cellName + "] in file[" + file.getName() + "]");
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return cellValue;
	}

	public static List<String> getCellNamesFromExcel(File file) throws Exception
	{
		List<String> cellValue = new ArrayList<String>();
		try
		{
			InputStream inp = new FileInputStream(file);
			Workbook workBook = WorkbookFactory.create(inp);
			int nameCount = workBook.getNumberOfNames();
			for (int nameIndex = 0; nameIndex < nameCount; nameIndex++)
			{
				Name name = workBook.getNameAt(nameIndex);
				cellValue.add(name.getNameName());
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return cellValue;
	}

	public static String getCellValueByCellName(File file, String cellName, String instance, String rowKey) throws Exception
	{
		String cellValue = null;
		String rowindex = null;
		String columnindex = null;

		String sheetName = null;
		// logger.info("Get value of cell[" + cellName + ", rowkey=" + rowKey +
		// ",instance=" + instance + "] in excel");
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workBook = WorkbookFactory.create(inp);
			int nameCount = workBook.getNumberOfNames();
			for (int nameIndex = 0; nameIndex < nameCount; nameIndex++)
			{
				Name name = workBook.getNameAt(nameIndex);
				if (name.getNameName().equals(cellName))
				{
					String refe = name.getRefersToFormula();
					sheetName = refe.split("!")[0];
					String rowCloumn = refe.split("!")[1].substring(1);
					rowCloumn = rowCloumn.replace("$", "~");
					columnindex = rowCloumn.split("~")[0];
					rowindex = rowCloumn.split("~")[1];
					break;
				}
			}
			sheetName = sheetName.replace("'", "");
			Sheet sheet = workBook.getSheet(sheetName);
			if (sheet == null)
			{
				sheetName = sheetName + "|" + instance;
				sheet = workBook.getSheet(sheetName);
			}
			int rowID = Integer.parseInt(rowindex) - 1;
			int colID = convertCoumnID(columnindex);
			if (rowKey.length() > 0)
			{
				int rowNo = Integer.parseInt(rowKey);
				if (rowNo > 1)
				{
					rowID = rowID + rowNo - 1;
				}
			}
			Row row = null;
			Cell cell = null;
			try
			{
				row = sheet.getRow(rowID);
				cell = row.getCell(colID);
			}
			catch (Exception e)
			{

			}

			if (cell != null)
			{
				cellValue=getCellValue(cell);
			}
			else
			{
				logger.info("Cannot find cell[" + cellName + "] in file[" + file.getName() + "]");
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}

		return cellValue;
	}

	public static Map<String, String> getAllNames(File file) throws Exception
	{
		logger.info("Get all names");
		Map<String, String> names = new HashMap<String, String>();
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workBook = WorkbookFactory.create(inp);
			int nameCount = workBook.getNumberOfNames();
			for (int nameIndex = 0; nameIndex < nameCount; nameIndex++)
			{
				Name name = workBook.getNameAt(nameIndex);
				String refe = name.getRefersToFormula();
				String sheetName = refe.split("!")[0];
				String rowCloumn = refe.split("!")[1].substring(1);
				rowCloumn = rowCloumn.replace("$", "~");
				String columnindex = rowCloumn.split("~")[0];
				String rowindex = rowCloumn.split("~")[1];

				int rowID = Integer.parseInt(rowindex) - 1;
				int colID = convertCoumnID(columnindex);
				names.put(name.getNameName(), sheetName + "#" + rowID + "#" + colID);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}

		return names;
	}

	public static String getCellValueFromExcel(File file, String cellName, Map<String, String> names) throws Exception
	{
		logger.info("Get value of cell[" + cellName + "]");
		String cellValue = null;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workBook = WorkbookFactory.create(inp);
			String nameDetail = null;
			nameDetail = names.get(cellName);

			String sheetName = nameDetail.split("#")[0];
			int rowID = Integer.parseInt(nameDetail.split("#")[1]);
			int colID = Integer.parseInt(nameDetail.split("#")[2]);
			Sheet sheet = workBook.getSheet(sheetName);
			Row row = null;
			Cell cell = null;
			try
			{
				row = sheet.getRow(rowID);
				cell = row.getCell(colID);
			}
			catch (Exception e)
			{
				sheet = workBook.getSheet(sheetName);
				row = sheet.getRow(rowID);
				cell = row.getCell(colID);
			}

			if (cell != null)
			{
				cellValue=getCellValue(cell);
			}
			else
			{
				logger.info("Cannot find cell[" + cellName + "] in file[" + file.getName() + "]");
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}

		return cellValue;
	}

	public static List<String> getAllSheets(File file) throws Exception
	{

		List<String> sheets = new ArrayList<String>();
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workbook = WorkbookFactory.create(inp);
			int count = workbook.getNumberOfSheets();

			for (int index = 0; index < count; index++)
			{
				Sheet sheet = workbook.getSheetAt(index);
				sheets.add(sheet.getSheetName());
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}

		return sheets;
	}

	public static boolean isDefinedCellNameExistInExcel(File file, List<String> cellNames) throws Exception
	{

		boolean testResult = true;
		boolean find = false;
		InputStream inp = new FileInputStream(file);
		try
		{
			Workbook workbook = WorkbookFactory.create(inp);
			int nameCount = workbook.getNumberOfNames();
			for (String cellName : cellNames)
			{
				for (int nameIndex = 0; nameIndex < nameCount; nameIndex++)
				{
					Name name = workbook.getNameAt(nameIndex);
					if (name.getNameName().equals(cellName))
					{
						find = true;
					}

				}
				if (!find)
				{
					testResult = false;
					logger.error("Cannot find cell:" + cellName);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (inp != null)
				{
					inp.close();
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage());
			}
		}

		return testResult;
	}

	
	public static void writeToExcel(File fileName, int rowID, int colID, String value) throws Exception
	{
		FileInputStream inp = new FileInputStream(fileName);
		try
		{
			Workbook xwb = WorkbookFactory.create(inp);
			inp.close();
			if (!value.equals(""))
			{
				Sheet sheet = xwb.getSheetAt(0);
				Row row = sheet.getRow(rowID);
				if (row == null)
					row = sheet.createRow(rowID);
				Cell cell = row.getCell(colID);
				if (cell == null)
					cell = row.createCell(colID);
				
				int number_CellStyle=xwb.getNumCellStyles();
				CellStyle cellStyle=null;
				if(number_CellStyle>0){
					cellStyle=xwb.getCellStyleAt(number_CellStyle-1);
					cell.setCellStyle(cellStyle);
				}else{
					cellStyle = xwb.createCellStyle();
					DataFormat format = xwb.createDataFormat();
					cellStyle.setDataFormat(format.getFormat("@"));
					cell.setCellStyle(cellStyle);
				}
				cell.setCellValue(value);

				FileOutputStream out = new FileOutputStream(fileName);
				xwb.write(out);
				out.flush();
				out.close();

			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		
	}

	public static int convertCoumnID(String comlumnName)
	{
		int colID = 0;
		if (comlumnName.length() == 1)
		{
			char[] chars = comlumnName.toCharArray();
			colID = (int) chars[0];
			colID = colID - 65;
		}
		else
		{
			char[] chars = comlumnName.toCharArray();

			int id2 = (int) chars[1];
			if (comlumnName.substring(0, 1).equals("A"))
				colID = 26 + id2 - 65;
			else if (comlumnName.substring(0, 1).equals("B"))
				colID = 26 * 2 + id2 - 65;
			else if (comlumnName.substring(0, 1).equals("C"))
				colID = 26 * 3 + id2 - 65;

		}

		return colID;
	}

	public static int numericPos(String str)
	{
		int pos = 0;
		for (int i = 0; i < str.length(); i++)
		{
			if (Character.isDigit(str.charAt(i)))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}

	public static List<String> getLastCaseID(File file, String caseID) throws Exception
	{
		List<String> rst = new ArrayList<String>();
		InputStream inp = new FileInputStream(file);
		Workbook workBook = WorkbookFactory.create(inp);
		Sheet sheet = workBook.getSheetAt(0);

		Row row = null;
		Cell cell = null;
		int rowAmt = sheet.getLastRowNum();
		if (rowAmt > 0)
		{
			rst.add(String.valueOf(rowAmt));

			row = sheet.getRow(rowAmt);
			cell = row.getCell(0);
			String caseNO = cell.getStringCellValue();
			if (caseNO.contains("."))
				caseNO = caseNO.split(".")[0];

			rst.add(caseNO);
			cell = row.getCell(1);
			rst.add(cell.getStringCellValue());
		}
		return rst;

	}

	public static void WriteTestResult(File testRstFile, String caseID, String step, String testRst) throws Exception
	{
		int rowIndex = getRowNums(testRstFile, null) + 1;
		writeToExcel(testRstFile, rowIndex, 0, caseID);
		if (!step.equals(""))
			writeToExcel(testRstFile, rowIndex, 1, step);
		writeToExcel(testRstFile, rowIndex, 2, testRst);
	}

	public static void WriteTestRst(File testRstFile, String caseID, String testRst, String module) throws Exception
	{
		int rowIndex = getRowNums(testRstFile, null) + 1;
		writeToExcel(testRstFile, rowIndex, 0, caseID);
		writeToExcel(testRstFile, rowIndex, 1, testRst);
		writeToExcel(testRstFile, rowIndex, 2, module);
	}

	public static void writeTestRstToFile(File testRstFile, int rowID, int colID, boolean testRst) throws Exception
	{
		String testResult = null;
		if (testRst)
			testResult = "Pass";
		else
			testResult = "Fail";
		writeToExcel(testRstFile, rowID, colID, testResult);
	}

	public static boolean isInteger(String str)
	{
		return str.matches("[0-9]+");
	}
	
	
/**
 * open a Workbook.<br>created by Kun.Shen
 * <p>create a FileInputStream for excel file, and read this file into workbook, then close this FileInputStream.<br>
 * @param filename: a excel file.
 * @return Workbook
 * @throws Exception
 */
public static Workbook openWorkbook(File filename) throws Exception
{
	FileInputStream inp = new FileInputStream(filename);
	Workbook workBook = WorkbookFactory.create(inp);
	CellStyle cellStyle = workBook.createCellStyle();
	DataFormat format = workBook.createDataFormat();
	cellStyle.setDataFormat(format.getFormat("@"));
	inp.close();
	return workBook;
}
/**
 * save Workbook.<br>created by Kun.Shen
 * <p>after editing all cells, then write it to Workbook.
 * @param filename a excel file.
 * @param workBook a Workbook
 * @throws Exception
 */
public static void saveWorkbook(File filename,Workbook workBook) throws Exception
{
	FileOutputStream out = new FileOutputStream(filename);
	workBook.write(out);
	out.flush();
	out.close();
}
/**
 * get the sheet of Workbook's total rows<br>created by Kun.Shen
 * @param workBook a Workbook
 * @param sheetName a sheet's name or null(means the first position sheet).
 * @return total rows in this sheet.
 * @throws Exception
 */
public static int getRowNum(Workbook workBook, String sheetName) throws Exception
{
	// logger.info("File is:"+file);
	int amt = 0;
	try
	{
		Sheet sheet = null;
		if (sheetName == null)
			sheet = workBook.getSheetAt(0);
		else
			sheet = workBook.getSheet(sheetName);
		amt = sheet.getLastRowNum();
	}
	catch (Exception e)
	{
		logger.error(e.getMessage());
	}
	return amt;	
}
/**
 * get all cells from a special row.<br>
 * @author kun shen
 * @param workBook a Workbook.
 * @param sheetName a sheet's name or null(means the first position sheet).
 * @param rowIndex a special row index.
 * @return a Array, all cells' value in this row.
 * @throws Exception
 */
public static ArrayList<String> getValueFromRow(Workbook workBook,String sheetName, int rowIndex) throws Exception
{
	ArrayList<String> rowVal = new ArrayList<String>();
	try{
		Sheet sheet = null;
		if (sheetName == null)
			sheet = workBook.getSheetAt(0);
		else
			sheet = workBook.getSheet(sheetName);
		
		Row row = sheet.getRow(rowIndex);
		if(row==null){return null;}
		int colAmt = sheet.getRow(0).getPhysicalNumberOfCells();

		for(int i = 0; i < colAmt; i++){

			Cell cell = row.getCell(i);
			String cellValue = null;
			if (cell != null)
			{
				cellValue=getCellValue(cell);
			}
			else
				cellValue = "";

			rowVal.add(cellValue);
		
		}
		
	}catch(Exception e){
		logger.error(e.getMessage());
	}
	return rowVal;
	
}

/**
 * edit a special cell<br>created by Kun.Shen
 * @param workBook a Workbook.
 * @param sheetName a sheet's name or null(means the first position sheet).
 * @param rowIndex a special row index, from 0...
 * @param colIndex a special column index, from 0...
 * @param value a special value for this cell.
 */
public static void editCell(Workbook workBook,String sheetName, int rowIndex, int colIndex, String value)
{
	if(!value.isEmpty()){
		Sheet sheet = null;
		if (sheetName == null)
			sheet = workBook.getSheetAt(0);
		else
			sheet = workBook.getSheet(sheetName);
		Row row = sheet.getRow(rowIndex);
		if (row == null)
			row = sheet.createRow(rowIndex);
		Cell cell = row.getCell(colIndex);
		if (cell == null)
			{
				cell = row.createCell(colIndex);
			}

		int number_CellStyle=workBook.getNumCellStyles();
		CellStyle cellStyle=null;
		if(number_CellStyle>0){
			cellStyle=workBook.getCellStyleAt(number_CellStyle-1);
			cell.setCellStyle(cellStyle);
		}else{
			cellStyle = workBook.createCellStyle();
			DataFormat format = workBook.createDataFormat();
			cellStyle.setDataFormat(format.getFormat("@"));
			cell.setCellStyle(cellStyle);
		}
		cell.setCellValue(value);
	}
}

/**
 * get object from a excel row<br>created by Kun.Shen
 * @param titleRow excel's sheet title row
 * @param rootRow the row can translate to object
 * @param pojo which object's class you want to translate
 * @return
 * @throws Exception
 */

@SuppressWarnings("rawtypes")  
private static Object fromRowToBean(Row titleRow,Row rootRow, Class pojo, List<Class> claes) throws Exception  
{  
    // 首先得到pojo所定义的字段  
    Field[] fields = pojo.getDeclaredFields();  
    // 根据传入的Class动态生成pojo对象  
    Object obj = pojo.newInstance();  
    
    for (Field field : fields)  
    {  
    	int mod=field.getModifiers();
    	if(Modifier.isFinal(mod) && Modifier.isStatic(mod)&& field.getType().equals(Logger.class)) continue;
        // 设置字段可访问（必须，否则报错）  
        field.setAccessible(true);  
        // 得到字段的属性名  
        String name = field.getName();  
        // 这一段的作用是如果字段在Element中不存在会抛出异常，如果出异常，则跳过。
        String rootCellValue=null;
        try  
        {
        	if (claes!=null && claes.contains(field.getType()))  
        	{  
        		 field.set(obj,fromRowToBean(titleRow,rootRow,field.getType(),claes));
        		 continue;
        	}
        	int lastCellNum=titleRow.getLastCellNum();
        	Cell titleCell=null;
        	String titleCellValue=null;
        	for(int cellNum=0;cellNum<lastCellNum;cellNum++)
        	{
        		titleCell=titleRow.getCell(cellNum);
        		if(titleCell==null){continue;}
        		titleCellValue=getCellValue(titleCell).trim();
        		
        		if(titleCellValue!=null && (name.equalsIgnoreCase(titleCellValue) || titleCellValue.equalsIgnoreCase(pojo.getSimpleName()+"."+name)))
        		{
        			Cell rootCell=rootRow.getCell(cellNum);
        			if(rootCell!=null) {rootCellValue=getCellValue(rootCell).trim();}
        			break;
        			
        		}
        	}
        	
        }catch (Exception ex)  
        {  
        	continue;  
        }  
        if (StringUtils.isNotBlank(rootCellValue))  
        {  
            // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。  
            if (field.getType().equals(String.class))  
            {  
            	field.set(obj, rootCellValue);   
            }
            else if (field.getType().equals(Long.class) || field.getType().equals(long.class))  
            {  
                field.set(obj, Long.parseLong(rootCellValue));
            }  
            else if (field.getType().equals(Double.class) || field.getType().equals(double.class))  
            {  
                field.set(obj, Double.parseDouble(rootCellValue));  
            }  
            else if (field.getType().equals(Integer.class) || field.getType().equals(int.class))  
            {  
                field.set(obj, Integer.parseInt(rootCellValue));  
            }
        }  
        
    }  
    return obj;  
}


private static String getCellValue(Cell cell)
{
	if (cell ==null){return null; }
    String cellValue=null;
    DataFormatter formatter = new DataFormatter();
    //poi 4.0.0
    /*if(cell.getCellType().equals(CellType.NUMERIC)){
    	if (DateUtil.isCellDateFormatted(cell))
		{
			cellValue = formatter.formatCellValue(cell);
		}
		else
		{
			double value = cell.getNumericCellValue();
			long intValue = (long) value;
			cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
							
		}
    }else if(cell.getCellType().equals(CellType.STRING)){
    	cellValue = cell.getStringCellValue();
    }else if(cell.getCellType().equals(CellType.BOOLEAN)){
    	cellValue = String.valueOf(cell.getBooleanCellValue());
    }else if(cell.getCellType().equals(CellType.FORMULA)){
    	cellValue = cell.getStringCellValue();
    }else if(cell.getCellType().equals(CellType.BLANK) || cell.getCellType().equals(CellType.ERROR)){
    	cellValue = "";
    }else{
    	cellValue = cell.toString().trim();
    }*/
   switch (cell.getCellType())
	{
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
			{
				cellValue = formatter.formatCellValue(cell);
			}
			else
			{
				double value = cell.getNumericCellValue();
				long intValue = (long) value;
				cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
								
			}
			break;
		case Cell.CELL_TYPE_STRING:
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			//cellValue = String.valueOf(cell.getCellFormula());
			cellValue = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			cellValue = "";
			break;
		case Cell.CELL_TYPE_ERROR:
			cellValue = "";
			break;
		default:
			cellValue = cell.toString().trim();
			break;
	}
    return cellValue;
}


/***
 * translate a object to a excel row
 * @param rootRow the row you want to write object, skip head row(index=0)
 * @param obj the object you want to write to the rootRow
 * @param claes inner classes of this obj's class
 * @param indexOfColumn start index of column
 * @return index of column
 * @throws Exception
 * @author kun shen
 * @since 2018/11/12
 */
@SuppressWarnings("rawtypes")  
private static int fromBeanToRow(Row rootRow, Object obj, List<Class> claes,int indexOfColumn) throws Exception  
{  
	Class pojo=obj.getClass();
    // 首先得到pojo所定义的字段  
    Field[] fields = pojo.getDeclaredFields();
    
    Boolean flagForWriteTitle=false;
    int index=rootRow.getRowNum();
    Row titleRow=null;
    if(index==1)
    {
    	flagForWriteTitle=true;
    	titleRow=rootRow.getSheet().getRow(0);
    	if(titleRow==null){
    		titleRow=rootRow.getSheet().createRow(0);
    	}
    }

    for (Field field : fields)  
    {  
    	int mod=field.getModifiers();
    	if(Modifier.isFinal(mod) && Modifier.isStatic(mod)&& field.getType().equals(Logger.class)) continue;
        // 设置字段可访问（必须，否则报错）  
        field.setAccessible(true);  
        // 得到字段的属性名  
        String name = field.getName();  
        Object valueObj=field.get(obj);
        Cell cell=null;
        try  
        {  	
        	if(claes!=null && claes.contains(field.getType())){
        		indexOfColumn=fromBeanToRow(rootRow,valueObj,claes,indexOfColumn);
        		continue;
        	}
        	if(valueObj!=null)
        	{
        		cell=rootRow.createCell(indexOfColumn);
        		cell.setCellType(1);//CellType.STRING
        		cell.setCellValue(valueObj.toString());
        	}
        	if(flagForWriteTitle)
        	{
        		cell=titleRow.createCell(indexOfColumn);
        		cell.setCellType(1);
        		if(claes!=null && claes.contains(pojo))
        		{
        			cell.setCellValue(pojo.getSimpleName()+"."+name);
        		}else
        		{
        			cell.setCellValue(name);
        		}
        	}
        }  
        catch (Exception ex)  
        {  
        	continue;  
        }  
        indexOfColumn++;
        
    }  
    return indexOfColumn;
}

/**
 * get forms from excel<br>created by Kun.Shen
 * @param excelFileStr fullpath with file name
 * @return
 */
public static List<Form> getForms(String excelFileStr)
{
	return getForms(excelFileStr,null);
}


/**
 * get forms from excel
 * @author kun shen
 * @param excelFileStr fullpath with file name
 * @param sheetName sheet's name, use null if use first sheet.
 * @return an list of forms or an empty list.
 * @since 2017.03.01
 */
public static List<Form> getForms(String excelFileStr,String sheetName)
{
	return getObjects(excelFileStr,sheetName, false,Form.class);
}

/***
 * get objects from excel
 * @param excelFileStr excel's full path and name
 * @param sheetName excel's sheet name, if {sheetName} is null, {getLastOne} is true, get the last sheet; if {sheetName} is null, {getLastOne} is false, get the first sheet
 * @param getLastOne true get last sheet start with {sheetName} plus numbers, like ExportToCSV3, false get the sheet named {sheetName}
 * @param pojo
 * @return
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public static <T> List<T> getObjects(String excelFileStr,String sheetName,Boolean getLastOne,Class pojo)
{
	List<T> list = new ArrayList<T>(); 
	Workbook xwb =null;
	try
	{
		xwb =ExcelUtil.openWorkbook(new File(excelFileStr));
		Sheet sheet = null;
		if(StringUtils.isNotBlank(sheetName))
		{
			sheet=xwb.getSheet(sheetName);
			if(getLastOne)
			{
				int lastIndex=xwb.getNumberOfSheets()-1;
				for(int index=lastIndex;index>=0;index--)
				{
					String sheetNameTmp=xwb.getSheetName(index);
					if(sheetNameTmp.equalsIgnoreCase(sheetName)){break;}
					if(sheetNameTmp.startsWith(sheetName)){
						String a=sheetNameTmp.substring(sheetName.length());
						Pattern patern=Pattern.compile("\\d+");
						Matcher isNum=patern.matcher(a);
						if(isNum.matches())
						{
							sheet=xwb.getSheet(sheetNameTmp);
							break;
						}
					}
				}
			}
		}else{
			if(getLastOne){
				sheet=xwb.getSheetAt(xwb.getNumberOfSheets()-1);
			}
		}
		if(sheet==null)
		{sheet = xwb.getSheetAt(0);}
		List<Class> innerClaes=new ArrayList<Class>();
		innerClaes.add(Transmission.class);
		Row titleRow=sheet.getRow(0);
		int rowNum=sheet.getLastRowNum();
		for(int i=1;i<=rowNum;i++)
		{
			Row row=sheet.getRow(i);
			if(row==null){continue;}
			//Form form=(Form)fromRowToBean(titleRow,row,Form.class,innerClaes);
			T form=(T)fromRowToBean(titleRow,row,pojo,innerClaes);
			if(StringUtils.isNoneBlank(form.toString()))
			{
				if(form instanceof Form){
					if(StringUtils.isNoneBlank(((Form)form).getRun()) && ((Form)form).getRun().equalsIgnoreCase("Y") && (StringUtils.isBlank(((Form)form).getExpiration()) ||!((Form)form).getExpiration().equalsIgnoreCase("Y"))){
						list.add(form);
					}
				}else{
					list.add(form);
				}
			}
		}
		
	}catch(Exception e)
	{
		System.out.println("data parsed error");  
	}
	return list;
}


/**
 * write forms to Excel 
 * @author kun shen
 * @param forms
 * @param excelFileStr fullpath with file name (support .xlsx and .xls formats)
 * @param sheetName
 * @since 2017.03.01
 */
public static void writeFormsToExcel(List<Form> forms,String excelFileStr,String sheetName)
{
	writeObjectsToExcel(forms, excelFileStr, sheetName, false);
}

/**
 * write objects to Excel 
 * @author kun shen
 * @param forms
 * @param excelFileStr fullpath with file name (support .xlsx and .xls formats)
 * @param sheetName, if {sheetName} is null, {rewrite} is true, get the last sheet
 * @param rewrite
 * @since 2017.10.25
 */
@SuppressWarnings("rawtypes")
public static <T> void writeObjectsToExcel(List<T> forms,String excelFileStr,String sheetName,Boolean rewrite)
{
	File excelFile=new File(excelFileStr);
	Workbook xwb=null;
	FileInputStream fileInputStream=null;
	try
	{
		if(!excelFile.exists())
		{
			//excelFile.createNewFile();
			if(excelFileStr.endsWith(".xls"))
			{
				xwb=new HSSFWorkbook();
			}
			if(excelFileStr.endsWith(".xlsx"))
			{
				xwb=new XSSFWorkbook();
			}
			
		}
		else
		{
			fileInputStream = new FileInputStream(excelFile);
			xwb = WorkbookFactory.create(fileInputStream);
			fileInputStream.close();
			int lastIndex=xwb.getNumberOfSheets()-1;
			if(rewrite)
			{
				String sheetNameTmp=null;
				int index=lastIndex;
				for(;index>=0;index--)
				{
					sheetNameTmp=xwb.getSheetName(index);
					if(StringUtils.isNotBlank(sheetName))
					{
						if(sheetNameTmp.equalsIgnoreCase(sheetName)|| sheetName.equalsIgnoreCase("null")){
							break;
						}
						if(sheetNameTmp.startsWith(sheetName)){
							String a=sheetNameTmp.substring(sheetName.length());
							Pattern patern=Pattern.compile("\\d+");
							Matcher isNum=patern.matcher(a);
							if(isNum.matches())
							{
								break;
							}else{
								
							}
						}
					}else{
						break;
					}
				}
				sheetName=sheetNameTmp;
				if(index>=0){
					xwb.removeSheetAt(index);//remove it, create new one at next step
				}
			}else{
				if(StringUtils.isBlank(sheetName) || sheetName.equalsIgnoreCase("null")){
					sheetName=xwb.getSheetName(lastIndex);
				}
			}
		}
		Sheet sheet = null;
		int i=1;
		String sheetNameVar=sheetName;
		while(sheet==null)
		{
			try
			{
				sheet=xwb.createSheet(sheetName);
			}catch(Exception e)
			{
				sheetName = sheetNameVar+String.valueOf(i);
				i++;
				continue;
			}
		}

		List<Class> innerClaes=new ArrayList<Class>();
		innerClaes.add(Transmission.class);
		for(i=0;i<forms.size();i++)
		{
			Row row=sheet.createRow(i+1);
			fromBeanToRow(row,forms.get(i),innerClaes,0);
		}
		FileOutputStream out = new FileOutputStream(excelFileStr);
		xwb.write(out);
		out.flush();
		out.close();
	}
	catch (Exception e)
	{
		logger.error(e.getMessage());
	}
	finally
	{
		if(fileInputStream!=null)
		{
			
			try {  
				fileInputStream.close();  
				} catch (IOException e) {logger.error(e.getMessage());}  

		}
	}
	
}

/**
 * use in "create new return from excel" function, update some values for QA.
 * @param importExcelPath
 * @param cells( sheetName, rowIndex, colIndex, value, date type) or (CellReferenceName,"-1","-1",value, date type)
 * @since 2017/7/13
 */
public static void updateCellsInExcel(String importExcelPath,String[][] cells)
{
	File importExcel=new File(importExcelPath);
	if(importExcel.exists())
	{
		updateCellsInExcel(importExcel,cells);
	}else
	{
		logger.error("File Not Found: "+importExcelPath);
	}
}	

/**
 * use in "create new return from excel" function, update some values for QA.
 * @param importExcelPath
 * @param cells( sheetName, rowIndex, colIndex, value)
 * @since 2017/7/13
 */
public static void updateCellsInExcel(File importExcel,String[][] cells)
{
	Workbook xwb=null;
	FileInputStream fileInputStream=null;
	try {
		fileInputStream=new FileInputStream(importExcel);
		xwb=WorkbookFactory.create(fileInputStream);
		fileInputStream.close();
		Sheet sheet=null;
		String sheetName=null,value=null,valueType=null;
		int rowIndex,colIndex;
		for(int i=0;i<cells.length;i++)
		{
			if(!cells[i][1].equals("-1") && !cells[i][2].equals("-1")){
				sheetName=cells[i][0];
				rowIndex=Integer.parseInt(cells[i][1])-1;//0-base, row number
				colIndex=Integer.parseInt(cells[i][2])-1;//0-base, column runmber
			}else{
				Name aNamedCell=xwb.getName(cells[i][0]);
				String aNamedAddress=aNamedCell.getRefersToFormula();
				AreaReference[] arefs=AreaReference.generateContiguous(aNamedAddress);
				CellReference crefs=arefs[0].getFirstCell();
				sheetName=crefs.getSheetName();
				rowIndex=crefs.getRow();
				colIndex=crefs.getCol();
			}
			
			value=cells[i][3];
			valueType=cells[i][4];
			if(sheetName!=null)
			{sheet=xwb.getSheet(sheetName);}
			if(sheet==null)
			{sheet = xwb.getSheetAt(0);}
			Row row = sheet.getRow(rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);
			Cell cell = row.getCell(colIndex);
			if (cell == null)
				cell = row.createCell(colIndex);
			
			CellStyle cellStyle=cell.getCellStyle();
			if(valueType!=null && valueType.startsWith("date:"))
			{
				String simpleDateFormatStr="MM/dd/yyyy";
				String dateFormat=valueType.replace("date:", "");
				if (dateFormat.equalsIgnoreCase("en_GB"))
				{
					simpleDateFormatStr="dd/MM/yyyy";
				}else if (dateFormat.equalsIgnoreCase("zh_CN"))
				{
					simpleDateFormatStr="yyyy-MM-dd";
				}
				SimpleDateFormat sdf=new SimpleDateFormat(simpleDateFormatStr);
				Date date=sdf.parse(value);
				cell.setCellValue(date);
			}else
			{
				cell.setCellValue(value);
			}
			cell.setCellStyle(cellStyle);
			
		}
		
		ExcelUtil.saveWorkbook(importExcel, xwb);
	} catch (Exception e) {
		logger.error(e.getMessage());
	}
}

}
