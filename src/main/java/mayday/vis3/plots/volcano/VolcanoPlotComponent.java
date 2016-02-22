/**
 * 
 */
package mayday.vis3.plots.volcano;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.ColorProvider;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradient.MIDPOINT_MODE;
import mayday.vis3.gradient.agents.Agent_Tricolore;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.manipulators.None;
import mayday.vis3.plots.scatter.ScatterPlotComponent;
import mayday.vis3.vis2base.DataSeries;

/**
 * @author Alexander Stoppel
 *
 */
@SuppressWarnings("serial")
public class VolcanoPlotComponent extends ScatterPlotComponent{
	
	protected BooleanSetting isLogged = new BooleanSetting(
			"Apply logarithm",
			"If data is not logarithmic, it has to be transformed for the volcano plot",
			false);
	
	// these two shadow the valueproviders in VolcanoPlotComponent
	protected VolcanoValueProvider X;
	protected VolcanoValueProvider Y;
	protected ColorProvider coloring;
	protected ColorGradient gradient;
	
	public VolcanoPlotComponent() {
		super();
	}
	
	
	//Überschriebene createView() aus AbstractProbeListScatterPlotComponent
	@Override
	public void createView() {
		
		List<ProbeList> pls = getProbeLists();
		
		Layers = new DataSeries[pls.size()];
		int h=0;
		
		for(int i = pls.size(); i!=0; --i) {
			ProbeList pl = pls.get(i-1);
			DataSeries ds = viewProbes(pl.getAllProbes());		
			//Set global color
//			ds.setColor(pl.getColor());
//			ds.setColor(Color.green);
//			ds.setAfterJumpModifier(probeColorSetter);
			//Try to set color for single point
//			System.out.println("Try to set col");
//			for(int j = 0; j < ds.getSize(); j++){
//				System.out.println("Probe "+ j);
//				if(j % 2 == 0)
//				ds.getDPoint(j).setColor(Color.blue);
//				else {
//					ds.getDPoint(j).setColor(Color.red);
//				}
//			}
			Layers[h++] = ds;
			addDataSeries(ds);
		}		select(Color.RED);
	}
	
	


	protected Color getColorFromGradient(double position, ColorGradient grad) {
		Color color = grad.mapValueToColor(position);
		return color;
	}
	
	
	protected double log10(double x) {
		if(x > 0){
			return Math.log10(x);
		} else if(x < 0){
			//calculate correct values for negative p-values
			return (Math.log10(-x));
		}	
		else{
			//this should not happen
			return 0d; 
		}
	}
	
	@Override
	public DataSeries viewProbes(Collection<Probe> probes) {
		DataSeries ds = new DataSeries();
		
		//Coloring
		//some random values for min and max.
		gradient=new ColorGradient(-100, 0, 100, true, 3, MIDPOINT_MODE.Center, 
				new Agent_Tricolore(false, Color.green, Color.yellow, Color.red, 1.0));
		Color color;
		color = Color.blue; //for testing purpose only -> does not work
		if (X!=null && Y!=null) {
			for (Probe pb : probes) {
				double pval = log10(X.getValue(pb)); 
				double fc = Y.getValue(pb);
				color = getColorFromGradient(fc, gradient);
				ds.addPoint(fc, pval, pb);
				szb.setObject(fc, pval, pb);
				//Points get colored
				ds.getDPoint(ds.getSize()-1).setColor(color);			
			}
		}
		return ds;
	}
	
	

	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		plotContainer.addViewSetting(isLogged, this);
	}

	
	public DataSeries getPlotComponent(int i) {
		int index = getNumberOfComponents()-i-1;
		ProbeList pl = viewModel.getProbeLists(true).get(index);
		DataSeries res = viewProbes(pl.getAllProbes());
		res.setAfterJumpModifier(probeColorSetter);
		return res;
	}
	
	@Override
	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<Probe> newSelection = new HashSet<Probe>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		for (Probe pb : viewModel.getProbes()) {
			double pval = log10(X.getValue(pb));
			double fc = Y.getValue(pb);
		
			boolean inX = (fc>clicked1[0] && fc<clicked2[0]);
			boolean inY = (pval<clicked1[1] && pval>clicked2[1]);
			if (inX && inY)
				newSelection.add(pb);
		}
		/*
		 * selection modes: 
		 * - no modifier: replace
		 * - ctrl: union with previous selection
		 * - alt: intersect with previous selection
		 * - ctrl+alt: remove from previous selection
		 */
		
		Set<Probe> previousSelection = viewModel.getSelectedProbes();
		if (control && alt) {
			previousSelection = new HashSet<Probe>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (control) {
			newSelection.addAll(previousSelection);
		} else if (alt) {
			newSelection.retainAll(previousSelection);
		} else {
			// nothing to do with prev selection
		}
		viewModel.setProbeSelection(newSelection);
	}
	
	//Provides a new window to select MIO p-values and fc
	protected MIGroup getMIOGroup(String selection){
		MIGroupSelectionDialog mgs = new MIGroupSelectionDialog(this.viewModel.getDataSet().getMIManager());
		if (mgs.getSelectableCount()==0)
			JOptionPane.showMessageDialog(null,"No appropriapte (categorical/nominal) meta information found","No Meta-Information",JOptionPane.ERROR_MESSAGE);
		mgs.setTitle("MIO selection");
		switch (selection) {
			case "pval":
				mgs.setDialogDescription("Please select p-values!");
				mgs.setVisible(true);
				if (mgs.isCanceled() || mgs.getSelection().size()==0)
					return null;
				return mgs.getSelection().get(0);
			case "fc": 
				mgs.setDialogDescription("Please select fold change!");
				mgs.setVisible(true);
				if (mgs.isCanceled() || mgs.getSelection().size()==0)
					return null;
				return mgs.getSelection().get(0);
			default:
				break;
		}
		return null;
	}
	
	protected void initValueProviders(ViewModel vm, PlotContainer plotContainer) {
		
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updatePlot();
			}
		};	
		
		MIGroup mgX = getMIOGroup("pval");
		MIGroup mgY = getMIOGroup("fc");
			
		if(mgX == null || mgY == null){
			return;
		}
		 
		X = new VolcanoValueProvider(viewModel,"-log p-value", mgX);
		Y = new VolcanoValueProvider(viewModel,"log fold change", mgY);
		
		//------------------------------------------
		coloring = new ColorProvider(viewModel);
		coloring.setMode(1);
		plotContainer.addViewSetting(coloring.getSetting(), this);
		coloring.addChangeListener(cl);
		//-----------------------------------///
		
		plotContainer.addViewSetting(X.getSetting(), this);
		plotContainer.addViewSetting(Y.getSetting(), this);
		X.addChangeListener(cl);
		Y.addChangeListener(cl);
		X.setProvider(X.new MIOProvider(mgX, new None()));
		Y.setProvider(Y.new MIOProvider(mgY, new None()));
	}

	
	@Override
	public String getPreferredTitle() {
		return "Volcano Plot";
	}
	
	@Override
	public String getAutoTitleY(String ytitle) {
		if (Y!=null)
			return  "-log p-value";
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (X!=null)
			return  "log fold change";
		return xtitle;
	}
}


