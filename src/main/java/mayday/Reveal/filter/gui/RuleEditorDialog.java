package mayday.Reveal.filter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import mayday.Reveal.data.SNPList;
import mayday.Reveal.events.SNPListEvent;
import mayday.Reveal.events.SNPListListener;
import mayday.core.gui.MaydayFrame;

@SuppressWarnings("serial")
public class RuleEditorDialog extends MaydayFrame {
	
	private SNPList snpList;
	
	public RuleEditorDialog(SNPList snpList) {
		this.snpList = snpList;
		setName(snpList);
		getContentPane().add(new RuleEditorPanel(snpList), BorderLayout.CENTER);
		
		setMinimumSize(new Dimension(800,600));
    	pack();
	}
	
	protected void setName(SNPList snpList) {
		setTitle("Rule Set Editor: " + snpList.getAttribute().getName()); 
	}
	
	protected SNPListListener slListener = new SNPListListener() {

		public void snpListChanged(SNPListEvent event) {
			switch(event.getChange()){
			case SNPListEvent.SNPLIST_CLOSED:
				dispose();
				break;
			case SNPListEvent.ANNOTATION_CHANGE:
				setName((SNPList)event.getSource());
				break;
			}
		}
	};
	
	protected WindowListener windowClosingAdapter = new WindowAdapter() {
		
		 public void windowClosed(WindowEvent e) {
			 finalizeWork();
		 }		
	};

	protected void addListenersTo(SNPList snpList) {
		addWindowListener(windowClosingAdapter);
		snpList.addSNPListListener(slListener);		
	}
	
	protected void removeListenersFrom(SNPList snpList) {
		removeWindowListener(windowClosingAdapter);
		snpList.removeSNPListListener(slListener);
	}
	
	protected void finalizeWork() {
		removeListenersFrom(snpList);
//		snpList.setIgnoreChanges(false);
		snpList.setSilent(false);
	}
	
	protected void startWork() {
//		snpList.setIgnoreChanges(true);
		snpList.setSilent(true);
		addListenersTo(snpList);
	}
}
