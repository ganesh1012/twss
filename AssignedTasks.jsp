<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@ include file="IncludesHeader.jsp" %>
    <script>
  function resizeIframe(obj) {
	  var height = obj.contentWindow.document.body.scrollHeight;
    obj.style.height = height+ 'px';    
  }
</script>
</script>
<style>
.label {
  color: white;
  padding: 10px;
  font-family: Arial;
   font-size:15px;
   
}

.info {background-color: #00008B;}

</style>
  </head>
<body class="container body no_margin">



<div class="" style="width:97%;">

<c:if test="${lastPage ne '1' && totalTasks ne '0'}">
		<div class="" style="width: 97%;">
		<table id="assignedGoto"
			class="table table-hover dt-responsive">
			<thead>
					    <th><div  class="pull-right">GoTo: 
					   <select id="pageNum" class="form-control-sm " onchange="gotoSelectedPage(this)" style="width: 50px;">
										    		<c:forEach varStatus="loop" var="page"  begin="1" end="${lastPage}" >							            	
									            		<c:choose>
									            		  <c:when test="${loop.count eq pageNumber }">
									            		 <c:if test="${pageAll ne 'All'}">
									            		  <option value="${loop.count}" selected="selected" label="${loop.count}" />
									            		  </c:if>
									            		  <c:if test="${pageAll eq 'All'}">
									            		  <option value="${loop.count}"  label="${loop.count}" />
									            		  </c:if>
									            		  </c:when>
									            		  <c:otherwise>
									            		    <option value="${loop.count}" label="${loop.count}" />
									            		  </c:otherwise>
									            		</c:choose>
									            	</c:forEach>
									            	<c:if test="${pageAll ne 'All'}">
									            	<option value="All" label="All" />
									            	</c:if>
									            	<c:if test="${pageAll eq 'All'}">
									            	<option value="All" selected="selected" label="All" />
									            	</c:if>
									            </select>
					    </div></th> 
			</thead>
		</table>
	</div>
	</c:if>
	</div>
	
	<div class="" style="width:97%;"><table id="assignTaskSearch" class="table table-hover dt-responsive">
                                 <thead>
									<form action = "PendingTasks?tab=Assigned&page=1" onSubmit="return validateSearch()">
									<th><input type="text" name="raisedBySearchkey" id="raisedBySearchkey" placeholder="Raised By..." value="${searchKeys.raisedBy}"/></th>
									<th><input type="text" name="assignToSearchkey" id="assignToSearchkey" placeholder="Assigned To..." value="${searchKeys.assignedTo}"/></th>
									<th><input type="text" name="severitySearchkey" id="severitySearchkey" placeholder="severity...." value="${searchKeys.severity}"/></th>
									 <th><input type="text" name="AssignedOnSearchkey" id="AssignedOnSearchkey" placeholder="Assigned Date..." value="${searchKeys.assignedOn}" class="requireDatePickerMM" onkeydown="return false"></th>
									<input type="hidden" name="page" id="formPage" value="1">
									<input type="hidden" name="tab" id="tab" value="Assigned">
									<input type="hidden" id="raisedBySearchkey1"/>
									<input type="hidden" id="assignToSearchkey1"/>
									<input type="hidden" id="severitySearchkey1"/>
									<th><input type ="submit" value="Search"/>
											
										 	
										   
										 <c:if test="${(searchKeys.raisedBy ne null || searchKeys.assignedTo ne null|| searchKeys.severity ne null || searchKeys.assignedOn ne null) && 
										(searchKeys.raisedBy ne '' || searchKeys.assignedTo ne '' ||searchKeys.severity ne '' ||searchKeys.assignedOn ne '')}">  
											<a class="pull-right" href="PendingTasks?page=1&tab=Assigned"><i class="fa fa-caret-square-o-right"></i> Back </a>
										</c:if>
										</th>
										</form>  
												<br/>
												</thead>
												</table></div> 
	
	<div>
    <span class="label info">Full Name&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
    &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
    Subject&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
    Severity&emsp;&emsp;Created Date&emsp;&emsp;&emsp;&emsp; </span>
