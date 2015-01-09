<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Data Manager" otherwise="/login.htm" redirect="/module/xreports/group.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<br/>
<spring:hasBindErrors name="group">
	<openmrs:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<openmrs:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
</spring:hasBindErrors>

<b class="boxHeader"><openmrs:message code="xreports.reportGroup.add.title"/></b>
<form id="reportGroupForm" method="post" action="group.form" autocomplete="off" class="box">
	<table cellpadding="2" cellspacing="0">
		<tr>
			<td><openmrs:message code="general.name"/></td>
			<td>
				<spring:bind path="group.name">
					<input type="text" name="${status.expression}" value="${status.value}" size="50"/>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><openmrs:message code="xreports.identifier"/></td>
			<td>
				<spring:bind path="group.identifier">
					<input type="text" name="${status.expression}" value="${status.value}" size="50"/>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td><openmrs:message code="xreports.reportGroup.parentGroup"/></td>
			<td>
				<spring:bind path="group.parentGroup">
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
			<td>
				<input class="smallButton" type="submit" value="<openmrs:message code="general.save"/>">
			</td>
			<c:if test="${param.groupId != null}">
				<td>
					<input type="hidden" name="groupId" value="${param.groupId}"/>
					<input class="smallButton" type="submit" name="action" value="<openmrs:message code="general.delete"/>" onclick="return confirm('<openmrs:message code="xreports.reportGroup.delete.confirm" />')">
				</td>
			</c:if>
			<td>
				<input class="smallButton" type="button" value="<openmrs:message code="general.close"/>" onclick="document.location.href='group.list';">
			</td>
		</tr>
	</table>
	
	
</form>

<script type="text/javascript">
 	document.forms[0].elements[1].focus();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>