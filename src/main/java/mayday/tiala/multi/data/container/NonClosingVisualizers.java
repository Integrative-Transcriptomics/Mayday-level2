package mayday.tiala.multi.data.container;

import java.util.ArrayList;

import mayday.tiala.multi.data.viewmodel.NonClosingVisualizer;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class NonClosingVisualizers extends ArrayList<NonClosingVisualizer> {
	
	/**
	 * @param size
	 */
	public NonClosingVisualizers(int size) {
		for(int i = 0; i < size; i++) {
			add(new NonClosingVisualizer());
		}
	}

	/**
	 * dispose all visualizers
	 */
	public void dispose() {
		for(NonClosingVisualizer v : this)
			v.dispose();
	}	
}
