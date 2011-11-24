package usagestatisticsclient;

import usagestatisticsclient.CommitListener;


public class CommitingDetailsTestImp3 implements CommitListener
{	
	String msg;
	boolean success=false;
	
	public CommitingDetailsTestImp3()
	{
	}

	@Override
	public void commitingStart()
	{
	}

	@Override
	public void commitingFinishedSuccesful()
	{
		success=true;
	}

	@Override
	public void commitingFailureWithError(String error)
	{
	}

	@Override
	public void step()
	{
	}
	
	@Override
	public void setLogsAmount(int amount)
	{
	}

	@Override
	public void stepInvalid(String reason)
	{
		msg=reason;
	}

}
