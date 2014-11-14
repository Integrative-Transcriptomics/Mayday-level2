package mayday.jsc.recognition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import mayday.jsc.tokenize.JSBraceToken;
import mayday.jsc.tokenize.JSTokenNavigator;
import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Tokenizer;
import mayday.mushell.tokenize.Token.TokenType;

/**
 * Methods for recognizing JavaScript-Commands within Strings
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSCommandRecognizer
{
	private ScriptEngine engine;	//Scriptengine needed to associate JS-vars with Java-Objects
	private JSClassRecognizer classRecon;
	protected Tokenizer tokenizer;
	
	
	public JSCommandRecognizer(ScriptEngine scriptEngine)
	{
		this.engine = scriptEngine;
		this.tokenizer = JSTokenizer.getInstance();
		this.classRecon = new JSClassRecognizer(this.engine, this);
	}

	public JSClassRecognizer getClassRecognizer()
	{
		return this.classRecon;
	}
	
	public JSRecognizedCommand recognizeCommand(String command) throws IllegalArgumentException, NoSuchElementException
	{				
		if(command.isEmpty())
			throw new NoSuchElementException(command);
		TokenSet tokens = this.tokenizer.tokenize(command);						
		int currentT = JSTokenNavigator.getFirstCmdTokenIndex(tokens, command);						
										
		return this.recognizeFromTokenSet_prepare(tokens, currentT, command);	
	}		
	
	/*
	 * Prepares the input, checks for errors
	 */
	private JSRecognizedCommand recognizeFromTokenSet_prepare(
			TokenSet tokens,
			int currentT,
			String command)
		throws IllegalArgumentException,//This will just throw IllegalArgumentException except [NoSuchElemt,NoSuchClass,...] as latter occur only because of invalid input
				NoSuchElementException
	{
		if(currentT >= tokens.size())
			throw new NoSuchElementException("");
		
		Token cmd = tokens.get(currentT);
		if(cmd.getType() == TokenType.ERROR_TOKEN
				|| cmd.getType() == TokenType.OPERATOR
				|| (cmd.getType() == TokenType.PUNCTUATION
						&& command.substring(cmd.getStart(),cmd.getEnd()+1).matches("[\\;\\,]")))
			throw new NoSuchElementException("");//eg open brace		

		return this.recognizeFromTokenSet_base(tokens, currentT, cmd, command);
	}
	
	/*
	 * Recognizes base object of command and gives that to final
	 * recognizeFromTokenSet-method
	 */
	private JSRecognizedCommand recognizeFromTokenSet_base(
			TokenSet tokens,
			int currentT,
			Token cmd,
			String command)
	{
		Class<?> currentClass = null; // Output-class
		boolean staticMember = false;				
			
		//First recognize the base object		 		
		String objectName = JSTokenizer.getStringOfToken(cmd, command);
		if(cmd.getType() == TokenType.PUNCTUATION//for eg ("izg"+"ib").substring(2)					
				&& cmd.getClass() == JSBraceToken.class)			
		{
			JSBraceToken bt = (JSBraceToken)cmd;
			if(bt.isOpeningBrace())
			{
				Class<?>[] cl = this.getParameters(tokens, currentT, command);
				if(cl.length == 1)
				{
					currentClass = cl[0];
					cmd = ((JSBraceToken)cmd).getPartner();
					currentT = tokens.indexOf(cmd)+1;	
				}
			}
		}
		else
		{		
			Object firstObject = this.engine.get(objectName.trim());
			if(firstObject == null)	//corrupt command
			{
				if( cmd.getType() == TokenType.STRING )				
					currentClass = String.class;	//Actually this would be a native js string			
				else if( cmd.getType() != TokenType.COMMAND )
				{
					try
					{			
						//Might be class
						firstObject = this.engine.eval(objectName);
						if(firstObject == null)
							throw new NoSuchElementException(objectName.trim());
					}
					catch(Exception e){};
				}
				else
					//Unrecognizable:
					throw new NoSuchElementException(objectName.trim());

			}	

			/*
			 * Some types, eg. lang.String will be returned as wrapped Objects. As 
			 * NativeJavaObject is not available here, we use eval+getClass(). As we
			 * just use this on NativeJavaObjects, eval can be used without harm. 
			 */			
			if(firstObject != null)
			{
				if(firstObject.getClass().getSimpleName().equals("NativeJavaObject"))
					try
				{
						currentClass = (Class<?>)engine.eval(objectName+".getClass()");
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				else if(firstObject instanceof java.lang.Class)
				{
					currentClass = (Class<?>)firstObject;
					staticMember = true;
				}
				else
					currentClass = firstObject.getClass();
			}			
		}

		if(currentClass == null)
			throw new NoSuchElementException(command.trim());
		if(this.isPrimitive(currentClass))
			throw new IllegalArgumentException();
		
		
		return this.recognizeFromTokenSet_rest(++currentT, tokens, currentClass, staticMember, command);		
	}

	
	/*
	 * After base-object is identified, this 
	 * method identifies the rest of the command
	 */
	public JSRecognizedCommand recognizeFromTokenSet_rest(
			int currentT,
			TokenSet tokens,			
			Class<?> currentClass,
			boolean staticMember,
			String command)
	{
		HashMap<Object, Class<?>> genMap = new HashMap<Object, Class<?>>();//Map for generic return types
		String prefix; //Output-prefix
		Token cmd;		
		while(currentT < tokens.size())
		{	
			cmd = tokens.get(currentT);
			String objectName = JSTokenizer.getStringOfToken(cmd, command).trim();		
			if(objectName.equals("."))
			{
				if(tokens.getNext(cmd) == null
						|| tokens.getNext(cmd).getType() == TokenType.OBJECT
						|| tokens.getNext(cmd).getType() == TokenType.COMMAND)
				{
					currentT++;//Just a dot, nothing to remember
				}
				else
					break;
			}
			else if(cmd.getType() == TokenType.TEXT || cmd.getType() == TokenType.WHITESPACE)
				currentT++;//Just a whitespace, nothing to remember
			else if(cmd.getType() == TokenType.OBJECT)
			{
				//Current Sub might be field, exception is thrown if not
				Class<?> nextClass;
				try {					
					nextClass = currentClass.getField(objectName).getType();
					staticMember = Modifier.isStatic(nextClass.getModifiers());
				} catch (Exception e)
				{
					break;
				}
				currentClass = nextClass;
				currentT++;
			}											 																													

			else if(cmd.getType() == TokenType.COMMAND
					&& (cmd = tokens.getNext(cmd)).getType() == TokenType.PUNCTUATION
					&& cmd.getClass() == JSBraceToken.class)
			{			
				//is method				
				Class<?>[] parameters = this.getParameters(tokens, 1+currentT, command);				
				JSRecognizedCommand recCmd = this.nextClass(currentClass, objectName, genMap, parameters);
				if(recCmd == null)				
					throw new NoSuchElementException(command);
				staticMember = recCmd.isStatic();
				currentClass = recCmd.getRecognizedClass();
				genMap = recCmd.getGenMap();				
				//Next command-Token
				cmd = ((JSBraceToken)cmd).getPartner();
				currentT = tokens.indexOf(cmd)+1;				
			}
			else
				currentT++;
									
			while(currentClass.isArray()//"while" b/c multi-dimensional array
					&& currentT < tokens.size()
					&& (cmd = tokens.get(currentT)).getType() == TokenType.PUNCTUATION
					&& cmd.getClass() == JSBraceToken.class)
			{//is array-element
				currentT++;
				currentClass = currentClass.getComponentType();//Works only on java arrays, not js arrays (as they may contain different Object-Types during runtime)				
				currentT = tokens.indexOf(((JSBraceToken)cmd).getPartner())+1; 				
			}			
			
			if(currentT < tokens.size()
					&& (cmd = tokens.get(currentT)).getClass() == JSBraceToken.class)
				break;
			
		}			
		
		prefix = JSTokenizer.getStringOfToken(tokens.getLast(), command);
		if(prefix.matches("[\\.\"\\'\\)]"))
			prefix = "";
		return new JSRecognizedCommand(currentClass, prefix, genMap, staticMember);	
	}
	
	
	/*
	 * Returns next class found in currentClass by looking for
	 * a method named cmdSub with parameters parameters and taking its
	 * return value. Also updates the genMap of generics.
	 */
	private JSRecognizedCommand nextClass(
			Class<?> currentClass,
			String cmdSub,
			HashMap<Object, Class<?>> genMap,
			Class<?>[] parameters)
	{		
		Method meth = JSMethodRecognizer.recognizeMethod(currentClass, cmdSub, parameters); 
		//NOTE: currentClass.getMethod(cmdSub, parameters) <- only exact parameter matches
		
		//boolean staticMember = Modifier.isStatic(meth.getModifiers());
		
		if(meth == null)
			throw new IllegalArgumentException();		
		
		Object returnType = meth.getGenericReturnType();
	
		currentClass = (genMap.containsKey(returnType)) ?
				genMap.get(returnType) : meth.getReturnType();
	
		genMap.clear();
		
		if(returnType instanceof ParameterizedType)//generics
		{
			ParameterizedType gen = (ParameterizedType)returnType;					
			Object[] typeParam = ((Class<?>)gen.getRawType()).getTypeParameters();
			Object[] actualType = gen.getActualTypeArguments();
			
			for(int i = 0; i < typeParam.length; i++)
			{
				try
				{
					Class<?> c =(Class<?>)actualType[i];
					genMap.put(typeParam[i], c);//(typeParam (= <E,..>), actual class)
				}
				catch(ClassCastException e){};
			}
		}
		return new JSRecognizedCommand(currentClass, "", genMap, false);
	}	

	
	/*
	 * Gets a tokenset and an index currentT of an opening brace following methodname
	 * Returns an array of classes found before corresponding closing brace
	 */
	private Class<?>[] getParameters(
			TokenSet tokens,
			int currentT,
			String command
			) throws IllegalArgumentException
	{
		int coBraceInd = tokens.indexOf(((JSBraceToken)tokens.get(currentT)).getPartner());
		
		List<Class<?>> parameters = new ArrayList<Class<?>>();//parameters for method		
		for(++currentT; currentT < coBraceInd; currentT+=2)//+2 avoids commata
		{
			Token paramToken = tokens.get(currentT);
			currentT = JSTokenNavigator.getLastCmdTokenIndex(tokens, command, currentT);
			Class<?> currentParamClass = this.classRecon.recognizeClass(paramToken, tokens.get(currentT), command);

			//Combine all objects connected via Operators, e.g. 1+2+3
			while(tokens.get(currentT+1).getType() == TokenType.OPERATOR)
			{
				currentT += 2;
				paramToken = tokens.get(currentT);
				currentT = JSTokenNavigator.getLastCmdTokenIndex(tokens, command, currentT);
				//This is where the magic happens:
				currentParamClass = JSOperatorHandler.classCombination(currentParamClass, this.classRecon.recognizeClass(paramToken, tokens.get(currentT), command));
			}			
			parameters.add(currentParamClass);	
		}
		Class<?>[] result = new Class<?>[parameters.size()];		
		return parameters.toArray(result);
	}	
	
	
	/*
	 * number vars defined within jsc are always java.land.Double;
	 * new java.lang.Double() however is represented as
	 * javascript enclosed native java type
	 */
	private boolean isPrimitive(Class<?> c)
	{
		return c == Double.class || c == Long.class || c == Integer.class;
	}
	
	/*
	 * @description Checks wether c is either a Java or a JavaScript-Array 
	 *
	private boolean isArray(Class<?> c)
	{
		return c.isArray() || c.getName().equals("sun.org.mozilla.javascript.internal.NativeArray");
	}
*/
}
