package mayday.Reveal.data.meta.snpcharacterization;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.io.gff3.GFFElement;
import mayday.Reveal.io.gff3.GFFTree;

public class SNPCharacterization implements MetaInformation {

	public static final String MYTYPE = "SNP-Characterization";
	
	public static final int FIVE_PRIME_UTR = 0;
	public static final int FIVE_PRIME_UTR_START_CODON_INSERTED = 1;
	public static final int SPLICE_SITE_ACCEPTOR = 2;
	public static final int SPLICE_SITE_DONOR = 3;
	public static final int START_CODON_LOST = 4;
	public static final int STOP_GAINED = 5;
	public static final int STOP_LOST = 6;
	public static final int THREE_PRIME_UTR = 7;
	public static final int INTRON = 8;
	public static final int EXON = 9;
	public static final int FIVE_PRIME_UTR_SYNONYMOUS = 10;
	
	public static final int IMPACT_HIGH = 3;
	public static final int IMPACT_MEDIUM = 2;
	public static final int IMPACT_LOW = 1;
	public static final int IMPACT_NO = 0;


	private String personID;
	private String personName;
	private SNP snp;

	private String originalAA;
	private String modifiedAAA;
	private String modifiedAAB;
	
	private char nucleotideA;
	private char nucleotideB;

	private boolean isProtSeqModifiedA = false;
	private boolean isProtSeqModifiedB = false;

	private int characterizationFeature = -1;
	
	private String gffElement;
	
	private DataStorage ds;
	
