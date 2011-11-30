package usagestatisticsclient;

final class CommitingDetailsEmpty implements CommitListener 
{
	
	CommitingDetailsEmpty()
	{}
	
	@Override
	public void commitingStart() {}

	@Override
	public void commitingFinishedSuccesful() {}

	@Override
	public void commitingFailureWithError(final String error) {}

	@Override
	public void step() {}

	@Override
	public void setLogsAmount(final int amount) {}

	@Override
	public void stepInvalid(final String reason)
	{}
}
