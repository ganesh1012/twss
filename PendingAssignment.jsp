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
<style>
.label {
  color: white;
  padding: 15px;
  font-family: Arial;
   font-size:15px;
  
}

.info {background-color: #00008B;}

</style>
  </head>
  <sec:authorize access="hasRole('ROLE_MANAGER') or hasRole('ROLE_SUPERUSER')">
<body class="container body no_margin">



<div class="" style="width:97%;">

<c:if test="${lastPage ne '1' && totalTasks ne '0'}">
		<div class="" style="width: 97%;">
		<table id="pendingGoto"
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

<div class="" style="width:97%;"><table id="MaPendingTasksearch" class="table table-hover dt-responsive">
                                 <thead>
									<form action = "MaPendingTasks?tab=Created&page=1" onSubmit="return validateSearch()">
									<th><input type="text" name="raisedBySearchkey" id="raisedBySearchkey" placeholder="Raised By..." value="${searchKeys.raisedBy}"/></th>
									<th><input type="text" name="subjectSearchkey" id="subjectSearchkey" placeholder="subject..." value="${searchKeys.subject}"/></th>
									<th><input type="text" name="categorySearchkey" id="categorySearchkey" placeholder="category..." value="${searchKeys.category}"/></th>
									<th><input type="text" name="severitySearchkey" id="severitySearchkey" placeholder="severity...." value="${searchKeys.severity}"/></th>
									<th><input type="text" name="createdDateSearchkey" id="createdDateSearchkey" placeholder="Created Date..." value="${searchKeys.createdDate}" class="requireDatePickerMM" onkeydown="return false"></th>
									<input type="hidden" name="page" id="formPage" value="1">
									<input type="hidden" name="tab" id="tab" value="Created">
									<input type="hidden" id="raisedBySearchkey1"/>
									<input type="hidden" id="subjectSearchkey1"/>
									<input type="hidden" id="categorySearchkey1"/>
									<input type="hidden" id="severitySearchkey1"/>
									<th><input type ="submit" value="Search"/>
											
										 	
										 <c:if test="${(searchKeys.raisedBy ne null || searchKeys.subject ne null || searchKeys.category ne null || searchKeys.severity ne null || searchKeys.createdDate ne null) && 
										(searchKeys.raisedBy ne '' || searchKeys.subject ne ''|| searchKeys.createdDate ne '' || searchKeys.severity ne '' || searchKeys.category ne '')}">   
										
											<a class="pull-right" href="MaMaPendingTasks?page=1&tab=Created"><i class="fa fa-caret-square-o-right"></i> Back </a>
										</c:if>
										</th>
										</form>  
												<br/>
												</thead>
												</table></div> 



	<div>
<!-- <span class="label info">Subject&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Category&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Severity&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Created Date&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;Full Name&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; </span>
 -->
   <span >  <table style="width:100%;text-shadow: rgba(0, 0, 0, 0.25) 0 -1px 0;
    background: #000073;
    background-image: initial;
    background-position-x: initial;
    background-position-y: initial;
    background-size: initial;
    background-repeat-x: initial;
    background-repeat-y: initial;
    background-attachment: initial;
    background-origin: initial;
    background-clip: initial;
    background-color: rgb(0, 0, 115);
    box-shadow: rgba(0, 0, 0, 0.25) 0 1px 0, inset rgba(255, 255, 255, 0.16) 0 1px 0;color:#FFFFFF">
		    <col width="15%" /><col width="7%" /><col width="7%" /><col width="7%" /><col width="10%" />
		    <tr>
		    	<td>Subject</td>
		    	<td>Category</td>
		    	<td>Severity</td>
		    	<td>Created Date</td>
		    	<td>Name</td>
		    </tr>
	    </table></span>
 
 </div><br/>
