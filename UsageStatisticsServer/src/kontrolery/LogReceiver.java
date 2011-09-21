package kontrolery;
 
	import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import connection.LogInformation;




	 
	@Controller
	public class LogReceiver { //TODO autentykowanie
	   	 
	    @RequestMapping(value = "/post", method = RequestMethod.POST)
	    	public ModelAndView addPerson(@RequestBody LogInformation log) {
	    	System.out.println("post="+log);
	    	return new ModelAndView("postowanie");
	    }
	}
