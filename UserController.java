package com.twss.java.spring.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.intuit.ipp.services.DataService;
import com.twss.java.spring.alerts.AlertConfiguration;
import com.twss.java.spring.alerts.AlertDAO;
import com.twss.java.spring.config.GoogleCloudStorage;
import com.twss.java.spring.config.TextMessageImpl;
import com.twss.java.spring.config.Variables;
import com.twss.java.spring.customproperty.CustomPropertyDAO;
import com.twss.java.spring.dao.AssetDAO;
import com.twss.java.spring.dao.ClientDAO;
import com.twss.java.spring.dao.ContactDAO;
import com.twss.java.spring.dao.DepartmentDAO;
import com.twss.java.spring.dao.DesktopMonitoringDAO;
import com.twss.java.spring.dao.DesktopReportsDAO;
import com.twss.java.spring.dao.EmployeeDAO;
import com.twss.java.spring.dao.FaqDAO;
import com.twss.java.spring.dao.FeatureDAO;
import com.twss.java.spring.dao.HelpVideoDAO;
import com.twss.java.spring.dao.InsuranceDAO;
import com.twss.java.spring.dao.MessageDAO;
import com.twss.java.spring.dao.MessageTemplateDAO;
import com.twss.java.spring.dao.OrganizationDAO;
import com.twss.java.spring.dao.OrganizationDAOImpl;
import com.twss.java.spring.dao.ProjectDAO;
import com.twss.java.spring.dao.StatusReportDAO;
import com.twss.java.spring.dao.TaskDAO;
import com.twss.java.spring.dao.TimesheetCriteriaDAO;
import com.twss.java.spring.dao.UserPropertiesDAO;
import com.twss.java.spring.dao.VendorDAO;
import com.twss.java.spring.documentsmodule.DocumentsDAO;
import com.twss.java.spring.emailLimit.EmailLimitDAO;
import com.twss.java.spring.leavemodule.LeaveConfiguration;
import com.twss.java.spring.leavemodule.LeaveRequestDAO;
import com.twss.java.spring.model.Alias;
import com.twss.java.spring.model.Asset;
import com.twss.java.spring.model.Client;
import com.twss.java.spring.model.Contact;
import com.twss.java.spring.model.Department;
import com.twss.java.spring.model.DepartmentMember;
import com.twss.java.spring.model.DesktopClient;
import com.twss.java.spring.model.DesktopMonitorWeek;
import com.twss.java.spring.model.Documents;
import com.twss.java.spring.model.Email;
import com.twss.java.spring.model.EmergencyContact;
import com.twss.java.spring.model.Employee;
import com.twss.java.spring.model.EmployeeCurrentAddresses;
import com.twss.java.spring.model.Feature;
import com.twss.java.spring.model.Files;
import com.twss.java.spring.model.HelpVideos;
import com.twss.java.spring.model.HomeAddr;
import com.twss.java.spring.model.IdleTimeRep;
import com.twss.java.spring.model.Insurance;
import com.twss.java.spring.model.Issue;
import com.twss.java.spring.model.LoginObject;
import com.twss.java.spring.model.MessageTemplate;
import com.twss.java.spring.model.Organization;
import com.twss.java.spring.model.Password;
import com.twss.java.spring.model.ProductivityRep;
import com.twss.java.spring.model.Project;
import com.twss.java.spring.model.ProjectResource;
import com.twss.java.spring.model.Screenshot;
import com.twss.java.spring.model.SwarmVendor;
import com.twss.java.spring.model.Task;
import com.twss.java.spring.model.TaskAssign;
import com.twss.java.spring.model.TaskMessage;
import com.twss.java.spring.model.TimeStatusConfiguration;
import com.twss.java.spring.model.TimesheetCriteria;
import com.twss.java.spring.model.TwoFactorAuthentication;
import com.twss.java.spring.model.WorkAddr;
import com.twss.java.spring.quickbooks.QuickBooksController;
import com.twss.java.spring.quickbooks.QuickBooksControllerHelper;
import com.twss.java.spring.teamsmodule.Manager;
import com.twss.java.spring.teamsmodule.RelationObject;
import com.twss.java.spring.teamsmodule.TeamDAO;
import com.twss.java.spring.utilitybeans.EmailImpl;
import com.twss.java.spring.utilitybeans.TimeFormatter;

