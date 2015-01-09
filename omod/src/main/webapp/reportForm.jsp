<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Manager" otherwise="/login.htm" redirect="/module/xreports/report.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<br/>
<spring:hasBindErrors name="report">
	<openmrs:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<openmrs:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
</spring:hasBindErrors>

<b class="boxHeader"><openmrs:message code="xreports.report.add.title"/></b>
<form id="reportForm" method="post" action="report.form" autocomplete="off" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<td><openmrs:message code="general.name"/></td>
			<td>
				<spring:bind path="report.name">
					<input type="text" name="${status.expression}" value="${status.value}" size="50"/>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><openmrs:message code="xreports.identifier"/></td>
			<td>
				<spring:bind path="report.identifier">
					<input type="text" name="${status.expression}" value="${status.value}" size="50"/>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><openmrs:message code="xreports.report.group"/></td>
			<td>
				<spring:bind path="report.group">
					<select name="${status.expression}" style="width:50%">
						<option></option>
						<c:forEach items="${groups}" var="group">
				        	<option value="${group.groupId}" <c:if test="${group.groupId == status.value}">selected="selected"</c:if>>
				        		${group.name}
				        	</option>
				        </c:forEach>
					</select>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><openmrs:message code="xreports.reportDefinition"/></td>
			<td>
				<spring:bind path="report.externalReportUuid">
					<select name="${status.expression}" style="width:50%">
						<option></option>
						<c:forEach items="${reportDefinitions}" var="reportDefinition">
				        	<option value="${reportDefinition.uuid}" <c:if test="${reportDefinition.uuid == status.value}">selected="selected"</c:if>>
				        		${reportDefinition.name}
				        	</option>
				        </c:forEach>
					</select>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		
		<c:if test="${param.reportId != null}">
			<tr>
				<td><openmrs:message code="xreports.design"/></td>
				<td>
					<input class="smallButton" type="button" value="..." onclick="document.location.href='${pageContext.request.contextPath}/module/xreports/reportDesigner.form?reportId=${param.reportId}';"/>
				</td>
			</tr>
		</c:if>
		
		<br/>
		<tr>
			<td>
				<input class="smallButton" style="float:left" type="submit" value="<openmrs:message code="general.save"/>">
			</td>
			<c:if test="${param.reportId != null}">
				<td>
					<input type="hidden" name="reportId" value="${param.reportId}"/>
					<input class="smallButton" type="submit" name="action" value="<openmrs:message code="general.delete"/>" onclick="return confirm('<openmrs:message code="xreports.report.delete.confirm" />')">
				</td>
			</c:if>
			<td>
				<input class="smallButton" style="float:right" type="button" value="<openmrs:message code="general.close"/>" onclick="document.location.href='report.list';">
			</td>
		</tr>
	</table>
	
</form>

<script type="text/javascript">
 	document.forms[0].elements[1].focus();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>