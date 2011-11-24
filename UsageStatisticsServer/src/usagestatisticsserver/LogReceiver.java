package usagestatisticsserver;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



	

	 
	@Controller
	class LogReceiver {

		private static final String MESSAGE = "message";
		private static final String CANNOT_AUTHENTICATE = "CANNOT_AUTHENTICATE";
		private static final String OK = "OK";
		private static final String ERROR = "ERROR";
		private static final String POST_SLASH = "/post";
		
	@Autowired
	private DaoServerDatabaseH2 dao;
	
	    @RequestMapping(value = POST_SLASH, method = RequestMethod.POST)
	    	ModelAndView addPerson(@RequestBody PairLogInformationAndPassword pair) {
	    	ModelAndView modelAndView = new ModelAndView(MESSAGE);
	    	//System.out.println("otrzymalem user="+pair.getLogInformation().getUser()+" oraz password="+pair.getPassword());
	    	if (pair!=null)
	    	{
		    	String password = pair.getPassword();
		    	LogInformation logInformation = pair.getLogInformation();
		    	if (logInformation!=null)
		    	{
				String user = logInformation.getUser();
		    	if (user!=null&&password!=null)
		    	{
		    			//System.out.println("user nie jest nullem, ani password nie jest nullem");
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
							modelAndView.addObject(MESSAGE, dao.saveLog(logInformation)?OK:ERROR);
							return modelAndView;
						}
		    	
		    	
		    	modelAndView.addObject(MESSAGE, CANNOT_AUTHENTICATE);
			    return modelAndView;
		    	}
		    	}
	    	}
	    	modelAndView.addObject(MESSAGE, ERROR);
	    	return modelAndView;
	    	
	    	
	    	//return new ModelAndView("OK");
	    	//return dao.saveLog(log.getBody())?new ModelAndView("OK"):new ModelAndView("ERROR");
	    	
	    }
	}
