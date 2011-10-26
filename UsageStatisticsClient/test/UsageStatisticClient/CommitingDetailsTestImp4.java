package UsageStatisticClient;

import java.util.LinkedList;

import junit.framework.Assert;

public class CommitingDetailsTestImp4 implements CommitingDetailsInterface
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
	}

	@Override
	public void commitingFinishedSuccesful()
	{
		licznik++;
		Assert.assertEquals(9, licznik);
	}

	@Override
	public void commitingFailureWithError(String error)
	{
	}

	@Override
	public void step()
	{
		licznik++;
		Assert.assertTrue(licznik>=4&&licznik<=8);
	}
	
	@Override
	public void setInfo(String info)
	{
		if (!temp) 
		{
			Assert.assertEquals(info, "Begin commiting");
			temp=true;
		} else
		{
			Assert.assertEquals(info, "Commiting finised succesful");
		}
		this.info=info;
		licznik++;
		Assert.assertEquals(3, licznik);
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
