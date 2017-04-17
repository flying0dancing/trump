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
	
	public static Boolean sendARResultMail(String resultPath, String suitePath, String result)
	{
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
        			subject=PropHelper.getProperty("mail.subject.prefix")+": "+resultPath.substring(i+1);
        		}
        	}
        }else
        {
        	subject="trump result";
        }
        mail.setSubject(subject);
       // String resultPath="H:\\ProductLine\\TrumpTest\\AR1.15.2+FED+Smoke+Parallel20170411\\scenarios-result";
        String html = "<!DOCTYPE html>";
        html += "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
        html += "<title>trump automatic email</title>";
        html += "</head><body>";
        //html += "<div style=\"width:600px;height:400px;margin:50px auto;\">";
        html += "hi,<br/><br/>";
        html += "<p>trump had finished running test cases, and generated results and reports for you.</p>";
        html += "<p>"+result+"</p>";
        html += "suite file location: <a href=\""+suitePath+"\">"+suitePath+"</a><br/>";
        html += "result folder location: <a href=\""+resultPath+"\">"+resultPath+"</a><br/><br/>Thanks.";
        html += "</div>";
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
					htmlEmail.addTo(entry.getKey(), entry.getValue());
				}else
				{
					htmlEmail.addTo(entry.getKey());
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
					htmlEmail.addTo(entry.getKey(), entry.getValue());
				}else
				{
					htmlEmail.addTo(entry.getKey());
				}
			}
			logger.info(info.toString());
		}
	}
}
