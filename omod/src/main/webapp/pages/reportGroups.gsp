<%
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("xreports.manage.groups.app.label") ]) 
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("xreports.app.label")}",
          link: "${ui.pageLink("xreports", "dashboard")}"
        },
        { label: "${ ui.message("xreports.manage.groups.app.label")}"}
    ];
</script>

<button class="confirm" onclick="location.href='${ ui.pageLink("xreports", "reportGroup") }'">
    ${ ui.message("xreports.reportGroup.add") }
</button>

</br></br>

<table>
    <thead>
	    <tr>
	        <th>${ ui.message("xreports.name")}</th>
	        <th>${ ui.message("xreports.identifier")}</th>
	        <th>${ ui.message("xreports.parent")}</th>
	        <th>${ ui.message("coreapps.actions") }</th>
	    </tr>
    </thead>
    
    <tbody>
	    <% groupsAndDepth.each { groupAndDepth -> %>
		    <tr>
		        <td>
		        	<% for (i = 1; i <= groupAndDepth.depth; i++) { %>
		 				&nbsp;&nbsp;
		 			<% } %>
		        	${groupAndDepth.group.name}
		        </td>
		        <td>${groupAndDepth.group.identifier}</td>
		        <td> <% if (groupAndDepth.group.parentGroup != null) { %> ${groupAndDepth.group.parentGroup.name} <% } %> </td>
		        <td>
					<i class="icon-pencil edit-action" title="${ ui.message("coreapps.edit") }"
						onclick="location.href='${ ui.pageLink("xreports", "reportGroup", [groupId:groupAndDepth.group.id]) }'"></i>
					<i class="icon-remove delete-action" title="${ ui.message("coreapps.delete") }" onclick="removeGroup('${ groupAndDepth.group }', ${ groupAndDepth.group.id})"></i>
				</td>
		    </tr>
	    <% } %>
    </tbody>
</table>

<div id="xreports-remove-group-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("xreports.reportGroup.delete") }</h3>
    </div>
    <div class="dialog-content">
        <ul>
            <li class="info">
                <span id="removeGroupMessage"></span>
            </li>
        </ul>
        <form method="POST" action="reportGroups.page">
            <input type="hidden" id="groupId" name="groupId" value=""/>
            <input type="hidden" name="action" value="removeGroup"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>

<script type="text/javascript">

	var deleteMessage = '${ ui.message("xreports.reportGroup.delete.confirm.specific") }';
	
	var removeGroupDialog = null;

	jq(document).ready( function() {
	    
	    removeGroupDialog = emr.setupConfirmationDialog({
	        selector: '#xreports-remove-group-dialog',
	        actions: {
	            cancel: function() {
	            	removeGroupDialog.close();
	            }
	        }
	    });
	
	});
	
	function showRemoveGroupDialog() {
	    removeGroupDialog.show();
	}
	
	function removeGroup(group, id) {
	    jq("#groupId").val(id);
	    jq("#removeGroupMessage").text(deleteMessage.replace("{0}", group));
	    showRemoveGroupDialog(group, id);
	}
</script>
