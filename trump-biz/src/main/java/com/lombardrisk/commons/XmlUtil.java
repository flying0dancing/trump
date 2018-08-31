package com.lombardrisk.commons;
import java.lang.reflect.Field;  
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.dom4j.Element;  

import com.lombardrisk.test.pojo.Transmission;

public class XmlUtil {

    /** 
     * json 数据转换对象 
     *  
     * @param Element 
     *            要转换的Element数据 
     * @param pojo 
     *            要转换的目标对象类型 
     * @return 转换的目标对象 
     * @throws Exception 
     *             转换失败 
     */  
    @SuppressWarnings("rawtypes")  
    public static Object fromXmlToBean(Element rootElt, Class pojo) throws Exception  
    {  
        // 首先得到pojo所定义的字段  
        Field[] fields = pojo.getDeclaredFields();  
        // 根据传入的Class动态生成pojo对象  
        Object obj = pojo.newInstance();  
        
        for (Field field : fields)  
        {  
            // 设置字段可访问（必须，否则报错）  
            field.setAccessible(true);  
            // 得到字段的属性名  
            String name = field.getName();  
            // 这一段的作用是如果字段在Element中不存在会抛出异常，如果出异常，则跳过。
            String rootEltText=null;
            try  
            {  
            	if (field.getType().equals(Transmission.class))  
                {  
            		field.set(obj,fromXmlToBean(rootElt.element(name),Transmission.class));
            		continue;
                }
            	if(rootElt.element(name) != null)
            	{
                	rootEltText=rootElt.elementTextTrim(name);
            	}else if(rootElt.attribute(name)!=null)
            	{
            		rootEltText=rootElt.attributeValue(name).trim();
            	}
            }  
            catch (Exception ex)  
            {  
            	//add log info for checking xml file's tag.
            	continue;  
            }  
            if (rootEltText != null && !rootEltText.equals(""))  
            {  
                // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。  
                if (field.getType().equals(String.class))  
                {  
                	field.set(obj, rootEltText);   
                }
                else if (field.getType().equals(Long.class) || field.getType().equals(long.class))  
                {  
                    field.set(obj, Long.parseLong(rootEltText));
                }  
                else if (field.getType().equals(Double.class) || field.getType().equals(double.class))  
                {  
                    field.set(obj, Double.parseDouble(rootEltText));  
                }  
                else if (field.getType().equals(Integer.class) || field.getType().equals(int.class))  
                {  
                    field.set(obj, Integer.parseInt(rootEltText));  
                }
                else  
                {  
                    continue;  
                }  
            }  
        }  
        return obj;  
    }  
    
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    public static void fromBeanToElement(Element element,Object obj) throws Exception  
    {  
    	Class pojo=obj.getClass();
        // 首先得到pojo所定义的字段  
        Field[] fields = pojo.getDeclaredFields();  

        for (Field field : fields)  
        {  
            // 设置字段可访问（必须，否则报错）  
            field.setAccessible(true);  
            // 得到字段的属性名  
            String name = field.getName(); 
            Object valueObj=field.get(obj);
            if(field.getType().equals(Transmission.class))
            {
            	fromBeanToElement(element.addElement(name),valueObj);
            	continue;
            }
            String value="";
            if(valueObj!=null){value=valueObj.toString();}
			Method method=pojo.getMethod("getAttributeList");
            ArrayList<String> attributes=(ArrayList<String>) method.invoke(obj);
            if(attributes.contains(name)){element.addAttribute(name, value);}
            else{element.addElement(name).setText(value);}
        }    
    } 
    
    
}
