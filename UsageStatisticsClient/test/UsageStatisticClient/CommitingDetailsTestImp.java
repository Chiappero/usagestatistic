package UsageStatisticClient;

import junit.framework.Assert;

public class CommitingDetailsTestImp implements CommitingDetailsInterface
{	
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
		System.out.println("DONE");
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
	public void setInfo(String info)
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
