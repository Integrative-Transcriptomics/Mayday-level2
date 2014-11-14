package it.genomering.structure;

import java.util.EventListener;

public interface GenomeListener extends EventListener {
	
	public void genomeChanged(GenomeEvent evt);

}