/**
 * 
 * Maps all requests originating from the user to the appropriate views.
 * <p>
 * <b>Project Name:</b> TWSSWebApplication
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
public class UserController {

	final static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private OrganizationDAO organizationDAO;
	@Autowired
	private VendorDAO vendorDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ClientDAO clientDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private DocumentsDAO documentsDAO;
	@Autowired
	private MessageDAO messageDAO;
	@Autowired
	private TaskDAO taskDAO;
	@Autowired
	private FeatureDAO featureDAO;
	@Autowired
	private LeaveRequestDAO leaveDAO;
	@Autowired
	private InsuranceDAO insuranceDAO;
	@Autowired
	private DepartmentDAO departmentDAO;
	@Autowired
	private DesktopMonitoringDAO desktopMonitoringDAO;
	@Autowired
	private UserPropertiesDAO userPropertiesDAO;
	@Autowired
	private HelpVideoDAO helpVideoDAO;
	@Autowired
	private DesktopReportsDAO desktopReportsDAO;
	@Autowired
	private MessageTemplateDAO messageTemplateDAO;
	@Autowired
	private GoogleCloudStorage gcs;
	@Autowired
	private TimeFormatter dateTime;
	@Autowired
	private EmailImpl aws;
	@Autowired
	private StatusReportDAO statusReportDAO;
	@Autowired
	private TimesheetCriteriaDAO timesheetCriteriaDAO;
	@Autowired
	private TeamDAO teamDAO;
	@Autowired
	private AlertDAO alertDAO;
	@Autowired
	CustomPropertyDAO customPropertyDAO;
	@Autowired
	private FaqDAO faqDAO;
	private int defaultPagination = 5;
	@Autowired
	private EmailLimitDAO emailLimitdao;
	@Autowired
	private UserPropertiesDAO propertiesDAO;
	@Autowired
	private QuickBooksController quickBooksController;
	@Autowired
	private QuickBooksControllerHelper qbControllerHelper;
	@Autowired
	private ContactDAO contactDAO;
	
	//sp-1335
    @Autowired
    private AssetDAO assetDAO;
	@RequestMapping(value = "/403")
	public ModelAndView accesssDenied(HttpServletRequest request, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			ModelAndView model = new ModelAndView();
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				UserDetails userDetail = (UserDetails) auth.getPrincipal();
				logger.warn("Access Denied For User: " + userDetail.getUsername());
				model.addObject("username", userDetail.getUsername());
			}
			model.setViewName("ErrorPage403");
			return model;
		} catch (Exception exp) {
			return new ModelAndView("redirect:/login?logout");
		}
	}

	@RequestMapping(value = "/404")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView pageNotFound() {
		logger.error("Resource Not Found");
		ModelAndView model = new ModelAndView();
		model.setViewName("ErrorPage404");
		return model;
	}

	@RequestMapping(value = "/400")
	public ModelAndView syntacticallyIncorrect() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ErrorPage400");
		return model;
	}

	@RequestMapping(value = "/500")
	public ModelAndView serverError() {
		ModelAndView model = new ModelAndView();
		model.setViewName("ErrorPage500");
		return model;
	}

	@RequestMapping(value = "/blankScreen")
	public ModelAndView blankScreen(HttpSession session) {
		ModelAndView model = new ModelAndView();
		session.setAttribute("invoiceSearch", null);
		session.setAttribute("invoicePageLink", null);
		model.setViewName("SuccessScreen");
		return model;
	}

	@ExceptionHandler({ java.lang.NullPointerException.class,
			org.springframework.web.servlet.NoHandlerFoundException.class })
	public ModelAndView pageNotFound(Exception exp) {
		StackTraceElement[] stackTrace = exp.getStackTrace();
		String message = "";
		for (StackTraceElement element : stackTrace)
			message += element.toString() + "<br/>";
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

	/******************************
	 * Creation of session object
	 * 
	 * @throws IOException
	 *****************************/

	public void createSessionObject(HttpSession session, String username) {
		// Do not change this order.
		Employee profile = employeeDAO.profile(username);
		String organizationID = profile.getOrganizationID();
		session.setAttribute("profile", profile);
		session.setAttribute("organizationID", organizationID);
		session.setAttribute("organizationName", organizationDAO.getOrganizationName(organizationID));
		logger.info("New Session Created for " + profile.getFirstname() + " " + profile.getLastname() + " At "
				+ dateTime.getCurrentTime());
		setProfilePicture(session, username);
		setDepartmentInfo(session, username);
		setFeatures(session, username);
		session.setMaxInactiveInterval(1800);
		session.setAttribute("pageExpireTime", (session.getMaxInactiveInterval() + 2) * 1000);
		session.setAttribute("employeeNameMap", employeeDAO.getEmployeeNames(organizationID));
		setProperties(session, username);
		calculateSessionSize(session);
	}

	private void calculateSessionSize(HttpSession session) {
		Enumeration<?> enumeration = session.getAttributeNames();
		int totalSize = 0;
		while (enumeration.hasMoreElements()) {
			try {
				Object value = session.getAttribute((String) enumeration.nextElement());
				if (value != null) {
					try {
						totalSize = totalSize + sizeof(value);
					} catch (Exception exp) {
						exp.printStackTrace();
						throw new RuntimeException(exp.getMessage());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("Final Session size: " + Math.round(totalSize / 1024.00) + " KB");
	}

	private static int sizeof(Object obj) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		try {
			objectOutputStream.writeObject(obj);
		} catch (Exception e) {
		}
		objectOutputStream.flush();
		objectOutputStream.close();
		return byteOutputStream.toByteArray().length;
	}

	private void createLoginObject(HttpSession session, HttpServletRequest request) {
		Employee profile = (Employee) session.getAttribute("profile");
		LoginObject obj = new LoginObject();
		obj.setFirstname(profile.getFirstname());
		obj.setLastname(profile.getLastname());
		obj.setUsername(profile.getUsername());
		obj.setOrganizationID(profile.getOrganizationID());
		try {
			if (null == profile.getDepartmentID())
				obj.setDepartmentID("No Department");
			else
				obj.setDepartmentID(profile.getDepartmentID());
		} catch (Exception e) {
			obj.setDepartmentID("No Department");
		}
		obj.setTimeStamp(dateTime.getCurrentTime());
		obj.setOrganizationName((String) session.getAttribute("departmentName"));
		obj.setIpaddress(request.getRemoteAddr());
		obj.setLogChannel("Web Application");
		obj.setLogDate(dateTime.getCurrentSQLDate());
		try {
			employeeDAO.createLoginObject(obj);
		} catch (Exception e) {
			logger.error("Coonecting through localhost");
		}
	}

	private void setProperties(HttpSession session, String username) {
		Map<String, String> allPropertiesMap = loadAllProperties();
		session.setAttribute("allPropertiesMap", allPropertiesMap);

		Map<String, String> availablePropertiesMap = loadAvailableProperties();
		session.setAttribute("defaultMap", availablePropertiesMap);

		Map<String, String> typeValueMap = new LinkedHashMap<String, String>();
		try {
			typeValueMap = userPropertiesDAO
					.listTypesAndValues(((Employee) session.getAttribute("profile")).getOrganizationID());
			if (typeValueMap.size() == 0)
				typeValueMap = loadAvailableProperties();
			session.setAttribute("typeValueMap", typeValueMap);
		} catch (Exception e) {
			typeValueMap = loadAvailableProperties();
			session.setAttribute("typeValueMap", typeValueMap);
		}
		String tableLength = "";
		try {
			tableLength = typeValueMap.get("Default Table Entries");
			if (null == tableLength)
				tableLength = availablePropertiesMap.get("Default Table Entries");
		} catch (Exception e) {
			tableLength = availablePropertiesMap.get("Default Table Entries");
		}
		session.setAttribute("tableLength", tableLength);

		String attendanceLine = "";
		try {
			attendanceLine = typeValueMap.get("Attendance Line");
			if (null == attendanceLine)
				attendanceLine = availablePropertiesMap.get("Attendance Line");
		} catch (Exception e) {
			attendanceLine = availablePropertiesMap.get("Attendance Line");
		}
		session.setAttribute("orgAttendanceLine", attendanceLine);
	}

	@SuppressWarnings("unchecked")
	private void setFeatures(HttpSession session, String username) {
		List<String> userRoles = (List<String>) session.getAttribute("userRoles");
		if (userRoles.contains("ROLE_ORGADMIN")) {
			userRoles.clear();
			userRoles.add("ROLE_USER");
			userRoles.add("ROLE_SUPERUSER");
		}
		Set<Feature> featureList = new HashSet<Feature>(featureDAO.listUserFeatures(username));

		// This try-catch block is required to handle switch departments feature.
		try {
			featureList.addAll(featureDAO.listDepartmentFeatures((String) session.getAttribute("deptID"), userRoles));
		} catch (Exception e) {
			logger.error("Could not find department features for user: " + username);
		}

		featureList.addAll(featureDAO.getBasicFeatures());

		if (userRoles.contains("ROLE_MANAGER")) {
			String organizationID = (String) session.getAttribute("organizationID");
			featureList.addAll(featureDAO.getManagerFeatures(organizationID, username));
		}

		Map<String, Feature> hmap = new LinkedHashMap<String, Feature>();
		for (Feature f : featureList)
			hmap.put(f.getFeatureID(), f);

		List<Feature> features = new ArrayList<Feature>(hmap.values());
		logger.info("Loaded " + features.size() + " Features for user: " + username);

		// Beta Features
		List<String> betaFeatureURIs = new ArrayList<String>();
		betaFeatureURIs.add("data-exchange");
		betaFeatureURIs.add("signRequest");
		betaFeatureURIs.add("lca-and-paf");
		session.setAttribute("betaFeatures", betaFeatureURIs);

		// Sort the features alphabetically
		try {
			Feature homeFeature = hmap.get("EPF001");
			features.remove(homeFeature);

			/*
			 * Start Employee Directory feature will not see normal users
			 */
			if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_HR") || userRoles.contains("ROLE_ORGADMIN")
					|| userRoles.contains("ROLE_MANAGER") || userRoles.contains("ROLE_SUPERADMIN")
					|| userRoles.contains("ROLE_SUPERUSER")) {
				System.out.print("Roles:");
			} else {
				String environment = Variables.site_url;
				if (environment.contains("uat")) {
					Feature empDirectFeature = hmap.get("EPF077");
					features.remove(empDirectFeature);
				} else {
					Feature empDirectProdFeature = hmap.get("EPF074");
					features.remove(empDirectProdFeature);
				}
			}

			/*
			 * end
			 */

			Collections.sort(features, new Comparator<Feature>() {
				@Override
				public int compare(Feature s1, Feature s2) {
					if (s1.getPriorty() == s2.getPriorty()) {
						return s1.getFeatureName().compareToIgnoreCase(s2.getFeatureName());
					}
					return s1.getPriorty() - s2.getPriorty();
				}
			});

			features.add(0, homeFeature);
		} catch (Exception e) {
			logger.error("Error while sorting features for user: " + username);
		}
		session.setAttribute("featureList", features);
	}

	private void setDepartmentInfo(HttpSession session, String username) {
		List<String> userRoles = employeeDAO.userroles(username);
		session.setAttribute("userRoles", userRoles);
		List<DepartmentMember> listDepts = new ArrayList<DepartmentMember>();
		if (userRoles.contains("ROLE_ADMIN"))
			listDepts = departmentDAO.listAdminDepartments(username);
		else
			listDepts = departmentDAO.listUserDepartments(username);
		session.setAttribute("depAdminList", listDepts);
		Department d = new Department();
		if (listDepts.size() > 0) {
			session.setAttribute("deptID", listDepts.get(0).getDepartmentID());
			session.setAttribute("departmentName", listDepts.get(0).getDepartmentName());
			d = departmentDAO.getDepartmentLogoByID(listDepts.get(0).getDepartmentID());
		} else {
			session.setAttribute("departmentName", departmentDAO.getDepartmentName(username));
			d = departmentDAO.getDepartmentLogo(username);
		}

		try {
			session.setAttribute("departmentLogoFileType", d.getLogoFileType());
			String logoString = new String(Base64.encodeBase64(d.getLogoFile()));
			session.setAttribute("deptLogo", logoString);
		} catch (Exception exp) {
			try {
				String logoString = new String(Base64.encodeBase64(
						organizationDAO.get((String) session.getAttribute("organizationID")).getLogoFile()));
				session.setAttribute("deptLogo", logoString);
			} catch (Exception e) {
				Documents defaultPic = documentsDAO.getPic(new String("defaultUserPicture"));
				String logoString = new String(Base64.encodeBase64(defaultPic.getFile()));
				session.setAttribute("deptLogo", logoString);
			}
		}
	}

	private void setProfilePicture(HttpSession session, String username) {
		Documents profilePic = documentsDAO.getPic(username);
		try {
			session.setAttribute("profilePicFilename", profilePic.getFilename());
			String profileImageString = new String(Base64.encodeBase64(profilePic.getFile()));
			session.setAttribute("profilePic", profileImageString);
		} catch (Exception e) {
			Documents defaultPic = documentsDAO.getPic(new String("defaultUserPicture"));
			String profileImageString = new String(Base64.encodeBase64(defaultPic.getFile()));
			session.setAttribute("profilePic", profileImageString);
		}
	}

	public Map<String, String> loadAllProperties() {
		Map<String, String> mapProps = new LinkedHashMap<String, String>();
		LocalDate lastMonth = LocalDate.now().minusMonths(2);
	    LocalDate now = LocalDate.now();
	    String startDate=lastMonth.toString();
	    String endDate=now.toString();
	    String organizationId=OrganizationDAOImpl.organizationID;
	    List<String> adminMailList=organizationDAO.listOrgAdminEmails(organizationId);
	    String mail="";
	    for(String adminEmail:adminMailList) {
	    	mail="["+mail+adminEmail+","+"]";
	    	
	    }
	    //String email="["+"contact@swarmhr.com"+"]";
	    String invoiceAlertProperty=startDate+","+endDate+","+"FRIDAY"+","+"Disabled"+","+mail;
		mapProps.put("Immigration File Type", "I-797/H1B,I-94,Passport,ID,Driving License,LCA,I-29,H4");
		mapProps.put("Employment File Type",
				"Offer Letter,Employment Agreement,SwarmVendor Letter,Client Letter,Experience Letter,Educational Certificates");
		mapProps.put("Onboarding File Type", "I-9,W-4,401-K,Insurance,Direct Deposit Form");
		mapProps.put("Vendor_Client_Supplier File Type", "Contract,MSA,PO,Insurance,W-9,Other");
		mapProps.put("Employee Groups", "Technical Recruiter,Bench Sales Recruiter");
		mapProps.put("Contact Type List", "Recruiter,HR,Other");
		mapProps.put("Pay Type", "Direct Deposit,Wire Transfer,Cheque,Cash,Other");
		mapProps.put("Invoice Status", "Open,Partially Paid,Paid,Closed");
		mapProps.put("Contract", "CTC,CTH,Full Time,Intern,Other,All");
		mapProps.put("Employment Type", "Temporary,W-2,W-2 Contract,1099,C2C,Intern");
		mapProps.put("Immigration Type", "H1-B,U.S.Citizen,G.C.,G.C.EAD,OPT,H4-EAD,T.N.Visa");
		mapProps.put("Base Working Country", "U.S.A.,India");
		mapProps.put("Base Currency", "USD,INR");
		mapProps.put("W2 Onboarding Documents", "W-4,Insurance,Direct Deposit,Void Check,I-9");
		mapProps.put("Indenpendent Consultant Documents",
				"W-9,Direct Deposit,Void Check,Proof of Citizenship,Purchase Order,MSAI");
		mapProps.put("C2C Documents", "W-9,Direct Deposit,Void Check,Insurance Certificate,Purchase Order,MSAI");
		mapProps.put("W2 Consultant Documents", "Direct Deposit,Void Check,W-4,I-9,H1-B/G.C./Citizenship Document");
		mapProps.put("Candidate File Type", "Resume,RTR,Visa,ID,Other");
		mapProps.put("Requirement Status", "Internal Submission,External Submission,Interview");
		mapProps.put("Expense Claim Type", "Certification,Travel,Food,Others");
		mapProps.put("Insurance Type", "Individual,Individual+Spouse,Individual+Kid,Individual+Spouse+Kid(s)");
		mapProps.put("Invoice Due Days", "5,10,15,30,45,60");
		mapProps.put("Timesheet Email", " ");
		mapProps.put("Default Timesheet", "Weekly");
		mapProps.put("Default Table Entries", "10");
		mapProps.put("Attendance Line", "Not Found");
		mapProps.put("Default Timesheet Status", "Saved");
		mapProps.put("Daily Status Report", "Not Required");
		mapProps.put("Invoice Start", "1000");
		mapProps.put("Next Time Based Invoice No", "10");
		mapProps.put("Display Quickbooks No in Invoice", "No");
		mapProps.put("Next Client Based Invoice No", "10");
		mapProps.put("Dept Name in Profile", "No");
		mapProps.put("Billers Type List", "");
		mapProps.put("Organization File Type", "Tax Document, Rental Agreement, Insurance, Other");
		mapProps.put("E-Signature File Type", "Onboarding,Employment,Immigration,Organization,Clients,Vendors,Suppliers,Other");
		mapProps.put("Timesheet Attachments", "Optional");
		mapProps.put("Invoice Attachments", "Merge");
		mapProps.put("Candidate File Type", "Resume,RTR,Visa,ID,Other");
		mapProps.put("Requirement Status", "Internal Submission,External Submission,Interview");
		mapProps.put("EmailLimitPerMonth", "2000");
		mapProps.put("Invoice Alerts",invoiceAlertProperty);
		return mapProps;
	}

	public Map<String, String> loadAvailableProperties() {
		Map<String, String> mapProps = new LinkedHashMap<String, String>();
		LocalDate lastMonth = LocalDate.now().minusMonths(2);
	    LocalDate now = LocalDate.now();
	    String startDate=lastMonth.toString();
	    String endDate=now.toString();
	    String organizationId=OrganizationDAOImpl.organizationID;
	    //String email="["+"contact@swarmhr.com"+"]";
	    logger.info("organizationId--------"+organizationId);
	    List<String> adminMailList=organizationDAO.listOrgAdminEmails(organizationId);
	    String mail="";
	    for(String adminEmail:adminMailList) {
	    	mail="["+mail+adminEmail+","+"]";
	    }
	    String invoiceAlertProperty=startDate+","+endDate+","+"FRIDAY"+","+"Disabled"+","+mail;
		mapProps.put("Immigration File Type", "I-797/H1B,I-94,Passport,ID,Driving License,LCA,I-29,H4");
		mapProps.put("Employment File Type",
				"Offer Letter,Employment Agreement,SwarmVendor Letter,Client Letter,Experience Letter,Educational Certificates");
		mapProps.put("Onboarding File Type", "I-9,W-4,401-K,Insurance,Direct Deposit Form");
		mapProps.put("Vendor_Client_Supplier File Type", "Contract,MSA,PO,Insurance,W-9,Other");
		mapProps.put("Employee Groups", "Technical Recruiter,Bench Sales Recruiter");
		mapProps.put("Contact Type List", "Recruiter,HR,Other,External Manager");
		mapProps.put("Pay Type", "Direct Deposit,Wire Transfer,Cheque,Cash,Other");
		mapProps.put("Invoice Status", "Open,Partially Paid,Paid,Closed");
		mapProps.put("Contract", "CTC,CTH,Full Time,Intern,Other,All");
		mapProps.put("Employment Type", "Temporary,W-2,W-2 Contract,1099,C2C,Intern");
		mapProps.put("Immigration Type", "H1-B,U.S.Citizen,G.C.,G.C.EAD,OPT,H4-EAD,T.N.Visa");
		mapProps.put("Base Working Country", "U.S.A.,India");
		mapProps.put("Base Currency", "USD,INR");
		mapProps.put("Expense Claim Type", "Certification,Travel,Food,Others");
		mapProps.put("Invoice Due Days", "5,10,15,30,45,60");
		mapProps.put("Timesheet Email", " ");
		mapProps.put("Default Timesheet", "Weekly");
		mapProps.put("Default Table Entries", "10");
		mapProps.put("Attendance Line", "Not Found");
		mapProps.put("Default Timesheet Status", "Saved");
		mapProps.put("Daily Status Report", "Not Required");
		mapProps.put("Invoice Start", "1000");
		mapProps.put("Next Time Based Invoice No", "10");
		mapProps.put("Next Client Based Invoice No", "10");
		mapProps.put("Display Quickbooks No in Invoice", "No");
		// task_category:Technical issue, Performance issue, Web UI issue
		// task_severity:Low,Normal,High
		mapProps.put("Task Category", "Technical issue, Performance issue, Web UI issue");
		mapProps.put("Task Severity", "Low,Normal,High");
		mapProps.put("Dept Name in Profile", "No");
		mapProps.put("Billers Type List", "");
		mapProps.put("Organization File Type", "Tax Document, Rental Agreement, Insurance, Other");
		mapProps.put("E-Signature File Type", "Onboarding,Employment,Immigration,Organization,Clients,Vendors,Suppliers,Other");
		mapProps.put("Timesheet Attachments", "Optional");
		mapProps.put("Invoice Attachments", "Merge");
		mapProps.put("Candidate File Type", "Resume,RTR,Visa,ID,Other");
		mapProps.put("Requirement Status", "Internal Submission,External Submission,Interview");
		mapProps.put("TimeSheetNotes", "Disable");
		mapProps.put("ProjectAutoInactive", "Disable");
		mapProps.put("EmailLimitPerMonth", "2000");
		mapProps.put("Invoice Alerts",invoiceAlertProperty);
		mapProps.put("Task Priority","Low,Medium,High,Very High");
		mapProps.put("TaskEnvironment","DEV,UAT,PROD");
		mapProps.put("Task Release","Release V1.26.7,Release V1.26.8,Release V1.26.9");
		mapProps.put("Task FoundInVersion","Release V1.26.7,Release V1.26.8,Release V1.26.9");
		mapProps.put("Task FixedInVersion","Release V1.26.7,Release V1.26.8,Release V1.26.9");
		mapProps.put("Task IssueType","Task,Defect,Story");
		return mapProps;
	}

	@SuppressWarnings("unchecked")
	public ModelAndView loadProfileRelatedInfo(String requestPath, String username, ModelAndView model,
			HttpSession session) {
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		Employee emp = employeeDAO.profile(username);
		String orgAuth = employeeDAO.getAuthentication(emp.getOrganizationID());
		if (orgAuth != null && !orgAuth.isEmpty()) {
			model.setViewName("UserProfile/Login1");
			if (orgAuth.equals("Enable"))
				return model;
		}

		try {
			if ((null == session.getAttribute("deptLogo"))
					|| (null == ((Employee) session.getAttribute("profile")).getAccessType()))
				this.createSessionObject(session, username);
		} catch (Exception e) {
			this.createSessionObject(session, username);
		}
		Employee profile = (Employee) session.getAttribute("profile");
		String featureCheck = "false";
		List<Feature> featureList = (List<Feature>) session.getAttribute("featureList");
		for (Feature f : featureList) {
			String featureUrl = f.getUrl().trim();
			if (requestPath.trim().equals("/" + featureUrl)) {
				model.addObject("featureList", featureList);
				featureCheck = "true";
				model.addObject("currentPageURL", featureUrl);
			}
		}
		if (requestPath.equals("/user"))
			featureCheck = "true";
		try {
			if (profile.getAccessType().equalsIgnoreCase("Mobile") || profile.getAccessType().equalsIgnoreCase("None")
					|| profile.getStatus().equalsIgnoreCase("Disabled") || featureCheck.equals("false")) {
				model.setViewName("redirect:/403");
				return model;
			}
			model.addObject("featureList", featureList);
			model.setViewName("Success");
			return model;

		} catch (Exception exp) {
			this.createSessionObject(session, username);
			profile = (Employee) session.getAttribute("profile");
			if (profile.getAccessType().equalsIgnoreCase("Mobile") || profile.getAccessType().equalsIgnoreCase("None")
					|| profile.getStatus().equalsIgnoreCase("Disabled") || featureCheck.equals("false")) {
				model.setViewName("redirect:/403");
				return model;
			}
			model.addObject("featureList", featureList);
			model.setViewName("Success");
			return model;
		}

	}

	@RequestMapping(value = "/switchDept")
	public ModelAndView switchDepartment(HttpServletRequest request, HttpSession session) {
		ModelAndView model = new ModelAndView();
		String username = request.getParameter("user");
		String currentPage = request.getParameter("cpage");
		String newDept = request.getParameter("switch");
		session.setAttribute("deptID", newDept);
		session.setAttribute("departmentName", departmentDAO.getDepartmentNameByID(newDept));
		// this.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		Department d = departmentDAO.getDepartmentLogoByID(newDept);
		try {
			session.setAttribute("departmentLogoFileType", d.getLogoFileType());
			Documents deptLogo = new Documents();
			deptLogo.setEncodedImage(new String(Base64.encodeBase64(d.getLogoFile())));
			session.setAttribute("deptLogo", deptLogo);
		} catch (Exception exp) {
			try {
				Documents defaultPic = new Documents();
				defaultPic.setEncodedImage(new String(
						Base64.encodeBase64(organizationDAO.get(profile.getOrganizationID()).getLogoFile())));
				session.setAttribute("deptLogo", defaultPic);
			} catch (Exception e) {
				Documents defaultPic = documentsDAO.getPic(new String("defaultUserPicture"));
				defaultPic.setEncodedImage(new String(Base64.encodeBase64(defaultPic.getFile())));
				session.setAttribute("deptLogo", defaultPic);
			}
		}
		setFeatures(session, username);
		model.setViewName("redirect:/" + currentPage);
		return model;
	}

	/**********************************************************************************************
	 * 
	 ************************************** Landing Page ******************************************
	 * 
	 **********************************************************************************************/

	/**
	 * 
	 * Redirects to the landing page of the Portal.
	 * <p>
	 * This method redirects the user to the landing page of the portal. On a
	 * successful login, the user is taken to a page which presents a snapshot of
	 * the user's messages, tasks and timesheets.
	 * <p>
	 * The user will be able to view the top 10 messages from his inbox as well as
	 * top 10 tasks that have been assigned. The user can also enter work hours for
	 * the current day and add any related notes.
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * 
	 */

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ModelAndView landingPage(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		try {
			String username = request.getRemoteUser();
			model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
			if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
				return model;

			if (null == session.getAttribute("deptLogo"))
				this.createSessionObject(session, username);

			model.setViewName("User");
			return model;
		} catch (Exception e) {
			logger.error(e.getMessage());
			request.getSession().invalidate();
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/loginCheck")
	public ModelAndView creatingLoginObject(ModelAndView model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws IOException, ParseException {
		String username = request.getRemoteUser();

		TextMessageImpl smsMessage = new TextMessageImpl();

		Employee emp = employeeDAO.profile(request.getRemoteUser());
		// String usrPass = employeeDAO.getUsrPass(request.getRemoteUser());
		String orgAuth = null;

		// Fix for SP-906
		if (emp != null)
			orgAuth = employeeDAO.getAuthentication(emp.getOrganizationID());

		try {

			String otpValue = "";
			String passValue = "";
			String otpTmp = request.getParameter("otp");
			StringTokenizer strn = new StringTokenizer(otpTmp, ",");
			while (strn.hasMoreTokens()) {
				passValue = strn.nextToken();
				otpValue = strn.nextToken();
			}
			if (otpValue.length() < 5 && orgAuth != null && !orgAuth.isEmpty()) {
				String empAuth = emp.getAuthentication();
				String pinValidation = emp.getAuthenticationType();
				if (empAuth != null && !empAuth.isEmpty()) {
					if (pinValidation != null && !pinValidation.isEmpty()) {
						if (orgAuth.equals("Enable") && emp.getAuthentication().equals("Enable")) {
							int countryCode;
							Random rand = new Random();
							String OTP = String.format("%06d", rand.nextInt(1000000));
							String textMessage = emp.getFirstname() + " " + emp.getLastname() + ","
									+ "\t\r\n Please use PIN: " + OTP + " to Login to SWARM HR Application";
							String country = employeeDAO.country(username, emp.getOrganizationID());
							if (country.equals("India")) {
								countryCode = 91;
							} else {
								countryCode = 1;
							}
							if (emp.getAuthenticationType().equals("Phone")) {
								smsMessage.sendMessage(countryCode, emp.getMobile(), textMessage);
							}
							if (emp.getAuthenticationType().equals("Email")) {
								Email email = new Email();
								email.setTo(emp.getEmail());
								email.setSubject("OTP Login for SWARM HR");
								email.setBody("Dear " + emp.getLastname() + "," + emp.getFirstname() + ","
										+ "\t\r\n Please use PIN: " + OTP + " to Login to SWARM HR Application.");
								String fromEmailID = organizationDAO.get(emp.getOrganizationID()).getEmail();
								String fromName = employeeDAO.getName(username);
								try {
									fromEmailID = customPropertyDAO
											.getCutomPropertyDetailsByName("Default-Email", emp.getOrganizationID())
											.getPropertyValue().replace(",", "");
								} catch (Exception e) {
									// e.printStackTrace();
								}
								aws.SendTextEmail(email, fromName, fromEmailID);
							}

							System.out.println("OTP :: " + OTP);

							model.addObject("usrPass", passValue);
							model.addObject("usrName", username);
							model.addObject("genOtp", OTP);
							model.setViewName("UserProfile/Login1");
							return model;
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("2 FA login Issue due to : " + e);
			model = this.loadProfileRelatedInfo("/user", username, model, session);
		}

		model = this.loadProfileRelatedInfo("/user", username, model, session);
		try {
			this.createLoginObject(session, request);
		} catch (Exception e) {
			logger.error("Connecting through localhost");
		}
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, username);

		/*
		 * if(request.isUserInRole("ROLE_MANAGER")) return new
		 * ModelAndView("redirect:/my-team");
		 */

		return new ModelAndView("redirect:/user");

	}

	@RequestMapping(value = "/changelog", method = RequestMethod.GET)
	public ModelAndView changeLog(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException, ParseException {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, username);

		model.setViewName("Changelog");
		return model;

	}

	/**********************************************************************************************
	 * 
	 ***************************** Profile & Associated Methods ***********************************
	 * 
	 **********************************************************************************************/

	/**
	 *
	 * Redirects the user to his profile page.
	 * <p>
	 * This method redirects the user to the profile page. It fetches the profile
	 * information and the user's current project information. It also fetches the
	 * user's address history for both home and work addresses.
	 *
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/MyProfile")
	public ModelAndView userMiniProfile(ModelAndView model, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		this.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String organizationID = profile.getOrganizationID();
		List<ProjectResource> currentProjects = employeeDAO.listCurrentProjects(username);
		List<ProjectResource> projectList = employeeDAO.listProjectResource(username);
		List<HomeAddr> homeAddressList = employeeDAO.listHomeAddr(username);
		List<WorkAddr> workAddressList = employeeDAO.listWorkAddr(username);
		List<HomeAddr> tempHomeAddress = new ArrayList<>();
		for (HomeAddr homeAddr : homeAddressList) {
			homeAddr.setAddress(homeAddr.getAddress());
			tempHomeAddress.add(homeAddr);
		}
		List<WorkAddr> tempWorkAddress = new ArrayList<>();
		for (WorkAddr workAddr : workAddressList) {
			workAddr.setAddress(workAddr.getAddress());
			tempWorkAddress.add(workAddr);
		}
		// getting clients and vendors by projectID
		for (ProjectResource projectResource : currentProjects) {
			SwarmVendor vendor = employeeDAO.getVendorByID(projectResource.getVendorID());
			if(vendor.getVendorID()!=null && !vendor.getVendorID().equalsIgnoreCase("") 
					&& !vendor.getVendorID().equalsIgnoreCase("null") && vendor.getVendorID().trim().length()>0) {
				WorkAddr workAddr = new WorkAddr();
				workAddr.setAddressID(vendor.getVendorID());
				workAddr.setAddress(projectResource.getProjectName()+" : Vendor - " + vendor.getAddress());
				// workAddr.setEmailID(rs.getString("emailID"));
				workAddr.setDeskPhone(vendor.getPhone1());
				// workAddr.setExt(vendor.getPhone1());
				workAddr.setCity(vendor.getCity());
				workAddr.setState(vendor.getState());
				workAddr.setCountry(vendor.getCountry());
				workAddr.setZipcode(vendor.getZipcode());
				// workAddr.setStartDate(String.valueOf(rs.getDate("startDate")));
				// workAddr.setEndDate(String.valueOf(rs.getDate("endDate")));
				tempWorkAddress.add(workAddr);
			}	
			Client client = employeeDAO.getClientByID(projectResource.getClientID());
			if(client.getClientID()!=null && !client.getClientID().equalsIgnoreCase("") 
					&& !client.getClientID().equalsIgnoreCase("null") && client.getClientID().trim().length()>0) {
				WorkAddr workAddr1 = new WorkAddr();
				workAddr1.setAddressID(client.getClientID());
				workAddr1.setAddress(projectResource.getProjectName()+" : Client - " + client.getAddress());
				// workAddr1.setEmailID(rs.getString("emailID"));
				workAddr1.setDeskPhone(client.getPhone1());
				// workAddr1.setExt(vendor.getPhone1());
				workAddr1.setCity(client.getCity());
				workAddr1.setState(client.getState());
				workAddr1.setCountry(client.getCountry());
				workAddr1.setZipcode(client.getZipcode());
				// workAddr1.setStartDate(String.valueOf(rs.getDate("startDate")));
				// workAddr1.setEndDate(String.valueOf(rs.getDate("endDate")));
				tempWorkAddress.add(workAddr1);
			}	

		}
		
		//getting default work and home addresses details
		EmployeeCurrentAddresses currentAddress = employeeDAO.getEmployeeCurrentAddresses(username);

		EmergencyContact emergencyContactList = employeeDAO.getEmergencyContact(username);
		Map<String, List<String>> projects = projectDAO.listProjects(organizationID);
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");
		Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
		Map<String, String> invoiceDueDays = new LinkedHashMap<String, String>();
		String c1 = typeValueMap.get("Invoice Due Days");
		String[] c = new String[1];
		c[0] = " ";

		try {
			c = c1.split(",");
		} catch (Exception x) {
			logger.info("No Invoice Due Days Found For: " + profile.getFirstname() + " " + profile.getLastname());
			String c2 = defaultMap.get("Invoice Due Days");
			c = c2.split(",");
		}
		for (int i = 0; i < c.length; i++)
			invoiceDueDays.put(c[i], c[i] + " Days");

		model.addObject("dueDays", invoiceDueDays);

		Map<String, String> weekdays = new LinkedHashMap<String, String>();
		weekdays.put("Monday", "Monday");
		weekdays.put("Sunday", "Sunday");
		Map<String, String> invoiceType = new LinkedHashMap<String, String>();
		invoiceType.put("Monthly", "Monthly");
		invoiceType.put("Bi-Monthly", "Bi-Monthly");
		invoiceType.put("Weekly", "Weekly");
		invoiceType.put("Bi-Weekly", "Bi-Weekly");
		invoiceType.put("4/5 Weeks", "4/5 Weeks");
		invoiceType.put("Custom Dates", "Custom Dates");
		model.addObject("weekdays", weekdays);
		model.addObject("invoiceType", invoiceType);

		// Dropdown options for timesheet configuration
		Map<String, String> submissionType = new LinkedHashMap<String, String>();
		submissionType.put("Daily", "Daily");
		submissionType.put("Weekly", "Weekly");
		model.addObject("submissionType", submissionType);

		// Default Timesheet Submission Type for organization
		String type = "";
		try {
			type = typeValueMap.get("Default Timesheet");
			if (null == type)
				type = defaultMap.get("Default Timesheet");
		} catch (Exception exp) {
			type = defaultMap.get("Default Timesheet");
		}

		// Dropdown options for timesheet reporting type
		Map<String, String> timeReporting = new LinkedHashMap<String, String>();
		timeReporting.put("Web", "Web");
		timeReporting.put("Mobile App", "Mobile App");
		timeReporting.put("PunchIn", "PunchIn");
		timeReporting.put("Scanner", "Scanner");
		timeReporting.put("Phone", "Phone");
		timeReporting.put("Email", "Email");
		model.addObject("timeReporting", timeReporting);

		// Employee's Timesheet Submission Type
		TimesheetCriteria empTimeReportingConfig = timesheetCriteriaDAO.getTimesheetCriteria(username);
		if (null == empTimeReportingConfig.getSubmissionType())
			empTimeReportingConfig.setSubmissionType(type);
		if (null == empTimeReportingConfig.getAutoSubmission())
			empTimeReportingConfig.setAutoSubmission("No");
		if (null == empTimeReportingConfig.getTimeReporting())
			empTimeReportingConfig.setTimeReporting("Web");
		model.addObject("empTimeReportingConfig", empTimeReportingConfig);

		// Default Status Report configuration for organization
		String reporting = "";
		try {
			reporting = typeValueMap.get("Daily Status Report");
			if (null == reporting)
				reporting = defaultMap.get("Daily Status Report");
		} catch (Exception exp) {
			reporting = defaultMap.get("Daily Status Report");
		}
		if (reporting.equalsIgnoreCase("Yes"))
			reporting = "Required";

		// Employee's Status Report configuration
		String employeeStatusReport = statusReportDAO.getStatusReportRequirement(username, organizationID);
		if (employeeStatusReport.equalsIgnoreCase("true")) {
			employeeStatusReport = "Required";
		} else if (employeeStatusReport.equalsIgnoreCase("false")) {
			employeeStatusReport = "Not Required";
		} else if (employeeStatusReport.equalsIgnoreCase("Not Found")) {
			employeeStatusReport = reporting;
		}
		model.addObject("employeeStatusReportRequirement", employeeStatusReport);

		// Employee-Manager Relation
		/*
		 * RelationObject managerRelation = teamDAO.getEmployeeManager(organizationID,
		 * username);
		 */
		/* String managerName = "No Manager"; */
		List<RelationObject> managerRelation = teamDAO.getEmployeeManagerList(organizationID, username);
		System.out.println(managerRelation);
		String managerName = "";
		/*
		 * if (null != managerRelation.getManagerFullName()) managerName =
		 * managerRelation.getManagerFullName(); model.addObject("managerName",
		 * managerName);
		 */
		List<String> m = new ArrayList<String>();

		for (int i = 0; i < managerRelation.size(); i++) {

			m.add(managerRelation.get(i).getManagerFullName());
			managerName = managerRelation.get(i).getManagerFullName().concat(";").concat(managerName);
		}
//to get all contacts for employee			
		List<Contact> employeeContacts = contactDAO.employeeContactsList(username);
		model.addObject("employeeContacts",employeeContacts);
		model.addObject("managerName", managerName);
		model.addObject("managerListofEmployee", managerRelation);
	
		model.addObject("newManagerRelation", new RelationObject());
		model.addObject("newManagerRelation1", new RelationObject());
		List<Manager> managerList = teamDAO.getManagers(organizationID);
		model.addObject("managerList", managerList);
		model.addObject("employee", new Employee());
		model.addObject("alias", new Alias());
		model.addObject("homeAddress", new HomeAddr());
		model.addObject("workAddress", new WorkAddr());
		model.addObject("emergencyContact", emergencyContactList);
		model.addObject("emContact", new EmergencyContact());

		model.addObject("currentProjects", currentProjects);
		model.addObject("projectList", projectList);
		model.addObject("homeList", homeAddressList);
		model.addObject("workList", workAddressList);
		// adding temp work and home addresses
		model.addObject("homeList1", tempHomeAddress);
		model.addObject("workList1", tempWorkAddress);
		model.addObject("currentAddress",currentAddress);
		
		model.addObject("empProfile", profile);
		model.addObject("Password", new Password());
		model.addObject("projectObject", new ProjectResource());
		model.addObject("availableProjects", projects);

		Map<String, String> authentication = new LinkedHashMap<String, String>();
		authentication.put("Disable", "Disable");
		authentication.put("Enable", "Enable");
		model.addObject("authentication", authentication);

		Map<String, String> authenticationType = new LinkedHashMap<String, String>();
		authenticationType.put("Email", "Email");
		authenticationType.put("Phone", "Phone");
		model.addObject("authenticationType", authenticationType);

		if (request.isUserInRole("ROLE_SUPERUSER"))
			model.addObject("userRole", "SuperAdmin");
		else
			model.addObject("userRole", "User");

		if (null != session.getAttribute("ProjectMessage")) {
			model.addObject("Message", session.getAttribute("ProjectMessage"));
			session.setAttribute("ProjectMessage", null);
		}

		if (null != session.getAttribute("updateProfileMessage")) {
			model.addObject("Message", session.getAttribute("updateProfileMessage"));
			session.setAttribute("updateProfileMessage", null);
		}

		if (null != session.getAttribute("profilePictureMessage")) {
			model.addObject("Message", session.getAttribute("profilePictureMessage"));
			session.setAttribute("profilePictureMessage", null);
		}

		if (null != session.getAttribute("homeAddressMessage")) {
			model.addObject("Message", session.getAttribute("homeAddressMessage"));
			session.setAttribute("homeAddressMessage", null);
		}

		if (null != session.getAttribute("workAddressMessage")) {
			model.addObject("Message", session.getAttribute("workAddressMessage"));
			session.setAttribute("workAddressMessage", null);
		}

		if (null != session.getAttribute("emergencyContactMessage")) {
			model.addObject("Message", session.getAttribute("emergencyContactMessage"));
			session.setAttribute("emergencyContactMessage", null);
		}

		if (null != session.getAttribute("addPasswordMessage")) {
			model.addObject("Message", session.getAttribute("addPasswordMessage"));
			session.setAttribute("addPasswordMessage", null);
		}

		if (null != session.getAttribute("alertConfigMessage")) {
			model.addObject("Message", session.getAttribute("alertConfigMessage"));
			session.setAttribute("alertConfigMessage", null);
		}

		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());

		} catch (Exception exp) {
			logger.info("there are no videos");
		}

		String TwoFactorCondidtion = organizationDAO.getTwoFactorStatus(organizationID).toLowerCase();
		TwoFactorAuthentication twoFactorAuthentication = new TwoFactorAuthentication();
		twoFactorAuthentication.setAuthentication(profile.getAuthentication());
		twoFactorAuthentication.setAuthenticationType(profile.getAuthenticationType());
		model.addObject("authenticationObject", twoFactorAuthentication);

		AlertConfiguration orgAlertConfiguration = alertDAO.getAlertconfiguration(organizationID);
		model.addObject("orgAlertConfiguration", orgAlertConfiguration);
		AlertConfiguration userlertConfiguration = alertDAO.getUserAlertconfiguration(organizationID, username);
		model.addObject("userAlertConfiguration", userlertConfiguration);

		model.addObject("TwoFactorCondidtion", TwoFactorCondidtion);
		model.addObject("employeeLeaveConfigList", leaveDAO.listEmployeeLeaveConfiguration(organizationID, username));
		model.addObject("leaveConfigList", leaveDAO.listLeaveConfiguration(organizationID));
		model.addObject("leaveConfig", new LeaveConfiguration());
		model.addObject("timeStatusConfig", new TimeStatusConfiguration());

		model.setViewName("UserProfile/MyProfile");
		return model;
	}

	@RequestMapping(value = "/makeDefaultEmployeeAddress", method = RequestMethod.GET)
	public boolean makeDefaultEmployeeAddress(@RequestParam("addressID") String addressID,
			@RequestParam("type") String type, @RequestParam("empUserName") String empUserName, HttpServletRequest request,
			HttpSession session) {
		EmployeeCurrentAddresses dataItem = new EmployeeCurrentAddresses();
		String username = request.getRemoteUser();
		if (null == username)
			return false;

		String organizationID = (String) session.getAttribute("organizationID");
		dataItem.setOrganizationID(organizationID);
		String addressType = "";
		if (type.equalsIgnoreCase("home")) {
			addressType = "home";
			dataItem.setAddress_htype(addressType);
			dataItem.setAddress_hid(addressID);
		} else if (type.equalsIgnoreCase("work")) {
			if (addressID.contains("VENDOR")) {
				addressType = "vendor";
			} else if (addressID.contains("CLIENT")) {
				addressType = "client";
			} else {
				addressType = "work";
			}
			dataItem.setAddress_wtype(addressType);
			dataItem.setAddress_wid(addressID);
		}
		
		dataItem.setCreated_by(employeeDAO.fullname(username));
		dataItem.setModified_by(employeeDAO.fullname(username));
		dataItem.setUsername(empUserName);
		
		boolean isRequestStatus = employeeDAO.setEmployeeCurrentAddress(addressType, dataItem);
		
		if(isRequestStatus) {
			logger.info("Sucessfully default the selected address!");
		}else {
			logger.info("Failed to default the selected address!");
		}
		
		/*if (employeeDAO.setEmployeeCurrentAddress(addressType, dataItem))
			session.setAttribute("alertConfigMessage", "Sucessfully default the selected address!");
		else
			session.setAttribute("alertConfigMessage", "Failed to default the selected address!");*/

		//return new ModelAndView("redirect:/MyProfile");
		return isRequestStatus;
	}

	/**
	 * Saves the alert configuration for a user. Alert configuration determines the
	 * frequency of sending email alerts.
	 * 
	 * @param config  - Alert Configuration object containing the required details
	 * @param request - HttpServletRequest object
	 * @param session - Session object of the current session
	 * @return Returns to the Profile page after the operation is complete.
	 */
	@RequestMapping(value = "/addUserAlertConfiguration", method = RequestMethod.POST)
	public ModelAndView addUserAlertConfiguration(@ModelAttribute AlertConfiguration config, HttpServletRequest request,
			HttpSession session) {
		String username = request.getRemoteUser();
		if (null == username)
			return new ModelAndView("redirect:/login?session");

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, request.getRemoteUser());

		String organizationID = (String) session.getAttribute("organizationID");
		config.setUsername(username);

		if (alertDAO.createUserAlertConfiguration(organizationID, config))
			session.setAttribute("alertConfigMessage", "Alert Configuration Updated Successfully.");
		else
			session.setAttribute("alertConfigMessage", "Failed to Update Alert Configuration.");

		return new ModelAndView("redirect:/MyProfile");
	}

	@RequestMapping(value = "/saveInsurance", method = RequestMethod.POST)
	public ModelAndView saveInsurance(@ModelAttribute Insurance insurance, HttpServletRequest request) {
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		try {
			if (!(insurance.getAttachment().getContentType().equals("application/octet-stream"))) {
				insurance.setInsuranceFileName(insurance.getAttachment().getOriginalFilename());
				insurance.setInsuranceFileType(insurance.getAttachment().getContentType());
				insurance.setInsuranceFile(insurance.getAttachment().getBytes());
			}
		} catch (Exception e) {
		}

		Employee employee = employeeDAO.profile(insurance.getUsername());
		insurance.setOrganizationID(employee.getOrganizationID());
		insurance.setCreatedBy(name);
		insurance.setModifiedBy(name);
		insurance.setCreatedDate(date);
		insurance.setModifiedDate(date);

		insuranceDAO.saveOrUpdate(insurance);
		ModelAndView model = new ModelAndView();
		model.addObject("Message", "");
		model.setViewName("redirect:/EmpProfile?usrname=" + insurance.getUsername());
		return model;
	}

	/**
	 *
	 * Allows the user to edit his/her profile.
	 *
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView editProfile(HttpServletRequest request) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		String username = request.getRemoteUser();
		Employee employee = employeeDAO.profile(username);
		employee.setUsername(username);
		ModelAndView model = new ModelAndView();
		model.addObject("employee", employee);
		model.setViewName("EditProfile");
		return model;
	}

	/**
	 *
	 * Saves the profile changes made by the user.
	 *
	 * @param employee
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/saveProfile", method = RequestMethod.POST)
	public ModelAndView saveProfile(@ModelAttribute Employee employee, HttpServletRequest request) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		employee.setModifiedBy(name);
		employee.setModifiedDate(date);
		employeeDAO.updateProfile(employee);
		return new ModelAndView("redirect:/MyProfile");
	}

	@RequestMapping(value = "/ChangeUserAuthenticationConfiguration", method = RequestMethod.POST)
	public ModelAndView ChangeUserAuthenticationConfiguration(@ModelAttribute TwoFactorAuthentication authentication,
			HttpServletRequest request) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		authentication.setName(name);
		authentication.setDate(date);
		employeeDAO.ChangeUserAuthenticationConfiguration(authentication);
		return new ModelAndView("redirect:/MyProfile");
	}

	/**
	 *
	 * Saves the profile changes made by the user.
	 *
	 * @param employee
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/saveEmContact", "/hr/saveEmContact" }, method = RequestMethod.POST)
	public ModelAndView saveEmergencyContact(@ModelAttribute EmergencyContact contact, HttpServletRequest request,
			HttpSession session) {
		String reqPath = request.getServletPath();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			if (reqPath.equals("/hr/saveEmContact")) {
				return new ModelAndView("redirect:/login?session");
			} else if (reqPath.equals("/saveEmContact"))
				return new ModelAndView("redirect:/login?session");
			return newModel;
		}
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		contact.setModifiedBy(name);
		contact.setModifiedDate(date);
		String message = employeeDAO.saveEmergencyContact(contact);
		session.setAttribute("emergencyContactMessage", message);
		ModelAndView returnModel = new ModelAndView();
		if (reqPath.equals("/saveEmContact"))
			return new ModelAndView("redirect:/MyProfile");
		else if (reqPath.equals("/hr/saveEmContact"))
			return new ModelAndView("redirect:/EmpMiniProfile?usrname=" + contact.getUsername());

		return returnModel;
	}

	/**
	 * Deletes the profile picture of a user.
	 * 
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/deleteProfilePic" }, method = RequestMethod.GET)
	public ModelAndView deleteProfilePic(HttpServletRequest request) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		String username = request.getRemoteUser();
		documentsDAO.deletePic(username);
		return new ModelAndView("redirect:/MyProfile");
	}

	/**
	 *
	 * Updates the modified password to the user's profile. This method is
	 * accessible only from the user's profile.
	 *
	 * @param password
	 * @param request
	 * @param redirectAttributes
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/UpdatePassword" }, method = RequestMethod.POST)
	public ModelAndView savePassword(@ModelAttribute Password password, HttpServletRequest request, HttpSession session,
			final RedirectAttributes redirectAttributes) {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");

		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();

		password.setModifiedBy(name);
		password.setModifiedDate(date);

		String status = "";
		if (employeeDAO.changePassword(password))
			status = "Password update successful.";
		else
			status = "Please enter correct Password.";

		session.setAttribute("addPasswordMessage", status);

		return new ModelAndView("redirect:/MyProfile");
	}

	/**
	 *
	 * Saves the Home Address of an Employee.
	 * 
	 * @param homeAddress
	 * @param request
	 * @return ModelAndView
	 * @throws ParseException
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/saveHomeAddr", "/updateHomeAddr", "/hr/saveHomeAddr",
			"/hr/updateHomeAddr" }, method = RequestMethod.POST)
	public ModelAndView saveHomeAddr(@ModelAttribute HomeAddr homeAddress, HttpServletRequest request,
			HttpSession session) throws ParseException {

		String reqPath = request.getServletPath();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			if (reqPath.equals("/hr/saveHomeAddr") || reqPath.equals("/hr/updateHomeAddr")) {
				newModel.addObject("Message", "You are not logged in. Please login to modify Employee Details.");
				newModel.addObject("reloadFlag", "reloadPage");
				newModel.setViewName("SuccessScreen");
			} else if (reqPath.equals("/saveHomeAddr") || reqPath.equals("/updateHomeAddr"))
				newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		try {
			if (homeAddress.getEndDate().isEmpty())
				homeAddress.setEndDate(null);
		} catch (Exception e) {
			homeAddress.setEndDate(null);
		}
		homeAddress.setModifiedBy(name);
		homeAddress.setModifiedDate(date);
		String returnMessage = "";
		if (reqPath.equals("/hr/updateHomeAddr") || reqPath.equals("/updateHomeAddr")) {
			returnMessage = employeeDAO.updateHomeAddr(homeAddress);
		} else {
			String timeStamp = dateTime.getCurrentTimeStampForId();
			homeAddress.setAddressID(homeAddress.getUsername() + timeStamp);
			homeAddress.setCreatedBy(name);
			homeAddress.setCreatedDate(date);
			returnMessage = employeeDAO.addHomeAddr(homeAddress);
		}

		// quickbook address update
		if (!returnMessage.equals("") && returnMessage.contains("Successfully") && homeAddress != null) {
			String organizationID = (String) session.getAttribute("organizationID");

			DataService service = quickBooksController.getService(session);
			String accessToken = (String) session.getAttribute("access_token");
			if (accessToken != null) {
				List<String> emplist = new ArrayList<String>();
				emplist.add(homeAddress.getUsername());
				List<Employee> rejectedEmployeeList = qbControllerHelper.processEmployeeExport(service, "Update",
						organizationID, emplist);

				if (rejectedEmployeeList.size() > 0) {
					logger.info("Failed to update employee home address in quickbooks");
				} else
					logger.info("home address was successfully updated in quickbooks");
			}

		}

		session.setAttribute("homeAddressMessage", returnMessage);
		ModelAndView returnModel = new ModelAndView();
		if (reqPath.equals("/saveHomeAddr") | reqPath.equals("/updateHomeAddr"))
			return new ModelAndView("redirect:/MyProfile");
		else if (reqPath.equals("/hr/saveHomeAddr") | reqPath.equals("/hr/updateHomeAddr"))
			return new ModelAndView("redirect:/EmpMiniProfile?usrname=" + homeAddress.getUsername());
		return returnModel;
	}

	/**
	 *
	 * Saves the Work Address of an Employee.
	 *
	 * @param workAddress
	 * @param request
	 * @return ModelAndView
	 * @throws ParseException
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/saveWorkAddr", "/updateWorkAddr", "/hr/saveWorkAddr",
			"/hr/updateWorkAddr" }, method = RequestMethod.POST)
	public ModelAndView saveWorkAddr(@ModelAttribute WorkAddr workAddress, HttpServletRequest request,
			HttpSession session) throws ParseException {
		String reqPath = request.getServletPath();
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			if (reqPath.equals("/hr/saveWorkAddr") || reqPath.equals("/hr/updateWorkAddr")) {
				newModel.addObject("Message", "You are not logged in. Please login to modify Employee Details.");
				newModel.addObject("reloadFlag", "reloadPage");
				newModel.setViewName("SuccessScreen");
			} else if (reqPath.equals("/saveWorkAddr") | reqPath.equals("/updateWorkAddr"))
				newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String name = employeeDAO.userDetails(request.getRemoteUser());
		java.sql.Date date = dateTime.getCurrentSQLDate();
		workAddress.setModifiedBy(name);
		workAddress.setModifiedDate(date);
		try {
			if (workAddress.getEndDate().isEmpty())
				workAddress.setEndDate(null);
		} catch (Exception e) {
			workAddress.setEndDate(null);
		}
		String returnMessage = "";
		if (reqPath.equals("/hr/updateWorkAddr") || reqPath.equals("/updateWorkAddr")) {
			returnMessage = employeeDAO.updateWorkAddr(workAddress);
		} else {
			String timeStamp = dateTime.getCurrentTimeStampForId();
			workAddress.setAddressID(workAddress.getUsername() + timeStamp);
			workAddress.setCreatedBy(name);
			workAddress.setCreatedDate(date);
			returnMessage = employeeDAO.addWorkAddr(workAddress);
		}
		session.setAttribute("workAddressMessage", returnMessage);
		ModelAndView returnModel = new ModelAndView();
		if (reqPath.equals("/saveWorkAddr") | reqPath.equals("/updateWorkAddr"))
			return new ModelAndView("redirect:/MyProfile");
		else if (reqPath.equals("/hr/saveWorkAddr") | reqPath.equals("/hr/updateWorkAddr"))
			return new ModelAndView("redirect:/EmpMiniProfile?usrname=" + workAddress.getUsername());
		return returnModel;
	}

	/**********************************************************************************************
	 * 
	 ***************************** Messages & Associated Methods **********************************
	 * 
	 **********************************************************************************************/

	/**
	 * 
	 * Redirects the user to the Messages Page.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/Messages")
	public ModelAndView Messages(HttpServletRequest request, HttpSession session) {
		ModelAndView model = new ModelAndView();
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, username);
		Employee profile = (Employee) session.getAttribute("profile");
		String emailid = profile.getEmail();

		try {
			String tabName = request.getParameter("view");
			int pageNumber = Integer.parseInt(request.getParameter("page"));
			if (tabName.equals("sent")) {
				tabName = "Sent";
				List<Email> sent = messageDAO.sent(username, pageNumber);

				model.addObject("sentPageNumber", pageNumber);
				model.addObject("sent", sent);
				List<Email> inbox = messageDAO.inbox(emailid, 1);
				model.addObject("inbox", inbox);
				model.addObject("inboxPageNumber", 1);
				model.addObject("tabName", tabName);
			} else {
				tabName = "Inbox";
				List<Email> sent = messageDAO.sent(username, 1);
				model.addObject("sent", sent);
				model.addObject("sentPageNumber", 1);
				List<Email> inbox = messageDAO.inbox(emailid, pageNumber);
				model.addObject("inbox", inbox);
				model.addObject("inboxPageNumber", pageNumber);
				model.addObject("tabName", tabName);
			}
		} catch (Exception exp) {
			String tabName = "Inbox";
			int pageNumber = 1;
			List<Email> inbox = messageDAO.inbox(emailid, pageNumber);
			List<Email> sent = messageDAO.sent(username, pageNumber);
			model.addObject("inbox", inbox);
			model.addObject("sent", sent);
			model.addObject("inboxPageNumber", 1);
			model.addObject("sentPageNumber", 1);
			model.addObject("tabName", tabName);
		}
		Email Email = new Email();
		model.addObject("Email", Email);

		List<Alias> listAlias = messageTemplateDAO.getAliasActiveByUsername(username);

		Map<String, String> AliasList = new LinkedHashMap<String, String>();
		Map<String, Alias> aliasMap = new LinkedHashMap<String, Alias>();
		String defaultEmail = employeeDAO.getEmail(username);
		AliasList.put("Self", employeeDAO.userDetails(username) + " (" + defaultEmail + ")-Default");
		Employee emp = employeeDAO.getbyUsername(username);
		Alias al = new Alias();
		al.setFirstname(emp.getFirstname());
		al.setLastname(emp.getLastname());
		al.setEmail(emp.getEmail());
		aliasMap.put("Self", al);

		// Fix for SP-904
		List<String> addedEmailIds = new ArrayList<String>();
		addedEmailIds.add(defaultEmail);

		for (Alias a : listAlias) {
			if (!addedEmailIds.contains(a.getEmail())) {
				AliasList.put(String.valueOf(a.getId()),
						a.getLastname() + ", " + a.getFirstname() + " (" + a.getEmail() + ")");
				addedEmailIds.add(a.getEmail());
				aliasMap.put(String.valueOf(a.getId()), a);
			}
		}
		model.addObject("AliasList", AliasList);
		model.addObject("aliasMap", aliasMap);
		List<MessageTemplate> messageTemplateList = messageTemplateDAO.getTemplateByUsername(username,
				employeeDAO.orgIDByUsername(username));

		model.addObject("messageTemplateList", messageTemplateList);

		try {
			// Expense Module Help Video
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("there are no videos");
		}
		//email limit		
				int emailCount = emailLimitdao.getEmailCount(employeeDAO.orgIDByUsername(username));
				
				Map<String, String> actualProperties = propertiesDAO.listTypesAndValues(employeeDAO.orgIDByUsername(username));
				String limitCount = actualProperties.get("EmailLimitPerMonth");
				Integer count =null;
				if(limitCount!=null) 
					count= Integer.parseInt(limitCount);	
				if(count==null) {
					//default config value
					count=Integer.parseInt(loadAvailableProperties().get("EmailLimitPerMonth"));
				}
				
				
				if(count<=emailCount) {
					model.addObject("emailconfigurationlimit","exceeded");
				}
				model.addObject("Message", session.getAttribute("messageSentMessage"));
				model.addObject("count", count);
		session.setAttribute("messageSentMessage", null);
		model.setViewName("Messages");
		return model;
	}

	/**
	 *
	 * Provides implementation for viewing a selected Message.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/viewMessage")
	public ModelAndView viewMessage(ModelAndView model, HttpServletRequest request) {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to view the Message.");
			newModel.addObject("reloadFlag", "reloadPage");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}
		int id = Integer.parseInt(request.getParameter("id"));
		Email email = messageDAO.get(id);
		String formattedBody = Pattern.compile("[^\\p{ASCII}]").matcher(email.getBody()).replaceAll("");
		email.setBody(formattedBody);
		if (email.getFrom().equals("twss.portal@gmail.com") | email.getFrom().equals(Variables.portal_email))
			email.setFrom("Employee Portal");
		model.addObject("email", email);
		try {
			Email emailOld = messageDAO.get(email.getOldMessageId());
			model.addObject("emailOld", emailOld);
		} catch (Exception e) {
		}

		Email Email = new Email();
		model.addObject("Email", Email);
		model.setViewName("ViewInboxMessage");
		return model;
	}

	/**
	 * 
	 * Allows the user to view attachments of a message.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/viewAttach" }, method = RequestMethod.GET)
	public ModelAndView viewAttach(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		int id = Integer.parseInt(request.getParameter("id"));
		int fileId = Integer.parseInt(request.getParameter("fileId"));
		Email email = messageDAO.get(id);
		if (fileId == 1) {
			response.setContentType(email.getContentType1());
			FileCopyUtils.copy(email.getFile1(), response.getOutputStream());
		} else if (fileId == 2) {
			response.setContentType(email.getContentType2());
			FileCopyUtils.copy(email.getFile2(), response.getOutputStream());
		} else if (fileId == 3) {
			response.setContentType(email.getContentType3());
			FileCopyUtils.copy(email.getFile3(), response.getOutputStream());
		}

		return null;
	}

	/**
	 * 
	 * Allows the user to reply to a message. Also allows the user to forward a
	 * message.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	
	@RequestMapping(value = { "/replyEmail", "/forwardEmail" })
	public ModelAndView ForwardEmail(ModelAndView model, HttpServletRequest request) {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to continue.");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}
		String username = request.getRemoteUser();
		int thisID = Integer.parseInt(request.getParameter("thismail"));
		String token = request.getParameter("token");
		Email Email = messageDAO.get(thisID);
		String subject = Email.getSubject();
		String formattedBody = Pattern.compile("[^\\p{ASCII}]").matcher(Email.getBody()).replaceAll("");
		Email.setBody(formattedBody);
		if (token.equals("reply")) {
			String toEmail = Email.getFrom();
			subject = "Re: " + subject;
			Email.setSubject(subject);
			Email.setTo(toEmail);
			Email.setOldMessageId(thisID);
		} else if (token.equals("forward")) {
			subject = "Fw: " + subject;
			Email.setSubject(subject);
			Email.setOldMessageId(thisID);
			Email.setTo("");
		}
		//email limit		
		int emailCount = emailLimitdao.getEmailCount(employeeDAO.orgIDByUsername(username));
		Map<String, String> actualProperties = propertiesDAO.listTypesAndValues(employeeDAO.orgIDByUsername(username));
		String limitCount = actualProperties.get("EmailLimitPerMonth");
		Integer count =null;
		if(limitCount!=null) 
			count= Integer.parseInt(limitCount);	
		if(count==null) {
			//default config value
			count=Integer.parseInt(loadAvailableProperties().get("EmailLimitPerMonth"));
		}
		
		
		if(count<=emailCount) {
			model.addObject("emailconfigurationlimit","exceeded");
		}
		model.addObject("Email", Email);
		model.addObject("count", count);
		model.setViewName("ReplyAndForward");
		return model;
	}

	/**
	 * 
	 * Allows the user to send an Email.
	 * 
	 * @param Email
	 * @param request
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException           ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/SendEmail", "/admin/SendEmail" }, method = RequestMethod.POST)
	public ModelAndView SendEmail(@ModelAttribute Email Email, HttpServletRequest request, HttpSession session)
			throws FileNotFoundException, IOException {
		if (null == request.getRemoteUser())
			return new ModelAndView("redirect:/login?session");
		String username = request.getRemoteUser();
		String orgID = employeeDAO.orgIDByUsername(username);
		String fromModal = request.getParameter("fromModal");
		Employee profile = employeeDAO.profile(request.getRemoteUser());
		String name = profile.getLastname() + ", " + profile.getFirstname();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		String  lastName=((Employee) session.getAttribute("profile")).getLastname(); 
		String  firstName=((Employee) session.getAttribute("profile")).getFirstname(); 
		String formattedBody = Pattern.compile("[^\\p{ASCII}]").matcher(Email.getBody()).replaceAll("");
		Email.setBody(formattedBody);
		Email.setCreatedBy(name);
		Email.setModifiedBy(name);
		Email.setCreatedDate(date);
		Email.setModifiedDate(date);
		Email.setFrom(profile.getEmail());
		Email.setUsername(username);
		Email.setName(name);
		Email.setStatus("sent");
		Email.setOrganizationID(orgID);
		Email.setMailType("UserGen");
		Email.setFirstName(firstName);
		Email.setLastName(lastName);
		Email.setCreatedBy(Email.getCreatedBy());
		Email.setFeature("My Messages");
		try {
			if (!(Email.getAttachment1().getContentType().equals("application/octet-stream"))) {
				Email.setFilename1(Email.getAttachment1().getOriginalFilename());
				Email.setContentType1(Email.getAttachment1().getContentType());
				Email.setFile1(Email.getAttachment1().getBytes());
			}
		} catch (Exception e) {
		}
		try {
			if (!(Email.getAttachment2().getContentType().equals("application/octet-stream"))) {
				Email.setFilename2(Email.getAttachment2().getOriginalFilename());
				Email.setContentType2(Email.getAttachment2().getContentType());
				Email.setFile2(Email.getAttachment2().getBytes());
			}
		} catch (Exception e) {
		}
		try {
			if (!(Email.getAttachment3().getContentType().equals("application/octet-stream"))) {
				Email.setFilename3(Email.getAttachment3().getOriginalFilename());
				Email.setContentType3(Email.getAttachment3().getContentType());
				Email.setFile3(Email.getAttachment3().getBytes());
			}
		} catch (Exception e) {
		}

		String fromName = "";
		String fromEmail = "";

		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		String no = dateFormat.format(Calendar.getInstance().getTime().getTime());
		String rand = UUID.randomUUID().toString();
		String id = no + rand.substring(0, 7);

		Email.setBody(convertImageToDatasourceTemplate(Email.getBody(), id, request, session));
		boolean messageStatus = false;
		try {

			if (null == Email.getAliasID() || Email.getAliasID().equalsIgnoreCase("NONE")
					|| Email.getAliasID().equalsIgnoreCase("Self")) {
				fromName = profile.getLastname() + ", " + profile.getFirstname();
				fromEmail = profile.getEmail();
				if (aws.SendTextEmail(Email, fromName, fromEmail))
					messageStatus = true;
			} else {
				Alias alias = new Alias();
				alias = messageTemplateDAO.getAlias(Integer.parseInt(Email.getAliasID()));
				fromName = alias.getLastname() + ", " + alias.getFirstname();
				fromEmail = alias.getEmail();
				Email.setSignature(alias.getSignature());
				if (aws.SendTextEmailWithSign(Email, fromName, fromEmail))
					messageStatus = true;
			}
		} catch (Exception e) {
			fromName = profile.getLastname() + ", " + profile.getFirstname();
			fromEmail = profile.getEmail();
			if (aws.SendTextEmail(Email, fromName, fromEmail))
				messageStatus = true;
		}
		String returnMessage = "";
		if (messageStatus) {
			messageDAO.saveOrUpdate(Email);
			returnMessage = "Email Sent Successfully.";
		} else {
			returnMessage = "Failed to send Email.";
		}
		ModelAndView model = new ModelAndView();
		if (fromModal.equals("true")) {
			model.addObject("Message", returnMessage);
			model.addObject("reloadFlag", "reloadPage");
			model.setViewName("SuccessScreen");
		} else {
			try {
				String parent = request.getParameter("parent");
				if (!(null == parent))
					model.setViewName("redirect:/" + parent);
				else
					model.setViewName("redirect:" + request.getHeader("Referer"));
			} catch (Exception e) {
				model.setViewName("redirect:" + request.getHeader("Referer"));
			}
			session.setAttribute("messageSentMessage", returnMessage);
		}
		return model;
	}

	private String convertImageToDatasourceTemplate(String Body, String id, HttpServletRequest request,
			HttpSession session) {
		String body = "";
		try {
			String[] splitOnImgSrc = Body.split("<img src=\"data:image/png;base64,");
			String[] splitOnDataFile = splitOnImgSrc[1].split("\" data-filename=");

			String base64 = splitOnDataFile[0];
			try {
				byte[] btDataFile = Base64.decodeBase64(base64.getBytes());
				String username = request.getRemoteUser();

				String organizationID = (employeeDAO.orgIDByUsername(request.getRemoteUser()));
				String filename = "";

				filename = request.getRemoteUser() + "/messages/template_" + id + ".png";
				gcs.uploadDocument(organizationID, filename, btDataFile);

				body += splitOnImgSrc[0];
				body += "<img src=\"https://" + Variables.site_url + "/MessageImages?filename=template_" + id
						+ "&orgID=" + organizationID + "&username=" + username + "\" data-filename=";
				body += splitOnDataFile[1];

				body = body.replaceAll("class=\"table table-bordered\"", " style='border-collapse: collapse;'");
				body = body.replaceAll("<td>", "<td style='border:1px solid black;'>");
				body = body.replaceAll("\"", "\'");
				System.out.println(body);

			} catch (Exception e) {
				logger.error("Convert  Message template  exception occured due to : " + e.getCause());
				body = Body.replaceAll("class=\"table table-bordered\"", " style='border-collapse: collapse;'");
				body = body.replaceAll("<td>", "<td style='border:1px solid black;'>");
				body = body.replaceAll("\"", "\'");
			}
		} catch (Exception e) {
			logger.error("Convert  Message template  exception occured due to : " + e.getCause());
			body = Body.replaceAll("class=\"table table-bordered\"", " style='border-collapse: collapse;'");
			body = body.replaceAll("<td>", "<td style='border:1px solid black;'>");
			body = body.replaceAll("\"", "\'");
		}

		return body;
	}

	@RequestMapping(value = "/MessageImages", method = RequestMethod.GET)
	public ModelAndView MessageImages(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {

		String filename = request.getParameter("filename");
		String orgID = request.getParameter("orgID");
		String username = request.getParameter("username");
		String organizationID = (String) session.getAttribute("organizationID");
		response.setContentType("image/png");
		byte[] download = null;
		try {
			String GCSPath = orgID + "/" + username + "/" + "messages" + "/" + filename;
			download = gcs.downloadAnyDoc(organizationID, GCSPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileCopyUtils.copy(download, response.getOutputStream());

		return null;
	}

	/**********************************************************************************************
	 * 
	 ***************************** Issue & Associated Methods *************************************
	 * 
	 **********************************************************************************************/

	@SuppressWarnings("unchecked")
	/**
	 *
	 * Redirects the user to the Tasks Page.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @throws IOException
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/Issue")
	public ModelAndView Issue(ModelAndView model, HttpServletRequest request, HttpSession session) throws IOException {
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, username);
		Employee employee = employeeDAO.getbyUsername(username);
		List<Task> created = taskDAO.created(username);

		// prop.load(inputStream);
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");

		Map<String, String> category = new LinkedHashMap<String, String>();
		String a1 = typeValueMap.get("Task Category");
		String[] a = new String[1];
		a[0] = " ";
		try {
			a = a1.split(",");
		} catch (Exception e) {
			System.out.println("no task category");
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Task Category");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++) {
			category.put(a[i], a[i]);
		}
		Map<String, String> severity = new LinkedHashMap<String, String>();
		String b1 = typeValueMap.get("Task Severity");
		String[] b = new String[1];
		b[0] = " ";
		try {
			b = b1.split(",");
		} catch (Exception e) {
			System.out.println("no task severity");
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String b2 = defaultMap.get("Task Severity");
			b = b2.split(",");
		}
		// String[] b = prop.getProperty("task_severity").split(",");
		for (int i = 0; i < b.length; i++) {
			severity.put(b[i], b[i]);
		}
		Task task = new Task();
		task.setUsername(username);
		task.setName(employee.getLastname() + ", " + employee.getFirstname());

		List<Task> assigned = taskDAO.assignedList(username);
		List<Task> completed = taskDAO.completedList(username);

		task.setEmail(employee.getEmail());

		model.addObject("Task", task);
		model.addObject("category", category);
		model.addObject("severity", severity);
		model.addObject("assigned", assigned);
		model.addObject("completed", completed);
		model.addObject("created", created);
		model.addObject("createTask", new Issue());
		model.addObject("taskAssign", new TaskAssign());
		try {
			// Expense Module Help Video
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());

		} catch (Exception exp) {
			logger.info("there are no videos");
		}
		model.setViewName("Issue");
		return model;
	}

	/**********************************************************************************************
	 * 
	 ***************************** Tasks & Associated Methods *************************************
	 * 
	 **********************************************************************************************/

	@SuppressWarnings("unchecked")
	/**
	 *
	 * Redirects the user to the Tasks Page.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @throws IOException
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/Tasks")
	public ModelAndView Tasks(ModelAndView model, HttpServletRequest request, HttpSession session) throws IOException {
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		if (null == session.getAttribute("deptLogo"))
			this.createSessionObject(session, username);
		Employee employee = employeeDAO.getbyUsername(username);
		List<Task> created = taskDAO.created(username);

		// prop.load(inputStream);
		
		Task task = new Task();
		task.setUsername(username);
		task.setName(employee.getLastname() + ", " + employee.getFirstname());

		List<Task> assigned = taskDAO.assignedList(username);
		List<Task> completed = taskDAO.completedList(username);
		

		task.setEmail(employee.getEmail());
		System.out.println("testing setTaskProperties...............................");
		setTaskProperties(model, request, session);
		
		
		model.addObject("Task", task);	
		
		model.addObject("assigned", assigned);
		model.addObject("completed", completed);
		model.addObject("created", created);
		model.addObject("createTask", new Task());
		model.addObject("taskAssign", new TaskAssign());
		
		try {
			// Expense Module Help Video
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());

		} catch (Exception exp) {
			logger.info("there are no videos");
		}
		model.setViewName("Issue");
		return model;
	}
	
	
	private void setTaskProperties(ModelAndView model, HttpServletRequest request, HttpSession session) {
		try {
			String username = request.getRemoteUser();
			Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");

			Map<String, String> category = new LinkedHashMap<String, String>();
			String a1 = typeValueMap.get("Task Category");
			String[] a = new String[1];
			a[0] = " ";
			try {
				a = a1.split(",");
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
					List<ProjectResource> userProjects = employeeDAO.listUserAssociatedProjects( username, organizationID);
					
					
					List<Task> parentList = taskDAO.listbyOrganizationId(organizationID);
					model.addObject("category", category);
					model.addObject("severity", severity);
					model.addObject("taskPriority", taskPriority);
					model.addObject("taskEnvironment", taskEnvironment);
					model.addObject("taskRelease", taskRelease);
					model.addObject("issueType", taskissueType);
					model.addObject("fixedInVersion",taskFixedVesrion);
					model.addObject("foundInVersion",taskFoundVesrion);
					model.addObject("userProjects", userProjects);
					
					model.addObject("parentList",parentList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Provides implementation to view the details of a selected Task.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/viewTask")
	public ModelAndView viewTask(ModelAndView model, HttpServletRequest request, HttpSession session) {	
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.addObject("Message", "You are not logged in. Please login to view Task Details.");
			newModel.addObject("reloadFlag", "reloadPage");
			newModel.setViewName("SuccessScreen");
			return newModel;
		}
		Map<String, String> createStatus = new LinkedHashMap<String, String>();
		String[] a = new String[] {"Open","Close"};
		for (int i = 0; i < a.length; i++) {
			createStatus.put(a[i], a[i]);
		}
		Map<String, String> inProgressStatus = new LinkedHashMap<String, String>();
		String[] b = new String[] {"InProgress","Close"};
		for (int i = 0; i < b.length; i++) {
			inProgressStatus.put(b[i], b[i]);
		}
		Map<String, String> completedStatus = new LinkedHashMap<String, String>();
		String[] c = new String[] {"Completed","Close"};
		for (int i = 0; i < c.length; i++) {
			completedStatus.put(c[i],c[i]);
		}


		int id = Integer.parseInt(request.getParameter("id"));
		String checkparam = request.getParameter("type");
		if (checkparam.equals("created")) {
			Task task = taskDAO.get(id);
			model.addObject("taskStatus", "created");
			model.addObject("Task", task);
			model.addObject("status", createStatus);

		}
		if (checkparam.equals("completed")) {
			Task task = taskDAO.get(id);
			model.addObject("taskStatus", "completed");
			model.addObject("Task", task);
			model.addObject("status", completedStatus);

		}
		if (checkparam.equals("assigned")) {
			Task task = taskDAO.getMyTask(id, request.getRemoteUser());
			model.addObject("taskStatus", "assigned");
			model.addObject("Task", task);
			model.addObject("status", inProgressStatus);

		}

		TaskAssign taskAssign = new TaskAssign();
		taskAssign.setUsername(request.getRemoteUser());
		taskAssign.setTaskID(id);
		setTaskProperties(model, request, session);
		model.addObject("adminUser", taskDAO.assignAdmin(taskAssign));
		model.addObject("taskAssign", new TaskAssign());
		Employee profile = employeeDAO.profile(request.getRemoteUser());
		model.addObject("profile", profile);
		String organizationID = profile.getOrganizationID();
		List<Employee> listEmployee = employeeDAO.list(organizationID);
		Map<String, String> empList = new LinkedHashMap<String, String>();
		for (Employee emp : listEmployee) {
			String name = emp.getLastname() + ", " + emp.getFirstname();
			empList.put(emp.getUsername(), name);
		}
		model.addObject("empList", empList);
		List<TaskMessage> messageList = taskDAO.listTaskComm();
		model.addObject("taskCommList", messageList);
		model.addObject("taskMessage", new TaskMessage());
		Email Email = new Email();
		model.addObject("Email", Email);
		model.addObject("createTask", new Task());
		model.setViewName("EditTask");
		

		return model;
	}

	/**
	 * 
	 * Provides implementation to view the details of a selected Task.
	 * 
	 * @param model
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/completeTask")
	public ModelAndView completedTask(ModelAndView model, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		int id = Integer.parseInt(request.getParameter("id"));
		if (id != 0) {
			taskDAO.updateUserTask(id, request.getRemoteUser(), dateTime.getCurrentSQLDate());
			taskDAO.updateUserTaskLogs(id, request.getRemoteUser(), dateTime.getCurrentSQLDate());
		}
		Task task = taskDAO.get(id);
		Employee employee = employeeDAO.getbyUsername(username);
		List<Task> created = taskDAO.created(username);

		// prop.load(inputStream);
		Map<String, String> typeValueMap = (Map<String, String>) session.getAttribute("typeValueMap");

		Map<String, String> category = new LinkedHashMap<String, String>();
		String a1 = typeValueMap.get("Task Category");
		String[] a = new String[1];
		a[0] = " ";
		try {
			a = a1.split(",");
		} catch (Exception e) {
			System.out.println("no task category");
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String a2 = defaultMap.get("Task Category");
			a = a2.split(",");
		}
		for (int i = 0; i < a.length; i++) {
			category.put(a[i], a[i]);
		}
		Map<String, String> severity = new LinkedHashMap<String, String>();
		String b1 = typeValueMap.get("Task Severity");
		String[] b = new String[1];
		b[0] = " ";
		try {
			b = b1.split(",");
		} catch (Exception e) {
			System.out.println("no task severity");
			Map<String, String> defaultMap = (Map<String, String>) session.getAttribute("defaultMap");
			String b2 = defaultMap.get("Task Severity");
			b = b2.split(",");
		}
		// String[] b = prop.getProperty("task_severity").split(",");
		for (int i = 0; i < b.length; i++) {
			severity.put(b[i], b[i]);
		}
		task.setUsername(username);
		task.setName(employee.getLastname() + ", " + employee.getFirstname());

		List<Task> assigned = taskDAO.assignedList(username);
		List<Task> completed = taskDAO.completedList(username);

		task.setEmail(employee.getEmail());

		model.addObject("Task", task);
		model.addObject("category", category);
		model.addObject("severity", severity);
		model.addObject("assigned", assigned);
		model.addObject("completed", completed);
		model.addObject("created", created);
		model.addObject("createTask", new Task());
		model.addObject("taskAssign", new TaskAssign());

		try {
			// Expense Module Help Video
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());

		} catch (Exception exp) {
			logger.info("there are no videos");
		}

		String assignedId = employeeDAO.getEmail(username);

		Email Email = new Email();

		Email.setCreatedBy(username);
		Email.setModifiedBy(username);
		Email.setCreatedDate(date);
		Email.setModifiedDate(date);

		Email.setUsername(username);
		Email.setName(task.getName());
		Email.setFrom(Variables.portal_email);
		Email.setPassword(Variables.portal_password);
		Email.setStatus("Created");

		Employee profile = employeeDAO.profile(username);
		List<String> adminList = employeeDAO.listAdminEmails(profile.getOrganizationID());

		String toEmailList = "";
		for (String emailID : adminList) {
			toEmailList += emailID + ", ";
		}
		toEmailList += task.getEmail() + ", " + assignedId + ", ";
		Email.setCc(toEmailList);
		Email.setTo(profile.getEmail());
		Email.setSubject("The task " + task.getSubject() + " is resolved");
		String msg = "<p>Hello,</p><br>";
		msg += "<p>The task " + task.getSubject() + " is resolved. </p>";
		msg += "<table>" + "<tr><td> Tracking Id: &emsp;</td><td> " + task.getTrackingID() + " &emsp;</td></tr>"
				+ "<tr><td> Name: &emsp;</td><td> " + task.getName() + " &emsp;</td></tr>"
				+ "<tr><td> Email: &emsp;</td><td> " + task.getEmail() + " &emsp;</td></tr>"
				+ "<tr><td> Phone no: &emsp;</td><td> " + task.getPhoneNo() + " &emsp;</td></tr>"
				+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>"
				+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"
				+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr></table>";
		msg += "<p style='font-style: oblique'>Thoughtwave Software and Solutions Inc.</p>";
		Email.setBody(msg);
		String fromEmailID = organizationDAO.get(task.getOrganizationID()).getEmail();
		try {
			fromEmailID = customPropertyDAO.getCutomPropertyDetailsByName("Default-Email", task.getOrganizationID())
					.getPropertyValue().replace(",", "");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		aws.SendTextEmail(Email, employeeDAO.fullname(username), fromEmailID);
		messageDAO.saveOrUpdate(Email);

		model.addObject("Message", "Task Completed Successfully");
		model.addObject("reloadFlag", "reloadPage");
		model.setViewName("SuccessScreen");
		return model;

	}

	/**
	 *
	 * Allows the user to view attachments associated with a Task.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/viewTaskAttachment" }, method = RequestMethod.GET)
	public ModelAndView viewTaskAttachment(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		int id = Integer.parseInt(request.getParameter("id"));
		int fileId = Integer.parseInt(request.getParameter("fileId"));
		Task task = taskDAO.get(id);
		if (fileId == 1) {
			response.setContentType(task.getFileType1());
			FileCopyUtils.copy(task.getFile1(), response.getOutputStream());
		} else if (fileId == 2) {
			response.setContentType(task.getFileType2());
			FileCopyUtils.copy(task.getFile2(), response.getOutputStream());
		} else if (fileId == 3) {
			response.setContentType(task.getFileType3());
			FileCopyUtils.copy(task.getFile3(), response.getOutputStream());
		}

		return null;
	}

	/**
	 * 
	 * Allows the user to create an Task as a task.
	 * 
	 * @param Email
	 * @param request
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException           ModelAndViewarchiveTask
	 * @see ModelAndView
	 */
	@RequestMapping(value = "/SendTask", method = RequestMethod.POST)
	public ModelAndView SendTask(@ModelAttribute Task task, HttpServletRequest request)
			throws FileNotFoundException, IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String username = request.getRemoteUser();
		String fromModal = request.getParameter("fromModal");
		java.sql.Date date = dateTime.getCurrentSQLDate();

		task.setCreatedBy(username);
		task.setModifiedBy(username);
		task.setCreatedDate(date);
		task.setModifiedDate(date);

		task.setUsername(username);
		task.setName(task.getName());
		task.setStatus("Created");
		task.setCc("");
		try {
			if (!(task.getAttachment1().getContentType().equals("application/octet-stream"))) {
				task.setFileName1(task.getAttachment1().getOriginalFilename());
				task.setFileType1(task.getAttachment1().getContentType());
				task.setFile1(task.getAttachment1().getBytes());
			}

			if (!(task.getAttachment2().getContentType().equals("application/octet-stream"))) {
				task.setFileName2(task.getAttachment2().getOriginalFilename());
				task.setFileType2(task.getAttachment2().getContentType());
				task.setFile2(task.getAttachment2().getBytes());
			}

			if (!(task.getAttachment3().getContentType().equals("application/octet-stream"))) {
				task.setFileName3(task.getAttachment3().getOriginalFilename());
				task.setFileType3(task.getAttachment3().getContentType());
				task.setFile3(task.getAttachment3().getBytes());
			}
		} catch (Exception e) {

		}

		Employee profile = employeeDAO.profile(username);
		List<String> adminList = employeeDAO.listAdminEmails(profile.getOrganizationID());
		task.setOrganizationID(profile.getOrganizationID());

		Random rnd = new Random();
		int pin = 10000000 + rnd.nextInt(90000000);
		task.setTrackingID(String.valueOf(pin));
		if (null == task.getDepartmentID())
			task.setDepartmentID("");
		if (null == task.getProjectID())
			task.setProjectID("");

		taskDAO.newTask(task);

		String urlLink = "\"" + Variables.site_url + "/viewDetails?trackingId=" + task.getTrackingID() + "&username="
				+ task.getUsername() + "\"";
		String message = "<!DOCTYPE html><html lang=\"en\">"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width\"><title>Create New Task Email</title></head><body>"
				+ "<table><tr><td><p> Hello " + task.getName() + "</p></td></tr>"
				+ "<tr><td><p>Please find the task details below</p></td></tr>" + "<tr><td><p><table>"
				+ "<tr><td> Raised By: &emsp;</td><td> " + task.getName() + " &emsp;</td></tr>"
				+ "<tr><td> Raised on: &emsp;</td><td> " + task.getCreatedDate() + " &emsp;</td></tr>"
				+ "<tr><td> Tracking ID: &emsp;</td><td> " + task.getTrackingID() + " &emsp;</td></tr>"
				+ "<tr><td> EmailID: &emsp;</td><td> " + task.getEmail() + " &emsp;</td></tr>"
				+ "<tr><td> Category: &emsp;</td><td> " + task.getCategory() + " &emsp;</td></tr>"
				+ "<tr><td> Severity:&emsp;</td><td> " + task.getSeverity() + "&emsp;</td></tr>"
				+ "<tr><td> Description:&emsp;</td><td> " + task.getDescription() + "&emsp;</td></tr>"
				+ "</table></p></td></tr><tr><td><p>Please click here for view user track: <a href=" + urlLink
				+ " target=\"_blank\" >View History</a></p></td></tr>" + "</table>" + "</body></html>";
		Email email = new Email();
		String toEmailList = "";
		for (String emailID : adminList) {
			toEmailList += emailID + ", ";
		}
		toEmailList += task.getEmail();
		email.setTo(toEmailList);
		email.setCc(task.getCc());
		email.setSubject(task.getSubject());
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

		email.setFrom(Variables.portal_email);
		email.setPassword(Variables.portal_password);
		email.setCreatedBy(task.getName());
		email.setModifiedBy(task.getName());
		email.setCreatedDate(date);
		email.setModifiedDate(date);

		email.setUsername(username);
		email.setName(task.getName());
		String fromEmailID = organizationDAO.get(task.getOrganizationID()).getEmail();
		try {
			fromEmailID = customPropertyDAO.getCutomPropertyDetailsByName("Default-Email", task.getOrganizationID())
					.getPropertyValue().replace(",", "");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		aws.SendTextEmail(email, employeeDAO.fullname(username), fromEmailID);

		messageDAO.saveOrUpdate(email);

		ModelAndView model = new ModelAndView();
		if (fromModal.equals("true")) {
			model.addObject("Message", "Email Sent Successfully");
			model.setViewName("SuccessScreen");
		} else
			model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}

	/**
	 * 
	 * Allows the user to Update Due Date and Time to the task assigned to him.
	 * 
	 * @param taskAssign
	 * @param request
	 * @return ModelAndView
	 * @see ModelAndView
	 */
	@RequestMapping(value = { "/emp/UpdateTaskTime", "/admin/updateDueDate" }, method = RequestMethod.POST)
	public ModelAndView UpdateTaskTime(@ModelAttribute TaskAssign taskAssign, HttpServletRequest request) {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String username = request.getRemoteUser();
		java.sql.Date date = dateTime.getCurrentSQLDate();
		String tab = request.getParameter("tab");
		String page = request.getParameter("page");
		taskAssign.setModifiedBy(username);
		taskAssign.setModifiedDate(date);

		taskDAO.updateTaskTime(taskAssign);
		ModelAndView model = new ModelAndView();
		if (request.getServletPath().equals("/emp/UpdateTaskTime"))
			model.setViewName("redirect:/viewTask?type=assigned&id=" + taskAssign.getTaskID());
		else if (request.getServletPath().equals("/admin/updateDueDate"))
			model.setViewName("redirect:/PendingTasks?tab=" + tab + "&page=" + page);
		return model;
	}

	@RequestMapping(value = "/taskComm", method = RequestMethod.POST)
	public ModelAndView sendTaskMessage(@ModelAttribute TaskMessage message, HttpServletRequest request) {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		java.sql.Date date = dateTime.getCurrentSQLDate();
		ModelAndView model = new ModelAndView();
		message.setCreatedDate(date);
		taskDAO.saveTaskMessage(message);
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;

	}

	/**********************************************************************************************
	 * 
	 ***************************** Resources & Associated Methods *********************************
	 * 
	 **********************************************************************************************/

	/**********************
	 * Organization Search
	 **********************/

	@RequestMapping(value = "/admin/searchOrganization", method = RequestMethod.POST)
	public ModelAndView searchOrganization(ModelAndView model, HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<Organization> listOrganization = organizationDAO.search(keyword);
		model.addObject("listOrganization", listOrganization);
		model.setViewName("Organization");
		return model;
	}

	/**********************
	 * SwarmVendor Search
	 **********************/

	@RequestMapping(value = "/admin/searchVendor", method = RequestMethod.POST)
	public ModelAndView searchVendor(ModelAndView model, HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<SwarmVendor> listVendor = vendorDAO.search(keyword);
		model.addObject("listVendor", listVendor);
		model.setViewName("SwarmVendor");
		return model;
	}

	/**********************
	 * Client Search
	 **********************/

	@RequestMapping(value = "/admin/searchClient", method = RequestMethod.POST)
	public ModelAndView searchClient(ModelAndView model, HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<Client> listClient = clientDAO.search(keyword);
		model.addObject("listClient", listClient);
		model.setViewName("Client");
		return model;
	}

	/**********************
	 * Project Search
	 **********************/

	@RequestMapping(value = "/admin/searchProject", method = RequestMethod.POST)
	public ModelAndView searchProject(ModelAndView model, HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<Project> listProject = projectDAO.search(keyword);
		model.addObject("listProject", listProject);
		model.setViewName("Project");
		return model;
	}

	/**********************
	 * Employee Search
	 **********************/

	@RequestMapping(value = "/admin/searchEmployee", method = RequestMethod.POST)
	public ModelAndView searchEmployee(ModelAndView model, HttpServletRequest request) {
		String keyword = request.getParameter("keyword");
		List<Employee> listEmployee = employeeDAO.search(keyword);
		model.addObject("listEmployee", listEmployee);
		model.setViewName("Employee");
		return model;
	}

	@RequestMapping(value = { "/viewInsuranceCard" }, method = RequestMethod.GET)
	public ModelAndView viewInsuranceCard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (null == request.getRemoteUser()) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		int id = Integer.parseInt(request.getParameter("id"));
		try {
			Insurance insurance = insuranceDAO.get(id);
			response.setContentType(insurance.getInsuranceFileType());
			response.setContentLength(insurance.getInsuranceFile().length);
			FileCopyUtils.copy(insurance.getInsuranceFile(), response.getOutputStream());
		} catch (Exception e) {
			response.setContentType(null);
			Files file = new Files();
			FileCopyUtils.copy(file.getFile(), response.getOutputStream());
		}
		return null;
	}

	@RequestMapping(value = "/DesktopMonitoring")
	public ModelAndView DesktopMonitoring1(ModelAndView model, HttpServletRequest request, HttpSession session) {
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;

		Employee profile = (Employee) session.getAttribute("profile");
		List<Employee> empList = employeeDAO.listForDesktopMonitoring(profile.getOrganizationID());

		List<Employee> empListWithKey = desktopMonitoringDAO.listEmployeesWithKey(profile.getOrganizationID());
		List<Screenshot> lastAccessList = desktopMonitoringDAO.listLastAccessed(profile.getOrganizationID());
		model.addObject("lastAccessList", lastAccessList);
		model.addObject("empListWithKey", empListWithKey);
		model.addObject("empList", empList);
		model.addObject("desktopClient", new DesktopClient());
		model.setViewName("DesktopMonitoring");
		return model;
	}

	@RequestMapping(value = { "/idleRep" })
	public ModelAndView idle1(ModelAndView model, HttpServletRequest request, HttpSession session,
			final RedirectAttributes redirectAttributes) throws IOException, ParseException {
		String username = request.getRemoteUser();
		String organizationID = (String) session.getAttribute("organizationID");
		String startDate = request.getParameter("startDt");
		String employee = request.getParameter("usrname");
		System.out.println("Idle Time Rep for " + employee + " on " + startDate);

		java.sql.Date date = dateTime.getCurrentSQLDate();
		String name = employeeDAO.userDetails(username);

		String[] sDate = startDate.split("-");

		String sD = sDate[2] + "/" + sDate[1] + "/" + sDate[0];

		DateFormat dateFormat1 = new SimpleDateFormat("yyMMddHHmm");
		String start = dateFormat1.format(date.getTime());

		String uniqueID = employee + "Rep" + start;

		IdleTimeRep idleTimeHead = desktopReportsDAO.getIdleTimeReport(employee, sD);
		List<IdleTimeRep> idleTimeList = new ArrayList<IdleTimeRep>();
		System.out.println(idleTimeHead.toString());

		int noOfScreen = 0;

		if (!(idleTimeHead.getUniqueID().equals("no report"))) {
			idleTimeList = desktopReportsDAO.getIdleTimeReportList(idleTimeHead.getUniqueID());
			if (idleTimeHead.getStatus().equals("Partial")) {
				List<IdleTimeRep> idleTimeListNew = new ArrayList<IdleTimeRep>();
				List<Screenshot> screenshots = desktopReportsDAO.screenShotForIdleTime(employee, sD);
				idleTimeListNew = desktopReportsDAO.CalcIdleTime(screenshots);
				idleTimeList.addAll(idleTimeListNew);

				List<Integer> id = new ArrayList<Integer>();

				String oldTOtal = idleTimeHead.getTotalIdleTime();
				int hrs = Integer.parseInt(oldTOtal.split("hrs ")[0].trim());
				int min = Integer.parseInt(oldTOtal.split("hrs ")[1].split("mins")[0].trim());

				if (idleTimeListNew.size() > 0) {
					int hrsNew = Integer.parseInt(idleTimeListNew.get(0).getTotalIdleTime().split("hrs ")[0].trim());
					int minNew = Integer.parseInt(
							idleTimeListNew.get(0).getTotalIdleTime().split("hrs ")[1].split("mins")[0].trim());

					int totalHr = hrs + hrsNew;
					int totalMin = min + minNew;
					totalMin = totalMin % 60;
					if (totalMin / 60 > 1)
						totalHr = totalHr + (totalMin / 60);
					oldTOtal = totalHr + " hrs " + totalMin + " mins";
				}
				for (IdleTimeRep i : idleTimeListNew) {
					i.setUniqueID(uniqueID);
					i.setCreatedBy(name);
					i.setCreatedDate(date);
					i.setTotalIdleTime(oldTOtal);

				}

				for (Screenshot i : screenshots) {
					id.add(i.getId());
				}

				if (desktopReportsDAO.saveIdleTimeReportTransaction(idleTimeListNew)) {
					idleTimeHead.setStatus("Partial");
					idleTimeHead.setTotalIdleTime(oldTOtal);
					desktopReportsDAO.updateIdleTimeReportHead(idleTimeHead);
					desktopReportsDAO.updateIdleStatus(id, name, date);
				}
			}

			for (IdleTimeRep i : idleTimeList) {

				byte[] downloadStartThumb = null;
				byte[] downloadEndThumb = null;
				try {
					downloadStartThumb = gcs.downloadAnyDoc(organizationID, i.getStartThumbPath());
					downloadEndThumb = gcs.downloadAnyDoc(organizationID, i.getEndThumbPath());
				} catch (Exception e) {
					e.printStackTrace();
				}

				i.setStartEncodedImage(new String(Base64.encodeBase64(downloadStartThumb)));
				i.setEndEncodedImage(new String(Base64.encodeBase64(downloadEndThumb)));
			}
		} else {
			List<Screenshot> screenshots = desktopReportsDAO.screenShotForIdleTime(employee, sD);
			idleTimeList = desktopReportsDAO.CalcIdleTime(screenshots);
			noOfScreen = screenshots.size();

			System.out.println("No of Screen------------" + noOfScreen);
			System.out.println("No of idle------------" + idleTimeList.size());
			if (idleTimeList.size() > 0) {
				idleTimeHead.setUniqueID(uniqueID);
				idleTimeHead.setDate(idleTimeList.get(0).getDate());
				idleTimeHead.setUsername(idleTimeList.get(0).getUsername());
				idleTimeHead.setTotalIdleTime(idleTimeList.get(0).getTotalIdleTime());
				idleTimeHead.setStart(idleTimeList.get(0).getStart());
				idleTimeHead.setEnd(idleTimeList.get(idleTimeList.size() - 1).getEnd());
				idleTimeHead.setStartTime(idleTimeList.get(0).getStartTime());
				idleTimeHead.setEndTime(idleTimeList.get(idleTimeList.size() - 1).getEndTime());
				idleTimeHead.setCreatedBy(name);
				idleTimeHead.setCreatedDate(date);

				List<Integer> id = new ArrayList<Integer>();
				for (IdleTimeRep i : idleTimeList) {
					i.setUniqueID(uniqueID);
					i.setCreatedBy(name);
					i.setCreatedDate(date);
					id.add(i.getId());

					byte[] downloadStartThumb = null;
					byte[] downloadEndThumb = null;
					try {
						downloadStartThumb = gcs.downloadAnyDoc(organizationID, i.getStartThumbPath());
						downloadEndThumb = gcs.downloadAnyDoc(organizationID, i.getEndThumbPath());
					} catch (Exception e) {
						e.printStackTrace();
					}

					i.setStartEncodedImage(new String(Base64.encodeBase64(downloadStartThumb)));
					i.setEndEncodedImage(new String(Base64.encodeBase64(downloadEndThumb)));
				}

				if (desktopReportsDAO.saveIdleTimeReportTransaction(idleTimeList)) {
					SimpleDateFormat dateformatJava = new SimpleDateFormat("dd/MM/yyyy");
					String today = dateformatJava.format(date);

					if (idleTimeHead.getDate().equals(today)) {
						System.out.println("Inside today");
						idleTimeHead.setStatus("Partial");
					} else
						idleTimeHead.setStatus("Completed");
					desktopReportsDAO.saveIdleTimeReportHead(idleTimeHead);
					desktopReportsDAO.updateIdleStatus(id, name, date);
				}

			} else {
				idleTimeHead.setStart("No Report for the selected Day");
			}

			if (idleTimeList.size() == 0)
				if (noOfScreen == 0)
					idleTimeHead.setTotalIdleTime("No Data Available  for the Selected Day");
				else
					idleTimeHead.setTotalIdleTime("No Idle Time  for the Selected Day");

		}
		System.out.println("Before Model");
		System.out.println(idleTimeHead.toString());
		System.out.println(idleTimeList.size());
		model.addObject("idleTimeHead", idleTimeHead);
		model.addObject("idleTimeList", idleTimeList);
		model.addObject("sDate", sD);
		model.setViewName("IdleTimeReport");

		return model;
	}

	@RequestMapping(value = { "/ProductivityRep" })
	public ModelAndView ProductivityRep(ModelAndView model, HttpServletRequest request, HttpSession session,
			final RedirectAttributes redirectAttributes) throws IOException, ParseException {

		String username = request.getRemoteUser();
		String organizationID = (String) session.getAttribute("organizationID");
		String startDate = request.getParameter("startDt");
		String employee = request.getParameter("usrname");
		System.out.println("Prod Time Rep for " + employee + " on " + startDate);

		java.sql.Date date = dateTime.getCurrentSQLDate();
		String name = employeeDAO.userDetails(username);

		String[] sDate = startDate.split("-");

		String sD = sDate[2] + "/" + sDate[1] + "/" + sDate[0];

		DateFormat dateFormat1 = new SimpleDateFormat("yyMMddHHmm");
		String start = dateFormat1.format(date.getTime());

		String uniqueID = employee + "Rep" + start;

		ProductivityRep prodRepHead = desktopReportsDAO.getProductivityReport(employee, sD);
		System.out.println(prodRepHead.toString());
		List<ProductivityRep> prodTimeList = new ArrayList<ProductivityRep>();

		int noOfScreen = 0;

		if (!(prodRepHead.getUniqueID().equals("no report"))) {
			prodTimeList = desktopReportsDAO.getProductivityReportList(prodRepHead.getUniqueID());

			if (prodRepHead.getStatus().equals("Partial")) {
				List<ProductivityRep> prodTimeListNew = new ArrayList<ProductivityRep>();
				List<Screenshot> screenshots = desktopReportsDAO.screenShotForProdTime(employee, sD);

				if (screenshots.size() > 0) {
					String url = request.getRequestURL().toString();
					String uri = request.getRequestURI();

					String dateNew = sD.replace("/", "-");
					;

					String videoGCS = "desktop-monitoring-images/" + organizationID + "/" + employee + "/" + dateNew
							+ "/video/";

					prodTimeListNew = desktopReportsDAO.CalcProdTime(screenshots, url, uri, videoGCS);
					for (ProductivityRep s : prodTimeListNew) {
						String[] st = s.getStart().split(":");
						if (Integer.parseInt(st[0]) >= 12)
							s.setStart(s.getStart() + " PM(" + screenshots.get(0).getTimezone() + ")");
						else
							s.setStart(s.getStart() + " AM(" + screenshots.get(0).getTimezone() + ")");
						String[] et = s.getEnd().split(":");
						if (Integer.parseInt(et[0]) >= 12)
							s.setEnd(s.getEnd() + " PM(" + screenshots.get(0).getTimezone() + ")");
						else
							s.setEnd(s.getEnd() + " AM(" + screenshots.get(0).getTimezone() + ")");

					}

					prodTimeList.addAll(prodTimeListNew);

					List<Integer> id = new ArrayList<Integer>();

					String oldTOtal = prodRepHead.getTotalProTime();
					int hrs = Integer.parseInt(oldTOtal.split("hrs ")[0].trim());
					int min = Integer.parseInt(oldTOtal.split("hrs ")[1].split("mins")[0].trim());

					if (prodTimeListNew.size() > 0) {
						int hrsNew = Integer.parseInt(prodTimeListNew.get(0).getTotalProTime().split("hrs ")[0].trim());
						int minNew = Integer.parseInt(
								prodTimeListNew.get(0).getTotalProTime().split("hrs ")[1].split("mins")[0].trim());

						int totalHr = hrs + hrsNew;
						int totalMin = min + minNew;
						totalMin = totalMin % 60;
						if (totalMin / 60 > 1)
							totalHr = totalHr + (totalMin / 60);
						oldTOtal = totalHr + " hrs " + totalMin + " mins";
					}
					for (ProductivityRep i : prodTimeListNew) {
						i.setUniqueID(uniqueID);
						i.setCreatedBy(name);
						i.setCreatedDate(date);
						i.setTotalProTime(oldTOtal);

					}

					for (Screenshot i : screenshots) {
						id.add(i.getId());
					}

					if (desktopReportsDAO.saveProdTimeReportTransaction(prodTimeListNew)) {
						prodRepHead.setStatus("Partial");
						prodRepHead.setTotalProTime(oldTOtal);
						if (desktopReportsDAO.updateProTimeReportHead(prodRepHead))
							desktopReportsDAO.updateProdStatus(id, name, date);
					}
				}
			}

			for (ProductivityRep i : prodTimeList) {

				byte[] downloadImage = null;
				try {
					downloadImage = gcs.downloadAnyDoc(organizationID, i.getImagePath());
				} catch (Exception e) {
					e.printStackTrace();
				}

				i.setImage(new String(Base64.encodeBase64(downloadImage)));
			}
		} else {
			List<Screenshot> screenshots = desktopReportsDAO.screenShotForProdTime(employee, sD);
			if (screenshots.size() > 0) {
				String url = request.getRequestURL().toString();
				String uri = request.getRequestURI();

				String dateNew = sD.replace("/", "-");
				;

				String videoGCS = "desktop-monitoring-images/" + organizationID + "/" + employee + "/" + dateNew
						+ "/video/";

				prodTimeList = desktopReportsDAO.CalcProdTime(screenshots, url, uri, videoGCS);
				for (ProductivityRep s : prodTimeList) {
					String[] st = s.getStart().split(":");
					if (Integer.parseInt(st[0]) >= 12)
						s.setStart(s.getStart() + " PM(" + screenshots.get(0).getTimezone() + ")");
					else
						s.setStart(s.getStart() + " AM(" + screenshots.get(0).getTimezone() + ")");
					String[] et = s.getEnd().split(":");
					if (Integer.parseInt(et[0]) >= 12)
						s.setEnd(s.getEnd() + " PM(" + screenshots.get(0).getTimezone() + ")");
					else
						s.setEnd(s.getEnd() + " AM(" + screenshots.get(0).getTimezone() + ")");

				}

				noOfScreen = screenshots.size();

				System.out.println("No of Screen------------" + noOfScreen);
				System.out.println("No of prod------------" + prodTimeList.size());
				if (prodTimeList.size() > 0) {
					prodRepHead.setUniqueID(uniqueID);
					prodRepHead.setDate(sD);
					prodRepHead.setUsername(employee);
					prodRepHead.setTotalProTime(prodTimeList.get(0).getTotalProTime());
					prodRepHead.setStart(prodTimeList.get(0).getStart());
					prodRepHead.setEnd(prodTimeList.get(prodTimeList.size() - 1).getEnd());
					prodRepHead.setStartTime(prodTimeList.get(0).getStartTime());
					prodRepHead.setEndTime(prodTimeList.get(prodTimeList.size() - 1).getEndTime());
					prodRepHead.setCreatedBy(name);
					prodRepHead.setCreatedDate(date);

					List<Integer> id = new ArrayList<Integer>();
					for (Screenshot s : screenshots)
						id.add(s.getId());

					for (ProductivityRep i : prodTimeList) {
						i.setUniqueID(uniqueID);
						i.setCreatedBy(name);
						i.setCreatedDate(date);

						byte[] downloadImage = null;
						try {
							downloadImage = gcs.downloadAnyDoc(organizationID, i.getImagePath());
							;
						} catch (Exception e) {
							e.printStackTrace();
						}

						i.setImage(new String(Base64.encodeBase64(downloadImage)));
					}

					if (desktopReportsDAO.saveProdTimeReportTransaction(prodTimeList)) {
						SimpleDateFormat dateformatJava = new SimpleDateFormat("dd/MM/yyyy");
						String today = dateformatJava.format(date);

						if (prodRepHead.getDate().equals(today)) {
							System.out.println("Inside today");
							prodRepHead.setStatus("Partial");
						} else
							prodRepHead.setStatus("Completed");
						if (desktopReportsDAO.saveProdTimeReportHead(prodRepHead))
							desktopReportsDAO.updateProdStatus(id, name, date);
					}

				} else {
					prodRepHead.setStart("No Report for the selected Day");
				}

				if (prodTimeList.size() == 0)
					if (noOfScreen == 0)
						prodRepHead.setTotalProTime("No Data Available  for the Selected Day");
					else
						prodRepHead.setTotalProTime("No Producitve Time  for the Selected Day");
			}
		}
		model.addObject("prodRepHead", prodRepHead);
		model.addObject("prodTimeList", prodTimeList);
		model.addObject("sDate", sD);
		model.setViewName("ProductivityRep");

		return model;
	}

	@RequestMapping(value = { "/DesktopWeekly" })
	public ModelAndView DesktopWeekly(ModelAndView model, HttpServletRequest request, HttpSession session,
			final RedirectAttributes redirectAttributes) throws IOException, ParseException {

		String username = request.getParameter("usrname");
		String startDate = request.getParameter("startDt");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date sd = sdf1.parse(startDate);
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		String sdt = sdf2.format(sd);
		// String username = "sush_tws";
		// String startDate = "07/08/2017";

		DesktopMonitorWeek desktopWeek = desktopReportsDAO.getWeeklyreport(sdt, username);

		desktopWeek.setName(employeeDAO.userDetails(username));

		model.addObject("desktop", desktopWeek);

		model.setViewName("DesktopMonitorWeek");

		return model;
	}

	@RequestMapping(value = { "/imageView" })
	public ModelAndView imageView(ModelAndView model, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) throws IOException {
		String organizationID = (String) session.getAttribute("organizationID");
		String path = request.getParameter("path");
		response.setContentType("image/png");
		byte[] download = null;
		try {
			download = gcs.downloadAnyDoc(organizationID, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileCopyUtils.copy(download, response.getOutputStream());

		return null;
	}

	@RequestMapping(value = "admin/updateDesktopClientKey")
	public ModelAndView generateClientKey(@ModelAttribute DesktopClient desktopClient, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String auth = request.getParameter("auth");
		String admin = request.getRemoteUser();
		if ((null == admin) || (!admin.equals(auth)))
			return new ModelAndView("redirect:/403");

		String productID = "";
		for (int i = 0; i < 4; i++)
			productID += RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-";
		productID = productID.substring(0, productID.length() - 1);
		desktopClient.setProductID(productID);
		desktopMonitoringDAO.setProductID(desktopClient);
		model.setViewName("redirect:/DesktopMonitoring");
		return model;
	}

	@RequestMapping(value = "admin/updateDesktopClientTimings")
	public ModelAndView updateClientKey(@ModelAttribute DesktopClient desktopClient, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String auth = request.getParameter("auth");
		String admin = request.getRemoteUser();
		if ((null == admin) || (!admin.equals(auth)))
			return new ModelAndView("redirect:/403");

		desktopMonitoringDAO.updateTimes(desktopClient);
		model.setViewName("redirect:/DesktopMonitoring");
		return model;
	}

	@RequestMapping(value = "/playVideo")
	public ModelAndView playVideo(ModelAndView model, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) throws IOException {

		String organizationID = (String) session.getAttribute("organizationID");
		String uniqueID = request.getParameter("uniqueID");
		String startTime = request.getParameter("startTime");
		System.out.println(startTime);
		System.out.println(uniqueID);
		new ProductivityRep();
		ProductivityRep f = desktopReportsDAO.getProductivityReportVideo(uniqueID, startTime);

		byte[] downloadImage = null;
		System.out.println("path------" + f.getVideoPath());
		try {
			downloadImage = gcs.downloadAnyDoc(organizationID, f.getVideoPath());
			response.setContentType("video/mp4");
			response.setContentLength(downloadImage.length);
			FileCopyUtils.copy(downloadImage, response.getOutputStream());
			return null;
		} catch (Exception e) {
			System.out.println("Inside Catch");
			e.printStackTrace();
			model.setViewName("SuccessScreen");
			model.addObject("Message", "No Video FOund");
			return model;
		}
	}

	/*
	 * @SuppressWarnings("unused") private Screenshot
	 * createGifFromImages(List<Screenshot> imagesList) throws Exception {
	 * Screenshot finalGif = new Screenshot(); AnimatedGIFWriter writer = new
	 * AnimatedGIFWriter(true); OutputStream os = new ByteArrayOutputStream();
	 * writer.prepareForWrite(os, -1, -1); for (Screenshot image : imagesList) {
	 * InputStream in = new ByteArrayInputStream(image.getFile()); BufferedImage
	 * bImage = ImageIO.read(in); writer.writeFrame(os, bImage, 600); }
	 * writer.finishWrite(os); os.close(); ByteArrayOutputStream bos =
	 * (ByteArrayOutputStream) os; finalGif.setEncodedImage(new
	 * String(Base64.encodeBase64(bos.toByteArray()))); File imageFile = new
	 * File("videoOut.mp4"); FileOutputStream fos = new
	 * FileOutputStream(imageFile.getAbsolutePath()); fos.write(bos.toByteArray());
	 * fos.close(); return finalGif; }
	 * 
	 * @SuppressWarnings("unused") private ProductivityRep
	 * createVideoFromImages(List<Screenshot> imageList) throws Exception {
	 * ProductivityRep finalGif = new ProductivityRep(); List<byte[]> imageByteList
	 * = new ArrayList<byte[]>(); for (Screenshot image : imageList) {
	 * imageByteList.add(image.getFile()); } finalGif.setVideo(new
	 * String(Base64.encodeBase64(CreateVideoFromImages.createVideo(imageByteList),
	 * true))); return finalGif; }
	 * 
	 * @SuppressWarnings("unused") private Screenshot createVideo(List<Screenshot>
	 * imageList) throws Exception { List<byte[]> imageByteList = new
	 * ArrayList<byte[]>(); for (Screenshot image : imageList) {
	 * imageByteList.add(image.getFile()); } Screenshot screen = new Screenshot();
	 * screen.setFile(CreateVideoFromImages.createVideo(imageByteList));
	 * screen.setEncodedImage(new
	 * String(Base64.encodeBase64(CreateVideoFromImages.createVideo(imageByteList),
	 * true))); return screen; }
	 */

	/*
	 * @SuppressWarnings("unused")
	 * 
	 * @RequestMapping(value = { "/imageView" }) public ModelAndView
	 * imageView(ModelAndView model, HttpServletResponse response,
	 * HttpServletRequest request) throws IOException {
	 * 
	 * int id = Integer.parseInt(request.getParameter("id")); Screenshot image = new
	 * Screenshot(); Screenshot f = screenshotDAO.getFile(id);
	 * 
	 * f.setEncodedImage(new String(Base64.encodeBase64(f.getFile()))); image = f;
	 * 
	 * response.setContentType("image/jpg");
	 * response.setContentLength(f.getFile().length);
	 * FileCopyUtils.copy(f.getFile(), response.getOutputStream()); return null; }
	 */
	@RequestMapping(value = "/MyAssets")
	public ModelAndView assetManagement(ModelAndView model, HttpServletRequest request, HttpSession session)
			throws IOException {
		String username = request.getRemoteUser();
		model = this.loadProfileRelatedInfo(request.getServletPath(), username, model, session);
		if (model.getViewName().equals("redirect:/403") | model.getViewName().equals("redirect:/login?session"))
			return model;
		if(session.getAttribute("AssetsubmitMessagecount") ==null || session.getAttribute("AssetsubmitMessagecount").equals("0")) {
			session.setAttribute("SubmitAsset", null);
		}
		String username1=request.getParameter("username");
		logger.info("username-------"+username1);
		session.setAttribute("AssetsubmitMessagecount", "0");
         String organizationID = employeeDAO.orgIDByUsername(username);
		//List<Employee> listEmployee = employeeDAO.list(organizationID);
		List<Asset> assetemplist=assetDAO.userassets(username);
		logger.info("here the user asset"+assetemplist.toString());

		try {
			String[] path = request.getServletPath().split("/");
			HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
			model.addObject("helpVideo", helpVideo.getVideoLink());
		} catch (Exception exp) {
			logger.info("No Help video found for Asset Management Module");
		}
		model.addObject("assetemplist", assetemplist);
	    Map<String, String> deptList = departmentDAO.listDepartmentNames(organizationID);
		model.addObject("deptList", deptList);
		model.addObject("newAsset", new Asset());
        model.setViewName("Myassets");
		return model;
	}
	
	
	

	//sp-1335
	@RequestMapping(value = { "/submitasset" }, method = RequestMethod.POST)
	public ModelAndView submitasset(@ModelAttribute Asset asset,HttpServletRequest request,HttpSession session) {
		String username = request.getRemoteUser();
		if (null == username) {
			ModelAndView newModel = new ModelAndView();
			newModel.setViewName("redirect:/login?session");
			return newModel;
		}
		String organizationID = (String) session.getAttribute("organizationID");
		String orgmail=organizationDAO.get(organizationID).getEmail();
		logger.info("here the ordmail------>>"+orgmail);

		Asset assetcreatedby=assetDAO.getAsset(asset.getAssetID());
		//String Assetname=assetcreatedby.getAssetName();
		//System.out.println("Asset createdby"+assetcreatedby.toString());
		//String createdname=assetcreatedby.getCreatedBy();
		String ManName = employeeDAO.fullname(assetcreatedby.getCreatedBy());

		//System.out.println("hiiiehete created by--->>"+createdname);
		
		ModelAndView model = new ModelAndView();
		String timefordate = dateTime.getCurrentTime();
		logger.info("time for date"+timefordate.toString());
		String time1=dateTime.getCurrentDate();
		Date time = dateTime.convertStringToSQLDate(time1);
		logger.info("here the time of date is"+time);
		String name = employeeDAO.fullname(username);
		String assetID = asset.getAssetID();
		logger.info("noyte&&&&&&&&&&&&"+assetID);
		String note=asset.getNote();
		logger.info("hre the note is"+note);
		String orgID = employeeDAO.orgIDByUsername(username);
		java.sql.Date date1 = dateTime.getCurrentSQLDate();
		Employee profile = employeeDAO.profile(username);
		String name1 = profile.getLastname() + ", " + profile.getFirstname();

		Email email = new Email();
		email.setCreatedBy(name1);
		email.setCreatedDate(date1);
		email.setUsername(username);
		email.setName(name1);
		email.setStatus("sent");
		email.setTo(employeeDAO.getEmail(assetcreatedby.getCreatedBy()));
		String subject="Asset Submitted By "+name;
		email.setSubject(subject);
		email.setFrom(profile.getEmail());
		email.setOrganizationID(orgID);
		String body = "<!DOCTYPE html><html lang=\"en\">"
				+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta charset=\"utf-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width\"></head><body>" + "<table><tr><td><p> Dear "
				+ ManName + " </p></td></tr>" + "<tr><td><p>I have Submitted the Asset with AssetID  "+"<b>"
				+ assetID+"</b></p></td></tr></table></body></html>";
		email.setBody(body);
	    String msg = "";
		String fromName = "";
		String fromEmail = "";
		
		if (assetDAO.submitasset(assetID,orgID, name, timefordate,note)) 
			msg = "Successfully Submitted the Asset";
		    
		else 
			msg = "Failed to  Submit the Asset";
	     
		session.setAttribute("SubmitAsset", msg);
		session.setAttribute("AssetsubmitMessagecount", "1");
		model.addObject("newAsset", new Asset());
		fromName = profile.getLastname() + ", " + profile.getFirstname();
		
		fromEmail = profile.getEmail();
		
		aws.SendTextEmail(email, fromName, fromEmail);
		//aws.SendTextEmail(Email, Email.getName(), Variables.fromEmailID);
		model.setViewName("redirect:" + request.getHeader("Referer"));
		return model;
	}
	
	
	//SP-1335
	
		@RequestMapping(value = "/EmployeeDirectoryMyAssets")
		public ModelAndView assetManagementEmployee(ModelAndView model, HttpServletRequest request, HttpSession session)
				throws IOException {
			String username = request.getRemoteUser();
			if (null == username) {
				ModelAndView newModel = new ModelAndView();
				newModel.setViewName("redirect:/login?session");
				return newModel;
			}
			
			String username1=request.getParameter("username");
			//System.out.println("username-------"+username1);
			session.setAttribute("AssetsubmitMessagecount", "0");
	         String organizationID = employeeDAO.orgIDByUsername(username1);
			//List<Employee> listEmployee = employeeDAO.list(organizationID);
			List<Asset> assetemplist=assetDAO.userassets(username1);
			logger.info("here the user asset"+assetemplist.toString());

			try {
				String[] path = request.getServletPath().split("/");
				HelpVideos helpVideo = helpVideoDAO.getHelpVideos(path[path.length - 1]);
				model.addObject("helpVideo", helpVideo.getVideoLink());
			} catch (Exception exp) {
				logger.info("No Help video found for Asset Management Module");
			}
			model.addObject("assetemplist", assetemplist);
		    Map<String, String> deptList = departmentDAO.listDepartmentNames(organizationID);
			model.addObject("deptList", deptList);
			model.addObject("newAsset", new Asset());
	        model.setViewName("EmployeedirectoryMyassets");
			return model;
		}

}