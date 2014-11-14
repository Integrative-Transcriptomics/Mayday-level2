package mayday.wapiti.gui;

import mayday.wapiti.gui.layeredpane.HorizontalLayeredPane;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class TransMatrixPane extends HorizontalLayeredPane {
	
	protected TransMatrix transMatrix;
	protected TransMatrixLayout tmLayout;

	public TransMatrixPane(TransMatrix tm) {
		transMatrix = tm;
		tmLayout = new TransMatrixLayout(tm);	
	}
	
	public TransMatrixLayout getTMLayout() {
		return tmLayout;
	}
	
	
}
