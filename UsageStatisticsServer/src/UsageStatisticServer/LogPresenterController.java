package UsageStatisticServer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	protected ModelAndView wyswietlFormatke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
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
	
	@RequestMapping(value = "/results", method = RequestMethod.POST)
	protected ModelAndView wyswietlWyniki(@ModelAttribute("Results")Results results, BindingResult result) {
		ArrayList<String> functionalities = new ArrayList<String>();
		ArrayList<String> tools = new ArrayList<String>();
		ArrayList<String> users = new ArrayList<String>();
		for (int i = 0; i < results.getFunctionalities().length; i++)
		{
			functionalities.add(results.getFunctionalities()[i]);
		}
		for (int i = 0; i < results.getTools().length; i++)
		{
			tools.add(results.getTools()[i]);
		}
		for (int i = 0; i < results.getUsers().length; i++)
		{
			users.add(results.getUsers()[i]);
		}
		
		ArrayList<LogInformation> logsWithWhereClausure;
		try
		{
			logsWithWhereClausure = dao.getLogsWithWhereClausure(null,null,functionalities,users,tools);
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
}