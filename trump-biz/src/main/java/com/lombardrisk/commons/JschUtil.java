package com.lombardrisk.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

	private static JSch jsch;
	private static Session session;
	
	public static void connect(String user, String passwd, String host, int port) throws JSchException
	{
		jsch= new JSch();
		session=jsch.getSession(user, host, port);
		session.setPassword(passwd);
		
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setTimeout(1500);
		session.connect();
				
	}
	
	public static void close()
	{
		session.disconnect();
	}
	
	public static void execCmd(String command) throws JSchException 
	 {
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
						 System.out.println(new String(tmp,0,i));
					 }
					 if(channel.isClosed())
					 {
						 if(in.available()>0) continue;
						 System.out.println("exit-status: " + channel.getExitStatus());
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
	
	 }
	
	public static void downloadFileToLocal(String src, String dst) throws JSchException, SftpException 
	 {
		ChannelSftp channelSftp =null;
		try
		{
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			channelSftp.get(src, dst);
			channelSftp.quit();
		}catch (SftpException e)
		 {e.printStackTrace();}
		 catch (JSchException e)
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
	 }
	
	
	
}