	public SNPCharacterization(String personID, String personName, SNP s) {
		this.personID = personID;
		this.snp = s;
	}
	
	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.write(personID);
		bw.write("\t");
		bw.write(personName);
		bw.write("\t");
		bw.write(snp.getID());
		bw.write("\t");
		bw.write(originalAA);
		bw.write("\t");
		bw.write(modifiedAAA);
		bw.write("\t");
		bw.write(modifiedAAB);
		bw.write("\t");
		bw.write(Character.toString(nucleotideA));
		bw.write("\t");
		bw.write(Character.toString(nucleotideB));
		bw.write("\t");
		bw.write(Boolean.toString(isProtSeqModifiedA));
		bw.write("\t");
		bw.write(Boolean.toString(isProtSeqModifiedB));
		bw.write("\t");
		bw.write(Integer.toString(characterizationFeature));
		bw.write("\t");
		bw.write(gffElement);
		bw.write("\n");
	}

	@Override
	public boolean deSerialize(String serial) {
		String[] split = serial.split("\t");
		
		if(split.length != 11) {
			return false;
		}
		
		this.personID = split[0];
		this.personName = split[1];
		this.snp = ds.getGlobalSNPList().get(split[2]);
		this.originalAA = split[3];
		this.modifiedAAA = split[4];
		this.modifiedAAB = split[5];
		this.nucleotideA = split[6].charAt(0);
		this.nucleotideB = split[7].charAt(0);
		this.isProtSeqModifiedA = Boolean.parseBoolean(split[8]);
		this.isProtSeqModifiedB = Boolean.parseBoolean(split[9]);
		this.characterizationFeature = Integer.parseInt(split[10]);
		this.gffElement = split[11];
		
		return true;
	}
	
	public void setGFFElement(String gffElement) {
		this.gffElement = gffElement;
	}
	
	public GFFElement getGFFElement() {
		List<MetaInformation> metaInfos = this.ds.getMetaInformationManager().get(GFFTree.MYTYPE);
		
		if(metaInfos != null && metaInfos.size() > 0) {
			MetaInformation first = metaInfos.get(0);
			if(first instanceof GFFTree) {
				GFFTree gffTree = (GFFTree)first;
				GFFElement e = gffTree.getGFFElement(this.gffElement);
				return e;
			}
		}
		
		return null;
	}

	@Override
	public Class<?> getResultClass() {
		return SNP.class;
	}

	@Override
	public String getName() {
		return "SNP Characterization";
	}
	
	public void setPersonID(String id) {
		this.personID = id;
	}
	
	public String getPersonID() {
		return this.personID;
	}
	
	public SNP getSNP() {
		return this.snp;
	}

	public void setOriginalAA(String protSeq) {
		this.originalAA = protSeq;
	}

	public void setModifiedAAA(String protSeq) {
		this.modifiedAAA = protSeq;
	}
	
	public void setModifiedAAB(String protSeq) {
		this.modifiedAAB = protSeq;
	}

	public void setIsModifiedAAA(boolean b) {
		this.isProtSeqModifiedA = b;
	}
	
	public void setIsModifiedAAB(boolean b) {
		this.isProtSeqModifiedB = b;
	}
	
	public String getModifiedAAA() {
		return this.modifiedAAA;
	}
	
	public String getModifiedAAB() {
		return this.modifiedAAB;
	}
	
	public String getOriginalAA() {
		return this.originalAA;
	}
	
	public boolean nonSynonymous() {
		return isProtSeqModifiedA || isProtSeqModifiedB;
	}
	
	public void setCharacterizationFeature(int feature) {
		this.characterizationFeature = feature;
	}
	
	public int getImpact() {
		int feature = this.characterizationFeature;
		boolean nonSynonymous = nonSynonymous();
		
		switch(feature) {
		case FIVE_PRIME_UTR:
			return IMPACT_MEDIUM;
		case FIVE_PRIME_UTR_START_CODON_INSERTED:
			return IMPACT_MEDIUM;
		case SPLICE_SITE_ACCEPTOR:
			return IMPACT_HIGH;
		case SPLICE_SITE_DONOR:
			return IMPACT_HIGH;
		case START_CODON_LOST:
			return IMPACT_HIGH;
		case STOP_GAINED:
			return IMPACT_HIGH;
		case STOP_LOST:
			return IMPACT_HIGH;
		case THREE_PRIME_UTR:
			return IMPACT_LOW;
		case INTRON:
			return IMPACT_NO;
		case EXON:
			return IMPACT_MEDIUM;
		case FIVE_PRIME_UTR_SYNONYMOUS:
			return IMPACT_NO;
		default:
			if(nonSynonymous) {
				return IMPACT_HIGH;
			} else {
				return IMPACT_NO;
			}
		}
	}

	public String getSNPClass() {
		switch(this.characterizationFeature) {
		case FIVE_PRIME_UTR:
			return "5' UTR";
		case FIVE_PRIME_UTR_START_CODON_INSERTED:
			return "5' UTR Start Inserted";
		case FIVE_PRIME_UTR_SYNONYMOUS:
			return "5' UTR Synonymous";
		case SPLICE_SITE_ACCEPTOR:
			return "Splice Site Acceptor";
		case SPLICE_SITE_DONOR:
			return "Splice Site Donor";
		case START_CODON_LOST:
			return "Start Codon Lost";
		case STOP_GAINED:
			return "Stop Codon Gained";
		case STOP_LOST:
			return "Stop Codon Lost";
		case THREE_PRIME_UTR:
			return "3' UTR";
		case INTRON:
			return "Intron";
		default:
			return "";
		}
	}
	
	public char getIndividualNucleotideA() {
		return this.nucleotideA;
	}
	
	public char getIndividualNucleotideB() {
		return this.nucleotideB;
	}
	
	public void setIndividualNucleotideA(char nucleotide) {
		this.nucleotideA = nucleotide;
	}
	
	public void setIndividualNucleotideB(char nucleotide) {
		this.nucleotideB = nucleotide;
	}

	public String getPersonName() {
		return this.personName;
	}
	
	public void setPersonName(String name) {
		this.personName = name;
	}

	public void setDataStorage(DataStorage dataStorage) {
		this.ds = dataStorage;
	}
}
