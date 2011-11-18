package UsageStatisticServer;
//Musi zosta public - Pawel Nieradka
public class StandardFilter 
{
	private String functionality;
	private String parameters;
	private int count;
	
	
	public StandardFilter() {}
	
	public StandardFilter(String functionality, int count, String parameters) {
		this.functionality = functionality;
		this.count = count;
		this.parameters=parameters;
	}
	
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getFunctionality() {
		return functionality;
	}
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String toString()
	{
		return "["+functionality+", "+count+"]";
	}
	
	
	
	
}
