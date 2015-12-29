package mayday.transkriptorium.meta;

import javax.swing.JLabel;

import mayday.core.meta.gui.AbstractMIRenderer;


@SuppressWarnings({ "serial", "unchecked" })
public class MIRendererEmpty extends AbstractMIRenderer {

	private String theValue;
	
	public MIRendererEmpty() {
	}

	@Override
	public String getEditorValue() {
		return theValue;
	}

	@Override
	public void setEditable(boolean editable) {
		// sorry, no.
	}

	@Override
	public void setEditorValue(String serializedValue) {
		theValue = serializedValue;
	}
	
	public JLabel getEditorComponent() {
		return new JLabel("This mio cannot be edited manually.");
	}

}
