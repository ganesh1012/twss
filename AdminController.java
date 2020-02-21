package com.twss.java.spring.controller;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.jai.operator.AWTImageDescriptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.util.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.twss.java.spring.config.Variables;
import com.twss.java.spring.customproperty.CustomPropertyConfiguration;
import com.twss.java.spring.customproperty.CustomPropertyDAO;
import com.twss.java.spring.dao.DepartmentDAO;
import com.twss.java.spring.dao.EmployeeDAO;
import com.twss.java.spring.dao.HelpVideoDAO;
import com.twss.java.spring.dao.IDCardDAO;
import com.twss.java.spring.dao.MemberDAO;
import com.twss.java.spring.dao.MessageDAO;
import com.twss.java.spring.dao.OrganizationDAO;
import com.twss.java.spring.dao.ProjectTaskDAO;
import com.twss.java.spring.dao.StatusReportDAO;
import com.twss.java.spring.dao.TaskDAO;
import com.twss.java.spring.dao.TimesheetCriteriaDAO;
import com.twss.java.spring.dao.UserPropertiesDAO;
import com.twss.java.spring.documentsmodule.DocumentsDAO;
import com.twss.java.spring.leavemodule.LeaveConfiguration;
import com.twss.java.spring.leavemodule.LeaveRequestDAO;
import com.twss.java.spring.model.Department;
import com.twss.java.spring.model.DepartmentMember;
import com.twss.java.spring.model.Documents;
import com.twss.java.spring.model.Email;
import com.twss.java.spring.model.Employee;
import com.twss.java.spring.model.EmployeeActivity;
import com.twss.java.spring.model.HelpVideos;
import com.twss.java.spring.model.ListDailyStatusReport;
import com.twss.java.spring.model.Member;
import com.twss.java.spring.model.Organization;
import com.twss.java.spring.model.Password;
import com.twss.java.spring.model.ProjectResource;
import com.twss.java.spring.model.ProjectTask;
import com.twss.java.spring.model.ProjectTaskDaily;
import com.twss.java.spring.model.StatusCriteria;
import com.twss.java.spring.model.StatusReport;
import com.twss.java.spring.model.Task;
import com.twss.java.spring.model.TaskAssign;
import com.twss.java.spring.model.TaskMessage;
import com.twss.java.spring.model.TaskSearch;
import com.twss.java.spring.model.TimesheetCriteria;
import com.twss.java.spring.model.UserProperties;
import com.twss.java.spring.teamsmodule.RelationObject;
import com.twss.java.spring.teamsmodule.TeamDAO;
import com.twss.java.spring.utilitybeans.EmailImpl;
import com.twss.java.spring.utilitybeans.TimeFormatter; 


/**
 * 
 * Maps all requests originating from the admin to the appropriate views.
 * <p>
 * <b>Project Name:</b> TWSSWebAplication
 * <p>
 * <b>Created Date:</b> Dec 1, 2016
 * <p>
 * <b>Controller Class:</b> Home Controller
 * 
 * @author Sushma Manjunatha, Raghuram Challapalli
 * @version 1.0
 * @since 2016-09-19
 *
 */
@Controller
public class AdminController {
 
	final static Logger logger = Logger.getLogger(AdminController.class);
	
	@Autowired
	private OrganizationDAO organizationDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private TimesheetCriteriaDAO timesheetCriteriaDAO;
	@Autowired
	private MessageDAO messageDAO;
	@Autowired
	private TaskDAO taskDAO;
	@Autowired
	private LeaveRequestDAO leaveDAO; 
	@Autowired
	private UserController userController;
	@Autowired
	private DepartmentDAO departmentDAO; 
	@Autowired
	private DocumentsDAO documentsDAO;
	@Autowired
	private MemberDAO memberDAO;
	@Autowired
	private IDCardDAO idCardDAO;
	@Autowired
	private StatusReportDAO statusReportDAO;
	@Autowired
	private ProjectTaskDAO projectTaskDAO;
	@Autowired
	private HelpVideoDAO helpVideoDAO;	
	@Autowired
	private UserPropertiesDAO propertiesDAO;
	@Autowired
	private TimeFormatter dateTime;
	@Autowired
	private EmailImpl aws;
	@Autowired
	private TeamDAO teamDAO;
	@Autowired
	private CustomPropertyDAO customPropertyDAO;
	private int defaultPagination=20;
	
