<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Analyst,Data Manager" otherwise="/login.htm" redirect="/module/xreports/reportParameter.form" />

<openmrs:message var="pageTitle" code="report.titlebar" arguments="${reportName}" scope="page"/>

<%@ include file="/WEB-INF/template/header.jsp" %>

<br/>

<center>
<form id="parameterForm" method="post" action="reportParameter.form" autocomplete="off">
<fieldset>
<legend><openmrs:message code="reportParameter.title" arguments="${reportName}"/></legend>
	<table class="unstyled">
		<c:forEach items="${parameters}" var="parameter">
			<tr>
				<td>${parameter.name}</td>
				<td>
					<c:if test="${fn:length(parameter.values) gt 0}">
						<select name="${parameter.binding}">
					        <c:forEach items="${parameter.values}" var="value">
					        	<option value="${value.value}">
					        		${value.name}
					        	</option>
					        </c:forEach>
						</select>
					</c:if>
					<c:if test="${fn:length(parameter.values) == 0}">
						<input type="text" name="${parameter.binding}" value=""/>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</table>

	<br/>
	
	<input type="hidden" name="reportId" value="${reportId}"/>
	<input type="hidden" name="parameterNames" value="${parameterNames}"/>
	
	<input class="smallButton" style="float:left" type="submit" value="<openmrs:message code="general.ok"/>">
	<input class="smallButton" style="float:right" type="button" value="<openmrs:message code="general.cancel"/>" onclick="document.location.href='index.htm';">

</fieldset>
</form>
</center>

<script type="text/javascript">
 	document.forms[0].elements[1].focus();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>