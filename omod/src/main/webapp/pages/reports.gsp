<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.manage.reports.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        },
        { label: "${ ui.message("xreports.manage.reports.app.label")}"}
    ];
</script>

<button class="confirm" onclick="location.href='${ ui.pageLink("xreports", "report") }'">
    ${ ui.message("xreports.report.add") }
</button>

</br></br>

<table>
    <thead>
	    <tr>
	        <th>${ ui.message("xreports.name")}</th>
	        <th>${ ui.message("xreports.identifier")}</th>
	        <th>${ ui.message("coreapps.actions") }</th>
	    </tr>
    </thead>
    
    <tbody>
	    <% reports.each { report -> %>
		    <tr>
		        <td>${report.name}</td>
		        <td>${report.identifier}</td>
		        <td>
					<i class="icon-pencil edit-action" title="${ ui.message("coreapps.edit") }"
						onclick="location.href='${ ui.pageLink("xreports", "report", [reportId:report.id]) }'"></i>
					<i class="icon-remove delete-action" title="${ ui.message("coreapps.delete") }" onclick="removeReport('${ report }', ${ report.id})"></i>
				</td>
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

<script type="text/javascript">

	var deleteMessage = '${ ui.message("xreports.report.delete.confirm.specific") }';
	
	var removeReportDialog = null;

	jq(document).ready( function() {
	    
	    removeReportDialog = emr.setupConfirmationDialog({
	        selector: '#allergyui-remove-allergy-dialog',
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
