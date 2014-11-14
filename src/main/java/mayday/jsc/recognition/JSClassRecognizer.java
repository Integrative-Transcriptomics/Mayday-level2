package mayday.jsc.recognition;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.Token.TokenType;

/**
 * Provides functions to recognize a Class from a given command String
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSClassRecognizer {
	ScriptEngine engine;
	JSCommandRecognizer cmdRecon;
	
    /** 
     * Instantiates JSClassRecognizer as well as its objects
     *
     * @version 1.0
     * @param eng ScriptEngine is necessary to check EngineScope-Bindings
     * @param recognizer Command-Recognizer to recognize command Strings referring to more then a Class
     */
	public JSClassRecognizer(ScriptEngine eng, JSCommandRecognizer recognizer){
		this.engine = eng;
		this.cmdRecon = recognizer;
	}
    /** 
     * Instantiates JSClassRecognizer, creates new JSCommandRecognizer
     *
     * @version 1.0
     * @param eng ScriptEngine is necessary to check EngineScope-Bindings
     */
	public JSClassRecognizer(ScriptEngine eng){
		this.engine = eng;
		this.cmdRecon = new JSCommandRecognizer(eng);
	}
	
    /** 
     * Recognizes a class from a String
     *
     * @version 1.0
     * @param str String containing the command to be recognized
     * @return class of Object determined by the command (str)
     */
	public Class<?> recognizeClass(String str)
	{		
		String s1 = str.trim();
		Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
		if(!s1.isEmpty() && bindings.containsKey(s1))			
			return bindings.get(s1).getClass();
		
		try
		{
			JSRecognizedCommand recCmd = this.cmdRecon.recognizeCommand(str);	

			if(recCmd.getRecognizedPrefix().equals(""))
				return recCmd.getRecognizedClass();		
			return recCmd.getRecognizedClass().getField(recCmd.getRecognizedPrefix()).getType();
		} catch(Exception exc)
		{
			//Object could not be identified
			return null;
		}																		
	
	}
	
    /** 
     * Recognizes a class within a String, specified by a token
     *
     * @version 1.0
     * @param paramToken Token referring to the class-part in the command
     * @param command String entered by user
     * @return class of Object determined by the command
     */
	protected Class<?> recognizeClass(Token paramToken, String command)
	{
		return this.recognizeClass(paramToken, paramToken, command);
	}
	
    /** 
     * Recognizes a class within a String, specified by two tokens
     *
     * @version 1.0
     * @param paramToken Token referring to the beginning of the class-part in the command
     * @param paramFinalToken Token referring to the end of the class-part in the command
     * @param command String entered by user
     * @return class of Object determined by the command
     */	
	protected Class<?> recognizeClass(Token paramToken, Token paramFinalToken, String command)
	{
			String param = command.substring(paramToken.getStart(), paramFinalToken.getEnd()+1);
			
			if(paramToken.getType() == TokenType.NUMBER)//param is number
			{	
				//distinguish int, double
				Double argD = new Double(param);
				if (argD.doubleValue() == argD.intValue())
					return(Integer.TYPE);
				else
					return(Double.TYPE);					
			}
			else
			{		
				Object maybeParam = null;
				//js-string
				if(paramToken.getType() == TokenType.STRING)
				{					
					return String.class;//Actually this is wrong, as js-Strings aren't j strings, but in order to allow easy use with OverloadedOperators we act like they would be
				}
				else
					maybeParam = this.engine.get(param);

				if(maybeParam != null)
				{						
					if(maybeParam.getClass().getSimpleName().equals("Double")	//js sets any number as double
							&& ((Double)maybeParam).doubleValue() == ((Double)maybeParam).intValue())				
						return(Integer.TYPE);					
					else
						return(maybeParam.getClass());						
				}
				else
					return(this.recognizeClass(param));				
			}
		
	}

}
