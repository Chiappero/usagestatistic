package usagestatisticsserver;
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
	
	public final String getParameters() {
		return parameters;
	}

	public final void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public final String getFunctionality() {
		return functionality;
	}
	public final void setFunctionality(String functionality) {
		this.functionality = functionality;
	}
	public final int getCount() {
		return count;
	}
	public final void setCount(int count) {
		this.count = count;
	}
	
	public final String toString()
	{
		return "["+functionality+", "+count+"]";
	}
	
	
	
	
}
