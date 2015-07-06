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
        <label for="reportname">
            ${ui.message("general.name")}
        </label>
        <input id="reportname" class="required" name="reportname"></input>
    </p>
    
    <p>
        <label for="identifier">
            ${ui.message("xreports.identifier")}
        </label>
        <input id="identifier" name="identifier"></input>
    </p>
    
    <p>
        <label for="group">
            ${ui.message("xreports.report.group")}
        </label>
        <select id="identifier" name="group"></select>
    </p>

    <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/xreports/manageReports.page'" />
    <input type="submit" class="confirm right" id="save-button" value="${ ui.message("general.save") }" />


</form>