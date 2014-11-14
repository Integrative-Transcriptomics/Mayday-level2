package mayday.jsc.autocomplete;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import mayday.jsc.recognition.JSRecognizedCommand;
import mayday.mushell.autocomplete.Completion;

/**
 * Offers functionality for getting all Fields and Methods
 * of class from recognized commands, prefix of Field-/Methodnam
 * maybe set as well.
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSClassFieldsAndMethods
{

    /** 
     * Returns a list of completions for all Fields and Methods of
	 * a given RecognizedClass
     *
     * @version 1.0
     * @param rec RecognizedClass to look at for fields and methods, may also define a prefix
     * @param command the command to be replaced by the completion
     * @return list of Completions for all Fields and Methods of a given RecognizedClass
     */
	public static List<Completion> getFieldsAndMethods(JSRecognizedCommand rec, String command)
	{		
		List<Completion> result = new ArrayList<Completion>();		
		result.addAll( getMethods( rec, command ) );
		result.addAll( getFields( rec, command ) );
		
		return result;
	}

    /** 
     * Returns a list of completions for all Methods of
	 * a given RecognizedClass, marking deprecation
     *
     * @version 1.0
     * @param rec RecognizedClass to look at for methods, may also define a prefix
     * @param command the command to be replaced by the completion
     * @return list of Completions for all Methods of a given RecognizedClass
     */
	public static List<Completion> getMethods(JSRecognizedCommand rec, String command)
	{		
		List<Completion> result = new ArrayList<Completion>();
		
		//Set value for substringing of fitting methods-names
		int preCut = rec.getRecognizedPrefix().length();
	
		for (Method m : rec.getRecognizedClass().getMethods())
		{		
			if(!isValidCompletion(m, rec))
				continue;			
			String method = m.getName().substring(preCut) + "(";
			
			// Add params
			Class<?>[] params = m.getParameterTypes();
			for(Class<?> param : params)			
				method += param.getSimpleName()+", ";
			if(method.endsWith(", "))
				method = method.substring(0, method.length()-2);//Delete last ','
			method += ")";
			// Add params - EOF
						
			Object returnType = m.getGenericReturnType();															
			Class<?> returnT = (rec.getGenMap().containsKey(returnType)) ?
					rec.getGenMap().get(returnType) : m.getReturnType();							
			
			result.add(JSCompletion.getCompletionFor(command, method, returnT,
					m.getDeclaringClass() == rec.getRecognizedClass() ? JSCompletion.CompletionType.METHOD
							: JSCompletion.CompletionType.SUPERMETHOD,
						isDeprecated(m)));
		}
		
		return result;
	}
	
    /** 
     * Returns a list of completions for all Fields of
	 * a given RecognizedClass, marking deprecation
     *
     * @version 1.0
     * @param rec RecognizedClass to look at for fields, may also define a prefix
     * @param command the command to be replaced by the completion
     * @return list of Completions for all Fields of a given RecognizedClass
     */
	public static List<Completion> getFields(JSRecognizedCommand rec, String command)
	{
		List<Completion> result = new ArrayList<Completion>();
		
		//Set value for substringing of fitting methods-names
		int preCut = rec.getRecognizedPrefix().length();
		
		for (Field f : rec.getRecognizedClass().getFields())
		{
			if(!isValidCompletion(f, rec))
				continue;			
			result.add(JSCompletion.getCompletionFor(command, f.getName().substring(preCut), f.getClass(),
					f.getDeclaringClass() == rec.getRecognizedClass() ? JSCompletion.CompletionType.FIELD
							: JSCompletion.CompletionType.SUPERFIELD,
					isDeprecated(f)));							
		}	
		
		return result;
	}
	
	
    /** 
     * Looks at an elements annotation to check if it is deprecated
     *
     * @version 1.0
     * @param e Element which may be deprecated
     * @return true if element is deprecated, false otherwise
     */
	private static boolean isDeprecated(AnnotatedElement e)
	{
		return e.getAnnotation(Deprecated.class) != null;
	}
	  
    /** 
     * Checks if a member is a valid completion for a given recognized command
     * by checking for private, static and prefix. 
     *
     * @version 1.0
     * @param e Possibly valid completion
     * @param rec RecognizedCommand to be completed
     * @return true if member is valid, false otherwise
     */
	private static boolean isValidCompletion(Member e, JSRecognizedCommand rec)
	{
		return e.getName().startsWith(rec.getRecognizedPrefix())
			&& !Modifier.isPrivate(e.getModifiers())
			&& (!rec.isStatic() || Modifier.isStatic(e.getModifiers()));
	}
}
