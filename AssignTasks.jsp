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
		try {
			var height = obj.contentWindow.document.body.scrollHeight;
			//obj.style.height = height + 'px';
			obj.style.height = 750 + 'px';
			//alert(height);
		} catch (resizeErr) {
			console.log('Error with Iframe resize');
		}
	}
</script>
 <link
	href="resources/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css"
	rel="stylesheet">
<link
	href="resources/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css"
	rel="stylesheet">
</head>
<sec:authorize access="hasRole('ROLE_MANAGER') or hasRole('ROLE_SUPERUSER')">
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
              <c:if test="${not empty successMessage}">
              	<div class="alert alert-success alert-dismissable fade in">
				  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
				  <strong>Success!</strong> ${successMessage}
				</div>
              </c:if>
              <c:if test="${not empty dangerMessage}">
              	<div class="alert alert-danger alert-dismissable fade in">
				  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
				  <strong>Failed!</strong> ${dangerMessage}
				</div>
              </c:if>
                <div class="x_panel">
									<div>
										<%@ include file="HelpVideoModal.jsp"%>
										<a
											class="btn button-border btn-small btn-warning createIFrameModal pull-right"
											data-toggle="tooltip" rel="tootltip" title="FAQs"
											data-displaytitle="FAQs" data-placement="bottom"
											data-url="listFAQquestions?page=1"> <i
											class="fa fa-question-circle" aria-hidden="true">FAQs</i>
										</a>
									</div>
									<div class="x_title">
                    <h2>Tasks to Assign</h2>
                    <div><b>Projects</b>
									<select id="userSelectedProject" style="width:220px;height:30px">
										<option value = "all">All</option>
										<c:forEach var ="managerProject" items ="${managerProjects}" >
										    <option value = "${managerProject.projectID}">${managerProject.projectName}</option> 
										</c:forEach>
									</select>
									</div></td></tr>
									</table> 
                    <div class="clearfix"></div>
                  </div>
                  
                    <ul class="nav nav-tabs">
					  <li id="pending-tab" class="active"><a data-toggle="tab" href="#pendingAssignment" >Pending Assignment</a></li>
					  <li id="assigned-tab" class=""><a data-toggle="tab" href="#assignedTasks" >Assigned Tasks</a></li>
					  <li id="archived-tab" class=""><a data-toggle="tab" href="#archivedTasks" >Archived Tasks</a></li>
				   </ul>
                  <div class="x_content" style="min-height: 750px;">                 
		  
                     <div id="pendingAssignment" class="tab-pane fade in active">
						  <iframe class="scrollable-content"  id="iFramePendingTasks"
						  style="border: none; display: block; width: 100%; height: 750px;" onload="resizeIframe(this)"></iframe>
					 </div>
					<div id="assignedTasks" class="tab-pane fade active">
						 <iframe class="scrollable-content" id="iFrameAssignedTasks"
						  style="border: none; display: block; width: 100%; height: 500%;" onload="resizeIframe(this)"></iframe>
					</div>
                     
                     <div id="archivedTasks" class="tab-pane fade active">
						 <iframe class="scrollable-content" id="iFrameArchivedTasks"
						  style="border: none; display: block; width: 100%; height: 100%;" onload="resizeIframe(this)"></iframe>
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
							<h4 class="modal-title modal-title-attachment">Forward:</h4>
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
			
        <!-- /page content -->
       <%@ include file="FooterContent.jsp" %>
      </div>
        
    <%@ include file="IncludesFooter.jsp" %>
  <script src="resources/js/messageframe.js"></script>
     <script src="resources/vendors/devbridge-autocomplete/dist/jquery.autocomplete.min.js"></script>
     
      <script>
     $(function(){	 
     $('#iFramePendingTasks').attr('src', "MaPendingTasks?page=1&tab=Created");
		
		$('#pending-tab').click(function(){
			$('#pendingAssignment').css("display", "block");
			$('#assignedTasks').css("display", "none");	
			$('#archivedTasks').css("display", "none");	
			$('#iFramePendingTasks').attr('src', "MaPendingTasks?page=1&tab=Created");
			 
		});
		
		$('#assigned-tab').click(function(){
			 $('#assignedTasks').css("display", "block");	
			 $('#pendingAssignment').css("display", "none");
			 $('#archivedTasks').css("display", "none");
			 $('#iFrameAssignedTasks').attr('src', "MaPendingTasks?page=1&tab=Assigned");
			
		});
		
		$('#archived-tab').click(function(){
			 $('#archivedTasks').css("display", "block");	
			 $('#pendingAssignment').css("display", "none");
			 $('#assignedTasks').css("display", "none");
			 $('#iFrameArchivedTasks').attr('src', "MaPendingTasks?page=1&tab=Completed");
			
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
         
  
  </body>
 </sec:authorize>
</html>