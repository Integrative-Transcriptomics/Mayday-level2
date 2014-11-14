package mayday.GWAS.visualizations.SNPExpHeatMap;

import mayday.GWAS.data.SNP;
import mayday.core.MasterTable;
import mayday.core.Probe;

public class SNPProbe extends Probe {

	private SNP snp;
	
	public SNPProbe(SNP snp, MasterTable masterTable) throws RuntimeException {
		super(masterTable);
		this.snp = snp;
	}
	
	public SNP getSNP() {
		return this.snp;
	}
}
