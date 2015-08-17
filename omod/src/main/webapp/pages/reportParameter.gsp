<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.run.reports.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        }
        
        ,{ label: "${ ui.message("xreports.run.reports.app.label")}",
        	link: "${ui.pageLink("xreports", "runReports")}"
        }
        	 
        <% if (crumbs.size() > 0) { %>
        	 <% crumbs.each { crumb -> %>
        	 	,{ label: "${crumb.name}",
	        	   link: "${ui.pageLink("xreports", "runReports", [groupId: crumb.value])}"
	        	 }
        	 <% } %>
        	 
        <% } %>
        
        ,{ label: "${formName}"}
    ];
</script>