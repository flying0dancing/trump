package com.lombardrisk.pages;

import com.lombardrisk.test.pojo.Form;

public interface IExportTo {
	ExportToRegulatorDialog exportToRegulator(Form form) throws Exception;
}
