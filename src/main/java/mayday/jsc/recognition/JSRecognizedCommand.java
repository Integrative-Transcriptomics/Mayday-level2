package mayday.jsc.recognition;

import java.util.HashMap;

/**
 * Holds information about any recognized command.
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSRecognizedCommand
{	
	protected Class<?> cmdClass;
	protected String cmdPrefix;
	protected HashMap<Object, Class<?>> cmdGenMap;
	protected boolean cmdStatic;
	
    /** 
     * Instantiates a JSRecognizedCommand
     *
     * @version 1.0
     * @param clas Class this command refers to
     * @param prefix Entered prefix for this command
     * @param genMap Map containing Type information
     * @param isStatic Determines wether the referred command is static
     */
	public JSRecognizedCommand(Class<?> clas,
			String prefix,
			HashMap<Object, Class<?>> genMap,
			boolean isStatic)
	{
		this.cmdClass = clas;
		this.cmdPrefix = prefix;
		this.cmdGenMap = genMap;
		this.cmdStatic = isStatic;
	}
	
    /** 
     * Getter for recognized class
     *
     * @version 1.0
     * @return recognized class this command refers to
     */
	public Class<?> getRecognizedClass()
	{
		return this.cmdClass;
	}
	
	/** 
     * Getter for this commands prefix
     *
     * @version 1.0
     * @return commands prefix as entered by user
     */
	public String getRecognizedPrefix()
	{
		return this.cmdPrefix;
	}
	
	/** 
     * Getter for type map
     *
     * @version 1.0
     * @return map containing type information
     */
	public HashMap<Object, Class<?>> getGenMap()
	{
		return this.cmdGenMap;
	}
	
	/** 
     * Getter for information wether command is static  
     *
     * @version 1.0
     * @return true if the command is static, false otherwise
     */
	public boolean isStatic()
	{
		return this.cmdStatic;
	}

}
