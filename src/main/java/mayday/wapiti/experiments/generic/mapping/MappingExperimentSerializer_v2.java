package mayday.wapiti.experiments.generic.mapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.advanced.ChromosomeSetIterator;
import mayday.genetics.advanced.VariableGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class MappingExperimentSerializer_v2 extends
		AbstractExperimentSerializer<MappingExperiment> {

	@Override
	protected MappingExperiment loadDataFromStream(String name, String sourceDescription, TransMatrix tm, DataInputStream dis) throws IOException {

		MappingData md = new MappingData();
		
		String compactionString = readString(dis);
		LinkedList<String> compactionParts = new LinkedList<String>(Arrays.asList(compactionString.split("\t")));
		md.setCompaction(compactionParts);

		long rc = dis.readLong();
		
		double scale = 10000d/rc;
		int count=0;
		
		Chromosome lastChrome = null;
		ChromosomeSetContainer tempCSC = new ChromosomeSetContainer();
		
		VariableGeneticCoordinate vgc = new VariableGeneticCoordinate(tempCSC);
		
		long from, to;
		Strand s;
		
		for (long i = 0; i!=rc; ++i) {
			boolean newLastChrome = dis.readBoolean();
			if (newLastChrome) {
				String spec = readString(dis);
				String chrome = readString(dis);
				lastChrome = tempCSC.getChromosome(SpeciesContainer.getSpecies(spec), chrome);
				vgc.setChromosome(lastChrome);
			}
			from = dis.readLong(); 
			to = dis.readLong();
			s = Strand.values()[(int)dis.readByte()]; // changed to byte (from int) for optimization
			vgc.setFrom(from);
			vgc.setTo(to);
			vgc.setStrand(s);
			md.addRead(vgc);
			if (count%100000 == 00)
				progress.setProgress((int)(count*scale));
			++count;
		}
		
		md.compact();
		
		MappingExperiment me = new MappingExperiment(name, sourceDescription, tm);
		me.setInitialData(md);
		
		return me;
	}

	@Override
	protected void writeDataToStream(MappingExperiment e, DataOutputStream dos) throws IOException {
		
		writeString(dos, e.initialData.getCompactionInitializer());
		
		dos.writeLong(e.initialData.getReadCount());

		double scale = 10000d/e.initialData.getReadCount();
		int count=0;
		
		Chromosome lastChrome = null;
		
		ChromosomeSetIterator csi = new ChromosomeSetIterator(e.initialData.asChromosomeSetContainer(), ChromosomeSetIterator.ITERATE_UNSORTED);
		for (AbstractGeneticCoordinate algc : csi) {
			if (algc.getChromosome()!=lastChrome) {
				dos.writeBoolean(true);
				lastChrome = algc.getChromosome();
				writeString(dos, lastChrome.getSpecies().getName());
				writeString(dos, lastChrome.getId());
			} else {
				dos.writeBoolean(false);
			}
			dos.writeLong(algc.getFrom());
			dos.writeLong(algc.getTo());
			dos.writeByte(algc.getStrand().ordinal());  // changed to byte (from int) for optimization
			if (count%100000 == 00)
				progress.setProgress((int)(count*scale));
			++count;
		}
		dos.flush();		
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
			getClass(),
			MC+".mapping_v2", 
			null,
			MC, 
			null, 
			"Florian Battke", 
			"battke@informatik.uni-tuebingen.de", 
			"Serializes mapped-read-based experiments", 
			"mapped reads"
		);
	}

}
