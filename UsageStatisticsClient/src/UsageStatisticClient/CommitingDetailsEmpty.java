package UsageStatisticClient;

final class CommitingDetailsEmpty implements CommitListener {
	
	CommitingDetailsEmpty()
	{
	}
	
	@Override
	public void commitingStart() {
	}

	@Override
	public void commitingFinishedSuccesful() {
	}

	@Override
	public void commitingFailureWithError(String error) {
	}

	@Override
	public void step() {
	}

	@Override
	public void setInfo(String info) {
	}

	@Override
	public void setLogsAmount(int amount) {
	}

	@Override
	public void stepInvalid(String reason)
	{
	}

	

}
