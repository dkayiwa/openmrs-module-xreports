<%@ include file="/WEB-INF/template/include.jsp"%>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DwrReportDesignerService.js'></script>

<openmrs:htmlInclude file="/openmrs.js" />
<openmrs:htmlInclude file="/moduleResources/xreports/reportrunner/ReportRunner.nocache.js"/>

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-addon.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-datepicker-i18n.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-i18n.js" />

<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/green/jquery-ui.custom.css" type="text/css" rel="stylesheet" />
<link rel="shortcut icon" type="image/ico" href="/images/openmrs-favicon.ico">
<link rel="icon" type="image/png" href="<openmrs:contextPath/>/images/openmrs-favicon.png">
		
<script type="text/javascript">
	<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
		var $j = jQuery.noConflict();
	</c:if>
	
	var jsDateFormat = '<openmrs:datePattern localize="false"/>';
	var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';
</script>
			
<style>
	body {
		font-family: "OpenSans", Arial, sans-serif;
		-webkit-font-smoothing: subpixel-antialiased;
		max-width: 1000px;
		margin: 10px auto;
		background: #eeeeee;
		color: #363463;
		font-size: 16px;
	}
	
	table th, table td {
		padding: 0px 0px;
		border: none;
	}
	
	table tr {
		border: none;
	}
	
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

