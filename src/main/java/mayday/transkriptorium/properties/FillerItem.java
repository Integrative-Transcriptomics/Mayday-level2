package mayday.transkriptorium.properties;

import javax.swing.BorderFactory;

import mayday.core.gui.properties.items.AbstractPropertiesItem;

@SuppressWarnings("serial")
public class FillerItem extends AbstractPropertiesItem {

	
	public FillerItem() {
		super("");
		setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void setValue(Object value) {
	}
	
	

}
