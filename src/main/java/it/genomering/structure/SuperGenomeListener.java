package it.genomering.structure;

import java.util.EventListener;

public interface SuperGenomeListener extends EventListener {
	
	public void superGenomeChanged(SuperGenomeEvent evt);

}