</div><br/>
<div class="accordion" id="accordion" role="tablist" aria-multiselectable="true">
  <c:forEach var="email" items="${listTask}">  
  <c:if test="${email.status eq 'Assigned'}" >                                          
  <div class="panel">
    <a class="panel-heading collapsed" role="tab" id="${email.subject}" data-toggle="collapse" data-parent="#accordion1" href="#${email.id}" aria-expanded="false" aria-controls="${email.id}">
      <h4 class="panel-title">
        <table>
		    <col width="25%" /><col width="25%" /><col width="5%" /><col width="7%" />
		    <tr>
		    	<td>${email.name}</td>
		    	<td>${email.subject}</td>		    	
		    	<td>${email.severity}</td>
		    	<td><fmt:formatDate type="date" value="${email.createdDate}" /></td>		    	
		    </tr>
	    </table>
      </h4>
    </a>
    <div id="${email.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="${email.subject}" aria-expanded="false">
      <div class="panel-body">
      	<div class="row">
      		<div class="col-md-12 col-sm-12 col-xs-12">
      		<div class="x_panel">
      		<div class="x_title">
      			<h2>Task Details</h2>
      			<div class="clearfix"></div>
      		</div>
      		<div class="x_content">										
				<div class="sender-info">
					<div class="row">
						<div class="col-md-6">
							<strong>Raised By: </strong> <span>${email.name}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />
							<br /> <strong>Raised On: </strong> <span><fmt:formatDate type="date" value="${email.createdDate}" /></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<br /><br /><strong>Tracking ID:</strong> <span>${email.trackingID}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<br /><br /><strong>Email ID:</strong> <span>${email.email}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<br/><br/>
						</div>
						<div class="col-md-6">
							<strong>Category: </strong> <span>${email.category}</span><br />
							<br /> <strong>Severity: </strong> <span>${email.severity}</span><br />
							<br /> <strong>phone No: </strong> <span>${email.phoneNo}</span><br />
						</div>
					</div>
				</div>
				<div class="ln_solid"></div>
				<div class="view-mail">

					<p>${email.description}</p>
				</div>
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

				<div class="clearfix"></div>
			</div>
			</div>
			</div>
			</div>
			
			<div class="row">										
				<div class="col-md-6 col-sm-6 col-xs-6">
					<div class="x_panel" style="height:200px;">
						<div class="x_title">
						<h2>New Assignment</h2>
						<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<form:form class="form-horizontal form-label-left" action="assign?${_csrf.parameterName}=${_csrf.token}&usrname=${pageContext.request.userPrincipal.name}&trackingid=${email.trackingID }&raisedUserName=${email.username }" method="post" modelAttribute="taskAssign">						        	
					        		<form:hidden path="taskID" value="${email.id}" />
									<form:hidden path="raisedBy" value="${email.name}" />
									<form:hidden path="tab" value="Assigned" />
								    <form:hidden path="page" value="${pageNumber}" />
								   
									  <form:input type="text" required="required"  class="form-control" id="AfullName${email.id}" path="fullName"  placeholder="Type Employee Name.. " />
									<br/><br/>
								
									<input class="btn btn-success pull-right" type="submit" value="Assign">
							</form:form>
						</div>
					</div>
				</div>										  
				<div class="col-md-6 col-sm-6 col-xs-6">
					<div class="x_panel" style="height:200px;">
						<div class="x_title">
						<h2>Archive Task</h2>
						<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<p>This action cannot be undone. 
							Make sure the issue is completely resolved before archiving this task.
							</p>
							<br/>
							<form:form class="form-horizontal form-label-left" action="archiveTask?${_csrf.parameterName}=${_csrf.token}&usrname=${pageContext.request.userPrincipal.name}" method="post" modelAttribute="taskAssign">						        	
					        		<form:hidden path="taskID" value="${email.id}" />
					        		<form:hidden path="tab" value="Assigned" />
								    <form:hidden path="page" value="${pageNumber}" />
									<input class="btn btn-danger pull-right" type="submit" value="Archive">
							</form:form>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-6 col-sm-6 col-xs-6">
					<div class="x_panel">
						<div class="x_title">
						<h2>Previous Assignments</h2>
						<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<c:forEach var="task" items="${taskAssignList}"> 
					            <c:if test="${task.taskID eq email.id}">
					            	<div class="x_panel tile_no_margin">
										<div class="x_title_modified">
											<c:forEach var="emp" items="${empList}">
									              <c:if test="${task.username eq emp.key}">
									              <h2.small>${emp.value}</h2>
									              </c:if>
								              </c:forEach>
								              <ul class="navbar-right panel_toolbox_single">
												<li><a class="collapse-link"><i class="fa fa-chevron-down"></i></a></li>
											  </ul>
							            </div>
							        	<div class="x_content" style="display:none; padding:5px">
							        		<p>Assigned On: <fmt:formatDate type="date" value="${task.createdDate}" /></p>
							        		<c:forEach var="emp" items="${empList}">
								              <c:if test="${task.createdBy eq emp.key}">
								              	<p>Assigned By: ${emp.value}</p>
								              </c:if>
								            </c:forEach>
								            <br>
							        		<p>Start Date: <fmt:formatDate type="date" value="${task.startDate}" /></p>
							        		<p>End Date: <fmt:formatDate type="date" value="${task.endDate}" /></p>
							        		<p>Due Date: <fmt:formatDate type="date" value="${task.dueDate}" /></p>
							        		<p>Estimated Time: <fmt:formatNumber type="number" value="${task.estimatedTime}" /></p>
							        		<p>Actual Time: <fmt:formatNumber type="number" value="${task.actualTime}" /></p>
							        		<br/>
							        		<button class="btn btn-xs btn-primary" onclick="showExtendForm_${email.id}()">Extend Due Date</button>
							        		<br/>
							        		<div id="extensionDate_${email.id}" style="display:none;">
							        		<br/>							        		
								        		<form:form action="admin/updateDueDate?${_csrf.parameterName}=${_csrf.token}" method="POST" modelAttribute="taskAssign">
								        		<form:hidden path="taskID" value="${task.taskID}" />
												<form:hidden path="username" value="${task.username}" />
												<form:hidden path="tab" value="Assigned" />
								                <form:hidden path="page" value="${pageNumber}" />
								        		<label class="form-label-left">New Due Date:</label>
								        		<form:input path="dueDate" required="required" class="form-control requireDatePicker"  onkeydown="return false"/>
								        		<br/>
								        		<input class="btn btn-success btn-xs pull-right" value="Update" type="submit" />
								        		</form:form>
							        		</div> 
							        	</div>
							        	<script type="text/javascript">
							        	function showExtendForm_${email.id}(){
							        		var elementID = '<c:out value="${email.id}"/>';
							        		elementID = "extensionDate_"+elementID;
							        		document.getElementById(elementID).style.display="block";
							        	}
							        	</script>
						        	</div>
					            </c:if>
					        </c:forEach>								        
				        
						
						</div>
					</div>
				</div>
				
				<div class="col-md-6 col-sm-6 col-xs-6">
					<div class="x_panel">
						<div class="x_title">
						<h2>Chat Box</h2>
						<div class="clearfix"></div>
						</div>
						<div class="x_content">
							
							<c:if test="${email.username ne pageContext.request.userPrincipal.name}" >
							<h4>Created By:</h4>
								<div class="x_panel tile_no_margin">
									<div class="x_title_modified">
										<c:forEach var="emp" items="${empList}">
							            <c:if test="${email.username eq emp.key}">
							            <h2.small>${emp.value}</h2>
							            </c:if>
							            </c:forEach>										
										<ul class="navbar-right panel_toolbox_single">
											<li><a class="collapse-link"><i class="fa fa-chevron-down"></i></a></li>
										</ul>
									</div>
									<div class="x_content" style="display: none; padding: 5px">										
										<c:forEach var="msg" items="${taskCommList}">
											<c:if test="${msg.taskID eq email.id}">
											<c:choose>
											<c:when test="${msg.from eq email.username}">
												<c:if test="${msg.to eq pageContext.request.userPrincipal.name}">
													<p><strong>Emp: </strong> ${msg.message}</p>
												</c:if>
											</c:when>
											<c:when test="${msg.from eq pageContext.request.userPrincipal.name}">
												<c:if test="${msg.to eq email.username}">
													<p><strong>You: </strong> ${msg.message}</p>
												</c:if>
											</c:when>
											</c:choose>
											</c:if>
										</c:forEach>
										<form:form class="form-horizontal form-label-left" action="taskComm?${_csrf.parameterName}=${_csrf.token}&usrname=${pageContext.request.userPrincipal.name}" method="post" modelAttribute="taskMessage">						        	
							        		<form:hidden path="taskID" value="${email.id}" />
											<form:hidden path="from" value="${pageContext.request.userPrincipal.name}" />
											<form:hidden path="to" value="${message.username}" />
											<form:input path="message" size="48" required="required" class="form-control"></form:input>
											<br/>
											<input class="btn btn-success pull-right" type="submit" value="Send">
										</form:form>
									</div>				
								</div>
								</c:if>
								<h4>Assigned To:</h4>
								<c:forEach var="message" items="${taskAssignList}">
								<c:if test="${message.taskID eq email.id}" >
								
								<c:if test="${message.createdBy eq pageContext.request.userPrincipal.name}" >
								<c:if test="${message.username ne pageContext.request.userPrincipal.name}">
								<div class="x_panel tile_no_margin">
									<div class="x_title_modified">
										<c:forEach var="emp" items="${empList}">
							            <c:if test="${message.username eq emp.key}">
							            <h2.small>${emp.value}</h2>
							            </c:if>
							            </c:forEach>										
										<ul class="navbar-right panel_toolbox_single">
											<li><a class="collapse-link"><i class="fa fa-chevron-down"></i></a></li>
										</ul>
									</div>
									<div class="x_content" style="display: none; padding: 5px">										
										<c:forEach var="msg" items="${taskCommList}">
											<c:if test="${msg.taskID eq email.id}">
											<c:choose>
											<c:when test="${msg.from eq message.username}">
												<c:if test="${msg.to eq pageContext.request.userPrincipal.name}">
													<p><strong>Emp: </strong> ${msg.message}</p>
												</c:if>
											</c:when>
											<c:when test="${msg.from eq pageContext.request.userPrincipal.name}">
												<c:if test="${msg.to eq message.username}">
													<p><strong>You: </strong> ${msg.message}</p>
												</c:if>
											</c:when>
											</c:choose>
											</c:if>
										</c:forEach>
										<form:form class="form-horizontal form-label-left" action="taskComm?${_csrf.parameterName}=${_csrf.token}&usrname=${pageContext.request.userPrincipal.name}" method="post" modelAttribute="taskMessage">						        	
							        		<form:hidden path="taskID" value="${email.id}" />
											<form:hidden path="from" value="${pageContext.request.userPrincipal.name}" />
											<form:hidden path="to" value="${message.username}" />
											<form:input path="message" size="48" required="required" class="form-control"></form:input>
											<br/>
											<input class="btn btn-success pull-right" type="submit" value="Send">
										</form:form>
									</div>				
								</div>
								</c:if>
								</c:if>
								</c:if>
							</c:forEach> 
						</div>
					</div>
				</div>										
			</div>
									        	
     	</div>
    </div>
  </div>
  </c:if>
  </c:forEach>
 <c:if test="${totalTasks eq '0'}">
		
			<p><center><font face="verdana" size="3" color="#A94442">No records found</font></center></p></c:if> 
