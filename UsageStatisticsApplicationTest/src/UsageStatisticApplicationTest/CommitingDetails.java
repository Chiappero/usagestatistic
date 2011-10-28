package UsageStatisticApplicationTest;

import UsageStatisticClient.CommitingDetailsInterface;

public class CommitingDetails implements CommitingDetailsInterface
{
	int logsammount=0;
	int current=0;
	

	@Override
	public void commitingFailureWithError(String arg0) 
	{
		System.out.println("commitingFailureWithError: "+arg0);
		
	}

	@Override
	public void commitingFinishedSuccesful() 
	{
		System.out.println("commitingFinishedSuccesful!!!!");
		
	}

	@Override
	public void commitingStart() {
		System.out.println("commitingStart...");
		current=0;
		
	}

	@Override
	public void setInfo(String arg0) 
	{
		System.out.println("Info: "+arg0);
		
	}

	@Override
	public void setLogsAmount(int arg0) {
		logsammount=arg0;
		
	}

	@Override
	public void step() {
		current++;
		System.out.println(current+"/"+logsammount);
		
	}

	@Override
	public void stepInvalid(String arg0) {
		System.out.println("Incalid log: "+arg0);
		current++;
		System.out.println(current+"/"+logsammount);
		
	}

}
