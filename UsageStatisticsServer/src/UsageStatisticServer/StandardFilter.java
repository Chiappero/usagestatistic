package UsageStatisticServer;
//Musi zosta public - Pawel Nieradka
public class StandardFilter 
{
	private String functionality;
	private String date;
	private int count;
	
	
	public StandardFilter() {}
	
	public StandardFilter(String functionality, String date, int count) {
		this.functionality = functionality;
		this.date = date;
		this.count = count;
	}
	public String getFunctionality() {
		return functionality;
	}
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString()
	{
		return "["+functionality+", "+date+", "+count+"]";
	}
	
	
	
	
}
