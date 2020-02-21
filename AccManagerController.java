package com.twss.java.spring.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.intuit.ipp.data.Item;
import com.intuit.ipp.services.DataService;
import com.twss.java.spring.ats.SupplierLogin;
import com.twss.java.spring.config.GoogleCloudStorage;
import com.twss.java.spring.config.Variables;
import com.twss.java.spring.customproperty.CustomPropertyConfiguration;
import com.twss.java.spring.customproperty.CustomPropertyDAO;
import com.twss.java.spring.dao.ClientDAO;
import com.twss.java.spring.dao.CommissionDAO;
import com.twss.java.spring.dao.ContactDAO;
import com.twss.java.spring.dao.EmployeeDAO;
import com.twss.java.spring.dao.HelpVideoDAO;
import com.twss.java.spring.dao.MessageDAO;
import com.twss.java.spring.dao.OrganizationDAO;
import com.twss.java.spring.dao.ProjectDAO;
import com.twss.java.spring.dao.ProjectTaskDAO;
import com.twss.java.spring.dao.ShareLinkDAO;
import com.twss.java.spring.dao.SuppliersDAO;
import com.twss.java.spring.dao.TaskDAO;
import com.twss.java.spring.dao.UserPropertiesDAO;
import com.twss.java.spring.dao.VendorDAO;
import com.twss.java.spring.documentsmodule.DocumentsDAO;
import com.twss.java.spring.documentsmodule.EmployeeDirectoryObject;
import com.twss.java.spring.model.Client;
import com.twss.java.spring.model.Commission;
import com.twss.java.spring.model.Contact;
import com.twss.java.spring.model.Email;
import com.twss.java.spring.model.Employee;
import com.twss.java.spring.model.Feature;
import com.twss.java.spring.model.HelpVideos;
import com.twss.java.spring.model.HomeAddr;
import com.twss.java.spring.model.InsuranceDocument;
import com.twss.java.spring.model.Organization;
import com.twss.java.spring.model.PODocument;
import com.twss.java.spring.model.Project;
import com.twss.java.spring.model.ProjectCategories;
import com.twss.java.spring.model.ProjectResource;
import com.twss.java.spring.model.SVCDoc;
import com.twss.java.spring.model.ShareLink;
import com.twss.java.spring.model.Suppliers;
import com.twss.java.spring.model.SwarmVendor;
import com.twss.java.spring.model.Task;
import com.twss.java.spring.model.TaskAssign;
import com.twss.java.spring.model.TaskMessage;
import com.twss.java.spring.model.TaskSearch;
import com.twss.java.spring.model.WorkAddr;
import com.twss.java.spring.quickbooks.QBConverter;
import com.twss.java.spring.quickbooks.QuickBooksController;
import com.twss.java.spring.quickbooks.QuickBooksDAO;
import com.twss.java.spring.quickbooks.QuickBooksDAOHelper;
import com.twss.java.spring.quickbooks.config.QuickBooksAuthConfig;
import com.twss.java.spring.signrequest.SignRequest;
import com.twss.java.spring.signrequest.SignRequestDAO;
import com.twss.java.spring.signrequest.SignRequestHelper;
import com.twss.java.spring.signrequest.SignRequestLog;
import com.twss.java.spring.signrequest.Signers;
import com.twss.java.spring.teamsmodule.TeamDAO;
import com.twss.java.spring.utilclass.CSVUtilClass;
import com.twss.java.spring.utilclass.Search;
import com.twss.java.spring.utilclass.SolrConfigDetails;
import com.twss.java.spring.utilclass.SwarmHRSolrClient;
import com.twss.java.spring.utilitybeans.EmailImpl;
import com.twss.java.spring.utilitybeans.TimeFormatter;

@Controller
public class AccManagerController {

	private static final String SPACE = " ";
	private static final String TAB = "\t";
	private static final String COMMA = ",";
	private static final String DOUBLE_QOUTE = "\"";
	

	final static Logger logger = Logger.getLogger(AccManagerController.class);

	@Autowired
	private VendorDAO vendorDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ClientDAO clientDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ContactDAO contactDAO;
	@Autowired
	private UserController userController;
	@Autowired
	private QuickBooksController qbController;
	@Autowired
	private QuickBooksDAO qbDAO;
	@Autowired
	private QuickBooksDAOHelper qbDAOhelper;
	@Autowired
	private QBConverter qbConverter;
	@Autowired
	private SuppliersDAO suppliersDAO;
	@Autowired
	private ProjectTaskDAO projectTaskDAO;
	@Autowired
	private DocumentsDAO documentsDAO;
	@Autowired
	private ShareLinkDAO shareLinkDAO;
	@Autowired
	private SuppliersDAO supplierDAO;
	@Autowired
	private GoogleCloudStorage gcs;
	@Autowired
	private TimeFormatter dateTime;
	@Autowired
	private EmailImpl aws;
	@Autowired
	private MessageDAO messageDAO;
	@Autowired
	private CustomPropertyDAO customPeropertyDAO;
//	private Object String;
	@Autowired
	private CustomPropertyDAO CustomPropertyDAO;
	
	@Autowired
	private OrganizationDAO organizationDAO;
	@Autowired
	private HelpVideoDAO helpVideoDAO;
	@Autowired
	SwarmHRSolrClient swarmHRSolrClient;
	@Autowired
	private SignRequestDAO signRequestDAO;
	@Autowired
	private SignRequestHelper signRequestHelper;
	
	@Autowired
	private UserPropertiesDAO userPropertiesDAO;
	@Autowired
	private CommissionDAO commissionDao;
	@Autowired
	private TeamDAO teamDAO;
	@Autowired
	private TaskDAO taskDAO;
	@Autowired
	private CustomPropertyDAO customPropertyDAO;
	private int defaultPagination=20;
	

	@ExceptionHandler({ java.lang.NullPointerException.class,
			org.springframework.web.servlet.NoHandlerFoundException.class })
	public ModelAndView pageNotFound(Exception exp) {
		StackTraceElement[] stackTrace = exp.getStackTrace();
		String message = "";
		for (StackTraceElement element : stackTrace)
			message += element.toString() + "<br/>";
		logger.error("Exception: " + exp.getMessage(), exp);
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

	@ExceptionHandler({ java.sql.SQLException.class, org.springframework.dao.DuplicateKeyException.class,
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
			com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException.class })
	public ModelAndView serverError(Exception exp) {
		StackTraceElement[] stackTrace = exp.getStackTrace();
		String message = "";
		for (StackTraceElement element : stackTrace)
			message += element.toString() + "<br/>";
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
	
	@RequestMapping(value = "/MaAssignNewTask")
	public ModelAndView maAssignNewTask(ModelAndView model, HttpServletRequest request, HttpSession session)
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
		
	    employeeUsernames =  teamDAO.getEmployeesUserNamesForManager((String)session.getAttribute("organizationID"), username);
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
    
		
	
	@RequestMapping(value = "/MaPendingTasks")
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
		
		if(!StringUtils.isEmpty(AssignedName)) {
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
		setTaskProperties(model, request, session);
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
		model.addObject("tab",assignmentTasks);
		model.addObject("lastPage", lastPage);
		model.addObject("paginationDefault",paginationDefault);
		model.addObject("raiseByMap", raiseByMap);
		model.addObject("subjectMap", subjectMap);
		model.addObject("categoryMap", categoryMap);
		model.addObject("severityMap", severityMap);
		model.addObject("assignedToMap", assignedToMap);
		model.addObject("searchKeys", searchKeys);
		model.addObject("taskSearch", new TaskSearch());
		model.addObject("manager","yes");
		model.setViewName(viewpage);
		return model;

	}
	
	private void setTaskProperties(ModelAndView model, HttpServletRequest request, HttpSession session) {
		try {
			String username = request.getRemoteUser();
			Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
			//List<String> listCatogery = new ArrayList<String>();
			String[] listCatogery = new String[1];
			String[] listSeverity = new String[1];
			String[] listEnvoronment = new String[1];
			String[] listtaskPriority = new String[1];
			String[] listissueType = new String[1];
			String[] listVersion = new String[1];
			String[] listtaskEnvironment = new String[1];
			String[] listtaskRelease = new String[1];
			String[] listtaskFixedVesrion = new String[1];
			String[] listtaskFoundVesrion = new String[1];

					

			Map<String, String> category = new LinkedHashMap<String, String>();
			String a1 = typeValueMap.get("Task Category");
			String[] a = new String[1];
			a[0] = " ";
			try {
				a = a1.split(",");
				listCatogery = a;
			} catch (Exception e) {
				System.out.println("no task category");
				/*Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
				String a2 = defaultMap.get("Task Category");
				a = a2.split(",");*/
			}
			for (int i = 0; i < a.length; i++) {
				category.put(a[i], a[i]);
			}
			
			//task severity
			Map<String, String> severity = new LinkedHashMap<String, String>();
			String b1 = typeValueMap.get("Task Severity");
			String[] b = new String[1];
			b[0] = " ";
			try {
				b = b1.split(",");
				listSeverity = b;
			} catch (Exception e) {
				System.out.println("no task severity");
				/*Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
				String b2 = defaultMap.get("Task Severity");
				b = b2.split(",");*/
			}
			for (int i = 0; i < b.length; i++) {
				severity.put(b[i], b[i]);
			}
			
			//task priority
			Map<String, String> taskPriority = new LinkedHashMap<String, String>();
			String p1 = typeValueMap.get("Task Priority");
			String[] p = new String[1];
			p[0] = " ";
			try {
				p = p1.split(",");
				listtaskPriority = p;
			} catch (Exception e) {
				System.out.println("no task priority");
				/*
				 * Map<String, String> defaultMap = (Map<String, String>)
				 * session.getAttribute("defaultMap"); String p2 =
				 * defaultMap.get("Task Priority"); `
				 */
			}
			// String[] b = prop.getProperty("task_severity").split(",");
			for (int i = 0; i < p.length; i++) {
				taskPriority.put(p[i], p[i]);
			}
			
			//task environment
			Map<String, String> taskEnvironment = new LinkedHashMap<String, String>();
			String env1 = typeValueMap.get("TaskEnvironment");
			String[] env = new String[1];
			env[0] = " ";
			try {
				env = env1.split(",");
				listtaskEnvironment = env;
			} catch (Exception e) {
				System.out.println("no task environment");
				/*
				 * Map<String, String> defaultMap = (Map<String, String>)
				 * session.getAttribute("defaultMap"); String env2 =
				 * defaultMap.get("TaskEnvironment"); env = env2.split(",");
				 */
			}
			// String[] b = prop.getProperty("task_severity").split(",");
			for (int i = 0; i < env.length; i++) {
				taskEnvironment.put(env[i], env[i]);
			}
			
			//task issue type
			
					Map<String, String> taskissueType = new LinkedHashMap<String, String>();
					String issueType1 = typeValueMap.get("Task IssueType");
					String[] issueType = new String[1];
					issueType[0] = " ";
					try {
						issueType = issueType1.split(",");
						listissueType = issueType;
					} catch (Exception e) { 
						System.out.println("no task environment");
						/*
						 * Map<String, String> defaultMap = (Map<String, String>)
						 * session.getAttribute("defaultMap"); String issueType2 =
						 * defaultMap.get("Task IssueType"); issueType = issueType2.split(",");
						 */
					}
					// String[] b = prop.getProperty("task_severity").split(",");
					for (int i = 0; i < issueType.length; i++) {
						taskissueType.put(issueType[i], issueType[i]);
					}
			       
					//task release
					Map<String, String> taskRelease = new LinkedHashMap<String, String>();
					String release1 = typeValueMap.get("Task Release");
					String[] release = new String[1];
					release[0] = " ";
					try {
						release = release1.split(",");
						listtaskRelease = release;
					} catch (Exception e) {
						System.out.println("no task Release");
						/*
						 * Map<String, String> defaultMap = (Map<String, String>)
						 * session.getAttribute("defaultMap"); String release2 =
						 * defaultMap.get("Task Release"); release = release2.split(",");
						 */
					}
					for (int i = 0; i < release.length; i++) {
						taskRelease.put(release[i], release[i]);
					}
					
					//task fixed in Version
					Map<String, String> taskFixedVesrion = new LinkedHashMap<String, String>();
					String version1 = typeValueMap.get("Task FixedInVersion");
					String[] version = new String[1];
					version[0] = " ";
					try {
						version = version1.split(",");
						listtaskFixedVesrion = version;
					} catch (Exception e) {
						System.out.println("no task FixedInVersion");
						/*
						 * Map<String, String> defaultMap = (Map<String, String>)
						 * session.getAttribute("defaultMap"); String version2 =
						 * defaultMap.get("Task FixedInVersion"); version = version2.split(",");
						 */
					}
					
					// String[] b = prop.getProperty("task_severity").split(",");
					for (int i = 0; i < version.length; i++) {
						taskFixedVesrion.put(version[i], version[i]);
					}
					
					//task fixed in Version
					Map<String, String> taskFoundVesrion = new LinkedHashMap<String, String>();
					String fversion1 = typeValueMap.get("Task FoundInVersion");
					String[] fversion = new String[1];
					fversion[0] = " ";
					try {
						fversion = fversion1.split(",");
						listtaskFoundVesrion = fversion;
					} catch (Exception e) {
						System.out.println("no task FoundInVersion");
						/*
						 * Map<String, String> defaultMap = (Map<String, String>)
						 * session.getAttribute("defaultMap"); String fversion2 =
						 * defaultMap.get("Task FoundInVersion"); fversion = fversion2.split(",");
						 */
					}
					
					// String[] b = prop.getProperty("task_severity").split(",");
					for (int i = 0; i < version.length; i++) {
						taskFoundVesrion.put(fversion[i], fversion[i]);
					}
					String organizationID = (String) session.getAttribute("organizationID");
					List<Project> userProjects = projectDAO.listOfAllProjects(organizationID);//employeeDAO.listUserAssociatedProjects( username, organizationID);
					
					
					List<Task> parentList = taskDAO.listbyOrganizationId(organizationID);
					model.addObject("listCatogery", listCatogery);
					model.addObject("listSeverity ", listSeverity );
					model.addObject("listEnvoronment ", listEnvoronment );
					model.addObject("listtaskPriority ", listtaskPriority );
					model.addObject("listtaskEnvironment", listtaskEnvironment);
					//model.addObject("listissueType ", listissueType );
					model.addObject("listissueType ", listissueType );
					model.addObject("listtaskfixedInVersion",listtaskFixedVesrion );
					model.addObject("listtaskFoundVesrion ",listtaskFoundVesrion );
					model.addObject("listtaskRelease ",listtaskRelease );
					model.addObject("listVersion ",listVersion );
					
					model.addObject("userProjects", userProjects);
					
					
					model.addObject("parentList",parentList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 *
	 * Redirects the user to the Resources Page. Restricted Access only to certain
	 * employee groups.
	 * <p>
	 * This method fetches data from several DAOs and provides the user access to
	 * CRUD operations on Resources.
	 *
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @throws IOException
	 * @see ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/Resources")
	public ModelAndView adminResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("User Name: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<SwarmVendor> listVendor = vendorDAO.list(organizationID);
		List<Suppliers> listSuppliers = suppliersDAO.listByOrg(organizationID);
		List<Client> listClient = clientDAO.list(organizationID);
		List<Project> listProject = projectDAO.list(organizationID);
		List<Contact> listContacts = contactDAO.list();
		logger.info("User Profile Details : " + profile);
		Map<String, String> vndrList = new LinkedHashMap<String, String>();
		for (SwarmVendor vndr : listVendor)
			vndrList.put(vndr.getVendorID(), vndr.getVendorName());

		logger.info("SwarmVendor Details : " + vndrList);
		vndrList.put("DIRECT", "No SwarmVendor / Direct Client");
		Map<String, List<String>> vendrList = new LinkedHashMap<String, List<String>>();
		for (SwarmVendor vndr : listVendor)
			vendrList.put(vndr.getVendorID(), Arrays.asList(vndr.getOrganizationID(), vndr.getVendorName()));

		logger.info("SwarmVendor Details : " + vendrList);

		Map<String, String> clntList = new LinkedHashMap<String, String>();
		for (Client clnt : listClient)
			clntList.put(clnt.getClientID(), clnt.getClientName());

		clntList.put("NO_CLIENT", "No Client");
		Map<String, List<String>> clntsList = new LinkedHashMap<String, List<String>>();
		for (Client clnt : listClient)
			clntsList.put(clnt.getClientID(), Arrays.asList(clnt.getVendorID(), clnt.getClientName()));

		clntsList.put("NO_CLIENT", Arrays.asList("DIRECT", "No Client"));
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> contactTypeList = new LinkedHashMap<String, String>();

		String a1 = typeValueMap.get("Contact Type List");
		String[] a = new String[1];
		a[0] = SPACE;

		try {
			a = a1.split(",");
		} catch (Exception x) {
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Contact Type List");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++)
			contactTypeList.put(a[i], a[i]);

		logger.info("Client Details : " + vendrList);
		model.addObject("projectList", listProject);
		model.addObject("clientList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("supplierList", listSuppliers);
		model.addObject("contactTypeList", contactTypeList);
		model.addObject("contactList", listContacts);

		model.addObject("vndrList", vndrList);
		model.addObject("vendrList", vendrList);
		model.addObject("clntList", clntList);
		model.addObject("clntsList", clntsList);

		model.addObject("editVendor", new SwarmVendor());
		model.addObject("editSupplier", new Suppliers());
		model.addObject("editClient", new Client());
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.setViewName("AccManagerResources");
		return model;
	}

	@RequestMapping(value = "/Clients")
	public ModelAndView clientResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Clients Module Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		List<Client> listClient = clientDAO.list(organizationID);
		List<CustomPropertyConfiguration> cutomPropertiesList = CustomPropertyDAO.listCutomPropertiesRequests1(organizationID);
		List<String> cutomPropertiesList1 = new ArrayList<String>();
		//System.out.println("request.getParameter(\"message\")  1111"+request.getParameter("message"));
		
		if(session.getAttribute("clientMessagecount") ==null || session.getAttribute("clientMessagecount").equals("0")) {
			session.setAttribute("clientMessage", null);
		}
		
		session.setAttribute("clientMessagecount", "0");
		
		for (CustomPropertyConfiguration customPropertyConfiguration : cutomPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Vendor-Client-Supplier")) {
				cutomPropertiesList1.add(customPropertyConfiguration.getPropertyValue());
			}
		}
		String[] cutomPropertiesArray = null;
		for (String property : cutomPropertiesList1) {
			cutomPropertiesArray=property.split(",");
		}
		List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Timesheet Module");
		}
		model.addObject("cutomPropertiesArray", cutomPropertiesArray);
		model.addObject("clientList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("editClient", new Client());
		model.addObject("workAddressList", workAdressList);
		model.setViewName("AccManager/Clients");
		return model;
	}
	@RequestMapping(value = "/clientActive")
	public ModelAndView clientActiveResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String organizationID = request.getParameter("organizationID");
		List<Project> listPoject = projectDAO.listActiveProjects(organizationID);
		List<Project> projectEmployeesList = projectDAO.listProjectEmployees(listPoject);
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		String username = request.getRemoteUser();
		List<CustomPropertyConfiguration> cutomPropertiesList = CustomPropertyDAO.listCutomPropertiesRequests1(organizationID);
		if(session.getAttribute("clientMessagecount") ==null || session.getAttribute("clientMessagecount").equals("0")) {
			session.setAttribute("clientMessage", null);
		}
		
		session.setAttribute("clientMessagecount", "0");
		
		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : cutomPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
				paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
				break;
			}
		}
		String pageNumber = "1";
		if (request.getParameter("page") != null) {
			pageNumber = request.getParameter("page");
		}
		List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		String searchKey = request.getParameter("searchKey");
		
		Search searchKeys = new Search();
		searchKeys.setProject(request.getParameter("projectSearchKey"));
		searchKeys.setVendor(request.getParameter("vendorSearchKey"));
		searchKeys.setClient(request.getParameter("clientSearchKey"));
		searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
		//System.out.println(searchKeys);
		model.addObject("searchKeys", searchKeys);
		
		
		Map<String, String> vendorMap = vendorDAO.activeVendorMap(organizationID, "Y");
		Map<String, String> clintMap = clientDAO.activeClientMap(organizationID, "Y");
		Map<String, String> projectMap = projectDAO.activeProjectMap(organizationID, "Y");
		List<Client> listClient = clientDAO.listClientsByPage(organizationID, pageNumber, paginationDefault, "Y", searchKeys);
		int totalClients = clientDAO.getClientsCount(organizationID, "Y", searchKeys);
		int lastPage = totalClients % paginationDefault == 0 ? (totalClients / paginationDefault) : ((totalClients / paginationDefault) + 1);

		model.addObject("pageAll", pageNumber);
		if(pageNumber.equalsIgnoreCase("All")) {
			pageNumber = Integer.toString(lastPage);
		}
		
		List<Project> projectEmpList = projectDAO.listProjectEmployees(listPoject);
		
		List<String> cutomPropertiesList1 = new ArrayList<String>();
		
		for (CustomPropertyConfiguration customPropertyConfiguration : cutomPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Vendor-Client-Supplier")) {
				cutomPropertiesList1.add(customPropertyConfiguration.getPropertyValue());
			}
		}
		String[] cutomPropertiesArray = null;
		for (String property : cutomPropertiesList1) {
			cutomPropertiesArray=property.split(",");
		}
		Map<String, String> employeeMap1 = projectDAO.employeeMap(organizationID);
		
		// List<Employee> employeeList = employeeDAO.listEmployeeNames(organizationID);
		// Map<String, String> empList = new LinkedHashMap<String, String>();

//		model.addObject("projectMap", uniqueProjects);
		model.addObject("vendorMap",vendorMap);
		model.addObject("clintMap",clintMap);
		model.addObject("projectMap",projectMap);
		model.addObject("employeeMap1",employeeMap1);
		model.addObject("paginationDefault",paginationDefault);
		model.addObject("projectEmpList", projectEmpList);
		model.addObject("vendorList", listVendor);
		model.addObject("listSize", listClient.size());
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("totalClients", totalClients);
		model.addObject("projectList", listPoject);
		model.addObject("projectEmployeesList", projectEmployeesList);
		model.addObject("cutomPropertiesArray", cutomPropertiesArray);
		model.addObject("clientsList", listClient);
		model.addObject("workAddressList", workAdressList);
		model.addObject("editClient", new Client());
		model.addObject("newContact", new Contact());
		model.addObject("searchKey", searchKey);

		model.setViewName("AccManager/clientActive");
		return model;
	}

	@RequestMapping(value = "/clientInActive")
	public ModelAndView clientInActiveResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String organizationID = request.getParameter("organizationID");
		// List<Project> listPoject = projectDAO.listActiveProjects(organizationID);
		String username = request.getRemoteUser();
		String pageNumber = "1";
		if (request.getParameter("page") != null) {
			pageNumber = request.getParameter("page");
		}
		List<CustomPropertyConfiguration> cutomPropertiesList = CustomPropertyDAO.listCutomPropertiesRequests1(organizationID);
		
		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : cutomPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
				paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
				break;
			}
		}
		List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		String searchKey = request.getParameter("searchKey");
		
		Search searchKeys = new Search();
		searchKeys.setProject(request.getParameter("projectSearchKey"));
		searchKeys.setVendor(request.getParameter("vendorSearchKey"));
		searchKeys.setClient(request.getParameter("clientSearchKey"));
		searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
		//System.out.println(searchKeys);
		model.addObject("searchKeys", searchKeys);
		
		List<Client> listClient = clientDAO.listClientsByPage(organizationID, pageNumber, paginationDefault, "N", searchKeys);
		Map<String, String> clintMap = clientDAO.activeClientMap(organizationID, "N");
		//System.out.println(listClient);
		int totalClients = clientDAO.getClientsCount(organizationID, "N", searchKeys);
		int lastPage = totalClients % paginationDefault == 0 ? (totalClients / paginationDefault) : ((totalClients / paginationDefault) + 1);
		
		model.addObject("pageAll", pageNumber);
		if(pageNumber.equalsIgnoreCase("All")) {
			pageNumber = Integer.toString(lastPage);
		}
		model.addObject("clintMap",clintMap);
		model.addObject("paginationDefault",paginationDefault);
		model.addObject("listSize", listClient.size());
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("totalClients", totalClients);
		// model.addObject("projectList", listPoject);

		model.addObject("clientsList", listClient);
		model.addObject("workAddressList", workAdressList);
		model.addObject("editClient", new Client());
		model.addObject("newContact", new Contact());
		model.addObject("searchKey", searchKey);

		model.setViewName("AccManager/clientInActive");
		return model;
	}
	
	@RequestMapping(value="/clientDeactivateEmployees", method=RequestMethod.POST)
	public ModelAndView clientDeactivateEmployees(@ModelAttribute Project project, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		String name = employeeDAO.userDetails(username);
		
		Date date = dateTime.getCurrentSQLDate();
		ModelAndView model = new ModelAndView();
		
		String projectId = request.getParameter("projectID");
		String clientId = request.getParameter("clientID");
		
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		project.setModifiedBy(name);
		project.setModifiedDate(date);
		//System.out.println("projectID--->"+projectId);
		project.setProjectID(projectId);
		projectDAO.deactivateProjectResources(project);
		//List<Project> empList = projectDAO.getProjectAssociatedEmployeeList(projectId);
	
		//check employees for zero
		//deactivate project
		List<Project> projectList = new ArrayList<Project>();
		List<Project> employeeList = new ArrayList<Project>();

		
		StringBuilder clientIds = new StringBuilder("");
	
		clientIds.append("'" + clientId + "'");
			
	    projectList = projectDAO.listClientActiveProjects1(clientIds.toString(), organizationID);
	    
	    if(projectId == null) {
	    	if(!projectList.isEmpty()) {
	    		projectId = projectList.get(0).getProjectID();
	    	}
	    }
		if(projectId!=null) {
			employeeList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		}
		
		
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("clientId",clientId);
		model.addObject("profile", profile);
		model.addObject("empList", employeeList);
		model.addObject("projectList", projectList);

		model.setViewName("AccManager/ClientProjectEmployees");
		return model;
	}
	
	@RequestMapping(value = "/clientAssociatedResources")
	public ModelAndView clientProjectAssociatedResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();

		String clientId =  request.getParameter("clientID");
		String projectId = request.getParameter("projectID");
		
		List<Project> projectList = new ArrayList<Project>();
		List<Project> employeeList = new ArrayList<Project>();

		
		StringBuilder clientIds = new StringBuilder("");
	
		clientIds.append("'" + clientId + "'");
			
	    projectList = projectDAO.listClientActiveProjects1(clientIds.toString(), organizationID);
	    
	    if(projectId == null) {
	    	if(!projectList.isEmpty()) {
	    		projectId = projectList.get(0).getProjectID();
	    	}
	    }
		if(projectId!=null) {
			employeeList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		}
		
		
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("clientId",clientId);
		model.addObject("profile", profile);
		model.addObject("empList", employeeList);
		model.addObject("projectList", projectList);

		model.setViewName("AccManager/ClientProjectEmployees");
		return model;
	}

	@RequestMapping(value = "/clientActivation")
	public ModelAndView clientActivation(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {

		String clientID = request.getParameter("clientID");
		String activationFlag = request.getParameter("activation");
		String message = "sucess";
		logger.info("clientID " + clientID + ", activation flag:" + activationFlag);
        
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<Project> listPoject = projectDAO.listClientActiveProjects(organizationID, clientID);
		
		for (Project project : listPoject) 
			projectDAO.projectActivation(project.getProjectID(), activationFlag);
			
		
		clientDAO.clientActivation(clientID, activationFlag);


		logger.info("message " + message);

		response.setContentType("text/plain");
		response.getWriter().write(message);
		return null;
	}
	//SP-718 start Jithendra
	@RequestMapping(value = "/Vendors")
	public ModelAndView vendorResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		/*
		 * get list of all customly created properties and sort all by alphabetical
		 * order using Colelctions
		 */
		List<CustomPropertyConfiguration> cutomPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);
		if(session.getAttribute("VendorMessagecount") ==null || session.getAttribute("VendorMessagecount").equals("0")) {
			session.setAttribute("vendorMessage", null);
		}
		session.setAttribute("VendorMessagecount", "0");
		/*
		 * Collections.sort(cutomPropertiesList, new
		 * Comparator<CustomPropertyConfiguration>() {
		 * 
		 * public int compare(CustomPropertyConfiguration o1,
		 * CustomPropertyConfiguration o2) { return
		 * o1.getPropertyName().compareTo(o2.getPropertyName()); } });
		 */
		String customPropertyValue = "";
		List<String> vendorClientSupplierList = new ArrayList<String>();
		for(CustomPropertyConfiguration propery : cutomPropertiesList ) {
			if(propery.getPropertyName().equals("Vendor-Client-Supplier")) {
		    customPropertyValue = (String)propery.getPropertyValue();
		    vendorClientSupplierList = Arrays.asList(customPropertyValue.split(","));
		    break;
			}
		}
		
		
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Timesheet Module");
		}
		
