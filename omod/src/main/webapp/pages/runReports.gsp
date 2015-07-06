<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.run.reports.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        },
        { label: "${ ui.message("xreports.run.reports.app.label")}"}
    ];
</script>

<table id="reports">
    <thead>
	    <tr>
	        <th>${ ui.message("xreports.name")}</th>
	    </tr>
    </thead>
    
    <tbody>
    	<% if ((reports == null || (reports != null && reports.size() == 0) ) &&
    			(groups == null || (groups != null && groups.size() == 0) )) { %>
			<tr>
				<td>${ ui.message("coreapps.none") }</td>
			</tr>
		<% } %>
		
	    <% reports.each { report -> %>
		    <tr>
		        <td>${report.name}</td>
		    </tr>
	    <% } %>
	    
	    <% groups.each { group -> %>
		    <tr>
		        <td>${group.name}</td>
		    </tr>
	    <% } %>
    </tbody>
</table>

<div id="allergyui-remove-allergy-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("xreports.report.delete") }</h3>
    </div>
    <div class="dialog-content">
        <ul>
            <li class="info">
                <span id="removeReportMessage"></span>
            </li>
        </ul>
        <form method="POST" action="manageReports.page">
            <input type="hidden" id="reportId" name="reportId" value=""/>
            <input type="hidden" name="action" value="removeReport"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>

<% if ((reports !=null && reports.size() > 0) || (reports !=null && reports.size() > 0) ) { %>
${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#reports",
                                                         options: [
                                                                     bFilter: true,
                                                                     bJQueryUI: true,
                                                                     bLengthChange: false,
                                                                     iDisplayLength: 10,
                                                                     sPaginationType: '\"full_numbers\"',
                                                                     bSort: false,
                                                                     sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
                                                                  ]
                                                        ]) }
<% } %>
