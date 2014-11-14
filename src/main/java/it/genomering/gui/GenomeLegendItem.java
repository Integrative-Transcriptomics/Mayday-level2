package it.genomering.gui;

import it.genomering.structure.Genome;
import it.genomering.structure.GenomeEvent;
import it.genomering.structure.GenomeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.StringSetting;

@SuppressWarnings("serial")
public class GenomeLegendItem extends JPanel implements GenomeListener {

	protected JPanel colorBox = new JPanel();
	protected JLabel name = new JLabel();
	
	public GenomeLegendItem(final Genome g) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setOpaque(true);
		JPanel colorPanel = new JPanel();
		colorPanel.setBackground(Color.WHITE);
		colorPanel.setOpaque(true);
		colorPanel.add(colorBox);
		add(colorPanel, BorderLayout.WEST);
		add(name, BorderLayout.CENTER);
		colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		g.addListener(this);
		updateGUI(g);
		
		colorBox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount()==2) { // change genome's color
					Color newColor = JColorChooser.showDialog(colorBox, "Change color for genome "+g.getName(), g.getColor());
					if (newColor!=null) {
						g.setColor(newColor);
						g.setVisible(true);
					}
				} else { // show/hide genome
					g.setVisible(!g.isVisible());
				}
			}
		});
		
		name.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getButton() == MouseEvent.BUTTON1)
					if (evt.getClickCount()==2)
						g.resortSuperGenome();
				if(evt.getButton() == MouseEvent.BUTTON3)
					if(evt.getClickCount()==2) {
						StringSetting nameSetting = new StringSetting("Name:", null, name.getText());
						SettingDialog sd = new SettingDialog(null, "Change Name", nameSetting);
						sd.showAsInputDialog();
						if(sd.closedWithOK()) {
							name.setText(nameSetting.getStringValue());
							g.setName(name.getText());
						}
					}
			}
		});
	}

	protected void updateGUI(Genome g) {
		Color c = Color.white;
		if (g.isVisible())
			c = g.getColor();
		colorBox.setBackground(c);
		name.setText(g.getName());
	}
	
	@Override
	public void genomeChanged(GenomeEvent evt) {
		switch(evt.getType()) {
		case GenomeEvent.COLOR_CHANGED: // fall
		case GenomeEvent.NAME_CHANGED: // fall
		case GenomeEvent.VISIBILITY_CHANGED: 
			updateGUI(evt.getSource());
			break;
		}
	}
	
}