package UsageStatisticServer;
 
import java.sql.SQLException;

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
	    	public ModelAndView addPerson(@RequestBody PairLogInformationAndPassword pair) {
	    	ModelAndView modelAndView = new ModelAndView("wiadomosc");
	    	//System.out.println("otrzymalem user="+pair.getLogInformation().getUser()+" oraz password="+pair.getPassword());
	    	if (pair!=null)
	    	{
		    	String password = pair.getPassword();
		    	String user = pair.getLogInformation().getUser();
		    	if (user!=null&&password!=null)
		    	{
		    			//System.out.println("user nie jest nullem, ani password nie jest nullem");
			    	try
					{
						if (dao.isValidCredential(user, password))
						{
									//System.out.println("udalo mi sie zautentykowac z user="+user+" oraz password="+password);
							/*if (dao.saveLog(pair.getLogInformation()))
							{
								modelAndView.addObject("message", "OK");
							} 
							else
							{
								modelAndView.addObject("message", "ERROR");
							}*/
							modelAndView.addObject("message", dao.saveLog(pair.getLogInformation())?"OK":"ERROR");
							return modelAndView;
						}
					} catch (SQLException e){}
		    	}
		    	modelAndView.addObject("message", "CANNOT_AUTHENTICATE");
			    return modelAndView;
	    		
	    	}
	    	modelAndView.addObject("message", "ERROR");
	    	return modelAndView;
	    	
	    	
	    	//return new ModelAndView("OK");
	    	//return dao.saveLog(log.getBody())?new ModelAndView("OK"):new ModelAndView("ERROR");
	    	
	    }
	}
