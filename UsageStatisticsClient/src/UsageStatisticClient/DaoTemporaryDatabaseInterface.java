package UsageStatisticClient;

interface DaoTemporaryDatabaseInterface { //TODO obsluga pliku jakby sie wysypal - wyjatki

boolean saveLog(LogInformation log);
boolean clearFirstLog(); 
LogInformation getFirstLog();
boolean isEmpty();
int getLogsAmount();

}
