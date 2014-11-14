package mayday.jsc.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import mayday.jsc.adjustableBehaviour.JSOverloadingOperators;
import mayday.jsc.adjustableBehaviour.JSReplacements;
import mayday.jsc.autocomplete.JSCompletion.CompletionType;
import mayday.jsc.recognition.JSCommandRecognizer;
import mayday.jsc.recognition.JSPackageRecognizer;
import mayday.jsc.recognition.JSRecognizedCommand;
import mayday.jsc.tokenize.JSTokenNavigator;
import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.autocomplete.AutoCompleter;
import mayday.mushell.autocomplete.Completion;
import mayday.mushell.tokenize.TokenSet;

/**
 * AutoCompleter using script engine, replacement-rules and overloading-operators
 * to determine completions for any given input.
 *
 * @author Tobias Ries
 * @version 1.0
 * @see AutoCompleter
 */
public class JSAutoCompleter implements AutoCompleter
{
	private ScriptEngine engine;	//Scriptengine needed to associate JS-vars with Java-Objects	
	private JSCommandRecognizer cmdRecognizer;
	private JSPackageRecognizer packageRecon;
	private JSReplacements replacer;
	private List<String> defaultCompletions;
	private JSOverloadingOperators overloadingOps;
	private JSNativeJS nativeJS;
	
    /** 
     * Instantiates JSAutoCompleter as well as its objects
     *
     * @version 1.0
     * @param engine ScriptEngine, neccessary to access variables of the engine scope
     * @param replace Replacement-rules
     * @param commandRecognizer Recognizes Commands from Strings
     * @param overloadingOperators Rules for applying overloaded operators
     */
	public JSAutoCompleter(ScriptEngine engine,
						   JSReplacements replace,
						   JSCommandRecognizer commandRecognizer,
						   JSOverloadingOperators overloadingOperators)
	{
		this.engine = engine;
		this.replacer = replace;		
		this.cmdRecognizer = commandRecognizer;//new JSCommandRecognizer( this.engine );
		this.packageRecon = new JSPackageRecognizer();
		this.defaultCompletions = this.getDefaultCompletions();
		this.overloadingOps = overloadingOperators;//new JSOverloadingOperators( this.cmdRecognizer.getClassRecognizer() );
		this.nativeJS = new JSNativeJS(this.engine);
	}	
		
    /** 
     * Contains Plugin-specific completions for empty input. 
     *
     * @version 1.0
     * @return List of Strings of available commands on empty input
     */
	public List<String> getDefaultCompletions()
	{
		ArrayList<String> result = new ArrayList<String>();
		
		//Plugin specific commands
		result.add("HELP");		
		result.add("FILE:");
		result.add("INFO:");
		result.add("importClass(Packages.");
		result.add("importPackage(Packages.");		
		result.add("REPLACE:");
		result.add("?REPLACE");
		result.add("!REPLACE:");
		//Rhino specific commands
		result.add("Packages");
		result.add("java");
		//JS specific commands
		result.add("typeof");
		result.add("var");		
		
		return result;
	}
		
    /** 
     * Handles basic completions for a command.
     * These include Plugin-Specific Meta-Commands as well as
     * the content of the engine-scope. 
     *
     * @version 1.0
     * @param command Command to be completed
     * @return List of Completions of available commands on given input
     */
	private List<Completion> baseCompletions(String command)
	{		
		List<Completion> result = new ArrayList<Completion>();
				
		//Commands defined for Plugin
		for(String s : this.defaultCompletions)
			if(s.startsWith(command))
				result.add(JSCompletion.getCompletionFor(command, s.substring(command.length()), null, CompletionType.OTHER, false));
		//Commands defined for Plugin-EOF
		 
		//Commands defined in Engine
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);				
		for(String s : bindings.keySet())
			if(s.startsWith(command))
			{								
				Class<?> c = bindings.get(s).getClass();				
				result.add(JSCompletion.getCompletionFor(command, s.substring(command.length()), c, CompletionType.OTHER, false));
			}
		//Commands defined in Engine-EOF				
		