	/*
	@ExceptionHandler({ java.lang.NullPointerException.class,
			org.springframework.web.servlet.NoHandlerFoundException.class })
	public ModelAndView pageNotFound(Exception exp) {
		StackTraceElement[] stackTrace = exp.getStackTrace();
		String message = "";		
		for(StackTraceElement element : stackTrace)
			message += element.toString()+"<br/>";		
		logger.error("Exception: " + exp.getMessage());
		ModelAndView model = new ModelAndView();
		try {
			aws.SendNullPointerErrorEmail(message);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.setViewName("ErrorPage404");
		return model;
	}

	@ExceptionHandler({java.sql.SQLException.class, 
		org.springframework.dao.DuplicateKeyException.class,		
		org.springframework.dao.DataRetrievalFailureException.class,
		org.springframework.dao.RecoverableDataAccessException.class,
		org.springframework.dao.DataIntegrityViolationException.class,
		org.springframework.dao.CleanupFailureDataAccessException.class,
		org.springframework.dao.PermissionDeniedDataAccessException.class,
		org.springframework.dao.NonTransientDataAccessResourceException.class,			
		org.springframework.jdbc.BadSqlGrammarException.class,		
		org.springframework.jdbc.UncategorizedSQLException.class,
		org.springframework.jdbc.CannotGetJdbcConnectionException.class,
		com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException.class,
		com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException.class})
	public ModelAndView serverError(Exception exp) {
		StackTraceElement[] stackTrace = exp.getStackTrace();
		String message = "";		
		for(StackTraceElement element : stackTrace)
			message += element.toString()+"<br/>";		
		logger.error("Exception: " + exp.getMessage());
		ModelAndView model = new ModelAndView();
		try {
			aws.SendSQLErrorEmail(message);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.setViewName("ErrorPage500");
		return model;
	}
*/
	/**
	 * 
	 * Redirects the user to view tasks pending assignment. Restricted access to
	 * certain users.
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws IOException
	 *             ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/AssignNewTask")
	public ModelAndView AssignNewTask(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		
	    List<String> employeeUsernames = null ;
	    List<ProjectResource> managerProjects = new ArrayList<ProjectResource>();
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Approve Daily Timesheet Module");
			
		}
		
	    employeeUsernames =  teamDAO.getEmployeesUserNamesForManager((String)session.getAttribute("organizationID"), "bstuart_wings");
	    if(employeeUsernames!=null) {
	    	Iterator<String> it = employeeUsernames.iterator();
	    	while(it.hasNext()) {
	    		String user = it.next();
	    		List<ProjectResource> userProject = employeeDAO.listUserAssociatedProjects( user, (String)session.getAttribute("organizationID"));
	    		
	    		for(ProjectResource userPro:userProject) {
	    			managerProjects.add(userPro);
	    		}
	    		
	    	}
	    	
	    }
	    
	    
	    

		model.addObject("managerProjects", managerProjects);
		model.addObject("taskSearch", new TaskSearch());
		model.setViewName("AssignTasks");
		return model;

	}

	
	@RequestMapping(value = "/PendingTasks")
	public ModelAndView PendingTasks( ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		
		String username = request.getRemoteUser();
		List<Employee> listEmployee = new ArrayList<Employee>();
		List<Task> listTask = new ArrayList<Task>();
		String orgID = (String) session.getAttribute("organizationID");
		System.out.println("organizationID   "+orgID);
		
		List<CustomPropertyConfiguration> customPropertiesList = customPropertyDAO.listCutomPropertiesRequests("bstuart_wings", orgID);
		
		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
				paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
				break;
			}
		}
		
		String assignmentTasks = request.getParameter("tab");
		String viewpage = "PendingAssignment";
		
		if (assignmentTasks.equals("Created")) {
			assignmentTasks = "Created";
			viewpage = "PendingAssignment";
		} 
		else if(assignmentTasks.equals("Assigned")){
			assignmentTasks = "Assigned";
			viewpage = "AssignedTasks";
		}
		else {
			assignmentTasks = "Completed";
			viewpage = "ArchivedTasks";
		}
		
		String pageNumber = "1";
		if (request.getParameter("page") != null) {
			pageNumber = request.getParameter("page");
		}
		
		
		
		Map<String, String> raiseByMap = taskDAO.listForRaisedBy(orgID, assignmentTasks);
		Map<String, String> subjectMap = taskDAO.listForSubjectMap(orgID, assignmentTasks);
		Map<String, String> severityMap = taskDAO.listForSeverityMap(orgID, assignmentTasks);
		Map<String, String> categoryMap = taskDAO.listForcategorytMap(orgID, assignmentTasks);
		Map<String, String> assignedToMap = taskDAO.listForAssignedTo(orgID);
		
		TaskSearch searchKeys = new TaskSearch();
		searchKeys.setRaisedBy(request.getParameter("raisedBySearchkey"));
		searchKeys.setSubject(request.getParameter("subjectSearchkey"));
		searchKeys.setCategory(request.getParameter("categorySearchkey"));
		searchKeys.setSeverity(request.getParameter("severitySearchkey"));
		searchKeys.setCreatedDate(request.getParameter("createdDateSearchkey"));
		searchKeys.setAssignedTo(request.getParameter("assignToSearchkey"));
		searchKeys.setAssignedOn(request.getParameter("AssignedOnSearchkey"));
		searchKeys.setCompletedOn(request.getParameter("completedDateSearchkey"));
		
		String AssignedName=searchKeys.getAssignedTo();
		String AssignedUsername="";
		
		if(!StringUtils.isNullOrEmpty(AssignedName)) {
		String lastname="";
		String firstname="";
		
		if(AssignedName.contains(",")) {
		String arr[]=AssignedName.split(",");
		if(arr.length==2) {
		lastname=arr[0];
		firstname=arr[1];
		}else {
			lastname=arr[0];	
		}
		}
		AssignedUsername=employeeDAO.getUsernameFromFullName(firstname, lastname, orgID);
		}
		
		int totalTasks=0;
		if (null == session.getAttribute("deptID")) {
			String organizationID = (String) session.getAttribute("organizationID");
			listEmployee = employeeDAO.list(organizationID);
			listTask = taskDAO.listbyOrgID(organizationID,paginationDefault,pageNumber,assignmentTasks,searchKeys,AssignedUsername);
			totalTasks=taskDAO.getTotalTasks(organizationID, assignmentTasks,searchKeys,AssignedUsername);
		} else {
			String departmentID = (String) session.getAttribute("deptID");
			listEmployee = employeeDAO.listDepartmentEmployees(departmentID);
			listTask = taskDAO.listbyDepartmentID(departmentID,paginationDefault,pageNumber,assignmentTasks,searchKeys,AssignedUsername);
			totalTasks=taskDAO.getTotalTasksByDeptId(departmentID, assignmentTasks,searchKeys,AssignedUsername);
		}
		
		int lastPage = totalTasks % paginationDefault == 0 ? (totalTasks / paginationDefault) : ((totalTasks / paginationDefault) + 1);
		
		Map<String, String> empList = new LinkedHashMap<String, String>();
		for (Employee emp : listEmployee) {
			String name = emp.getLastname() + ", " + emp.getFirstname();
			empList.put(emp.getUsername(), name);
		}

		for(Task tsk:listTask) {
			if(tsk.getPhoneNo()==null||tsk.getPhoneNo()=="")
				tsk.setPhoneNo("--");
			if(tsk.getTrackingID()==null||tsk.getTrackingID()=="")
				tsk.setTrackingID("--");
		}
		 model.addObject("pageAll", pageNumber);
			if(pageNumber.equalsIgnoreCase("All")) {
				pageNumber = Integer.toString(lastPage);
			}
		List<TaskAssign> assignments = taskDAO.listAssigned();
		List<TaskAssign> taskLog = taskDAO.listTaskLog();
		List<TaskMessage> messageList = taskDAO.listTaskComm();
		Task task = new Task();
		model.addObject("Task",task);
		model.addObject("taskCommList", messageList);
		model.addObject("taskLogList", taskLog);
		model.addObject("taskAssignList", assignments);
		model.addObject("empList", empList);
		model.addObject("taskAssign", new TaskAssign());
		model.addObject("taskMessage", new TaskMessage());
		model.addObject("listTask", listTask);
		model.addObject("organizationID", orgID);
		model.addObject("totalTasks",totalTasks);
		model.addObject("listSize", listTask.size());
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("paginationDefault",paginationDefault);
		model.addObject("raiseByMap", raiseByMap);
		model.addObject("subjectMap", subjectMap);
		model.addObject("categoryMap", categoryMap);
		model.addObject("severityMap", severityMap);
		model.addObject("assignedToMap", assignedToMap);
		model.addObject("searchKeys", searchKeys);
		model.addObject("taskSearch", new TaskSearch());
		model.setViewName(viewpage);
		return model;

	}

	
	/**
	 * 
	 * Allows the user to assign tasks to Employees. Restricted access to
	 * certain users.
	 * 
	 * @param taskAssign
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/assign" }, method = RequestMethod.POST)
	public ModelAndView assign(@ModelAttribute TaskAssign taskAssign, HttpServletRequest request, HttpSession session) {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String organizationID=(String) session.getAttribute("organizationID");
		String fromEmailID = organizationDAO.get(organizationID).getEmail();
		try {
			fromEmailID = customPropertyDAO.getCutomPropertyDetailsByName("Default-Email", organizationID).getPropertyValue().replace(",", "");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		String username = request.getRemoteUser();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		taskAssign.setCreatedBy(username);
		taskAssign.setModifiedBy(username);
		taskAssign.setCreatedDate(date);
		taskAssign.setModifiedDate(date);
		taskAssign.setStatus("Assigned");
		String name=taskAssign.getFullName();
		String lastname="";
		String firstname="";
		
		if(name.contains(", ")) {
		String arr[]=name.split(", ");
		lastname=arr[0];
		firstname=arr[1];
		}	
		
        String EmpUsername=employeeDAO.getUsernameFromFullName(firstname, lastname, organizationID);
        taskAssign.setUsername(EmpUsername);
        String trackingID=request.getParameter("trackingid");
        String raisedUserName=request.getParameter("raisedUserName");
        int taskid=Integer.parseInt(request.getParameter("taskID"));
        String tab=request.getParameter("tab");
        String page=request.getParameter("page");
        Task task=taskDAO.getTaskByTaskId(taskid, raisedUserName);
        String emailid=employeeDAO.getEmail(EmpUsername);
        String sub=" Hello " + taskAssign.getFullName()+"Task Assigned: "+task.getSubject();
        String userSub=" Hello " + task.getName()+",Your task assigned to "+taskAssign.getFullName();
        String urlLink = "\""+Variables.site_url+"/viewAssignTaskDetails?trackingId="+trackingID+"&AssignedUserName="+taskAssign.getUsername()+"&name="+taskAssign.getFullName()+"&taskid="+taskid+"&username="+task.getUsername()+"\"";
        String phoneNo = null==task.getPhoneNo()?"N/A":task.getPhoneNo(); 
    	String message = "<!DOCTYPE html><html lang=\"en\">"
    			+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
    			+ "<meta name=\"viewport\" content=\"width=device-width\"><title>Assign Task Email</title></head><body>"
    			+ "<table><tr><td><p> Hello " + taskAssign.getFullName() + ",</p></td></tr>"
    			+ "<tr><td><p>A new task is assigned for you</p></td></tr>"
    			+ "<tr><td><p><table>"
    			+ "<tr><td> Raised By: &emsp;</td><td> " + task.getName() + " &emsp;</td></tr>" 
    			+ "<tr><td> Raised on: &emsp;</td><td> " + task.getCreatedDate() + " &emsp;</td></tr>" 
    			+ "<tr><td> Tracking ID: &emsp;</td><td> " + task.getTrackingID() + " &emsp;</td></tr>" 
    			+ "<tr><td> Phone No: &emsp;</td><td> " + phoneNo + " &emsp;</td></tr>" 
    			+ "<tr><td> EmailID: &emsp;</td><td> " + task.getEmail() + " &emsp;</td></tr>" 
    			+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>" 
    			+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"  
    			+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr>"  
    			+ "</table></p></td></tr><tr><td><p>Please click here for view user track: <a href="+urlLink+" target=\"_blank\" >View History</a></p></td></tr>"
    			+ "</table>"
    			+"</body></html>";
    	String userUrlLink = "\""+Variables.site_url+"/viewDetails?trackingId="+trackingID+"&AssignedUserName="+taskAssign.getUsername()+"&username="+task.getUsername()+"\"";
     	String UserMsg = "<!DOCTYPE html><html lang=\"en\">"
     		   	+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
     		   	+ "<meta name=\"viewport\" content=\"width=device-width\"><title>Assign Task Email</title></head><body>"
     		   	+ "<table><tr><td><p> Hello " + task.getName() + "</p></td></tr>"
     		   	+ "<tr><td><p>created task is assigned to "+taskAssign.getFullName()+"</p></td></tr>"
     		   	+ "<tr><td><p><table>"
     		   	+ "<tr><td> Raised By: &emsp;</td><td> " + task.getName() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Raised on: &emsp;</td><td> " + task.getCreatedDate() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Tracking ID: &emsp;</td><td> " + task.getTrackingID() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Phone No: &emsp;</td><td> " + phoneNo + " &emsp;</td></tr>" 
     		   	+ "<tr><td> EmailID: &emsp;</td><td> " + task.getEmail() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"  
     		   	+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr>"  	
     		   + "</table></p></td></tr><tr><td><p>Please click here for view user track: <a href="+userUrlLink+" target=\"_blank\" >View History</a></p></td></tr>"
   			+ "</table>"
    			+"</body></html>";

    	Email email = new Email();
    	email.setTo(emailid);
    	email.setSubject(sub);
    	email.setBody(message);
    	email.setFile1(task.getFile1());
    	email.setFilename1(task.getFileName1());
    	email.setContentType1(task.getFileType1());
    	email.setFile2(task.getFile2());
    	email.setFilename2(task.getFileName2());
    	email.setContentType2(task.getFileType2());
    	email.setFile3(task.getFile3());
    	email.setFilename3(task.getFileName3());
    	email.setContentType3(task.getFileType3());
        email.setUsername(username);
    	email.setFrom(Variables.portal_email);
    	email.setPassword(Variables.portal_password);
    	email.setName(taskAssign.getFullName());
    	
    	Email UsrEmail = new Email();
       	UsrEmail.setTo(task.getEmail());
       	UsrEmail.setSubject(userSub);
       	UsrEmail.setBody(UserMsg);
       	UsrEmail.setFile1(task.getFile1());
       	UsrEmail.setFilename1(task.getFileName1());
       	UsrEmail.setContentType1(task.getFileType1());
       	UsrEmail.setFile2(task.getFile2());
       	UsrEmail.setFilename2(task.getFileName2());
       	UsrEmail.setContentType2(task.getFileType2());
       	UsrEmail.setFile3(task.getFile3());
       	UsrEmail.setFilename3(task.getFileName3());
       	UsrEmail.setContentType3(task.getFileType3());
       	UsrEmail.setUsername(username);
       	UsrEmail.setFrom(Variables.portal_email);
       	UsrEmail.setPassword(Variables.portal_password);
       	UsrEmail.setName(task.getName());
    	String fromName="Swarm Admin";
    	
    	String msg = taskDAO.assign(taskAssign);
		ModelAndView model = new ModelAndView();
		if (msg == "Task Assignment Successfull") {
			model.addObject("successMessage", msg);
			 try {
				   aws.SendTextEmail(email, fromName, fromEmailID);
					messageDAO.saveOrUpdate(email);
					aws.SendTextEmail(UsrEmail, fromName, fromEmailID);
					messageDAO.saveOrUpdate(UsrEmail);
				   } catch (Exception exp) {
						logger.error("Failed to send Email.");
					}
		}
		else {
			model.addObject("dangerMessage", msg);
		}
		model.setViewName("redirect:/PendingTasks?tab="+tab+"&page="+page);
		return model;
	}

	/**
	 * 
	 * Allows the user to archive a selected Task. Restricted access to certain
	 * users.
	 * 
	 * @param taskAssign
	 * @param request
	 * @return ModelAndView
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @see ModelAndView
	 */
	
	
	
