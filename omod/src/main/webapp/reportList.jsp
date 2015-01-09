<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Manager" otherwise="/login.htm" redirect="/module/xreports/report.list" />
<openmrs:message var="pageTitle" code="xreports.report.manage.titlebar" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<a href="report.form"><openmrs:message code="xreports.report.add"/></a>

<br/><br/>

<b class="boxHeader"><openmrs:message code="xreports.report.list.title"/></b>
<form method="get" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<th> <openmrs:message code="general.name"/> </th>
			<th> <openmrs:message code="xreports.identifier"/> </th>
		</tr>
	<c:forEach var="report" items="${reports}" varStatus="rowStatus">
		<tr class='${rowStatus.index % 2 == 0 ? "evenRow" : "oddRow" }'>
			<td style="white-space: nowrap">
				<a href="report.form?reportId=<c:out value="${report.reportId}"/>">
					<c:out value="${report.name}"/>
				</a>
			</td>
			<td style="white-space: nowrap">
				<c:out value="${report.identifier}"/>
			</td>
		</tr>
	</c:forEach>
	</table>
</form>

<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>