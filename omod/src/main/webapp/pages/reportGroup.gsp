<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.manage.groups.app.label") ])
    
    ui.includeCss("referenceapplication", "manageApps.css");
    
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        },
        { label: "${ ui.message("xreports.manage.groups.app.label")}",
          link: "${ui.pageLink("xreports", "manageReportGroups")}"
        },
        { label: "${ ui.message("xreports.reportGroup.add.title")}"}
    ];
</script>

<form method="POST" action="reportGroup.page">

    <p>
        <label for="groupname">
            ${ui.message("general.name")}
        </label>
        <input id="groupname" class="required" name="groupname"></input>
    </p>
    
    <p>
        <label for="identifier">
            ${ui.message("xreports.identifier")}
        </label>
        <input id="identifier" name="identifier"></input>
    </p>
    
    <p>
        <label for="parentGroup">
            ${ui.message("xreports.reportGroup.parentGroup")}
        </label>
        <select id="identifier" name="parentGroup"></select>
    </p>

    <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/xreports/manageReportGroups.page'" />
    <input type="submit" class="confirm right" id="save-button" value="${ ui.message("general.save") }" />


</form>