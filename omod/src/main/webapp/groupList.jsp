<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Manager" otherwise="/login.htm" redirect="/module/xreports/group.list" />
<openmrs:message var="pageTitle" code="xreports.reportGroup.manage.titlebar" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<a href="group.form"><openmrs:message code="xreports.reportGroup.add"/></a>

<br/><br/>

<b class="boxHeader"><openmrs:message code="xreports.reportGroup.list.title"/></b>
<form method="get" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th> <openmrs:message code="general.name"/> </th>
			<th> <openmrs:message code="xreports.identifier"/> </th>
		</tr>
	<c:forEach var="group" items="${groups}" varStatus="rowStatus">
		<tr class='${rowStatus.index % 2 == 0 ? "evenRow" : "oddRow" }'>
			<td style="white-space: nowrap">
				<a href="group.form?groupId=<c:out value="${group.groupId}"/>">
					<c:out value="${group.name}"/>
				</a>
			</td>
			<td style="white-space: nowrap">
				<c:out value="${group.identifier}"/>
			</td>
		</tr>
	</c:forEach>
	</table>
</form>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>