package usagestatisticsclient;

import usagestatisticsclient.CommitListener;
import junit.framework.Assert;

public class CommitingDetailsTestImp implements CommitListener
{	
	boolean success=false;
	private int amountExpected;
	public CommitingDetailsTestImp(int amountExpected)
	{
		this.amountExpected=amountExpected;
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
		Assert.fail(error);
	}

	@Override
	public void step()
	{
	}

	@Override
	public void setLogsAmount(int amount)
	{
		Assert.assertEquals(amount, amountExpected);
	}

	@Override
	public void stepInvalid(String reason)
	{
		Assert.fail(reason);
	}

}
