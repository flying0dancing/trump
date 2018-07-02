package com.lombardrisk.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * utility of Jsch, it can connects without prompting checking dialog, and download from linux server to local, and execute commands in linux server, and also can disconnect linux server after you connected.
 * @author kun shen
 * @since 2017.02.15
 *
 */
public class JschUtil {
	final static Logger logger=LoggerFactory.getLogger(JschUtil.class);
	private static JSch jsch;
	private static Session session;
	
	public static Boolean connect(String user, String passwd, String host, int port) throws JSchException
	{
		jsch= new JSch();
		session=jsch.getSession(user, host, port);
		session.setPassword(passwd);
		
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setTimeout(1500);
		session.connect();
		logger.debug("session's connected status:"+session.isConnected());
		return session.isConnected();
				
	}
	
	public static Boolean close()
	{
		session.disconnect();
		logger.debug("session's connected status:"+session.isConnected());
		return !session.isConnected();
	}
	
	public static int execCmd(String command) throws JSchException 
	 {
		 int retrn=1;//l:error, 0:success
		 Channel channel = null;
		 try
		 {
			 if (command != null)
			 {
				 channel=session.openChannel("exec");
				 ((ChannelExec) channel).setCommand(command);
				 channel.connect();
				 
				 channel.setInputStream(null);
				 ((ChannelExec) channel).setErrStream(System.err);
				 InputStream in=channel.getInputStream();
				 byte[] tmp=new byte[1024];
				 while(true)
				 {
					 while(in.available()>0)
					 {
						 int i=in.read(tmp,0,1024);
						 if(i<0)break;
						 logger.info(new String(tmp,0,i));
					 }
					 if(channel.isClosed())
					 {
						 if(in.available()>0) continue;
						 retrn=channel.getExitStatus();
						 logger.info("remote execute command exit-status: " + retrn);
						 break;
					 }
					 Thread.sleep(1000);
				 }
				 in.close();
			 }
		 }catch (IOException e)
		 {e.printStackTrace();}
		 catch (JSchException e)
		 { e.printStackTrace();} 
		 catch (InterruptedException e) 
		 {e.printStackTrace();}
		 finally
		 {
			 channel.disconnect();
		 }
		 return retrn;
	 }
	
	public static Boolean downloadFileToLocal(String src, String dst) throws JSchException, SftpException 
	 {
		Boolean flag=false;
		ChannelSftp channelSftp =null;
		try
		{
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			if(channelSftp.isConnected())
			{
				try
				 {
					channelSftp.get(src, dst);
					flag=true;
				 }catch(SftpException sftpe)
				 {
					 String errorMsg=sftpe.getMessage();
					 if(errorMsg.contains("No such file"))
					 {
						 logger.error("no such file");
					 }
				 }
			}
			channelSftp.quit();
		}catch (JSchException e)
		 { e.printStackTrace();} 
		 catch (Exception e) 
		 {e.printStackTrace();}
		finally
		{
			if(!channelSftp.isClosed())
			{
				channelSftp.disconnect();
			}
		}
		return flag;
	 }
	
	
	
}
