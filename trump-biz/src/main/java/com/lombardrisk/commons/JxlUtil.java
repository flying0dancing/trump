package com.lombardrisk.commons;

import java.io.File;
import java.io.FileInputStream;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.Label;
import jxl.format.CellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author kun shen
 * @since 2017/7/14
 */
public class JxlUtil {

	private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);


	private JxlUtil(){}
	
	public static void UpdateCellInExcel(String importExcelPath,String[][] cells)
	{
		File importExcel=new File(importExcelPath);
		if(importExcel.exists())
		{
			JxlUtil.UpdateCellInExcel(importExcel,cells);
		}else
		{
			logger.error("File Not Found: "+importExcelPath);
		}
	}	
	public static void UpdateCellInExcel(File importExcel,String[][] cells)
	{
		Workbook xwb=null;
		WritableWorkbook book=null;
		try {
			xwb=Workbook.getWorkbook(importExcel);
			book=Workbook.createWorkbook(importExcel, xwb);
			WritableSheet sheet=null;
			String sheetName=null,value=null;
			int rowIndex,colIndex;
			for(int i=0;i<cells.length;i++)
			{
				sheetName=cells[i][0];
				rowIndex=Integer.parseInt(cells[i][1])-1;//0-base
				colIndex=Integer.parseInt(cells[i][2])-1;//0-base
				value=cells[i][3];
				if(sheetName!=null)
				{sheet=book.getSheet(sheetName);}
				if(sheet==null)
				{sheet = book.getSheet(0);}
				
				Cell cell=sheet.getCell(colIndex, rowIndex);
				CellFormat cellFormat=cell.getCellFormat();
				Label label=new Label(colIndex, rowIndex,value);
				label.setCellFormat(cellFormat);
				sheet.addCell(label);
				book.write();
				
			}
			book.close();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	
}
