package com.lombardrisk.test.pojo;

import org.apache.commons.lang3.StringUtils;

import com.lombardrisk.commons.EssentialOperation;


public class ServerInfo{
	private String host;
	private int port;
	private String downloadPath;
	private String user;
	private String password;
	
	public ServerInfo(String key)
	{
		setServerInfo(key);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public void setServerInfo(String key)
	{
		String serverInfoStr=key; //like this sha-com-qa-3:22:/home/test/export:test:password
		if(serverInfoStr!=null)
		{
			String[] serverInfoArr=serverInfoStr.split(":");
			if(serverInfoArr.length>=3)
			{
				setHost(serverInfoArr[0]);
				setPort(StringUtils.isBlank(serverInfoArr[1])?-1:Integer.parseInt(serverInfoArr[1]));
				String downloadPath=EssentialOperation.removeFileSeparator(serverInfoArr[2]);
				setDownloadPath(downloadPath);
				if(serverInfoArr.length>=5)
				{
					setUser(StringUtils.isBlank(serverInfoArr[3])?"":serverInfoArr[3]);
					setPassword(StringUtils.isBlank(serverInfoArr[4])?"":serverInfoArr[4]);
				}
				
			}
		}
		
	}
	
}
