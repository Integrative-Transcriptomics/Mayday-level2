package mayday.vis3.plots.chromogram;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;

public class ChromogramDisplaySetting extends HierarchicalSetting
{
	private BooleanSetting renderText;
	private IntSetting columnsSetting; 
	
	
	public ChromogramDisplaySetting() 
	{
		super("Display Settings");
		renderText=new BooleanSetting("Render Text","Render text in chromogram cells",false);
		columnsSetting=new IntSetting("Number of columns","The number of columns to be displayed in the chromgram",30,1,200,true,true);
		
		addSetting(renderText).addSetting(columnsSetting);
	}


	/**
	 * @return the renderText
	 */
	public BooleanSetting getRenderText() {
		return renderText;
	}


	/**
	 * @param renderText the renderText to set
	 */
	public void setRenderText(BooleanSetting renderText) {
		this.renderText = renderText;
	}


	/**
	 * @return the columnsSetting
	 */
	public IntSetting getColumnsSetting() {
		return columnsSetting;
	}


	/**
	 * @param columnsSetting the columnsSetting to set
	 */
	public void setColumnsSetting(IntSetting columnsSetting) {
		this.columnsSetting = columnsSetting;
	}
	
	@Override
	public ChromogramDisplaySetting clone() 
	{
		return (ChromogramDisplaySetting)reflectiveClone();
	}
	
	
}
