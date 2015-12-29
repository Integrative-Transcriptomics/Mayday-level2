package mayday.jsc.adjustableBehaviour;

import java.util.ArrayList;

/** 
 * Handles auto-replacing given regexes on every input. 
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
public class JSReplacements
{
	private ArrayList<String[]> replacementRules;
	
	public JSReplacements()
	{
		this.replacementRules = new ArrayList<String[]>();
		
	}
	
	
	/*
	 * Adds new replacementrule
	 * (removes previously defined rule for same regex)
	 */
	public void defineReplacementRule(String rule)
	{
		String[] r = rule.split("=>");		
		r[0] = r[0].trim();r[1] = r[1].trim();
		this.removeRuleFor(r[0]);
		this.replacementRules.add(r);		
	}
	
	public String applyReplacementRules(String command)
	{
		for(String[] s : this.replacementRules)
			command = command.replaceAll(s[0], s[1]);
		return command;
	}
	
	public boolean removeRuleFor(String command)
	{		
		boolean success = false;
		for(String[] s : this.replacementRules)
			if(s[0].equals(command))
			{
				this.replacementRules.remove(s);
				success = true;
				break;
			};
		return success;
	}
	
	public ArrayList<String[]> getRules()
	{
		return this.replacementRules;
	}

}
