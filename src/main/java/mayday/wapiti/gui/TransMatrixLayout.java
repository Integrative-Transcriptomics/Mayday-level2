package mayday.wapiti.gui;

import java.awt.Color;
import java.util.HashMap;

import mayday.core.DelayedUpdateTask;
import mayday.core.gui.GUIUtilities;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.base.Transformation;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class TransMatrixLayout {
	
	protected TransMatrix transMatrix;
	protected HashMap<Transformation, TransformationInfo> tinfo = new HashMap<Transformation, TransformationInfo>();
	protected HashMap<Integer, Integer> starts = new HashMap<Integer, Integer>();
	protected HashMap<Integer, Integer> widths = new HashMap<Integer, Integer>();

	protected DelayedUpdateTask updater = new DelayedUpdateTask("GUI update",100) {

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			updateLayout0();
		}
		
	};
	
	public TransMatrixLayout(TransMatrix tm) {
		transMatrix = tm;
	}
	
	public TransformationInfo get(Transformation t) {
		TransformationInfo ti = tinfo.get(t);
		if (ti==null)
			tinfo.put( t, ti = new TransformationInfo() );
		return ti;
	}
	
	public Color getColor(Transformation t) {
		return get(t).color;
	}

	public int getWidth(Transformation t) {
		Integer w = widths.get(get(t).minIndex); 
		return w==null?0:w;
	}
	
	public int getStart(Transformation t) {
		Integer s = starts.get(get(t).minIndex);
		return s==null?0:s;
	}

	
	public int getTitleWidth() {
		if (starts.size()>0)
			return starts.get(0);
		else return 0;
	}
	
	public void updateLayout() {
//		updater.trigger();
		updateLayout0();
	}
	
	protected void updateLayout0() {	
		tinfo.keySet().retainAll(transMatrix.getTransformations());
		widths.clear();

		Color[] rainbow = GUIUtilities.rainbow(transMatrix.getTransformationCount(), 0.75);
		int i=0;		
		for (Transformation t : transMatrix.getTransformations()) {
			TransformationInfo ti = get(t);
			ti.color = rainbow[i++];
			ti.minIndex = transMatrix.getMinimumIndex(t);
			
			int prefWidth=0;
			for (Experiment e : transMatrix.getExperiments(t)) 
				prefWidth = Math.max(prefWidth,  t.getGUIElement(e).getPreferredSize().width);
			
			Integer W = widths.get(ti.minIndex);
			if (W==null)
				W = 0;
			W = Math.max(W, prefWidth);
			widths.put(ti.minIndex,W);			
		}
		
		starts.clear();
		int wsum = 0;
		for (Experiment e : transMatrix.getExperiments())
			wsum = Math.max(wsum, e.getGUIElement().getTitleWidth());
		starts.put(0, wsum);
				
		for (i=1; i<widths.size(); ++i) {
			wsum+=3+widths.get(i-1);
			starts.put(i, wsum);
		}
		
		for (Experiment e : transMatrix.getExperiments())
			e.getGUIElement().stateChanged(this);
		transMatrix.getPane().validate();
	}
	
	public static class TransformationInfo {
		public Color color = Color.black;
		public int minIndex = 0;
	}
	
}
