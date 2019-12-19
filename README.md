usage 
===
###This project is used for lombardrisk compliance product team. Automated testing returns/forms and generate result, report and email. 

----
	check the latest test data and scenarios are all imported into common data pool.
	cd to trump-sel folder
	check test.json and test.properties under /target/test-classes with correct settings
	run RunTest.bat
		mvn test -DsrcFolder="Z:\APAutomation\sources" -DxmlFileName="Z:\ABC\Smoke_foo.xml" -DresultFolder="Z:\ABC\foo" -DupdateSource -Drerun="error"
		
* [xmlFileName]
	* the full path with name of xml file(syntax following testNG schema, which paremeters following webcore and trump's definition)
	* paremeters 
		* `browser`, trump use `chrome` as default browser.
		* `indexDBServers` set the ID of `databaseServers` from test.json.
		* `indexAppServers` set the ID of `applicationServers` from test.json.
		* `scenarioName` set value to scenario file's name.
		* `scenarioSheet` set value to `scenarioName`'s sheet name.
		* `selectLanguage` set value to zh_CN or en_GB or en_US, higher priority than `Regional.language` in test.properties.
* [resultFolder]
	* the full path with name of result folder(exist or not). suggest every product following same folder naming rule.
	* if the resultFolder exist and not use return, trump will be terminated
* [rerun]
	* –Drerun="error" will rerun test cases which executionStatus starts with "error".
	* –Drerun="fail" will rerun test cases which executionStatus start with "fail".
	* –Drerun="all" will rerun test cases which executionStatus `NOT` starts with "pass".
* [updateSource]
	* If the result folder exists, and using `updateSouce` flag, it will only update/add new source, won’t delete results and download files.
	* If the result folder exists, and `NOT` using `updateSource` flag, it will use existed sources, won't update/add new source if any.
	* If the result folder doesn't exist, and use `updateSource` flag or not, it will executes the same effect.
* [srcFolder]
    * resource folder path.
    * if not provided it, use default value of test.source in test.properties.
-------
change log 
===
trump 1.2.1 support agile reporter version 1.16.1+
trump 1.2.0 support agile reporter version 1.16.1 - 19.3
trump 1.1.0 support agile reporter version 1.15.3 - 1.16.0
trump 1.0.0 support agile reporter version 1.15.0 - 1.15.3