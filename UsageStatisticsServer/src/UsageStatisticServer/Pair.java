package UsageStatisticServer;

public class Pair<Lewy,Prawy>
{
Lewy lewy;
Prawy prawy;
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

public Pair()
{}

public Pair(Lewy l, Prawy p)
{
	lewy=l;
	prawy=p;
}
}
