package mayday.expressionmapping.view.information;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import mayday.core.Probe;
import mayday.expressionmapping.controller.MainFrame;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public abstract class AttractorWindow extends BasicPlotPanel implements MouseListener, ViewModelListener {

	protected MainFrame master;
	protected ViewModel viewModel;
	protected Color select = Color.red;
	
	protected Color fillMain = Color.lightGray;
	protected Color fillAtt = Color.gray;
	
	
	protected Color[] mainColor;
	protected Color[] attColor;
	
	protected Polygon[] mainAttractors;
	protected Polygon[] attractors;
	
	private boolean mouseClicked = false;
	
	private void resetColor() {
		for (int i = 0; i < this.mainColor.length; ++i) {
			this.mainColor[i] = fillMain;
		}
		for (int i = 0; i < this.attColor.length; ++i) {
			this.attColor[i] = fillAtt;
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		int xPos = e.getX();
		int yPos = e.getY();
		
		this.mouseClicked = true;
		
		for (int i = 0; i < this.mainAttractors.length; ++i) {
			if (this.mainAttractors[i].contains(xPos, yPos)) {
				resetColor();
				this.mainColor[i] = select;
				this.master.signalMainAttractorClick(i, e.getButton());
				updatePlot();

				/* the attractor has been found and we can exit
				 */
				return;
			}
		}

		for (int i = 0; i < this.attractors.length; ++i) {
			if (this.attractors[i].contains(xPos, yPos)) {
				resetColor();
				this.attColor[i] = select;
				this.master.signalAttractorClick(i, e.getButton());
				updatePlot();
				
				/* the attractor has been found and we can exit
				 */
				return;
			}
		}

		resetColor();
		this.master.signalClearAttractor();
		updatePlot();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void updatePlot() {
		this.repaint();
	}
	
	@Override
	public void setup(PlotContainer plotContainer) {
		this.viewModel = plotContainer.getViewModel();
		this.viewModel.addViewModelListener(this);
		this.setupPlot(plotContainer);
	}
	
	public void paintComponent(Graphics g) {
		//enable antialiasing
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		super.paintComponent(g);
	}
	
	/**
	 * @param container
	 */
	public abstract void setupPlot(PlotContainer container);
	
	/**
	 * @param selectionColor
	 */
	public void setSelectionColor(Color selectionColor) {
		this.updateSelectionColors(selectionColor);
		this.select = selectionColor;
	}
	
	private void updateSelectionColors(Color newColor) {
		for(int i = 0; i < attColor.length; i++) {
			if(attColor[i].equals(select)) {
				attColor[i] = newColor;
			}
		}
		for(int i = 0; i < mainColor.length; i++) {
			if(mainColor[i].equals(select)) {
				mainColor[i] = newColor;
			}
		}
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if(!mouseClicked) {
			if(vme.getChange() == ViewModelEvent.PROBE_SELECTION_CHANGED) {
				this.resetColor();		
				
				PointList<? extends Point> points = this.master.getPoints();
				Probe[] allProbes = viewModel.getProbes().toArray(new Probe[0]);
				
				for(int i = 0; i < this.mainAttractors.length; i++) {
					TIntArrayList mAPoints = points.getMainAttractorPoints(i);
					
					for(int j = 0; j < mAPoints.size(); j++) {
						int id = mAPoints.get(j);
						Probe pb = allProbes[id];
						if(viewModel.isSelected(pb)) {
							this.mainColor[i] = select.darker();
							break;
						}
					}
				}
				
				for(int i = 0; i < this.attractors.length; i++) {
					TIntArrayList aPoints = points.getAttractorPoints(i);
					
					for(int j = 0; j < aPoints.size(); j++) {
						int id = aPoints.get(j);
						Probe pb = allProbes[id];
						if(viewModel.isSelected(pb)) {
							this.attColor[i] = select.darker();
							break;
						}
					}
				}
				
				this.updatePlot();
			}
		}
		mouseClicked = false;
	}
}
