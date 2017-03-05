<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.manage.reports.app.label") ])
    
    ui.includeCss("referenceapplication", "manageApps.css");
    
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        },
        { label: "${ ui.message("xreports.manage.reports.app.label")}",
          link: "${ui.pageLink("xreports", "reports")}"
        },
        <% crumbs.each { crumb -> %>
        	 	{ label: "${crumb.name}",
	        	   link: "${ui.pageLink("xreports", "reports", [groupId: crumb.value])}"
	        	},
        <% } %>
        { label: "${reportName}"}
    ];
</script>

<form method="POST" action="report.page">

    <p>
        <label for="reportName">
            ${ui.message("general.name")}
        </label>
        <input id="reportName" class="required" name="reportName" <% if (report.name != null) { %> value="${report.name}" <% } %> />
    </p>
    
    <p>
        <label for="identifier">
            ${ui.message("xreports.identifier")}
        </label>
        <input id="identifier" name="identifier" <% if (report.identifier != null) { %> value="${report.identifier}" <% } %> />
    </p>
    
    <p>
        <label for="displayOrder">
            ${ui.message("xreports.displayOrder")}
        </label>
        <input id="displayOrder" name="displayOrder" <% if (report.displayOrder != null) { %> value="${report.displayOrder}" <% } %> />
    </p>
    
    <p>
        <label for="group">
            ${ui.message("xreports.report.group")}
        </label>
        <select id="group" name="group"  <% if (report.group != null) { %> value="${report.group.groupId}" <% } %> >
        	<option></option>
        	<% groups.each { grp -> %>
	        	<option value="${grp.groupId}" <% if (report.group != null && grp.groupId == report.group.groupId) { %> selected="selected" <% } %> >
	        		${grp.name}
	        	</option>
        	<% } %>
        </select>
    </p>
    
    <p>
        <label for="source">
            ${ui.message("xreports.reportDefinition")}
        </label>
        <select id="externalReportUuid" name="externalReportUuid"  <% if (report.externalReportUuid != null) { %> value="${report.externalReportUuid}" <% } %> >
        	<option></option>
        	<% reportDefinitions.each { reportDefinition -> %>
	        	<option value="${reportDefinition.uuid}" <% if (report.externalReportUuid != null && reportDefinition.uuid == report.externalReportUuid) { %> selected="selected" <% } %> >
	        		${reportDefinition.name}
	        	</option>
        	<% } %>
        </select>
    </p>
    
    <p>
        <label for="extension">
            ${ui.message("xreports.extension")}
        </label>
        <textarea id="extension" name="extension"> <% if (report.extension != null) { %> ${report.extension} <% } %> </textarea>
    </p>

    <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/xreports/reports.page<% if (param.groupId) { %>?groupId=${param.groupId[0]}<% } %>'" />
    <input type="submit" class="confirm right" id="save-button" value="${ ui.message("general.save") }" />
	<input type="hidden" id="reportId" name="reportId" <% if (report.reportId != null) { %> value="${report.reportId}" <% } %> />

</form>