<div class="accordion" id="accordion" role="tablist" aria-multiselectable="true">


  <c:forEach var="email" items="${listTask}">  
  <c:if test="${email.status eq 'Created'}" >                                          
  <div class="panel">
    <a class="panel-heading collapsed" role="tab" id="${email.subject}" data-toggle="collapse" data-parent="#accordion1" href="#${email.id}" aria-expanded="false" aria-controls="${email.id}">
      <h4 class="panel-title">
        <table>
		    <col width="15%" /><col width="7%" /><col width="7%" /><col width="7%" /><col width="10%" />
		    <tr>
		    	<td>${email.subject}</td>
		    	<td>${email.category}</td>
		    	<td>${email.severity}</td>
		    	<td><fmt:formatDate type="date" value="${email.createdDate}" /></td>
		    	<td>${email.name}</td>
		    </tr>
	    </table>
      </h4>
    </a>
    <div id="${email.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="${email.subject}" aria-expanded="false">
      <div class="panel-body">
      	<div class="row">
      		<div class="col-md-12 col-sm-12 col-xs-12">
      		<div class="x_panel">
      		<%@ include file="ManagerEditTask.jsp"%>
      		
      		<%-- <div class="x_title">
      			<h2>Task Details</h2>
      			<div class="clearfix"></div>
      		</div>
      		<div class="x_content">										
				<div class="sender-info">
					<div class="row">
						<div class="col-md-6">
							<strong>Raised By: </strong> <span>${email.name}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />
							<br /> <strong>Date: </strong> <span>${email.createdDate}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
			</div> --%>
			</div>
			</div>
			</div>
			
			<%-- <div class="row">										
				<div class="col-md-6 col-sm-6 col-xs-6">
					<div class="x_panel" style="height:250px;">
						<div class="x_title">
						<h2>New Assignment</h2>
						<div class="clearfix"></div>
						</div>
						<div class="x_content">
							<form:form class="form-horizontal form-label-left" action="assign?${_csrf.parameterName}=${_csrf.token}&usrname=${pageContext.request.userPrincipal.name}&trackingid=${email.trackingID }&raisedUserName=${email.username }" method="post" modelAttribute="taskAssign">						        	
					        		<form:hidden path="taskID" value="${email.id}" />
									<form:hidden path="raisedBy" value="${email.name}" />
									<form:hidden path="tab" value="Created" />
								    <form:hidden path="page" value="${pageNumber}" />
									  <form:input type="text" required="required"  class="form-control" id="fullName${email.id}" path="fullName" name="fullName1"  placeholder="Type Employee Name.. " />
									<br/>
									<form:input type="text" required="required" class="form-control requireDatePicker" path="dueDate" placeholder="Due Date" onkeydown="return false"/> 
									<br/>
									
									<input class="btn btn-success pull-right" type="submit" value="Assign">
							</form:form>
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
							<br/>
								<c:if test="${email.username ne pageContext.request.userPrincipal.name}" >
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
												
													<p><strong>Emp: </strong> ${msg.message}</p>
												
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
											<form:hidden path="to" value="${email.username}" />
											<form:input path="message" size="48" required="required" class="form-control"></form:input>
											<br/>
											<input class="btn btn-success pull-right" type="submit" value="Send">
										</form:form>
									</div>				
								</div>
								</c:if>
						</div>
					</div>
				</div>
				
			</div> --%>
									        	
     	</div>
    </div>
  </div>
  
  </c:if>
  </c:forEach>
  <c:if test="${totalTasks eq '0'}">
		
			<p><center><font face="verdana" size="3" color="#A94442">No records found</font></center></p></c:if>
 
