package com.lombardrisk.commons;

import java.util.Map;

public class MailInfo {
	public static final String ENCODEING="UTF-8";
	private String host;
	private String port;
	private String senderAddress;
	private String senderName;
	private String senderPassword;
	private String receiverAddress;
	private String receiverName;
	private Map<String,String> to;
	private Map<String,String> cc;
	private Map<String,String> bcc;
	
	private String subject;
	private String htmlMessage;
	private String textMessage;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getSenderPassword() {
		return senderPassword;
	}
	public void setSenderPassword(String senderPassword) {
		this.senderPassword = senderPassword;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public Map<String,String> getTo() {
		return to;
	}
	public void setTo(Map<String,String> to) {
		this.to = to;
	}
	public Map<String,String> getCc() {
		return cc;
	}
	public void setCc(Map<String,String> cc) {
		this.cc = cc;
	}
	public Map<String,String> getBcc() {
		return bcc;
	}
	public void setBcc(Map<String,String> bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlMessage() {
		return htmlMessage;
	}
	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}
	public String getTextMessage() {
		return textMessage;
	}
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
	
}
