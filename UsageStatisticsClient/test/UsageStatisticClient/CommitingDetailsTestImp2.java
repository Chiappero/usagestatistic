package UsageStatisticClient;

import junit.framework.Assert;

public class CommitingDetailsTestImp2 implements CommitListener
{	

String msg;
	
@Override
public void stepInvalid(String reason) {
}

@Override
public void step() {
}

@Override
public void setLogsAmount(int amount) {
}

@Override
public void setInfo(String info) {
}

@Override
public void commitingStart() {
}

@Override
public void commitingFinishedSuccesful() {
}

@Override
public void commitingFailureWithError(String error) {
	msg=error;

}
}
