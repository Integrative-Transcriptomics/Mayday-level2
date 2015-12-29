package mayday.jsc.adjustableBehaviour;

import java.util.HashMap;

import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Token.TokenType;

/** 
 * An overloaded operator as definable within the JSC 
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
public class JSOperator
{		
	String operator;
	TokenSet operatorTS;
	String operation;
	String pureOperator;//Operator without initial object if any (eg. obj.asMatrix() -> .asMatrix())	
	//HashMap<String, List<Integer>> insertMap; //contains var-string/index-pairs (specifying where a value should be inserted into the operation)
	private boolean isAssignable;
	private Class<?> operatorClass;
	
	public JSOperator(String operator, String operation, Class<?> clazz)
	{					
		this.operation = operation;
		this.operator = operator;
		this.operatorTS = JSTokenizer.getInstance().tokenize(this.operator);
		this.isAssignable = false;
		this.operatorClass = clazz;
		this.purificateOperator();
	}
	
	public JSOperator(String operator, String operation, boolean assignable, Class<?> clazz)
	{					
		this.operation = operation;
		this.operator = operator;
		this.operatorTS = JSTokenizer.getInstance().tokenize(this.operator);
		this.isAssignable = assignable;
		this.operatorClass = clazz;
		this.purificateOperator();
	}
	
    /** 
     * Create a clean version of this operators syntax by removing the initial object-reference
     *
     * @version 1.0     
     */
	private void purificateOperator()
	{
		if(this.operatorTS.getFirst().getType() == TokenType.COMMAND
				|| this.operatorTS.getFirst().getType() == TokenType.OBJECT)
			this.pureOperator = this.operator.substring(this.operatorTS.getFirst().getEnd()+1);
		else 
			this.pureOperator = this.operator;
	}

    /** 
     * Inserts a set of values into a copy of this operators operation
     *
     * @version 1.0
     * @param varValue A mapping of this operators variables and their values
     * @return A string with this operators syntax and the specified variable-values
     */
	public String apply(HashMap<String,String> varValue)
	{
		String result = this.operation;
		
		//Replace vars in Operation with values
		TokenSet ts = JSTokenizer.getInstance().tokenize(this.operation);		
		int n = 0;
		for(int i = 0; i < ts.size(); i++)
		{
			Token t = ts.get(i);
			if(t.getType().equals(TokenType.OBJECT) || t.getType().equals(TokenType.COMMAND))
			{
				String var = this.operation.substring(t.getStart(),t.getEnd()+1);				
				if(varValue.containsKey(var))
				{
					String value = varValue.get(var);
					result = result.substring(0, t.getStart()+n)+value+result.substring(t.getEnd()+1+n);					
					n+=value.length()-var.length();
				}
			}
		}				
		
		return result;
	}
	
	
	public String toString()
	{		
		return "<html><b><font color='#FF6700'>" + this.operator + "</font></b> <font size='3'>&#8658;</font> <b><font color='#00A876'>" + this.operation + "</font></b></html>";
	}
	
	public String getOperation()
	{
		return this.operation;
	}
	public String getPureOperator()
	{
		return this.pureOperator;
	}	
	public Class<?> getOperatorClass()
	{
		return this.operatorClass;
	}	
	public String getOperator()
	{
		return this.operator;
	}
	public boolean isAssignable()
	{
		return this.isAssignable;
	}

	public void setAssignable(boolean assignable)
	{
		this.isAssignable = assignable;
	}
}	