package mayday.Reveal.visualizations.graphs.SNPNetwork;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SNPNetworkSetting extends HierarchicalSetting {

	private BooleanSetting highlightNodeDegree;
	private BooleanSetting showNodeLabels;
	private RestrictedStringSetting edgeTypes;
	
	private SNPNetwork snpGraph;
	
	private BooleanSetting showControls;
	private Controls controls;
	
	public SNPNetworkSetting(SNPNetwork snpGraph) {
		super("SNP Graph Setting");
		
		this.snpGraph = snpGraph;
		
		addSetting(highlightNodeDegree = new BooleanSetting("Highlight Node Degree", null, false));
		addSetting(showNodeLabels = new BooleanSetting("Show Node Labels", null, false));
		addSetting(edgeTypes = new RestrictedStringSetting("Choose Edge Type", null, 1, "Lines", "Curves", "Cubic", "Orthogonal"));
		addSetting(showControls = new BooleanSetting("Show Controls", null, false));
		
		controls = new Controls();
		
		this.addChangeListener(new SNPGraphSettingChangeListener());
	}
	
	public boolean highlightNodes() {
		return this.highlightNodeDegree.getBooleanValue();
	}
	
	public boolean showNodeLabels() {
		return this.showNodeLabels.getBooleanValue();
	}
	
	public String getEdgeType() {
		return this.edgeTypes.getStringValue();
	}
	
	public SNPNetworkSetting clone() {
		SNPNetworkSetting sgs = new SNPNetworkSetting(snpGraph);
		sgs.fromPrefNode(this.toPrefNode());
		return sgs;
	}
	
	private class SNPGraphSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {

		public SNPGraphSettingChangeListener() {
			super("SNP Graph Updater");
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			snpGraph.updatePlot();
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			if(e.getSource().equals(showControls)) {
				if(showControls.getBooleanValue() == true)
					controls.setVisible(true);
				else
					controls.setVisible(false);
			} else {
				trigger();
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class Controls extends JFrame {
		
		public Controls() {
			super("SNP Network Controls");
			addWindowListener(new WindowListener() {
				@Override
				public void windowOpened(WindowEvent e) {}

				@Override
				public void windowClosing(WindowEvent e) {}

				@Override
				public void windowClosed(WindowEvent e) {
					showControls.setBooleanValue(false);
				}

				@Override
				public void windowIconified(WindowEvent e) {}

				@Override
				public void windowDeiconified(WindowEvent e) {}

				@Override
				public void windowActivated(WindowEvent e) {}

				@Override
				public void windowDeactivated(WindowEvent e) {}
			});
		}
	}
}
