package au.org.noojee.irrigation.util;

public class Tuple<L,R>
{
	public final L lhs;
	public final R rhs;
	
	public Tuple(L lhs, R rhs)
	{
		this.lhs = lhs;
		this.rhs = rhs;
	}
}
