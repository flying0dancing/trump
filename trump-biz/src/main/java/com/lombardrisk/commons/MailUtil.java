package com.lombardrisk.commons;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.PropHelper;


public class MailUtil {
	private final static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	
	public static Boolean sendARResultMail(String resultPath, String suitePath,long totalPass,long totalFail,long totalSkip,float totalTime,Boolean rerunFlag)
	{
		String content="Tests run: "+(totalPass+totalFail+totalSkip)+", Pass: "+totalPass+", Failures: "+totalFail+", Skipped:"+totalSkip+", Time elapsed:"+totalTime+"min";
		MailInfo mail=new MailInfo();
        mail.setHost(StringUtils.isNotBlank(PropHelper.getProperty("mail.hostname"))?PropHelper.getProperty("mail.hostname"):"172.20.20.143");
        mail.setPort(StringUtils.isNotBlank(PropHelper.getProperty("mail.port"))?PropHelper.getProperty("mail.port"):"25");
        mail.setSenderName(PropHelper.getProperty("mail.senderName"));
        mail.setSenderAddress(PropHelper.getProperty("mail.senderAddress"));
        mail.setSenderPassword(PropHelper.getProperty("mail.senderPassword"));
        String[] toList=null;
        Map<String,String> toMap=null;
        
        if(StringUtils.isNotBlank(PropHelper.getProperty("mail.receiver.toList")))
        {
        	toList=PropHelper.getProperty("mail.receiver.toList").trim().split(";");
            toMap=new HashMap<String,String>();
            for(String to:toList)
            {
            	if(StringUtils.isNotBlank(to))
            	{
            		toMap.put(to,null);
            	}
            }
            mail.setTo(toMap);
        }
        
        if(StringUtils.isNotBlank(PropHelper.getProperty("mail.receiver.ccList")))
        {
        	toList=PropHelper.getProperty("mail.receiver.ccList").trim().split(";");
        	toMap=new HashMap<String,String>();
            for(String to:toList)
            {
            	if(StringUtils.isNotBlank(to))
            	{
            		toMap.put(to,null);
            	}
            }
            mail.setCc(toMap);
        }
        
        if(StringUtils.isNotBlank(PropHelper.getProperty("mail.receiver.bccList")))
        {
        	toList=PropHelper.getProperty("mail.receiver.bccList").trim().split(";");
        	toMap=new HashMap<String,String>();
            for(String to:toList)
            {
            	if(StringUtils.isNotBlank(to))
            	{
            		toMap.put(to,null);
            	}
            }
            mail.setBcc(toMap);
        }
        String subject=null;
        if(StringUtils.isNotBlank(PropHelper.getProperty("mail.subject.prefix")))
        {
        	subject=PropHelper.getProperty("mail.subject.prefix");
        	if(resultPath!=null)
        	{
        		int i=StringUtils.lastIndexOfAny(resultPath, "/","\\");
        		if(i!=-1)
        		{
        			subject=PropHelper.getProperty("mail.subject.prefix")+resultPath.substring(i+1);
        		}
        	}
        }else
        {
        	subject="trump result";
        }
        if(rerunFlag)
		{
        	subject+="(rerun)";
        	content="<b>(rerun)</b>"+content;
		}
		
        mail.setSubject(subject);
       // String resultPath="H:\\ProductLine\\TrumpTest\\AR1.15.2+FED+Smoke+Parallel20170411\\scenarios-result";
        String html = "<!DOCTYPE html>";
        html += "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        html += "<title>trump automatic email</title>";
        html+="<script>  "+
            "//绘制饼图  "+
            "function drawCircle(canvasId, data_arr, color_arr, text_arr)  "+
            "{  "+
                "var c = document.getElementById(canvasId);"+  
                "var ctx = c.getContext(\"2d\");  "+
                "ctx.fillStyle='#F8F8FF';"+
                "ctx.fillRect(0,0,500,300);"+
                "var radius = c.height / 2 - 20; //半径  "+
                "var ox = radius + 20, oy = radius + 20; //圆心  "+
                "var width = 30, height = 10; //图例宽和高  "+
                "var posX = ox * 2 + 20, posY = 30;   // "+ 
                "var textX = posX + width + 5, textY = posY + 10;  "+
                "var totalData=0"+
                "for(var i=0;i<data_arr.length;i++)"+
               " { totalData+=data_arr[i];}"+
                "var startAngle = 0; //起始弧度  "+
                "var endAngle = 0;   //结束弧度 " +
               " for (var i = 0; i < data_arr.length; i++) " +
                "{ "+
                  "  //绘制饼图  "+
                   " endAngle = endAngle + data_arr[i]/totalData * Math.PI * 2; //结束弧度  "+
                   " ctx.fillStyle = color_arr[i];  "+
                   " ctx.beginPath();  "+
                   " ctx.moveTo(ox, oy); //移动到到圆心  "+
                   " ctx.arc(ox, oy, radius, startAngle, endAngle, false);  "+
                   " ctx.closePath();  "+
                   " ctx.fill();  "+
                   " startAngle = endAngle; //设置起始弧度  "+
                    "//绘制比例图及文字  "+
                    "ctx.fillStyle = color_arr[i];  "+
                   " ctx.fillRect(posX, posY + 20 * i, width, height);  "+
                   " ctx.moveTo(posX, posY + 20 * i);  "+
                   " ctx.font = 'bold 12px 微软雅黑';    //斜体 30像素 微软雅黑字体  "+
                   " ctx.fillStyle = color_arr[i];  "+
                   " var percent = text_arr[i] + \":\" + (data_arr[i]/totalData*100).toFixed(2) + \"%\";"  +
                   " ctx.fillText(percent, textX, textY + 20 * i);  "+
                "}  "+
           " }"+  
            "function init() {  //绘制饼图  //比例数据和颜色  "+
                "var data_arr = ["+totalPass+", "+totalFail+", "+totalSkip+"];  "+
                "var color_arr = [\"#88ee88\", \"#ff8888\", \"#FFD700\"];"+  
                "var text_arr = [\"Pass\", \"Fail\", \"Skip\"];  "+
               " drawCircle(\"canvas_circle\", data_arr, color_arr, text_arr); " +
           " }  "+
            "window.onload = init;  //页面加载时执行init()函数  "+
       " </script>";
        html += "</head><body>";
        //html += "<div style=\"width:600px;height:400px;margin:50px auto;\">";
        html += "hi,<br/><br/>";
        html += "<p>trump had finished running test cases, and generated results and reports for you.</p>";
        html += "<p>"+content+"</p>";
        html += "suite file location: <a href=\""+suitePath+"\">"+suitePath+"</a><br/>";
        html += "result folder location: <a href=\""+resultPath+"\">"+resultPath+"</a><br/>";
        html +="<p><canvas id=\"canvas_circle\" width=\"500\" height=\"300\" style=\"border:2px solid #0026ff;\" ></canvas>  </p>";
        html += "<br/><br/>Thanks.</div>";
        html += "</body></html>";

        mail.setHtmlMessage(html);
        mail.setTextMessage(StringUtils.isNotBlank(PropHelper.getProperty("mail.textMsg"))?PropHelper.getProperty("mail.textMsg"):"hi,\n\n  trump had finished running test cases.\n\nThanks.");
        new MailUtil().sendHtmlMail(mail);
		return true;
	}
	
