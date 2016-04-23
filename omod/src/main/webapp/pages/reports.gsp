<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.manage.reports.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        }
        
        <% if (crumbs.size() == 0) { %>
        	,{ label: "${ ui.message("xreports.manage.reports.app.label")}"}
        <% } else { %>
        	,{ label: "${ ui.message("xreports.manage.reports.app.label")}",
        	   link: "${ui.pageLink("xreports", "reports")}"
        	 }
        	 
        	 <% crumbs.each { crumb -> %>
        	 	,{ label: "${crumb.name}",
	        	   link: "${ui.pageLink("xreports", "reports", [groupId: crumb.value])}"
	        	 }
        	 <% } %>
        	 
        <% } %>
    ];
</script>

<style type="text/css">
	table thead th  {
		text-align: center;
	}
</style>

<button class="confirm" onclick="location.href='${ ui.pageLink("xreports", "report") }'">
    ${ ui.message("xreports.report.add") }
</button>

</br></br>

<table id="reports">
    <thead>
	    <tr>
	        <th>${ ui.message("xreports.name")}</th>
	        <th>${ ui.message("xreports.identifier")}</th>
	        <th>${ ui.message("coreapps.actions") }</th>
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
		        	<a href='${ ui.pageLink("xreports", "report", [reportId:report.id, groupId:param.groupId]) }'> ${report.name} </a>
		        </td>
		        <td>
		        	${report.identifier}
		        </td>
		        <td align="center">	
					<i class="icon-table edit-action" title="${ ui.message("xreports.design") }"
						onclick="location.href='/${ ui.contextPath() }/module/xreports/reportDesigner.form?reportId=${report.reportId}&refApp=true<% if (param.groupId) { %>&groupId=${param.groupId[0]}<% } %>'"></i>
					
					<i class="icon-pencil edit-action" title="${ ui.message("coreapps.edit") }"
						onclick="location.href='${ ui.pageLink("xreports", "report", [reportId:report.id, groupId:param.groupId]) }'"></i>
							
					<i class="icon-remove delete-action" title="${ ui.message("coreapps.delete") }" onclick="removeReport('${ report }', ${ report.id})"></i>
				</td>
		    </tr>
	    <% } %>
	    
	    <% groups.each { group -> %>
		    <tr>
		        <td>
		        	<a href="reports.page?groupId=${group.groupId}&refApp=true">${group.name}</a>
		        </td>
		        <td>
		        	${group.identifier}
		        </td>
		    </tr>
	    <% } %>
    </tbody>
</table>

<div id="xreports-remove-report-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("xreports.report.delete") }</h3>
    </div>
    <div class="dialog-content">
        <ul>
            <li class="info">
                <span id="removeReportMessage"></span>
            </li>
        </ul>
        <form method="POST" action="reports.page">
            <input type="hidden" id="reportId" name="reportId" value=""/>
            <input type="hidden" name="action" value="removeReport"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>

<script type="text/javascript">

	var deleteMessage = '${ ui.message("xreports.report.delete.confirm.specific") }';
	
	var removeReportDialog = null;

	jq(document).ready( function() {
	    
	    removeReportDialog = emr.setupConfirmationDialog({
	        selector: '#xreports-remove-report-dialog',
	        actions: {
	            cancel: function() {
	            	removeReportDialog.close();
	            }
	        }
	    });
	
	});
	
	function showRemoveReportDialog() {
	    removeReportDialog.show();
	}
	
	function removeReport(report, id) {
	    jq("#reportId").val(id);
	    jq("#removeReportMessage").text(deleteMessage.replace("{0}", report));
	    showRemoveReportDialog(report, id);
	}
</script>