<script language="javascript">
	
	var PurcformsText = {
		    file: "<spring:message code="xreports.file" />",
	    	view: "<spring:message code="xreports.view" />",
	    	item: "<spring:message code="xreports.item" />",
	    	tools: "<spring:message code="xreports.tools" />",
	    	help: "<spring:message code="xreports.help" />",
	    	newItem: "<spring:message code="xreports.newItem" />",
	    	open: "<spring:message code="xreports.open" />",
	    	save: "<spring:message code="xreports.save" />",
	    	saveAs: "<spring:message code="xreports.saveAs" />",
	
	    	openLayout: "<spring:message code="xreports.openLayout" />",
	    	saveLayout: "<spring:message code="xreports.saveLayout" />",
	    	openLanguageText: "<spring:message code="xreports.openLanguageText" />",
	    	saveLanguageText: "<spring:message code="xreports.saveLanguageText" />",
	    	close: "<spring:message code="xreports.close" />",
	
	    	refresh: "<spring:message code="xreports.refresh" />",
	    	addNew: "<spring:message code="xreports.addNew" />",
	    	addNewChild: "<spring:message code="xreports.addNewChild" />",
	    	deleteSelected: "<spring:message code="xreports.deleteSelected" />",
	    	moveUp: "<spring:message code="xreports.moveUp" />",
	    	moveDown: "<spring:message code="xreports.moveDown" />",
	    	cut: "<spring:message code="xreports.cut" />",
	    	copy: "<spring:message code="xreports.copy" />",
	    	paste: "<spring:message code="xreports.paste" />",
	    	
	    	format: "<spring:message code="xreports.format" />",
	    	languages: "<spring:message code="xreports.languages" />",
	    	options: "<spring:message code="xreports.options" />",
	
	    	helpContents: "<spring:message code="xreports.helpContents" />",
	    	about: "<spring:message code="xreports.about" />",
	
	    	forms: "<spring:message code="xreports.forms" />",
	    	widgetProperties: "<spring:message code="xreports.widgetProperties" />",
	    	properties: "<spring:message code="xreports.properties" />",
	    	xformsSource: "<spring:message code="xreports.xformsSource" />",
	    	designSurface: "<spring:message code="xreports.designSurface" />",
	    	layoutXml: "<spring:message code="xreports.layoutXml" />",
	    	languageXml: "<spring:message code="xreports.languageXml" />",
	    	preview: "<spring:message code="xreports.preview" />",
	    	modelXml: "<spring:message code="xreports.modelXml" />",
	
	    	text: "<spring:message code="xreports.text" />",
	    	helpText: "<spring:message code="xreports.helpText" />",
	    	type: "<spring:message code="xreports.type" />",
	    	binding: "<spring:message code="xreports.binding" />",
	    	visible: "<spring:message code="xreports.visible" />",
	    	enabled: "<spring:message code="xreports.enabled" />",
	    	locked: "<spring:message code="xreports.locked" />",
	    	required: "<spring:message code="xreports.required" />",
	    	defaultValue: "<spring:message code="xreports.defaultValue" />",
	    	descriptionTemplate: "<spring:message code="xreports.descriptionTemplate" />",
	
	    	language: "<spring:message code="xreports.language" />",
	    	skipLogic: "<spring:message code="xreports.skipLogic" />",
	    	validationLogic: "<spring:message code="xreports.validationLogic" />",
	    	dynamicLists: "<spring:message code="xreports.dynamicLists" />",
	
	    	valuesFor: "<spring:message code="xreports.valuesFor" />",
	    	whenAnswerFor: "<spring:message code="xreports.whenAnswerFor" />",
	    	isEqualTo: "<spring:message code="xreports.isEqualTo" />",
	    	forQuestion: "<spring:message code="xreports.forQuestion" />",
	    	enable: "<spring:message code="xreports.enable" />",
	    	disable: "<spring:message code="xreports.disable" />",
	    	show: "<spring:message code="xreports.show" />",
	    	hide: "<spring:message code="xreports.hide" />",
	    	makeRequired: "<spring:message code="xreports.makeRequired" />",
	
	    	when: "<spring:message code="xreports.when" />",
	    	ofTheFollowingApply: "<spring:message code="xreports.ofTheFollowingApply" />",
	    	all: "<spring:message code="xreports.all" />",
	    	any: "<spring:message code="xreports.any" />",
	    	none: "<spring:message code="xreports.none" />",
	    	notAll: "<spring:message code="xreports.notAll" />",
	
	    	addNewCondition: "<spring:message code="xreports.addNewCondition" />",
	
	    	isEqualTo: "<spring:message code="xreports.isEqualTo" />",
	    	isNotEqual: "<spring:message code="xreports.isNotEqual" />",
	    	isLessThan: "<spring:message code="xreports.isLessThan" />",
	    	isLessThanOrEqual: "<spring:message code="xreports.isLessThanOrEqual" />",
	    	isGreaterThan: "<spring:message code="xreports.isGreaterThan" />",
	    	isGreaterThanOrEqual: "<spring:message code="xreports.isGreaterThanOrEqual" />",
	    	isNull: "<spring:message code="xreports.isNull" />",
	    	isNotNull: "<spring:message code="xreports.isNotNull" />",
	    	isInList: "<spring:message code="xreports.isInList" />",
	    	isNotInList: "<spring:message code="xreports.isNotInList" />",
	    	startsWith: "<spring:message code="xreports.startsWith" />",
	    	doesNotStartWith: "<spring:message code="xreports.doesNotStartWith" />",
	    	endsWith: "<spring:message code="xreports.endsWith" />",
	    	doesNotEndWith: "<spring:message code="xreports.doesNotEndWith" />",
	    	contains: "<spring:message code="xreports.contains" />",
	    	doesNotContain: "<spring:message code="xreports.doesNotContain" />",
	    	isBetween: "<spring:message code="xreports.isBetween" />",
	    	isNotBetween: "<spring:message code="xreports.isNotBetween" />",
	
			isValidWhen: "<spring:message code="xreports.isValidWhen" />",
			errorMessage: "<spring:message code="xreports.errorMessage" />",
			question: "<spring:message code="xreports.question" />",
	
			addField: "<spring:message code="xreports.addField" />",
			submit: "<spring:message code="xreports.submit" />",
			addWidget: "<spring:message code="xreports.addWidget" />",
			newTab: "<spring:message code="xreports.newTab" />",
			deleteTab: "<spring:message code="xreports.deleteTab" />",
			selectAll: "<spring:message code="xreports.selectAll" />",
			load: "<spring:message code="xreports.load" />",
			
			label: "<spring:message code="xreports.label" />",
			textBox: "<spring:message code="xreports.textBox" />",
			checkBox: "<spring:message code="xreports.checkBox" />",
			radioButton: "<spring:message code="xreports.radioButton" />",
			dropdownList: "<spring:message code="xreports.dropdownList" />",
			textArea: "<spring:message code="xreports.textArea" />",
			button: "<spring:message code="xreports.button" />",
			datePicker: "<spring:message code="xreports.datePicker" />",
			groupBox: "<spring:message code="xreports.groupBox" />",
			repeatSection: "<spring:message code="xreports.repeatSection" />",
			picture: "<spring:message code="xreports.picture" />",
			videoAudio: "<spring:message code="xreports.videoAudio" />",
			listBox: "<spring:message code="xreports.listBox" />",
	
			deleteWidgetPrompt: "<spring:message code="xreports.deleteWidgetPrompt" />",
			deleteTreeItemPrompt: "<spring:message code="xreports.deleteTreeItemPrompt" />",
			selectDeleteItem: "<spring:message code="xreports.selectDeleteItem" />",
	
			selectedPage: "<spring:message code="xreports.selectedPage" />",
			shouldNotSharePageBinding: "<spring:message code="xreports.shouldNotSharePageBinding" />",
			selectedQuestion: "<spring:message code="xreports.selectedQuestion" />",
			shouldNotShareQuestionBinding: "<spring:message code="xreports.shouldNotShareQuestionBinding" />",
			selectedOption: "<spring:message code="xreports.selectedOption" />",
			shouldNotShareOptionBinding: "<spring:message code="xreports.shouldNotShareOptionBinding" />",
			newForm: "<spring:message code="xreports.newForm" />",
			page: "<spring:message code="xreports.page" />",
			option: "<spring:message code="xreports.option" />",
			noDataFound: "<spring:message code="xreports.noDataFound" />",
	
			formSaveSuccess: "<spring:message code="xreports.formSaveSuccess" />",
			selectSaveItem: "<spring:message code="xreports.selectSaveItem" />",
			deleteAllWidgetsFirst: "<spring:message code="xreports.deleteAllWidgetsFirst" />",
			deleteAllTabWidgetsFirst: "<spring:message code="xreports.deleteAllTabWidgetsFirst" />",
			cantDeleteAllTabs: "<spring:message code="xreports.cantDeleteAllTabs" />",
			noFormId: "<spring:message code="xreports.noFormId" />",
			divFound: "<spring:message code="xreports.noFormId" />",
			noFormLayout: "<spring:message code="xreports.noFormLayout" />",
			formSubmitSuccess: "<spring:message code="xreports.formSubmitSuccess" />",
			missingDataNode: "<spring:message code="xreports.missingDataNode" />",
	
			openingForm: "<spring:message code="xreports.openingForm" />",
			loadingTemplate: "<spring:message code="xreports.loadingTemplate" />",
			openingFormLayout: "<spring:message code="xreports.openingFormLayout" />",
			savingForm: "<spring:message code="xreports.savingForm" />",
			savingFormLayout: "<spring:message code="xreports.savingFormLayout" />",
			refreshingForm: "<spring:message code="xreports.refreshingForm" />",
			translatingFormLanguage: "<spring:message code="xreports.translatingFormLanguage" />",
			savingLanguageText: "<spring:message code="xreports.savingLanguageText" />",
			refreshingDesignSurface: "<spring:message code="xreports.refreshingDesignSurface" />",
			loadingDesignSurface: "<spring:message code="xreports.loadingDesignSurface" />",
			refreshingPreview: "<spring:message code="xreports.refreshingPreview" />",
	
			count: "<spring:message code="xreports.count" />",
			clickToPlay: "<spring:message code="xreports.clickToPlay" />",
			loadingPreview: "<spring:message code="xreports.loadingPreview" />",
			unexpectedFailure: "<spring:message code="xreports.unexpectedFailure" />",
			uncaughtException: "<spring:message code="xreports.uncaughtException" />",
			causedBy: "<spring:message code="xreports.causedBy" />",
			openFile: "<spring:message code="xreports.openFile" />",
			saveFileAs: "<spring:message code="xreports.saveFileAs" />",
	
			alignLeft: "<spring:message code="xreports.alignLeft" />",
			alignRight: "<spring:message code="xreports.alignRight" />",
			alignTop: "<spring:message code="xreports.alignTop" />",
			alignBottom: "<spring:message code="xreports.alignBottom" />",
			makeSameWidth: "<spring:message code="xreports.makeSameWidth" />",
			makeSameHeight: "<spring:message code="xreports.makeSameHeight" />",
			makeSameSize: "<spring:message code="xreports.makeSameSize" />",
			layout: "<spring:message code="xreports.layout" />",
			deleteTabPrompt: "<spring:message code="xreports.deleteTabPrompt" />",
	
			text: "<spring:message code="xreports.text" />",
		    toolTip: "<spring:message code="xreports.toolTip" />",
		    childBinding: "<spring:message code="xreports.childBinding" />",
		    width: "<spring:message code="xreports.width" />",
		    height: "<spring:message code="xreports.height" />",
		    left: "<spring:message code="xreports.left" />",
		    top: "<spring:message code="xreports.top" />",
		    tabIndex: "<spring:message code="xreports.tabIndex" />",
		    repeat: "<spring:message code="xreports.repeat" />",
		    externalSource: "<spring:message code="xreports.externalSource" />",
		    displayField: "<spring:message code="xreports.displayField" />",
		    valueField: "<spring:message code="xreports.valueField" />",
		    fontFamily: "<spring:message code="xreports.fontFamily" />",
		    foreColor: "<spring:message code="xreports.foreColor" />",
		    fontWeight: "<spring:message code="xreports.fontWeight" />",
		    fontStyle: "<spring:message code="xreports.fontStyle" />",
		    fontSize: "<spring:message code="xreports.fontSize" />",
		    textDecoration: "<spring:message code="xreports.textDecoration" />",
		    textAlign: "<spring:message code="xreports.textAlign" />",
		    backgroundColor: "<spring:message code="xreports.backgroundColor" />",
		    borderStyle: "<spring:message code="xreports.borderStyle" />",
		    borderWidth: "<spring:message code="xreports.borderWidth" />",
		    borderColor: "<spring:message code="xreports.borderColor" />",
		    aboutMessage: "<spring:message code="xreports.aboutMessage" />",
		    more: "<spring:message code="xreports.more" />",
		    requiredErrorMsg: "<spring:message code="xreports.requiredErrorMsg" />",
		    questionTextDesc: "<spring:message code="xreports.questionTextDesc" />",
		    questionDescDesc: "<spring:message code="xreports.questionDescDesc" />",
		    questionIdDesc: "<spring:message code="xreports.questionIdDesc" />",
		    defaultValDesc: "<spring:message code="xreports.defaultValDesc" />",
		    questionTypeDesc: "<spring:message code="xreports.questionTypeDesc" />",
		    qtnTypeText: "<spring:message code="xreports.qtnTypeText" />",
		    qtnTypeNumber: "<spring:message code="xreports.qtnTypeNumber" />",
		    qtnTypeDecimal: "<spring:message code="xreports.qtnTypeDecimal" />",
		    qtnTypeDate: "<spring:message code="xreports.qtnTypeDate" />",
		    qtnTypeTime: "<spring:message code="xreports.qtnTypeTime" />",
		    qtnTypeDateTime: "<spring:message code="xreports.qtnTypeDateTime" />",
		    qtnTypeBoolean: "<spring:message code="xreports.qtnTypeBoolean" />",
		    qtnTypeSingleSelect: "<spring:message code="xreports.qtnTypeSingleSelect" />",
		    qtnTypeMultSelect: "<spring:message code="xreports.qtnTypeMultSelect" />",
		    qtnTypeRepeat: "<spring:message code="xreports.qtnTypeRepeat" />",
		    qtnTypePicture: "<spring:message code="xreports.qtnTypePicture" />",
		    qtnTypeVideo: "<spring:message code="xreports.qtnTypeVideo" />",
		    qtnTypeAudio: "<spring:message code="xreports.qtnTypeAudio" />",
		    qtnTypeSingleSelectDynamic: "<spring:message code="xreports.qtnTypeSingleSelectDynamic" />",
		    deleteCondition: "<spring:message code="xreports.deleteCondition" />",
			addCondition: "<spring:message code="xreports.addCondition" />",
			value: "<spring:message code="xreports.value" />",
			questionValue: "<spring:message code="xreports.questionValue" />",
			and: "<spring:message code="xreports.and" />",
	   		deleteItemPrompt: "<spring:message code="xreports.deleteItemPrompt" />",
			changeWidgetTypePrompt: "<spring:message code="xreports.changeWidgetTypePrompt" />",
			removeRowPrompt: "<spring:message code="xreports.removeRowPrompt" />",
			remove: "<spring:message code="xreports.remove" />",
			browse: "<spring:message code="xreports.browse" />",
			clear: "<spring:message code="xreports.clear" />",
			deleteItem: "<spring:message code="xreports.deleteItem" />",
			cancel: "<spring:message code="xreports.cancel" />",
			clickToAddNewCondition: "<spring:message code="xreports.clickToAddNewCondition" />",
			qtnTypeGPS: "<spring:message code="xreports.qtnTypeGPS" />",
			qtnTypeBarcode: "<spring:message code="xreports.qtnTypeBarcode" />",
			qtnTypeGroup: "<spring:message code="xreports.qtnTypeGroup" />",
			palette: "<spring:message code="xreports.palette" />",
			saveAsXhtml: "<spring:message code="xreports.saveAsXhtml" />",
			groupWidgets: "<spring:message code="xreports.groupWidgets" />",
			action: "<spring:message code="xreports.action" />",
			submitting: "<spring:message code="xreports.submitting" />",
			authenticationPrompt: "<spring:message code="xreports.authenticationPrompt" />",
			invalidUser: "<spring:message code="xreports.invalidUser" />",
			login: "<spring:message code="xreports.login" />",
			userName: "<spring:message code="xreports.userName" />",
			password: "<spring:message code="xreports.password" />",
			noSelection: "<spring:message code="xreports.noSelection" />",
			cancelFormPrompt: "<spring:message code="xreports.cancelFormPrompt" />",
			print: "<spring:message code="xreports.print" />",
			pageSetup: "<spring:message code="xreports.pageSetup" />",
			yes: "<spring:message code="xreports.yes" />",
			no: "<spring:message code="xreports.no" />",
	   		searchServer: "<spring:message code="xreports.searchServer" />",
	   		recording: "<spring:message code="xreports.recording" />",
	   		search: "<spring:message code="xreports.search" />",
	   		processingMsg: "<spring:message code="xreports.processingMsg" />",
	   		length: "<spring:message code="xreports.length" />",
	   		clickForOtherQuestions: "<spring:message code="xreports.clickForOtherQuestions" />",
	   		ok: "<spring:message code="xreports.ok" />",
	   		undo: "<spring:message code="xreports.undo" />",
	   		redo: "<spring:message code="xreports.redo" />",
	   		loading: "<spring:message code="xreports.loading" />",
	   		allQuestions: "<spring:message code="xreports.allQuestions" />",
	   		selectedQuestions: "<spring:message code="xreports.selectedQuestions" />",
	   		otherQuestions: "<spring:message code="xreports.otherQuestions" />",
	   		wrongFormat: "<spring:message code="xreports.wrongFormat" />",
	   		timeWidget: "<spring:message code="xreports.timeWidget" />",
			dateTimeWidget: "<spring:message code="xreports.dateTimeWidget" />",
			lockWidgets: "<spring:message code="xreports.lockWidgets" />",
			unLockWidgets: "<spring:message code="xreports.unLockWidgets" />",
			changeWidgetH: "<spring:message code="xreports.changeWidgetH" />",
			changeWidgetV: "<spring:message code="xreports.changeWidgetV" />",
			changeToTextBoxWidget: "<spring:message code="xreports.changeToTextBoxWidget" />",
			saveAsPurcForm: "<spring:message code="xreports.saveAsPurcForm" />",
			localeChangePrompt: "<spring:message code="xreports.localeChangePrompt" />",
			javaScriptSource: "<spring:message code="xreports.javaScriptSource" />",
	   		calculation: "<spring:message code="xreports.calculation" />",
	   		id: "<spring:message code="xreports.id" />",
	   		formKey: "<spring:message code="xreports.formKey" />",
	   		logo: "<spring:message code="xreports.logo" />",
	   		filterField: "<spring:message code="xreports.filterField" />",
	   		table: "<spring:message code="xreports.table" />",
	  		horizontalLine: "<spring:message code="xreports.horizontalLine" />",
	   		verticalLine: "<spring:message code="xreports.verticalLine" />",
	   		addRowsBelow: "<spring:message code="xreports.addRowsBelow" />",
	   		addRowsAbove: "<spring:message code="xreports.addRowsAbove" />",
	   		addColumnsRight: "<spring:message code="xreports.addColumnsRight" />",
	   		addColumnsLeft: "<spring:message code="xreports.addColumnsLeft" />",
	   		numberOfRowsPrompt: "<spring:message code="xreports.numberOfRowsPrompt" />",
	   		numberOfColumnsPrompt: "<spring:message code="xreports.numberOfColumnsPrompt" />",
	   		deleteColumn: "<spring:message code="xreports.deleteColumn" />",
	   		deleteRow: "<spring:message code="xreports.deleteRow" />",
	   		repeatChildDataNodeNotFound: "<spring:message code="xreports.repeatChildDataNodeNotFound" />",
	   		selectedFormField: "<spring:message code="xreports.selectedFormField" />",
	   		edit: "<spring:message code="xreports.edit" />",
	   		find: "<spring:message code="xreports.find" />",
	   		css: "<spring:message code="xreports.css" />",
	   		bold: "<spring:message code="xreports.bold" />",
	   		italic: "<spring:message code="xreports.italic" />",
	   		underline: "<spring:message code="xreports.underline" />",
	   		mergeCells: "<spring:message code="xreports.mergeCells" />",
	   		deleteFormPrompt: "<spring:message code="xreports.deleteFormPrompt" />",
       		formDeleteSuccess: "<spring:message code="xreports.formDeleteSuccess" />",
       		exclusiveOption: "<spring:message code="xreports.exclusiveOption" />",
       		otherProperties: "<spring:message code="xreports.otherProperties" />",
       		exclusiveQuestion: "<spring:message code="xreports.exclusiveQuestion" />",
       		rotate: "<spring:message code="xreports.rotate" />",
       		report: "<spring:message code="xreports.report" />",
       		exportPdf: "<spring:message code="xreports.exportPdf" />",		
       		email: "<spring:message code="xreports.email" />",
       		send: "<spring:message code="xreports.send" />",
       		sendTo: "<spring:message code="xreports.sendTo" />",
       		subject: "<spring:message code="xreports.subject" />",
       		message: "<spring:message code="xreports.message" />",
       		report: "<spring:message code="xreports.report" />"
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