<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Meta, title, CSS, favicons, etc. -->
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<%@ include file="IncludesHeader.jsp"%>
</head>
<body class="container body no_margin scrollable-content">
<form:form id="edit_task" class="form-horizontal form-label-left"
	action="assignTask?${_csrf.parameterName}=${_csrf.token}&username=${pageContext.request.userPrincipal.name}&fromModal=false"
	method="POST" modelAttribute="Task" enctype="multipart/form-data"> 
	<form:hidden path="id" value="${email.id}"/>  
	<input type="hidden" name="page" id="formPage" value="1">
	<input type="hidden" name="tab" id="tab" value="Created">
	<div class="main_container no_margin">
		<div class="right_col no_margin" >
		<div class="col-md-9 col-sm-9 col-xs-11 mail_view">
<div class="form-group">
		<label class="control-label">Summary <span class="required"><font color="red">*</font></span></label>
		<form:input path="subject" class="resizable_textarea form-control"
			required="required" value="${email.subject}"/>
	</div>
	<%-- <div class="form-group" id="element1">
		<label class="control-label">Attachment</label>
		<form:input class="btn uploadTaskAttachment" type="file"
			path="attachment1" name="attachment" />
		<a class="btn" id="add_more_1">Add more</a>
	</div>

	<div class="form-group add_more_attachments" id="element2">
		<label class="control-label">Attachment 2: </label>
		<form:input class="btn uploadTaskAttachment" type="file"
			path="attachment2" name="attachment2" />
		<a class="btn" id="add_more_2">Add more</a>
	</div>

	<div class="form-group add_more_attachments" id="element3">
		<label class="control-label">Attachment 3: </label>
		<form:input class="btn uploadTaskAttachment" type="file"
			path="attachment3" name="attachment3" />
	</div> --%>
	
	<div class="attachment">
		<c:if test="${not empty email.fileName1}">
			<a class="btn btn-xs btn-info view-att1"
				href="viewTaskAttachment?id=${email.id}&fileId=1&embedded=true"
				data-docname="${email.fileName1}"
				data-filetype="${email.fileType1}">${email.fileName1}</a>
		</c:if>
		<c:if test="${not empty email.fileName2}">
			<a class="btn btn-xs btn-info view-att2"
				href="viewTaskAttachment?id=${email.id}&fileId=2&embedded=true"
				data-docname="${email.fileName2}"
				data-filetype="${email.fileType2}">${email.fileName2}</a>
		</c:if>
		<c:if test="${not empty email.fileName3}">
			<a class="btn btn-xs btn-info view-att3"
				href="viewTaskAttachment?id=${email.id}&fileId=3&embedded=true"
				data-docname="${email.fileName3}"
				data-filetype="${email.fileType3}">${email.fileName3}</a>
		</c:if>
	</div>



	<div class="form-group">
		<label class="control-label">Description</label>
		<form:textarea class="resizable_textarea form-control" id="description_${email.id}"
			path="description" placeholder=""
			style="overflow: hidden; word-wrap: break-word; resize: horizontal; height: 150px;"></form:textarea>
		<%-- <%@ include file="TextEditorWithControlsp" %> --%>
	</div>

	

	<div class="form-group">
		<input class="btn btn-success" type="submit" value="Assign" />&nbsp;&nbsp;
<!-- 		<input class="btn btn-danger" type="button" value="Archive" onclick="taskArchive()">
 -->	</div>


	</div>
	<div class="col-sm-3 mail_list_column" style="border:solid">
											   
	<div class="form-group">
		<label for="project">Project <span class="required">*</span></label>
		<form:select class="form-control" id="propertyType" path="projectID">
			<form:option value="all" label="-----Select-----" />
			<c:forEach var="option" items="${userProjects}">
				<c:choose>
					<c:when test="${email.projectID eq option.projectID}">
						<option value="${option.projectID}" selected="true">${option.projectName}</option>
					</c:when>
					<c:otherwise>
						<form:option value="${option.projectID}" label="${option.projectName}" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
	</div>
		
	<div class="form-group">
		<label for="Status">Status </label>
		<label for="toDo" class="form-control" style="bacground-color:red" ><span style="color:blue;font-weight:bold">${email.status}</span></label>
		
	</div>
	<div class="form-group">
			<%-- <form:hidden path="taskID" value="${email.id}" />
			<form:hidden path="raisedBy" value="${email.name}" />
			<form:hidden path="tab" value="Created" />
			<form:hidden path="page" value="${pageNumber}" /> --%>
			<form:input type="text" required="required"  class="form-control" id="fullName${email.id}" path="fullName" name="fullName1"  placeholder="Type Assignee Name.. " />
			<br/>
<%-- 			<form:input type="text" required="required" class="form-control requireDatePicker" path="dueDate" placeholder="Due Date" onkeydown="return false"/> 
 --%>			<br/>
	</div>
	<div class="form-group">
		<label for="category">Reporter: </label>
		<input id="reporter" class="form-control" value="${email.name}" readonly="readonly"/>
	</div>
	<div class="form-group">
		<label for="category">Category: </label>
		<form:select class="form-control" id="category" path="category">
			 <form:option selected="selected" value="${email.category}">${email.category}</form:option>
 				 <c:forEach var="cat" items="${listCatogery}">
				 	 <c:if test="${email.category ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
				 </c:forEach>
		</form:select>
	</div>
	<div class="form-group">
		<label for="priority">Priority: </label>
		<form:select class="form-control" id="priority" path="priority">
			 <form:option selected="selected" value="${email.priority}">${email.priority}</form:option>
 				 <c:forEach var="priority" items="${listtaskPriority}">
				 	 <c:if test="${email.priority ne 'Low'}" >
				 	 	<form:option value="${priority}" label="${priority}" />
				 	 </c:if>
				 </c:forEach>
		</form:select>
	</div>
	
