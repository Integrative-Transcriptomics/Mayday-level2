package mayday.transkriptorium.properties;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.core.gui.properties.items.AbstractListItem;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.transkriptorium.data.MappedRead;

@SuppressWarnings("serial")
public class MappingsListItem extends AbstractListItem {

	List<MappedRead> m;
	
	public MappingsListItem(String title, List<MappedRead> mappings) {		
		super(title);
		m = mappings;
		//speed up
		if (m.size()>0)
			listField.setPrototypeCellValue(m.get(0));
		initList((DefaultListModel)getValue());
		initButtons();
	}
	
	protected JButton[] getButtons() {
		JButton EditButton = new JButton(editObjectAction);
		editObjectAction.putValue(AbstractAction.NAME, "Show");
		return new JButton[]{EditButton};
	}

	protected void initList(DefaultListModel lm) {
		int position=0;
		lm.clear();
		for (MappedRead mr : m)
			lm.add(position++, mr);
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new MappedReadCellRenderer();
	}

	protected class MappedReadCellRenderer extends DefaultListCellRenderer {
		 public Component getListCellRendererComponent(
			        JList list,
			        Object value,
			        int index,
			        boolean isSelected,
			        boolean cellHasFocus)
			    {
			 
			 MappedRead mr = (MappedRead)value;
			 AbstractGeneticCoordinate agc = mr.getTargetCoordinate();
			 
			 String ret = "<html>Alignment: <b>"+mr.getStartInRead()+"-"+mr.getEndInRead()+"</b>, Quality <b>"+mr.quality()+"</b> at "+
			 			  "<b>"+agc.getChromosome().getSpecies().getName()+"</b>:<b>"+agc.getChromosome().getId()+"</b>:"+
			 			  "<b>"+agc.getStrand().toChar()+"</b>:"+
			 			 "<b>"+agc.getFrom()+"</b>-<b>"+agc.getTo()+"</b>";
			 return super.getListCellRendererComponent(list, ret, index, isSelected, cellHasFocus);			 
			    }
	}
	
}
