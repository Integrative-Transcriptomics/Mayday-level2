package mayday.GWAS.visualizations.SNPExpHeatMap;

import java.util.ArrayList;
import java.util.List;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.HaplotypesList;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;

public class SNPExpressionCalculator {

	private DataStorage ds;
	private SNPList snps;
	
	public SNPExpressionCalculator(DataStorage ds, SNPList snps) {
		this.ds = ds;
		this.snps = snps;
	}
	
	public DoubleMatrix[] calculateSNPVectors(boolean weighted) {
		int numSNPs = snps.size();
		int numGenes = ds.getGenes().size();
		
		GeneList genes = ds.getGenes();
		List<Subject> personsA = ds.getSubjects().getAffectedSubjects();
		List<Subject> personsU = ds.getSubjects().getUnaffectedSubjects();
		
		double numAffected = personsA.size();
		double numUnAffected = personsU.size();
		
		DoubleMatrix matrixA = new DoubleMatrix(numSNPs, numGenes);
		DoubleMatrix matrixU = new DoubleMatrix(numSNPs, numGenes);
		
		for(int i = 0; i < numSNPs; i++) {
			SNP s = snps.get(i);
			System.out.println((i+1) + " = " + s.getID());
			for(int j = 0; j < numGenes; j++) {
				Gene g = genes.getGene(j);
				List<Double> refA = getRefExp(s, g, personsA);
				List<Double> refU = getRefExp(s, g, personsU);
				List<Double> hetA = getHeteroExp(s, g, personsA);
				List<Double> hetU = getHeteroExp(s, g, personsU);
				List<Double> homA = getHomoExp(s, g, personsA);
				List<Double> homU = getHomoExp(s, g, personsU);
				
				double refAMedian = getMedian(refA);
				double refUMedian = getMedian(refU);
				double hetAMedian = getMedian(hetA);
				double hetUMedian = getMedian(hetU);
				double homAMedian = getMedian(homA);
				double homUMedian = getMedian(homU);		
				
				double expRefA = refAMedian;
				double expRefU = refUMedian;
				
				double expHetA = hetAMedian * 2; 
				double expHetU = hetUMedian * 2; 
				
				double expHomA = homAMedian;
				double expHomU = homUMedian;
				
				if(weighted) {
					expRefA *= (refA.size()/numAffected);
					expRefU *= (refU.size()/numUnAffected);
					expHetA *= (hetA.size()/numAffected);
					expHetU *= (hetU.size()/numUnAffected);
					expHomA *= (homA.size()/numAffected);
					expHomU *= (homU.size()/numUnAffected);
				}
				
				double expValueAffected = (expHetA+expHomA)/2 - expRefA;
				double expValueUnAffected = (expHetU+expHomU)/2 - expRefU;
				
				matrixA.setValue(i, j, expValueAffected);
				matrixU.setValue(i, j, expValueUnAffected);
			}
		}
		
		return new DoubleMatrix[]{matrixA, matrixU};
	}
	
	private double getMedian(List<Double> values) {
		if(values.size() == 0) {
			return 0;
		}
		DoubleVector v = new DoubleVector(values);
		return v.median();
	}

	private List<Double> getHomoExp(SNP s, Gene g, List<Subject> persons) {
		double[] originalValues = g.getValues();
		char ref = s.getReferenceNucleotide();
		ArrayList<Double> newValues = new ArrayList<Double>();
		
		HaplotypesList haplos = ds.getHaplotypes();
		
		for(int i = 0; i < persons.size(); i++) {
			Subject p = persons.get(i);
			Haplotypes h = haplos.get(p.getIndex());
			char a = h.getSNPA(s.getIndex());
			char b = h.getSNPB(s.getIndex());
			if(a == b && a != ref) {
				double value = originalValues[p.getIndex()];
				newValues.add(value);
			}
		}
		
		return newValues;
	}

	private List<Double> getHeteroExp(SNP s, Gene g, List<Subject> persons) {
		double[] originalValues = g.getValues();
		char ref = s.getReferenceNucleotide();
		ArrayList<Double> newValues = new ArrayList<Double>();
		
		HaplotypesList haplos = ds.getHaplotypes();
		
		for(int i = 0; i < persons.size(); i++) {
			Subject p = persons.get(i);
			Haplotypes h = haplos.get(p.getIndex());
			char a = h.getSNPA(s.getIndex());
			char b = h.getSNPB(s.getIndex());
			if(a != b && (a == ref || b == ref)) {
				double value = originalValues[p.getIndex()];
				newValues.add(value);
			}
		}
		
		return newValues;
	}

	private List<Double> getRefExp(SNP s, Gene g, List<Subject> persons) {
		double[] originalValues = g.getValues();
		char ref = s.getReferenceNucleotide();
		ArrayList<Double> newValues = new ArrayList<Double>();
		
		HaplotypesList haplos = ds.getHaplotypes();
		
		for(int i = 0; i < persons.size(); i++) {
			Subject p = persons.get(i);
			Haplotypes h = haplos.get(p.getIndex());
			char a = h.getSNPA(s.getIndex());
			char b = h.getSNPB(s.getIndex());
			if(a == b && a == ref) {
				double value = originalValues[p.getIndex()];
				newValues.add(value);
			}
		}
		
		return newValues;
	}
}
