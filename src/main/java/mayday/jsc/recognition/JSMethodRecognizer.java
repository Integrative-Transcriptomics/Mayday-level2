package mayday.jsc.recognition;

import java.lang.reflect.Method;

/** 
 * Handles recognizing methods 
 *
 * @version 1.0
 * @author Tobias Ries, ries@exean.net
 */
public class JSMethodRecognizer {

	/** 
	 * Recognized a method of a certain class with a certain name and parameters 
	 *
	 * @version 1.0
	 * @param currentClass Class to which the method belongs
	 * @param methodName Name of the method
	 * @param methodParams List of parameter-types which the method should accept
	 * @return Identified method or null if none found 
	 */
	public static Method recognizeMethod(Class<?> currentClass, String methodName, Class<?>[] methodParams)
	{		
		Method[] methods = currentClass.getMethods();
		for(Method m : methods)
		{
			if(m.getName().equals(methodName))
			{				
				Class<?>[] mParams = m.getParameterTypes();
				if(mParams.length == methodParams.length)
				{
					boolean correctMethod = true;
					for(int i = 0; i < mParams.length; i++)
					{						
						if(methodParams[i] == null
								||!mParams[0].isAssignableFrom(methodParams[i]))
						{
							correctMethod = false;
							break;
						}
						
					}
					if(correctMethod)
						return m;
				}
			}
				
		}
		return null;
	}
	
}
