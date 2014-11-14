package mayday.jsc.autocomplete;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.jsc.shell.ToolBox;

/**
 * CellRenderer for nicer-looking ChoiceChooser-Choice-List
 *
 * @author Tobias Ries
 * @version 1.0
 * @see JSCellRenderer
 */
@SuppressWarnings("serial")
public class JSCellRenderer extends JLabel implements ListCellRenderer
{	
	private ImageIcon icoMethod, icoSuperMethod;
	private ImageIcon icoField, icoSuperField;
	private ImageIcon icoPackage;
	private ImageIcon icoDefault;

	/**
	 * Instantiates the CellRenderer while loading all necessary images	 
	 */
	public JSCellRenderer()
	{
		this.icoMethod = ToolBox.loadImage("mayday/jsc/icoMethod.png");			
		this.icoField = ToolBox.loadImage("mayday/jsc/icoField.png");
		this.icoSuperMethod = ToolBox.loadImage("mayday/jsc/icoSuperMethod.png");			
		this.icoSuperField = ToolBox.loadImage("mayday/jsc/icoSuperField.png");
		this.icoPackage = ToolBox.loadImage("mayday/jsc/icoPackage.png");
		this.icoDefault = ToolBox.loadImage("mayday/jsc/icoOther.png");
	}
		
	/**
	 * Makes the list-entries look sweet
	 * 
	 * @see ListCellRenderer.getListCellRendererComponent
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		this.setOpaque(false);
		if (isSelected)
		{
			setBackground(Color.GRAY);
			setForeground(Color.BLUE);			
		} else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}			
							
		String listText = value.toString();
		if(value.getClass() == JSCompletion.class)
		{
			JSCompletion jsvalue = (JSCompletion)value;
			listText = "<b>"+jsvalue.getShortPrefix()+"</b>" + listText;
			if(jsvalue.isDeprecated())
				listText = "<strike>"+listText+"</strike>";
			if(!jsvalue.getAdditionalInfo().equals(""))
				listText += " : <font color='#353535'><i>"+jsvalue.getAdditionalInfo()+"</i></font>";
			switch(jsvalue.getType())
			{
			case METHOD:
				this.setIcon(icoMethod);
				break;
			case FIELD:
				this.setIcon(icoField);
				break;
			case SUPERMETHOD:
				this.setIcon(icoSuperMethod);
				break;
			case SUPERFIELD:
				this.setIcon(icoSuperField);
				break;
			case PACKAGE:
				this.setIcon(icoPackage);
				break;
			default:
				this.setIcon(icoDefault); 
				break;
			}
		}

		setText("<html>"+listText+"</html>");

		return this;
	}
}
