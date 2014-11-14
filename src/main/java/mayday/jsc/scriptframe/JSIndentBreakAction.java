package mayday.jsc.scriptframe;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/** 
 * Simple line-indent-action, only based on previous line indent.
 * Could be extended for layer-dependent auto-indent. 
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
@SuppressWarnings("serial")
public class JSIndentBreakAction extends TextAction
{		
    
	public JSIndentBreakAction()
	{
		super(DefaultEditorKit.insertBreakAction);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JTextComponent textC = super.getTextComponent(e);
		if (textC == null)
			return;			
		
		Document document = textC.getDocument();
		Element root = document.getDefaultRootElement();

		//Determine line					
		Element line = root.getElement(
							root.getElementIndex(
								textC.getSelectionStart() ));
		//Determine line - eof
		
		try
		{
			//Get the text of line
			int start = line.getStartOffset();				
			int length = line.getEndOffset() - start;
			String text = document.getText(start, length);				
			//Get the text of line - eof

			//Get leading-whitepace-count			
			for (int offset = 0; offset < length; offset++)
			{
				char c = text.charAt(offset);
				if(c != ' '	&& c != '\t')
				{
					textC.replaceSelection("\n" + text.substring(0, offset));
					break;
				}
			}					
			//Get leading-whitepace-count - eof			
		}
		catch(BadLocationException ble) {}
	}
}