</div>

 <div class="" style="width:97%;"><c:if test="${totalTasks ne '0'}">
												<table id="assignedTable" class="table table-hover dt-responsive"><thead>
												<th class="pull-left"><i class="fa fa-caret-square-o-left">
												<c:choose>
													<c:when test="${totalTasks eq '0'}">
														<h5>Showing 0 of 0 Entries</h5>
													</c:when>
													<c:when test="${pageAll eq 'All'}">
									                     <h5>Showing 1-${totalTasks} of ${totalTasks} Entries</h5>
								                    </c:when>
													<c:otherwise>
														<c:if test="${lastPage ne '1'}">
														<c:if test="${pageNumber ne lastPage}"><h5>Showing ${(pageNumber-1)*paginationDefault+1}-${pageNumber*paginationDefault} of ${totalTasks} Entries</h5></c:if>
														<c:if test="${pageNumber eq lastPage}"><h5>Showing ${(pageNumber-1)*paginationDefault+1}-${totalTasks} of ${totalTasks} Entries</h5></c:if></c:if>
														<c:if test="${lastPage eq '1'}"><h5>Showing ${listSize} of ${totalTasks} Entries</h5></c:if>
													</c:otherwise>
												</c:choose>
												</th>
												<!-- <th></th><th></th><th></th> -->
												<c:if test="${pageAll ne 'All'}">
												<c:if test="${totalTasks ne '0'}">
												<th class="pull-right"><i class="fa fa-caret-square-o-right">
												<div class="pagination" oncontextmenu="return false;" onmousedown="return false;" onmouseup="return false;">
												
												<c:if test="${pageNumber gt '1'}"> 
													<c:set var="oldPage" value="${pageNumber - 1}" />
													<li><a href="PendingTasks?tab=Assigned&page=${oldPage}&raisedBySearchkey=${searchKeys.raisedBy}&severitySearchkey=${searchKeys.severity}&assignToSearchkey=${searchKeys.assignedTo}&createdDateSearchkey=${searchKeys.assignedOn}"> Prev </a></li>
												</c:if>
												
												<c:if test="${lastPage ne '1'}" >
												
												<li><a href="PendingTasks?tab=Assigned&page=${pageNumber}&raisedBySearchkey=${searchKeys.raisedBy}&severitySearchkey=${searchKeys.severity}&assignToSearchkey=${searchKeys.assignedTo}&createdDateSearchkey=${searchKeys.assignedOn}">${pageNumber }</a></li>
												</c:if>
							
												<c:if test="${pageNumber lt lastPage}" > 
												<c:if test="${lastPage ne '1'}" >
													<c:set var="newPage" value="${pageNumber + 1}" />
													<li><a href="PendingTasks?tab=Assigned&page=${newPage}&raisedBySearchkey=${searchKeys.raisedBy}&severitySearchkey=${searchKeys.severity}&assignToSearchkey=${searchKeys.assignedTo}&createdDateSearchkey=${searchKeys.assignedOn}">  Next </a> </li>
												</c:if></c:if>
											
												</div></th>
												</c:if>
												</c:if>
											
										</thead>
									</table>
									</c:if>
							</div>
      </div>
        
    <%@ include file="IncludesFooter.jsp" %>
  <script src="resources/js/messageframe.js"></script>
    <script src="resources/vendors/devbridge-autocomplete/dist/jquery.autocomplete.min.js"></script>
    <script>
    
    function gotoSelectedPage(obj){
		var pageNum = obj.value;
		window.location.href = "PendingTasks?tab=Assigned&page="+pageNum+"&raisedBySearchkey=${searchKeys.raisedBy}&assignToSearchkey=${searchKeys.assignedTo}&createdDateSearchkey=${searchKeys.assignedOn}";
	}
    function validateSearch(){
		 var raisedBySearchkey = $.trim($('#raisedBySearchkey').val());
		 var assignToSearchkey = $.trim($('#assignToSearchkey').val());
		 var AssignedOnSearchkey = $.trim($('#AssignedOnSearchkey').val());
		 var severitySearchkey = $.trim($('#severitySearchkey').val());
		 if(raisedBySearchkey=="" && assignToSearchkey=="" && AssignedOnSearchkey=="" && severitySearchkey==""){
			 alert("Please enter value in any search fields")
			 return false;
		 }else{
			 return true;
		 }
		 return true;
	 }
    
    </script>
    <script>
        $(function(){        	
        	
        	
        	var employees  = {
        			<c:forEach var="emp" items="${empList}">        		
        			 "${emp.key}":"${emp.value}",        		        		
        			</c:forEach>
        			 "NONE":" "
        			};
        	var employeeArray = $.map(employees, function(value, key) {
                return {value: value, data: key};
              });
        	
        	 <c:forEach var="email" items="${listTask}">  
        	
        	var c="${email.id}";
        	
              $('#AfullName'+c).autocomplete({
                  lookup: employeeArray,
                  onSelect: function(){
                  	var selectedEmployee = $('#AfullName'+c).val();
              		$(employeeArray).each(function(index){
                      	if(selectedEmployee == employeeArray[index].value)                    		
                      		$('#AfullNameId'+c).val(employeeArray[index].data);                    	
                      });
                  }
              });
              
              </c:forEach>
           
                    })
        
        </script> 
         
      <script>
        $(function(){        	
        	
        	
        	var names  = {
        			<c:forEach var="raise" items="${raiseByMap}">        		
        			 "${raise.key}":"${raise.value}",        		        		
        			</c:forEach>
        			 "NONE":" "
        			};
        	var namesArray = $.map(names, function(value, key) {
                return {value: value, data: key};
              });
        	
        	var assignTo  = {
        			<c:forEach var="assign" items="${assignedToMap}">        		
        			 "${assign.key}":"${assign.value}",        		        		
        			</c:forEach>
        			 "NONE":" "
        			};
        	var assignArray = $.map(assignTo, function(value, key) {
                return {value: value, data: key};
              });
        	
        	
        	var severity  = {
         			<c:forEach var="sub2" items="${severityMap}">        		
         			 "${sub2.key}":"${sub2.value}",        		        		
         			</c:forEach>
         			 "NONE":" "
         			};
         	var subArray2 = $.map(severity, function(value, key) {
                 return {value: value, data: key};
               });
        	
        	
              $('#raisedBySearchkey').autocomplete({
                lookup: namesArray,
                onSelect: function(){
                	var selectedName = $('#raisedBySearchkey').val();
                
            		$(namesArray).each(function(index){
                    	if(selectedName == namesArray[index].value)                    		
                    		$('#raisedBySearchkey1').val(namesArray[index].data);
                    });
                }
            });
              $('#assignToSearchkey').autocomplete({
                  lookup: assignArray,
                  onSelect: function(){
                  	var selectedAssign = $('#assignToSearchkey').val();
                  
              		$(assignArray).each(function(index){
                      	if(selectedAssign == assignArray[index].value)                    		
                      		$('#assignToSearchkey1').val(assignArray[index].data);
                      });
                  }
              });
              
              $('#severitySearchkey').autocomplete({
                  lookup: subArray2,
                  onSelect: function(){
                  	var selectedsub2 = $('#severitySearchkey').val();
                  
              		$(subArray2).each(function(index){
                      	if(selectedsub1 == subArray2[index].value)                    		
                      		$('#severitySearchkey1').val(subArray2[index].data);
                      });
                  }
              });
        })
        </script>
 <script>
 $(function(){ 
  var cdate = moment().utcOffset('-0600', true);
  $('.requireDatePickerMM').daterangepicker({
  singleDatePicker : true,
  showDropdowns : true,
  format : "YYYY-MM-DD",
 	
  calender_style : "picker_3"
 }); 
 })
  </script>
 </body>

 </html>