	@RequestMapping(value = { "/archiveTask" }, method = RequestMethod.POST)
	public ModelAndView archiveTask(@ModelAttribute TaskAssign taskAssign, HttpServletRequest request)
			throws FileNotFoundException, IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		
		java.sql.Date date = dateTime.getCurrentSQLDate();
		taskAssign.setCreatedBy(request.getRemoteUser());
		taskAssign.setCreatedDate(date);
		taskAssign.setStatus("Assigned");
	    String tab=request.getParameter("tab");
	    String page=request.getParameter("page");
		taskDAO.archive(taskAssign);

		Task task = taskDAO.get(taskAssign.getTaskID());
        List<String> assignedUsersEmailsList=taskDAO.getAssignedUsersEmails(taskAssign.getTaskID());
		String name = employeeDAO.userDetails(request.getRemoteUser());

		String username = request.getRemoteUser();
		Email Email = new Email();

		Email.setCreatedBy(username);
		Email.setModifiedBy(username);
		Email.setCreatedDate(date);
		Email.setModifiedDate(date);

		Email.setUsername(username);
		Email.setName(Email.getName());
		Email.setFrom(Variables.portal_email);
		Email.setPassword(Variables.portal_password);
		Email.setStatus("Created");
		Email.setName(name);

		Employee profile = employeeDAO.profile(username);
		List<String> adminList = employeeDAO.listAdminEmails(profile.getOrganizationID());
		String fromEmailID = organizationDAO.get(profile.getOrganizationID()).getEmail();
		try {
			fromEmailID = customPropertyDAO.getCutomPropertyDetailsByName("Default-Email", profile.getOrganizationID()).getPropertyValue().replace(",", "");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		String toEmailList = "";
		for (String emailID : adminList) {
			toEmailList += emailID + ", ";
		}
		toEmailList+=task.getEmail()+", ";
		for (String emailID : assignedUsersEmailsList) {
			toEmailList += emailID + ", ";
		}
		System.out.println("toEmailList"+toEmailList);
		Email.setCc(toEmailList);
		Email.setTo(profile.getEmail());
		Email.setSubject("The task " + task.getSubject() + " is resolved");
		String msg = "<p>Hello,</p><br>";
		msg += "<p>The task " + task.getSubject() + " is resolved by "+name+ "</p>";
		msg+= "<table>"
				+ "<tr><td> Tracking Id: &emsp;</td><td> " + task.getTrackingID() + " &emsp;</td></tr>" 
				+ "<tr><td> Name: &emsp;</td><td> " + task.getName() + " &emsp;</td></tr>" 
				+ "<tr><td> Email: &emsp;</td><td> " + task.getEmail()+ " &emsp;</td></tr>" 
				+ "<tr><td> Phone no: &emsp;</td><td> " + task.getPhoneNo() + " &emsp;</td></tr>" 
				+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>" 
				+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"  
				+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr></table>" ;
		msg += "<p style='font-style: oblique'>Thoughtwave Software and Solutions Inc.</p>";
		Email.setBody(msg);				
		aws.SendTextEmail(Email, employeeDAO.fullname(username), fromEmailID);
		messageDAO.saveOrUpdate(Email);

		return new ModelAndView("redirect:/PendingTasks?tab="+tab+"&page="+page);

	}
	
	@RequestMapping(value = "/ViewEmployeeStatus")
	public ModelAndView viewEmployeeStatus(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);	

		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		List<Employee> empList = new ArrayList<Employee>();
		List<Employee> adminList = new ArrayList<Employee>();
		
		if (null == session.getAttribute("deptID")) {
			String organizationID = (String) session.getAttribute("organizationID");			
			empList = employeeDAO.getEmployeeStatus(organizationID);
			adminList = employeeDAO.getEmployeeAdminStatus(organizationID);
			List<DepartmentMember> listDeptAdmins = departmentDAO.listDepartmentAdmins(organizationID);
			model.addObject("adminList", adminList);
			model.addObject("deptAdminList", listDeptAdmins);
		} else {
			empList = employeeDAO.getEmployeeStatusByDepartment((String) session.getAttribute("deptID"));			
		}	
		model.addObject("empList", empList);

		Map<String, String> accessTypeList = new LinkedHashMap<String, String>();
		accessTypeList.put("Web", "Web Only");
		accessTypeList.put("Mobile", "Mobile Only");
		accessTypeList.put("Both", "All Access");
		accessTypeList.put("None", "No Access");
		model.addObject("accessTypeList", accessTypeList);
		
