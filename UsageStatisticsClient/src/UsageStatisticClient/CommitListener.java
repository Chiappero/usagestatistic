package UsageStatisticClient;

public interface CommitListener {
void commitingStart();
void commitingFinishedSuccesful();
void commitingFailureWithError(final String error);
void step();
void stepInvalid(final String reason);
void setInfo(final String info);
void setLogsAmount(final int amount);
}