//		Employee profile = (Employee) session.getAttribute("profile");
//		String organizationID = profile.getOrganizationID();
//		List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		model.addObject("editVendor", new SwarmVendor());
		model.addObject("newContact", new Contact());
//		model.addObject("workAddressList", workAdressList);
		model.addObject("vendorClientSupplierList", vendorClientSupplierList);
		model.setViewName("AccManager/Vendors");
		return model;
	
	}
	@RequestMapping(value = "/activevendor")
	public ModelAndView activevendorResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		String activeColumn = request.getParameter("tab");
		if(session.getAttribute("VendorMessagecount") ==null || session.getAttribute("VendorMessagecount").equals("0")) {
			session.setAttribute("vendorMessage", null);
		}
		session.setAttribute("VendorMessagecount", "0");
		String viewName = "AccManager/ActiveVendor";

		if (activeColumn.equals("active")) {
			activeColumn = "Y";
			viewName = "AccManager/ActiveVendor";
		} else {
			activeColumn = "N";
			viewName = "AccManager/InActiveVendor";
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		//Custom Properties Pagination
		
		List<CustomPropertyConfiguration> customPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);

		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
				paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
				break;
			}
		}

		
		try {
			String pageNumber = "1";
			if (request.getParameter("page") != null) {
				pageNumber = request.getParameter("page");
			}

//			String searchKey = request.getParameter("searchKey");
			
			Search searchKeys = new Search();
			searchKeys.setProject(request.getParameter("projectSearchKey"));
			searchKeys.setVendor(request.getParameter("vendorSearchKey"));
			searchKeys.setClient(request.getParameter("clientSearchKey"));
			searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
			//System.out.println(searchKeys);
			model.addObject("searchKeys", searchKeys);

			// System.out.println("searchKey--->"+searchKey);
			Map<String, String> vendorMap = vendorDAO.activeVendorMap(organizationID, activeColumn);
			Map<String, String> clintMap = clientDAO.activeClientMap(organizationID, activeColumn);
			Map<String, String> projectMap = projectDAO.activeProjectMap(organizationID, activeColumn);
			List<SwarmVendor> vendorList = vendorDAO.listVendorClientsByPage( organizationID, pageNumber, paginationDefault, searchKeys, activeColumn);
			int totalVendors = vendorDAO.getVendorClientsCount(activeColumn, organizationID, searchKeys);
			int lastPage = totalVendors % paginationDefault == 0 ? (totalVendors / paginationDefault) : ((totalVendors / paginationDefault) + 1);
			
			model.addObject("pageAll", pageNumber);
			if(pageNumber.equalsIgnoreCase("All")) {
				pageNumber = Integer.toString(lastPage);
			}
			Map<String, String> employeeMap1 = projectDAO.employeeMap(organizationID);
			
			model.addObject("vendorMap", vendorMap);
			model.addObject("clintMap", clintMap);
			model.addObject("projectMap", projectMap);
			model.addObject("employeeMap1", employeeMap1);
			model.addObject("totalVendors", totalVendors);
			model.addObject("vendorList", vendorList);
			model.addObject("listSize", vendorList.size());
			model.addObject("pageNumber", pageNumber);
			model.addObject("lastPage", lastPage);
			model.addObject("paginationDefault",paginationDefault);

			/* Getting clients based on the organization */
			List<Client> clientList = clientDAO.list(organizationID);
			model.addObject("clientList", clientList);

			/* Getting projects based on the vendors retrieved above */
			// preparing vendorIds String from vendorList

			StringBuilder vendorIds = new StringBuilder("");
			int count = 1;
			int size = vendorList.size();
			for (SwarmVendor vendor : vendorList) {
				vendorIds.append("'" + vendor.getVendorID() + "'");
				if (count < size) {
					vendorIds.append(",");
				}
				count++;
			}

			List<Project> projectList = new ArrayList<Project>();

			if (size > 0) {
				projectList = projectDAO.listVendorProjects(vendorIds.toString(), organizationID);
			}
			model.addObject("projectList", projectList);

			/* Getting employees based on the vendors retrieved above */

			List<Project> employeeList = new ArrayList<Project>();

			if (size > 0) {
				employeeList = projectDAO.listVendorEmployees(vendorIds.toString(), organizationID);
			}
			model.addObject("employeeList", employeeList);
			model.addObject("employeeListSize", employeeList.size());
			
			
			/*
			 * Collections.sort(cutomPropertiesList, new
			 * Comparator<CustomPropertyConfiguration>() {
			 * 
			 * public int compare(CustomPropertyConfiguration o1,
			 * CustomPropertyConfiguration o2) { return
			 * o1.getPropertyName().compareTo(o2.getPropertyName()); } });
			 */
			String customPropertyValue = "";
			List<String> vendorClientSupplierList = new ArrayList<String>();
			for(CustomPropertyConfiguration propery : customPropertiesList ) {
				if(propery.getPropertyName().equals("Vendor-Client-Supplier")) {
			    customPropertyValue = (String)propery.getPropertyValue();
			    vendorClientSupplierList = Arrays.asList(customPropertyValue.split(","));
			    break;
				}
			}
			model.addObject("vendorClientSupplierList", vendorClientSupplierList);

		} catch (Exception e) {
			logger.error("Exception while getting vendor list", e);
		}

		/*
		 * List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		 * model.addObject("editVendor", new SwarmVendor());
		 * model.addObject("newContact", new Contact());
		 * model.addObject("workAddressList", workAdressList);
		 */
		model.addObject("editVendor", new SwarmVendor());
		model.addObject("newContact", new Contact());
		model.setViewName(viewName);
		return model;
	}
	@RequestMapping(value = "/vendorActivation")
	public ModelAndView vendorActivation(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		String username = request.getRemoteUser();
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		String name = employeeDAO.userDetails(username);
		java.sql.Date date = dateTime.getCurrentSQLDate();
		
		
		String vendorID = request.getParameter("vendorID");
		String activationFlag = request.getParameter("activation");
		
		//System.out.println("username-->"+username);
		//System.out.println("name-->"+name);
		//System.out.println("date-->"+date);
		//System.out.println("vendorID-->"+vendorID);
		//System.out.println("activationFlag-->"+activationFlag);
		
		SwarmVendor vendor = new SwarmVendor();
		vendor.setModifiedBy(name);
		vendor.setModifiedDate(date);
		vendor.setVendorID(vendorID);
		vendor.setActive(activationFlag);
		
		String message = "success";
		logger.info("vendorID " + vendorID + ", activation flag:" + activationFlag);
		
		
		String vendorIds = "'" + vendorID + "'";
		
		if("N".equals(activationFlag)) {
			//deactivating projects
			List<Project> projectList = projectDAO.listVendorProjects(vendorIds, organizationID);
			for(Project project:projectList) {
				project.setModifiedBy(name);
				project.setModifiedDate(date);
				project.setActive(activationFlag);
				projectDAO.projectActivation(project);
			}
			
			//deactivating clients
			List<Client> clientList = clientDAO.list(organizationID);
			for(Client client:clientList) {
				if(client.getVendorID().equals(vendorID) && organizationID.equals(client.getOrganizationID())) {
					client.setModifiedBy(name);
					client.setModifiedDate(date);
					client.setActive(activationFlag);
					clientDAO.clientActivation(client);
				}
			}
		}
		vendorDAO.vendorActivation(vendor);

		logger.info("message " + message);

		response.setContentType("text/plain");
		response.getWriter().write(message);
		return null;
	}
	
	@RequestMapping(value = "/VendorSearch")
	public ModelAndView VendorSearch(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		
		if(null == request.getRemoteUser()) {
			return new ModelAndView("redirect:/login?session");	
		}
		String username = request.getRemoteUser();
		String searchKey = request.getParameter("Name");
		int pageNumber = 1;
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<SwarmVendor> list = vendorDAO.search(searchKey);
		int totalVendors = vendorDAO.getCountOfVendors(organizationID);
		int lastPage = 1;
		logger.info("Vendors Module Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		model.addObject("vendorList",list);
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("totalVendors", totalVendors);
		model.addObject("listSize", list.size());
		model.addObject("editVendor", new SwarmVendor());
		model.addObject("newContact", new Contact());
		model.setViewName("AccManager/Vendors");
		return model;
	}
	//SP-708 end
	
	
	
	@RequestMapping(value = "/Suppliers")
	public ModelAndView supplierResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Suppliers Module Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<Suppliers> listSuppliers = suppliersDAO.listByOrg(organizationID);
		List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		
		List<CustomPropertyConfiguration> cutomPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);
		if(session.getAttribute("SupplierMessagecount") ==null || session.getAttribute("SupplierMessagecount").equals("0")) {
			session.setAttribute("SupplierMessage", null);
		}
		session.setAttribute("SupplierMessagecount", "0");
		
		String customPropertyValue = "";
		List<String> vendorClientSupplierList = new ArrayList<String>();
		for(CustomPropertyConfiguration propery : cutomPropertiesList ) {
			if(propery.getPropertyName().equals("Vendor-Client-Supplier")) {
		    customPropertyValue = (String)propery.getPropertyValue();
		    vendorClientSupplierList = Arrays.asList(customPropertyValue.split(","));
		    break;
			}
		}
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Timesheet Module");
		}
		model.addObject("vendorClientSupplierList", vendorClientSupplierList);
		model.addObject("supplierList", listSuppliers);
		model.addObject("editSupplier", new Suppliers());
		model.addObject("newContact", new Contact());
		model.addObject("workAddressList", workAdressList);
		model.setViewName("AccManager/Suppliers");
		return model;
	}
	@RequestMapping(value = "/activeSuppliers")
	public ModelAndView activeSuppliersResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Suppliers Module Accessed By User: " + username);

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		String activeColumn = request.getParameter("tab");
		if(session.getAttribute("SupplierMessagecount") ==null || session.getAttribute("SupplierMessagecount").equals("0")) {
			session.setAttribute("SupplierMessage", null);
		}
		session.setAttribute("SupplierMessagecount", "0");
		String viewName = "AccManager/ActiveSuppliers";

		if (activeColumn.equals("active")) {
			activeColumn = "Y";
			viewName = "AccManager/ActiveSuppliers";
		} else {
			activeColumn = "N";
			viewName = "AccManager/InActiveSuppliers";
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		//Custom Properties Pagination
		
		List<CustomPropertyConfiguration> customPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);

		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) {
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
				paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
				break;
			}
		}

		
		try {
			String pageNumber = "1";
			if (request.getParameter("page") != null) {
				pageNumber = request.getParameter("page");
			}

//			String searchKey = request.getParameter("searchKey");
			
			Search searchKeys = new Search();
			searchKeys.setProject(request.getParameter("projectSearchKey"));
			searchKeys.setSupplier(request.getParameter("supplierSearchKey"));
			searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
			System.out.println(searchKeys);

			// System.out.println("searchKey--->"+searchKey);
			Map<String, String> employeeMap1 = projectDAO.employeeMap(organizationID);
			List<Employee> EmployeesfullnamesList = employeeDAO.listAllEmployeesfullnames(organizationID);
			List<Suppliers> supplierList = supplierDAO.listSuppliersByPage( organizationID, pageNumber, paginationDefault, searchKeys, activeColumn);
			Map<String, String> supplierMap = supplierDAO.supplierMap(organizationID, activeColumn);
			Map<String, String> projectMap = projectDAO.activeProjectMap(organizationID, activeColumn);
			List<ProjectResource> projectList = employeeDAO.listSuppliersProjects(organizationID);
			int totalSuppliers = supplierDAO.getSuppliersCount(activeColumn, organizationID, searchKeys);
			int lastPage = totalSuppliers % paginationDefault == 0 ? (totalSuppliers / paginationDefault) : ((totalSuppliers / paginationDefault) + 1);
			
			model.addObject("pageAll", pageNumber);
			if(pageNumber.equals("All")) {
				pageNumber = Integer.toString(lastPage);
			}
			
			model.addObject("employeeMap1", employeeMap1);			
			model.addObject("supplierMap", supplierMap);
			model.addObject("projectMap", projectMap);
			model.addObject("totalSuppliers", totalSuppliers);
			model.addObject("supplierList", supplierList);
			model.addObject("projectList", projectList);
			model.addObject("listSize", supplierList.size());
			model.addObject("EmployeesfullnamesList", EmployeesfullnamesList);
			model.addObject("pageNumber", pageNumber);
			model.addObject("lastPage", lastPage);
			model.addObject("paginationDefault",paginationDefault);
			model.addObject("searchKeys",searchKeys);
			
			String customPropertyValue = "";
			List<String> vendorClientSupplierList = new ArrayList<String>();
			for(CustomPropertyConfiguration propery : customPropertiesList ) {
				if(propery.getPropertyName().equals("Vendor-Client-Supplier")) {
			    customPropertyValue = (String)propery.getPropertyValue();
			    vendorClientSupplierList = Arrays.asList(customPropertyValue.split(","));
			    break;
				}
			}
			model.addObject("vendorClientSupplierList", vendorClientSupplierList);

		} catch (Exception e) {
			logger.error("Exception while getting vendor list", e);
		}

		/*
		 * List<WorkAddr> workAdressList = employeeDAO.listWorkAddr(username);
		 * model.addObject("editVendor", new SwarmVendor());
		 * model.addObject("newContact", new Contact());
		 * model.addObject("workAddressList", workAdressList);
		 */
		model.addObject("editSupplier", new Suppliers());
		model.addObject("newContact", new Contact());
		model.setViewName(viewName);
		return model;
	}
			
	@RequestMapping(value = "/supplierActivation")
	public ModelAndView supplierActivation(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		String username = request.getRemoteUser();
		//Employee profile = (Employee) session.getAttribute("profile");
		//String organizationID = profile.getOrganizationID();
		String name = employeeDAO.userDetails(username);
		java.sql.Date date = dateTime.getCurrentSQLDate();
		
		
		String supplierID = request.getParameter("supplierID");
		String activationFlag = request.getParameter("activation");
		
		
		
		//System.out.println("username-->"+username);
		//System.out.println("name-->"+name);
		//System.out.println("date-->"+date);
		//System.out.println("vendorID-->"+vendorID);
		//System.out.println("activationFlag-->"+activationFlag);
		
		Suppliers suppliers = new Suppliers();
		suppliers.setModifiedBy(name);
		suppliers.setModifiedDate(date);
		suppliers.setSupplierID(supplierID);
		suppliers.setActive(activationFlag);
		
		String message = "success";
		logger.info("supplierID " + supplierID + ", activation flag:" + activationFlag);
		
		
		//String vendorIds = "'" + supplierID + "'";
		
		/*if("N".equals(activationFlag)) {
			//deactivating projects
			List<Project> projectList = projectDAO.listVendorProjects(vendorIds, organizationID);
			for(Project project:projectList) {
				project.setModifiedBy(name);
				project.setModifiedDate(date);
				project.setActive(activationFlag);
				projectDAO.projectActivation(project);
			}
			
			//deactivating clients
			List<Client> clientList = clientDAO.list(organizationID);
			for(Client client:clientList) {
				if(client.getVendorID().equals(vendorID) && organizationID.equals(client.getOrganizationID())) {
					client.setModifiedBy(name);
					client.setModifiedDate(date);
					client.setActive(activationFlag);
					clientDAO.clientActivation(client);
				}
			}
		}*/
		supplierDAO.supplierActivation(suppliers);

		logger.info("message " + message);

		response.setContentType("text/plain");
		response.getWriter().write(message);
		return null;
	}

	@RequestMapping(value = "/Projects")
	public ModelAndView projectResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Projects Module Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		List<Project> listProject = projectDAO.list(organizationID);
		if(session.getAttribute("projectMessagecount") ==null || session.getAttribute("projectMessagecount").equals("0")) {
			session.setAttribute("projectMessage", null);
		}
		session.setAttribute("projectMessagecount", "0");
		
       List<Employee> employeeList = employeeDAO.listEmployeeNames(organizationID);
		Map<String, String> empList = new LinkedHashMap<String, String>();
		employeeList.forEach((employee) -> empList.put(employee.getUsername(),
				employee.getLastname() + ", " + employee.getFirstname()));
		model.addObject("employeeList", empList);

		Map<String, Project> uniqueProjects = new LinkedHashMap<String, Project>();
		for (Project p : listProject) {
			uniqueProjects.put(p.getProjectID(), p);
		}
		Map<String, List<String>> employeeMap = new LinkedHashMap<String, List<String>>();
		
		Map<String, Project> uniqueActiveProjects = new LinkedHashMap<String, Project>();
		Map<String, Project> uniqueInActiveProjects = new LinkedHashMap<String, Project>();
		for (Map.Entry<String, Project> entry : uniqueProjects.entrySet()) {
			if("N".equalsIgnoreCase(entry.getValue().getActive()))
			{
				uniqueInActiveProjects.put(entry.getKey(), entry.getValue());
			}
			else
			{
				uniqueActiveProjects.put(entry.getKey(), entry.getValue());
			}
			List<String> empNameList = new ArrayList<String>();
			for (Project p : listProject) {
				if (p.getProjectID().equals(entry.getKey())) {
					empNameList.add(p.getEmployeeName());
				}
			}
			employeeMap.put(entry.getKey(), empNameList);
		}
		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Timesheet Module");
		}

		model.addObject("uniqueActiveProjects", uniqueActiveProjects);
		model.addObject("uniqueInActiveProjects", uniqueInActiveProjects);
		model.addObject("projectMap", uniqueProjects);
		model.addObject("employeeMap", employeeMap);
		model.addObject("projectList", listProject);
		model.addObject("clientsList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.setViewName("AccManager/Projects");
		return model;
	}
	
	@RequestMapping(value = "/active")
	public ModelAndView activeResources(ModelAndView model, HttpServletRequest request, HttpSession session) throws IOException {
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);

		String organizationID = request.getParameter("organizationID");
		/*
		 * Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		 * Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		 */
		
		model.addObject("organizationID",organizationID);
		
		//Custom Properties Pagination
		
		List<CustomPropertyConfiguration> customPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);

		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) { 
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
						paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
						break;
				}
		}


		String pageNumber = "1";
		if (request.getParameter("page") != null) {
			pageNumber = request.getParameter("page");
		}
