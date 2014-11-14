/*
 * Created on 29.11.2005
 */
package mayday.wapiti.containers.loci;

import java.util.HashMap;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.LocusImportPlugin;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;

public class CreateTiled extends AbstractLocusImportPlugin implements LocusImportPlugin {

	protected LocusMapSetting lms;
	protected StringSetting lmName;
	protected IntSetting size;
	protected IntSetting overlap;
	
	public final PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".CreateTiling",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates locus data with a tiling approach",
				"Create tiling locus data"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_OTHER);
		return pli;
	}

	public Setting getSetting() {
		if (mySetting==null) {
			mySetting = new HierarchicalSetting("Create tiling loci")
				.addSetting(lmName = new StringSetting("Locus Set Name",null,"",false))
				.addSetting(lms = new LocusMapSetting().setName("Template Locus Data"))
				.addSetting(size = new IntSetting("Locus Size",null, 100, 1, null, true, false))
				.addSetting(overlap = new IntSetting("Overlap (bp)",
						"Positive values specify overlapping loci,\n negative values specify distance between loci", 50,1,null,true,false));
				;
		}
		return mySetting;
	}
	
	public void init() {}

	@SuppressWarnings("unchecked")
	public LocusMap importFrom() {
		
		ChromosomeSetContainer csc = lms.getLocusMap().asChromosomeSetContainer();
		
		LocusMap lm = new LocusMap(lmName.getStringValue());
		long delta = size.getIntValue() - overlap.getIntValue();
		int size = this.size.getIntValue();
		
		for (Chromosome c : csc.getAllChromosomes()) {
			for (Strand s : new Strand[]{Strand.PLUS, Strand.MINUS}) {
				Chromosome tgtc = ChromosomeSetContainer.getDefault().getChromosome(c);				
				for ( long start = 0; start<=c.getLength(); start+=delta ) {
					long end = start+size-1;
					if (((AbstractLocusChromosome)c).isOverlapped(start, end, s)) {
						GeneticCoordinate gc = new GeneticCoordinate(tgtc,s,start,start+size-1);
						lm.put(gc.toString(), gc);
					}
				}
			}
		}
		
		return lm;

	}




}
