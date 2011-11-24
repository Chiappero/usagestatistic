package usagestatisticsclient;

public class Pair<Lewy,Prawy>
{
Lewy lewy;
Prawy prawy;


public Pair(Lewy lewy, Prawy prawy)
{
	super();
	this.lewy = lewy;
	this.prawy = prawy;
}
public Lewy getLewy()
{
	return lewy;
}
public void setLewy(Lewy lewy)
{
	this.lewy = lewy;
}
public Prawy getPrawy()
{
	return prawy;
}
public void setPrawy(Prawy prawy)
{
	this.prawy = prawy;
}

}
