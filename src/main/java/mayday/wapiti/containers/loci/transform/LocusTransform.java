package mayday.wapiti.containers.loci.transform;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.advanced.LocusTransformer;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class LocusTransform {

	public static void run(TransMatrix transMatrix) {
		
		StringSetting ss = new StringSetting( "Locus Set Name", "Specify a new name for the merged Locus Data", "", false);
		LocusTransformSetting lts = new LocusTransformSetting("Transformation");
		LocusMapSetting lms = new LocusMapSetting();
		
		HierarchicalSetting hs = new HierarchicalSetting("Transform Locus Data")
			.addSetting(lms)
			.addSetting(ss)
			.addSetting(lts)
			;
		
		SettingDialog sd = new SettingDialog(transMatrix.getFrame(), hs.getName(), hs);
		sd.showAsInputDialog();
		
		LocusMap m2 = new LocusMap(ss.getStringValue());
		
		if (!sd.canceled()) {
			LocusMap lm = lms.getLocusMap();
			LocusTransformer lt = lts.getTransformer();
			for (String s : lm.keySet()) {
				AbstractGeneticCoordinate gc = lm.get(s);
				AbstractGeneticCoordinate[] atgc = lt.transform(gc, ChromosomeSetContainer.getDefault());
				if (atgc.length>1) {
					int suffix=0;
					for (AbstractGeneticCoordinate tgc : atgc)
						m2.put( s + (++suffix) , tgc );
				} else {
					m2.put( s , atgc[0] );	
				}
			}
			LocusMapContainer.INSTANCE.add(m2);
		}
		
	}
	
}