//		String searchKey = request.getParameter("searchKey");
		
		Search searchKeys = new Search();
		searchKeys.setProject(request.getParameter("projectSearchKey"));
		searchKeys.setVendor(request.getParameter("vendorSearchKey"));
		searchKeys.setClient(request.getParameter("clientSearchKey"));
		searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
		System.out.println(searchKeys);

		// System.out.println("searchKey--->"+searchKey);

		List<Project> listProject = projectDAO.listProjectsByPage(organizationID, pageNumber, paginationDefault, "Y", searchKeys);
		
		Map<String, String> vendorMap = vendorDAO.activeVendorMap(organizationID, "Y");
		Map<String, String> clintMap = clientDAO.activeClientMap(organizationID, "Y");
		
		Map<String, String> employeeMap1 = projectDAO.employeeMap(organizationID);
		
		model.addObject("vendorMap", vendorMap);
		model.addObject("clintMap", clintMap);
		
		model.addObject("employeeMap1", employeeMap1);
		
		

		List<Project> projectEmpList = projectDAO.listProjectEmployees(listProject);

//		List<Employee> employeeList = employeeDAO.listEmployeeNames(organizationID);
//		Map<String, String> empList = new LinkedHashMap<String, String>();
//		employeeList.forEach((employee) -> empList.put(employee.getUsername(), employee.getLastname() + ", " + employee.getFirstname()));
//		model.addObject("employeeList", empList);

		/*
		 * Map<String, Project> uniqueProjects = new LinkedHashMap<String, Project>();
		 * for (Project p : listProject) { uniqueProjects.put(p.getProjectID(), p); }
		 */
		Map<String, Map<String,String>> employeeMap = new LinkedHashMap<String, Map<String,String>>();               
		//List<String> inactivatedProjects=new ArrayList<>();
		//Map<String, String> userproperties=userPropertiesDAO.listTypesAndValues(organizationID);
		for (Project p : listProject) {
			Map<String,String> empNameList = new LinkedHashMap<String,String>();
			for (Project pr : projectEmpList) {
				if (p.getProjectID().equals(pr.getProjectID())) {
					empNameList.put(pr.getUsername(),pr.getEmployeeName());
				}
			}
			
			
			/*
			 * if(userproperties!=null && userproperties.get("ProjectAutoInactive")!=null &&
			 * userproperties.get("ProjectAutoInactive").equals("Enable")) {
			 * 
			 * 
			 * List<Project> projectAllEmpList =
			 * projectDAO.getProjectAllAssociatedEmployeeList(p.getProjectID(),
			 * organizationID); if(empNameList.size()==0 && projectAllEmpList!=null &&
			 * projectAllEmpList.size()>0) { //call to inactiveProject
			 * inactivatedProjects.add(p.getProjectID());
			 * request.setAttribute("projectID",p.getProjectID());
			 * request.setAttribute("activation","N"); String
			 * message=projectAutoInActivation(request,session);
			 * logger.info(p.getProjectID()+"Inactivated Project message"+message); }else
			 * employeeMap.put(p.getProjectID(), empNameList);
			 * 
			 * }else
			 */
				employeeMap.put(p.getProjectID(), empNameList);
		}
		
		//inactivatedProjects
		/*
		 * if(inactivatedProjects.size()>0) { List<Project> newList = new ArrayList<>();
		 * for(String projectid:inactivatedProjects) { listProject.stream() .forEach(x
		 * -> { if (x.getProjectID().equals(projectid)) { newList.add(x); } }); }
		 * 
		 * logger.info("inactivated list"+newList); listProject.removeAll(newList);
		 * logger.info("activated list"+listProject); }
		 */
			
		
		String customPropertyValue = "";
		List<String> vendorClientSupplierList = new ArrayList<String>();
		for(CustomPropertyConfiguration propery : customPropertiesList ) {
			if(propery.getPropertyName().equals("Vendor-Client-Supplier")) {
		    customPropertyValue = (String)propery.getPropertyValue();
		    vendorClientSupplierList = Arrays.asList(customPropertyValue.split(","));
		    break;
			}
		}
		model.addObject("vendorClientSupplierList", vendorClientSupplierList);

		
		int totalProjects = projectDAO.getProjectsCount(organizationID, "Y", searchKeys);
		int lastPage = totalProjects % paginationDefault == 0 ? (totalProjects / paginationDefault) : ((totalProjects / paginationDefault) + 1);
		model.addObject("pageAll", pageNumber);
		if(pageNumber.equals("All")) {
			pageNumber = Integer.toString(lastPage);
		}
		
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("totalProjects", totalProjects);
		model.addObject("paginationDefault",paginationDefault);
		
//		model.addObject("uniqueActiveProjects", uniqueActiveProjects);
//		model.addObject("uniqueInActiveProjects", uniqueInActiveProjects);

//		model.addObject("projectMap", uniqueProjects);
		model.addObject("employeeMap", employeeMap);
		model.addObject("projectList", listProject);
		Map<String, String> projectMap = projectDAO.activeProjectMap(organizationID, "Y");
		model.addObject("projectMap", projectMap);
		model.addObject("listSize", listProject.size());
		/*
		 * model.addObject("clientsList", listClient); 
		 * model.addObject("vendorList", listVendor);
		 */
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.addObject("searchKeys", searchKeys);

		model.setViewName("AccManager/active"); 
		return model; 

	}
	
	@RequestMapping(value = "/inactive")
	public ModelAndView inactiveResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);
		
		String organizationID = request.getParameter("organizationID");
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		
		model.addObject("organizationID",organizationID);		
		//Custom Properties Pagination
		
		List<CustomPropertyConfiguration> customPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);

		int paginationDefault=defaultPagination;
		for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) { 
		if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
					paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
					break;
					}
		}

		
		String pageNumber= "1";
		if(request.getParameter("page")!=null) {
			pageNumber = request.getParameter("page");
		}
		//String searchKey = request.getParameter("searchKey");
		
		Search searchKeys = new Search();
		searchKeys.setProject(request.getParameter("projectSearchKey"));
		searchKeys.setVendor(request.getParameter("vendorSearchKey"));
		searchKeys.setClient(request.getParameter("clientSearchKey"));
		searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
		System.out.println(searchKeys);
		//System.out.println("searchKey--->"+searchKey);
		
		List<Project> listProject = projectDAO.listProjectsByPage(organizationID, pageNumber, paginationDefault, "N", searchKeys);
		int totalProjects = projectDAO.getProjectsCount(organizationID, "N", searchKeys);
		int lastPage = totalProjects%paginationDefault==0?(totalProjects/paginationDefault):((totalProjects/paginationDefault)+1);
		model.addObject("pageAll", pageNumber);
		if(pageNumber.equals("All")) {
			pageNumber = Integer.toString(lastPage);
		}
		model.addObject("listSize", listProject.size());
		model.addObject("pageNumber", pageNumber);
		model.addObject("lastPage", lastPage);
		model.addObject("totalProjects", totalProjects);
		model.addObject("paginationDefault",paginationDefault);

		
		List<Project> projectEmpList = projectDAO.listProjectEmployees(listProject);
//		List<Project> projectEmpList = projectDAO.

		List<Employee> employeeList = employeeDAO.listEmployeeNames(organizationID);
		Map<String, String> empList = new LinkedHashMap<String, String>();
		employeeList.forEach((employee) -> empList.put(employee.getUsername(),
				employee.getLastname() + ", " + employee.getFirstname()));
		model.addObject("employeeList", empList);

		/*Map<String, Project> uniqueProjects = new LinkedHashMap<String, Project>();
		for (Project p : listProject) {
			uniqueProjects.put(p.getProjectID(), p);
		}*/
		Map<String, List<String>> employeeMap = new LinkedHashMap<String, List<String>>();
		
		Map<String, Project> uniqueActiveProjects = new LinkedHashMap<String, Project>();
		Map<String, Project> uniqueInActiveProjects = new LinkedHashMap<String, Project>();
		for (Project p : listProject) {
			if("N".equalsIgnoreCase(p.getActive())) {
				uniqueInActiveProjects.put(p.getProjectID(), p);
			} else {
				uniqueActiveProjects.put(p.getProjectID(), p);
			}
			List<String> empNameList = new ArrayList<String>();
			for (Project pr : projectEmpList) {
				if (p.getProjectID().equals(pr.getProjectID())) {
					System.out.println("projectID:"+p.getProjectID()+"---PR Project ID:"+pr.getProjectID()+"---EMPName:"+pr.getEmployeeName());
					empNameList.add(pr.getEmployeeName());
				}
			}
			employeeMap.put(p.getProjectID(), empNameList);
		}
		Map<String, String> projectMap = projectDAO.activeProjectMap(organizationID, "N");
		
		
		model.addObject("projectMap", projectMap);
		model.addObject("uniqueActiveProjects", uniqueActiveProjects);
		model.addObject("uniqueInActiveProjects", uniqueInActiveProjects);