		model.addObject("newPassword", new Password());		
		model.addObject("employeeAccess", new Employee());
		try {
			// Help Video
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help Videos found for Employee Status Page.");
		}
		String passwordFlag = (String) session.getAttribute("passwordFlag");
		String accessTypeFlag = (String) session.getAttribute("accessTypeFlag");
		String employeeStatusFlag = (String) session.getAttribute("employeeStatusFlag");
		if (!(null == passwordFlag)) {
			model.addObject("messageTypeFlag", session.getAttribute("passwordFlag"));
			model.addObject("messageType", "Password Reset");
			model.addObject("Message", session.getAttribute("passwordMessage"));
			session.setAttribute("passwordFlag", null);
			session.setAttribute("passwordMessage", null);
		}
		if (!(null == accessTypeFlag)) {
			model.addObject("messageTypeFlag", session.getAttribute("accessTypeFlag"));
			model.addObject("messageType", "Access Type");
			model.addObject("Message", session.getAttribute("accessTypeMessage"));
			session.setAttribute("accessTypeFlag", null);
			session.setAttribute("accessTypeMessage", null);
		}
		if (!(null == employeeStatusFlag)) {
			model.addObject("messageTypeFlag", session.getAttribute("employeeStatusFlag"));
			model.addObject("messageType", "Employee Status");
			model.addObject("Message", session.getAttribute("employeeStatusMessage"));
			session.setAttribute("employeeStatusFlag", null);
			session.setAttribute("employeeStatusMessage", null);
		}
		model.setViewName("ViewEmployeeStatus");
		return model;

	}

	@RequestMapping(value = {"/disableEmployee", "/disableEmp"})
	public ModelAndView changeEmployeeStatus(HttpServletRequest request, HttpSession session) {
		if (null == request.getRemoteUser()) {
			return new ModelAndView("redirect:/login?session");
		}
		String username = request.getParameter("user");
		String orgID = request.getParameter("org");
		session.setAttribute("employeeStatusFlag", "false");
		boolean safeToDisable = true;
		List<String> userRoles = employeeDAO.userroles(username);
		if(userRoles.contains("ROLE_MANAGER")){
			List<RelationObject> managerEmployees = teamDAO.getEmployeesForManager(orgID, username);
			if(managerEmployees.size()>0){
				safeToDisable = false;
			}else{
				teamDAO.deleteManager(orgID, username);
			}
		}
		if(safeToDisable){
			if (employeeDAO.updateEmployeeStatus(username, orgID, "Disabled")) {
				session.setAttribute("employeeStatusMessage", employeeDAO.userDetails(username)
						+ " has been disabled. Employee Profile can be found in archived data.");
				logger.info(employeeDAO.userDetails(username) + " Profile Disabled By "
						+ employeeDAO.userDetails(request.getRemoteUser()));
			}
		}else{
			session.setAttribute("employeeStatusMessage", "Current Employee has other employees associated. Failed to disable.");
		}
		if(request.getServletPath().equals("/disableEmp"))
			return new ModelAndView("redirect:/employee-directory");
		
		return new ModelAndView("redirect:/ViewEmployeeStatus");
	}

	@RequestMapping(value = { "/admin/changeEmployeeAccessType", "/hr/changeEmployeeAccessType" })
	public ModelAndView changeEmployeeAccess(HttpServletRequest request, @ModelAttribute Employee employee,
			HttpSession session) {
		String auth = request.getParameter("auth");
		if ((!(request.getRemoteUser().equals(auth))) | (null == request.getRemoteUser()))
			return new ModelAndView("redirect:/login?session");

		if (employeeDAO.updateEmployeeAccess(employee.getUsername(), employee.getOrganizationID(),
				employee.getAccessType())) {
			session.setAttribute("accessTypeFlag", "true");
			session.setAttribute("accessTypeMessage",
					"Access Type Modified Successfully for " + employeeDAO.userDetails(employee.getUsername()));
			logger.info("Access Type of " + employeeDAO.userDetails(employee.getUsername()) + " Modified By "
					+ employeeDAO.userDetails(request.getRemoteUser()));
		} else {
			session.setAttribute("accessTypeFlag", "false");
			session.setAttribute("accessTypeMessage",
					"Unable to Modify Access Type for " + employeeDAO.userDetails(employee.getUsername()));
		}
		
		if(request.getServletPath().contains("/admin/"))
			return new ModelAndView("redirect:/ViewEmployeeStatus");
		
		return new ModelAndView("redirect:/EmpMiniProfile?usrname="+employee.getUsername());
	}

	/**
	 * Generates the QR code for a selected employee.
	 * 
	 * @param request
	 *            HTTPRequest with username of the employee as the request
	 *            parameter.
	 * @return - Redirects the user to the parent window.
	 * @throws WriterException
	 *             - Handles MultiFormatWriter Encoding.
	 * @throws IOException
	 *             - Handles the generation of files and dealing with byte[]
	 *             data.
	 * @throws NoSuchAlgorithmException
	 *             - Handles MessageDigest.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	@RequestMapping(value = "/generateQRCode")
	public ModelAndView createQRCode(HttpServletRequest request)
			throws WriterException, IOException, NoSuchAlgorithmException {
		String admin = request.getRemoteUser();
		String auth = request.getParameter("auth");
		if (!((null != request.getRemoteUser()) || (admin.equals(auth)))) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String username = request.getParameter("username");
		String timeStamp = dateTime.getCurrentTimeStampForId();
		String user = username + "_user_qrcode_scanner_" + timeStamp;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(user.getBytes());
		String qrCodeData = Base64.encodeBase64URLSafeString(messageDigest);

		File tempFile = File.createTempFile("QRCode", ".png", null);

		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes("UTF-8"), "UTF-8"),
				BarcodeFormat.QR_CODE, 200, 200, hintMap);
		MatrixToImageWriter.writeToFile(matrix, "png", tempFile);

		FileInputStream fis = new FileInputStream(tempFile);
		int byteLength = (int) tempFile.length(); // bytecount of the
													// file-content
		byte[] fileContent = new byte[byteLength];
		fis.read(fileContent, 0, byteLength);

		employeeDAO.createQRToken(qrCodeData, username, admin, fileContent);
		fis.close();
		return new ModelAndView("redirect:/ViewEmployeeStatus");

	}

	/**
	 * Loads the profile picture and QR code of an employee and generates the ID
	 * Card of an employee.
	 * 
	 * @param request
	 *            HTTPRequest with username of the employee as the request
	 *            parameter.
	 * @return - A ModelAndView object with the view containing the ID card of
	 *         the employee.
	 */
	@RequestMapping(value = "/viewEmployeeIDCard")
	public ModelAndView viewEmployeeIDCard(HttpServletRequest request) {
		String admin = request.getRemoteUser();
		String auth = request.getParameter("auth");
		if (!((null != request.getRemoteUser()) || (admin.equals(auth)))) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		ModelAndView model = new ModelAndView();
		String username = request.getParameter("user");
		Employee emp = employeeDAO.getEmployeeIDInfo(username);
		model.addObject("emp", emp);
		Documents empImage = documentsDAO.getPic(username);
		try {
			empImage.setEncodedImage(new String(Base64.encodeBase64(empImage.getFile())));
			model.addObject("empImage", empImage);
		} catch (Exception e) {
		}
		Documents qrImage = employeeDAO.getQrCodeImage(username);
		try {
			qrImage.setEncodedImage(new String(Base64.encodeBase64(qrImage.getFile())));
			model.addObject("qrImage", qrImage);
		} catch (Exception e) {
		}
		model.setViewName("EmployeeIDCard");
		return model;

	}

	@RequestMapping(value = {"/hardResetPasswordEmail", "/resetEmployeePassword"})
	public ModelAndView emailEmployeePassword(HttpServletRequest request, HttpSession session) throws FileNotFoundException, IOException {
		String admin = request.getRemoteUser();
		String auth = request.getParameter("auth");
		if (!((null != request.getRemoteUser()) || (admin.equals(auth)))) 			
			return new ModelAndView("redirect:/login?session");	
		Employee profile = employeeDAO.profile(admin);
		String fromEmailID = organizationDAO.get(profile.getOrganizationID()).getEmail();
		try {
			fromEmailID = customPropertyDAO.getCutomPropertyDetailsByName("Default-Email", profile.getOrganizationID()).getPropertyValue().replace(",", "");
		} catch (Exception e) {
		//	e.printStackTrace();
		}
		String password = request.getParameter("pass");
		String emailID = request.getParameter("email");
		String username = request.getParameter("usr");
		String name = employeeDAO.userDetails(username);
		String bccmail = request.getParameter("ccmail");
		String accessType = request.getParameter("accessType");
		String bodyContent = "Your password has been reset by the Admin. ";
		if (accessType.equals("Web")) {
			bodyContent += "Please sign in to the portal using your new credentials.";
		} else if (accessType.equals("Mobile")) {
			bodyContent += "Please sign in to the mobile aplpication using your new credentials.";
		} else if (accessType.equals("Both")) {
			bodyContent += "Please sign in to the portal and/or mobile application using your new credentials.";
		}
		Password pass = new Password();
		pass.setUsername(username);
		pass.setNewPassword(password);
		pass.setModifiedBy(admin);
		pass.setModifiedDate(dateTime.getCurrentSQLDate());
		if(employeeDAO.hardResetPassword(pass)){
			Email Email = new Email();
			Email.setTo(emailID);
			Email.setBcc(bccmail);
			String body = "<!DOCTYPE html><html lang=\"en\">"
			+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta charset=\"utf-8\">"
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>SWARM</title></head>"
			+ "<body><table>"
			+ "<tr><td><p>Dear " + name + ",</p></td></tr>"											
			+ "<tr><td><p>Your password has been reset. Please use the following credentials to login to SWARM HR.</p></td></tr>"
			+ "<tr><td><p>Username: <strong>"+username+"</strong><br/>"
			+ "Password: <strong>"+ password +"</strong></p></td></tr>"
			+ "<tr><td><p>"+bodyContent+"</p></td></tr>"
			+ "<tr><td><br/><br/><br/>"
			+ "<p style=\"font-size:9px;\">This is an auto-generated email and does not accept any responses.</p></td></tr>"
			+ "</table></body></html>";
			
			String subject = "Dear " + name + ", your password has been reset ! ";
			Email.setSubject(subject);
			Email.setBody(body);					
			aws.SendTextEmail(Email, employeeDAO.fullname(username), fromEmailID);
			session.setAttribute("passwordMessage", "Password reset successful for "+employeeDAO.userDetails(username)+".");
			session.setAttribute("passwordFlag", "true");	
		}else{
			session.setAttribute("passwordMessage", "Failed to reset password for "+employeeDAO.userDetails(username)+".");
			session.setAttribute("passwordFlag", "false");
		}			
		if(request.getServletPath().contains("hardReset"))
			return new ModelAndView("redirect:/ViewEmployeeStatus");
		
		return new ModelAndView("redirect:/EmpMiniProfile?usrname="+username);
	}

	@RequestMapping(value = "/validateMemberID")
	public ModelAndView validateMemberID(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String query = request.getParameter("query");
		request.getParameter("param");
		String message = memberDAO.validateUsername(query);
		request.getSession().setAttribute("MemberUsername", message);
		response.setContentType("text/plain");
		response.getWriter().write(message);
		return null;
	}

	@RequestMapping(value = { "/addMember", "/EventManagement" })
	public ModelAndView addMember(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		Organization org = organizationDAO.get(profile.getOrganizationID());
		String identifier = org.getEmpIdentifier();
		List<Department> listDepartments = departmentDAO.listAllDepartments(profile.getOrganizationID());
		List<Member> membersList = memberDAO.listByOrg(profile.getOrganizationID());

		model.addObject("membersList", membersList);
		model.addObject("departmentList", listDepartments);
		model.addObject("orgDetails", org);
		model.addObject("orgID", identifier);
		model.addObject("newMember", new Member());
		model.setViewName("EventMembers");
		model.addObject("Message", session.getAttribute("MemberSavedMessage"));
		session.setAttribute("MemberSavedMessage", null);
		return model;

	}

	@RequestMapping(value = "/editMemberInfo")
	public ModelAndView editMember(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to view Employee Details.");
			newModel.addObject("reloadFlag", "reloadPage");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String user = request.getParameter("usrname");
		Member editMember = memberDAO.get(user);
		Organization org = organizationDAO.get(profile.getOrganizationID());
		List<Department> listDepartments = departmentDAO.listAllDepartments(profile.getOrganizationID());

		model.addObject("departmentList", listDepartments);
		model.addObject("orgDetails", org);
		model.addObject("editMember", editMember);
		model.setViewName("EditEventMembers");
		return model;

	}

	@RequestMapping(value = "/viewEventMembers")
	public ModelAndView viewMembers(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		String dept = request.getParameter("dept");

		/*
		 * Employee profile = (Employee) session.getAttribute("profile");
		 * List<Member> membersList =
		 * memberDAO.listByOrg(profile.getOrganizationID()); List<Department>
		 * listDepartments =
		 * departmentDAO.listAllDepartments(profile.getOrganizationID());
		 * model.addObject("departmentList", listDepartments);
		 */

		List<Member> membersList = memberDAO.listByDept(dept);
		model.addObject("membersList", membersList);
		model.addObject("idCardSelect", new Member());
		model.addObject("departmentID", dept);
		model.setViewName("ViewEventMembers");
		return model;

	}

	@RequestMapping(value = "/generateGuests")
	public ModelAndView generateGuestMembers(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, NoSuchAlgorithmException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		int guestNumber = Integer.parseInt(request.getParameter("guestNumber"));
		String deptID = request.getParameter("deptID");
		java.sql.Date date = dateTime.getCurrentSQLDate();
		Employee profile = (Employee) session.getAttribute("profile");
		Department dept = departmentDAO.getDept(deptID);
		byte[] idCardLogo;
		if (null != dept.getLogoFile())
			idCardLogo = dept.getLogoFile();
		else
			idCardLogo = organizationDAO.get(profile.getOrganizationID()).getLogoFile();

		for (int i = 0; i < guestNumber; i++) {
			Member member = new Member();
			member.setDepartmentID(deptID);
			member.setOrganizationID(dept.getOrganizationID());
			String timeStamp = dateTime.getCurrentTimeStampForId();
			member.setUsername("randomUser_" + timeStamp);
			String codeData = member.getUsername() + "_qrcode_scanner_" + timeStamp;
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(codeData.getBytes());
			String qrCodeData = Base64.encodeBase64URLSafeString(messageDigest);
			String header = "BEGIN:VCARD";
			String version = "VERSION:3.0";
			String footer = "END:VCARD";
			String final_vCard = String.format("%s%n%s%n%s%n%s%n", header, version, "UID:" + qrCodeData, footer);
			BarcodeQRCode my_code = new BarcodeQRCode(final_vCard, 500, 500, null);
			Image qr_awt_image = my_code.createAwtImage(Color.BLACK, Color.WHITE);
			try {
				ImageIO.write(AWTImageDescriptor.create(qr_awt_image, null), "png", new File("QRCodeImage.png"));
			} catch (IOException e) {
				logger.error(e.getCause());
			}

			File tempFile = new File("QRCodeImage.png");
			FileInputStream fis = new FileInputStream(tempFile);
			int byteLength = (int) tempFile.length(); // bytecount of the
														// file-content
			byte[] fileContent = new byte[byteLength];
			fis.read(fileContent, 0, byteLength);
			member.setQrCodeImage(fileContent);
			member.setQrCodeImageType("image/png");
			employeeDAO.createQRToken(qrCodeData, member.getUsername(), username, fileContent);

			byte[] idCardImage = idCardDAO.guestIDCard(member, dept.getDepartmentName(), idCardLogo, request);
			member.setIdCardImage(idCardImage);
			member.setIdCardImageType("image/png");
			member.setCreatedBy(username);
			member.setCreatedDate(date);
			member.setModifiedBy(username);
			member.setModifiedDate(date);
			memberDAO.saveGuestInfo(member);
			fis.close();
		}
		model.setViewName("redirect:/viewEventMembers?dept=" + deptID);
		return model;

	}

	@RequestMapping(value = "/printIDCards")
	public ModelAndView printID(ModelAndView model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) throws IOException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		List<Member> membersList = memberDAO.listByOrg(profile.getOrganizationID());
		// List<Member> membersList =
		// memberDAO.listByDept("DEPITS170510135921");

		String filename = "Id" + profile.getOrganizationID();
		File tempFile = File.createTempFile(filename, ".pdf", null);

		byte[] id = idCardDAO.idCardPDF(membersList, tempFile, request);
		try {
			FileCopyUtils.copy(id, response.getOutputStream());
		} catch (Exception e) {
			session.setAttribute("Message", "No Members");
			model.addObject("Message", session.getAttribute("Message"));
		}
		// model.setViewName("redirect:/viewMembers");
		return null;

	}

	@RequestMapping(value = { "/printSelectedIDCards" })
	public ModelAndView printSelectedIDCards(@ModelAttribute Member member, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) throws Exception {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String admin = request.getRemoteUser();
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, admin);
		Employee profile = (Employee) session.getAttribute("profile");
		String deptID = member.getDepartmentID();
		Department dept = departmentDAO.getDept(deptID);
		byte[] idCardLogo;
		if (null != dept.getLogoFile())
			idCardLogo = dept.getLogoFile();
		else
			idCardLogo = organizationDAO.get(profile.getOrganizationID()).getLogoFile();

		String departmentName = dept.getDepartmentName();
		List<String> membersList = new ArrayList<String>();
		for (String emp : member.getIdCardUserSelect()) {
			if (!emp.contains("random")) {
				Member newMember = memberDAO.get(emp);
				generateMemberIDCard(admin, departmentName, newMember, idCardLogo);
			}
			membersList.add(emp);
		}
		String filename = "IdCards_";
		File tempFile = File.createTempFile(filename, ".pdf", null);
		byte[] id = idCardDAO.printSelectedMemberIDCards6(membersList, tempFile, member.getDepartmentID(), admin);
		try {
			FileCopyUtils.copy(id, response.getOutputStream());
		} catch (Exception e) {
			session.setAttribute("Message", "No Members");
		}
		session.setAttribute("idCardMessage", null);
		return null;
	}

	private Member generateMemberIDCard(String username, String departmentName, Member member, byte[] idCardLogo)
			throws NoSuchAlgorithmException, IOException {
		java.sql.Date date = dateTime.getCurrentSQLDate();
		String timeStamp = dateTime.getCurrentTimeStampForId();
		String codeData = member.getUsername() + "_user_qrcode_scanner_" + timeStamp;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(codeData.getBytes());
		String qrCodeData = Base64.encodeBase64URLSafeString(messageDigest);

		String header = "BEGIN:VCARD";
		String version = "VERSION:3.0";
		String footer = "END:VCARD";
		String final_vCard = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n", header, version,
				"UID:" + qrCodeData, "ORG:" + member.getCompanyName(),
				"FN:" + member.getFirstname() + " " + member.getLastname(), "ROLE:" + member.getDesignation(),
				"EMAIL:" + member.getEmail(), "TEL;TYPE=CELL:" + member.getMobile(), "GENDER:" + member.getGender(),
				"ADR;TYPE=HOME,PREF:;;" + member.getAddress(), ";" + member.getCity() + ";" + member.getState() + ";"
						+ member.getZipcode() + ";" + member.getCountry(),
				footer);

		BarcodeQRCode my_code = new BarcodeQRCode(final_vCard, 500, 500, null);
		Image qr_awt_image = my_code.createAwtImage(Color.BLACK, Color.WHITE);
		try {
			ImageIO.write(AWTImageDescriptor.create(qr_awt_image, null), "png", new File("QRCodeImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		File tempFile = new File("QRCodeImage.png");
		FileInputStream fis = new FileInputStream(tempFile);
		int byteLength = (int) tempFile.length(); // bytecount of the
													// file-content
		byte[] fileContent = new byte[byteLength];
		fis.read(fileContent, 0, byteLength);
		employeeDAO.createQRToken(qrCodeData, member.getUsername(), username, fileContent);
		fis.close();
		member.setQrCodeImage(fileContent);
		member.setQrCodeImageType("image/png");
		byte[] idCardImage = idCardDAO.memberIDCard(member, departmentName, idCardLogo);
		member.setIdCardImage(idCardImage);
		member.setIdCardImageType("image/png");
		member.setCreatedBy(username);
		member.setCreatedDate(date);
		member.setModifiedBy(username);
		member.setModifiedDate(date);
		memberDAO.saveGuestInfo(member);
		return member;
	}

	@SuppressWarnings("unused")
	private Member generateMemberIDCardBasic(String username, String departmentName, Member member, byte[] idCardLogo)
			throws NoSuchAlgorithmException, IOException {
		java.sql.Date date = dateTime.getCurrentSQLDate();
		byte[] idCardImage = idCardDAO.memberIDCardBasic(member, departmentName, idCardLogo);
		member.setIdCardImage(idCardImage);
		member.setIdCardImageType("image/png");
		member.setCreatedBy(username);
		member.setCreatedDate(date);
		member.setModifiedBy(username);
		member.setModifiedDate(date);
		memberDAO.saveGuestInfo(member);
		return member;
	}

	/**
	 * 
	 * Saves the Member Details. Restricted access to certain users.
	 *
	 * @param member
	 * @param request
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             ModelAndView
	 * @throws NoSuchAlgorithmException
	 * @throws WriterException
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/admin/saveMemberInfo", "/admin/updateMemberInfo" }, method = RequestMethod.POST)
	public ModelAndView saveMemberInfo(@ModelAttribute Member member, HttpServletRequest request, HttpSession session)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, WriterException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		// String organizationID = ((Employee)
		// session.getAttribute("profile")).getOrganizationID();
		// byte[] idCardLogo = (byte[]) session.getAttribute("deptLogo");
		byte[] idCardLogo = departmentDAO.getDept(member.getDepartmentID()).getLogoFile();
		String departmentName = (String) session.getAttribute("departmentName");
		String username = request.getRemoteUser();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		String reqPath = request.getServletPath();
		String timeStamp = dateTime.getCurrentTimeStampForId();
		if (reqPath.equals("/admin/saveMemberInfo")) {
			Organization org = organizationDAO.get(member.getOrganizationID());
			String identifier = org.getEmpIdentifier();

			timeStamp = identifier.toUpperCase() + timeStamp;
			String user = member.getUsername();
			user = user + "_" + identifier.toLowerCase();
			member.setUsername(user);
		}

		String fname = WordUtils.capitalize(member.getFirstname());
		member.setFirstname(fname);
		String lName = WordUtils.capitalize(member.getLastname());
		member.setLastname(lName);

		/*
		 * String codeData = member.getUsername() + "_user_qrcode_scanner_" +
		 * timeStamp; MessageDigest md = MessageDigest.getInstance("MD5");
		 * byte[] messageDigest = md.digest(codeData.getBytes()); String
		 * qrCodeData = Base64.encodeBase64URLSafeString(messageDigest);
		 * 
		 * String header = "BEGIN:VCARD"; String version = "VERSION:3.0"; String
		 * footer = "END:VCARD"; String final_vCard = String.format(
		 * "%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n", header,
		 * version, "UID:" + qrCodeData, "ORG:" + member.getCompanyName(), "FN:"
		 * + member.getFirstname() + " " + member.getLastname(), "ROLE:" +
		 * member.getDesignation(), "EMAIL:" + member.getEmail(),
		 * "TEL;TYPE=CELL:" + member.getMobile(), "GENDER:" +
		 * member.getGender(), "BDAY:" + member.getDob(),
		 * "ADR;TYPE=WORK,PREF:;;" + member.getAddress() + ";" +
		 * member.getCity() + ";" + member.getState() + ";" +
		 * member.getZipcode() + ";" + member.getCountry(), "TEL;TYPE=WORK:" +
		 * member.getDeskPhone() + "-" + member.getExt(), "TEL;TYPE=FAX:" +
		 * member.getFax(), footer);
		 * 
		 * BarcodeQRCode my_code = new BarcodeQRCode(final_vCard, 500, 500,
		 * null); Image qr_awt_image = my_code.createAwtImage(Color.BLACK,
		 * Color.WHITE); // AWTImageDescriptor converter = new
		 * AWTImageDescriptor(); try {
		 * ImageIO.write(AWTImageDescriptor.create(qr_awt_image, null), "png",
		 * new File("QRCodeImage.png")); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 * File tempFile = new File("QRCodeImage.png"); FileInputStream fis =
		 * new FileInputStream(tempFile); int byteLength = (int)
		 * tempFile.length(); // bytecount of the file-content byte[]
		 * fileContent = new byte[byteLength]; fis.read(fileContent, 0,
		 * byteLength); employeeDAO.createQRToken(qrCodeData,
		 * member.getUsername(), username, fileContent);
		 * member.setQrCodeImage(fileContent);
		 * member.setQrCodeImageType("image/png"); try { if
		 * (!member.getProfilePictureAttachment().getContentType().equals(
		 * "application/octet-stream")) {
		 * member.setProfilePic(member.getProfilePictureAttachment().getBytes())
		 * ; member.setProfilePicType(member.getProfilePictureAttachment().
		 * getContentType()); } else member.setProfilePicType("No Pic"); } catch
		 * (Exception exp) { member.setProfilePicType("No Pic"); }
		 */

		member = generateMemberIDCard(username, departmentName, member, idCardLogo);
		member.setCreatedBy(username);
		member.setCreatedDate(date);
		member.setModifiedBy(username);
		member.setModifiedDate(date);

		boolean saveSuccess = memberDAO.saveGuestInfo(member);
		/*
		 * if (reqPath.equals("/admin/saveMemberInfo")) { saveSuccess =
		 * memberDAO.saveGuestInfo(member); } else if
		 * (reqPath.equals("/admin/updateMemberInfo")) { saveSuccess =
		 * memberDAO.updateMemberInfo(member); }
		 */

		if (saveSuccess) {
			/*
			 * byte[] idCardImage = null; Department dept =
			 * departmentDAO.getDept(member.getDepartmentID()); if
			 * (member.getProfilePicType().equalsIgnoreCase("No Pic"))
			 * idCardImage = idCardDAO.idCardWithoutPic(member, dept, request);
			 * else idCardImage = idCardDAO.idCardWithPic(member, dept,
			 * request);
			 * 
			 * member.setIdCardImage(idCardImage);
			 * member.setIdCardImageType("image/png");
			 * memberDAO.updateIDCard(member);
			 */
			session.setAttribute("MemberSavedMessage", "Member Information Added Successfully");
		} else
			session.setAttribute("MemberSavedMessage", "Could Not Add Member");

		if (reqPath.equals("/admin/saveMemberInfo")) {
			return new ModelAndView("redirect:/EventManagement");
		} else if (reqPath.equals("/admin/updateMemberInfo")) {
			ModelAndView model = new ModelAndView();
			model.addObject("Message", "Member Information Updated Successfully.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		} else {
			return new ModelAndView("redirect:/EventManagement");
		}

	}

	@RequestMapping(value = "/EmployeeActivity", method = RequestMethod.GET)
	public ModelAndView viewEmployeeActivity(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Employee Activity Module");
		}

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		model.setViewName("EmployeeActivity/ActivityHome");
		return model;

	}

	@RequestMapping(value = "/empActivity", method = RequestMethod.GET)
	public ModelAndView viewEmployeeActivityByDate(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		String username = request.getRemoteUser();
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");			
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		List<EmployeeActivity> employeeList = new ArrayList<EmployeeActivity>();
		List<EmployeeActivity> adminList = new ArrayList<EmployeeActivity>();
		if (null == session.getAttribute("deptID")) {
			String organizationID = (String) session.getAttribute("organizationID");
			employeeList = employeeDAO.listEmployeeActivityFromPunchInByOrganization(organizationID,
					request.getParameter("date"));
			adminList = employeeDAO.listAdminActivity(organizationID, request.getParameter("date"));
			model.addObject("listAdmins", adminList);
		} else {
			String departmentID = (String) session.getAttribute("deptID");
			employeeList = employeeDAO.listEmployeeActivityFromPunchInByDepartment(departmentID,
					request.getParameter("date"));
		}
		model.addObject("todayDate", dateTime.convertStringToSQLDate(request.getParameter("date")));
		model.addObject("listEmployees", employeeList);
		model.setViewName("EmployeeActivity/CompleteActivity");
		return model;

	}

	@RequestMapping(value = "/viewEmployeeActivity")
	public ModelAndView viewIndividualEmployeeActivity(ModelAndView model, HttpServletRequest request,
			HttpSession session) throws IOException, ParseException {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");

		String auth = request.getParameter("auth");
		if (auth.equals(request.getRemoteUser())) {
			String empOrg = employeeDAO.getOrganizationNameByUsername(request.getParameter("user"));
			String adminOrg = employeeDAO.getOrganizationNameByUsername(request.getRemoteUser());
			if (empOrg.equals(adminOrg)) {
				model.addObject("empName", employeeDAO.userDetails(request.getParameter("user")));
				List<EmployeeActivity> employeeActivity = employeeDAO
						.listUserActivityFromPunchIn(request.getParameter("user"), request.getParameter("date"));
				model.addObject("activity", employeeActivity);
				model.addObject("todayDate", dateTime
						.convertSQLToRegularDate(dateTime.convertStringToSQLDate(request.getParameter("date"))));
				model.setViewName("EmployeeActivity/IndividualActivity");
				return model;
			}
			return new ModelAndView("redirect:/403");
		}
		return new ModelAndView("redirect:/403");
	}

	@RequestMapping(value = "/viewAdminActivity")
	public ModelAndView viewIndividualAdminActivity(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");

		String auth = request.getParameter("auth");
		if (auth.equals(request.getRemoteUser())) {
			String empOrg = employeeDAO.getOrganizationNameByUsername(request.getParameter("user"));
			String adminOrg = employeeDAO.getOrganizationNameByUsername(request.getRemoteUser());
			if (empOrg.equals(adminOrg)) {
				String organizationID = (String) session.getAttribute("organizationID");
				model.addObject("empName", employeeDAO.userDetails(request.getParameter("user")));
				List<EmployeeActivity> employeeActivity = employeeDAO.listAdminActivity(organizationID,
						request.getParameter("user"), request.getParameter("date"));
				model.addObject("activity", employeeActivity);
				model.addObject("todayDate", dateTime
						.convertSQLToRegularDate(dateTime.convertStringToSQLDate(request.getParameter("date"))));
				model.setViewName("EmployeeActivity/AdminActivity");
				return model;
			}
			return new ModelAndView("redirect:/403");
		}
		return new ModelAndView("redirect:/403");

	}

	@RequestMapping(value = "/employee-configuration")
	public ModelAndView addEmployeeHomePage(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		
		String organizationID = (String) session.getAttribute("organizationID");
		Map<String, String> actualProperties = propertiesDAO.listTypesAndValues(organizationID);
		List<Employee> employeeList = new ArrayList<Employee>();
		if(request.isUserInRole("ROLE_MANAGER")){
			employeeList = employeeDAO.listManagerEmployeeNames(organizationID, username);
		}else{
			if (null == session.getAttribute("deptID")) 
				employeeList = employeeDAO.listEmployeeNames(organizationID);
			else{
				String departmentID = (String) session.getAttribute("deptID");
				employeeList = employeeDAO.listDepartmentEmployees(departmentID);
			}				
		}
		model.addObject("employeeList", employeeList);
		
		model.addObject("propertyList", actualProperties);
		model.addObject("leaveConfigList", leaveDAO.listLeaveConfiguration(organizationID));
		model.addObject("UserProperties", new UserProperties());
		model.addObject("leaveConfig", new LeaveConfiguration());
		model.addObject("Message", session.getAttribute("leaveConfigMessage"));
		session.setAttribute("leaveConfigMessage", null);
		model.setViewName("EmployeeConfiguration/ConfigurationHome");
		return model;
	}
	
	@RequestMapping(value = "/updateProperty", method=RequestMethod.POST)
	public ModelAndView updateProperty(@ModelAttribute UserProperties prop, HttpServletRequest request, HttpSession session)
	{
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		prop.setModifiedDate(dateTime.getCurrentSQLDate());
		prop.setOrganizationID((String) session.getAttribute("organizationID"));
		prop.setModifiedBy(request.getRemoteUser());
		propertiesDAO.saveOrUpdate(prop, true);		
		Map<String, String> typeValueMap = propertiesDAO.listTypesAndValues(prop.getOrganizationID());		
		session.setAttribute("typeValueMap", typeValueMap);		
		return new ModelAndView("redirect:/employee-configuration");
	}	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/timesheet-configuration", method = RequestMethod.GET)
	public ModelAndView employeeTimesheetConfiguration(ModelAndView model, HttpServletRequest request, HttpSession session){
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		List<TimesheetCriteria> listTimesheetCriteria = new ArrayList<TimesheetCriteria>();
		List<Employee> empList = new ArrayList<Employee>();
		List<Employee> adminList = new ArrayList<Employee>();
		
		if (null == session.getAttribute("deptID")) {
			String organizationID = (String) session.getAttribute("organizationID");
			listTimesheetCriteria = timesheetCriteriaDAO.ListTimesheetCriteriaByOrg(organizationID);
			empList = employeeDAO.getEmployeeStatus(organizationID);
			adminList = employeeDAO.getEmployeeAdminStatus(organizationID);
			List<DepartmentMember> listDeptAdmins = departmentDAO.listDepartmentAdmins(organizationID);
			model.addObject("adminList", adminList);
			model.addObject("deptAdminList", listDeptAdmins);
		} else {
			empList = employeeDAO.getEmployeeStatusByDepartment((String) session.getAttribute("deptID"));
			listTimesheetCriteria = timesheetCriteriaDAO.ListTimesheetCriteriaByDept((String) session.getAttribute("deptID"));
		}		
		
		Map<String, String> submissionType = new LinkedHashMap<String, String>();
		submissionType.put("Daily", "Daily");
		submissionType.put("Weekly", "Weekly");		

		Map<String, String> timeReporting = new LinkedHashMap<String, String>();
		timeReporting.put("Web", "Web");
		timeReporting.put("Mobile App", "Mobile App");
		timeReporting.put("PunchIn", "PunchIn");
		timeReporting.put("Scanner", "Scanner");
		timeReporting.put("Phone", "Phone");
		timeReporting.put("Email", "Email");
		
		Map<String, String> roundOffLimit = new LinkedHashMap<String, String>();
		roundOffLimit.put("00", "00");
		roundOffLimit.put("15", "15");
		roundOffLimit.put("30", "30");
		roundOffLimit.put("45", "45");
		
		String type = "";
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
		try {
			type = typeValueMap.get("Default Timesheet");
			if (null == type) 					
				type = defaultMap.get("Default Timesheet");				
		} catch (Exception exp) {				
			type = defaultMap.get("Default Timesheet");
		}
		model.addObject("defaultTimeReporting", type);
		model.addObject("empList", empList);
		model.addObject("listTimesheetCriteria", listTimesheetCriteria);
		model.addObject("submissionType", submissionType);
		model.addObject("timeReporting", timeReporting);
		model.addObject("roundOffLimit", roundOffLimit);
		model.addObject("timesheetCriteria", new TimesheetCriteria());	
		model.addObject("Message", session.getAttribute("timesheetConfigurationMessage"));
		session.setAttribute("timesheetConfigurationMessage", null);
		model.setViewName("EmployeeConfiguration/TimesheetConfiguration");
		return model;
	}

	@RequestMapping(value = { "/admin/saveTimesheetCriteria", "/saveTimesheetCriteria" }, method = RequestMethod.POST)
	public ModelAndView saveTimesheetCriteria(@ModelAttribute TimesheetCriteria timesheetCriteria,
			HttpServletRequest request, HttpSession session) {
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		timesheetCriteria.setCreatedBy(name);
		timesheetCriteria.setModifiedBy(name);
		timesheetCriteria.setCreatedDate(date);
		timesheetCriteria.setModifiedDate(date);

		String organizationID = (String) session.getAttribute("organizationID");
		if (null == session.getAttribute("deptID")) {
			timesheetCriteria.setOrganizationID(organizationID);
			timesheetCriteria.setDepartmentID(departmentDAO.getDepartmentID(timesheetCriteria.getUsername()));
		} else {
			timesheetCriteria.setOrganizationID(organizationID);
			timesheetCriteria.setDepartmentID((String) session.getAttribute("deptID"));
		}
		if(timesheetCriteriaDAO.insertOrUpdateTimesheetCriteria(timesheetCriteria))
			session.setAttribute("timesheetConfigurationMessage", "Timesheet Criteria updated successfully.");
		else
			session.setAttribute("timesheetConfigurationMessage", "Failed to update timesheet criteria.");

		return new ModelAndView("redirect:/timesheet-configuration");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/status-report-configuration", method = RequestMethod.GET)
	public ModelAndView employeeStatusReportConfiguration(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		String organizationID = (String) session.getAttribute("organizationID");		
		String reporting = "";
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
		try {
			reporting = typeValueMap.get("Daily Status Report");
			if (null == reporting)
				reporting = defaultMap.get("Daily Status Report");
		} catch (Exception exp) {
			reporting = defaultMap.get("Daily Status Report");
		}
		if(reporting.equalsIgnoreCase("Yes"))
			reporting = "Required";
		List<Employee> employeeList = new ArrayList<Employee>();
		List<Employee> exceptionList = new ArrayList<Employee>();
		if (null == session.getAttribute("deptID")) {
			employeeList = employeeDAO.list(organizationID);
			if (reporting.equalsIgnoreCase("Required"))
				exceptionList = statusReportDAO.listEmployeesWithoutStatusReportByOrganization(organizationID);
			else
				exceptionList = statusReportDAO.listEmployeesWithStatusReportByOrganization(organizationID);
		} else {
			String departmentID = (String) session.getAttribute("deptID");
			employeeList = employeeDAO.listDepartmentEmployees(departmentID);
			if (reporting.equalsIgnoreCase("Required"))
				exceptionList = statusReportDAO.listEmployeesWithoutStatusReportByDepartment(departmentID);
			else
				exceptionList = statusReportDAO.listEmployeesWithStatusReportByDepartment(departmentID);
		}	
		List<Employee> duplicateList = new ArrayList<Employee>();
		for(Employee emp : employeeList){
			for(Employee empl : exceptionList){
				if(emp.getUsername().equals(empl.getUsername()))
					duplicateList.add(emp);				
			}
		}
		employeeList.removeAll(duplicateList);
		model.addObject("employeeList", employeeList);
		model.addObject("statusReportRequirement", reporting);
		model.addObject("exceptionList", exceptionList);
		model.addObject("statusReport", new StatusReport());
		model.addObject("Message", session.getAttribute("statusReportMessage"));
		session.setAttribute("statusReportMessage", null);		
		model.setViewName("EmployeeConfiguration/StatusReportConfiguration");
		return model;
	}
	
	@RequestMapping(value = { "/excludeStatusReportMultiple", "/addStatusReportMultiple" }, method = RequestMethod.POST)
	public ModelAndView addStatusReportForEmployee(@ModelAttribute StatusReport statusReport, HttpServletRequest request, HttpSession session){
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		int updateCount = 0;
		String organizationID = (String) session.getAttribute("organizationID");
		
		StatusReport report = new StatusReport();
		report.setCreatedBy(employeeDAO.userDetails(request.getRemoteUser()));
		report.setCreatedDate(dateTime.getCurrentSQLDate());				
		report.setOrganizationID(organizationID);		
		
		if(request.getServletPath().equals("/addStatusReportMultiple"))			
			report.setStatusReportRequired(true);
		else if(request.getServletPath().equals("/excludeStatusReportMultiple"))
			report.setStatusReportRequired(false);
		
		for(String username: statusReport.getUsersList()){
			report.setUsername(username);
			report.setEmployeeName(employeeDAO.userDetails(username));
			report.setDepartmentID(departmentDAO.getDepartmentID(username));
			if(statusReportDAO.addEmployeeToStatusReport(report))
				updateCount ++;
		}		
		
		session.setAttribute("statusReportMessage", "Added "+updateCount+" employees to status report list.");
		return new ModelAndView("redirect:/status-report-configuration");
	}
	
	@RequestMapping(value = { "/addStatusReport", "/excludeStatusReport" }, method = RequestMethod.GET)
	public ModelAndView removeStatusReportForEmployee(HttpServletRequest request, HttpSession session){
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		
		int updateCount = 0;		
		String organizationID = (String) session.getAttribute("organizationID");
		
		StatusReport report = new StatusReport();
		report.setUsername(request.getParameter("user"));	
		report.setEmployeeName(employeeDAO.userDetails(report.getUsername()));
		report.setCreatedBy(employeeDAO.userDetails(request.getRemoteUser()));
		report.setCreatedDate(dateTime.getCurrentSQLDate());				
		report.setOrganizationID(organizationID);
		report.setDepartmentID(departmentDAO.getDepartmentID(report.getUsername()));
		
		if(request.getServletPath().equals("/addStatusReport"))			
			report.setStatusReportRequired(true);
		else if(request.getServletPath().equals("/excludeStatusReport"))
			report.setStatusReportRequired(false);		
		if(statusReportDAO.addEmployeeToStatusReport(report))
			updateCount ++;
		
		session.setAttribute("statusReportMessage", "Added "+updateCount+" employees to status report list.");
		return new ModelAndView("redirect:/status-report-configuration");
	}
	
	@RequestMapping(value = { "/deleteFromExceptionList" }, method = RequestMethod.GET)
	public ModelAndView deleteEmployeeFromExceptionList(HttpServletRequest request, HttpSession session){
		if(null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
				
		String organizationID = (String) session.getAttribute("organizationID");
		if(statusReportDAO.deleteEmployeeFromStatusReport(request.getParameter("user"), organizationID))
			session.setAttribute("statusReportMessage", "Deleted Employee From Exception list.");
		else
			session.setAttribute("statusReportMessage", "Failed to delete Employee From Exception list.");		
		
		return new ModelAndView("redirect:/status-report-configuration");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/StatusReportAdmin", method = RequestMethod.GET)
	public ModelAndView StatusReportAdmin(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		String organizationID = (String) session.getAttribute("organizationID");		
		String reporting = "";
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
		try {
			reporting = typeValueMap.get("Daily Status Report");
			if (null == reporting)
				reporting = defaultMap.get("Daily Status Report");
		} catch (Exception exp) {
			reporting = defaultMap.get("Daily Status Report");
		}
		if(reporting.equalsIgnoreCase("Yes"))
			reporting = "Required";
		List<Employee> employeeList = new ArrayList<Employee>();
		List<Employee> exceptionList = new ArrayList<Employee>();
		if (null == session.getAttribute("deptID")) {
			employeeList = employeeDAO.list(organizationID);
			if (reporting.equalsIgnoreCase("Required"))
				exceptionList = statusReportDAO.listEmployeesWithoutStatusReportByOrganization(organizationID);
			else
				exceptionList = statusReportDAO.listEmployeesWithStatusReportByOrganization(organizationID);
		} else {
			String departmentID = (String) session.getAttribute("deptID");
			employeeList = employeeDAO.listDepartmentEmployees(departmentID);
			if (reporting.equalsIgnoreCase("Required"))
				exceptionList = statusReportDAO.listEmployeesWithoutStatusReportByDepartment(departmentID);
			else
				exceptionList = statusReportDAO.listEmployeesWithStatusReportByDepartment(departmentID);
		}	
		List<Employee> duplicateList = new ArrayList<Employee>();
		for(Employee emp : employeeList){
			for(Employee empl : exceptionList){
				if(emp.getUsername().equals(empl.getUsername()))
					duplicateList.add(emp);				
			}
		}
		employeeList.removeAll(duplicateList);
		model.addObject("employeeList", employeeList);
		model.addObject("statusReportRequirement", reporting);
		model.addObject("exceptionList", exceptionList);
		model.addObject("statusReport", new StatusReport());
		model.addObject("Message", session.getAttribute("statusReportMessage"));
		session.setAttribute("statusReportMessage", null);		
		model.setViewName("StatusReports/Admin/StatusReportAdmin");
		return model;
	}
	
	@RequestMapping(value = { "/SaveProjectTask" }, method = RequestMethod.POST)
	public ModelAndView SaveProjectTask(@ModelAttribute ProjectTask projectTask, HttpServletRequest request,
			HttpSession session, final RedirectAttributes redirectAttributes) throws ParseException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			if (request.getServletPath().equals("/emp/saveCreatedTimesheet")) {
				newModel.addObject("Message", "You are not logged in. Please login to save Timesheet Details.");
				newModel.addObject("reloadFlag", "reloadPage");
				newModel.setViewName("SuccessScreen");
			} else
				newModel.setViewName("redirect:/login?session");
			return newModel;
		}

		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());
		String organizationID = (String) session.getAttribute("organizationID");

		projectTask.setOrganizationID(organizationID);
		projectTask.setDepartmentID(departmentDAO.getDepartmentID(projectTask.getUsername()));
		projectTask.setCreatedBy(name);
		projectTask.setCreatedDate(date);
		projectTask.setModifiedBy(name);
		projectTask.setModifiedDate(date);
		projectTask.setStatus("Created");

		projectTaskDAO.saveProjectTask(projectTask);

		ModelAndView model = new ModelAndView();
		model.addObject("Message", "Status ReportSaved Successfully.");
		model.addObject("reloadFlag", "reloadPage");
		model.setViewName("redirect:" + request.getHeader("Referer"));

		return model;
	}

	@RequestMapping(value = "/generateProjectTaskHistory")
	public ModelAndView generateProjectTaskHistory(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to view Timesheet Details.");
			newModel.addObject("reloadFlag", "reloadPage");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());

		List<ProjectTaskDaily> listProjectDaily = new ArrayList<ProjectTaskDaily>();

		String date = request.getParameter("start");
		String username = request.getParameter("username");

		listProjectDaily = projectTaskDAO.getReportbyUsername(username, date);

		/*
		 * for (ProjectTaskDaily p : listProjectDaily) {
		 * p.setStatusReport(statusReportDAO.getReport(p.getUsername(),
		 * p.getDate())); }
		 */

		model.addObject("listProjectDaily", listProjectDaily);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date parsed = format.parse(date);
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		model.addObject("startDate", sql);

		model.setViewName("ProjectTaskHistory");
		return model;

	}

	@RequestMapping(value = "/StatusReportUser", method = RequestMethod.GET)
	public ModelAndView StatusReportUser(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		String username = request.getRemoteUser();
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, request.getRemoteUser());

		Employee emp = employeeDAO.getbyUsername(username);
		java.sql.Date date = dateTime.getCurrentSQLDate();

		List<StatusReport> todayReport = new ArrayList<StatusReport>();
		// todayReport = statusReportDAO.getReport(username, date);

		List<ProjectTask> tasks = new ArrayList<ProjectTask>();

		if (todayReport.isEmpty()) {
			tasks = projectTaskDAO.getProjectTaskListOpen(username);
			if (tasks.isEmpty()) {
				ProjectTask pjtTask = new ProjectTask();
				pjtTask.setUsername(username);
				pjtTask.setName(emp.getFirstname() + " " + emp.getLastname());
				pjtTask.setEmail(emp.getEmail());
				pjtTask.setCategory("Daily Status");
				pjtTask.setDescription("Not assigned by Admin");
				pjtTask.setStartDate(date);
				pjtTask.setOrganizationID(emp.getOrganizationID());
				pjtTask.setDepartmentID(departmentDAO.getDepartmentID(username));
				pjtTask.setStatus("Created");
				pjtTask.setCreatedBy(pjtTask.getName());
				pjtTask.setCreatedDate(date);
				projectTaskDAO.saveProjectTask(pjtTask);
				tasks = projectTaskDAO.getProjectTaskListOpen(username);
			}
			List<StatusReport> reports = new ArrayList<StatusReport>();

			for (ProjectTask p : tasks) {
				StatusReport report = new StatusReport();
				report.setUsername(username);
				report.setDate(date);
				report.setTask(p.getCategory());
				report.setTaskID(p.getId());
				report.setOrganizationID(p.getOrganizationID());
				report.setDepartmentID(p.getDepartmentID());
				report.setStatus(p.getStatus());
				report.setReport("");
				report.setHour(0);
				reports.add(report);
			}
			todayReport.addAll(reports);
		}

		ListDailyStatusReport listStatusReports = new ListDailyStatusReport();
		listStatusReports.setStatusReports(todayReport);
		model.addObject("todayReport", listStatusReports);

		List<ProjectTaskDaily> listProjectDaily = projectTaskDAO.getProjectTaskDaily(username, date);

		/*
		 * for (ProjectTaskDaily p : listProjectDaily) {
		 * p.setStatusReport(statusReportDAO.getReport(username, p.getDate()));
		 * }
		 */
		List<ProjectTask> assignedTask = projectTaskDAO.getProjectTaskListAssigned(username);
		model.addObject("assignedTask", assignedTask);

		for (ProjectTask p : assignedTask) {
			p.setReports(statusReportDAO.getReportByID(p.getId()));
			float tot = 0;
			for (StatusReport s : p.getReports())
				tot = tot + s.getHour();
			p.setTimeConsumed(tot);
		}

		model.addObject("listProjectDaily", listProjectDaily);

		java.sql.Date todayDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		DayOfWeek today = LocalDate.now().getDayOfWeek();
		String dayName = today.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		model.addObject("currentDate", todayDate);
		model.addObject("currentDay", dayName);

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date1 = sdf1.parse(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
		java.sql.Date todayDt = new java.sql.Date(date1.getTime());

		model.addObject("todayDt", todayDt);

		model.addObject("statusReport", new StatusReport());
		model.addObject("ProjectTask", new ProjectTask());
		model.setViewName("StatusReportUser");
		return model;
	}

	@RequestMapping(value = "/createReportCriteria")
	public ModelAndView createReportCriteria(ModelAndView model, HttpServletRequest request)
			throws IOException, ParseException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to create new Timesheet.");
			newModel.addObject("reloadFlag", "reloadPage");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}
		String username = request.getParameter("usrname");
		// System.out.println("view"+username);
		// List<List<String>> criterias = new ArrayList<List<String>>();
		List<String> criterias = new ArrayList<String>();
		criterias.add("Project");
		criterias.add("Certification");
		criterias.add("Training");
		criterias.add("Practise");
		criterias.add("Marketing");
		criterias.add("Others");
		// criterias.add( );

		model.addObject("username", username);

		String name = employeeDAO.userDetails(username);

		new StatusCriteria();

		statusReportDAO.getStatusCriteria(username);
		// if(statusCriteria.getId()==0)
		model.addObject("statusCriteria", new StatusCriteria());
		// else
		// model.addObject("statusCriteria", statusCriteria);
		model.addObject("name", name);
		model.addObject("criterias", criterias);
		model.setViewName("AssignStatusCriteria");
		return model;
	}

	@RequestMapping(value = { "/saveStatusCriteria" }, method = RequestMethod.POST)
	public ModelAndView saveStatusCriteria(@ModelAttribute StatusCriteria statusCriteria, HttpServletRequest request,
			final RedirectAttributes redirectAttributes) throws ParseException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			if (request.getServletPath().equals("/emp/saveCreatedTimesheet")) {
				newModel.addObject("Message", "You are not logged in. Please login to save Timesheet Details.");
				newModel.addObject("reloadFlag", "reloadPage");
				newModel.setViewName("SuccessScreen");
			} else
				newModel.setViewName("redirect:/login?session");
			return newModel;
		}

		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		// String types[] = {"","","","","",""};
		String types[] = { null, null, null, null, null, null };
		for (int i = 0; i < statusCriteria.getIdSelect().length; i++) {
			types[i] = statusCriteria.getIdSelect()[i];
		}

		statusCriteria.setType1(types[0]);
		statusCriteria.setType2(types[1]);
		statusCriteria.setType3(types[2]);
		statusCriteria.setType4(types[3]);
		statusCriteria.setType5(types[4]);
		statusCriteria.setType6(types[5]);
		statusCriteria.setCreatedBy(name);
		statusCriteria.setCreatedDate(date);
		statusCriteria.setModifiedBy(name);
		statusCriteria.setModifiedDate(date);

		statusReportDAO.saveOrUpdateCriteria(statusCriteria);
		ModelAndView model = new ModelAndView();
		model.addObject("Message", "Status ReportSaved Successfully.");
		model.addObject("reloadFlag", "reloadPage");
		model.setViewName("SuccessScreen");

		/*
		 * if (frame.equals("true")) { model.addObject("Message",
		 * "Timesheet Saved Successfully."); model.addObject("reloadFlag",
		 * "reloadPage"); model.setViewName("SuccessScreen"); } else
		 * model.setViewName("redirect:" + request.getHeader("Referer"));
		 */
		return model;
	}

	@RequestMapping(value = { "/saveStatusReport" }, method = RequestMethod.POST)
	public ModelAndView saveStatusReport(@ModelAttribute ListDailyStatusReport listDailyStatusReport,
			HttpServletRequest request, final RedirectAttributes redirectAttributes)
					throws IOException, ParseException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();

			newModel.setViewName("redirect:/login?session");
			return newModel;
		}

		ProjectTaskDaily pjtTask = new ProjectTaskDaily();

		float total = 0;
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		for (StatusReport statusReport : listDailyStatusReport.getStatusReports()) {
			statusReport.setCreatedBy(name);
			statusReport.setModifiedBy(name);
			statusReport.setCreatedDate(date);
			statusReport.setModifiedDate(date);
			statusReport.setStatus("Created");
			total = total + statusReport.getHour();
			statusReportDAO.saveOrUpdateReport(statusReport);
		}

		pjtTask.setUsername(listDailyStatusReport.getStatusReports().get(0).getUsername());
		pjtTask.setName(name);
		pjtTask.setDate(date);
		pjtTask.setTotalHours(total);
		pjtTask.setOrganizationID(listDailyStatusReport.getStatusReports().get(0).getOrganizationID());
		pjtTask.setDepartmentID(listDailyStatusReport.getStatusReports().get(0).getDepartmentID());
		pjtTask.setStatus("Created");
		pjtTask.setCreatedBy(name);
		pjtTask.setModifiedBy(name);
		pjtTask.setCreatedDate(date);
		pjtTask.setModifiedDate(date);

		projectTaskDAO.saveOrUpdateProjectTaskDaily(pjtTask);

		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:/StatusReportUser");
		return model;
	}

	@RequestMapping(value = { "/completeProjectTask" }, method = RequestMethod.POST)
	public ModelAndView completeProjectTask(@ModelAttribute ProjectTask ProjectTask, HttpServletRequest request,
			HttpSession session) throws FileNotFoundException, IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}

		Employee profile = employeeDAO.profile(request.getRemoteUser());
		String name = profile.getLastname() + ", " + profile.getFirstname();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		ProjectTask.setModifiedBy(name);
		ProjectTask.setModifiedDate(date);

		try {
			if (!(ProjectTask.getAttachment().getContentType().equals("application/octet-stream"))) {
				ProjectTask.setFileName(ProjectTask.getAttachment().getOriginalFilename());
				ProjectTask.setFileType(ProjectTask.getAttachment().getContentType());
				ProjectTask.setFile(ProjectTask.getAttachment().getBytes());
			}
		} catch (Exception e) {
		}

		ProjectTask.setStatus("Completed");
		ProjectTask.setTimeConsumed(statusReportDAO.timeConsumed(ProjectTask.getId()));
		projectTaskDAO.completeProjectTask(ProjectTask);

		return new ModelAndView("redirect:/StatusReportUser");
	}
	//
	// @RequestMapping(value = { "/viewExpenseFile" }, method =
	// RequestMethod.GET)
	// public ModelAndView viewExpenseFile(HttpServletRequest request,
	// HttpServletResponse response) throws IOException {
	// if (null == request.getRemoteUser()) {
	// ModelAndView newModel = new ModelAndView();
	// newModel.setViewName("redirect:/login?session");
	// return newModel;
	// }
	// int id = Integer.parseInt(request.getParameter("id"));
	// try {
	// Expense exp = expenseDAO.get(id);
	// response.setContentType(exp.getFileType());
	// response.setContentLength(exp.getFile().length);
	// FileCopyUtils.copy(exp.getFile(), response.getOutputStream());
	// } catch (Exception e) {
	// response.setContentType(null);
	// Files file = new Files();
	// FileCopyUtils.copy(file.getFile(), response.getOutputStream());
	// }
	// return null;
	// }

}
