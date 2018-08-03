usage 
===
###This project is used for lombardrisk compliance product team. Automated testing returns/forms and generate result, report and email. 

----
	check the latest test data and scenarios are all imported into common data pool.
	cd to trump-sel folder
	check test.json and test.properties under /target/test-classes with correct settings
	run RunTest.bat
		mvn test -DxmlFileName="Z:\ABC\Smoke_foo.xml" -DresultFolder="Z:\ABC\foo" -DupdateSource -Drerun="error"
		
* [xmlFileName]
	* the full path with name of xml file(syntax following testNG schema, which paremeters following webcore and trump's definition)
	* paremeters 
		* `browser`, trump use `chrome` as default browser.
		* `indexDBServers` set the ID of `databaseServers` from test.json.
		* `indexAppServers` set the ID of `applicationServers` from test.json.
		* `scenarioName` set value to scenario file's name.
		* `scenarioSheet` set value to `scenarioName`'s sheet name.
* [resultFolder]
	* the full path with name of result folder(exist or not). suggest every product following same folder naming rule.
* [rerun]
	* –Drerun="error" will rerun test cases which executionStatus starts with "error".
	* –Drerun="fail" will rerun test cases which executionStatus start with "fail".
	* –Drerun="all" will rerun test cases which executionStatus `NOT` starts with "pass".
* [updateSource]
	* If the result folder exists, and using `updateSouce` flag, it will only update/add new source, won’t delete results and download files.
	* If the result folder exists, and `NOT` using `updateSource` flag, it will use existed sources, won't update/add new source if any.
	* If the result folder doesn't exist, and use `updateSource` flag or not, it will executes the same effect.

-------

