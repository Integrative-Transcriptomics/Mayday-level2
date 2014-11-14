package mayday.vis3.plots.distancematrix;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.settings.Setting;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.actions.ExportTableAction;
import mayday.vis3.gui.actions.GoToProbeAction;
import mayday.vis3.model.Visualizer;

/**
 * 
 * @author Jennifer Lange
 *
 */
@SuppressWarnings("serial")
public class DistanceMatrixWindow extends AbstractTableWindow<DistanceMatrixComponent> {

	public DistanceMatrixWindow(Visualizer viz) {
		super(viz,"Distance Matrix Table");
		for (Setting s : tabular.getSettings())
			addViewSetting(s, null);
				
		JMenu settings = getMenu(VIEW_MENU, (PlotComponent)null);
		settings.add(new GoToProbeAction() {
			public boolean goToProbe(String probeIdentifier) {
				return tabular.goToProbe(probeIdentifier);
			}
		});
		settings.add(new JumpToSelectionAction());
	}
	
	public String getPreferredTitle() {
		return "Distance Matrix";
	}
	
	@Override
	protected DistanceMatrixComponent createTableComponent() {
		return new DistanceMatrixComponent(visualizer);
	}

	@Override
	protected void goToProbe(String name) {
		tabular.goToProbe(name);
	}
	
	protected boolean manageExperimentSelection() {
		return false;
	}

	protected boolean manageProbeSelection() {
		return true;
	}

	protected JMenu makeFileMenu() {
		JMenu table = new JMenu("Distance Matrix");
		table.setMnemonic('D');		
		table.add(new ExportTableAction(tabular, getViewModel()));
		table.add(new JSeparator());
		table.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(DistanceMatrixWindow.this);
			}
		});
		return table;
	}
}
