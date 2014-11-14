package mayday.jsc.tokenize;

import mayday.mushell.tokenize.BraceToken;
import mayday.mushell.tokenize.Token;

/**
 * Extends the BraceToken for easy access to partnerbrace
 * and determination wether brace is opening or closing.
 *
 * @author Tobias Ries
 * @version 1.0
 * @see BraceToken
 */
public class JSBraceToken extends BraceToken
{

	/**
	 * Instantiates by simply calling super-constructor
	 *
	 * @version 1.0
	 * @param Offset offset of token
	 * @param Length length of token
	 * @param Char Corresponding brace as String
	 * @see BraceToken
	 */
	public JSBraceToken(int Offset, int Length, String Char)
	{
		super(Offset, Length, Char);
	}
	
	/**
	 * Getter for partner of this brace
	 *
	 * @version 1.0
	 * @return Currents braces partner
	 */
	public Token getPartner()
	{
		return super.partner;
	}
	
	/**
	 * Determines wether current brace is opening or closing brace
	 *
	 * @version 1.0
	 * @return true if this is a opening brace, false otherwise
	 */
	public boolean isOpeningBrace()
	{
		return super.isOpeningBrace(super.myChar+"");
	}
	

}
