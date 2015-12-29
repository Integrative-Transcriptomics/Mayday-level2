package mayday.jsc.adjustableBehaviour;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import mayday.jsc.recognition.JSClassRecognizer;
import mayday.jsc.shell.ToolBox;
import mayday.jsc.tokenize.JSBraceToken;
import mayday.jsc.tokenize.JSTokenNavigator;
import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Tokenizer;
import mayday.mushell.tokenize.Token.TokenType;

/*
 * DEFINE FOR CLASS:qwertz
 * FOR:[x,y]=z
 * DO:get(x).get(y)=z
 */

/** 
 * Organizes and applies overloaded Operators
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
public class JSOverloadingOperators extends Observable
{
	HashMap<Class<?>, List<JSOperator>> operations;//key is class for which operators in list are defined
	Tokenizer tokenizer;
	JSClassRecognizer classRecon;
	
	public JSOverloadingOperators(JSClassRecognizer classRecognizer)
	{
		this.operations = new HashMap<Class<?>, List<JSOperator>>();
		this.classRecon = classRecognizer;
		this.tokenizer = JSTokenizer.getInstance();
	}	
	
	public HashMap<Class<?>, List<JSOperator>> getOperators()
	{
		return this.operations; 
	}
	
	//Parse new operator from console input
	public void addOperator( String consoleInput ) throws ClassNotFoundException	
	{
		/*
		 * DEFINE FOR CLASS:qwertz
		 * FOR:obj[x,y]=z
		 * DO:obj.get(x).get(y)=z
		 */				
		addOperator(consoleInput.split("\n"));										
	}
	public void addOperator( String[] lines ) throws ClassNotFoundException	
	{
		//.startsWith("DEFINE FOR CLASS:"))
		String className = lines[0].substring(17);
		//.startsWith("FOR:"))
		String operatorString = lines[1].substring(4);
		//.startsWith("DO:"))
		String operationString = lines[2].substring(3);						
		
		//Assignable?
		boolean assignable = false;
		if(className.startsWith("ASSIGNABLE FROM:"))
		{
			className = className.substring(16);
			assignable = true;
		}
			
		
		this.addOperator( className, operatorString, operationString, assignable );		
	}
	
	//Add an operator to map
	public JSOperator addOperator( String className,
							 String operatorString,
							 String operationString,
							 boolean assignable) throws ClassNotFoundException				
	{
		Class<?> c = Class.forName(className);
		JSOperator op = new JSOperator(operatorString, operationString, assignable, c);
		
		this.addOperator( c, op );
		return op;
	}	
	
	/** 
     * Adds an operator to the correct position of this class's operator-list
     *
     * @version 1.0
     * @param forClass Class for which operator is defined
     * @param operator Operator to be inserted
     */
	public void addOperator(Class<?> forClass, JSOperator operator)
	{
		List<JSOperator> opList;
		
		if(this.operations.containsKey(forClass))
			opList = this.operations.get( forClass );
		else
		{			
			opList = new ArrayList<JSOperator>();
			this.operations.put( forClass, opList );
		}
		
		insertSorted(opList, operator);	
		super.setChanged();
		super.notifyObservers();
	}
	
	/** 
     * Applies Operators to all arguments within given command
     *
     * @version 1.0
     * @param command Operators will be applied to this
     */
	private String apply2Arguments(String command)
	{		
		TokenSet ts = this.tokenizer.tokenize(command);
		
		if(ts.size() <= 0)
			return command;
		
		int lastUnused = 0;
		String result = "";
		for(int i = 0; i < ts.size(); i++)
		{
			Token t = ts.get(i);
			if(t.getType() == TokenType.PUNCTUATION
					&& t.getClass().equals(JSBraceToken.class)
					&& ((JSBraceToken)t).isOpeningBrace())
			{				
				result += command.substring(ts.get(lastUnused).getStart(), t.getEnd()+1);
				Token tp = ((JSBraceToken)t).getPartner();				
				result += this.applyOperators(command.substring(t.getEnd()+1, tp.getStart()));
				i = ts.indexOf(tp);
				lastUnused = i;
			}			
		}
		result += command.substring(ts.get(lastUnused).getStart(), ts.getLast().getEnd()+1);
		return result;
	}
	
	/** 
     * Inserts a new Operator into the given list, based on the operators length
     *
     * @version 1.0
     * @param opList List into which the operator shall be inserted
     * @param operator JSOperator to be inserted
     */
	private void insertSorted(List<JSOperator> opList, JSOperator operator)
	{
		int tsSize = operator.operatorTS.size();
		for(int i = 0; i < opList.size(); i++)
			if(opList.get(i).operatorTS.size() < tsSize)
			{
				opList.add(i, operator);
				return;
			}
		opList.add(operator);		
	}

	/** 
     * Deletes a specific operator belonging to a specific class
     *
     * @version 1.0
     * @param c Class which shall be freed of an operators
     * @param op Operator to be removed
     */
	public void removeOperator(Class<?> c, JSOperator op)
	{		
		List<JSOperator> l = this.operations.get(c);
		this.operations.get(c).remove(op);
		if(l.isEmpty())//Completely remove if no more operators present
			this.operations.remove(c);
		super.setChanged();
		super.notifyObservers();
	}
	
	public boolean removeOperator(JSOperator op)
	{		
		Set<Class<?>> cl = this.operations.keySet();
		for(Class<?> c : cl)
		{
			if(this.operations.get(c).remove(op))
			{
				if(this.operations.get(c).isEmpty())//Completely remove if no more operators present
					this.operations.remove(c);
				super.setChanged();
				super.notifyObservers();				
				return true;
			}				
		}
		return false;		
	}

	/** 
     * Deletes all operators belonging to a specific class
     *
     * @version 1.0
     * @param c Class which shall be freed of all operators
     */
	public void removeAllOperatorsOf(Class<?> c)
	{	
		this.operations.remove(c);
		super.setChanged();
		super.notifyObservers();
	}
	
    /** 
     * Exports defined Operators in a way that can be evaluated by the jsc
     *
     * @version 1.0
     * @param f Operators will be written to this file
     */
	public void export(File f)
	{
		String ops = "";
		for(Class<?> key : this.operations.keySet())
		{
			String defForClass = "DEFINE FOR CLASS:";
			String assignable = "ASSIGNABLE FROM:";
			String className = key.getName();		
			for(JSOperator op : this.operations.get(key))
			{				
				ops += defForClass;
				if(op.isAssignable())
					ops += assignable;
				ops += className + "\n";
				ops += "FOR:"+op.getOperator()+"\n";
				ops += "DO:"+op.getOperation()+"\n\n";
			}
		}
		ToolBox.save(f, ops, null);		
	}

    /** 
     * Applies all fitting defined operators to a given command
     *
     * @version 1.0
     * @param commands String containing command to which operators shall be applied
     * @return Command with applied operators
     */
	public String applyOperators( String commands )
	{		
		/* 
		 * Start by tokenizing our input. Based on the tokens we'll check
		 * whether any operator fits.
		 */
		TokenSet ts = this.tokenizer.tokenize(commands);
		
		/*
		 * This will contain the input-command with applied operators.
		 */
		String result = "";
		
		/*
		 * We start at the first token of ts.
		 */
		int firstToken = 0;		
		/*
		 * Any defined operator is defined for a specific class. Therefore we'll always
		 * look at ts from the first index of any subcommand (e.g. divided by ';').
		 */		
		for(int tsLast = JSTokenNavigator.getLastCmdTokenIndex(ts, commands, firstToken)+1;
			tsLast <= ts.size() && firstToken < ts.size();
			tsLast = JSTokenNavigator.getLastCmdTokenIndex(ts,
														   commands,
														   firstToken=++tsLast)+1)
		{												
			String subCommand = commands.substring(ts.get(firstToken).getStart());//OvOps might contain semicola, so it might succeed a regular subcommand
					
			Try2ApplyResult tres = try2applyOp(subCommand,tsLast-firstToken);
			result += tres.result;
			tsLast = Math.max(tres.index+firstToken, tsLast);			
		}			
		
		result = result.isEmpty() ? commands : result;		
		result = apply2Arguments(result);
		return commands.equals(result) ? result : this.applyOperators(result);
	}

    /** 
     * Assigns a value to a variable of the operator
     *
     * @version 1.0
     * @return Index of current token after value was assigned
     */
	private int assignVarValue(
			Token opT,
			String commands,
			int currentIn,
			TokenSet ts,
			JSOperator operator,
			HashMap<String, String> varValue)
	{
		Token inT = ts.get(currentIn);
		TokenType inType = inT.getType();
		
		if((opT.getType() == TokenType.OBJECT//Probably assign a value to a variable of the operator
				|| opT.getType() == TokenType.COMMAND)
				&& (inType == TokenType.OBJECT
						|| inType == TokenType.COMMAND
						|| inType == TokenType.NUMBER
						|| (inType == TokenType.OPERATOR
								&& currentIn < ts.size()
								&& (ts.getNext(inT).getType() == TokenType.NUMBER
									|| (currentIn+1 < ts.size()
											&& ts.getNext(inT).getType() == TokenType.OPERATOR
											&& ts.getNext(inT).getType() == TokenType.NUMBER)))
						|| inType == TokenType.STRING
						|| (inType == TokenType.PUNCTUATION//Might be braced value
								&& inT.getClass() == JSBraceToken.class
								&& ((JSBraceToken)inT).isOpeningBrace()))												
								&& !JSTokenizer.getStringOfToken(opT, operator.operator).equals(JSTokenizer.getStringOfToken(inT, commands)))//ignore if match
		{	
			//Set i to index of end of current command
			//if(ts.getPrevious(inT).getType() == TokenType.OPERATOR)
			currentIn = JSTokenNavigator.getLastCalcTokenIndex(ts, commands, currentIn); 
			//else
			//	currentIn = JSTokenNavigator.getLastCmdTokenIndex(ts,commands,currentIn);						
			if(currentIn==ts.size())
			{
				return -1;
			}
			String value = commands.substring(inT.getStart(), (inT = ts.get(currentIn)).getEnd()+1);
			
			String var = JSTokenizer.getStringOfToken(opT, operator.operator);			
			varValue.put(var, value); //Save command string with corresponding var from operator
			return currentIn;
		}
		
		/*
		 * Until now the operator might still match if input and operator
		 * match stringwise.
		 */
		else if(!JSTokenizer.getStringOfToken(opT, operator.operator).equals(JSTokenizer.getStringOfToken(inT, commands)))
		{							
			return -1;
		}
		
		
		return currentIn;
	}
	
    /** 
     * Skip all whitespaces in a Tokenset starting from given position
     *
     * @version 1.0
     * @param i Index from which on whitespaces shall be skipped
     * @return Index of first non-whitespace symbol
     */
	private int ignoreWhitespaces(int i, TokenSet ts)
	{
		Token t = ts.get(i);
		while((t.getType() == TokenType.TEXT) || (t.getType() == TokenType.WHITESPACE))//WHITESPACE
		{
			if(++i < ts.size())
				t = ts.get(i);//Input token
			else
				break;
		}
		return i;	
	}
	
    /** 
     * Instantiates JSClassRecognizer as well as its objects
     *
     * @version 1.0
     * @param eng ScriptEngine is necessary to check EngineScope-Bindings
     * @param tsLast ->expected last Token of subcommand
     */
	private Try2ApplyResult try2applyOp(String command, int tsLast)
	{			
		Try2ApplyResult result = new Try2ApplyResult();		
		
		TokenSet ts = JSTokenizer.getInstance().tokenize(command);		
		if(ts.size() == 0)
			return result;
		tsLast = Math.min(tsLast, ts.size()-1);
		/*
		 * We always assume that any given operator doesn't match, in 
		 * which case our result will match our input
		 */
		boolean correctOp = false;		
		int lastToken = 0;		
		int currentIn = 0;

		/*
		 * Starting from firsttoken we'll try to match an operator
		 */
		for(lastToken = currentIn;
			lastToken < ts.size() && !correctOp;//As long as we haven't reached this commands end or found a matching Operator
			lastToken=Math.max(currentIn, ++lastToken))
		{			
			/*
			 * Get last token. This has to be part of the command specifying the
			 * operators class. We'll try to recognize it.
			 */
			Token token = ts.get(lastToken);				
			String classString = command.substring(0, token.getEnd()+1);
		
			Class <?> c = classRecon.recognizeClass(classString);

			/*
			 * Was a class recognized?
			 */
			if(c == null)
				continue;
			TokenSet tokens = this.tokenizer.tokenize(classString);						
			int currentT = JSTokenNavigator.getFirstCmdTokenIndex(tokens, classString);

			if(currentT > 0){
				int tokenStart = tokens.get(currentT-1).getStart();
				result.result = classString.substring(0, tokenStart);
				classString = classString.substring(tokenStart);				
			}
			else
				result.result = "";		
				
			/*
			 * Eventually we found a class which may have operators.
			 * Search for them (as isAssignable maybe used) 
			 */
			List<JSOperator> opList = this.getOperatorsFor(c);	

			/*
			 * Operators found?
			 */
			if(opList.size() < 1)
				continue;
							
			correctOp = false;
			for(JSOperator operator : opList)
			{
				/*
				 * We create a HashMap in order to store any values matching
				 * variables of the currenty operator.
				 */
				HashMap<String, String> varValue = new HashMap<String, String>();
				
				/*
				 * Each operator has a tokenset which we want to match with our ts
				 */
				TokenSet opTokens = operator.operatorTS;
				
				/*
				 * usually a operator definition starts with a variable for
				 * storing the class-specifying part of the input-command (e.g. "obj[.." )
				 */
				if(opTokens.getFirst().getType().equals(TokenType.OBJECT)
						|| opTokens.getFirst().getType().equals(TokenType.COMMAND))					
					varValue.put(JSTokenizer.getStringOfToken(opTokens.getFirst(), operator.operator), classString);											
				
				/*
				 *  This will contain the current token of our input
				 */
				Token inT = null;			
				currentIn = lastToken;
				
				/*
				 * Now everything is set: Let's check if the operator really matches!
				 */
				for(int opTIndex = varValue.size(); opTIndex < opTokens.size(); opTIndex++)
				{	
					/*
					 * Ignore Whitespaces
					 */
					currentIn = ignoreWhitespaces(currentIn, ts);
					opTIndex = ignoreWhitespaces(opTIndex, opTokens);
					if(opTIndex >= opTokens.size() || currentIn >= ts.size())
					{
						correctOp = false;
						break;
					}												
					/*
					 * Ignore Whitespaces - EOF
					 */				
					
					/*
					 * We assume the operator is correct unless proved otherwise.
					 */
					correctOp = true;
					
					/*
					 * Get the next token of the operator
					 */
					Token opT = opTokens.get(opTIndex);//Defined operators token
					
					/*
					 * if available, get the next token of the input.
					 */
					if(++currentIn < ts.size())
						inT = ts.get(currentIn);//Input token
					else
					{
						correctOp = false;		
						break;
					}
					
					/*
					 * Probably assign value from input to a variable from the operator
					 */
					if((currentIn = this.assignVarValue(opT, command, currentIn, ts, operator, varValue)) == -1)
					{
						correctOp = false;
						break;
					}
					/*
					 * Yay, the token matched! (Otherwise we wouldn't arrive at this point)						 
					 */
					
											
				}			
				//correct operator?
				if(correctOp)
				{
					//insert values into operators operation
				
					result.result += operator.apply(varValue);

					result.index = currentIn;//ts.indexOf(inT);
					if(inT!=null && inT.getEnd()+1 < ts.get(tsLast).getEnd()+1)					
						result.result += command.substring(inT.getEnd()+1, ts.get(tsLast).getEnd()+1);
										
					return result;					 
				}								
			}						
		}			
		//No op found				
		result.result = command.substring(0, ts.get(tsLast).getEnd()+1);
		return result;		
	}
	
	/*
	 * Creates a sublist of this objects Operator-List. The sublist
	 * only contains those operators fitting the specified class c. 
	 */
	private ArrayList<JSOperator> getOperatorsFor(Class<?> c)
	{
		ArrayList<JSOperator> res = new ArrayList<JSOperator>();
		
		for(Class<?> clazz : this.operations.keySet())
		{
			if(c.equals(clazz))
				res.addAll(this.operations.get(clazz));
			else if(clazz.isAssignableFrom(c))
			{					
				for(JSOperator o : this.operations.get(clazz))
				{
					if(o.isAssignable())
						this.insertSorted(res, o);
				}					
			}
		}
		
		return res;
	}
	
	private class Try2ApplyResult
	{
		String result ="";
		int index=0;
	}
}
