package com.lombardrisk.pages;

public interface IReturnDialog {
	public Boolean isThisPage() throws Exception;
	public void closeThisPage() throws Exception;
	public Boolean selectInfo() throws Exception;
	public FormInstancePage clickOK() throws Exception;
	
}
