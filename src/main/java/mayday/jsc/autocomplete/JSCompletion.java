package mayday.jsc.autocomplete;

import mayday.mushell.autocomplete.Completion;

/**
 * Contains a completion for a command as used in JSAutoCompleter.
 * Also contains a prefix, type, additionalInfo and maybe marked
 * as deprecated.
 *
 * @author Tobias Ries
 * @version 1.0
 * @see Completion
 */
public class JSCompletion extends Completion
{
	private String additionalInfo;
	private String shortPrefix;
	private CompletionType type;
	private boolean deprecated;
	
    /** 
     * Instantiates a JSCompletion as well as its objects
     *
     * @version 1.0
     * @param completion String specifying the completion
     * @param prefixReplacement contains prefix, prefix is substring of completion and command to be completed
     * @param AdditionalInfo Additional information
     * @param typ Type of this completion (FIELD, SUPERFIELD, METHOD, SUPERMETHOD, PACKAGE, OTHER)
     * @param deprecate boolean determining if command is deprecated
     */
	public JSCompletion(String completion,
						String prefixReplacement,
						String AdditionalInfo,
						CompletionType typ,
						boolean deprecate) {
		super(completion, prefixReplacement);		
		this.type = typ;
		this.additionalInfo = AdditionalInfo;
		this.shortPrefix = prefixReplacement.matches("[^/.]*") ? 
				prefixReplacement : prefixReplacement.substring(prefixReplacement.lastIndexOf('.')+1);
		this.deprecated = deprecate;
	}
	
    /** 
     * Different types of completions, especially useful for
     * adding icons during visualization
     *
     * @version 1.0
     */
	public static enum CompletionType
	{
		FIELD, SUPERFIELD, METHOD, SUPERMETHOD, PACKAGE, OTHER
	}
	
    /**
     * Getter for additionalInfo 
     * 
     * @version 1.0
     * @return Additional Information for this completion (e.g. Class)
     */
	public String getAdditionalInfo()
	{
		return this.additionalInfo;
	}
    /**
     * Getter for completion type
     * 
     * @version 1.0
     * @return type of this completion (FIELD, SUPERFIELD, METHOD, SUPERMETHOD, PACKAGE, OTHER)
     */
	public CompletionType getType()
	{
		return this.type;
	}
    /**
     * Getter for prefix
     * 
     * @version 1.0
     * @return prefix, substring of completion and command to be completed
     */
	public String getShortPrefix()
	{
		return this.shortPrefix;
	}	
    /**
     * Adds next char to prefix, calls super-method
     * 
     * @version 1.0
     */
	public void pop()
	{
		this.shortPrefix+=super.getCompletion().charAt(0);
		super.pop();
	}
    /**
     * Getter for deprecation-flag
     * 
     * @version 1.0
     * @return true if completion is deprecated, false otherwise
     */
	public boolean isDeprecated()
	{
		return this.deprecated;
	}		
	
    /**
     * Creates a new completion, generates additional information through name of given class
     * 
     * @version 1.0
     * @param command Command to be completed by new completion
     * @param matchingComp String specifying the completion
     * @param clazz Class of matching completion which is used to generate additional information
     * @param typ Type of this completion (FIELD, SUPERFIELD, METHOD, SUPERMETHOD, PACKAGE, OTHER)
     * @param deprecate boolean determining if command is deprecated
     * @return A brand new completion
     */
	public static JSCompletion getCompletionFor(String command,
												String matchingComp,
												Class<?> clazz,
												CompletionType type,
												boolean deprecated)
	{		
		String additionalInfo = "";
		if(clazz != null)
		{
			additionalInfo += clazz.getSimpleName();
			if(clazz.getPackage() != null)
				additionalInfo += " (" + clazz.getPackage().getName() + ")";
		}
		return new JSCompletion(matchingComp, command, additionalInfo, type, deprecated);
	}
}
