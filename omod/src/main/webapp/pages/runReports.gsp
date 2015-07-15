<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.run.reports.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        }
        
        <% if (crumbs.size() == 0) { %>
        	,{ label: "${ ui.message("xreports.run.reports.app.label")}"}
        <% } else { %>
        	,{ label: "${ ui.message("xreports.run.reports.app.label")}",
        	   link: "${ui.pageLink("xreports", "runReports")}"
        	 }
        	 
        	 <% crumbs.each { crumb -> %>
        	 	,{ label: "${crumb.name}",
	        	   link: "${ui.pageLink("xreports", "runReports", [groupId: crumb.value])}"
	        	 }
        	 <% } %>
        	 
        <% } %>
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
		        <td>
		        	<a href="reportRunner.page?reportId=${report.reportId}<% if (param.groupId) { %>&groupId=${param.groupId[0]}<% } %>&refApp=true">${report.name}</a>
		        </td>
		    </tr>
	    <% } %>
	    
	    <% groups.each { group -> %>
		    <tr>
		        <td>
		        	<a href="runReport.form?groupId=${group.groupId}&refApp=true">${group.name}</a>
		        </td>
		    </tr>
	    <% } %>
    </tbody>
</table>

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
