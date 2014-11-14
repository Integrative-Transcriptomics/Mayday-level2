package mayday.GWAS.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.maps.BidirectionalHashMap;

/**
 * @author jaeger
 *
 */
public class GeneList extends ProbeList {
	
	private int nameLength = 0;
	
	/**
	 * @param dataSet
	 */
	public GeneList(DataSet dataSet) {
		super(dataSet, true);
	}
	
	/**
	 * @return number of genes in this list
	 */
	public int size() {
		return this.getNumberOfProbes();
	}
	
	private BidirectionalHashMap<String, Integer> idMapping = new BidirectionalHashMap<String, Integer>();
	
	/**
	 * @param g
	 */
	public void addGene(Gene g) {
		idMapping.put(g.getName(), this.getNumberOfProbes());
		this.addProbe(g);
	}
	
	/**
	 * @param name
	 * @return gene with the specified name
	 */
	public Gene getGene(String name) {
		return (Gene)this.getProbe(name);
		//return (Gene)this.getProbe(((Integer)idMapping.get(name)).intValue());
	}
	
	/**
	 * @param id
	 * @return gene with the specified id
	 */
	public Gene getGene(int id) {
		return this.getGene(idMapping.get(id).toString());
	}
	
	/**
	 * @param g
	 * @return id of the specified gene
	 */
	public int indexOf(Gene g) {
		int i = 0;
		for(Probe g2 : this) {
			if(g2.equals(g)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	/**
	 * @param chromosome
	 * @return genes on the specified chromosome
	 */
	public GeneList getGenesOnChromosome(String chromosome) {
		GeneList genes = new GeneList(this.getDataSet());
		for(int i = 0; i < this.getNumberOfProbes(); i++) {
			if(this.getGene(i).getChromosome().equals(chromosome)) {
				genes.addGene(this.getGene(i));
			}
		}
		return genes;
	}

	/**
	 * @return maximum length of the names
	 */
	public int getMaxNameLength() {
		if(nameLength == 0) {
			for(Probe g: this) {
				if(g.getName().length() > nameLength) {
					nameLength = g.getName().length();
				}
			}
		}
		return nameLength;
	}

	public void serialize(BufferedWriter bw) throws IOException {
		for(Probe g : this) {
			bw.append(((Gene)g).serialize());
			bw.append("\n");
		}
	}
	
	public String toString() {
		return "Genes (" + size() + ")";
	}
	
    public GeneList cloneProperly() {
    	GeneList l_geneList = (GeneList)clone();
        l_geneList.setName( this.getName()+(new Random()).nextInt() );
        return l_geneList;
    }

	public String[] getGeneNames() {
		String[] geneNames = new String[size()];
		int i = 0;
		for(Probe p : this) {
			Gene g = (Gene)p;
			geneNames[i++] = g.getDisplayName();
		}
		return geneNames;
	}
}
