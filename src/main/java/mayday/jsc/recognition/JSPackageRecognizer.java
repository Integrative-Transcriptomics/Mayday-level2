package mayday.jsc.recognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.jsc.autocomplete.JSCompletion;
import mayday.mushell.autocomplete.Completion;

/**
 * Provides functions to recognize a Package from a given command String
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSPackageRecognizer
{
	private HashMap<String,List<String>> packageMap;//List of names of all available packages
	
	public JSPackageRecognizer()
	{
		this.packageMap = this.getPackageMap();
	}
		
	/**
	 * Will only be called during initialization. By iterating over
	 * all available Packages a HashMap is filled which assigns
	 * always the next level packages to the String-key defining
	 * the corresponding prior Package. 
	 *
	 * @author Tobias Ries
	 * @version 1.0 
     * @return HashMap of Packages
	 */
	private HashMap<String,List<String>> getPackageMap()
	{
		HashMap<String,List<String>> result = new HashMap<String,List<String>>();
		
		List<String> completions = new ArrayList<String>();
		completions.add("Packages");
		result.put("",completions);
		
		for(Package p : Package.getPackages())
		{
			String pkgName = p.getName();
			String[] keys = pkgName.split("[\\.]");
			if(keys.length <= 0)
				continue;												
			
			for(int k = 0; k < keys.length; k++)
			{
				String value = keys[k];
				String key = "Packages.";				
				
				for(int k2 = 0; k2 < k; k2++)
					key += keys[k2]+".";
				
				if(result.containsKey(key))
				{
					List<String> l = result.get(key);
					if(!l.contains(value))
						l.add(value);
				}
				else
				{
					completions = new ArrayList<String>();
					completions.add(value);
					result.put(key, completions);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns a list of packages based on the given command
	 *
	 * @author Tobias Ries
	 * @version 1.0 
	 * @param command Input defining only the (incoplete) Name of a package
     * @return List of Package-Completions
	 */
	public List<Completion> packageCompletions(String command)
	{
		List<Completion> result = new ArrayList<Completion>();
		
		if(!command.startsWith("Packages."))
			command = "Packages."+command;
		
		String key = "";				
		int endOfPrefix = command.lastIndexOf('.')+1;
		if(endOfPrefix >= 0)					
			key= command.substring(0,endOfPrefix);
		
		String value = command.substring(endOfPrefix);
		
		if(this.packageMap.containsKey(key))			
			for(String s : this.packageMap.get(key))
				if(s.startsWith(value))				
					result.add(new JSCompletion(s.substring(value.length()), value, "", JSCompletion.CompletionType.PACKAGE, false));		
		
		return result;
	}

}
