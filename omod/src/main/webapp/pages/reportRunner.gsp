<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.run.reports.app.label") ]) 
%>

<% if (patientId) { %>
	${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
<% } %>

<script type="text/javascript" src ='/${contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src ='/${contextPath}/dwr/util.js'></script>
<script type="text/javascript" src ='/${contextPath}/dwr/interface/DwrReportDesignerService.js'></script>

<script type="text/javascript" src ='/${contextPath}/moduleResources/xreports/reportrunner/ReportRunner.nocache.js'></script>

<% if (patientId) { %>
	<script type="text/javascript">
	    var breadcrumbs = [
	        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	        { label: "${ ui.format(patient.familyName) }, ${ ui.format(patient.givenName) }" , link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.patientId])}'},
	    	,{ label: "${formName}"}
	    ];
	</script>
<% } else { %>
	<script type="text/javascript">
	    var breadcrumbs = [
	        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
	        { label: "${ ui.message("xreports.app.label")}",
	          link: "${ui.pageLink("xreports", "dashboard")}"
	        }
	        
	    	,{ label: "${ ui.message("xreports.run.reports.app.label")}",
	    	   link: "${ui.pageLink("xreports", "runReports")}"
	    	 }
	    	 
	    	 <% crumbs.each { crumb -> %>
	    	 	,{ label: "${crumb.name}",
	        	   link: "${ui.pageLink("xreports", "runReports", [groupId: crumb.value])}"
	        	 }
	    	 <% } %>
	    	 
	    	 ,{ label: "${formName}"}
	    ];
	</script>
<% } %>

<style type="text/css">

	body {
		font-family: "OpenSans", Arial, sans-serif;
		-webkit-font-smoothing: subpixel-antialiased;
		max-width: 1000px;
		margin: 10px auto;
		background: #eeeeee;
		color: #363463;
		font-size: 16px;
	}
	
	#body-wrapper {
		padding: 0px;
	}
	
	table {
		width: auto;
	}
	
	table th, table td {
		padding: 0px 0px;
		border: none;
	}
	
	table tr {
		border: none;
	}
	
	.gwt-ListBox, .gwt-TextBox, gwt-CheckBox, gwt-RadioButton {
 		min-width: 80%;
		color: #363463;
		display: block;
		margin: 0;
		margin-top: 1%;
		background-color: #FFF;
		border: 1px solid #DDD;
		font-family: inherit;
		font-size: 100%;
	}
	
	.gwt-ListBox:focus, .gwt-TextBox:focus {
	    outline: none;
	    background: #fffdf7;
	}
	
	.purcforms-repeat-border td {
		padding: 2px 5px;
	}
	
	.purcforms-group-border {
		background: #F9F9F9;
	}
	
	.purcforms-horizontal-grid-line, .purcforms-vertical-grid-line {
		display: block;
		position: absolute;
	}
	
	.popupSearchForm {
		width: 500px;
		height: 390px;
		padding: 2px;
		background-color: whitesmoke;
		border: 1px solid gray;
		position: absolute;
		display: block;
		z-index: 10;
		margin: 5px;
		overflow-y: auto;
	}

	.smallButton {
		font-size: .7em;
		border: 0px solid lightgrey;
		cursor: pointer;
		width: 0px;
		height: 0px;
		margin: 0px;
	}

	.description {
		font-size: .9em;
		padding-left: 10px;
		color: gray;
	}

	.closeButton {
		border: 1px solid gray;
		background-color: lightpink;
		font-size: .6em;
		color: black;
		float: right;
		margin: 2px;
		padding: 1px;
		cursor: pointer;
	}
	
	#proposeConceptForm { display: none; }
	.alert { color: red; }
	
</style>

<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

<div id="purcreportsrunner"><div>

<div id="title" style="visibility:hidden;">${title}</div>

<div id="formId" style="visibility:hidden;">${formId}</div>
   
<div id="closeUrl" style="visibility:hidden;">${closeUrl}</div>

