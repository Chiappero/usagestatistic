package UsageStatisticClient;

public interface CommitingDetailsInterface { //TODO a co jak przypisza nulla
void commitingStart();
void commitingFinishedSuccesful();
void commitingFailureWithError(String error);
void step();
void stepInvalid(String reason);
void setInfo(String info);
void setLogsAmount(int amount);
}
