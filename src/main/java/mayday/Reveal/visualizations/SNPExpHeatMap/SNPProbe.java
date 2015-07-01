package mayday.Reveal.visualizations.SNPExpHeatMap;

import mayday.Reveal.data.SNV;
import mayday.core.MasterTable;
import mayday.core.Probe;

public class SNPProbe extends Probe {

	private SNV snp;
	
	public SNPProbe(SNV snp, MasterTable masterTable) throws RuntimeException {
		super(masterTable);
		this.snp = snp;
	}
	
	public SNV getSNP() {
		return this.snp;
	}
}
