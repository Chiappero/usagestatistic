package UsageStatisticClient;

interface CommitingDetailsInterface { //TODO a co jak przypisza nulla
void commitingStart();
void commitingFinishedSuccesful();
void commitingFailureWithError(String error);
void step();
void stepInvalid();
void setInfo(String info);
void setLogsAmount(int amount);
}
