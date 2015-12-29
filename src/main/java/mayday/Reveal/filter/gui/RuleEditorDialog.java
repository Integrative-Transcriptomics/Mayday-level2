package mayday.Reveal.filter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.events.SNVListEvent;
import mayday.Reveal.events.SNVListListener;
import mayday.core.gui.MaydayFrame;

@SuppressWarnings("serial")
public class RuleEditorDialog extends MaydayFrame {
	
	private SNVList snpList;
	
	public RuleEditorDialog(SNVList snpList) {
		this.snpList = snpList;
		setName(snpList);
		getContentPane().add(new RuleEditorPanel(snpList), BorderLayout.CENTER);
		
		setMinimumSize(new Dimension(800,600));
    	pack();
	}
	
	protected void setName(SNVList snpList) {
		setTitle("Rule Set Editor: " + snpList.getAttribute().getName()); 
	}
	
	protected SNVListListener slListener = new SNVListListener() {

		public void snpListChanged(SNVListEvent event) {
			switch(event.getChange()){
			case SNVListEvent.SNPLIST_CLOSED:
				dispose();
				break;
			case SNVListEvent.ANNOTATION_CHANGE:
				setName((SNVList)event.getSource());
				break;
			}
		}
	};
	
	protected WindowListener windowClosingAdapter = new WindowAdapter() {
		
		 public void windowClosed(WindowEvent e) {
			 finalizeWork();
		 }		
	};

	protected void addListenersTo(SNVList snpList) {
		addWindowListener(windowClosingAdapter);
		snpList.addSNVListListener(slListener);		
	}
	
	protected void removeListenersFrom(SNVList snpList) {
		removeWindowListener(windowClosingAdapter);
		snpList.removeSNVListListener(slListener);
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
