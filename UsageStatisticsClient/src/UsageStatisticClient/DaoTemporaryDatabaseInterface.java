package UsageStatisticClient;

import java.sql.SQLException;

interface DaoTemporaryDatabaseInterface {

boolean saveLog(LogInformation log);
void clearFirstLog() throws SQLException; 
LogInformation getFirstLog() throws SQLException;
boolean isEmpty() throws SQLException;
int getLogsAmount() throws SQLException;
void openDatabase();
void closeDatabase();
void resetDatabase();
java.util.Date getOldestLogDate();

}
