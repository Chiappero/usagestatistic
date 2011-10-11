package UsageStatisticClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

class DaoTemporaryDatabaseSerializationDEPRECATED implements
		DaoTemporaryDatabaseInterface {

	private LinkedList<LogInformation> lista;
	private boolean databaseActive;
	private File file = new File("dane");

	DaoTemporaryDatabaseSerializationDEPRECATED() {
	}

	@Override
	public boolean isEmpty() {
		try {
			init();
		} catch (FileNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (IOException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		}
		return lista.isEmpty();
	}

	@Override
	public int getLogsAmount() {
		try {
			init();
		} catch (FileNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (IOException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		}
		return lista.size();
	}

	@SuppressWarnings("unchecked")
	private void init() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		if (!databaseActive || lista == null) {
			if (file.createNewFile()) {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(file));
				lista = new LinkedList<LogInformation>();
				out.writeObject(lista); //TODO co gdy plik pusty a brakuje naglowka w nim
				databaseActive = true;
			} else {
				ObjectInputStream in = new ObjectInputStream(
						new FileInputStream(file));
				lista = (LinkedList<LogInformation>) in.readObject();
				databaseActive = true;
			}
		}
	}

	@Override
	public boolean saveLog(LogInformation log) {
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		}
		lista.addLast(log);
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(lista);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean clearFirstLog() {
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			databaseActive = false;
			return false;
		}
		lista.removeFirst();
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream("dane"));
			out.writeObject(lista);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public LogInformation getFirstLog() {// TODO a co jak null (w klasie gdzie
											// sie zwraca wynik)
		try {
			init();
		} catch (FileNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (IOException e) {
			databaseActive = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			databaseActive = false;
			e.printStackTrace();
		}
		return lista.getFirst();
	}

	@Override
	public void openDatabase()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeDatabase()
	{
		// TODO Auto-generated method stub
		
	}

}
