package com.lombardrisk.test.pojo;

import com.lombardrisk.commons.EssentialOperation;
import com.lombardrisk.test.DBInfo;

public class ServerInfo {
	private String host;
	private int port;
	private String downloadPath;
	private String user;
	private String password;
	
	public ServerInfo()
	{
		setServerInfo();
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
	
	
	public void setServerInfo()
	{
		String serverInfoStr=DBInfo.getApplicationServer_Key(); //like this sha-com-qa-3:22:/home/test/export:test:password
		String[] serverInfoArr=serverInfoStr.split(":");
		if(serverInfoArr.length>=5)
		{
			setHost(serverInfoArr[0]);
			setPort(Integer.parseInt(serverInfoArr[1]));
			String downloadPath=EssentialOperation.removeFileSeparator(serverInfoArr[2]);
			setDownloadPath(downloadPath);
			setUser(serverInfoArr[3]);
			setPassword(serverInfoArr[4]);
		}
		
	}
	
}
