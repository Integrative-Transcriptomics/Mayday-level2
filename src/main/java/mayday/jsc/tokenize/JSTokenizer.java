package mayday.jsc.tokenize;

import java.util.Stack;

import mayday.mushell.tokenize.DefaultTokenizer;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Tokenizer;
import mayday.mushell.tokenize.Token.TokenType;

/** 
 * Basically the same as DefaultTokenizer, differences in Brace-Token class
 * and Comment-Syntax as well as whitespace-handling. 
 *
 * @version 1.0
 * @see DefaultTokenizer
 */
public class JSTokenizer extends DefaultTokenizer
{
	protected String punctuation = "[\\.\\,\\;]";
	protected String startOfLineComment = "//";
	protected String startOfMultiLineComment = "/*";
	protected String endOfMultiLineComment = "*/";
	protected Stack<JSBraceToken> openBraces = new Stack<JSBraceToken>(); 		
	
	private static Tokenizer tokenizerInstance;
	
	private JSTokenizer(){};
	
	public synchronized static Tokenizer getInstance()
	{
         if (tokenizerInstance == null)
         {
        	 tokenizerInstance = new JSTokenizer();
         }
         return tokenizerInstance;
     }
	
	public TokenSet tokenize(String text)
	{
		openBraces.clear();
		return super.tokenize(text);
	}

	public Token parseNextToken(String text, int offset)
	{
		if (offset==text.length())
			return null;
			
		
		if (text.length() > offset+2)
		{
			if(text.substring(offset, offset+2).equals(startOfLineComment))
				return collectLineComment(text, offset);
			if(text.substring(offset, offset+2).equals(startOfMultiLineComment))
				return collectMultiLineComment(text, offset);			
		}
		
		String oneChar = text.substring(offset,offset+1);
		
		if(oneChar.matches(whitespace))
			return new Token(offset, 1, TokenType.WHITESPACE);				

		if (oneChar.matches(punctuation))
			return collectPunctuation(text, offset);
		
		if (JSBraceToken.isBrace(oneChar)) {
			JSBraceToken braceT = new JSBraceToken(offset, 1, oneChar);
			if (JSBraceToken.isOpeningBrace(oneChar)) {
				openBraces.push(braceT);
			} else {
				if (openBraces.size()>0 && openBraces.peek().fits(braceT))
					braceT.setPartner(openBraces.pop());
			}
			return braceT;
		}
			
		// number starts here
		if (oneChar.matches(digits)) 
			return collectNumber(text, offset);
		
		if (oneChar.matches(stringDelim)) 
			return collectString(text, offset);
		
		if (oneChar.matches(operators))
			return new Token(offset, 1, TokenType.OPERATOR);

		// now we collect non-whitespace together, i.e. everything but whitespace,punctuation,operators,stringDelim
		// numbers are allowed now also
		int endOffset = collectIdentifier(text, offset);		
		
		TokenType type = TokenType.OBJECT;
		// find out if that was an object name or a function name
		if (endOffset < text.length()-1 && text.charAt(endOffset+1)==startOfCommand)
			type = TokenType.COMMAND;		
		
		return new Token(offset, endOffset-offset+1, type);
	}
	
	protected Token collectMultiLineComment(String text, int offset) {
		int newOffset = offset+1;
		while (newOffset < text.length()-1)
		{
			if(text.substring(newOffset, newOffset+2).equals(endOfMultiLineComment))
				return new Token(offset, newOffset-offset+2, TokenType.COMMENT);
			++newOffset;			
		}
							
		return new Token(offset, ++newOffset-offset, TokenType.COMMENT);
	}
	
	protected int collectIdentifier(String text, int offset) {
		String current;
		int newOffset = offset;
		while (newOffset < text.length() &&
			  !(current = text.substring(newOffset, newOffset+1)).matches(punctuation) &&
			  !current.matches(whitespace) &&
			  !current.matches(operators) &&
			  !current.matches(stringDelim) &&
			  !(current.charAt(0)==startOfCommand) &&
			  !JSBraceToken.isBrace(current)
		) {
			++ newOffset;
		}	
		return newOffset-1;
	}
	
	public static String getStringOfToken(Token t, String text)
	{
		return text.substring(t.getStart(), t.getEnd()+1 );
	}

}
