package UsageStatisticClient;

import junit.framework.Assert;

public class CommitingDetailsTestImp2 implements CommitingDetailsInterface
{	

String msg;
	
@Override
public void stepInvalid(String reason) {
	Assert.assertEquals("Wrong Response", reason);
		

}

@Override
public void step() {
// TODO Auto-generated method stub

}

@Override
public void setLogsAmount(int amount) {
// TODO Auto-generated method stub

}

@Override
public void setInfo(String info) {
// TODO Auto-generated method stub

}

@Override
public void commitingStart() {
// TODO Auto-generated method stub

}

@Override
public void commitingFinishedSuccesful() {
// TODO Auto-generated method stub

}

@Override
public void commitingFailureWithError(String error) {
	msg=error;

}
}
