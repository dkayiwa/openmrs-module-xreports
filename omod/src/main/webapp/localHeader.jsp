<c:if test="${param.hideHeader != 'true'}">
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
		<openmrs:extensionPoint pointId="org.openmrs.configs.localHeader" type="html">
				<c:forEach items="${extension.links}" var="link">
					<li <c:if test="${fn:endsWith(pageContext.request.requestURI, link.key)}">class="active"</c:if> >
						<a href="${pageContext.request.contextPath}/${link.key}"><openmrs:message code="${link.value}"/></a>
					</li>
				</c:forEach>
		</openmrs:extensionPoint>
	</ul>
</c:if>