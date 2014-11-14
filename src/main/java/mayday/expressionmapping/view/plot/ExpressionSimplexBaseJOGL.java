package mayday.expressionmapping.view.plot;


import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.swing.JComponent;

import mayday.core.Probe;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.utilities.Camera3D;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */

public abstract class ExpressionSimplexBaseJOGL extends AbstractPlot3DPanel implements ExpressionSimplex {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8542457447936527657L;
	protected PointList<? extends Point> points;
	protected float[] backgroundColor = convertColor(Color.WHITE);
	protected float[] labelColor = convertColor(Color.BLUE);
	protected ArrayList<float[]> pointColors = new ArrayList<float[]>();
	
	protected int width, height;
	
	protected TextRenderer renderer;
	protected Color selectionColor = Color.RED;
	protected float pointSize = 4.0f;

	/**
	 * @param points
	 */
	public ExpressionSimplexBaseJOGL(PointList<? extends Point> points) {
		super();
		this.points = points;
	}
	
	protected abstract void createHull(GL gl);
	protected abstract void createPoints(GL gl, int glRender);
	protected abstract void createLabels(GL gl);
	
	
	public void setupPanel(PlotContainer plotContainer) {
		((Camera3D)this.camera).setPosition(2, 3);
		renderer = new TextRenderer(new Font("Sans.Serif", Font.BOLD, 24),
				true, true);
		renderer.setSmoothing(true);
	}
	
	/**
	 * @return ExpressionSimplexBasisJOGL.this
	 */
	public JComponent getComponent() {
		return this;
	}

/*
 * old structures that are not needed any more
 */
//	/**
//	 * @param mainAccessList
//	 * @param mainColorPack
//	 * @param subAccessList
//	 * @param subColorPack
//	 */
//	public void colorPoints(TIntArrayList mainAccessList, ColorPack mainColorPack, TIntArrayList subAccessList, ColorPack subColorPack) {
//		
//		//this.resetPointColor();
//		this.clearProbeSelection();
//		
//		Point currentPoint;
//		
//		/* process first list, if the second list is null, the first list
//		 * corresponds to a main attractor
//		 */
//		int size_1 = mainAccessList.size();
//		
//		for(int i = 0; i < size_1; ++i) {
//			currentPoint = this.points.get(mainAccessList.get(i));
//			this.pointColors.set(mainAccessList.get(i), mainColorPack.getColor(currentPoint));
//		}
//		
//		/* process second list if unequal null
//		 *
//		 */
//		if(subAccessList != null) {
//			int size_2 = subAccessList.size();
//			
//			for(int i = 0; i < size_2; ++i) {
//				currentPoint = this.points.get(subAccessList.get(i));
//				this.pointColors.set(subAccessList.get(i), subColorPack.getColor(currentPoint));
//			}
//		}
//		this.canvas.repaint();
//	}
	
	public void changeProbeSelection(TIntArrayList selectedProbeIDs) {
		Set<Probe> selectedProbes = new HashSet<Probe>();
		Object[] allProbes = viewModel.getProbes().toArray();
		
		for(int i = 0; i < selectedProbeIDs.size(); i++) {
			selectedProbes.add((Probe)allProbes[selectedProbeIDs.get(i).intValue()]);
		}
		
		this.viewModel.setProbeSelection(selectedProbes);
		this.updatePlot();
	}
	
	public void changeProbeSelection(Collection<Probe> probes) {
		this.viewModel.setProbeSelection(probes);
		this.updatePlot();
	}
	
	public void clearProbeSelection() {
		this.viewModel.setProbeSelection(new HashSet<Probe>());
		this.updatePlot();
	}
	
/*
 * old structures that are not needed any more
 */
//	/**
//	 * 
//	 */
//	public void resetPointColor() {
//		int size = this.pointColors.size();
//		this.pointColors.clear();
//		for(int i = 0; i < size; i++) {
//			this.pointColors.add(this.pointColor);
//		}
//		this.updatePlot();
//	}

	@Override
	public void update(GL gl) {
		this.updatePlot();
	}
	
	/**
	 * Paint component
	 */
	public void plot() {
		this.canvas.repaint();
	}

	/**
	 * @param selectionColor
	 */
	public void setSelectionColor(Color selectionColor) {
		this.selectionColor  = selectionColor;
	}
	
	/**
	 * @param pointSize
	 */
	public void setPointSize(float pointSize) {
		this.pointSize = pointSize;
	}
}