</div>




                                  <div class="" style="width:97%;"><c:if test="${totalTasks ne '0'}">
												<table id="pendingTable" class="table table-hover dt-responsive"><thead>
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
													<li><a href="MaPendingTasks?tab=Created&page=${oldPage}&raisedBySearchkey=${searchKeys.raisedBy}&categorySearchkey=${searchKeys.category}&severitySearchkey=${searchKeys.severity}&subjectSearchkey=${searchKeys.subject}&createdDateSearchkey=${searchKeys.createdDate}"> Prev </a></li>
												</c:if>
												
												<c:if test="${lastPage ne '1'}" >
												
												<li><a href="MaPendingTasks?tab=Created&page=${pageNumber}&raisedBySearchkey=${searchKeys.raisedBy}&categorySearchkey=${searchKeys.category}&severitySearchkey=${searchKeys.severity}&subjectSearchkey=${searchKeys.subject}&createdDateSearchkey=${searchKeys.createdDate}">${pageNumber }</a></li>
												</c:if>
							
												<c:if test="${pageNumber lt lastPage}" > 
												<c:if test="${lastPage ne '1'}" >
													<c:set var="newPage" value="${pageNumber + 1}" />
													<li><a href="MaPendingTasks?tab=Created&page=${newPage}&raisedBySearchkey=${searchKeys.raisedBy}&categorySearchkey=${searchKeys.category}&severitySearchkey=${searchKeys.severity}&subjectSearchkey=${searchKeys.subject}&createdDateSearchkey=${searchKeys.createdDate}">  Next </a> </li>
												</c:if></c:if>
											
												</div></th>
												</c:if>
												</c:if>
											
										</thead>
									</table>
									</c:if>
							</div> 
							
							    <!-- /page content -->
    
      </div>
        
    <%@ include file="IncludesFooter.jsp" %>
  <script src="resources/js/messageframe.js"></script>
    <script src="resources/vendors/devbridge-autocomplete/dist/jquery.autocomplete.min.js"></script>
    
    
    <script>
    
    function gotoSelectedPage(obj){
		var pageNum = obj.value;
		window.location.href = "MaPendingTasks?tab=Created&page="+pageNum+"&raisedBySearchkey=${searchKeys.raisedBy}&categorySearchkey=${searchKeys.category}&severitySearchkey=${searchKeys.severity}&subjectSearchkey=${searchKeys.subject}&createdDateSearchkey=${searchKeys.createdDate}";
	}
    
    function validateSearch(){
		 var raisedBySearchkey = $.trim($('#raisedBySearchkey').val());
		 var subjectSearchkey = $.trim($('#subjectSearchkey').val());
		 var createdDateSearchkey = $.trim($('#createdDateSearchkey').val());
		 var severitySearchkey = $.trim($('#severitySearchkey').val());
		 var categorySearchkey = $.trim($('#categorySearchkey').val());
		 if(raisedBySearchkey=="" && subjectSearchkey=="" && createdDateSearchkey=="" && categorySearchkey=="" && severitySearchkey==""){
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
        	
              $('#fullName'+c).autocomplete({
                lookup: employeeArray,
                onSelect: function(){
                	var selectedEmployee = $('#fullName'+c).val();
                
            		$(employeeArray).each(function(index){
                    	if(selectedEmployee == employeeArray[index].value)                    		
                    		$('#fullNameId'+c).val(employeeArray[index].data);
                    
                    });
                }
            });
          	
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
        	
        	var subject  = {
        			<c:forEach var="sub" items="${subjectMap}">        		
        			 "${sub.key}":"${sub.value}",        		        		
        			</c:forEach>
        			 "NONE":" "
        			};
        	var subArray = $.map(subject, function(value, key) {
                return {value: value, data: key};
              });
        	
        	
        	 var category  = {
        			<c:forEach var="sub1" items="${categoryMap}">        		
        			 "${sub1.key}":"${sub1.value}",        		        		
        			</c:forEach>
        			 "NONE":" "
        			};
        	var subArray1 = $.map(category, function(value, key) {
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
              $('#subjectSearchkey').autocomplete({
                  lookup: subArray,
                  onSelect: function(){
                  	var selectedsub = $('#subjectSearchkey').val();
                  
              		$(namesArray).each(function(index){
                      	if(selectedsub == subArray[index].value)                    		
                      		$('#subjectSearchkey1').val(subArray[index].data);
                      });
                  }
              });
              
              
              
               $('#categorySearchkey').autocomplete({
                  lookup: subArray1,
                  onSelect: function(){
                  	var selectedsub1 = $('#categorySearchkey').val();
                  
              		$(namesArray).each(function(index){
                      	if(selectedsub1 == subArray1[index].value)                    		
                      		$('#categorySearchkey1').val(subArray1[index].data);
                      });
                  }
              }); 
               
               
               
               $('#severitySearchkey').autocomplete({
                   lookup: subArray2,
                   onSelect: function(){
                   	var selectedsub2 = $('#severitySearchkey').val();
                   
               		$(namesArray).each(function(index){
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
  </sec:authorize>
 </html>