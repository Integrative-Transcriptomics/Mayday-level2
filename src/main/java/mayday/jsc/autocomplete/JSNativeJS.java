package mayday.jsc.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import mayday.jsc.autocomplete.JSCompletion.CompletionType;
import mayday.jsc.recognition.JSRecognizedCommand;
import mayday.mushell.autocomplete.Completion;


/*
 * Specific JS-Objects like string cannot be resolved in java.
 * This is a q&d attempted to make autocomp work on them.
 * For a better solution some sort of js-reflection would be required.
 */
public class JSNativeJS {
	private List<String> stringCompletions;
	private List<String> arrayCompletions;
	private ScriptEngine engine;
	
	public JSNativeJS(ScriptEngine e)
	{
		this.engine = e;
		
		this.stringCompletions = new ArrayList<String>();
		
		this.stringCompletions.add("length");
		this.stringCompletions.add("constructor");
		this.stringCompletions.add("anchor(String anchor)");
		this.stringCompletions.add("big()");
		this.stringCompletions.add("blink()");
		this.stringCompletions.add("bold()");
		this.stringCompletions.add("charCodeAt(int pos)");
		this.stringCompletions.add("concat(String con)");
		this.stringCompletions.add("fixed()");
		this.stringCompletions.add("fontcolor(String col)");
		this.stringCompletions.add("fontsize(int i)");
		this.stringCompletions.add("indexOf(String s)");
		this.stringCompletions.add("italics()");
		this.stringCompletions.add("lastIndexOf(String s)");
		this.stringCompletions.add("link(String link)");
		this.stringCompletions.add("match(String regex)");		
		this.stringCompletions.add("search(String regex)");
		this.stringCompletions.add("sub()");
		this.stringCompletions.add("substring(int start, int end)");
		this.stringCompletions.add("substr(int start, int end)");
		this.stringCompletions.add("sup()");
		this.stringCompletions.add("slice(int start, int end)");
		this.stringCompletions.add("small()");
		this.stringCompletions.add("split(String s)");
		this.stringCompletions.add("strike()");
		this.stringCompletions.add("toLowerCase()");
		this.stringCompletions.add("toUpperCase()");
		this.stringCompletions.add("replace(String regex,String repl)");	
		
		this.arrayCompletions = new ArrayList<String>();
		
		this.arrayCompletions.add("length");
		this.arrayCompletions.add("constructor");
		this.arrayCompletions.add("concat(Array arr)");
		this.arrayCompletions.add("join(String glue)");
		this.arrayCompletions.add("pop()");
		this.arrayCompletions.add("push(Object o)");
		this.arrayCompletions.add("reverse()");
		this.arrayCompletions.add("shift()");
		this.arrayCompletions.add("slice(int index)");
		this.arrayCompletions.add("slice(int from, int to)");
		this.arrayCompletions.add("splice(int startIndex, int amount[, Object val1[, Object val2[, ...]]])");
		this.arrayCompletions.add("sort()");
		this.arrayCompletions.add("unshift(Object 1[, Object 2[,...]])");
	}

	private List<Completion> getCompletions(String command, List<String> src)
	{
		List<Completion> result = new ArrayList<Completion>();
				
		for(String s : src)
			if(s.startsWith(command))
				result.add(JSCompletion.getCompletionFor(command, s.substring(command.length()), null, CompletionType.OTHER, false));
				
		return result;			
	}

	public Collection<? extends Completion> getCompletions(String actualCmd,
			JSRecognizedCommand recCmd)
	{
		int p = recCmd.getRecognizedPrefix().length();
		int a = actualCmd.length();
		String clean = actualCmd;
		if(p != a)
			clean = clean.substring(0, a-p-1);
		if(!clean.isEmpty())		
			try {						
				if(recCmd.getRecognizedClass().getSimpleName().equals("NativeArray")//Cannot get directly to NativeArray.class
						&& engine.eval(clean+".constructor==Array().constructor").equals(true))//Only do this after classcheck!
					return this.getCompletions(recCmd.getRecognizedPrefix(), this.arrayCompletions);
				if(recCmd.getRecognizedClass() == String.class
						&& engine.eval("typeof "+clean).equals("string"))					
					return this.getCompletions(recCmd.getRecognizedPrefix(), this.stringCompletions);			
			} catch (ScriptException e)
			{
				e.printStackTrace();
			};

		return new ArrayList<Completion>();
	}

}