<%-- 	<form:hidden path="category" id="selectedProjectCategory" />	
 --%>	
 	
	<div class="form-group">
		<label for="severity">Severity: </label>
		<form:select class="form-control" id="severity" path="severity">
				 <form:option selected="selected" value="${email.severity}">${email.severity}</form:option>
 				 <c:forEach var="cat" items="${listSeverity}">
				 	 <c:if test="${email.severity ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
				 </c:forEach>
				
		</form:select>
	</div>
	<div class="form-group">
		<label for="parent">Parent: </label>
		<form:select class="form-control" id="parent" path="parent">
			<form:option value="">-----Select-----</form:option>
			<c:forEach var="taskDetails" items="${parentList}">
				<form:option value="${taskDetails.id}" label="${taskDetails.subject}" />
			</c:forEach>
		</form:select>
		
	</div>
	<div class="form-group">
		<label for="environment">Environment: </label>
		<form:select class="form-control" id="environment" path="environment">
		 <form:option selected="selected" value="${email.environment}">${email.environment}</form:option>
		     <c:forEach var="cat" items="${listEnvoronment}">
				 	 <c:if test="${email.environment ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
			</c:forEach>
			
		</form:select>
	</div>
	<div class="form-group">
		<label for="Issue">Issue Type: </label>
		<form:select class="form-control" id="issueType" path="issueType">
		  <form:option selected="selected" value="${email.issueType}">${email.issueType}</form:option>
		  <c:forEach var="cat" items="${listissueType}">
				 	 <c:if test="${email.issueType ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
			</c:forEach>
		</form:select>
		
	</div>
	<div class="form-group">
		<label for="Estimate">Original Estimate <span class="required">*</span></label>
		<form:input id="originalEstimate" path="originalEstimate" class="form-control"/>
		
	</div>
	<div class="form-group">
		<label for="timeConsumed">Time Spent</span></label>
		<form:input id="timeConsumed" path="timeConsumed" class="form-control"/>
	</div>
		<div class="form-group">
		<label for="timeRequired">Time Required</span></label>
		<form:input id="timeRequired" path="timeRequired" class="form-control"/>
	</div>
	<div class="form-group">
		<label for="FoundInVersion">Found in Version <span class="required">*</span></label>
		<form:select class="form-control" id="foundInVersion" path="foundInVersion">
		  <form:option selected="selected" value="${email.foundInVersion}">${email.foundInVersion}</form:option>
		 <c:forEach var="cat" items="${listtaskFoundInVersion}">
				 	 <c:if test="${email.category ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
			</c:forEach>
		</form:select>
		
	</div>
	<div class="form-group">
		<label for="FixedInVersion">Fixed in Version : </label>
		<form:select class="form-control" id="fixedInVersion" path="fixedInVersion">		  
		<form:option selected="selected" value="${email.fixedInVersion}">${email.fixedInVersion}</form:option>
		
		 	<c:forEach var="cat" items="${listtaskFixedVesrion}">
				 	 <c:if test="${email.fixedInVersion ne cat}" >
				 	 	<form:option value="${cat}" label="${cat}" />
				 	 </c:if>
			</c:forEach>
		</form:select>
		
	</div>
	
	<div class="form-group">
		<label for="createdDate">Created Date</span></label>
		<form:input id="createdDate" path="createdDate" class="form-control" readonly="true" value="${email.createdDate }"/>
	</div>
	<div class="form-group">
		<form:input type="text" required="required" class="form-control requireDatePicker" path="dueDate" placeholder="Due Date" onkeydown="return false"/> 
	</div>
	<div class="form-group">
		<label for="createdBy">Created By</span></label>
		<form:input id="createdBy" path="createdBy" class="form-control"  readonly="true" value="${email.createdBy}"/>
	</div>
	
		
		
	
	
	<%-- <div class="form-group">
		<label for="name">Name <span class="required"><font color="red">*</font></span></label>
		<form:input id="name" size="48" path="name" required="required" readonly="true"
			style="background:white" class="form-control col-md-7 col-xs-12" />
	</div> --%>
	<div class="form-group">
		<form:hidden id="name" size="48" path="name"/>
		<label for="name">Email <span class="required"><font color="red">*</font></span></label>
		<form:input id="emailId" size="48" path="email" required="required" readonly="true"
			style="background:white" class="form-control col-md-7 col-xs-12" value="${email.email}"/>
	</div>

		</div>
	</div>
	</form:form>
	<%@ include file="IncludesFooter.jsp"%>
	<script src="resources/js/messageframe.js"></script>
</body>
<script type="text/javascript">
$(function() {
	//alert("${email.id}");
	$("#description_${email.id}").val("${email.description}");
	
}
);

function taskArchive(){
	alert("task Archive");
	alert($('#edit_task').attr('action'));
	var action = $('#edit_task').attr('action');
	action = action.replace("assignTask","taskArchive");
	alert(action)
	$('#edit_task').attr('action', action);
	$('#edit_task').submit();
	
}
</script>
</html>