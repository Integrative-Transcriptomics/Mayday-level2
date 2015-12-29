package mayday.jsc.recognition;

/**
 * Precedence of Operators
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSOperatorHandler
{
	JSClassRecognizer classRecon;
	private static String opR1 = "[(\\+\\+)(\\-\\-)]";
	private static String opR2 = "[\\~\\!]";
	private static String opR3 = "[\\*\\/\\%]";
	private static String opR4 = "[\\+\\-]";
	private static String opR5 = "[(\\<\\<)(\\>\\>)(\\>\\>\\>)]";
	private static String opR6 = "[\\<\\>(\\>\\=)(\\<\\=)]";
	private static String opR7 = "[(\\=\\=)(\\!\\=)]";
	private static String opR8 = "[\\&]";
	private static String opR9 = "[\\^]";
	private static String opR10 = "[\\|]";
	private static String opR11 = "[(\\&\\&)]";
	private static String opR12 = "[(\\|\\|)]";
	private static String opR13 = "[\\?\\:]";
	private static String opR14 = "[\\=(\\+\\=)(\\-\\=)(<<\\=)(>>\\=)(&\\=)(^\\=)(\\|\\=)]";
	private static String opR15 = "[\\[\\]\\(\\)\\,\\;]";
	private static String[] operators = new String[]{opR1,opR2,opR3,opR4,opR5,opR6,opR7,opR8,opR9,opR10,opR11,opR12,opR13,opR14,opR15};
	
	/*
	 * @return Class result of combination of Classes c1, c2
	 * throws exception if classes not combinable
	 */
	public static Class<?> classCombination(Class<?> c1, Class<?> c2) throws IllegalArgumentException	
	{
		if(c1 == null || c2 == null)
			throw new IllegalArgumentException();
		if(c1.equals(c2))
			return c1;
		if((c1.equals(String.class)) || (c2.equals(String.class)))
			return String.class;
		if((c1.equals(Integer.TYPE) && c2.equals(Double.TYPE))
				|| (c1.equals(Double.TYPE) && c2.equals(Integer.TYPE)))
			return Double.TYPE;
		
		throw new IllegalArgumentException();
	}	
	
	public static int precedenceOfOperator(String op)
	{		
		for(int i = 0; i < operators.length; i++)
			if(op.matches(operators[i]))
					return i;
		return -1;
	}

	public static boolean hasAhigherPrecedence(String a, String b)
	{
		int prioA = precedenceOfOperator(a);
		int prioB = precedenceOfOperator(b);
		return prioA > prioB;
	}
}
