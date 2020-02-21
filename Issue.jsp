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
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ include file="IncludesHeader.jsp"%>
<link rel="stylesheet" href="resources/css/help.css">
</head>
<body class="nav-md footer_fixed">
	<div class="container body">
		<div class="main_container">
			<%@ include file="sidebarMenu.jsp"%>
			<%@ include file="topNavigation.jsp"%>
			<!-- page content -->
			<div class="right_col" role="main">
				<div class="">
					<div class="clearfix"></div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
							       <div>
								    <%@ include file="HelpVideoModal.jsp"%>
                                     <a class="btn button-border btn-small btn-warning createIFrameModal pull-right"
												data-toggle="tooltip" rel="tootltip"
												title="FAQs"
												data-displaytitle="FAQs" data-placement="bottom"
												data-url="listFAQquestions?page=1">
												<i class="fa fa-question-circle" aria-hidden="true">FAQs</i>
											</a>
                                    
                                    </div>
								<div class="x_title">
								<table width="40%" border="0"><tr><td>
									<h2>My Tasks</h2></td><td>
									<div><b>Projects</b>
									<select id="userSelectedProject" style="width:220px;height:30px">
										<option value = "all">All</option>
										<c:forEach var ="userProject" items ="${userProjects}" >
										    <option value = "${userProject.projectID}">${userProject.projectName}</option> 
										</c:forEach>
									</select>
									</div></td></tr>
									</table>
								 	<div class="clearfix"></div>
								</div>
								
					           <div class="x_content" style="min-height: 500px; ">
									<ul class="nav nav-tabs">
										<li class="active"><a data-toggle="tab" href="#Inbox"><i
												class="fa fa-ticket"></i> Active Tasks</a></li>
										<li><a data-toggle="tab" href="#History"><i
												class="fa fa-handshake-o"></i> Completed Tasks</a></li>
										<li ><a data-toggle="tab" href="#Sent"><i
												class="fa fa-thumb-tack"></i> Created Tasks</a></li>
										<li><a data-toggle="tab" href="#Compose"><i
												class="fa fa-hand-o-up"></i> Create Task</a></li>
									</ul>

									<div class="tab-content col-md-12">
										<div id="Inbox" class="tab-pane fade in active" style="min-height:600px;">
											<br />
											<div class="col-sm-3 mail_list_column">
												<c:forEach var="email" items="${assigned}">
													<a class="view-assigned-tasks" href="viewTask?id=${email.id}&type=assigned">
														<div class="mail_list">
															<div class="left">
																<i class="fa fa-terminal"></i>
															</div>
															<div class="right">
																<h3>${email.subject}
																	<small><fmt:formatDate pattern="MM-dd-yy" value="${email.createdDate}" /></small>
																</h3>
																<p>${email.severity}</p>
															</div>
														</div>
													</a>
												</c:forEach>
											</div>
											<div class="col-sm-9 col-md-9 col-xs-11 mail_view">
												<c:forEach items="${assigned}" var="email" varStatus="status">
													<c:if test="${status.first}">
														<iframe class="scrollable-content" id="viewAssignedTaskFrame"
													src="viewTask?id=${email.id}&type=assigned"
													style="border: none; overflow: auto; display: block; width: 100%; height: 160vh;"></iframe>
													</c:if>
												</c:forEach>												
											</div>
										</div>
										<div id="History" class="tab-pane fade" style="min-height:600px;">
											<br />
											<div class="col-sm-3 mail_list_column">
												<c:forEach var="email" items="${completed}">
												<c:if test="${email.status eq 'Completed'}">
													<a class="view-previous-tasks" href="viewTask?id=${email.id}&type=completed">
														<div class="mail_list">
															<div class="left">
																<i class="fa fa-terminal"></i>
															</div>
															<div class="right">
																<h3>${email.category}
																	<small><fmt:formatDate pattern="MM-dd-yy" value="${email.createdDate}" /></small>
																</h3>
																<p>${email.severity}</p>
															</div>
														</div>
													</a>
												</c:if>
												</c:forEach>
											</div>
											<div class="col-sm-9 col-md-9 col-xs-11 mail_view">
												<c:forEach items="${completed}" var="email" varStatus="status">
												<c:if test="${email.status eq 'Completed'}">
													<c:if test="${status.first}">
														<iframe class="scrollable-content" id="viewPreviousTaskFrame"
															src="viewTask?id=${email.id}&type=completed"
															style="border: none; overflow: auto; display: block; width: 100%; height: 160vh;"></iframe>
													</c:if>
												</c:if>
												</c:forEach>
											</div>
										</div>
										<div id="Sent" class="tab-pane fade" style="min-height:600px;">
											<br>
											<div class="col-sm-3 mail_list_column">
												<c:forEach var="email" items="${created}">
													<a class="view-created-tasks"
														href="viewTask?id=${email.id}&type=created">
														<div class="mail_list">
															<div class="left">
																<i class="fa fa-terminal"></i>
															</div>
															<div class="right">
																<h3>${email.category}
																	<small><fmt:formatDate pattern="MM-dd-yy" value="${email.createdDate}" /></small>
																</h3>
																<p>${email.severity}</p>
															</div>
														</div>
													</a>
												</c:forEach>
											</div>
											 <div class="col-sm-1 col-md-9 col-xs-11 mail_view">
												<c:forEach items="${created}" var="email" varStatus="status">
													<c:if test="${status.first}">
														<iframe class="scrollable-content" id="viewCreatedTaskFrame"
															src="viewTask?id=${email.id}&type=created"
															style="border: none; overflow: auto; display: block; width: 100%; height: 160vh;"></iframe>
													</c:if>
												</c:forEach>
											</div> 
																					
	
											
											
											<%-- <%@ include file="CreateTask.jsp"%> --%>
											</div>
											<div id="Compose" class="tab-pane fade" style="min-height:600px;">
											<br />
											
											<div class="col-md-9 col-sm-9 col-xs-11 mail_view">
												<form:form id="create_task" class="form-horizontal form-label-left"
	action="SendTask?${_csrf.parameterName}=${_csrf.token}&username=${pageContext.request.userPrincipal.name}&fromModal=false"
	method="POST" modelAttribute="Task" enctype="multipart/form-data">   

	
	<%@ include file="CreateTask.jsp"%>
	
	</form:form>
											
											
											<%-- <%@ include file="CreateTask.jsp"%> --%>
											</div>
	
										</div>
										</div>
										
									</div>
								</div>
						
						
						</div>
					</div>
				</div>
			</div>

			<div id="viewAttachmentModal" class="modal fade" role="dialog">
				<div class="modal-dialog modal-lg">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">&times;</button>
							<h4 class="modal-title modal-title-attachment">Attachment</h4>
						</div>

						<div class="modal-body modal-body-attachment"></div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>
			
			<div id="dynamicIFrameModal" class="modal modal-fs fade" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<div class="modal-content modal-fs">
				<div class="modal-header modal-fs">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title" id="dynamicIFrameModalTitle"></h4>
				</div>
				<div class="modal-body" id="dynamicIFrameModalBody"
					style="min-height: 600px;"></div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>
		</div>
		<!-- /page content -->
		<%@ include file="FooterContent.jsp"%>
		
	</div>
	<%@ include file="IncludesFooter.jsp"%>
	
	<script src="resources/js/messages.js"></script>
	<script type="text/javascript">
	$(function(){    
	    $('.view-assigned-tasks').on('click',function(){
	  		$("#viewAssignedTaskFrame").attr("src", $(this).attr("href") + "&output=embed");
	        $('#viewAssignedTaskFrame').attr("name", $(this).attr("subject"));	        
	        return false;        
	    });	    
	    $('.view-previous-tasks').on('click',function(){
	  		$("#viewPreviousTaskFrame").attr("src", $(this).attr("href") + "&output=embed");
	        $('#viewPreviousTaskFrame').attr("name", $(this).attr("subject"));	        
	        return false;        
	    });	    
	    $('.view-created-tasks').on('click',function(){
	  		$("#viewCreatedTaskFrame").attr("src", $(this).attr("href") + "&output=embed");
	        $('#viewCreatedTaskFrame').attr("name", $(this).attr("subject"))        
	        return false;        
	    });
	    
	    $(".uploadTaskAttachment").fileinput({
		    overwriteInitial: true,
		    maxFileSize: 15000,
		    showClose: false,
		    showCaption: false,	    
		    removeTitle: 'Cancel or reset changes',
		    msgErrorClass: 'alert alert-block alert-danger',
		    browseLabel: '',
		    removeLabel: '',
		    layoutTemplates: {main2: '{preview} {remove} {browse}'},
		    allowedFileExtensions: ["pdf", "jpg", "png"]
		});
	})
	</script>
	<script>
 $(function() {
	 $('.createIFrameModal') .on( 'click',
		 function() {
			 var link = $(this).attr('data-url');
			 var title = $(this).attr( 'data-displaytitle');
			 var iframe = '<iframe src="' + link
					 + '" width="100%" style="border:none;height:600px;"></iframe>';
			 var modal = $(window.document).find( '#dynamicIFrameModal');
			 $(window.document).find( '#dynamicIFrameModalBody').html( iframe);
			 $(window.document).find( '#dynamicIFrameModalTitle').text( title);
			 $(window.document).find( '#dynamicIFrameModal').modal({
				 backdrop : true,
				 scrollable : true
			 });
		 $(window.document).find( '#dynamicIFrameModal') .modal("show");
	 });
 })
 </script>
 	<script>
  $(function() {
	 $("#userSelectedProject").change(function () {
        //var end = this.value;
        var selectVal = $('#userSelectedProject').val();
        $("#propertyType").val(selectVal);
        //alert($('#userSelectedProject').val());
        
        alert(selectVal);
       // $("#selectOption")
    });
 })
 </script>
</body>
</html>