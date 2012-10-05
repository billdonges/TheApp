package the.app.admin;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import the.app.AppInfo;
import the.app.AppValidation;

public class DbManagementGateway extends HttpServlet implements AppInfo
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException 
	{       
		service(req,res);
	}

	/**
	 * 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException 
	{       
		service(req,res);
	}	
	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{
		System.out.println("DBManagementGateway.service()");

		DBMgmt dbm = new DBMgmt();
		HttpSession session = req.getSession();
		if (session.getAttribute("DBMgmt") != null)
			dbm = (DBMgmt)session.getAttribute("DBMgmt");
		
		String url = "/db_management.jsp";
		
		try
		{
			/*
			 * possible actions
			 * 
			 * dbs = get dbs
			 * col = get collections in db
			 * dta = get data in specific collection
			 * dbstats = get db statistics
			 */
			String action = req.getParameter("a");
			action = AppValidation.checkNull(action);
			
			/*
			 * special cases.
			 * 
			 * connect = new when someone resubmits the the connect form.
			 */
			String connect = req.getParameter("connect");
			connect = AppValidation.checkNull(connect);
			
			System.out.println("action: " + action + ", connect: " + connect);
			
			/*
			 *  validate that a custom search request wasn't made.  possible input fields are:
			 *  
			 *  1.  dblocation (ip or name)
			 *  2.  dbport	
			 *  3.  dbname
			 *  4.  username
			 *  5.  password
			 *  6.  validate (boolean)
			 */
			if (connect.equals("new")) 
			{
				System.out.println("  connect == new");
				String dbLocation = AppValidation.checkNull(req.getParameter("dblocation"));
				if (dbLocation.equals(""))
					dbLocation = DB_LOCATION;
				
				System.out.println("req: " + dbLocation + ", ses: " + dbm.getDbLocation());
				
				String dbPortStr = AppValidation.checkNull(req.getParameter("dbport"));
				if (dbPortStr.equals(""))
					dbPortStr = "27017";
				
				int dbPort = Integer.parseInt(dbPortStr);
				if (dbPort == 0)
					dbPort = DB_PORT;
				
				String dbName = AppValidation.checkNull(req.getParameter("dbname"));
				if (dbName.equals(""))
					dbName = "test";
						
				String username = AppValidation.checkNull(req.getParameter("dbusername"));
				String password = AppValidation.checkNull(req.getParameter("dbpassword"));
				
				boolean useValidation = false;
				String useValidationStr = AppValidation.checkNull(req.getParameter("dbusevalidation"));
				if (useValidationStr.equals("on"))
					useValidation = true;
	
				boolean useToAuth = false;
				String useToAuthStr = AppValidation.checkNull(req.getParameter("dbusetoauth"));
				if (useToAuthStr.equals("on"))
					useToAuth = true;
				
				dbm.setDbLocation(dbLocation);
				dbm.setDbPort(dbPort);
				dbm.setDbName(dbName);
				dbm.setAuthDbName(dbName);
				dbm.setDbUsername(username);
				dbm.setDbPassword(password);
				dbm.setValidate(useValidation);
				dbm.setUseToAuth(useToAuth);
			}
			else 
			{
				System.out.println("  connect == else");
				if (!dbm.isUseToAuth()) 
				{
					System.out.println("    !dbm.isUseToAuth()...");
					
					if (dbm.getDbLocation().equals(""))
						dbm.setDbLocation(DB_LOCATION);
					
					System.out.println("      1");
					
					if (dbm.getDbPort() == 0)
						dbm.setDbPort(DB_PORT);
					
					System.out.println("      2");
					
					if (dbm.getDbName().equals(""))
						dbm.setDbName(DB_NAME);
					
					System.out.println("      3");
					
					if (dbm.getAuthDbName().equals(""))
						dbm.setAuthDbName(DB_NAME);
					
					System.out.println("      4");
					
					if (dbm.getChecked().equals("checked"))
						dbm.setValidate(true);
					
					System.out.println("      5");
					
					dbm.setUseToAuth(false);
					
					System.out.println("      6");
				}
			}
			
			dbm.setMessage("you've visited the DbManagementGateway and everything is fine at "+dbm.getDbLocation()+" "+dbm.getDbName()+" "+dbm.getDbPort()+" '"+dbm.getChecked()+"' "+dbm.getDbUsername()+" "+dbm.getDbPassword());
			dbm.setErrorMessage("");

			DbManagementAction dba = new DbManagementAction(dbm);
			
			if (action.equals("dbs")) 
			{
				dbm.setDbs(dba.getDbs());
				dbm.setCols(new ArrayList<String>());
				dbm.setStats(new Stats());
				dbm.setRows(new ArrayList<String>());
				

			}
			else if (action.equals("col"))
			{
				String dbName = AppValidation.checkNull(req.getParameter("dbname"));
				dbm.setDbName(dbName);
				dbm.setDbs(dba.getDbs());
				dbm.setCols(dba.getCols(dbName));
				dbm.setStats(dba.getDbStats(dbName));
				dbm.setRows(new ArrayList<String>());
			}
			else if (action.equals("dta"))
			{
				String dbName = AppValidation.checkNull(req.getParameter("dbname"));
				String colName = AppValidation.checkNull(req.getParameter("colname"));
				dbm.setDbName(dbName);
				dbm.setColName(colName);
				dbm.setDbs(dba.getDbs());
				dbm.setCols(dba.getCols(dbName));
				dbm.setRows(dba.getRows(dbName, colName));
			}
			else if (action.equals("alldbstats"))
			{
				dbm.setAllDbStats(dba.getDbStats());
				url = "/db_stats.jsp";
			}
		}
		catch (Exception e)
		{
			System.err.println("exception - dbmanagementgateway - message: " + e.getMessage());
			e.printStackTrace();
			if (e.getMessage().startsWith("cannot connect to mongo database at ")) 
			{
				dbm.setMessage("");
				dbm.setErrorMessage("you've visited the DBManagementGatway and had a problem...<br/><br/><font color=\"red\">"+e.getMessage()+"</font>");
			}
			else 
			{
				dbm.setMessage("");
				dbm.setErrorMessage("<font color=\"red\">"+e.getMessage()+"</font>");				
				e.printStackTrace();
			}
		}
		
		session.setAttribute("DBMgmt", dbm);
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);		
		rd.forward(req, res);
	}
	
	
}
