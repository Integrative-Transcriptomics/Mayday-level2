package mayday.tiala.multi.gui.plots;

import java.awt.Color;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.gui.plots.multiprobemultiprofileplot3d.AutoTimepointMultiProfileplot3DComponent;

/**
 * @author jaeger
 *
 */
public class AlignmentMultiProfileplotComponent extends AutoTimepointMultiProfileplot3DComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2579826470254015238L;

	protected Color[] colors;
	
	/**
	 * @param store
	 */
	public AlignmentMultiProfileplotComponent(AlignmentStore store) {
		super(store);
	}

	/**
	 * @param selectionColors
	 */
	public void setSelectionColors(Color[] selectionColors) {
		for(int i = 0; i < settings.getSelectionColorSettings().length; i++) {
			settings.getSelectionColorSettings()[i].setColorValue(selectionColors[i]);
		}
		if (viewModel!=null && viewModel.getSelectedProbes().size() > 0) {
			updatePlot();
		}
	}
	
	/**
	 * @param colors
	 */
	public void setColors(Color[] colors) {
		this.colors = colors;
		if (viewModel != null) {
			updatePlot();
		}
	}
	
	/**
	 * @param b
	 */
	public void setInferMissing(boolean b) {
		//settings.getInferDataSetting().setBooleanValue(b);
	}
}