//		model.addObject("projectMap", uniqueProjects);
		model.addObject("employeeMap", employeeMap);
		model.addObject("projectList", listProject);

		model.addObject("clientsList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.addObject("searchKeys", searchKeys);
		model.setViewName("AccManager/inactive");
		return model;
		
	}
	
	@RequestMapping(value="/deactivateEmployees", method=RequestMethod.POST)
	public ModelAndView deactivaterEmployees(@ModelAttribute Project project, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		String name = employeeDAO.userDetails(username);
		Date date = dateTime.getCurrentSQLDate();
		ModelAndView model = new ModelAndView();
		String projectId = project.getProjectID();
		project.setModifiedBy(name);
		project.setModifiedDate(date);
		//System.out.println("projectID--->"+projectId);
		projectDAO.deactivateProjectResources(project);
		List<Project> empList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		 Map<String, String> userproperties=userPropertiesDAO.listTypesAndValues(organizationID);
		if(userproperties!=null && userproperties.get("ProjectAutoInactive")!=null && userproperties.get("ProjectAutoInactive").equals("Enable")) {	
		if(empList.size()==0) {
			request.setAttribute("projectID",projectId);
			 request.setAttribute("activation","N");
			String message=projectAutoInActivation(request,session);
			model.addObject("Message",message);
			model.setViewName("ExpenseSubmitSuccessScreen");
			return model;
		}		
		}
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("empList", empList);
		model.setViewName("AccManager/ProjectEmployees");
		return model;
	}
	
	@RequestMapping(value = "/projectsexport")
	public void InvoiceExport(ModelAndView model, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ParseException {

		try {
			String action = request.getParameter("action");			
			Search searchKeys = new Search();
			searchKeys.setProject(request.getParameter("projectSearchKey"));
			searchKeys.setVendor(request.getParameter("vendorSearchKey"));
			searchKeys.setClient(request.getParameter("clientSearchKey"));
			searchKeys.setEmployee(request.getParameter("employeeSearchKey"));
			
			String status = request.getParameter("status");
			String organizationID = request.getParameter("organizationID");
			String pageNumber= "1";
			if(request.getParameter("page")!=null) {
				pageNumber = request.getParameter("page");
			}
			
			//Custom Properties Pagination
			
			List<CustomPropertyConfiguration> customPropertiesList = customPeropertyDAO.listCutomPropertiesRequests1(organizationID);

			int paginationDefault=defaultPagination;
			for (CustomPropertyConfiguration customPropertyConfiguration : customPropertiesList) { 
			if (customPropertyConfiguration.getPropertyName().equalsIgnoreCase("Pagination-Default")) {
						paginationDefault=Integer.valueOf(customPropertyConfiguration.getPropertyValue());
						break;
						}
			}
			
			
			List<Project> listProject = projectDAO.listProjectsByPage(organizationID, pageNumber, paginationDefault, status, searchKeys);
			
			logger.info(listProject);
			
			List<Project> projectEmpList = projectDAO.listProjectEmployees(listProject);
			
			//Map<String, List<String>> employeeMap = new LinkedHashMap<String, List<String>>();
			
			Map<String, Project> uniqueActiveProjects = new LinkedHashMap<String, Project>();
			Map<String, Project> uniqueInActiveProjects = new LinkedHashMap<String, Project>();
			for (Project p : listProject) {
				if("N".equalsIgnoreCase(p.getActive())) {
					uniqueInActiveProjects.put(p.getProjectID(), p);
				} else {
					uniqueActiveProjects.put(p.getProjectID(), p);
				}
				List<String> empNameList = new ArrayList<String>();
				for (Project pr : projectEmpList) {
					if (p.getProjectID().equals(pr.getProjectID())) {
						System.out.println("projectID:"+p.getProjectID()+"---PR Project ID:"+pr.getProjectID()+"---EMPName:"+pr.getEmployeeName());
						empNameList.add("["+pr.getEmployeeName()+"]");
						
					}
				}
				if(empNameList.size()==0)
					p.setEmployeeName("");
				else {
					p.setEmployeeName(empNameList.toString());	
				}
				
				//employeeMap.put(p.getProjectID(), empNameList);
			}
			
				if (action.equals("csv")) {
					CSVUtilClass csv = new CSVUtilClass();
					response = csv.writeProjects(listProject, response,(status.equals("Y")?"Active":"InActive"));
				}
		

		} catch (Exception e) {
			logger.error("Error while Exporting Time Based Invoices as CSV --" + e.getMessage());
		}

	}
	
	
	
	
	@RequestMapping(value="/vendorDeactivateEmployees", method=RequestMethod.POST)
	public ModelAndView vendorDeactivateEmployees(@ModelAttribute Project project, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		String name = employeeDAO.userDetails(username);
		
		Date date = dateTime.getCurrentSQLDate();
		ModelAndView model = new ModelAndView();
		
		String projectId = request.getParameter("projectID");
		String vendorId = request.getParameter("vendorID");
		
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		project.setModifiedBy(name);
		project.setModifiedDate(date);
		//System.out.println("projectID--->"+projectId);
		project.setProjectID(projectId);
		projectDAO.deactivateProjectResources(project);
		//List<Project> empList = projectDAO.getProjectAssociatedEmployeeList(projectId);
	
		//check employees for zero
		//deactivate project
		List<Project> projectList = new ArrayList<Project>();
		List<Project> employeeList = new ArrayList<Project>();

		
		StringBuilder vendorIds = new StringBuilder("");
	
		vendorIds.append("'" + vendorId + "'");
			
	    projectList = projectDAO.listVendorActiveProjects(vendorIds.toString(), organizationID);
	    
	    if(projectId == null) {
	    	if(!projectList.isEmpty()) {
	    		projectId = projectList.get(0).getProjectID();
	    	}
	    }
		if(projectId!=null) {
			employeeList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		}
		
		
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("vendorId",vendorId);
		model.addObject("profile", profile);
		model.addObject("empList", employeeList);
		model.addObject("projectList", projectList);

		model.setViewName("AccManager/VendorProjectEmployees");
		return model;
	}
	
	@RequestMapping(value = "/projectActivation")
	public ModelAndView projectActivation(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws IOException {
		String username = request.getRemoteUser();
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		String name = employeeDAO.userDetails(username);
		java.sql.Date date = dateTime.getCurrentSQLDate();
		
		
//		String organizationID = (String) session.getAttribute("organizationID");
		String projectID = request.getParameter("projectID");
		String activationFlag = request.getParameter("activation");
		String message = "success";
		logger.info("projectID "+projectID+", activation flag:" + activationFlag);
		
		Project project = new Project();
		project.setModifiedBy(name);
		project.setModifiedDate(date);
		project.setActive(activationFlag);
		project.setProjectID(projectID);
		project.setOrganizationID(organizationID);
		
		projectDAO.projectActivation(project);
		QuickBooksAuthConfig qbconfig=qbDAO.getAuthToken(organizationID);
		if (null != qbconfig && qbconfig.getRealmId()!=null) {
			
			Project qbProjectObject = projectDAO.activeProjectDetails(organizationID,projectID);
			qbProjectObject.setActive(activationFlag);
			logger.info("QuickBooks Configured for: " + organizationID);
			DataService service = qbController.getService(session);
			qbDAO.activeORdeactiveProject(service,organizationID,  qbProjectObject);
			
			
		} else
			logger.warn("QuickBooks Not Configured for: " + organizationID);
		
		logger.info("message "+message);
		
		response.setContentType("text/plain");
		response.getWriter().write(message);
		return null;
	}
	

	public String projectAutoInActivation(HttpServletRequest request,HttpSession session) {
		String username = request.getRemoteUser();
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		String name = employeeDAO.userDetails(username);
		java.sql.Date date = dateTime.getCurrentSQLDate();
		
		
//		String organizationID = (String) session.getAttribute("organizationID");
		String projectID = request.getAttribute("projectID").toString();
		String activationFlag = request.getAttribute("activation").toString();
		String message = "Project In-Activated due to No Associated Employees.";
		logger.info("projectID "+projectID+", activation flag:" + activationFlag);
		
		Project project = new Project();
		project.setModifiedBy(name);
		project.setModifiedDate(date);
		project.setActive(activationFlag);
		project.setProjectID(projectID);
		project.setOrganizationID(organizationID);
		
		projectDAO.projectActivation(project);
		QuickBooksAuthConfig qbconfig=qbDAO.getAuthToken(organizationID);
		if (null != qbconfig && qbconfig.getRealmId()!=null) {
			
			Project qbProjectObject = projectDAO.activeProjectDetails(organizationID,projectID);
			qbProjectObject.setActive(activationFlag);
			logger.info("QuickBooks Configured for: " + organizationID);
			DataService service = qbController.getService(session);
			qbDAO.activeORdeactiveProject(service,organizationID,  qbProjectObject);
			
			
		} else
			logger.warn("QuickBooks Not Configured for: " + organizationID);
		
		logger.info("message "+message);
		
		
		return message;
	}
	
	@RequestMapping(value = "/associatedResources")
	public ModelAndView viewProjectAssociatedResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		String projectId = request.getParameter("projectID");
		List<Project> empList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		
		
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("profile", profile);
		model.addObject("empList", empList);
		model.setViewName("AccManager/ProjectEmployees");
		return model;
	}

	@RequestMapping(value = "/vendorAssociatedResources")
	public ModelAndView vendorProjectAssociatedResources(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();

		String vendorId =  request.getParameter("vendorId");
		String projectId = request.getParameter("projectID");
		List<Project> projectList = new ArrayList<Project>();
		List<Project> employeeList = new ArrayList<Project>();

		
		StringBuilder vendorIds = new StringBuilder("");
	
		vendorIds.append("'" + vendorId + "'");
			
	    projectList = projectDAO.listVendorActiveProjects(vendorIds.toString(), organizationID);
	    
	    if(projectId == null) {
	    	if(!projectList.isEmpty()) {
	    		projectId = projectList.get(0).getProjectID();
	    	}
	    }
		if(projectId!=null) {
			employeeList = projectDAO.getProjectAssociatedEmployeeList(projectId, organizationID);
		}
		
		
		model.addObject("newProject", new Project());
		model.addObject("projectId",projectId);
		model.addObject("vendorId",vendorId);
		model.addObject("profile", profile);
		model.addObject("empList", employeeList);
		model.addObject("projectList", projectList);

		model.setViewName("AccManager/VendorProjectEmployees");
		return model;
	}

	//SP-969 Start
		@RequestMapping(value = "/supplierAssociatedResources")
		public ModelAndView supplierProjectAssociatedResources(ModelAndView model, HttpServletRequest request, HttpSession session)
				throws IOException {
			String username = request.getRemoteUser();
			if (null == username)
				return new ModelAndView("redirect:/sessionExpiredWithinModal");

			Employee profile = (Employee) session.getAttribute("profile");
			String organizationID = profile.getOrganizationID();

			String supplierID =  request.getParameter("supplierId");
			String projectId = request.getParameter("projectID");
			
			System.out.println("supplierId  "+supplierID+"************projectId  "+projectId);
			
			List<ProjectResource> projectList = new ArrayList<ProjectResource>();
			List<ProjectResource> employeeList = new ArrayList<ProjectResource>();
			List<ProjectResource> projectResourceList = employeeDAO.listSuppliersProjectsBySupplierID(supplierID, organizationID);
			
			//List<Project> projectList = new ArrayList<Project>();
			//Employee employee = new Employee();
			projectList = projectDAO.listSupplierActiveProjects(supplierID, organizationID);
		    
		    if(projectId == null) {
		    	if(!projectList.isEmpty()) {
		    		projectId = projectList.get(0).getProjectID();
		    	}
		    }
			if(projectId!=null) {
				employeeList = projectDAO.getProjectSupplierAssociatedEmployeeList(projectId, supplierID, organizationID);
			}
			List<Employee> EmployeesfullnamesList = employeeDAO.listAllEmployeesfullnames(organizationID);
			
			
			model.addObject("newProjectResource", new ProjectResource());
			model.addObject("projectID",projectId);
			model.addObject("supplierID",supplierID);
			model.addObject("profile", profile);
			model.addObject("empList", employeeList);
			model.addObject("EmployeesfullnamesList", EmployeesfullnamesList);
			model.addObject("projectList", projectList);
			model.addObject("projectResourceList", projectResourceList);
			model.setViewName("AccManager/SupplierProjectEmployees");
			return model;
		}
		@RequestMapping(value="/supplierDeactivateEmployees", method=RequestMethod.POST)
		public ModelAndView supplierDeactivateEmployees(@ModelAttribute ProjectResource project, HttpServletRequest request, HttpSession session) {
			String username = request.getRemoteUser();
			String name = employeeDAO.userDetails(username);
			List<ProjectResource> projectList = new ArrayList<ProjectResource>();
			List<ProjectResource> employeeList = new ArrayList<ProjectResource>();
			Date date = dateTime.getCurrentSQLDate();
			ModelAndView model = new ModelAndView();
			
			String projectID = request.getParameter("projectID");
			String supplierID = request.getParameter("supplierId");
			
			Employee profile = (Employee) session.getAttribute("profile");
			String organizationID = profile.getOrganizationID();
			
			project.setModifiedBy(name);
			project.setModifiedDate(date);
			System.out.println("projectID--->"+projectID);
			List<ProjectResource> projectResourceList = employeeDAO.listSuppliersProjectsBySupplierID(supplierID, organizationID);
			projectList = projectDAO.listSupplierActiveProjects(supplierID, organizationID);
			if(projectID == null) {
		    	if(!projectList.isEmpty()) {
		    		projectID = projectList.get(0).getProjectID();
		    	}
		    }
			project.setProjectID(projectID);
			projectDAO.deactivateProjectResources1(project);

		    
			if(projectID == null) {
		    	if(!projectList.isEmpty()) {
		    		projectID = projectList.get(0).getProjectID();
		    	}
		    }
			if(projectID!=null) {
				employeeList = projectDAO.getProjectSupplierAssociatedEmployeeList(projectID, supplierID, organizationID);
			}
		    model.addObject("newProjectResource", new ProjectResource());
			model.addObject("projectID",projectID);
			model.addObject("supplierID",supplierID);
			model.addObject("profile", profile);
			model.addObject("empList", employeeList);
			model.addObject("projectResourceList", projectResourceList);
			model.addObject("projectList", projectList);

			model.setViewName("AccManager/SupplierProjectEmployees");
			return model;
		}
	@RequestMapping(value = "/viewProject")
	public ModelAndView viewProject(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		Project project = projectDAO.get(request.getParameter("project"));
		List<Project> listProject = projectDAO.list(organizationID);
		
		Map<String, Project> uniqueProjects = new LinkedHashMap<String, Project>();
		for (Project p : listProject) {
			uniqueProjects.put(p.getProjectID(), p);
		}
		
		model.addObject("projectMap", uniqueProjects);
		model.addObject("profile", profile);
		model.addObject("clientsList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("project", project);
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.setViewName("AccManager/ProjectDetails");
		return model;
	}
	
	@RequestMapping(value = "/viewProjectdetails")
	public ModelAndView viewProjectdetails(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		Project project = projectDAO.get(request.getParameter("project"));
		List<Project> listProject = projectDAO.list(organizationID);
		
		Map<String, Project> uniqueProjects = new LinkedHashMap<String, Project>();
		for (Project p : listProject) {
			uniqueProjects.put(p.getProjectID(), p);
		}
		
		model.addObject("projectMap", uniqueProjects);
		model.addObject("profile", profile);
		model.addObject("clientsList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("project", project);
		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.setViewName("AccManager/ProjectDetailsview");
		return model;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/assignProject")
	public ModelAndView assignProject(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {

		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		String username = request.getParameter("user");
		Employee profile = employeeDAO.profile(username);
		Project project = projectDAO.get(request.getParameter("project"));
		Map<String, String> suppliers = supplierDAO.listSuppliers(profile.getOrganizationID());
		List<HomeAddr> homeAddressList = employeeDAO.listHomeAddr(username);
		List<WorkAddr> workAddressList = employeeDAO.listWorkAddr(username);

		if (homeAddressList.isEmpty()) {
			model.addObject("Message",
					"Employee does not have a valid home address. Please create a valid home address first.");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (workAddressList.isEmpty()) {
			model.addObject("Message",
					"Employee does not have a valid work address. Please create a valid work address first.");
			model.setViewName("SuccessScreen");
			return model;
		}
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> invoiceDueDays = new LinkedHashMap<String, String>();
		String[] c = new String[1];
		c[0] = SPACE;
		try {
			c = typeValueMap.get("Invoice Due Days").split(",");
		} catch (Exception x) {
			logger.info("No Invoice Due Days Found For: " + profile.getFirstname() + SPACE + profile.getLastname());
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			c = defaultMap.get("Invoice Due Days").split(",");
		}
		for (int i = 0; i < c.length; i++)
			invoiceDueDays.put(c[i], c[i] + " Days");

		model.addObject("dueDays", invoiceDueDays);

		Map<String, String> payRateFrequency = new LinkedHashMap<String, String>();
		payRateFrequency.put("Per Annum", "Per Annum");
		payRateFrequency.put("Per Month", "Per Month");
		payRateFrequency.put("Per Hour", "Per Hour");

		model.addObject("payFrequency", payRateFrequency);
		model.addObject("billFrequency", payRateFrequency);

		Map<String, String> weekdays = new LinkedHashMap<String, String>();
		weekdays.put("Monday", "Monday");
		weekdays.put("Sunday", "Sunday");
		model.addObject("weekdays", weekdays);

		Map<String, String> invoiceType = new LinkedHashMap<String, String>();
		invoiceType.put("Monthly", "Monthly");
		invoiceType.put("Bi-Monthly", "Bi-Monthly");
		invoiceType.put("Weekly", "Weekly");
		invoiceType.put("Bi-Weekly", "Bi-Weekly");
		invoiceType.put("4/5 Weeks", "4/5 Weeks");
		model.addObject("invoiceType", invoiceType);

		model.addObject("homeList", homeAddressList);
		model.addObject("workList", workAddressList);
		model.addObject("empProfile", profile);
		model.addObject("suppliers", suppliers);
		model.addObject("project", project);
		model.addObject("editProject", new Project());
		model.addObject("projectResource", new ProjectResource());
		model.setViewName("AccManager/AssignProjectToEmployee");
		return model;
	}

	@RequestMapping(value = "/accManager/assignProjectToEmployee")
	public ModelAndView assignProjectToEmployee(@ModelAttribute ProjectResource ProjectResource,
			HttpServletRequest request, HttpSession session) throws IOException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/sessionExpiredWithinModal");

		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		try {
			if (ProjectResource.getEndDate().toString().equals("1900-01-01"))
				ProjectResource.setEndDate(null);
		} catch (Exception e) {
			logger.error("Error with project end date.");
		}
		try {
			if ((ProjectResource.getProjectName().trim().isEmpty()) || (null == ProjectResource.getProjectName()))
				ProjectResource.setProjectName(employeeDAO.getProjectName(ProjectResource.getProjectID()));
		} catch (Exception e1) {
			logger.error("Error while fetching project name.");
		}
		ProjectResource.setBillRateFrequency("Per Hour");
		ProjectResource.setPayRateFrequency("Per Hour");
		String msg = "";
		ProjectResource.setCreatedBy(name);
		ProjectResource.setCreatedDate(date);
		if (employeeDAO.addProjectResource(ProjectResource))
			msg = "Project associated successfully. Please refresh the page to see the changes.";
		else
			msg = "Project association failed. Employee might be still working on the same Project.";

		logger.info("Project of " + employeeDAO.userDetails(ProjectResource.getUsername()) + " updated by: "
				+ employeeDAO.userDetails(request.getRemoteUser()));
		ModelAndView returnModel = new ModelAndView();
		returnModel.addObject("Message", msg);
		returnModel.setViewName("SuccessScreen");
		return returnModel;

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/projectsListByPage")
	public ModelAndView projectResourcesByPage(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		logger.info("Projects Module Accessed By User: " + username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		Map<String, String> listVendor = vendorDAO.listVendors(organizationID);
		Map<String, List<String>> listClient = clientDAO.listClients(organizationID);
		List<Project> listProject = projectDAO.listProjectsByPage(organizationID, request.getParameter("page"), 50, "Y", null);
		List<Contact> listContacts = contactDAO.listProjectContacts(organizationID);

		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> contactTypeList = new LinkedHashMap<String, String>();

		String a1 = typeValueMap.get("Contact Type List");
		String[] a = new String[1];
		a[0] = SPACE;

		try {
			a = a1.split(",");
		} catch (Exception x) {
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Contact Type List");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++)
			contactTypeList.put(a[i], a[i]);

		model.addObject("clientsList", listClient);
		model.addObject("vendorList", listVendor);
		model.addObject("projectList", listProject);
		model.addObject("contactTypeList", contactTypeList);
		model.addObject("contactList", listContacts);

		model.addObject("editProject", new Project());
		model.addObject("newContact", new Contact());
		model.addObject("prevPageNumber", Integer.parseInt(request.getParameter("page")) - 1);
		model.addObject("nextPageNumber", Integer.parseInt(request.getParameter("page")) + 1);
		model.addObject("cpageno", request.getParameter("page"));
		model.addObject("deleteProjectMessage", session.getAttribute("deleteProjectMessage"));
		session.setAttribute("deleteProjectMessage", null);
		model.setViewName("AccManager/ProjectsByPage");
		return model;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/viewSVCContacts")
	public ModelAndView viewClientContacts(HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		String contactType = request.getParameter("type");
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> contactTypeList = new LinkedHashMap<String, String>();
		// String contactType = request.getParameter("contactType");
		String a1 = typeValueMap.get("Contact Type List");
		String[] a = new String[1];
		a[0] = SPACE;
		try {
			a = a1.split(",");
			if (a1.isEmpty()) {
				Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
				String a2 = defaultMap.get("Contact Type List");
				a = a2.split(",");
			}
		} catch (Exception x) {
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Contact Type List");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++) {
			if (!a[i].isEmpty()) {
				logger.info("this is externalmanagers contact"+a[i]);
				if(a[i].equalsIgnoreCase("External Manager")) {
					if (contactType.equals("project"))
							contactTypeList.put(a[i], a[i]);
				}else
					contactTypeList.put(a[i], a[i]);
				
			}
		}
		model.addObject("contactTypeList", contactTypeList);
		List<Contact> listContacts = contactDAO.listSingleSVCContacts(request.getParameter("typeID"));
		model.addObject("contactList", listContacts);
		model.addObject("newContact", new Contact());
// external manager contacts list		
		List<Contact> emanagersContacts=new ArrayList<Contact>();
		for(Contact cont:listContacts) {
			if(cont.getContactType()!=null && cont.getContactType().equalsIgnoreCase("External Manager"))
				emanagersContacts.add(cont);
		}
		
		model.addObject("emanagers", emanagersContacts);
		
		
		if (contactType.equals("client")) {
			model.addObject("clientID", request.getParameter("typeID"));
			model.setViewName("AccManager/ClientContacts");
		} else if (contactType.equals("vendor")) {
			model.addObject("vendorID", request.getParameter("typeID"));
			model.setViewName("AccManager/VendorContacts");
		} else if (contactType.equals("supplier")) {
			model.addObject("newSupplierLogin", new SupplierLogin());
			model.addObject("supplierID", request.getParameter("typeID"));
			model.setViewName("AccManager/SupplierContacts");
		} else if (contactType.equals("project")) {
			model.addObject("projectID", request.getParameter("typeID"));
			model.setViewName("AccManager/ProjectContacts");
		}

		return model;
	}
	
	
	
	
	

	//sp-974
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addProjectContacts")
	public ModelAndView addProjectContacts(HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		String contactType = request.getParameter("type");
		
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> contactTypeList = new LinkedHashMap<String, String>();
		String a1 = typeValueMap.get("Contact Type List");
		String[] a = new String[1];
		a[0] = " ";
		try {
			a = a1.split(",");
			if (a1.isEmpty()) {
				Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
				String a2 = defaultMap.get("Contact Type List");
				a = a2.split(",");
			}
		} catch (Exception x) {
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Contact Type List");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++) {
			if (!a[i].isEmpty()) {
				if(a[i].equalsIgnoreCase("External Manager")) {
					if (contactType.equals("project"))
						contactTypeList.put(a[i], a[i]);
				}else
					contactTypeList.put(a[i], a[i]);
			}
		}
		
		String organizationID=request.getParameter("organizationID");
		logger.info("project id= "+request.getParameter("typeID")+" project name= "+request.getParameter("projectName")+" client ID= "+request.getParameter("clientID")+" client Name= "+request.getParameter("clientName")+" vendor ID= "+request.getParameter("vendorID")+" vendor Name= "+request.getParameter("vendorName"));
		model.addObject("contactTypeList", contactTypeList);
		List<Contact> listContactsofClient = contactDAO.listContactsofProject(request.getParameter("typeID"),request.getParameter("clientID"),request.getParameter("vendorID"),"ClientContact");
		List<Contact> listContactsofVendor = contactDAO.listContactsofProject(request.getParameter("typeID"),request.getParameter("clientID"),request.getParameter("vendorID"),"VendorContact");
		List<Contact> listContactsofSupplier = contactDAO.listContactsofSupplier(request.getParameter("typeID"),request.getParameter("empId"));
		List<EmployeeDirectoryObject> listEmployee = contactDAO.getEmployeeContacts(organizationID);
		List<Contact> assignVendorContacts = contactDAO.listofAssignedVendorContacts(request.getParameter("vendorID"),request.getParameter("typeID"),request.getParameter("empId"));
		List<Contact> assignClientContacts = contactDAO.listofAssignedClientContacts(request.getParameter("clientID"),request.getParameter("typeID"),request.getParameter("empId"));
		List<Contact> assignSupplierContacts = contactDAO.listofAssignedSupplierContacts(request.getParameter("typeID"),"SupplierContact",request.getParameter("empId"));
		List<Contact> assignEmpContacts = contactDAO.listofAssignedEmployeeContacts(request.getParameter("typeID"),"EmployeeContact",request.getParameter("empId"));
		List<Contact> assignAllContacts = contactDAO.listofAllAssignedContacts(request.getParameter("typeID"),request.getParameter("empId"));
		model.addObject("assignVendorContacts", assignVendorContacts);
		model.addObject("assignClientContacts", assignClientContacts);
		model.addObject("assignSupplierContacts", assignSupplierContacts);
		model.addObject("assignEmpContacts", assignEmpContacts);
		model.addObject("assignAllContacts", assignAllContacts);
		model.addObject("listContactsofClient", listContactsofClient);
		model.addObject("listContactsofVendor", listContactsofVendor);
		model.addObject("listContactsofSupplier", listContactsofSupplier);
		model.addObject("listEmployee", listEmployee);
		model.addObject("newContact", new Contact());
		model.addObject("newEmployee", new EmployeeDirectoryObject());
		model.addObject("projectID", request.getParameter("typeID"));
		model.addObject("clientID", request.getParameter("clientID"));
		model.addObject("vendorID", request.getParameter("vendorID"));
		model.addObject("projectName", request.getParameter("projectName"));
		model.addObject("clientName", request.getParameter("clientName"));
		model.addObject("vendorName", request.getParameter("vendorName"));
		model.addObject("emp_username", request.getParameter("empId"));
		model.addObject("organizationID", organizationID);

		
		if (contactType.equals("client")) {
			model.addObject("clientID", request.getParameter("typeID"));
			model.setViewName("AccManager/ClientContacts");
		} else if (contactType.equals("vendor")) {
			model.addObject("vendorID", request.getParameter("typeID"));
			model.setViewName("AccManager/VendorContacts");
		} else if (contactType.equals("supplier")) {
			model.addObject("newSupplierLogin", new SupplierLogin());
			model.addObject("supplierID", request.getParameter("typeID"));
			model.setViewName("AccManager/SupplierContacts");
		} else if (contactType.equals("project")) {
			model.addObject("projectID", request.getParameter("typeID"));
			model.setViewName("AccManager/AddProjectContacts");
		}

		return model;
	}
	
	
	//

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/viewSVCDocuments")
	public ModelAndView viewSVCDocuments(HttpServletRequest request, HttpSession session) throws Exception {
		String username = request.getRemoteUser();
		String typeID = request.getParameter("forID");
		String typeAssociation = request.getParameter("forType");
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);

		model.addObject("docList", documentsDAO.listSVCDocuments(typeID));
		model.addObject("podocList", documentsDAO.listPODocuments(typeID));
		model.addObject("insurancedocList", documentsDAO.listInsuranceDocuments(typeID));
		model.addObject("typeID", typeID);
		model.addObject("site_url", Variables.site_url);
		if (typeAssociation.contains("Document"))
			model.addObject("typeAssociation", typeAssociation);
		else
			model.addObject("typeAssociation", typeAssociation + "Document");
		List<ShareLink> sharedLinkList = shareLinkDAO.listSVCShareLinks(typeID);
		for (ShareLink shareLink : sharedLinkList) {
			String token = shareLink.getToken();
			token = token.replace("[", "");
			shareLink.setToken(token);
		}
		
		Employee profile = (Employee) session.getAttribute("profile");
		List<SignRequest> signRequestList = signRequestDAO.getSignRequestsForUsername(profile.getOrganizationID(), typeID);
		List<SignRequestLog> SignRequestLog = signRequestDAO.getSignRequestLogList(profile.getOrganizationID());
		for (SignRequest s : signRequestList) {
			boolean flag = true;
			for (SignRequestLog log : SignRequestLog) {
				if (log.getFileID().equals(s.getFileID())) {
					flag = false;
					s.setSignRequestLog(log);
					break;
				}
			}
			if (flag) {
				String documentURL = request.getParameter("documentURL");
				if (null != documentURL && documentURL.equals(s.getDocumentURL())) {
					String response = signRequestHelper.getDocumentFromURL(documentURL);
					if (null != response) {
						String status = signRequestHelper.getParameterValue(response, "status");
						List<Signers> signers = signRequestHelper.getSignersFromResponse(response);
						if (null != status)
							if (status.equals("si") || status.equals("sd")) {
								SignRequestLog signRequestLog = new SignRequestLog();
								signRequestLog.setFileID(s.getFileID());
								signRequestLog.setSigner1Name(
										signers.get(0).getFirstName() + " " + signers.get(0).getLastName());
								signRequestLog.setSigner1Email(signers.get(0).getEmail());
								signRequestLog.setSigner1Timestamp(signers.get(0).getTimestamp());
								if (signers.size() > 1) {
									signRequestLog.setSigner2Name(
											signers.get(1).getFirstName() + " " + signers.get(1).getLastName());
									signRequestLog.setSigner2Email(signers.get(1).getEmail());
									signRequestLog.setSigner2Timestamp(signers.get(1).getTimestamp());
									signRequestLog.setSigner3Name("");
									signRequestLog.setSigner3Email("");
									signRequestLog.setSigner3Timestamp("");
								}
								if (signers.size() > 2) {
									signRequestLog.setSigner3Name(
											signers.get(2).getFirstName() + " " + signers.get(2).getLastName());
									signRequestLog.setSigner3Email(signers.get(2).getEmail());
									signRequestLog.setSigner3Timestamp(signers.get(2).getTimestamp());
								}
								String signedFilePath = signRequestHelper.getParameterValue(response, "pdf");
								String signLogPathResponse = signRequestHelper.getParameterValueJSON(response,
										"signing_log");
								String signLogFilePath = signRequestHelper.getParameterValue(signLogPathResponse,
										"pdf");
								try {
									URL url = new URL(signedFilePath);
									BufferedInputStream bis = new BufferedInputStream(url.openStream());
									File tempFile = new File(
											request.getRealPath("/resources/" + s.getFileID() + "_Signed.pdf"));
									FileOutputStream fis = new FileOutputStream(tempFile);
									byte[] signedFile = new byte[1024];
									int count = 0;
									while ((count = bis.read(signedFile, 0, 1024)) != -1) {
										fis.write(signedFile, 0, count);
									}
									fis.close();
									bis.close();
									String gcsPath = profile.getUsername() + "/SignRequestLog/" + s.getFileID()
											+ "_Signed.pdf";
									signRequestLog.setSignedFileGcsPath(gcsPath);
									signedFile = Files.readAllBytes(tempFile.toPath());
									if (gcs.uploadDocument(profile.getOrganizationID(), gcsPath, signedFile)) {
										tempFile.delete();
										url = new URL(signLogFilePath);
										bis = new BufferedInputStream(url.openStream());
										tempFile = new File(
												request.getRealPath("/resources/" + s.getFileID() + "_Log.pdf"));
										fis = new FileOutputStream(tempFile);
										byte[] signedLogFile = new byte[1024];
										count = 0;
										while ((count = bis.read(signedLogFile, 0, 1024)) != -1) {
											fis.write(signedLogFile, 0, count);
										}
										fis.close();
										bis.close();
										gcsPath = profile.getUsername() + "/SignRequestLog/" + s.getFileID()
												+ "_Log.pdf";
										signRequestLog.setSignLogGcsPath(gcsPath);
										signedLogFile = Files.readAllBytes(tempFile.toPath());
										if (gcs.uploadDocument(profile.getOrganizationID(), gcsPath, signedLogFile)) {
											tempFile.delete();
											signRequestDAO.saveSignRequestLog(signRequestLog,
													profile.getLastname() + ", " + profile.getFirstname(),
													profile.getOrganizationID());
										}
									}
									s.setSignRequestLog(signRequestLog);
								} catch (IOException e) {
									e.printStackTrace();
								}

							} else
								s.setSingersList(signers);
					}
				} else {
					String[] signers = s.getSigners().split(",");
					List<String> signersList = new ArrayList<String>();
					for (String si : signers)
						signersList.add(si);
					s.setSignersList(signersList);
				}
			}
		}
		model.addObject("signRequestList", signRequestList);
		model.addObject("sharedList", sharedLinkList);
		model.addObject("newDoc", new SVCDoc());
		model.addObject("newPODoc", new PODocument());
		model.addObject("newInsuranceDoc", new InsuranceDocument());
		model.addObject("email", new Email());
		model.addObject("documentMessage", session.getAttribute("addSVCDocMessage"));
		session.setAttribute("addSVCDocMessage", null);
		model.setViewName("AccManager/SVCDocuments");
		return model;
	}

	@RequestMapping(value = "/updateSVCDocTitle")
	public ModelAndView updateSVCDocTitle(@ModelAttribute SVCDoc doc, HttpServletRequest request, HttpSession session) {
		if (null == request.getRemoteUser()) {
			ModelAndView model = new ModelAndView();
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		documentsDAO.updateSVCDocTitle(doc);
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	@RequestMapping(value = "/saveSVCDoc")
	public ModelAndView SaveSVCDocument(@ModelAttribute SVCDoc svcdoc, HttpServletRequest request,
			HttpSession session) {
		String username = request.getRemoteUser();
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		String msg = "";
		String timestamp = dateTime.getCurrentTimeStamp();
		svcdoc.setTimeStamp(timestamp.substring(11, (timestamp.length())));
		String fileName = svcdoc.getTypeID() + "_" + svcdoc.getDocType() + "_" + timestamp;
		String organizationID = ((Employee) session.getAttribute("profile")).getOrganizationID();
		try {
			if (svcdoc.getDocument().getContentType().equals("application/pdf")) {
				svcdoc.setFileType("PDF");
			} else if (svcdoc.getDocument().getContentType().contains("image")) {
				svcdoc.setFileType("Picture");
			}
			svcdoc.setFile(svcdoc.getDocument().getBytes());
			svcdoc.setContentType(svcdoc.getDocument().getContentType());
			svcdoc.setOrganizationID(organizationID);
			svcdoc.setCreateDate(dateTime.getCurrentSQLDate());
			svcdoc.setCreatedBy(employeeDAO.userDetails(request.getRemoteUser()));
			svcdoc.setFileName(fileName);
			svcdoc.setGcsPath(
					svcdoc.getTypeAssociation() + "/" + svcdoc.getTypeID() + "/" + "documents/" + svcdoc.getFileName());

			if (gcs.uploadDocument(organizationID, svcdoc.getGcsPath(), svcdoc.getFile())) {
				if (documentsDAO.uploadSVCDoc(svcdoc))
					msg = "Document Successfully Uploaded";
				else
					msg = "Failed to Upload Document";
			} else {
				msg = "Failed to Upload Document";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			msg = "Failed to Upload Document";
		}
		logger.info(msg);
		session.setAttribute("addSVCDocMessage", msg);
		String typeAssc = svcdoc.getTypeAssociation();
		typeAssc = typeAssc.replaceAll("Document", "");
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	@RequestMapping(value = "/savePODoc")
	public ModelAndView savePODocument(@ModelAttribute PODocument podoc, HttpServletRequest request,
			HttpSession session) {
		String username = request.getRemoteUser();
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		String msg = "";
		String timestamp = dateTime.getCurrentTimeStamp();
		podoc.setTimeStamp(timestamp.substring(11, (timestamp.length())));
		String fileName = podoc.getTypeID() + "_PODocument_" + timestamp;
		String organizationID = ((Employee) session.getAttribute("profile")).getOrganizationID();
		try {
			if (podoc.getDocument().getContentType().equals("application/pdf")) {
				podoc.setFileType("PDF");
			} else if (podoc.getDocument().getContentType().contains("image")) {
				podoc.setFileType("Picture");
			}
			podoc.setFile(podoc.getDocument().getBytes());
			podoc.setContentType(podoc.getDocument().getContentType());
			podoc.setOrganizationID(organizationID);
			podoc.setCreateDate(dateTime.getCurrentSQLDate());
			podoc.setCreatedBy(employeeDAO.userDetails(request.getRemoteUser()));
			podoc.setFileName(fileName);
			podoc.setGcsPath(
					podoc.getTypeAssociation() + "/" + podoc.getTypeID() + "/" + "documents/" + podoc.getFileName());

			if (gcs.uploadDocument(organizationID, podoc.getGcsPath(), podoc.getFile())) {
				if (documentsDAO.uploadPODoc(podoc))
					msg = "Document Successfully Uploaded";
				else
					msg = "Failed to Upload Document";
			} else {
				msg = "Failed to Upload Document";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			msg = "Failed to Upload Document";
		}
		logger.info(msg);
		session.setAttribute("addSVCDocMessage", msg);
		String typeAssc = podoc.getTypeAssociation();
		typeAssc = typeAssc.replaceAll("Document", "");
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	@RequestMapping(value = "/saveInsuranceDoc")
	public ModelAndView saveInsuranceDocument(@ModelAttribute InsuranceDocument insurancedoc,
			HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		String msg = "";
		String timestamp = dateTime.getCurrentTimeStamp();
		insurancedoc.setTimeStamp(timestamp.substring(11, (timestamp.length())));
		String fileName = insurancedoc.getTypeID() + "_InsuranceDocument_" + timestamp;
		String organizationID = ((Employee) session.getAttribute("profile")).getOrganizationID();
		try {
			if (insurancedoc.getDocument().getContentType().equals("application/pdf")) {
				insurancedoc.setFileType("PDF");
			} else if (insurancedoc.getDocument().getContentType().contains("image")) {
				insurancedoc.setFileType("Picture");
			}
			insurancedoc.setFile(insurancedoc.getDocument().getBytes());
			insurancedoc.setContentType(insurancedoc.getDocument().getContentType());
			insurancedoc.setOrganizationID(organizationID);
			insurancedoc.setCreateDate(dateTime.getCurrentSQLDate());
			insurancedoc.setCreatedBy(employeeDAO.userDetails(request.getRemoteUser()));
			insurancedoc.setFileName(fileName);
			insurancedoc.setGcsPath(insurancedoc.getTypeAssociation() + "/" + insurancedoc.getTypeID() + "/"
					+ "documents/" + insurancedoc.getFileName());

			if (gcs.uploadDocument(organizationID, insurancedoc.getGcsPath(), insurancedoc.getFile())) {
				if (documentsDAO.uploadInsuranceDoc(insurancedoc))
					msg = "Document Successfully Uploaded";
				else
					msg = "Failed to Upload Document";
			} else {
				msg = "Failed to Upload Document";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			msg = "Failed to Upload Document";
		}
		logger.info(msg);
		session.setAttribute("addSVCDocMessage", msg);
		String typeAssc = insurancedoc.getTypeAssociation();
		typeAssc = typeAssc.replaceAll("Document", "");
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	@RequestMapping(value = "/viewSVCDoc")
	public ModelAndView viewSVCDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		SVCDoc svcdoc = documentsDAO.getSVCDoc(request.getParameter("typeID"), request.getParameter("fileName"));
		byte[] download = null;
		try {
			response.setContentType(svcdoc.getContentType());
			download = svcdoc.getFile();
			logger.info("Successfully retrieved SVC Document: " + request.getParameter("filename"));
		} catch (Exception e) {
			download = null;
			logger.error("Unable to retrieve SVC Document: " + request.getParameter("filename"));
		}
		if (null == download)
			return new ModelAndView("Documents/DocumentError");
		FileCopyUtils.copy(download, response.getOutputStream());
		return null;
	}

	@RequestMapping(value = "/viewPODoc")
	public ModelAndView viewPODocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		PODocument podoc = documentsDAO.getPODoc(request.getParameter("typeID"), request.getParameter("fileName"));
		byte[] download = null;
		try {
			response.setContentType(podoc.getContentType());
			download = podoc.getFile();
			logger.info("Successfully retrieved SVC Document: " + request.getParameter("filename"));
		} catch (Exception e) {
			download = null;
			logger.error("Unable to retrieve SVC Document: " + request.getParameter("filename"));
		}
		if (null == download)
			return new ModelAndView("Documents/DocumentError");
		FileCopyUtils.copy(download, response.getOutputStream());
		return null;
	}

	@RequestMapping(value = "/viewInsuranceDoc")
	public ModelAndView viewInsuranceDocument(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		ModelAndView model = new ModelAndView();
		if (null == request.getRemoteUser()) {
			model.addObject("Message", "You are not logged in. Please login to continue.");
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
			return model;
		}
		InsuranceDocument insurancedoc = documentsDAO.getInsuranceDoc(request.getParameter("typeID"),
				request.getParameter("fileName"));
		byte[] download = null;
		try {
			response.setContentType(insurancedoc.getContentType());
			download = insurancedoc.getFile();
			logger.info("Successfully retrieved SVC Document: " + request.getParameter("filename"));
		} catch (Exception e) {
			download = null;
			logger.error("Unable to retrieve SVC Document: " + request.getParameter("filename"));
		}
		if (null == download)
			return new ModelAndView("Documents/DocumentError");
		FileCopyUtils.copy(download, response.getOutputStream());
		return null;
	}

	/**
	 * 
	 * Saves the SwarmVendor Details. Restricted access to certain users.
	 * 
	 * @param vendor
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/saveSupplierDetails", method = RequestMethod.POST)
	public ModelAndView saveSupplier(@ModelAttribute Suppliers supplier, HttpServletRequest request,HttpSession session) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			String editStatus = request.getParameter("status");
			System.out.println("Edit Status:   "+editStatus);
			logger.info("Supplier Status: " + editStatus);
			String name = employeeDAO.userDetails(request.getRemoteUser());
			java.sql.Date date = dateTime.getCurrentSQLDate();
			//String organizationID = employeeDAO.orgIDByUsername(request.getRemoteUser());
			System.out.println("*************"+supplier);
			supplier.setCreatedBy(name);
			supplier.setModifiedBy(name);
			supplier.setModifiedDate(date);
			supplier.setSupplierName(supplier.getSupplierName().trim());
			String msg ="Supplier Created successsfully";
			String msg1 ="Supplier Updated successsfully";
			suppliersDAO.saveOrUpdate(supplier, editStatus);
			if (editStatus.equals("editSupplier")) {
				//System.out.println("editSupplier.123................."+msg1);
				session.setAttribute("SupplierMessage", msg1);
				session.setAttribute("SupplierMessagecount", "1");
				//System.out.println("editSupplier..................");
			}
			if (editStatus.equals("newsupplier")) {
				//System.out.println("newsupplier.123................."+msg);
				session.setAttribute("SupplierMessage", msg);
				session.setAttribute("SupplierMessagecount", "1");
				//System.out.println("newsupplier..................");
			}
			saveToSolrCore(supplier);
			
			return new ModelAndView("redirect:" + request.getHeader("Referer"));
		} catch (Exception e) {
			logger.error("Save Supplier Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	/**
	 * 
	 * Saves the SwarmVendor Details. Restricted access to certain users.
	 * 
	 * @param vendor
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/saveVendorDetails", method = RequestMethod.POST)
	public ModelAndView saveVendor(@ModelAttribute SwarmVendor vendor, HttpServletRequest request,
			HttpSession session) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			String editStatus = request.getParameter("status");
			logger.info("SwarmVendor Status: " + editStatus);
			String name = employeeDAO.userDetails(request.getRemoteUser());
			java.sql.Date date = dateTime.getCurrentSQLDate();
			String organizationID = employeeDAO.orgIDByUsername(request.getRemoteUser());
			vendor.setCreatedBy(name);
			vendor.setModifiedBy(name);
			vendor.setModifiedDate(date);
			vendor.setVendorName(vendor.getVendorName().trim());
			if (vendor.getNotes() == null) {
				vendor.setNotes("---");
			}
			if (vendor.getLinkedin() == null) {
				vendor.setLinkedin("--");
			}
			if (vendor.getWebsites() == null) {
				vendor.setWebsites("--");
			}
			String msg ="Vendor Created successsfully";
			String msg1 ="Vendor Updated successsfully";
			if (vendorDAO.saveOrUpdate(vendor, editStatus)) {
				if (editStatus.equals("editVendor")) {
			
					session.setAttribute("vendorMessage", msg1);
					session.setAttribute("VendorMessagecount", "1");
				}
				if (editStatus.equals("newVendor")) {
					
					session.setAttribute("vendorMessage", msg);
					session.setAttribute("VendorMessagecount", "1");
				}
				if (null != qbDAO.getAuthToken(organizationID).getRealmId()) {
					logger.info("QuickBooks Configured for: " + organizationID);
					DataService service = qbController.getService(session);
					if (editStatus.equals("editVendor"))
						qbDAO.updateCustomer(service, vendor);
					if (editStatus.equals("newVendor"))
						qbDAO.createCustomer(service, vendor);
				} else
					logger.warn("QuickBooks Not Configured for: " + organizationID);
				System.out.println("vendor ************"+vendor);
				vendor.setActive("Y");
				saveToSolrCore(vendor);
			}
			return new ModelAndView("redirect:" + request.getHeader("Referer"));
		} catch (Exception e) {
			logger.error("Save SwarmVendor Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	/**
	 *
	 * Saves the Client Details. Restricted access to certain users.
	 * 
	 * @param client
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/saveClientDetails", method = RequestMethod.POST)
	public ModelAndView saveClient(@ModelAttribute Client client, HttpServletRequest request, HttpSession session) {
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/login?session");

			String editStatus = request.getParameter("status");
			//String smessage = request.getParameter("smessage");
			logger.info("Client Status: " + editStatus);
			
			String name = employeeDAO.userDetails(request.getRemoteUser());
			java.sql.Date date = dateTime.getCurrentSQLDate();
			String organizationID = employeeDAO.orgIDByUsername(request.getRemoteUser());
			client.setCreatedBy(name);
			client.setModifiedBy(name);
			client.setModifiedDate(date);
			client.setClientName(client.getClientName().trim());
			
			System.out.println(client.getNotes() + "Client Notes");
			if (client.getNotes() == null) {
				client.setNotes("---");
			}
			if (client.getLinkedin() == null) {
				client.setLinkedin("---");
			}
			if (client.getWebsites() == null) {
				client.setWebsites("---");
			}
			String msg ="Client Created successsfully";
			String msg1 ="Client Updated successsfully";
			
			if (clientDAO.saveOrUpdate(client, editStatus)) {

				if (editStatus.equals("editClient")) {
					
					session.setAttribute("clientMessage", msg1);
					session.setAttribute("clientMessagecount", "1");
				}
				if (editStatus.equals("newClient")) {
					
					session.setAttribute("clientMessage", msg);
					session.setAttribute("clientMessagecount", "1");
				}
				
				if (null != qbDAO.getAuthToken(organizationID).getRealmId()) {

					logger.info("QuickBooks Configured for: " + organizationID);
					DataService service = qbController.getService(session);
					if (editStatus.equals("editClient"))
						qbDAO.updateCustomer(service, client);
					if (editStatus.equals("newClient"))
						qbDAO.createCustomer(service, client);
				} else
					logger.warn("QuickBooks Not Configured for: " + organizationID);
				client.setActive("Y");
				saveToSolrCore(client);
			}
			return new ModelAndView("redirect:" + request.getHeader("Referer"));
		} catch (Exception e) {
			logger.error("Save Client Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:" + request.getHeader("Referer"));
	}

	/**
	 * 
	 * Saves the Project Details. Restricted access to certain users.
	 * 
	 * @param project
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/saveProjectDetails", method = RequestMethod.POST)
	public ModelAndView saveProject(@ModelAttribute Project project, HttpServletRequest request, HttpSession session) {
		ModelAndView model = new ModelAndView();
		try {
			String editStatus = request.getParameter("status");
			try {
				String username = request.getRemoteUser();
				if (null == username)
					return new ModelAndView("redirect:/login?session");

				String organizationID = employeeDAO.orgIDByUsername(request.getRemoteUser());
				String name = employeeDAO.userDetails(username);
				Date date = dateTime.getCurrentSQLDate();
				try {
					if (project.getEndDate()!=null && project.getEndDate().toString().equals("1900-01-01"))
						project.setEndDate(null);
				} catch (Exception e) {
					logger.error("Save Project Exception occured due to :" + e.getCause());
				}

				if (editStatus.equals("newProject"))
					project.setProjectID(organizationID.toUpperCase() + "_PRJ_" + dateTime.getCurrentTimeStampForId());

				project.setCreatedBy(name);
				project.setCreatedDate(date);
				project.setModifiedBy(name);
				project.setModifiedDate(date);
				project.setProjectName(project.getProjectName().trim());		
				if(project!=null && project.getProjectName()!=null) {
					project.setProjectName(project.getProjectName().trim());
				}
				
				/*
				 * Quick book Access Check
				 */
				
				 String quickbookAccess= qbDAO.getAuthToken(organizationID).getRealmId();

				if (null !=quickbookAccess && editStatus.equals("newProject")) {

					String projectSwarmCheck = projectDAO.getProjectID(project.getProjectName(), organizationID);
					
					logger.info("QuickBooks Configured for: " + organizationID);
					DataService service = qbController.getService(session);
					List<Item> qbItems = qbController.getAllServiceItems(service, organizationID);
					boolean projectCheck = false;
					System.out.println("Project Name: "+project.getProjectName());
					for (Item projectItem : qbItems) {
						System.out.println("item Project Name: "+projectItem.getName());
						if (projectItem.getName().equalsIgnoreCase(project.getProjectName())) {
							projectCheck = true;
						break;
						}
					}
					if (projectCheck == true && projectSwarmCheck == null) {
						if (projectDAO.saveOrUpdate(project, editStatus))
						return new ModelAndView("redirect:/Projects");
					}
					if (projectCheck == true && projectSwarmCheck != null) {						
						return new ModelAndView("redirect:/Projects");
					}
					if(projectCheck == false && projectSwarmCheck == null) {
					if (projectDAO.saveOrUpdate(project, editStatus))
						qbDAO.createItem(service, project,organizationID);
										
						logger.info("Successfully Saved project: " + project.getProjectID() + " by user: "
								+ request.getRemoteUser());
					}else {
						logger.info("enterd to project edit");
					}
						
					
				} else {
					Project oldProject=null;
					if(editStatus.equals("editProject") && quickbookAccess!=null && project!=null) {
						oldProject=projectDAO.get(project.getProjectID());
					}
					String msg ="Project Created successsfully";
					//String msg1 ="Project Updated successsfully";
					if (projectDAO.saveOrUpdate(project, editStatus)) {
//						if (editStatus.equals("editProject")) {
//							session.setAttribute("projectMessage", msg1);
//							System.out.println("editClient..................");
//						}
						if (editStatus.equals("newProject")) {
							session.setAttribute("projectMessage", msg);
							session.setAttribute("projectMessagecount", "1");
							System.out.println("newProject..................");
						}
						logger.info("Successfully Saved project: " + project.getProjectID() + " by user: "
								+ request.getRemoteUser());
						if(quickbookAccess!=null) {
							DataService service = qbController.getService(session);
							qbDAO.updateItem(service, project, organizationID);
							if(oldProject!=null) {
							List<Item> allItems=qbDAOhelper.getInvoiceItems(service, organizationID);
							Item oldItem=null;
							for(Item item:allItems) {
								if(item.getName().equalsIgnoreCase(oldProject.getProjectName()+"_OT")) {
									oldItem=item;
									break;
								}
							}
							if(oldItem!=null) {
								project.setQuickBooksID(oldItem.getId());
								project.setProjectName(project.getProjectName()+"_OT");
								//update old item
								qbDAO.updateOTItem(service, project, organizationID);
							}else if(project.getOvertimeFlag()!=null && project.getOvertimeFlag().equals("1")) {
								project.setProjectName(project.getProjectName()+"_OT");
								//need to be create
								Item item = qbConverter.createQBServiceItem(project, service, organizationID);
								qbDAO.createOTItem(service,item);
							}
							}
						}
						
						
					}else
						logger.error("Failed to save project: " + project.getProjectID() + " by user: "
								+ request.getRemoteUser());
					logger.warn("QuickBooks Not Configured for: " + organizationID);
				}		
				

				/*if (projectDAO.saveOrUpdate(project, editStatus))
					logger.error("Successfully Saved project: " + project.getProjectID() + " by user: "
							+ request.getRemoteUser());
				else
					logger.error("Failed to save project: " + project.getProjectID() + " by user: "
							+ request.getRemoteUser());*/

			} catch (Exception e) {
				if (projectDAO.saveOrUpdate(project, editStatus))
					logger.error("Successfully Saved project: " + project.getProjectID() + " by user: "
							+ request.getRemoteUser());
				else
					logger.error("Failed to save project: " + project.getProjectID() + " by user: "
							+ request.getRemoteUser());
			}
            String msg="Project Updated successsfully";
            
			if (editStatus.equals("editProject"))
			{
				model.addObject("Message", msg);
			model.setViewName("ExpenseSubmitSuccessScreen"); 
			//return new ModelAndView("redirect:/Projects");
             return model;
		}
			return new ModelAndView("redirect:/Projects");
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return new ModelAndView("redirect:/Projects");
		}
	}

	/**
	 *
	 * Adds a new contact to the SwarmVendor or Client. Restricted access to certain
	 * users.
	 *
	 * @param contact
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/addNewContact", method = RequestMethod.POST)
	public ModelAndView saveContact(@ModelAttribute Contact contact, HttpServletRequest request, HttpSession session) {
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/403");

//			String redirectPage = request.getParameter("redirect");
			String name = employeeDAO.userDetails(request.getRemoteUser());
			String organizationID = (String) session.getAttribute("organizationID");
			//System.out.println(contact.getContactLastame() + "}}}}}}}}}}}}}}}}}}}}}}}}}}");
			contact.setOrganizationID(organizationID);
			contact.setCreatedBy(name);
			contact.setModifiedBy(name);
			contact.setModifiedDate(dateTime.getCurrentSQLDate());
			if (contact.getSkype() == null) {
				contact.setSkype("---");
			}
			
			logger.info("contact id isss"+contact.getId());
			
			Contact oldcontact=null;
			if(contact!=null && contact.getId()>0) {
				oldcontact=contactDAO.getContactById(contact.getId());
			
				if(oldcontact!=null) {
					System.out.println("contact isddddd"+oldcontact.getContactID());
					contact.setContactID(oldcontact.getContactID());
				}
			}
			
			
			contactDAO.save(contact);
			/*
			 * if (redirectPage.equals("client")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=client&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("vendor")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=vendor&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("supplier")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=supplier&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("project")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=project&typeID="+contact.
			 * getTypeID()+"");
			 */
			
			logger.info("this is for contact"+contact.getContactName());
			
			if(contact!=null && contact.getContactType() !=null && contact.getContactType().equalsIgnoreCase("External Manager")) {
			String contactID = contactDAO.getContactID(contact.getContactName(), organizationID, contact.getTypeID(), contact.getType(),contact.getContactType(),contact.getMobile());
			logger.info("contactid"+contactID);
			contact.setContactID(contactID);
			String projectname=projectDAO.getProjectName(contact.getTypeID(), organizationID);
			contact.setProjectName(projectname);
			logger.info("remoteuser"+request.getRemoteUser());
			
			Organization organization=organizationDAO.get(contact.getOrganizationID());
			
			if(organization!=null && organization.getOrganizationName()!=null)
			contact.setOrganizationName(organization.getOrganizationName());
			
			
			employeeDAO.notifyExternalManager(contact,name,employeeDAO.getEmail(request.getRemoteUser()));
			}else if(contact!=null && oldcontact !=null && contact.getContactType() !=null && !contact.getContactType().equalsIgnoreCase("External manager") && oldcontact.getContactType()!=null && oldcontact.getContactType().equalsIgnoreCase("External Manager")) {
				Organization organization=organizationDAO.get(contact.getOrganizationID());
				
				if(organization!=null && organization.getOrganizationName()!=null)
				contact.setOrganizationID(organization.getOrganizationName());
				
				employeeDAO.notifyExternalManagerTypeChange(contact,name,employeeDAO.getEmail(request.getRemoteUser()));
				
			}
			return new ModelAndView("redirect:" + request.getHeader("Referer"));

		} catch (Exception e) {
			logger.error("Add New Contact Exception occured due to :" + e.getCause());
		}
		return new ModelAndView("redirect:/Resources");
	}
	
	
	
	
	
	//sp-974
	
	@RequestMapping(value = "/accManager/addNewProjectContact", method = RequestMethod.POST)
	public ModelAndView saveProjectContact(@ModelAttribute Contact contact, HttpServletRequest request, HttpSession session) {
		
		ModelAndView model = new ModelAndView();
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/403");

			
			String name = employeeDAO.userDetails(request.getRemoteUser());
			String organizationID = (String) session.getAttribute("organizationID");
			System.out.println(contact.getContactLastame() + "}}}}}}}}}}}}}}}}}}}}}}}}}}");
			String[] splitValue = contact.getContactIDArray();
		    String projectId=contact.getTypeID();
		    String projectName=contact.getProjectName();
		    String clientId=contact.getClientID();
		    String clientName=contact.getClientName();
		    String vendorId=contact.getVendorID();
		    String vendorName=contact.getVendorName();
		    String type=contact.getType();
		    String typeId;
		    
		   if(type.equalsIgnoreCase("ClientContact")) {
			   typeId=clientId;
		   }
		   else if(type.equalsIgnoreCase("VendorContact")) {
		   typeId=vendorId;
		   }
		   
		   else {
			   typeId=projectId;
		    }
		   
		    for(String conNote:splitValue)
		    { 
		    	if(!(conNote.equalsIgnoreCase(null)||conNote.isEmpty())) {
		    	String[] parts = conNote.split(",", 2);
		    	String conID = parts[0];
		    	String notes = parts[1];
		    List<Contact> listContacts = contactDAO.listofSelectedContact(conID);
		    contact.setContactID("null");
		    contact.setTypeID(typeId);
			contact.setContactType(listContacts.get(0).getContactType());
			contact.setContactName(listContacts.get(0).getContactName());
			contact.setMobile(listContacts.get(0).getMobile());
			contact.setWorkPhone(listContacts.get(0).getWorkPhone());
			contact.setExt(listContacts.get(0).getExt());
			contact.setFax(listContacts.get(0).getFax());
			contact.setEmail(listContacts.get(0).getEmail());
			contact.setLinkedIn(listContacts.get(0).getLinkedIn());
			contact.setSkype(listContacts.get(0).getSkype());
			contact.setContactLastame(listContacts.get(0).getContactLastame());
			contact.setSuppContUserName(listContacts.get(0).getSuppContUserName());
			contact.setProjectID(projectId);
			contact.setProjectName(projectName);
			contact.setClientID(clientId);
			contact.setClientName(clientName);
			contact.setVendorID(vendorId);
			contact.setVendorName(vendorName);
			 if(type.equalsIgnoreCase("SupplierContact")) {
				  contact.setTypeID(listContacts.get(0).getTypeID());
				 contact.setSupplierID(listContacts.get(0).getTypeID());
				   }
			
			contact.setOrganizationID(organizationID);
			contact.setCreatedBy(name);
			contact.setModifiedBy(name);
			contact.setNotes(notes);
			contact.setModifiedDate(dateTime.getCurrentSQLDate());
			if (contact.getSkype() == null) {
				contact.setSkype("---");
			}
			contactDAO.saveAssociateContact(contact);
			
		    }	
		    }
			
			return new ModelAndView("redirect:" + request.getHeader("Referer"));
		   

		} catch (Exception e) {
			logger.error("Add New Contact Exception occured due to :" + e.getCause());
			//model.addObject("Message", " please select contacts properly ");
		}
		
		
		  model.addObject("reloadFlag", "reloadPage");
		 model.setViewName("SuccessScreen"); 
		 return model;
		 
		}
	
	
	@RequestMapping(value = "/accManager/addEmployeeContact", method = RequestMethod.POST)
	public ModelAndView saveEmployeeContact(@ModelAttribute EmployeeDirectoryObject employee,@ModelAttribute Contact contact, HttpServletRequest request, HttpSession session) {
		
		ModelAndView model = new ModelAndView();
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/403");

			
			String name = employeeDAO.userDetails(request.getRemoteUser());
			String organizationID = (String) session.getAttribute("organizationID");
		   String[] splitValue = employee.getEmpArray();
		    for(String conNote:splitValue)
		    { 
		  
		    	if(!(conNote.equalsIgnoreCase(null)||conNote.isEmpty())) {
		    	String[] parts = conNote.split(",", 2);
		    	String conID = parts[0];
		    	String notes = parts[1];
		    List<EmployeeDirectoryObject> listContacts = contactDAO.detailsofSelectedEmployeeContact(conID);
		    contact.setContactID("null");
		    contact.setTypeID(conID);
		    contact.setType(employee.getType());
			contact.setContactName(listContacts.get(0).getFirstname());
			contact.setMobile(listContacts.get(0).getMobile());
			contact.setEmail(listContacts.get(0).getEmail());
			contact.setContactLastame(listContacts.get(0).getLastname());
			contact.setSuppContUserName(listContacts.get(0).getUsername());
			contact.setProjectID(employee.getProjectID());
			contact.setProjectName(employee.getProjectName());
			contact.setOrganizationID(organizationID);
			contact.setCreatedBy(name);
			contact.setModifiedBy(name);
			contact.setNotes(notes);
			contact.setModifiedDate(dateTime.getCurrentSQLDate());
			contact.setEmployeeID(employee.getUsername());
			contactDAO.saveAssociateContact(contact);
		    }	
		    }
			
			return new ModelAndView("redirect:" + request.getHeader("Referer"));
		   

		} catch (Exception e) {
			logger.error("Add New Contact Exception occured due to :" + e.getCause());
			model.addObject("Message", " please select contacts properly ");
		}
		//return new ModelAndView("redirect:/Resources");
		model.addObject("reloadFlag", "reloadPage");
		model.setViewName("SuccessScreen");
		return model;
	}

	
	@RequestMapping(value = "/deleteAssociateContact", method = RequestMethod.GET)
	public ModelAndView deleteAssociateContact(HttpServletRequest request) {

		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		String ContactId = request.getParameter("contactID");

		contactDAO.deleteAssociatedContact(ContactId);
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}

//end of sp-974
	

	/**
	 *
	 * Updates the contact information of a SwarmVendor or Client contact.
	 * Restricted access to certain users.
	 *
	 * @param contact
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/updateContact", method = RequestMethod.POST)
	public ModelAndView updateContact(@ModelAttribute Contact contact, HttpServletRequest request,
			HttpSession session) {
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/403");

//			String redirectPage = request.getParameter("redirect");
			String name = employeeDAO.userDetails(request.getRemoteUser());
			String organizationID = (String) session.getAttribute("organizationID");
			contact.setOrganizationID(organizationID);
			contact.setCreatedBy(name);
			contact.setModifiedBy(name);
			contact.setModifiedDate(dateTime.getCurrentSQLDate());
			contactDAO.update(contact);
			/*
			 * if (redirectPage.equals("client")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=client&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("vendor")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=vendor&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("supplier")) return new
			 * ModelAndView("redirect:/viewSVCContacts?type=supplier&typeID="+contact.
			 * getTypeID()+""); if (redirectPage.equals("project")) return new
			 * ModelAndView("redirect:/projectsListByPage?page=1");
			 */
			return new ModelAndView("redirect:" + request.getHeader("Referer"));

		} catch (Exception e) {
			logger.error("Add New Contact Exception occured due to :" + e.getCause());
		}
		return new ModelAndView("redirect:/Resources");
	}

	/**
	 * 
	 * Deletes a Supplier from the database. Restricted access to certain users.
	 * 
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/deleteSupplier", method = RequestMethod.GET)
	public ModelAndView deleteSupplier(HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser())
				return new ModelAndView("redirect:/403");

			String supplierID = request.getParameter("supplierID");
			String username = request.getParameter("auth");
			logger.info("supplierID  : " + supplierID + " deleted by username: " + username);
			if (username.equals(request.getRemoteUser()))
				suppliersDAO.delete(supplierID);
			return new ModelAndView("redirect:/Suppliers");
		} catch (Exception e) {
			logger.error("Delete Supplier Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:/Suppliers");
	}

	/**
	 * 
	 * Deletes a SwarmVendor from the database. Restricted access to certain users.
	 * 
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/deleteVendor", method = RequestMethod.GET)
	public ModelAndView deleteVendor(HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			String vendorID = request.getParameter("vendorID");
			String username = request.getParameter("auth");
			logger.info("vendorID  : " + vendorID + " username: " + username);
			if (username.equals(request.getRemoteUser()))
				vendorDAO.delete(vendorID);
			return new ModelAndView("redirect:/Vendors");
		} catch (Exception e) {
			logger.error("Delete SwarmVendor Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:/Vendors");
	}

	/**
	 * 
	 * Deletes a Client from the database. Restricted access to certain users.
	 * 
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/deleteClient", method = RequestMethod.GET)
	public ModelAndView deleteClient(HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			String clientID = request.getParameter("clientID");
			String username = request.getParameter("auth");
			logger.info("clientID  : " + clientID + " username: " + username);
			if (username.equals(request.getRemoteUser()))
				clientDAO.delete(clientID);
			return new ModelAndView("redirect:/Clients");
		} catch (Exception e) {
			logger.error("Delete Client Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:/Clients");
	}

	/**
	 * 
	 * Deletes a Project from the database. Restricted access to certain users.
	 * 
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/accManager/deleteProject", method = RequestMethod.GET)
	public ModelAndView deleteProject(HttpServletRequest request, HttpSession session) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/403");

		String projectID = request.getParameter("projectID");
		String username = request.getParameter("auth");
		if (username.equals(request.getRemoteUser())) {
			if (projectDAO.checkProjectAssociation(projectID)) {
				if (projectDAO.delete(projectID)) {
					session.setAttribute("deleteProjectMessage", "Project Deleted Successfully.");
				} else {
					session.setAttribute("deleteProjectMessage", "Failed to delete Project.");
				}
			} else {
				session.setAttribute("deleteProjectMessage",
						"Project is currently associated to employee. Cannot Delete Project.");
			}
		} else {
			return new ModelAndView("redirect:/403");
		}
		return new ModelAndView("redirect:/projectsListByPage?page=" + request.getParameter("redirectPage"));
	}

	@RequestMapping(value = "/projectCategory")
	public ModelAndView projectCategory(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Projects Categories Accessed By User: " + username);
		model = userController.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<Project> listProject = projectDAO.listOfAllProjects(organizationID);
		List<ProjectCategories> listProjectCategories = projectTaskDAO.listProjectCategoriesByOrgID(organizationID);

		model.addObject("projectList", listProject);
		model.addObject("listProjectCategories", listProjectCategories);
		model.addObject("projectCategory", new ProjectCategories());
		model.setViewName("AccManager/ProjectCategories");
		return model;
	}

	@RequestMapping(value = "/accManager/saveProjectCategory", method = RequestMethod.POST)
	public ModelAndView saveProjectCategory(@ModelAttribute ProjectCategories projectCategory,
			HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}

			String name = employeeDAO.userDetails(request.getRemoteUser());
			java.sql.Date date = dateTime.getCurrentSQLDate();

			projectCategory.setCreatedBy(name);
			projectCategory.setModifiedBy(name);
			projectCategory.setModifiedDate(date);

			projectTaskDAO.saveProjectCategory(projectCategory);

			return new ModelAndView("redirect:/projectCategory");
		} catch (Exception e) {
			logger.error("Save Project Task Exception occured due to :" + e.getCause());
		}
		return new ModelAndView("redirect:/projectCategory");
	}

	@RequestMapping(value = "/accManager/deleteProjectCategory", method = RequestMethod.GET)
	public ModelAndView deleteProjectCategory(HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			String id = request.getParameter("id");
			String username = request.getParameter("auth");
			logger.info("id  : " + id + " username: " + username);
			if (username.equals(request.getRemoteUser()))
				projectTaskDAO.deleteCategory(Integer.parseInt(id));
			return new ModelAndView("redirect:/projectCategory");
		} catch (Exception e) {
			logger.error("Delete projectCategory Exception occured due to : " + e.getCause());
		}
		return new ModelAndView("redirect:/projectCategory");
	}

	@RequestMapping(value = "/deleteprojectcontact", method = RequestMethod.GET)
	public ModelAndView deleteProjectContact(HttpServletRequest request) {

		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		String projectContactid = request.getParameter("projectcontact");
		if(projectContactid!=null) {
			Contact contact=contactDAO.getContactByContactId(projectContactid);
			boolean deleteStatus=contactDAO.deleteAssociatedContact(projectContactid);
			if(deleteStatus) {
				if(contact!=null && contact.getContactType().equalsIgnoreCase("External Manager")) {
					Employee employee=employeeDAO.getbyUsername(username);
					if(employee!=null) {
						employeeDAO.notifyExternalManagerContactDelete(contact,employee.getFirstname()+", "+employee.getLastname() , employee.getEmail());
					}
				}
			}		
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}
	
	
	@RequestMapping(value = "/accManager/saveSupplierLoginDetails", method = RequestMethod.POST)
	public ModelAndView saveSupplierLoginDetails(@ModelAttribute SupplierLogin newSupplierLogin,
			HttpServletRequest request) {
		try {
			if (null == request.getRemoteUser()) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}

			String name = employeeDAO.userDetails(request.getRemoteUser());
			Employee emp = employeeDAO.profile(request.getRemoteUser());
			java.sql.Date date = dateTime.getCurrentSQLDate();
			newSupplierLogin.setCreatedDate(date);
			newSupplierLogin.setModifiedBy(name);
			newSupplierLogin.setCreatedDate(date);
			contactDAO.createOrUpdateContactLogin(newSupplierLogin, emp.getOrganizationID());

			return new ModelAndView(
					"redirect:/viewSVCContacts?type=supplier&typeID=" + newSupplierLogin.getSupplierid() + "");
		} catch (Exception e) {
			logger.error("Save Project Task Exception occured due to :" + e.getCause());
		}
		return new ModelAndView(
				"redirect:/viewSVCContacts?type=supplier&typeID=" + newSupplierLogin.getSupplierid() + "");
	}

	@RequestMapping(value = "/deleteClientContact", method = RequestMethod.GET)
	public ModelAndView deleteClientContact(HttpServletRequest request) {

		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		String clientContactId = request.getParameter("clientcontact");

		clientDAO.deleteClientContact(clientContactId);
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}

	@RequestMapping(value = "/deleteVendorContact", method = RequestMethod.GET)
	public ModelAndView deleteVendorContact(HttpServletRequest request) {

		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		String vendorContactId = request.getParameter("vendorcontact");

		clientDAO.deleteClientContact(vendorContactId);
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}

	@RequestMapping(value = "/deleteSupplierContact", method = RequestMethod.GET)
	public ModelAndView deleteSupplierContact(HttpServletRequest request) {

		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");
		String supplierContactId = request.getParameter("suppliercontact");
		clientDAO.deleteClientContact(supplierContactId);
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}
	
	@RequestMapping(value = { "/vendor-data-copy" })
	@ResponseBody
	public String generateVendorDataForCopy( HttpServletRequest request, HttpSession session){
		
		
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);

		if (null == session.getAttribute("deptLogo")) {
			userController.createSessionObject(session, username);
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		List<SwarmVendor> vendorList = vendorDAO.list(organizationID);
		
		if(CollectionUtils.isEmpty(vendorList)) {
			return StringUtils.EMPTY;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Vendor Name").append(TAB).append("Address").append(TAB).append("Phone 1").append(TAB).append("Phone 2");
		
		for(SwarmVendor vendor: vendorList) {
			sb.append(System.getProperty("line.separator"));
			sb.append(vendor.getVendorName()).append(TAB);
			
			sb.append(vendor.getAddress()).append(SPACE).append(vendor.getCity()).append(SPACE);
			sb.append(vendor.getState()).append(SPACE).append(vendor.getZipcode()).append(SPACE).append(vendor.getCountry()).append(TAB);
			
			sb.append(vendor.getPhone1()).append(TAB);
			sb.append(vendor.getPhone2());
		}
		return sb.toString();
	}
	
	
	@RequestMapping(value = { "/vendor-data-csv-download" })
	@ResponseBody
	public void generateVendorDataForCSVDownload( HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException{
		
		
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);

		if (null == session.getAttribute("deptLogo")) {
			userController.createSessionObject(session, username);
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		List<SwarmVendor> vendorList = vendorDAO.list(organizationID);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(DOUBLE_QOUTE).append("Vendor Name").append(DOUBLE_QOUTE).append(COMMA);
		sb.append(DOUBLE_QOUTE).append("Address").append(DOUBLE_QOUTE).append(COMMA);
		sb.append(DOUBLE_QOUTE).append("Phone 1").append(DOUBLE_QOUTE).append(COMMA);
		sb.append(DOUBLE_QOUTE).append("Phone 2").append(DOUBLE_QOUTE).append(COMMA);
		
		for(SwarmVendor vendor: vendorList) {
			sb.append(System.getProperty("line.separator"));
			sb.append(DOUBLE_QOUTE).append(vendor.getVendorName()).append(DOUBLE_QOUTE).append(COMMA);
			
			sb.append(DOUBLE_QOUTE).append(vendor.getAddress()).append(SPACE).append(vendor.getCity()).append(SPACE);
			sb.append(vendor.getState()).append(SPACE).append(vendor.getZipcode()).append(SPACE).append(vendor.getCountry()).append(DOUBLE_QOUTE).append(COMMA);
			
			sb.append(DOUBLE_QOUTE).append(vendor.getPhone1()).append(DOUBLE_QOUTE).append(COMMA);
			sb.append(DOUBLE_QOUTE).append(vendor.getPhone2()).append(DOUBLE_QOUTE);
		}
		
		response.setContentType("application/csv");
		response.setHeader("Content-Disposition", "filename=vendors.csv");
		
		PrintWriter writer = response.getWriter();
		writer.write(sb.toString());
		writer.close();
		
		return;
	}
	
	
	@RequestMapping(value = { "/vendor-data-excel-download" })
	@ResponseBody
	public void generateVendorDataForExcelDownload( HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException{
		
		
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);

		if (null == session.getAttribute("deptLogo")) {
			userController.createSessionObject(session, username);
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		
		List<SwarmVendor> vendorList = vendorDAO.list(organizationID);
		
		
		 XSSFWorkbook workbook = new XSSFWorkbook();
		 
		 XSSFSheet sheet = workbook.createSheet("Vendor Data");
		 
		 int rowCount = 0;
		 
		 XSSFRow row = sheet.createRow(rowCount++);
		 
		 int cellCounter = 0;
		 XSSFCell cell =  row.createCell(cellCounter++);
		 cell.setCellValue("Vendor Name");
		 
		 cell =  row.createCell(cellCounter++);
		 cell.setCellValue("Address");
		 
		 cell =  row.createCell(cellCounter++);
		 cell.setCellValue("Phone 1");
		 
		 cell =  row.createCell(cellCounter++);
		 cell.setCellValue("Phone 2");
		
		
		
		for(SwarmVendor vendor: vendorList) {
			
			cellCounter = 0;
			
			 row = sheet.createRow(rowCount++);

			 cell =  row.createCell(cellCounter++);
			 cell.setCellValue(vendor.getVendorName());
			 
			 cell =  row.createCell(cellCounter++);
			 StringBuilder sb = new StringBuilder();
			 sb.append(vendor.getAddress()).append(SPACE).append(vendor.getCity()).append(SPACE);
			 sb.append(vendor.getState()).append(SPACE).append(vendor.getZipcode()).append(SPACE).append(vendor.getCountry());
			 cell.setCellValue(sb.toString());
			 
			 cell =  row.createCell(cellCounter++);
			 cell.setCellValue(vendor.getPhone1());
			 
			 cell =  row.createCell(cellCounter++);
			 cell.setCellValue(vendor.getPhone2());
		}
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "filename=vendors.xlsx");
		
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		outputStream.close();
		
		return;
	}
	
	@RequestMapping(value = "/vendor-list-print")
	public ModelAndView vendorFullListPrint(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		logger.info("Vendors Module Accessed By User: " + username);


		if (null == session.getAttribute("deptLogo"))
			userController.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		try {

			List<SwarmVendor> list = vendorDAO.list(organizationID);
			
			model.addObject("vendorList",list);
		} catch (Exception e) {
			logger.error("Exception while getting vendor list", e);
		}
		
		model.setViewName("AccManager/print/VendorsFullList");
		return model;
	}
	
	
private void saveToSolrCore(SwarmVendor vendor)throws Exception {
		
		logger.debug(" SaveToSlrCore !!! ");
		SolrClient solrClient  = null;
		
		// String urlString = "http://34.68.3.236:8983/solr/vendor";
		// HttpSolrClient solr = new HttpSolrClient(urlString).build();
		// solr.setParser(new XMLResponseParser());
		try {
			solrClient  = swarmHRSolrClient.getSolrClient(SolrConfigDetails.VENDOR_SOLR_CLIENT_KEY);
			
			if(solrClient != null) {
				SolrInputDocument doc = null;
				
				doc = new SolrInputDocument();
				doc.setField("id", vendor.getVendorID());
				doc.setField("emailId", vendor.getVendorID());
				doc.setField("active",vendor.getActive());
				
				doc.setField("address", StringUtils.isEmpty(vendor.getAddress()) ? "" : vendor.getAddress());
				doc.setField("bookmark", false);
				doc.setField("city", StringUtils.isEmpty(vendor.getCity()) ? "" : vendor.getCity());
				doc.setField("country", StringUtils.isEmpty(vendor.getCountry()) ? "NA" : vendor.getCountry());
				doc.setField("createdBy", StringUtils.isEmpty(vendor.getCreatedBy()) ? "NA" : vendor.getCreatedBy());
				doc.setField("createdDate", vendor.getCreatedDate());
				doc.setField("linkedin", vendor.getLinkedin());
				doc.setField("notes", vendor.getNotes());
				doc.setField("orgId", vendor.getOrganizationID());
				doc.setField("phone1", vendor.getPhone1());
				doc.setField("phone2", vendor.getPhone2());
				doc.setField("state", vendor.getState());
				doc.setField("vendorId", vendor.getVendorID());
				doc.setField("vendorName", vendor.getVendorName());
				doc.setField("websites", vendor.getWebsites());
				doc.setField("zipcode", vendor.getZipcode());	
				doc.setField("status", true);
				doc.setField("ownerName", vendor.getOwnerName());	
				
				UpdateResponse ur = solrClient.add(doc);
				
				NamedList<Object> nl = ur.getResponse();

				System.out.println(" List Value = >" +nl.toString());
				solrClient.commit();
			}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(solrClient != null) {
				solrClient.close();
			}
		}
		
	}
	

private void saveToSolrCore(Suppliers supplier)throws Exception {
	logger.debug(" SaveToSlrCore !!! ");
	SolrClient solrClient  = null;
	try {
		
		solrClient  = swarmHRSolrClient.getSolrClient(SolrConfigDetails.SUPPLIER_SOLR_CLIENT_KEY);
		
		if(solrClient != null) {
			SolrInputDocument doc = null;
			
			
				doc = new SolrInputDocument();
				doc.setField("id", supplier.getSupplierID());
			
			doc.setField("active",supplier.getActive());
			doc.setField("supplierID", StringUtils.isEmpty(supplier.getSupplierID()) ? "" : supplier.getSupplierID());
			doc.setField("supplierName", supplier.getSupplierName() != null ? supplier.getSupplierName() : "");
			doc.setField("address", StringUtils.isEmpty(supplier.getAddress()) ? "" : supplier.getAddress());
			doc.setField("bookmark", false);
			doc.setField("city", StringUtils.isEmpty(supplier.getCity()) ? "" : supplier.getCity());
			doc.setField("country", StringUtils.isEmpty(supplier.getCountry()) ? "NA" : supplier.getCountry());
			doc.setField("createdBy", StringUtils.isEmpty(supplier.getCreatedBy()) ? "NA" : supplier.getCreatedBy());
			doc.setField("createdDate", supplier.getCreatedDate());
			doc.setField("emailId", supplier.getSupplierID());
			doc.setField("linkedin", supplier.getLinkedin());
			doc.setField("status", supplier.getStatus());
			doc.setField("notes", supplier.getNotes());
			doc.setField("orgId", supplier.getOrganizationID());
			doc.setField("phone1", supplier.getPhone1());
			doc.setField("phone2", supplier.getPhone2());
			doc.setField("state", supplier.getState());
			doc.setField("websites", supplier.getWebsites());
			doc.setField("zipcode", supplier.getZipcode());	
			//doc.setField("sharing", supplier.getSharing());	
			doc.setField("ownerName", supplier.getOwnerName());	
//			doc.setField("employees", "[]");
//			doc.setField("projects", "[]");
//			doc.setField("svcOwnerships", "[]");
			
			UpdateResponse ur = solrClient.add(doc);
			
			NamedList<Object> nl = ur.getResponse();

			System.out.println(" List Value = >" +nl.toString());
			solrClient.commit();
		} // End of If

	}catch(Exception e) {
		e.printStackTrace();
	}finally {
		if(solrClient != null) {
			solrClient.close();
		}
	}
	
}

private void saveToSolrCore(Client client)throws Exception {
	
	logger.debug(" SaveToSlrCore !!! ");
	SolrClient solrClient  = null;
	try {
		
		solrClient  = swarmHRSolrClient.getSolrClient(SolrConfigDetails.CLIENT_SOLR_CLIENT_KEY);
		
		if(solrClient != null) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("id", client.getClientID());
			doc.setField("active",client.getActive());
			doc.setField("address", StringUtils.isEmpty(client.getAddress()) ? "" : client.getAddress());
			doc.setField("bookmark", false);
			doc.setField("city", StringUtils.isEmpty(client.getCity()) ? "" : client.getCity());
			doc.setField("clientId", StringUtils.isEmpty(client.getClientID()) ? "" : client.getClientID()); //StringUtils.isEmpty(vendor.getClientId()) ? "" : v.getClientId()
			//doc.setField("clientName", StringUtils.isEmpty(client.getClientname()) ? "" : client.getClientname()); //StringUtils.isEmpty(vendor.getClientName()) ? "" : v.getClientName()
			doc.setField("country", StringUtils.isEmpty(client.getCountry()) ? "NA" : client.getCountry());
			doc.setField("createdBy", StringUtils.isEmpty(client.getCreatedBy()) ? "NA" : client.getCreatedBy());
			doc.setField("createdDate", client.getCreatedDate());
			doc.setField("emailId", client.getClientID());
			doc.setField("linkedin", client.getLinkedin());
			doc.setField("notes", client.getNotes());
			doc.setField("orgId", client.getOrganizationID());
			doc.setField("phone1", client.getPhone1());
			doc.setField("phone2", client.getPhone2());
			doc.setField("state", client.getState());
			doc.setField("vendorId", client.getVendorID());
			doc.setField("vendorName", client.getVendorName());
			doc.setField("websites", client.getWebsites());
			doc.setField("zipcode", client.getZipcode());	
			//doc.setField("status", client.getStatus());
			doc.setField("ownerName", client.getOwnerName());	
			UpdateResponse ur = solrClient.add(doc);
			
			NamedList<Object> nl = ur.getResponse();

			System.out.println(" List Value = >" +nl.toString());
			solrClient.commit();
		} // End of If

	}catch(Exception e) {
		e.printStackTrace();
	}finally {
		if(solrClient != null) {
			solrClient.close();
		}
	}
	
}
	

@RequestMapping(value = "/empCommission")
public ModelAndView empCommission(ModelAndView model, HttpServletRequest request, HttpSession session)
	throws IOException {
	String username = request.getRemoteUser();
	if (null == username)
		return new ModelAndView("redirect:/sessionExpiredWithinModal");
	//String organizationID = request.getParameter("organizationID");
	Employee profile = (Employee) session.getAttribute("profile");
	String organizationID = session.getAttribute("organizationID").toString();
	
	Map<String, String> commissionType = new LinkedHashMap<String, String>();
	commissionType.put("Fixed", "Fixed");
	commissionType.put("Time Based", "Time Based");
	
	Map<String, String> currency = new LinkedHashMap<String, String>();
	currency.put("USD", "USD($)");
	currency.put("CAD", "CAD($)");
	currency.put("INR", "INR(₹)");
	
	model.addObject("currency", currency);
	
	
	Map<String, String> commissionPayTypes = new LinkedHashMap<String, String>();
	commissionPayTypes.put("Monthly", "Monthly");
	commissionPayTypes.put("BiMonthly", "Bi-Monthly");
	model.addObject("commissionPayType", commissionPayTypes);
	
	Map<String, String> commissionPayTerms = new LinkedHashMap<String, String>();
	commissionPayTerms.put("5", "5 Days");
	commissionPayTerms.put("10", "10 Days");
	commissionPayTerms.put("15", "15 Days");
	commissionPayTerms.put("30", "30 Days");
	commissionPayTerms.put("45", "45 Days");
	commissionPayTerms.put("60", "60 Days");
	
	model.addObject("commissionPayTerms", commissionPayTerms);
	
	
	if(session.getAttribute("commissionMessagecount") ==null || session.getAttribute("commissionMessagecount").equals("0")) {
		session.setAttribute("commissionMessage", null);
	}
	session.setAttribute("commissionMessagecount", "0");
		
	ProjectResource projectDetails = projectDAO.getProjectResourceListDetails(request.getParameter("project"), organizationID, request.getParameter("username"));
	projectDetails.setOrganizationID(organizationID);
	
	//List<Commission> listCommissions=commissionDao.getCommissions(organizationID);
	List<Commission> listCommissions=commissionDao.getCommissionsByUserAndProject(organizationID, request.getParameter("username"),request.getParameter("project"));
	
	for(Commission comm:listCommissions) {
		comm.setBillRate(projectDetails.getBillRate());
		comm.setPayRate(projectDetails.getPayRate());
		
	}
	
	if(request.getParameter("action")!=null && request.getParameter("action").equalsIgnoreCase("Edit")) {
		if(request.getParameter("id")!=null) {
			Integer id=Integer.parseInt(request.getParameter("id"));
			Commission commiss=listCommissions.stream().filter(comm->{
				if(comm.getId()==id)
					return true;
				return false;								
				}).findAny().orElse(null);
			
			logger.info(id+"commission data"+commiss.toString());
			model.addObject("commissionEditObject",commiss);
			model.addObject("action","Edit");
		}
	}
	Map<String, String> paymentChoiceTypeOptions = new LinkedHashMap<String, String>();
	paymentChoiceTypeOptions.put("One Time", "One Time");
	paymentChoiceTypeOptions.put("Multiple Times", "Multiple Times");
	model.addObject("paymentChoiceTypeOptions", paymentChoiceTypeOptions);
	
	Map<String, String> months = new LinkedHashMap<String, String>();
	for(int month=1;month<=12;month++) {
		months.put(""+month,""+month);
	}
	
	model.addObject("monthsmap",months);
	
	Map<String, String> projectName = projectDAO.listProjectNames(organizationID);
	//System.out.println("projectName---------->"+projectName.toString());
	model.addObject("projectName",projectName);
	model.addObject("commissionsList",listCommissions);
	
	model.addObject("projectDetails", projectDetails);
	//model.addObject("organizationID", organizationID);
	model.addObject("commissionType", commissionType);
	//model.addObject("projectMap", uniqueProjects);
	model.addObject("profile", profile);
	//${projectName[commission.projectID]}
	Map<String,String> employeeList = employeeDAO.getEmployeeFirstNameMap(organizationID,"Active");
	model.addObject("employeeList",employeeList );
	model.addObject("commission", new Commission());
	model.setViewName("AccManager/CommissionForm");
	return model;
}


	@RequestMapping(value = "/assignTask", method = RequestMethod.POST)
	public ModelAndView SendTask(@ModelAttribute Task task, HttpServletRequest request, HttpSession session)
			throws FileNotFoundException, IOException {
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
		
		Task dbTask = taskDAO.get(task.getId());
		
		TaskAssign taskAssign = new TaskAssign();
		taskAssign.setFullName(task.getFullName());
		taskAssign.setTaskID(task.getId());
		taskAssign.setRaisedBy(dbTask.getCreatedBy());
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
        String emailid=employeeDAO.getEmail(EmpUsername);
        String sub=" Hello " + taskAssign.getFullName()+"Task Assigned: "+task.getSubject();
        String userSub=" Hello " + dbTask.getName()+",Your task assigned to "+taskAssign.getFullName();
        String urlLink = "\""+Variables.site_url+"/viewAssignTaskDetails?trackingId="+dbTask.getTrackingID()+"&AssignedUserName="+taskAssign.getUsername()+"&name="+taskAssign.getFullName()+"&taskid="+dbTask.getId()+"&username="+dbTask.getUsername()+"\"";
        String phoneNo = null==dbTask.getPhoneNo()?"N/A":dbTask.getPhoneNo(); 
    	String message = "<!DOCTYPE html><html lang=\"en\">"
    			+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
    			+ "<meta name=\"viewport\" content=\"width=device-width\"><title>Assign Task Email</title></head><body>"
    			+ "<table><tr><td><p> Hello " + taskAssign.getFullName() + ",</p></td></tr>"
    			+ "<tr><td><p>A new task is assigned for you</p></td></tr>"
    			+ "<tr><td><p><table>"
    			+ "<tr><td> Raised By: &emsp;</td><td> " + dbTask.getName() + " &emsp;</td></tr>" 
    			+ "<tr><td> Raised on: &emsp;</td><td> " + dbTask.getCreatedDate() + " &emsp;</td></tr>" 
    			+ "<tr><td> Tracking ID: &emsp;</td><td> " + dbTask.getTrackingID() + " &emsp;</td></tr>" 
    			+ "<tr><td> Phone No: &emsp;</td><td> " + phoneNo + " &emsp;</td></tr>" 
    			+ "<tr><td> EmailID: &emsp;</td><td> " + dbTask.getEmail() + " &emsp;</td></tr>" 
    			+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>" 
    			+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"  
    			+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr>"  
    			+ "</table></p></td></tr><tr><td><p>Please click here for view user track: <a href="+urlLink+" target=\"_blank\" >View History</a></p></td></tr>"
    			+ "</table>"
    			+"</body></html>";
    	String userUrlLink = "\""+Variables.site_url+"/viewDetails?trackingId="+dbTask.getTrackingID()+"&AssignedUserName="+taskAssign.getUsername()+"&username="+task.getUsername()+"\"";
     	String UserMsg = "<!DOCTYPE html><html lang=\"en\">"
     		   	+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
     		   	+ "<meta name=\"viewport\" content=\"width=device-width\"><title>Assign Task Email</title></head><body>"
     		   	+ "<table><tr><td><p> Hello " + dbTask.getName() + "</p></td></tr>"
     		   	+ "<tr><td><p>created task is assigned to "+taskAssign.getFullName()+"</p></td></tr>"
     		   	+ "<tr><td><p><table>"
     		   	+ "<tr><td> Raised By: &emsp;</td><td> " + dbTask.getName() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Raised on: &emsp;</td><td> " + dbTask.getCreatedDate() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Tracking ID: &emsp;</td><td> " + dbTask.getTrackingID() + " &emsp;</td></tr>" 
     		   	+ "<tr><td> Phone No: &emsp;</td><td> " + phoneNo + " &emsp;</td></tr>" 
     		   	+ "<tr><td> EmailID: &emsp;</td><td> " + dbTask.getEmail() + " &emsp;</td></tr>" 
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
		String tab=request.getParameter("tab");
        String page=request.getParameter("page");
		model.setViewName("redirect:/PendingTasks?tab="+tab+"&page="+page);
		return model;
	}
	
	@RequestMapping(value = { "/taskArchive" }, method = RequestMethod.POST)
	public ModelAndView archiveTask(@ModelAttribute Task task, HttpServletRequest request)
			throws FileNotFoundException, IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		
		TaskAssign taskAssign = new TaskAssign();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		taskAssign.setTaskID(task.getId());
		taskAssign.setCreatedBy(request.getRemoteUser());
		taskAssign.setCreatedDate(date);
		taskAssign.setStatus("Assigned");
	    String tab=request.getParameter("tab");
	    String page=request.getParameter("page");
		taskDAO.archive(taskAssign);

		Task dbtask = taskDAO.get(task.getId());
        List<String> assignedUsersEmailsList=taskDAO.getAssignedUsersEmails(task.getId());
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
		Email.setSubject("The task " + dbtask.getSubject() + " is resolved");
		String msg = "<p>Hello,</p><br>";
		msg += "<p>The task " + dbtask.getSubject() + " is resolved by "+name+ "</p>";
		msg+= "<table>"
				+ "<tr><td> Tracking Id: &emsp;</td><td> " + dbtask.getTrackingID() + " &emsp;</td></tr>" 
				+ "<tr><td> Name: &emsp;</td><td> " + dbtask.getName() + " &emsp;</td></tr>" 
				+ "<tr><td> Email: &emsp;</td><td> " + dbtask.getEmail()+ " &emsp;</td></tr>" 
				+ "<tr><td> Phone no: &emsp;</td><td> " + dbtask.getPhoneNo() + " &emsp;</td></tr>" 
				+ "<tr><td> Category: &emsp;</td><td> " + dbtask.getCategory() + " &emsp;</td></tr>" 
				+ "<tr><td> Severity:&emsp;</td><td> " + dbtask.getSeverity() + "&emsp;</td></tr>"  
				+ "<tr><td> Description:&emsp;</td><td> " + dbtask.getDescription() + "&emsp;</td></tr></table>" ;
		msg += "<p style='font-style: oblique'>Thoughtwave Software and Solutions Inc.</p>";
		Email.setBody(msg);				
		aws.SendTextEmail(Email, employeeDAO.fullname(username), fromEmailID);
		messageDAO.saveOrUpdate(Email);

		return new ModelAndView("redirect:/PendingTasks?tab="+tab+"&page="+page);

	}
}
