package mayday.vis3d.plots.profileplot;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import mayday.core.Probe;
import mayday.vis3d.AbstractPlot2DPanel;
import mayday.vis3d.utilities.SelectionHandler3D;

public class ProfilePlotSelectionHandler extends SelectionHandler3D {

	private int labelAxis;
	private HighlightAxis highlightAxis;
	private LabelHistogram labelHistogram;
	
	public ProfilePlotSelectionHandler(AbstractPlot2DPanel panel, HighlightAxis hlaxis, LabelHistogram hist) {
		super(panel);
		this.highlightAxis = hlaxis;
		this.labelHistogram = hist;
		this.labelAxis = this.highlightAxis.getAxisHashCode();
	}
	
	public void identifiyPickedHit(int hits, int[] selectBuf) {
		int names, ptr = 0, minZ, ptrNames = 0, numberOfNames = 0;
		minZ = Integer.MAX_VALUE;
		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		
		//add only minimum hits to selection
		for (int i = 0; i < hits; i++) {
			names = selectBuf[ptr];
			ptr++;
			if (selectBuf[ptr] < minZ) {
				numberOfNames = names;
				minZ = selectBuf[ptr];
				ptrNames = ptr + 2;
			}
			ptr += names + 2;
		}
		ptr = ptrNames;
		
		for(int i = 0; i < numberOfNames; i++, ptr++){
			selection.add(selectBuf[ptr]);
		}
		
		if(selection.size() > 0) {
			if(!MB3) {
				Object selected = this.selectableObjects.get(selection.get(0));
				if(selected == null) {
					selected = this.selectableProbes.get(selection.get(0));
					if(selected != null) { 
						this.processPickedHit(selected, true);
					}
				} else {
					this.processPickedHit(selected, false);
				}
			} else {
				if(selection.get(0) == this.labelAxis) {
					this.highlightAxis.setSelected(true);
				} else if(this.selectableProbes.containsKey(selection.get(0))) {
					this.labelHistogram.setProbeForDragging((Probe)this.selectableProbes.get(selection.get(0)));
				}
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = true;
			setRectangle(e.getX() - 2.5, e.getY() - 2.5, 5, 5);
			setPickable(true);
			//TODO panel.getCanvas().repaint();
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = false;
			this.labelHistogram.setProbeForDragging(null);
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
	}
}