	public Boolean sendHtmlMail(MailInfo mailInfo)
	{
		HtmlEmail email=new HtmlEmail();
		try
		{
			email.setHostName(mailInfo.getHost());
			email.setSmtpPort(Integer.parseInt(mailInfo.getPort()));
			email.setCharset(MailInfo.ENCODEING);
			if(StringUtils.isNotBlank(mailInfo.getSenderName()))
			{
				email.setFrom(mailInfo.getSenderAddress(), mailInfo.getSenderName());
				email.setAuthenticator(new DefaultAuthenticator(mailInfo.getSenderName(),mailInfo.getSenderPassword()));
			}else
			{
				email.setFrom(mailInfo.getSenderAddress());
				String userName=mailInfo.getSenderAddress().split("@")[0].replace(".", " ");
				email.setAuthenticator(new DefaultAuthenticator(userName,mailInfo.getSenderPassword()));
			}
			setTo(email,mailInfo);
			setCc(email,mailInfo);
			setBcc(email,mailInfo);
			email.setSubject(mailInfo.getSubject());
			if(StringUtils.isNotBlank(mailInfo.getHtmlMessage()))
			{
				email.setHtmlMsg(mailInfo.getHtmlMessage());
			}else
			{
				if(StringUtils.isNotBlank(mailInfo.getTextMessage()))
				{
					email.setTextMsg(mailInfo.getTextMessage());
				}
			}
			
			email.send();
			logger.info("email is already sent.");
			return true;
		}catch(Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private void setTo(HtmlEmail htmlEmail, MailInfo mail) throws EmailException
	{
		StringBuilder info=new StringBuilder("send to ");
		if(StringUtils.isNotBlank(mail.getReceiverAddress()))
		{
			logger.info(info.toString()+mail.getReceiverAddress());
			if(StringUtils.isNotBlank(mail.getReceiverName()))
			{
				htmlEmail.addTo(mail.getReceiverAddress(), mail.getReceiverName());
			}else
			{
				htmlEmail.addTo(mail.getReceiverAddress());
			}
		}
		if(mail.getTo()!=null)
		{
			
			for(Map.Entry<String,String> entry:mail.getTo().entrySet())
			{
				info.append(entry.getKey()+";");
				if(StringUtils.isNotBlank(entry.getValue()))
				{
					htmlEmail.addTo(entry.getKey(), entry.getValue());
				}else
				{
					htmlEmail.addTo(entry.getKey());
				}
			}
			
			logger.info(info.toString());
		}
	}
	
	private void setCc(HtmlEmail htmlEmail, MailInfo mail) throws EmailException
	{
		StringBuilder info=new StringBuilder("cc to ");
		if(mail.getCc()!=null)
		{
			for(Map.Entry<String,String> entry:mail.getCc().entrySet())
			{
				info.append(entry.getKey()+";");
				if(StringUtils.isNotBlank(entry.getValue()))
				{
					htmlEmail.addCc(entry.getKey(), entry.getValue());
				}else
				{
					htmlEmail.addCc(entry.getKey());
				}
			}
			logger.info(info.toString());
		}
	}
	
	private void setBcc(HtmlEmail htmlEmail, MailInfo mail) throws EmailException
	{
		StringBuilder info=new StringBuilder("bcc to ");
		if(mail.getBcc()!=null)
		{
			for(Map.Entry<String,String> entry:mail.getBcc().entrySet())
			{
				info.append(entry.getKey()+";");
				if(StringUtils.isNotBlank(entry.getValue()))
				{
					htmlEmail.addBcc(entry.getKey(), entry.getValue());
				}else
				{
					htmlEmail.addBcc(entry.getKey());
				}
			}
			logger.info(info.toString());
		}
	}
}
