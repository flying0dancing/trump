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

Automation Supported Functionality
-----
###some functions are single ones, some are workflows contains many functions.

* create new return
* create new return from excel, you can select applyScale for imported excel.
* import test data in opening form, you can choose importMode to existed form.
* check UI display, trump will open the form, and then download all cells's display value of all pages into a csv file, after that, compare the csv file with the expectation and result into UIDisplay folder .
* check Export To CSV
* check Export To Excel/ Export To Excel(applyScale) 
* check Export To PDF
* ExportToRegulator
	* checkInForm
	* checkInFormWithLockVal
	* checkInDashBoard
	because the download file and expectation file might be contains disordered issue, trump use <regulator>.transmissionProperties.csv to solve this issue,  like return FFIEC002, fileType is "txt", should be splits by  "\(|\)|," and then ignore some items at the first position. another example, like return "FFIEC031", fileType is "xml", should ignore xml attribute "contextRef" and xml element "schemaRef".

* check Validation Rules, if all validation rules's status are "pass", you can provide an empty file under expectation folder. You also can provide an expectation only with not "pass" status. If actual result contains "fail"/"error" status beyond its expectation, trump will log them for you.
* retrieveForm:
	* retrieve form
	* retrieve multiple forms(group)
* DataScheduleWorkflow: click "Ready for approval" and "Approval"
* createImportCheckValue: do three things, 1. create new return/create new from excel, 2. import data, 3. compare some cells's value
* createImportPrecision: similiar with createImportCheckValue, and one more, is do check UI display as its last step. this workflow also can do the retrieve Form, and then check UI display.
