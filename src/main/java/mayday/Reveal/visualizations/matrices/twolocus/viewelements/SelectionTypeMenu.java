package mayday.Reveal.visualizations.matrices.twolocus.viewelements;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;

import mayday.Reveal.visualizations.matrices.twolocus.AssociationMatrixSetting;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SelectionTypeMenu extends JMenuBar {
	private int selectionType;
	private AssociationMatrixSetting setting;
	
	/**
	 * @param setting
	 */
	public SelectionTypeMenu() {
		super();
		add(getTypeMenu(null));
	}
	
	/**
	 * @param setting
	 */
	public void setSetting(AssociationMatrixSetting setting) {
		this.setting = setting;
	}
	
	/**
	 * @return the selection type
	 */
	public int getSelectionType() {
		return selectionType;
	}
	
	private JMenu getTypeMenu(JMenu modeMenu) {
		if(modeMenu == null) {
            modeMenu = new JMenu();// {
            Icon icon = BasicIconFactory.getMenuArrowIcon();
            modeMenu.setIcon(BasicIconFactory.getMenuArrowIcon());
            modeMenu.setPreferredSize(new Dimension(icon.getIconWidth()+10, 
            		icon.getIconHeight()+10));

            final JRadioButtonMenuItem probeSelectionButton = 
                new JRadioButtonMenuItem("Probe Selection");
            probeSelectionButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        probeSelectionButton.setSelected(true);
                        if(setting != null)
                        	setting.useProbeSelectionMode();
                    }
                }});
            
            final JRadioButtonMenuItem snpSelectionButton =
                new JRadioButtonMenuItem("SNP Selection");
            snpSelectionButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        snpSelectionButton.setSelected(true);
                        if(setting != null)
                        	setting.useSNPSelectionMode();
                    }
                }});
            ButtonGroup radio = new ButtonGroup();
            radio.add(probeSelectionButton);
            radio.add(snpSelectionButton);
            probeSelectionButton.setSelected(true);
            modeMenu.add(probeSelectionButton);
            modeMenu.add(snpSelectionButton);
            modeMenu.setToolTipText("Menu for setting Selection Mode");
        }
        return modeMenu;
	}
}