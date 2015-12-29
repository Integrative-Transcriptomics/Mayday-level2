package it.genomering.gui;

import it.genomering.structure.Block;
import it.genomering.structure.SuperGenome;
import it.genomering.structure.SuperGenomeEvent;
import it.genomering.structure.SuperGenomeListener;

import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.StringSetting;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RememberOrderingAction extends AbstractAction implements SuperGenomeListener {

	SuperGenome sg;
	JMenu menu;
	boolean menuempty;
	
	/**
	 * @param sg
	 * @param blocks
	 */
	public RememberOrderingAction(SuperGenome sg, JMenu mymenu) {
		super("Remember current block order");
		menu=mymenu;		
		this.sg = sg;
		sg.addListener(this);
		this.superGenomeChanged(new SuperGenomeEvent(sg, SuperGenomeEvent.GENOMES_CHANGED));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		RememberedOrdering ro = new RememberedOrdering(sg);
		if (ro.getName()!=null) {
			if (menuempty) {
				menu.removeAll();
				menuempty = false;
			}
			menu.add(new RecallOrderingAction(ro));
		}
	}
	
	public static class RememberedOrdering extends ArrayList<Block> {
		protected String name;
		protected SuperGenome sg;
		
		public RememberedOrdering(List<Block> b, SuperGenome sg, String name) {
			super(b);
			this.name=name;
			this.sg=sg;
		}
		
		public RememberedOrdering(List<Block> b, SuperGenome sg) {
			super(b);
			this.sg=sg;
			String name = DateFormat.getInstance().format(new Date());
			StringSetting names = new StringSetting("Name","Enter a name for the current block order",name);
			SettingDialog sd = new SettingDialog(null, "Name current order", names);
			if (sd.showAsInputDialog().closedWithOK()) 
				this.name=names.getStringValue();
			else 
				this.name=null;
		}
		
		public RememberedOrdering(SuperGenome sg) {
			this(sg.getBlocks(), sg);
		}

		public String getName() {
			return name;
		}

		public void restore() {
			sg.setBlocks(this);
		}
		
	}

	@Override
	public void superGenomeChanged(SuperGenomeEvent evt) {
		if (evt.getChange()==SuperGenomeEvent.GENOMES_CHANGED) {
			menu.removeAll();
			menu.add("-- Nothing remembered so far --");
			menuempty = true;
		}
	}
}
