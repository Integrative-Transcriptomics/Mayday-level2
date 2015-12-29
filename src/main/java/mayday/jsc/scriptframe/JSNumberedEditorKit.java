package mayday.jsc.scriptframe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.rtf.RTFEditorKit;

/** 
 * Handles line-numbering. Provides a border for displaying the numbers.
 * Numbers are retrieved via a paragraph view. (Simple numbering by height
 * of a line wouldn't be sufficient as the editor auto-breaks lines).  
 *
 * @version 1.0
 * @author Tobias Ries, mail@exean.net
 */
@SuppressWarnings("serial")
class JSNumberedEditorKit extends RTFEditorKit
{
	private NumberedParagraphView view;
	private ViewFactory stdFac;
	
	public JSNumberedEditorKit()
	{
		this.stdFac = super.getViewFactory();
	}
	
    /** 
     * Provides NumberedParagraphView for Paragraph-Elements enabling line numbering 
     *
     * @version 1.0
     * @return ViewFactory
     */
	public ViewFactory getViewFactory()
	{			
		return new ViewFactory()
		{
			@Override
			public View create(Element elem)
			{
				String kind = elem.getName();	
				if (kind != null && kind.equals(AbstractDocument.ParagraphElementName))
				{
					view = new NumberedParagraphView(elem);
					return view;
				}
				return stdFac.create(elem);
			}	        
		};
	}	
	
	public AbstractBorder getNumberedBorder()
	{
		return new NumberedBorder();
	}

	private class NumberedParagraphView extends ParagraphView
	{	    
		public NumberedParagraphView(Element e)
		{
			super(e);	        
		}

	    /** 
	     * Counts lines by counting views (a view for
	     * each row, multiple rows may form a single line!). 
	     *
	     * @version 1.0
	     * @return Array with length same as existing lines, each entry specifying amount of rows this line takes
	     */
		public int[] getLineCount()
		{	        	    	    	
			View parent = this.getParent();
			if(parent == null)
				return new int[] {0};
			int count = parent.getViewCount();	       
			int[] result = new int[count];

			for (int line = 0; line < count; line++)	        	
				result[line] = parent.getView(line).getViewCount()-1;	        

			return result;
		}
	}

    /** 
     * Border for displaying line-numbering 
     *
     * @version 1.0
     */
	private class NumberedBorder extends AbstractBorder
	{
		private int DEFAULT_NUMBER_WIDTH = 30;
		private int DEFAULT_NUMBER_HEIGHT = 14;//Exact height for each line available through rect in ParagraphView PaintChild
		private Insets insets;			

		public NumberedBorder()
		{			
			this.insets = new Insets(3, DEFAULT_NUMBER_WIDTH+2, 2, 2);			
		}

		public void paintBorder(Component c,
				Graphics g,
				int x,
				int y,
				int width,
				int height)
		{			
			Color originalColor = g.getColor();			
			g.setColor(Color.LIGHT_GRAY);

			/* number of rows of each line => length ≃= number of lines */
			int[] lines;
			if(view != null)
				lines = view.getLineCount();
			else
				lines = new int[]{0};
			int vertPos = 0;	
			for(int j = 0; j < lines.length; j++)
			{										
				g.drawString((j+1)+"", 5, vertPos+=DEFAULT_NUMBER_HEIGHT);			    		    										
				vertPos+=lines[j]*DEFAULT_NUMBER_HEIGHT;																		
			}			

			g.setColor(originalColor);
		}		

		public Insets getBorderInsets(Component c)
		{
			return this.insets;
		}
	}

}
