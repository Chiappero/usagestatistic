package UsageStatisticClient;

import junit.framework.Assert;

public class CommitingDetailsTestImp4 implements CommitListener
{	//TYLKO dla 5 logow to dziala;
	boolean commitingStart=false;
	boolean commitingFinishedSuccesful=false;
	int amount=-1;
	int licznik=0;
	String info=null;
	boolean temp=false;
	public CommitingDetailsTestImp4()
	{
	}

	@Override
	public void commitingStart()
	{
		licznik++;
		Assert.assertEquals(1, licznik);
		commitingStart=true;
	}

	@Override
	public void commitingFinishedSuccesful()
	{
		licznik++;
		Assert.assertEquals(8, licznik);
		commitingFinishedSuccesful=true;
	}

	@Override
	public void commitingFailureWithError(String error)
	{
	}

	@Override
	public void step()
	{
		licznik++;
		Assert.assertTrue(licznik>=3&&licznik<=7);
	}

	@Override
	public void setLogsAmount(int amount)
	{
		this.amount=amount;
		licznik++;
		Assert.assertEquals(2, licznik);
		Assert.assertEquals(5,amount);
	}

	@Override
	public void stepInvalid(String reason)
	{
	}

}
