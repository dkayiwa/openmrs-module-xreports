<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Manager" otherwise="/login.htm" redirect="module/xreports/runReports.list" />

<openmrs:message var="pageTitle" code="xreports.reports.titlebar" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<ul id="menu">

	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><openmrs:message code="xreports.admin"/></a>
	</li>
	<openmrs:hasPrivilege privilege="Manage Reports">
		<li <c:if test='<%= request.getRequestURI().contains("xreports/group") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/xreports/group.list">
				<openmrs:message code="xreports.manage.reportGroups"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="Manage Reports">
		<li <c:if test='<%= request.getRequestURI().contains("xreports/report") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/xreports/report.list">
				<openmrs:message code="xreports.manage.reports"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege="View Reports">
		<li <c:if test='<%= request.getRequestURI().contains("xreports/runReports") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/xreports/runReports.list">
				<openmrs:message code="xreports.run"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
	<c:if test="${!empty param.groupId}">
		<li class="active">
			<a href="${pageContext.request.contextPath}/module/xreports/runReports.list?groupId=${param.groupId}">
				${reportTitle}
			</a>
		</li>
	</c:if>
</ul>


<script type="text/javascript">
	$j(document).ready(function() {
		var oTable = $j("#reportsTable").dataTable({
			"bPaginate": false,
			"bAutoWidth": true,
			"aaSorting": [[0, 'asc']],
			"aoColumns":
				[
					{ "iDataSort": 1 },
					{ "bVisible": false},
				]
		});
	});
</script>

	<table id="reportsTable" style="width:100%">
		<tbody>
		
			<c:forEach var="report" items="${reports}">
				<tr>
					<td>
						<a href="runReport.form?reportId=${report.reportId}<c:if test="${!empty param.groupId}">&groupId=${param.groupId}</c:if>">${report.name}</a>
					</td>
					<td></td>
				</tr>
			</c:forEach>
			
			<c:forEach var="group" items="${groups}">
				<tr>
					<td>
						<a href="runReport.form?groupId=${group.groupId}">${group.name}</a>
					</td>
					<td></td>
				</tr>
			</c:forEach>
			
		</tbody>
	</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>