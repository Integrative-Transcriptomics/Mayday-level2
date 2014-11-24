package mayday.Reveal.data.ld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;

import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.meta.MetaInformationPlugin;
import cern.colt.Arrays;

public class LDBlocks extends MetaInformationPlugin {

	public static final String MYTYPE = "LDBLOCKS";
	
	private int[] blockIndices;
	private SNPList snps;
	
	public LDBlocks() {};
	
	public void setSNPs(SNPList snps) {
		this.snps = snps;
	}
	
	public void setBlockIndices(int[] indices) {
		this.blockIndices = indices;
	}
	
	public LDBlocks(SNPList unionList, int[] blockIndices) {
		this.blockIndices = blockIndices;
		this.snps = unionList;
	}
	
	public boolean inLD(SNP a, SNP b) {
		int ia = snps.indexOf(a);
		int ib = snps.indexOf(b);
		
		if(ia != -1 && ib != -1) {
			boolean equal = blockIndices[ia] == blockIndices[ib];
			boolean minus = blockIndices[ia] == -1;
			return  !minus && equal;
		}
		
		return false;
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		//Header for Meta Information Plugins
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		String snpListName = snps.getAttribute().getName();
		String blockIndicesString = Arrays.toString(blockIndices);
		
		bw.append(snpListName);
		bw.append("\n");
		bw.append(blockIndicesString);
		bw.append("\n");
	}

	@Override
	public boolean deSerialize(String serial) {
		BufferedReader br = new BufferedReader(new StringReader(serial));
		
		try {
			String snpListName = br.readLine();
			String blockIndicesString = br.readLine();
			
			this.snps = this.dataStorage.getSNPList(snpListName);
			blockIndicesString = blockIndicesString.substring(1, blockIndicesString.length()-1);
			String[] split = blockIndicesString.split(", ");
			
			this.blockIndices = new int[split.length];
			
			for(int i = 0; i < blockIndices.length; i++) {
				blockIndices[i] = Integer.parseInt(split[i]);
			}
			
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public Class<?> getResultClass() {
		return LDBlocks.class;
	}

	@Override
	public String getName() {
		return "Linkage Disequilibrium Blocks";
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "LD block structure information";
	}
	
	public String toString() {
		return "LD Blocks for " + snps.size() + " SNPs";
	}

	public int getBlockID(SNP s) {
		int index = snps.indexOf(s);
		if(index == -1)
			return -1;
		return blockIndices[index];
	}
}
