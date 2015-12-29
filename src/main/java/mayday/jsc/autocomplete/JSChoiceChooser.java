package mayday.jsc.autocomplete;

import mayday.core.settings.typed.BooleanSetting;
import mayday.jsc.shell.JSSettings;
import mayday.mushell.InputField;
import mayday.mushell.autocomplete.DefaultChoiceChooser;

/**
 * Nice-looking ChoiceChooser allows chagrinless choosing chilly choices for chunks of commands
 *
 * @author Tobias Ries
 * @version 1.0
 * @see DefaultChoiceChooser
 */
@SuppressWarnings("serial")
public class JSChoiceChooser extends DefaultChoiceChooser
{					
    /** 
     * Instantiates JSAutoCompleter, but as setting-specific ChoiceChooser will be used
     * getChoiceChooser should be called instead.
     *
     * @version 1.0
     * @param input InputField to which the choice chooser should be applied
     * @see getChoiceChooser
     */
	private JSChoiceChooser(InputField input)
	{
		super(input);
	}
	
    /** 
     * Creates the kind of ChoiceChooser specified in the settings.
     *
     * @version 1.0
     * @param input InputField to which the choice chooser should be applied
     * @return ChoiceChooser for given InputField
     */
	public static DefaultChoiceChooser getChoiceChooser(InputField input)
	{
		if(((BooleanSetting)JSSettings.getInstance().getSettings().getChild("Classic Autocompletion (Restart required)", true)).getBooleanValue())
			return new DefaultChoiceChooser(input);
		else
			return new JSChoiceChooser(input);
	}

	/** 
     * Initiates list for displaying choices
     *
     * @version 1.0
     */
	protected void initList()
	{
		super.list.setCellRenderer(new JSCellRenderer());
		super.initList();
	}
	
}
