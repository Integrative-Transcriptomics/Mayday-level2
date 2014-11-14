package mayday.vis3.plots.venn2;

public enum VennSet 
{
	A(1),
	B(2),
	C(4),
	AB(3),
	AC(5),
	BC(6),
	ABC(7);
	
	private int value;
	
	private VennSet(int v)
	{
		value=v;
	}

	public int getValue() {
		return value;
	}
	
	
	
}
