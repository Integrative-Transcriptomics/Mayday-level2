package mayday.Reveal.io.vcf;

/**
 * A class representing a VCF Entry.
 * @author heumos
 *
 */
public class VCFEntry implements Comparable<VCFEntry>{
	
	// the CHROM of the entry
	private String chrom;
	// the POS of the entry
	private String pos;
	// the ID of the entry
	private String iD;
	// the REF of the entry
	private String ref;
	// the ALT of the entry
	private String alt;
	// the QUAL of the entry
	private String qual;
	// the FILTER of the entry
	private String filter;
	// the INFO of the entry
	private String info;
	// the FORMAT of the entry
	private String format;
	// the Sample_IDs of the entry
	private String sampleIds;
	
	/**
	 * @param chrom
	 * @param pos
	 * @param iD
	 * @param ref
	 * @param alt
	 * @param qual
	 * @param filter
	 * @param info
	 * @param annotation
	 * @param format
	 * @param sampleIds
	 */
	public VCFEntry(String chrom, String pos, String iD, String ref,
			String alt, String qual, String filter, String info, String format, String sampleIds) {
		super();
		this.chrom = chrom;
		this.pos = pos;
		this.iD = iD;
		this.ref = ref;
		this.alt = alt;
		this.qual = qual;
		this.filter = filter;
		this.info = info;
		this.format = format;
		this.sampleIds = sampleIds;
	}

	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public Integer getPos() {
		return Integer.parseInt(pos);
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getiD() {
		return iD;
	}

	public void setiD(String iD) {
		this.iD = iD;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getQual() {
		return qual;
	}

	public void setQual(String qual) {
		this.qual = qual;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(String sampleIds) {
		this.sampleIds = sampleIds;
	}
	
	public void addSamplesBefore(String samplesString) {
		this.sampleIds = samplesString + "\t" + this.sampleIds;
	}
	
	public void addSamplesAfter(String samplesString) {
		this.sampleIds += "\t"+samplesString;
	}
	
	/**
	 * checks, if this VcfEntry has exactly the same format as that VcfEntry
	 * @param vE
	 * @return
	 */
	public boolean equalsFormat(VCFEntry vE) {
		String thisF = this.getFormat();
		String thatF = vE.getFormat();
		return thisF.equals(thatF);
	}

	@Override
	public int compareTo(VCFEntry o) {
		return this.getPos().compareTo(o.getPos());
	}

	public static VCFEntry fromVCFLine(String vcfLine) {
		String[] split = vcfLine.split("\t");
		
		//something must be wrong since there are not enough fields in this line!
		if(split.length < 10) {
			return null;
		}
		
		String chrom = split[0];
		String pos = split[1];
		String id = split[2];
		String ref = split[3];
		String alt = split[4];
		String qual = split[5];
		String filter = split[6];
		String info = split[7];
		String format = split[8];
		
		StringBuilder genotypes = new StringBuilder();
		genotypes.append(split[9]);
		
		for(int i = 10; i < split.length; i++) {
			genotypes.append("\t");
			genotypes.append(split[i]);
		}
		
		VCFEntry e = new VCFEntry(chrom, pos, id, ref, alt, qual, filter, info, format, genotypes.toString());
		return e;
	}

	public String getChromosomalLocation() {
		return this.chrom.toLowerCase() + this.pos;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(chrom);
		sb.append("\t");
		sb.append(pos);
		sb.append("\t");
		sb.append(iD);
		sb.append("\t");
		sb.append(ref);
		sb.append("\t");
		sb.append(alt);
		sb.append("\t");
		sb.append(qual);
		sb.append("\t");
		sb.append(filter);
		sb.append("\t");
		sb.append(info);
		sb.append("\t");
		sb.append(format);
		sb.append("\t");
		sb.append(sampleIds);
		return sb.toString();
	}
}