		return result;
	}
	
    /** 
     * Collects all available Completions for a given command. 
     *
     * @version 1.0
     * @param command Command to be completed
     * @return List of Completions of available commands on given input
     */
	@Override
	public List<Completion> allCompletions(String cmd)
	{					
		List<Completion> result = new ArrayList<Completion>();//Output-Container (return)
		
		/*
		 * Shorten Input to last command.
		 * This is a two-sided blade as it quickens the command preprocessing but
		 * ignores the power of Overloaded-Operators to void normal command-boundaries
		 * (e.g. commata)
		 */
		TokenSet tokens = JSTokenizer.getInstance().tokenize(cmd);
		int fId = JSTokenNavigator.getFirstCmdTokenIndex(tokens, cmd);
		cmd = cmd.substring(tokens.get(fId).getStart());
		if(cmd.startsWith("new "))
			cmd = cmd.substring(4);		
		// Shorten Input to last command - EOF
				
		String actualCmd = this.replacer.applyReplacementRules(cmd);
		actualCmd = this.overloadingOps.applyOperators(actualCmd);								
		try
		{
			JSRecognizedCommand recCmd = this.cmdRecognizer.recognizeCommand(actualCmd);			
			String recclass = recCmd.getRecognizedClass().getName();
						
			if(recclass.endsWith("javascript.NativeJavaPackage")//endsWidth is used due to variation within the exact package name (sun.org.mozilla/org.mozilla). Only looking at the simple name is obviously insufficient.
					|| recclass.endsWith("javascript.NativeJavaTopPackage"))
				result.addAll(this.packageCompletions(actualCmd));					
			else if(recclass.startsWith("sun.org.mozilla.javascript.gen.c"))//JS Method
				return result;
			else
				result.addAll(this.nativeJS.getCompletions(actualCmd, recCmd));
				
			//"normal" completion	
			if (result.isEmpty())
				result.addAll(JSClassFieldsAndMethods.getFieldsAndMethods(recCmd, cmd));			
		} catch(IllegalArgumentException e)//Empty Result
		{} catch(NoSuchElementException e)
		{				
			cmd = e.getMessage();				
			result.addAll(this.baseCompletions(cmd));//Default Completions					
		}	
		
		return result;
	}

    /** 
     * Collects completions for an Input referring to a package or uninitialized class 
     *
     * @version 1.0
     * @param command Input referring to a Package or uninitialized class
     * @return List of Packages or Static fields
     */
	private Collection<? extends Completion> packageCompletions(String actualCmd)
	{
		ArrayList<Completion> result = new ArrayList<Completion>();
		
		TokenSet ts = JSTokenizer.getInstance().tokenize(actualCmd);						
		int first = JSTokenNavigator.getFirstCmdTokenIndex(ts, actualCmd);
		String cutcmd = actualCmd.substring(ts.get(first).getStart());
		result.addAll(this.staticCompletions(cutcmd));
		result.addAll(this.packageRecon.packageCompletions(cutcmd));
		
		return result;
	}

    /** 
     * Collects all available static Completions for uninitialized classes  
     *
     * @version 1.0
     * @param command Input referring to an uninitialized class
     * @return List of Static fields of class
     */
	private List<Completion> staticCompletions(String cmd)
	{		
		Class<?> c;
		if(cmd.startsWith("Packages."))
			cmd = cmd.substring(9);
		String prefix = "";
		try {
			c = Class.forName(cmd);						
			JSRecognizedCommand recCmd = new JSRecognizedCommand(c, prefix, new HashMap<Object, Class<?>>(), true);			
			return JSClassFieldsAndMethods.getFieldsAndMethods(recCmd, cmd);
			
		} catch (ClassNotFoundException e) {
			int i = cmd.lastIndexOf('.');
			boolean statik;
			while(i > 0 && i < cmd.length())
			{		
				if(cmd.contains("("))					
				{
					i = cmd.indexOf('(');
					statik = false;
				}
				else
					statik = true;
				String className = cmd.substring(0,i);				
				try
				{
					c = Class.forName(className);					
					prefix = cmd.substring(i);
					JSRecognizedCommand recCmd = this.cmdRecognizer.recognizeFromTokenSet_rest(
							1,
							JSTokenizer.getInstance().tokenize(prefix),			
							c,
							statik,
							prefix);
					return JSClassFieldsAndMethods.getFieldsAndMethods(recCmd, cmd);
					
				} catch (ClassNotFoundException e1)
				{					
					i = className.lastIndexOf('.');//Previous .
				}
			}
			return new ArrayList<Completion>();
		}					
	}
	
    /** 
     * Completes any given command for which a specific completion exists 
     *
     * @version 1.0
     * @param command Command to be completed
     * @return Specific Completion for given input
     */
	@Override
	public Completion complete(String command)
	{
		List<Completion> all = this.allCompletions(command);
		if(all.size() == 1)
			return all.get(0);
		return new Completion("",command);
	}
			

}
