package com.lombardrisk.commons;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter; 
import java.util.ArrayList;  
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;  
import java.util.List;  

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;  
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;  
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;  
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lombardrisk.test.pojo.Form;

public class Dom4jUtil {
	final static Logger logger=LoggerFactory.getLogger(Dom4jUtil.class);
    /** 
     * @param args 
     * @throws DocumentException 
     */  /*
    public static void main(String[] args) throws DocumentException {  
        // TODO Auto-generated method stub
        String xmlFileFullPath="D:/PersonalSummary_LombardRisk/LN_Tools_Selenium/XSL/form.xml";
        List<Form> forms=getForms(new File(xmlFileFullPath).getAbsolutePath());
        for(Form form:forms){
        	System.out.println(form.getName()+ "|"+form.getVersion()+ "|"+form.getRegulator()+ "|"+form.getAllowNull()+ "|"+
        			form.getEntity()+ "|"+form.getProcessDate()+ "|"+form.getInitToZero()+ "|"+form.getDeleteExistent()+ "|"+
        			form.getRun()+ "|"+form.getExpiration()+ "|"+form.getImportFile()+ "|"+form.getExpectationFile()+ "|"+form.getExecutionStatus()+ "|"+
        			form.getTransmission().getFileType()+ "|"+form.getTransmission().getFramework()+ "|"+form.getTransmission().getTaxonomy()+ "|"+form.getTransmission().getModule());
        	form.setExecutionStatus("pass");
        	form.getTransmission().setTaxonomy("FED_1.0.0");
        }
        String tmp="D:/PersonalSummary_LombardRisk/LN_Tools_Selenium/XSL/formlist.xsl";
        Document doc=writeFormToDocument(forms,null);
        writeDocumentToXml(doc,"D:/PersonalSummary_LombardRisk/LN_Tools_Selenium/XSL/formout.xml");
    }  
  */
    /** 
     * 把xml转化为Form集合 
     *  
     * @param xml 
     * @return 
     */  
public static List<Form> getForms(String xmlFileStr) 
{  
	Document doc = null;  
    List<Form> list = new ArrayList<Form>();  
    try {  

        // 读取并解析XML文档  
        // SAXReader就是一个管道，用一个流的方式，把xml文件读出来  
         SAXReader reader = new SAXReader();   
         File xmlFile=new File(xmlFileStr);
         doc = reader.read(xmlFile);  

        Element rootElt = doc.getRootElement(); // 获取根节点  

        //System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称  

        @SuppressWarnings("unchecked")
		Iterator<Element> it = rootElt.elementIterator("form");// 获取根节点下所有form 
        while (it.hasNext()) {  
            Element elementGroupService = (Element) it.next(); 
            Form baseBean = (Form) XmlUtil.fromXmlToBean(  
                    elementGroupService, Form.class);  
            if(StringUtils.isNoneBlank(baseBean.toString()) && StringUtils.isNoneBlank(baseBean.getRun()) && baseBean.getRun().equalsIgnoreCase("Y") && (StringUtils.isBlank(baseBean.getExpiration()) ||!baseBean.getExpiration().equalsIgnoreCase("Y")))
			{
                list.add(baseBean);  
			}

        }

    } catch (Exception e) {  
        // TODO: handle exception  
        System.out.println("data parsed error");  
    }  

    return list; 
} 


    
public static Document writeFormsToDocument(List<Form> forms,String xslFile) 
{ 
	Document doc = DocumentHelper.createDocument();  
    try
    {
    	if(xslFile!=null)
    	{doc.addProcessingInstruction("xml-stylesheet", "type='text/xsl' href='"+xslFile+"'");}
    	Element root=doc.addElement("list");
    	for(Form form:forms)
    	{
    		Element formElement=root.addElement("form");
    		XmlUtil.fromBeanToElement(formElement,form);
    	}
    	
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    return doc;  
} 
public static void writeFormsToMethodXml(List<Form> forms,String xmlFileStr,String xslFile) 
{ 
	Document doc =null;
    try
    {
    	File xmlFile=new File(xmlFileStr);
    	if(xmlFile.exists())
    	{
    		xmlFile.delete();
    	}
    	doc= DocumentHelper.createDocument();  
    	if(xslFile!=null)
    	{doc.addProcessingInstruction("xml-stylesheet", "type='text/xsl' href='"+xslFile+"'");}
    	Element root=doc.addElement("list");
    	for(Form form:forms)
    	{
    		Element formElement=root.addElement("form");
    		XmlUtil.fromBeanToElement(formElement,form);
    	}
    	writeDocumentToXml(doc,xmlFileStr);
    	
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
}

public static void writeFormsToXml(String identifer,List<Form> forms,String xmlFileStr,String xslFile) 
{ 
	Document doc =null;
    try
    {
    	SAXReader reader=new SAXReader();
    	File xmlFile=new File(xmlFileStr);
    	Element root=null;
    	if(!xmlFile.exists())
    	{
    		doc= DocumentHelper.createDocument();  
        	if(xslFile!=null)
        	{doc.addProcessingInstruction("xml-stylesheet", "type='text/xsl' href='"+xslFile+"'");}
        	root=doc.addElement("list");
        	
    	}else
    	{
    		doc=reader.read(xmlFile);
    		root=doc.getRootElement();//list
    	}
    	@SuppressWarnings("unchecked")
    	Iterator<Element> it = root.elementIterator("forms");
        while (it.hasNext()) {  
            Element elementForms = (Element) it.next(); 
            if(identifer.equalsIgnoreCase(elementForms.attributeValue("id")))
            {
            	root.remove(elementForms);
            	break;
            }
            
        }
    	Element identifierElement=root.addElement("forms");
    	identifierElement.addAttribute("id", identifer);
    	for(Form form:forms)
    	{
    		Element formElement=identifierElement.addElement("form");
    		XmlUtil.fromBeanToElement(formElement,form);
    		
    	}
    	writeDocumentToXml(doc,xmlFileStr);
    	
    }catch(Exception e)
    {
    	e.printStackTrace();
    } 
}

public static void writeDocumentToXml(Document doc,String xmlFileStr) 
{ 
	try
	{
		OutputFormat format=OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		File fileHandler=new File(xmlFileStr);
		FileOutputStream fileOutputStream=new FileOutputStream(fileHandler);
		OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
		XMLWriter writer=new XMLWriter(outputStreamWriter,format);
		writer.write(doc);
		writer.flush();
		writer.close();
		outputStreamWriter.close();
		fileOutputStream.close();
		
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	
} 
/**
 * not used
 * @param reportOutputFolder
 */
public static void writeReportOutputToPOM(String reportOutputFolder)
{
		Document doc =null;
	    try
	    {
	    	SAXReader reader=new SAXReader();
	    	File xmlFile=new File("pom.xml");
	    	Element root=null;
	    	if(xmlFile.exists())
	    	{
	    		doc=reader.read(xmlFile);
	    		root=doc.getRootElement();//list
	        	
	    	}
	    	Element propertiesElt=root.element("properties");
	    	@SuppressWarnings("unchecked")
	    	Iterator<Element> it = propertiesElt.elementIterator();
	        while (it.hasNext()) {  
	            Element element = (Element) it.next(); 
	            if(element.getName().equals("reportOutput"))
	            {
	            	propertiesElt.remove(element);
	            	break;
	            }
	            
	        }
	        propertiesElt.addElement("reportOutput").setText(reportOutputFolder);
	    	writeDocumentToXml(doc,"pom.xml");
	    	
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    } 

}

/**
 * purpose for parallel use.
 * @param identifer
 * @param forms
 * @param xmlFileStr
 * @param xslFile
 */
public static void writeFormToXml(String identifer,Form form,String xmlFileStr,String xslFile) 
{ 
	Document doc =null;
    try
    {
    	SAXReader reader=new SAXReader();
    	File xmlFile=new File(xmlFileStr);
    	Element root=null;
    	if(!xmlFile.exists())
    	{
    		doc= DocumentHelper.createDocument();  
        	if(xslFile!=null)
        	{doc.addProcessingInstruction("xml-stylesheet", "type='text/xsl' href='"+xslFile+"'");}
        	root=doc.addElement("list");
        	
    	}else
    	{
    		doc=reader.read(xmlFile);
    		root=doc.getRootElement();//list
    	}
    	@SuppressWarnings("unchecked")
    	Iterator<Element> it = root.elementIterator("forms");
    	boolean flag=false;
        while (it.hasNext()) {  
            Element elementForms = (Element) it.next(); 
            if(identifer.equalsIgnoreCase(elementForms.attributeValue("id")))
            {
            	//root.remove(elementForms);
            	//new add (start)
            	@SuppressWarnings("unchecked")
            	Iterator<Element> formit = elementForms.elementIterator("form");
            	while(formit.hasNext())
            	{
            		Element elementForm=formit.next();
            		/*if(form.getName().equalsIgnoreCase(elementForm.attributeValue("name")) && form.getVersion().equalsIgnoreCase(elementForm.attributeValue("version")) && form.getRegulator().equalsIgnoreCase(elementForm.attributeValue("regulator")) )
            		{
            			if(form.getEntity().equalsIgnoreCase(elementForm.elementText("entity")) &&form.getProcessDate().equalsIgnoreCase(elementForm.elementText("processDate")))
            			{
            				if(form.getImportFile().equalsIgnoreCase(elementForm.elementText("importFile")) &&form.getExpectationFile().equalsIgnoreCase(elementForm.elementText("expectationFile")))
            				{
            					//elementForm.element("importFile").setText(form.getImportFile());
                				//elementForm.element("executionStatus").setText(form.getExecutionStatus());
                				//flag=true;
                				elementForm.detach();
                				break;
            				}
            			}
            		}*/
            		
            		Form elementFormBean = (Form) XmlUtil.fromXmlToBean(elementForm, Form.class); 
            		if(form.equals(elementFormBean))
            		{
            			logger.debug("cover same form");
            			elementForm.detach();
        				break;
            		}
            	}
            	if(!flag)
            	{
            		//new add (end)
                	Element formElement=elementForms.addElement("form");
                	XmlUtil.fromBeanToElement(formElement,form);
                	flag=true;
                	break;
            	}
            	
            }
            
        }
    	if(!flag)
    	{
    		Element identifierElement=root.addElement("forms");
        	identifierElement.addAttribute("id", identifer);
        	Element formElement=identifierElement.addElement("form");
    		XmlUtil.fromBeanToElement(formElement,form);
    	}
    	writeDocumentToXml(doc,xmlFileStr);
    }catch(Exception e)
    {
    	e.printStackTrace();
    } 
}

/**
 * sort xml content with ignore attributes and ignore elements.
 * @param fileFullName
 * @param ignoreAttributes
 * @param ignoreElements
 * @return sorted file's full name with "_sort" suffix.
 */
public static String sortXmlContentToNewFileByName(String fileFullName,List<String> ignoreAttributes,List<String> ignoreElements,String newFilePath)
{
	logger.info("sorte xml by element.");
	String newFileFullName=null;
	Document doc =null;
	SAXReader reader=new SAXReader();
	File file=new File(fileFullName);
	Element root=null;
	try {
		doc=reader.read(file);
		root=doc.getRootElement();
		Document docNew=DocumentHelper.createDocument();
		//add new element as root
		Element rootNew=docNew.addElement(root.getName());
		
		sortXmlElementByName(root,rootNew,ignoreAttributes,ignoreElements);
		newFileFullName=FileUtil.createNewFileWithSuffix(fileFullName,"_sort",newFilePath);
		writeDocumentToXml(docNew,newFileFullName);
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return newFileFullName;
}

/**
 * sort element,and adding sorted elements to elementNew
 * @param element
 * @param elementNew
 */
public static void sortXmlElementByName(Element element,Element elementNew,List<String> ignoreAttributes,List<String> ignoreElements)
{
	Element eleNew;
	if(!ignoreElements.contains(element.getName())){
		eleNew=elementNew.addElement(element.getName());
		eleNew.addText(element.getText());
		@SuppressWarnings("unchecked")
		List<Attribute> attrs=element.attributes();
		List<Attribute> attrsNew=new ArrayList<Attribute>();
		for(Attribute attr:attrs)
		{
			if(!ignoreAttributes.contains(attr.getName()))
			{
				attrsNew.add(attr);
			}
		}
		if(attrsNew.size()>0){
			Collections.sort(attrsNew, new Comparator<Attribute>(){
				public int compare(Attribute o1, Attribute o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			for(Attribute attr:attrsNew){
				eleNew.addAttribute(attr.getName(), attr.getValue());
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Element> list=element.elements();
		if(list!=null && list.size()>0)
		{
			Collections.sort(list, new Comparator<Element>(){
				public int compare(Element o1, Element o2) {
					int flag=o1.getName().compareToIgnoreCase(o2.getName());
					if(flag==0){
						@SuppressWarnings("unchecked")
						List<Attribute> o1Attrs=o1.attributes();
						@SuppressWarnings("unchecked")
						List<Attribute> o2Attrs=o2.attributes();
						sortAttributes(o1Attrs);
						sortAttributes(o2Attrs);
						//sorted by first attribute's value if element are same
						if(o1Attrs.size()>0 && o2Attrs.size()>0){
							if(o1Attrs.size()==o2Attrs.size()){
								int flagT=0;
								for(int i=0;i<o1Attrs.size();i++){
									flagT=o1Attrs.get(i).getValue().compareToIgnoreCase(o2Attrs.get(i).getValue());
									if(flagT!=0){
										flag=flagT;
										break;
									}
								}
							}else{
								flag=o1Attrs.get(0).getValue().compareToIgnoreCase(o2Attrs.get(0).getValue());
							}
						}
					}
					return flag;
				}
			});
		}
		
		for(Element ele:list)
		{
			sortXmlElementByName(ele,eleNew,ignoreAttributes,ignoreElements);
		}
	}
	
	
	return ;
}

public static void sortAttributes(List<Attribute> attrs){
	if(attrs!=null && attrs.size()>0){
		Collections.sort(attrs, new Comparator<Attribute>(){
			public int compare(Attribute o1, Attribute o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
	}
}

}
