package mayday.wapiti.gui.actions.transform;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingDialog;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationPropertiesAction extends AbstractAction {

	private final Transformation t;

	public TransformationPropertiesAction(Transformation transformation) {
		super("Properties");
		t = transformation;
		if (t.getSetting()==null)
			setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (t.getSetting()!=null)
			new SettingDialog(null, t.getName(), t.getSetting()).setVisible(true);
	}

}