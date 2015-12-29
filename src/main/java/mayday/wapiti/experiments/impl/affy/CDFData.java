package mayday.wapiti.experiments.impl.affy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.structures.maps.MultiHashMap;
import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;
import mayday.wapiti.containers.featuresummarization.IFeatureSummarizationMap;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cdf.FusionCDFHeader;
import affymetrix.fusion.cdf.FusionCDFProbeGroupInformation;
import affymetrix.fusion.cdf.FusionCDFProbeInformation;
import affymetrix.fusion.cdf.FusionCDFProbeSetInformation;
import affymetrix.fusion.cel.FusionCELData;

public class CDFData implements IFeatureSummarizationMap {
	
	protected boolean[] pm;
	protected MultiHashMap<String, Integer> probeSetsPM = new MultiHashMap<String, Integer>();
	protected MultiHashMap<String, Integer> probeSetsMM = new MultiHashMap<String, Integer>();
	protected String chipType;
	
	protected ArrayLayout layout;
	
	public CDFData(FusionCDFData cdfData) {		
		
		chipType = cdfData.getChipType();
		
		FusionCDFHeader header = cdfData.getHeader();		
		int c = header.getCols();
		int r = header.getRows();
		
		int numTotalCells = r*c;

		layout = new ArrayLayout(r,c);
		
		pm = new boolean[numTotalCells];
		
		FusionCDFProbeSetInformation PSinfo = new FusionCDFProbeSetInformation();			
		FusionCDFProbeGroupInformation Ginfo = new FusionCDFProbeGroupInformation();
		FusionCDFProbeInformation Cinfo = new FusionCDFProbeInformation();
		
		int numPS = header.getNumProbeSets();
		
		for (int probeSetID = 0; probeSetID!=numPS; ++probeSetID) {
			
			cdfData.getProbeSetInformation(probeSetID, PSinfo);
			String psName = cdfData.getProbeSetName(probeSetID);
			int numGroup = PSinfo.getNumGroups();
			
			for (int groupID = 0; groupID!=numGroup; ++ groupID) {
				PSinfo.getGroup(groupID, Ginfo);
				int numCells = Ginfo.getNumCells();
				
				for (int cellID = 0; cellID!=numCells; ++cellID) {
					Ginfo.getCell(cellID, Cinfo);
					int x = Cinfo.getX();
					int y = Cinfo.getY();
					int index = FusionCELData.xyToIndex(x, y, r, c);
					char pBase = Character.toLowerCase(Cinfo.getPBase());
					char tBase = Character.toLowerCase(Cinfo.getTBase());
					boolean isPM = false;
					switch(pBase) {
					case 'a': isPM = (tBase=='t'); break;
					case 't': isPM = (tBase=='a'); break;
					case 'c': isPM = (tBase=='g'); break;
					case 'g': isPM = (tBase=='c'); break;
					}					
					pm[index] = isPM;
					if (isPM)
						probeSetsPM.put(psName, index);
					else
						probeSetsMM.put(psName, index);
				}
			}
		}
		
		
		
	}
	
	public boolean isPM(int index) {
		return pm[index];
	}
	
	public boolean isMM(int index) {
		return !pm[index];
	}
	
	public List<Integer> getProbeSetIndices_PM(String ProbeSetName) {
		return probeSetsPM.get(ProbeSetName);
	}
	
	public List<Integer> getProbeSetIndices_MM(String ProbeSetName) {
		return probeSetsMM.get(ProbeSetName);
	}
	
	public Collection<String> getProbeSetNames() {
		return probeSetsPM.keySet();
	}
	
	public String getChipType() {
		return chipType;
	}
	
	public boolean[] pm() {
		return pm; 
	}

	public String getName() {
		return getChipType();
	}

	public List<String> getSubFeatures(String feature) {
		List<Integer> li = probeSetsPM.get(feature);
		ArrayList<String> ret = new ArrayList<String>(li.size());
		for (Integer i : li)
			ret.add(i.toString());
		return ret;
	}

	public Set<String> featureNames() {
		return probeSetsPM.keySet();
	}
	
	public String toString() {
		return getChipType()+ " ("+featureNames().size()+" summary features)";
	}
	
	public ArrayLayout getLayout() {
		return layout;
	}

	public void writeToStream(BufferedWriter bw) throws IOException {
		FeatureSummarizationMap.writeToStream(this, bw);
	}
	
	
	
	// CACHING //
	
	protected static HashMap<String,  WeakReference<CDFData>> cached = new HashMap<String, WeakReference<CDFData>>();

	public static CDFData get(String cdfID, boolean interactive, String interactiveSearchDir) {
		WeakReference<CDFData> wr = cached.get(cdfID);
		if (wr!=null) {
			CDFData ret = wr.get();
			if (ret!=null) {
				System.out.println("Found CDF data for \'"+cdfID+"\' in cache.");
				return ret;
			}
		}
		FusionCDFData cdf = interactive?CDFRepository.getCDFinteractive(cdfID, interactiveSearchDir):CDFRepository.getCDF(cdfID);
		if (cdf==null)
			return null;
		CDFData cdfd = new CDFData(cdf);
		System.out.println("Added CDF data for \'"+cdfID+"\' to cache.");
		cached.put(cdfID, new WeakReference<CDFData>(cdfd));
		return cdfd;
	}
	
}
