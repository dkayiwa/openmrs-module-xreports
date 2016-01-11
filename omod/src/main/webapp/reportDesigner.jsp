<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ include file="/WEB-INF/template/include.jsp" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DwrReportDesignerService.js'></script>


<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />

<html>
  <head>
    <title>XReports - ${templateName}</title>
    
    <openmrs:htmlInclude file="/openmrs.js" />
    <openmrs:htmlInclude file="/moduleResources/xreports/reportdesigner/ReportDesigner.nocache.js"/>

			<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
			<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
		    <openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-addon.js" />
			<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-datepicker-i18n.js" />
			<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-i18n.js" />
		
        	<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/green/jquery-ui.custom.css" type="text/css" rel="stylesheet" />
		
			<script type="text/javascript">
				<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
					var $j = jQuery.noConflict();
				</c:if>
				
				var jsDateFormat = '<openmrs:datePattern localize="false"/>';
				var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';
			</script>
			
	<link rel="shortcut icon" type="image/ico" href="/images/openmrs-favicon.ico">
	<link rel="icon" type="image/png" href="<openmrs:contextPath/>/images/openmrs-favicon.png">
	

  </head>
  <body>
  
  <style type="text/css">
	body {
		font-size: 12px;
	}
</style>

  	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
  
    <div id="purcreportsdesigner"><div>
    <div id="title" style="visibility:hidden;">${templateName}</div>
    <div id="rubberBand"></div>
    
    <div id="formId" style="visibility:hidden;">${formId}</div>
    
	<div id="closeUrl" style="visibility:hidden;">${closeUrl}</div>
    
    <div id="entityIdName" style="visibility:hidden;">templateId</div>
    <div id="formIdName" style="visibility:hidden;">formId</div>
    
    <div id="formDefDownloadUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/reportDownloadServlet?contentType=xml&</div>
    <div id="formDefUploadUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/reportUploadServlet?contentType=xml&</div>
    <div id="exportPdfUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/exportPdfServlet?</div>
    <div id="fileOpenUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/fileOpenServlet</div>
    <div id="fileSaveUrlSuffix" style="visibility:hidden;">moduleServlet/xreports/fileSaveServlet</div>
    <div id="uploadImageUrlSuffix" style="visibility:hidden;">/moduleServlet/xreports/imageUploadServlet</div>
    
    <div id="defaultFontFamily" style="visibility:hidden;">${defaultFontFamily}</div>
    <div id="defaultFontSize" style="visibility:hidden;">${defaultFontSize}</div>
    <div id="defaultGroupBoxHeaderBgColor" style="visibility:hidden;">${defaultGroupBoxHeaderBgColor}</div>
    
    <div id="showSubmitSuccessMsg" style="visibility:hidden;">${showSubmitSuccessMsg}</div>
        
    <div id="undoRedoBufferSize" style="visibility:hidden;">${undoRedoBufferSize}</div>
    
    <div id="formatXml" style="visibility:hidden;">false</div>
    
    <div id="dynamicDesignItems" style="visibility:hidden;">true</div>
    
   <script language="javascript">
   		
    	var PurcformsText = {
     	    	file: "<spring:message code="xreports.file" />",
    	    	view: "<spring:message code="xreports.view" />",
    	    	item: "<spring:message code="xreports.item" />",
    	    	tools: "<spring:message code="xreports.tools" />",
    	    	help: "<spring:message code="xreports.help" />",
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

    	    	designItems: "<spring:message code="xreports.designItems" />",
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

    			errorMessage: "<spring:message code="xreports.errorMessage" />",
    			addWidget: "<spring:message code="xreports.addWidget" />",
    			selectAll: "<spring:message code="xreports.selectAll" />",
    			
    			label: "<spring:message code="xreports.label" />",
    			groupBox: "<spring:message code="xreports.groupBox" />",
    			picture: "<spring:message code="xreports.picture" />",
    			listBox: "<spring:message code="xreports.listBox" />",

    			deleteWidgetPrompt: "<spring:message code="xreports.deleteWidgetPrompt" />",
    			deleteTreeItemPrompt: "<spring:message code="xreports.deleteTreeItemPrompt" />",
    			selectDeleteItem: "<spring:message code="xreports.selectDeleteItem" />",

   			    newForm: "<spring:message code="xreports.newForm" />",
    			noDataFound: "<spring:message code="xreports.noDataFound" />",

    			reportSaveSuccess: "<spring:message code="xreports.reportSaveSuccess" />",
    			selectSaveItem: "<spring:message code="xreports.selectSaveItem" />",
    			deleteAllWidgetsFirst: "<spring:message code="xreports.deleteAllWidgetsFirst" />",
    			deleteAllTabWidgetsFirst: "<spring:message code="xreports.deleteAllTabWidgetsFirst" />",
    			cantDeleteAllTabs: "<spring:message code="xreports.cantDeleteAllTabs" />",
    			noFormId: "<spring:message code="xreports.noFormId" />",
    			divFound: "<spring:message code="xreports.noFormId" />",
    			noFormLayout: "<spring:message code="xreports.noFormLayout" />",
    			formSubmitSuccess: "<spring:message code="xreports.formSubmitSuccess" />",
    			missingDataNode: "<spring:message code="xreports.missingDataNode" />",

    			loadingTemplate: "<spring:message code="xreports.loadingTemplate" />",
    			openingFormLayout: "<spring:message code="xreports.openingFormLayout" />",
    			savingTemplate: "<spring:message code="xreports.savingTemplate" />",
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
     		    width: "<spring:message code="xreports.width" />",
    		    height: "<spring:message code="xreports.height" />",
    		    left: "<spring:message code="xreports.left" />",
    		    top: "<spring:message code="xreports.top" />",
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
          		and: "<spring:message code="xreports.and" />",
           		deleteItemPrompt: "<spring:message code="xreports.deleteItemPrompt" />",
        		remove: "<spring:message code="xreports.remove" />",
        		browse: "<spring:message code="xreports.browse" />",
        		clear: "<spring:message code="xreports.clear" />",
        		deleteItem: "<spring:message code="xreports.deleteItem" />",
        		cancel: "<spring:message code="xreports.cancel" />",
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
           		search: "<spring:message code="xreports.search" />",
           		processingMsg: "<spring:message code="xreports.processingMsg" />",
           		length: "<spring:message code="xreports.length" />",
           		ok: "<spring:message code="xreports.ok" />",
           		undo: "<spring:message code="xreports.undo" />",
           		redo: "<spring:message code="xreports.redo" />",
           		loading: "<spring:message code="xreports.loading" />",
      			lockWidgets: "<spring:message code="xreports.lockWidgets" />",
    			unLockWidgets: "<spring:message code="xreports.unLockWidgets" />",
    			lockAllWidgets: "<spring:message code="xreports.lockAllWidgets" />",
    			unLockAllWidgets: "<spring:message code="xreports.unLockAllWidgets" />",
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
           		page: "<spring:message code="xreports.page" />",
           		insertPage: "<spring:message code="xreports.insertPage" />",
           		deletePage: "<spring:message code="xreports.deletePage" />",
           		exportPdf: "<spring:message code="xreports.exportPdf" />",
           		openArchive: "<spring:message code="xreports.openArchive" />",
    	    	saveArchive: "<spring:message code="xreports.saveArchive" />",
           		selectArchive: "<spring:message code="xreports.selectArchive" />",
           		archiveSaveSuccess: "<spring:message code="xreports.archiveSaveSuccess" />",
           		deleteArchive: "<spring:message code="xreports.deleteArchive" />",
           		uploadImage: "<spring:message code="xreports.uploadImage" />"
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
    	
    </script>
    
  </body>
</html>