package mayday.expressionmapping.view.ui;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.expressionmapping.controller.MainFrame;

/**
 * @author jaeger
 *
 */
public class EMViewSetting extends HierarchicalSetting {

	
	private ColorSetting selectionColor;
	private DoubleSetting pointSize;
	
	private MainFrame frame;
	
	/**
	 * @param frame
	 */
	public EMViewSetting(MainFrame frame) {
		super("Expression Mapping View Setting");

		this.frame = frame;
		
		this.addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		this.addSetting(pointSize = new DoubleSetting("Data Point Size", null, 4.0));
		
		this.addChangeListener(new EMVSListener());
	}
	
	/**
	 * @return selection color
	 */
	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}
	
	/**
	 * @return point size for expression simplex view
	 */
	public float getPointSize() {
		return (float)this.pointSize.getDoubleValue();
	}
	
	public EMViewSetting clone() {
		EMViewSetting emvs = new EMViewSetting(frame);
		emvs.fromPrefNode(this.toPrefNode());
		return emvs;
	}
	
	private class EMVSListener extends DelayedUpdateTask implements SettingChangeListener{
		
		public EMVSListener() {
			super("Expression Mapping View Settings Listener");
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			frame.updateComponents();
		}
	}
}
