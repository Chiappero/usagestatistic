package server;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


	

	 
	@Controller
	public class LogReceiver { //TODO autentykowanie

	@Autowired
	DaoServerDatabaseH2 dao;
		
	    @RequestMapping(value = "/post", method = RequestMethod.POST)
	    	public ModelAndView addPerson(@RequestBody LogInformation log) {
	    	//System.out.println("post="+log);
	    	//System.out.println(
	    			dao.saveLog(log);
	    			//);
	    	return new ModelAndView("postowanie");
	    }
	}
