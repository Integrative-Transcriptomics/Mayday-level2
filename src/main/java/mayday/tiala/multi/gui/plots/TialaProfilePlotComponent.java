package mayday.tiala.multi.gui.plots;

import java.awt.Color;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.profile.ProfilePlotComponent;

/**
 * @author jaeger
 *
 */
public class TialaProfilePlotComponent extends ProfilePlotComponent implements SettingChangeListener {

	protected AlignmentStore store;
	private Color selectionColor = Color.RED;
	protected int id;
	
	/**
	 * @param id 
	 * @param store
	 */
	public TialaProfilePlotComponent(int id, AlignmentStore store) {
		super();
		this.store = store;
		this.id = id;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6429574200137582603L;
	
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		super.settings.addChangeListener(this);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if(!super.settings.getSelectionColor().getColorValue().equals(selectionColor)) {
			store.getSettings().setSelectionColor(id, super.settings.getSelectionColor().getColorValue());
		}
	}
}
