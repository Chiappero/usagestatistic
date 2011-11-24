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
	
	@Autowired
	private DaoServerDatabaseH2 dao;
	
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	protected ModelAndView wyswietlFormatke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		 //Map<String, Object> myModel = new HashMap<String, Object>();
		String data = (new java.util.Date()).toString();
		ModelAndView mav = new ModelAndView("wybierzStatystyki");
		mav.addObject("data", data);
		mav.addObject("tools",dao.getTools());
		mav.addObject("functionalities",dao.getFunctionalities());
		mav.addObject("users",dao.getUsers());
		mav.addObject("command",new Results());
		return mav;
	}
	
	@RequestMapping(value = "/getfuns", method = RequestMethod.GET)
	protected void getFuns(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		String selectedTool = httpServletRequest.getParameter("tool");
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
		String selectedTool = httpServletRequest.getParameter("tool");
		httpServletResponse.setContentType("text/xml");
		//DAO
		UsersXML users=new UsersXML( dao.getUsers(selectedTool) );
		String usersXML=users.toXml();		
		try {
			httpServletResponse.getWriter().write(usersXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/results", method = RequestMethod.POST)
	protected ModelAndView wyswietlWyniki(@ModelAttribute("Results")Results results, BindingResult result) {
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
		ModelAndView mav = new ModelAndView("statystyki");
		mav.addObject("showParams", results.isParam());
		mav.addObject("data", data);
		mav.addObject("dateFrom", results.getDateFrom());
		mav.addObject("dateTill", results.getDateTill());
		mav.addObject("tool", tool.get(0));
		mav.addObject("logi", logsFromDatabase);
		return mav;
	}
	
//	private boolean isValidNameColumn(String columnName)
//	{
//		return "user".equals(columnName)||"tool".equals(columnName)||"functionality".equals(columnName)||"parameters".equals(columnName)||"timestamp".equals(columnName);
//	}
}