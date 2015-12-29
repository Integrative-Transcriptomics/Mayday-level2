package mayday.vis3.plots.tagcloud;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.vis3.plotsWithoutModel.histogram.HistogramWithMeanComponent;

@SuppressWarnings("serial")
public class FrequencyFilteringDialog extends MaydayDialog
{
	private HistogramWithMeanComponent histogram;
	private SpinnerNumberModel cutoffModel;
	private double cutoff=-1; 
	private JCheckBox invert;
	
	public FrequencyFilteringDialog(List<Double> freqs) 
	{
		histogram=new HistogramWithMeanComponent(new Dimension(400, 300));
		histogram.getHistogramPlotComponent().getValueProvider().setValues(freqs);
		
		setTitle("Set Frequency Cutoff");
		setLayout(new ExcellentBoxLayout(true, 10));
		
		
		cutoffModel=new SpinnerNumberModel(5, 0.0, 10000, 1);
		JSpinner cutoffSpinner=new JSpinner(cutoffModel);
		JLabel cutoffLabel=new JLabel("Minimum Frequency:");
		Box spBox=Box.createHorizontalBox();
		spBox.add(cutoffLabel);
		spBox.add(Box.createHorizontalGlue());
		spBox.add(cutoffSpinner);
		
		Box okBox=Box.createHorizontalBox();
		okBox.add(Box.createHorizontalGlue());
		okBox.add(new JButton(new OkAction()));
		
		invert=new JCheckBox("Invert filter");
		
		add(histogram);
		add(spBox);
		add(invert);
		add(okBox);
		
				
		pack();
		setModal(true);
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() 
		{
			super("Ok");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			cutoff=(Double)cutoffModel.getValue();
			dispose();
			
		}
	}

	public double getCutoff() 
	{
		return cutoff;
	}
	
	public boolean isInvert()
	{
		return invert.isSelected();
	}
	
}
