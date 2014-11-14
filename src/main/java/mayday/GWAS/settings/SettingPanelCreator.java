package mayday.GWAS.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import mayday.core.settings.SettingComponent;

public class SettingPanelCreator {
	
	public static JPanel getSettingPanel(final SettingComponent settingComponent) {
		JPanel settingPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JComponent editorComp = settingComponent.getEditorComponent();
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingComponent.updateSettingFromEditor(false);
			}
		});
		
		buttonPanel.add(applyButton);
		
		settingPanel.add(editorComp, BorderLayout.CENTER);
		settingPanel.add(buttonPanel, BorderLayout.SOUTH);
		return settingPanel;
	}
}