<div id="entityIdName" style="visibility:hidden;">templateId</div>
<div id="formIdName" style="visibility:hidden;">formId</div>

<div id="formDefDownloadUrlSuffix" style="visibility:hidden;">${reportDownloadServlet}</div>
<div id="exportPdfUrlSuffix" style="visibility:hidden;">${exportPdfServlet}</div>
<div id="fileOpenUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/fileOpenServlet</div>
<div id="fileSaveUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/fileSaveServlet</div>

<div id="defaultFontFamily" style="visibility:hidden;">${defaultFontFamily}</div>
<div id="defaultFontSize" style="visibility:hidden;">${defaultFontSize}</div>
<div id="defaultGroupBoxHeaderBgColor" style="visibility:hidden;">${defaultGroupBoxHeaderBgColor}</div>
    
<div id="formatXml" style="visibility:hidden;">${formatXml}</div>

<div id="imagePath" style="visibility:hidden;">images</div>

<div id="maximizeReport" style="visibility:hidden;">false</div>
<div id="showToolbar" style="visibility:hidden;">false</div>

<script language="javascript">
	
	var PurcformsText = {
		    file: "${ ui.message("xreports.file") }",
	    	view: "${ ui.message("xreports.view") }",
	    	item: "${ ui.message("xreports.item") }",
	    	tools: "${ ui.message("xreports.tools") }",
	    	help: "${ ui.message("xreports.help") }",
	    	newItem: "${ ui.message("xreports.newItem") }",
	    	open: "${ ui.message("xreports.open") }",
	    	save: "${ ui.message("xreports.save") }",
	    	saveAs: "${ ui.message("xreports.saveAs") }",
	
	    	openLayout: "${ ui.message("xreports.openLayout") }",
	    	saveLayout: "${ ui.message("xreports.saveLayout") }",
	    	openLanguageText: "${ ui.message("xreports.openLanguageText") }",
	    	saveLanguageText: "${ ui.message("xreports.saveLanguageText") }",
	    	close: "${ ui.message("xreports.close") }",
	
	    	refresh: "${ ui.message("xreports.refresh") }",
	    	addNew: "${ ui.message("xreports.addNew") }",
	    	addNewChild: "${ ui.message("xreports.addNewChild") }",
	    	deleteSelected: "${ ui.message("xreports.deleteSelected") }",
	    	moveUp: "${ ui.message("xreports.moveUp") }",
	    	moveDown: "${ ui.message("xreports.moveDown") }",
	    	cut: "${ ui.message("xreports.cut") }",
	    	copy: "${ ui.message("xreports.copy") }",
	    	paste: "${ ui.message("xreports.paste") }",
	    	
	    	format: "${ ui.message("xreports.format") }",
	    	languages: "${ ui.message("xreports.languages") }",
	    	options: "${ ui.message("xreports.options") }",
	
	    	helpContents: "${ ui.message("xreports.helpContents") }",
	    	about: "${ ui.message("xreports.about") }",
	
	    	forms: "${ ui.message("xreports.forms") }",
	    	widgetProperties: "${ ui.message("xreports.widgetProperties") }",
	    	properties: "${ ui.message("xreports.properties") }",
	    	xformsSource: "${ ui.message("xreports.xformsSource") }",
	    	designSurface: "${ ui.message("xreports.designSurface") }",
	    	layoutXml: "${ ui.message("xreports.layoutXml") }",
	    	languageXml: "${ ui.message("xreports.languageXml") }",
	    	preview: "${ ui.message("xreports.preview") }",
	    	modelXml: "${ ui.message("xreports.modelXml") }",
	
	    	text: "${ ui.message("xreports.text") }",
	    	helpText: "${ ui.message("xreports.helpText") }",
	    	type: "${ ui.message("xreports.type") }",
	    	binding: "${ ui.message("xreports.binding") }",
	    	visible: "${ ui.message("xreports.visible") }",
	    	enabled: "${ ui.message("xreports.enabled") }",
	    	locked: "${ ui.message("xreports.locked") }",
	    	required: "${ ui.message("xreports.required") }",
	    	defaultValue: "${ ui.message("xreports.defaultValue") }",
	    	descriptionTemplate: "${ ui.message("xreports.descriptionTemplate") }",
	
	    	language: "${ ui.message("xreports.language") }",
	    	skipLogic: "${ ui.message("xreports.skipLogic") }",
	    	validationLogic: "${ ui.message("xreports.validationLogic") }",
	    	dynamicLists: "${ ui.message("xreports.dynamicLists") }",
	
	    	valuesFor: "${ ui.message("xreports.valuesFor") }",
	    	whenAnswerFor: "${ ui.message("xreports.whenAnswerFor") }",
	    	isEqualTo: "${ ui.message("xreports.isEqualTo") }",
	    	forQuestion: "${ ui.message("xreports.forQuestion") }",
	    	enable: "${ ui.message("xreports.enable") }",
	    	disable: "${ ui.message("xreports.disable") }",
	    	show: "${ ui.message("xreports.show") }",
	    	hide: "${ ui.message("xreports.hide") }",
	    	makeRequired: "${ ui.message("xreports.makeRequired") }",
	
	    	when: "${ ui.message("xreports.when") }",
	    	ofTheFollowingApply: "${ ui.message("xreports.ofTheFollowingApply") }",
	    	all: "${ ui.message("xreports.all") }",
	    	any: "${ ui.message("xreports.any") }",
	    	none: "${ ui.message("xreports.none") }",
	    	notAll: "${ ui.message("xreports.notAll") }",
	
	    	addNewCondition: "${ ui.message("xreports.addNewCondition") }",
	
	    	isEqualTo: "${ ui.message("xreports.isEqualTo") }",
	    	isNotEqual: "${ ui.message("xreports.isNotEqual") }",
	    	isLessThan: "${ ui.message("xreports.isLessThan") }",
	    	isLessThanOrEqual: "${ ui.message("xreports.isLessThanOrEqual") }",
	    	isGreaterThan: "${ ui.message("xreports.isGreaterThan") }",
	    	isGreaterThanOrEqual: "${ ui.message("xreports.isGreaterThanOrEqual") }",
	    	isNull: "${ ui.message("xreports.isNull") }",
	    	isNotNull: "${ ui.message("xreports.isNotNull") }",
	    	isInList: "${ ui.message("xreports.isInList") }",
	    	isNotInList: "${ ui.message("xreports.isNotInList") }",
	    	startsWith: "${ ui.message("xreports.startsWith") }",
	    	doesNotStartWith: "${ ui.message("xreports.doesNotStartWith") }",
	    	endsWith: "${ ui.message("xreports.endsWith") }",
	    	doesNotEndWith: "${ ui.message("xreports.doesNotEndWith") }",
	    	contains: "${ ui.message("xreports.contains") }",
	    	doesNotContain: "${ ui.message("xreports.doesNotContain") }",
	    	isBetween: "${ ui.message("xreports.isBetween") }",
	    	isNotBetween: "${ ui.message("xreports.isNotBetween") }",
	
			isValidWhen: "${ ui.message("xreports.isValidWhen") }",
			errorMessage: "${ ui.message("xreports.errorMessage") }",
			question: "${ ui.message("xreports.question") }",
	
			addField: "${ ui.message("xreports.addField") }",
			submit: "${ ui.message("xreports.submit") }",
			addWidget: "${ ui.message("xreports.addWidget") }",
			newTab: "${ ui.message("xreports.newTab") }",
			deleteTab: "${ ui.message("xreports.deleteTab") }",
			selectAll: "${ ui.message("xreports.selectAll") }",
			load: "${ ui.message("xreports.load") }",
			
			label: "${ ui.message("xreports.label") }",
			textBox: "${ ui.message("xreports.textBox") }",
			checkBox: "${ ui.message("xreports.checkBox") }",
			radioButton: "${ ui.message("xreports.radioButton") }",
			dropdownList: "${ ui.message("xreports.dropdownList") }",
			textArea: "${ ui.message("xreports.textArea") }",
			button: "${ ui.message("xreports.button") }",
			datePicker: "${ ui.message("xreports.datePicker") }",
			groupBox: "${ ui.message("xreports.groupBox") }",
			repeatSection: "${ ui.message("xreports.repeatSection") }",
			picture: "${ ui.message("xreports.picture") }",
			videoAudio: "${ ui.message("xreports.videoAudio") }",
			listBox: "${ ui.message("xreports.listBox") }",
	
			deleteWidgetPrompt: "${ ui.message("xreports.deleteWidgetPrompt") }",
			deleteTreeItemPrompt: "${ ui.message("xreports.deleteTreeItemPrompt") }",
			selectDeleteItem: "${ ui.message("xreports.selectDeleteItem") }",
	
			selectedPage: "${ ui.message("xreports.selectedPage") }",
			shouldNotSharePageBinding: "${ ui.message("xreports.shouldNotSharePageBinding") }",
			selectedQuestion: "${ ui.message("xreports.selectedQuestion") }",
			shouldNotShareQuestionBinding: "${ ui.message("xreports.shouldNotShareQuestionBinding") }",
			selectedOption: "${ ui.message("xreports.selectedOption") }",
			shouldNotShareOptionBinding: "${ ui.message("xreports.shouldNotShareOptionBinding") }",
			newForm: "${ ui.message("xreports.newForm") }",
			page: "${ ui.message("xreports.page") }",
			option: "${ ui.message("xreports.option") }",
			noDataFound: "${ ui.message("xreports.noDataFound") }",
	
			formSaveSuccess: "${ ui.message("xreports.formSaveSuccess") }",
			selectSaveItem: "${ ui.message("xreports.selectSaveItem") }",
			deleteAllWidgetsFirst: "${ ui.message("xreports.deleteAllWidgetsFirst") }",
			deleteAllTabWidgetsFirst: "${ ui.message("xreports.deleteAllTabWidgetsFirst") }",
			cantDeleteAllTabs: "${ ui.message("xreports.cantDeleteAllTabs") }",
			noFormId: "${ ui.message("xreports.noFormId") }",
			divFound: "${ ui.message("xreports.noFormId") }",
			noFormLayout: "${ ui.message("xreports.noFormLayout") }",
			formSubmitSuccess: "${ ui.message("xreports.formSubmitSuccess") }",
			missingDataNode: "${ ui.message("xreports.missingDataNode") }",
	
			openingForm: "${ ui.message("xreports.openingForm") }",
			loadingTemplate: "${ ui.message("xreports.loadingTemplate") }",
			openingFormLayout: "${ ui.message("xreports.openingFormLayout") }",
			savingForm: "${ ui.message("xreports.savingForm") }",
			savingFormLayout: "${ ui.message("xreports.savingFormLayout") }",
			refreshingForm: "${ ui.message("xreports.refreshingForm") }",
			translatingFormLanguage: "${ ui.message("xreports.translatingFormLanguage") }",
			savingLanguageText: "${ ui.message("xreports.savingLanguageText") }",
			refreshingDesignSurface: "${ ui.message("xreports.refreshingDesignSurface") }",
			loadingDesignSurface: "${ ui.message("xreports.loadingDesignSurface") }",
			refreshingPreview: "${ ui.message("xreports.refreshingPreview") }",
	
			count: "${ ui.message("xreports.count") }",
			clickToPlay: "${ ui.message("xreports.clickToPlay") }",
			loadingPreview: "${ ui.message("xreports.loadingPreview") }",
			unexpectedFailure: "${ ui.message("xreports.unexpectedFailure") }",
			uncaughtException: "${ ui.message("xreports.uncaughtException") }",
			causedBy: "${ ui.message("xreports.causedBy") }",
			openFile: "${ ui.message("xreports.openFile") }",
			saveFileAs: "${ ui.message("xreports.saveFileAs") }",
	
			alignLeft: "${ ui.message("xreports.alignLeft") }",
			alignRight: "${ ui.message("xreports.alignRight") }",
			alignTop: "${ ui.message("xreports.alignTop") }",
			alignBottom: "${ ui.message("xreports.alignBottom") }",
			makeSameWidth: "${ ui.message("xreports.makeSameWidth") }",
			makeSameHeight: "${ ui.message("xreports.makeSameHeight") }",
			makeSameSize: "${ ui.message("xreports.makeSameSize") }",
			layout: "${ ui.message("xreports.layout") }",
			deleteTabPrompt: "${ ui.message("xreports.deleteTabPrompt") }",
	
			text: "${ ui.message("xreports.text") }",
		    toolTip: "${ ui.message("xreports.toolTip") }",
		    childBinding: "${ ui.message("xreports.childBinding") }",
		    width: "${ ui.message("xreports.width") }",
		    height: "${ ui.message("xreports.height") }",
		    left: "${ ui.message("xreports.left") }",
		    top: "${ ui.message("xreports.top") }",
		    tabIndex: "${ ui.message("xreports.tabIndex") }",
		    repeat: "${ ui.message("xreports.repeat") }",
		    externalSource: "${ ui.message("xreports.externalSource") }",
		    displayField: "${ ui.message("xreports.displayField") }",
		    valueField: "${ ui.message("xreports.valueField") }",
		    fontFamily: "${ ui.message("xreports.fontFamily") }",
		    foreColor: "${ ui.message("xreports.foreColor") }",
		    fontWeight: "${ ui.message("xreports.fontWeight") }",
		    fontStyle: "${ ui.message("xreports.fontStyle") }",
		    fontSize: "${ ui.message("xreports.fontSize") }",
		    textDecoration: "${ ui.message("xreports.textDecoration") }",
		    textAlign: "${ ui.message("xreports.textAlign") }",
		    backgroundColor: "${ ui.message("xreports.backgroundColor") }",
		    borderStyle: "${ ui.message("xreports.borderStyle") }",
		    borderWidth: "${ ui.message("xreports.borderWidth") }",
		    borderColor: "${ ui.message("xreports.borderColor") }",
		    aboutMessage: "${ ui.message("xreports.aboutMessage") }",
		    more: "${ ui.message("xreports.more") }",
		    requiredErrorMsg: "${ ui.message("xreports.requiredErrorMsg") }",
		    questionTextDesc: "${ ui.message("xreports.questionTextDesc") }",
		    questionDescDesc: "${ ui.message("xreports.questionDescDesc") }",
		    questionIdDesc: "${ ui.message("xreports.questionIdDesc") }",
		    defaultValDesc: "${ ui.message("xreports.defaultValDesc") }",
		    questionTypeDesc: "${ ui.message("xreports.questionTypeDesc") }",
		    qtnTypeText: "${ ui.message("xreports.qtnTypeText") }",
		    qtnTypeNumber: "${ ui.message("xreports.qtnTypeNumber") }",
		    qtnTypeDecimal: "${ ui.message("xreports.qtnTypeDecimal") }",
		    qtnTypeDate: "${ ui.message("xreports.qtnTypeDate") }",
		    qtnTypeTime: "${ ui.message("xreports.qtnTypeTime") }",
		    qtnTypeDateTime: "${ ui.message("xreports.qtnTypeDateTime") }",
		    qtnTypeBoolean: "${ ui.message("xreports.qtnTypeBoolean") }",
		    qtnTypeSingleSelect: "${ ui.message("xreports.qtnTypeSingleSelect") }",
		    qtnTypeMultSelect: "${ ui.message("xreports.qtnTypeMultSelect") }",
		    qtnTypeRepeat: "${ ui.message("xreports.qtnTypeRepeat") }",
		    qtnTypePicture: "${ ui.message("xreports.qtnTypePicture") }",
		    qtnTypeVideo: "${ ui.message("xreports.qtnTypeVideo") }",
		    qtnTypeAudio: "${ ui.message("xreports.qtnTypeAudio") }",
		    qtnTypeSingleSelectDynamic: "${ ui.message("xreports.qtnTypeSingleSelectDynamic") }",
		    deleteCondition: "${ ui.message("xreports.deleteCondition") }",
			addCondition: "${ ui.message("xreports.addCondition") }",
			value: "${ ui.message("xreports.value") }",
			questionValue: "${ ui.message("xreports.questionValue") }",
			and: "${ ui.message("xreports.and") }",
	   		deleteItemPrompt: "${ ui.message("xreports.deleteItemPrompt") }",
			changeWidgetTypePrompt: "${ ui.message("xreports.changeWidgetTypePrompt") }",
			removeRowPrompt: "${ ui.message("xreports.removeRowPrompt") }",
			remove: "${ ui.message("xreports.remove") }",
			browse: "${ ui.message("xreports.browse") }",
			clear: "${ ui.message("xreports.clear") }",
			deleteItem: "${ ui.message("xreports.deleteItem") }",
			cancel: "${ ui.message("xreports.cancel") }",
			clickToAddNewCondition: "${ ui.message("xreports.clickToAddNewCondition") }",
			qtnTypeGPS: "${ ui.message("xreports.qtnTypeGPS") }",
			qtnTypeBarcode: "${ ui.message("xreports.qtnTypeBarcode") }",
			qtnTypeGroup: "${ ui.message("xreports.qtnTypeGroup") }",
			palette: "${ ui.message("xreports.palette") }",
			saveAsXhtml: "${ ui.message("xreports.saveAsXhtml") }",
			groupWidgets: "${ ui.message("xreports.groupWidgets") }",
			action: "${ ui.message("xreports.action") }",
			submitting: "${ ui.message("xreports.submitting") }",
			authenticationPrompt: "${ ui.message("xreports.authenticationPrompt") }",
			invalidUser: "${ ui.message("xreports.invalidUser") }",
			login: "${ ui.message("xreports.login") }",
			userName: "${ ui.message("xreports.userName") }",
			password: "${ ui.message("xreports.password") }",
			noSelection: "${ ui.message("xreports.noSelection") }",
			cancelFormPrompt: "${ ui.message("xreports.cancelFormPrompt") }",
			print: "${ ui.message("xreports.print") }",
			pageSetup: "${ ui.message("xreports.pageSetup") }",
			yes: "${ ui.message("xreports.yes") }",
			no: "${ ui.message("xreports.no") }",
	   		searchServer: "${ ui.message("xreports.searchServer") }",
	   		recording: "${ ui.message("xreports.recording") }",
	   		search: "${ ui.message("xreports.search") }",
	   		processingMsg: "${ ui.message("xreports.processingMsg") }",
	   		length: "${ ui.message("xreports.length") }",
	   		clickForOtherQuestions: "${ ui.message("xreports.clickForOtherQuestions") }",
	   		ok: "${ ui.message("xreports.ok") }",
	   		undo: "${ ui.message("xreports.undo") }",
	   		redo: "${ ui.message("xreports.redo") }",
	   		loading: "${ ui.message("xreports.loading") }",
	   		allQuestions: "${ ui.message("xreports.allQuestions") }",
	   		selectedQuestions: "${ ui.message("xreports.selectedQuestions") }",
	   		otherQuestions: "${ ui.message("xreports.otherQuestions") }",
	   		wrongFormat: "${ ui.message("xreports.wrongFormat") }",
	   		timeWidget: "${ ui.message("xreports.timeWidget") }",
			dateTimeWidget: "${ ui.message("xreports.dateTimeWidget") }",
			lockWidgets: "${ ui.message("xreports.lockWidgets") }",
			unLockWidgets: "${ ui.message("xreports.unLockWidgets") }",
			changeWidgetH: "${ ui.message("xreports.changeWidgetH") }",
			changeWidgetV: "${ ui.message("xreports.changeWidgetV") }",
			changeToTextBoxWidget: "${ ui.message("xreports.changeToTextBoxWidget") }",
			saveAsPurcForm: "${ ui.message("xreports.saveAsPurcForm") }",
			localeChangePrompt: "${ ui.message("xreports.localeChangePrompt") }",
			javaScriptSource: "${ ui.message("xreports.javaScriptSource") }",
	   		calculation: "${ ui.message("xreports.calculation") }",
	   		id: "${ ui.message("xreports.id") }",
	   		formKey: "${ ui.message("xreports.formKey") }",
	   		logo: "${ ui.message("xreports.logo") }",
	   		filterField: "${ ui.message("xreports.filterField") }",
	   		table: "${ ui.message("xreports.table") }",
	  		horizontalLine: "${ ui.message("xreports.horizontalLine") }",
	   		verticalLine: "${ ui.message("xreports.verticalLine") }",
	   		addRowsBelow: "${ ui.message("xreports.addRowsBelow") }",
	   		addRowsAbove: "${ ui.message("xreports.addRowsAbove") }",
	   		addColumnsRight: "${ ui.message("xreports.addColumnsRight") }",
	   		addColumnsLeft: "${ ui.message("xreports.addColumnsLeft") }",
	   		numberOfRowsPrompt: "${ ui.message("xreports.numberOfRowsPrompt") }",
	   		numberOfColumnsPrompt: "${ ui.message("xreports.numberOfColumnsPrompt") }",
	   		deleteColumn: "${ ui.message("xreports.deleteColumn") }",
	   		deleteRow: "${ ui.message("xreports.deleteRow") }",
	   		repeatChildDataNodeNotFound: "${ ui.message("xreports.repeatChildDataNodeNotFound") }",
	   		selectedFormField: "${ ui.message("xreports.selectedFormField") }",
	   		edit: "${ ui.message("xreports.edit") }",
	   		find: "${ ui.message("xreports.find") }",
	   		css: "${ ui.message("xreports.css") }",
	   		bold: "${ ui.message("xreports.bold") }",
	   		italic: "${ ui.message("xreports.italic") }",
	   		underline: "${ ui.message("xreports.underline") }",
	   		mergeCells: "${ ui.message("xreports.mergeCells") }",
	   		deleteFormPrompt: "${ ui.message("xreports.deleteFormPrompt") }",
       		formDeleteSuccess: "${ ui.message("xreports.formDeleteSuccess") }",
       		exclusiveOption: "${ ui.message("xreports.exclusiveOption") }",
       		otherProperties: "${ ui.message("xreports.otherProperties") }",
       		exclusiveQuestion: "${ ui.message("xreports.exclusiveQuestion") }",
       		rotate: "${ ui.message("xreports.rotate") }",
       		report: "${ ui.message("xreports.report") }",
       		exportPdf: "${ ui.message("xreports.exportPdf") }",		
       		email: "${ ui.message("xreports.email") }",
       		send: "${ ui.message("xreports.send") }",
       		sendTo: "${ ui.message("xreports.sendTo") }",
       		subject: "${ ui.message("xreports.subject") }",
       		message: "${ ui.message("xreports.message") }",
       		report: "${ ui.message("xreports.report") }"
	};

	function isUserAuthenticated(){
		DwrReportDesignerService.isAuthenticated(checkIfLoggedInCallback);
	}

	function authenticateUser(username, password){
		DwrReportDesignerService.authenticate(username,password,checkIfLoggedInCallback);
	}

	function checkIfLoggedInCallback(isLoggedIn) {
		authenticationCallback(isLoggedIn);
	}

	function initialize(){
		
	}
	
	function report() {
		
	}
	
	function onReportLoaded(xml) {
		loadReport(xml);
	}
	
	function clearReportCard() {
		loadReport(null);
	}
	
	function getEntityId() {
		return 1;
	}
	
</script>
