package mayday.Reveal.visualizations.matrices.association;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import mayday.core.gui.components.VerticalLabel;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.gui.setuppers.SetupPreview;
import mayday.vis3.gradient.gui.setuppers.SetupPreview.ROTATION;

@SuppressWarnings("serial")
public class ColorGradientPanel extends JPanel implements SettingChangeListener {
	
	AssociationMatrix matrix;
	AssociationMatrixSetting setting;
	SetupPreview betaPreview;
	SetupPreview expPreview;
	
	public ColorGradientPanel(AssociationMatrix matrix) {
		this.matrix = matrix;
		setting = matrix.setting;
		setting.addChangeListener(this);
		ColorGradient betaGradient = setting.getBetaColorGradient();
		ColorGradient expGradient = setting.getExpressionColorGradient();
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		betaPreview = new SetupPreview();
		betaPreview.updateFromGradient(betaGradient, true);
		
		expPreview = new SetupPreview();
		expPreview.updateFromGradient(expGradient, true);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout());
		panel1.setOpaque(true);
		panel1.setBackground(Color.WHITE);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridBagLayout());
		panel2.setOpaque(true);
		panel2.setBackground(Color.WHITE);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		VerticalLabel label1 = new VerticalLabel("Exp. Gradient", true);
		label1.setAlignmentX(0.5f);
		VerticalLabel label2 = new VerticalLabel("Beta Gradient", true);
		label2.setAlignmentX(0.5f);
		
		gbc.anchor=GridBagConstraints.PAGE_START;
		gbc.gridx=0;
		gbc.gridy=0;
		panel1.add(label1, gbc);
		gbc.gridy=1;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel1.add(expPreview.getJComponent(ROTATION.VERTICAL), gbc);
		
		gbc.anchor=GridBagConstraints.PAGE_START;
		gbc.gridx=0;
		gbc.gridy=0;
		panel1.add(label1, gbc);
		panel2.add(label2, gbc);
		gbc.gridy=1;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel2.add(betaPreview.getJComponent(ROTATION.VERTICAL), gbc);
		
		add(panel1);
		add(panel2);
		
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		if(e.getSource() instanceof ColorGradientSetting) {
			betaPreview.updateFromGradient(setting.getBetaColorGradient(), true);
			expPreview.updateFromGradient(setting.getExpressionColorGradient(), true);
		}
	}
}
