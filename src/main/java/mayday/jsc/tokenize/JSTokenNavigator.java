package mayday.jsc.tokenize;

import mayday.jsc.recognition.JSOperatorHandler;
import mayday.mushell.tokenize.Token;
import mayday.mushell.tokenize.TokenSet;
import mayday.mushell.tokenize.Token.TokenType;

/**
 * Provides methods for easy navigation within tokensets
 * referring to JavaScript-commands
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSTokenNavigator 
{
		
	/**
	 * Returns the index of the first token belonging to the current command
	 *
	 * @version 1.0
	 * @param tokens the complete tokenset for the command string
	 * @param command the command string corresponding to the tokenset
	 * @return index of the first token belonging to the current command
	 */
	public static int getFirstCmdTokenIndex(TokenSet tokens, String command)
	{
		int currentT = tokens.size() - 1;		//Index of last token in set
		Token cmd;	
		int acceptableT = currentT;
		while(currentT >= 0)
		{
			cmd = tokens.get(currentT);
			
			if(cmd.getType() == TokenType.COMMAND
					|| cmd.getType() == TokenType.OBJECT)
			{
				acceptableT = currentT;
				currentT--;
			}
			else if(cmd.getType() == TokenType.WHITESPACE//Skip whitespaces
					|| (cmd.getType() == TokenType.TEXT
							&& JSTokenizer.getStringOfToken(cmd, command).matches("[\\s]+")))
				currentT--;
			else if(cmd.getClass() == JSBraceToken.class)//Braces
			{
				JSBraceToken bt = (JSBraceToken)cmd;
				if(!bt.isOpeningBrace())
				{
					acceptableT = currentT;
					currentT = tokens.indexOf(bt.getPartner())-1;// Index to token b e f o r e brace partner
				}
				else
				{
					currentT = acceptableT;
					break;
				}
			}
			else if(JSTokenizer.getStringOfToken(cmd, command).equals(".")
					&& (currentT < 1
							|| !JSTokenizer.getStringOfToken(cmd = tokens.get(currentT-1), command).equals(".")))
				currentT--;
			else if(cmd.getType() == TokenType.STRING)
				break;
			else
			{
				currentT = acceptableT;
				break;
			}
		}
		
		return Math.max(0, currentT);
	}
	
	/**
	 * Returns the index of the last token belonging to the current command
	 *
	 * @version 1.0
	 * @param tokens TokenSet containing the command
	 * @param command The input command String
	 * @param currentT Index of first Token belonging to searched command
	 * @return index of the last token belonging to the current command
	 */
	public static int getLastCmdTokenIndex(TokenSet tokens, String command, int currentT)
	{
		Token cmd;		
		int result = currentT;
		while(result < tokens.size())
		{			
			cmd = tokens.get(result);
		
			if(cmd.getType() == TokenType.COMMAND
					|| cmd.getType() == TokenType.OBJECT
					|| cmd.getType() == TokenType.STRING
					|| cmd.getType() == TokenType.NUMBER
					|| cmd.getType() == TokenType.WHITESPACE					
					|| JSTokenizer.getStringOfToken(cmd, command).matches("[\\s]"))
				result++;
			else if(cmd.getType() == TokenType.PUNCTUATION
					&& cmd.getClass() == JSBraceToken.class)//Braces
			{
				JSBraceToken bt = (JSBraceToken)cmd;
				if(bt.isOpeningBrace() && tokens.indexOf(bt.getPartner()) >= 0)
				{
					result = tokens.indexOf(bt.getPartner())+1;// Index of token a f t e r brace partner
				}
				else
				{
					result--;
					break;
				}
			}				
			else if(JSTokenizer.getStringOfToken(cmd, command).equals(".")
					&& (result >= tokens.size() - 1
							|| !JSTokenizer.getStringOfToken(tokens.get(result+1), command).equals(".")))
				result++;
			else
			{
				result--;
				break;
			}			
		}
		result = Math.max(currentT, result);		
		return Math.min(result, tokens.size()-1);
	}	
	
	/**
	 * Returns the index of the last token belonging to a calculation.
	 * CurrentT has to be index of first (and highest ranking) operator
	 * in accordance to which it is determined which following operators
	 * are counted. 
	 *
	 * @version 1.0
	 * @param tokens TokenSet containing the command
	 * @param command The input command String
	 * @param currentT Index of first Token belonging to searched command
	 * @return index of the last token belonging to the current calculation
	 */
	public static int getLastCalcTokenIndex(TokenSet tokens, String command, int currentT)
	{					
		Token token = tokens.get(currentT-1);						
		String firstOp = JSTokenizer.getStringOfToken(token, command); //None may be higher ranked
		
		int result = currentT;			
		for(; result < tokens.size(); result++)
		{			
			token = tokens.get(result);
			
			if(token.getType() == TokenType.OPERATOR)
			{
				if(JSOperatorHandler.hasAhigherPrecedence(JSTokenizer.getStringOfToken(token, command),firstOp))
					return --result;
			}
			else if(token.getType() == TokenType.WHITESPACE//Skip whitespaces
					|| (token.getType() == TokenType.TEXT
							&& JSTokenizer.getStringOfToken(token, command).matches("[\\s]+")))
				continue;						
			else if(token.getType() == TokenType.PUNCTUATION)					
			{
				if(token.getClass() == JSBraceToken.class)//Braces
				{
					JSBraceToken bt = (JSBraceToken)token;
					if(bt.isOpeningBrace() && tokens.indexOf(bt.getPartner()) >= 0)
					{
						result = tokens.indexOf(bt.getPartner())+1;// Index of token a f t e r brace partner
					}
					else
					{
						return --result;					
					}
				}
				else
				{
					return --result;					
				}
			}	
			else
			{
				result = getLastCmdTokenIndex(tokens, command, result);			
			}			
		}
		result = Math.max(currentT, result);		
		return Math.min(result, tokens.size()-1);
	}
	
}
