/**
 * 
 */
package mayday.expressionmapping.exceptions;

import java.io.IOException;

/**
 * @author Stephan Gade
 *
 */
@SuppressWarnings("serial")
public class MalformedInputException extends IOException {

    
    /**
     * @param message
     */
    public MalformedInputException(String message) {
	
	super(message);
	
    }


}
