package usagestatisticsserver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
class LogPresenterController{
	
	private static final String CHOOSE_STATISTICS = "chooseStatistics";
	private static final String TOOLS = "tools";
	private static final String TOOL = "tool";
	private static final String TOOL_POST = TOOL; //wprawdzie jest to to samo, ale raz uzywane jako parametr, a raz w formatce, przypadkowa zbieznosc, celowo rozdzielone
	private static final String FUNCTIONALITIES = "functionalities";
	private static final String USERS = "users";
	private static final String COMMAND = "command";
	private static final String DATA_LOGS_GET = "data"; 
	private static final String DATA_LOGS_POST = DATA_LOGS_GET; //wprawdzie jest to to samo, ale jest uzywane w roznych formatkach, celowo rozdzielone
	private static final String SHOW_PARAMS = "showParams";
	private static final String DATE_FROM = "dateFrom";
	private static final String DATE_TILL = "dateTill";
	private static final String LOGS_POST = "logs";

	private static final String STATISTICS = "statistics";
	
	@Autowired
	private DaoServerDatabaseH2 dao;
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	protected ModelAndView wyswietlFormatke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		 //Map<String, Object> myModel = new HashMap<String, Object>();
		String data = (new java.util.Date()).toString();
		ModelAndView mav = new ModelAndView(CHOOSE_STATISTICS);
		mav.addObject(DATA_LOGS_GET, data);
		mav.addObject(TOOLS,dao.getTools());
		mav.addObject(FUNCTIONALITIES,dao.getFunctionalities());
		mav.addObject(USERS,dao.getUsers());
		mav.addObject(COMMAND,new Results());
		return mav;
	}
	
	@RequestMapping(value = "/getfuns", method = RequestMethod.GET)
	protected void getFuns(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		String selectedTool = httpServletRequest.getParameter(TOOL);
		httpServletResponse.setContentType("text/xml");
		//DAO
		FunsXML funs=new FunsXML( dao.getFunctionalities(selectedTool) );
		String funsXML=funs.toXml();		
		try {
			httpServletResponse.getWriter().write(funsXML);
		} catch (IOException e) {
		}
	}
	
	@RequestMapping(value = "/getusers", method = RequestMethod.GET)
	protected void getUsers(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		String selectedTool = httpServletRequest.getParameter(TOOL);
		httpServletResponse.setContentType("text/xml");
		//DAO
		UsersXML users=new UsersXML( dao.getUsers(selectedTool) );
		String usersXML=users.toXml();		
		try {
			httpServletResponse.getWriter().write(usersXML);
		} catch (IOException e) {
		}
	}
	
	@RequestMapping(value = "/logs", method = RequestMethod.POST)
	protected ModelAndView wyswietlWyniki(@ModelAttribute("Results")Results results) {
		ArrayList<String> tool = new ArrayList<String>();

		tool.add(results.getTool());
		
		ArrayList<StandardFilter> logsFromDatabase;
		try
		{
			logsFromDatabase = dao.getLogsFromDatabase(results.getFunctionalities(), results.getUsers(), tool.get(0), results.getDateFrom(), results.getDateTill(), results.isParam());
		} catch (SQLException e)
		{
			return new ModelAndView("ERROR");
		}
		String data = (new java.util.Date()).toString();
		ModelAndView mav = new ModelAndView(STATISTICS);
		mav.addObject(SHOW_PARAMS, results.isParam());
		mav.addObject(DATA_LOGS_POST, data);
		mav.addObject(DATE_FROM, results.getDateFrom());
		mav.addObject(DATE_TILL, results.getDateTill());
		mav.addObject(TOOL_POST, tool.get(0));
		mav.addObject(LOGS_POST, logsFromDatabase);
		return mav;
	}
	
//	private boolean isValidNameColumn(String columnName)
//	{
//		return "user".equals(columnName)||"tool".equals(columnName)||"functionality".equals(columnName)||"parameters".equals(columnName)||"timestamp".equals(columnName);
//	}
}