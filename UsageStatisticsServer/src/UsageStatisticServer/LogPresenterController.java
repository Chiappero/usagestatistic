package UsageStatisticServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
	
	@RequestMapping(value = "/form", method = RequestMethod.GET)
	protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView("formatka");
		mav.addObject("msg", "HELLO!");
		return mav;
	}
	
	@RequestMapping(value = "/wyniki", method = RequestMethod.POST)
	protected ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
		ModelAndView mav = new ModelAndView("wyniki");
		mav.addObject("msg", "wyniki");
		return mav;
	}
}