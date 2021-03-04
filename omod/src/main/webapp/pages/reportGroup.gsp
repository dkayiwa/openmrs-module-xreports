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
          link: "${ui.pageLink("xreports", "reportGroups")}"
        },
        { label: "${ ui.message("xreports.reportGroup.add.title")}"}
    ];
    
    function validate() {
    	var groupName = document.getElementById("groupName").value;
    	var displayOrder = document.getElementById("displayOrder").value;
    	var valid = true;
    	
    	if(groupName.trim() == ""){
    		alert("Name should not be empty.");
    		valid = false;
    	}
    	
    	if(isNaN(displayOrder)){
    		alert("Display Order must be a number.");
    		valid = false;
    	}
    	return valid;
    };
</script>

<form method="POST" action="reportGroup.page" onsubmit = "return validate();">

    <p>
        <label for="groupName">
            ${ui.message("general.name")}
        </label>
        <input id="groupName" class="required" name="groupName" <% if (group.name != null) { %> value="${group.name}" <% } %> />
    </p>
    
    <p>
        <label for="identifier">
            ${ui.message("xreports.identifier")}
        </label>
        <input id="identifier" name="identifier" <% if (group.identifier != null) { %> value="${group.identifier}" <% } %> />
    </p>
    
    <p>
        <label for="displayOrder">
            ${ui.message("xreports.displayOrder")}
        </label>
        <input id="displayOrder" name="displayOrder" <% if (group.displayOrder != null) { %> value="${group.displayOrder}" <% } %> />
    </p>
    
    <p>
        <label for="parentGroup">
            ${ui.message("xreports.reportGroup.parentGroup")}
        </label>
        <select id="parentGroup" name="parentGroup"  <% if (group.parentGroup != null) { %> value="${group.parentGroup.groupId}" <% } %> >
        	<option></option>
        	<% groups.each { grp -> %>
	        	<option value="${grp.groupId}" <% if (group.parentGroup != null && grp.groupId == group.parentGroup.groupId) { %> selected="selected" <% } %> >
	        		${grp.name}
	        	</option>
        	<% } %>
        </select>
    </p>

    <input type="button" class="cancel" value="${ ui.message("general.cancel") }" onclick="javascript:window.location='/${ contextPath }/xreports/reportGroups.page'" />
    <input type="submit" class="confirm right" id="save-button" value="${ ui.message("general.save") }" />
	<input type="hidden" id="groupId" name="groupId" <% if (group.groupId != null) { %> value="${group.groupId}" <% } %> />

</form>