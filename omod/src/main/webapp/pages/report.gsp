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
          link: "${ui.pageLink("xreports", "manageReports")}"
        },
        { label: "${ ui.message("xreports.report.add.title")}"}
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

    <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/xreports/reports.page'" />
    <input type="submit" class="confirm right" id="save-button" value="${ ui.message("general.save") }" />
	<input type="hidden" id="reportId" name="reportId" <% if (report.reportId != null) { %> value="${report.reportId}" <% } %> />

</form>