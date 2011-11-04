package UsageStatisticServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sun.net.httpserver.HttpServer;

@Controller
public class LogPresenterController{
	
	@Autowired
	DaoServerDatabaseH2 dao;
	
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
		//Dropdown list
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("---Select---");
		columns.add("tool");
		columns.add("functionality");
		columns.add("user");
		columns.add("timestamp");
		columns.add("parameters");
		mav.addObject("columns", columns);
		return mav;
	}
	
	@RequestMapping(value = "/ajax", method = RequestMethod.GET)
	protected void ajax(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws SQLException{
		String selectedTool = httpServletRequest.getParameter("tool");
		httpServletResponse.setContentType("text/xml");
		//DAO
		FunsXML funs=new FunsXML( dao.getFunctionalities(selectedTool) );
		String funsXML=funs.toXml();		
		try {
			httpServletResponse.getWriter().write(funsXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/results", method = RequestMethod.POST)
	protected ModelAndView wyswietlWyniki(@ModelAttribute("Results")Results results, BindingResult result) {
		ArrayList<String> functionalities = new ArrayList<String>();
		ArrayList<String> tool = new ArrayList<String>();
		ArrayList<String> users = new ArrayList<String>();
		for (int i = 0; i < results.getFunctionalities().length; i++)
		{
			functionalities.add(results.getFunctionalities()[i]);
		}
		tool.add(results.getTool());
		for (int i = 0; i < results.getUsers().length; i++)
		{
			users.add(results.getUsers()[i]);
		}
		
		ArrayList<LogInformation> logsWithWhereClausure;
		try
		{
			LinkedList<String> linked = new LinkedList<String>();
/*			if (isValidNameColumn(results.getSortChoose1()))
			{
			linked.add(results.getSortChoose1());
			}
			if (isValidNameColumn(results.getSortChoose2()))
			{
			linked.add(results.getSortChoose2());
			}
			if (isValidNameColumn(results.getSortChoose3()))
			{
			linked.add(results.getSortChoose3());
			}*/
			logsWithWhereClausure = dao.getLogsWithWhereClausure(new LogFilter(null,null,functionalities,users,tool),linked);
		} catch (SQLException e)
		{
			return new ModelAndView("ERROR");
		}
		String data = (new java.util.Date()).toString();
		ModelAndView mav = new ModelAndView("statystyki");
		mav.addObject("data", data);
		mav.addObject("logi", logsWithWhereClausure);
		
		return mav;
	}
	
	private boolean isValidNameColumn(String columnName)
	{
		return "user".equals(columnName)||"tool".equals(columnName)||"functionality".equals(columnName)||"parameters".equals(columnName)||"timestamp".equals(columnName);
	}
}