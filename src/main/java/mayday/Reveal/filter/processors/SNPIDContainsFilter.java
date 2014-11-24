package mayday.Reveal.filter.processors;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.Reveal.data.SNP;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.filter.StorageNodeStorable;
import mayday.Reveal.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;

public class SNPIDContainsFilter extends AbstractDataProcessor<SNP, Boolean> implements OptionPanelProvider, StorageNodeStorable {

	private String contained = "";
	
	@Override
	public void dispose() {
		contained = null;
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SNPIDContainsFilter", "");
		parent.addChild("Contained", contained);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		Object o = storageNode.getChild("Contained");
		if(o != null) {
			contained = ((StorageNode) o).Value;
		}
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createTitledBorder("SNPIDContainsFilter - Settings"));
		
		final JTextField textField = new JTextField();
		
		if(contained != null) {
			textField.setText(contained);
		}
		
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = textField.getText();
				contained = text;
				fireChanged();
			}
		});
		
		panel.add(new JLabel("Contained substring:"));
		panel.add(textField);
		
		return panel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return contained == null ? null : new Class<?>[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "SNP-ID contains \"" + contained;
	}

	@Override
	protected Boolean convert(SNP value) {
		if(contained != null)
			return value.getID().contains(contained);
		else
			return null;
	}

	@Override
	public String getName() {
		return "SNP-ID Contains Substring";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.snpidcontains";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs with a specific substring in their identifier";
	}
}
