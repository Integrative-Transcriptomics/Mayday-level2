package mayday.transkriptorium.properties;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.gui.properties.items.InfoItem;
import mayday.transkriptorium.data.Read;

@SuppressWarnings("serial")
public class ReadItem extends InfoItem {

	
	public ReadItem(String title, final Read read) {
		super(title, read.getIdentifier());
		JButton butt = new JButton(new AbstractAction("Show") {
			public void actionPerformed(ActionEvent e) {
				AbstractPropertiesDialog dlg = PropertiesDialogFactory.createDialog(read);
				if (parent!=null)
					dlg.setModal(parent.isModal());
				dlg.setVisible(true);
			}
		});
		add(butt, BorderLayout.EAST);
	}

	

}
