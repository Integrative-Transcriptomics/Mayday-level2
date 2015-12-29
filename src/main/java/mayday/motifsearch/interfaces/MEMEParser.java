package mayday.motifsearch.interfaces;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Sequence;
import mayday.motifsearch.model.Site;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nux.xom.xquery.ResultSequence;
import nux.xom.xquery.XQuery;

public class MEMEParser implements IMotifSearchAlgoParser{

    private Document document;
    private ConcurrentHashMap<String, Motif> motifs;
    private ConcurrentHashMap<String, Sequence> finalSequences;

    public MEMEParser(String destFolderPath, ProbeList allProbes) throws Exception{
	super();
	this.document = new Builder().build(new File(destFolderPath + java.io.File.separator +"meme.xml"));
	this.motifs = this.parseMotifs();
	this.finalSequences = this.parseSitesToSequencesAndMotifs(this.motifs, this.parseSequences(allProbes));
    }

    public MEMEParser(){
	super();
    }


    public boolean isParsableDataInFolderWithPath(String destFolderPath) {
	if((new File(destFolderPath + java.io.File.separator +"meme.xml")).exists()){
	    return true;
	} else {
	    return false;
	}

    }

    public ConcurrentHashMap<String, Sequence> getFinalSequences() {
	return this.finalSequences;
    }


    public ConcurrentHashMap<String, Motif> getMotifs() {
	return motifs;
    }

    private ConcurrentHashMap<String, Sequence>parseSequences(ProbeList allProbes) throws Exception{
	ConcurrentHashMap<String, Sequence> sequencesHashMap = new ConcurrentHashMap<String, Sequence>();

	XQuery xquery = new XQuery("//training_set/sequence", null);
	ResultSequence resultSequence = xquery.execute(this.document);

	Nodes sequenceNodes = resultSequence.toNodes();

	int numSequences = sequenceNodes.size();
	System.out.println("Num sequence nodes = " + numSequences);
	for (int i = 0; i<numSequences; i++){
	    Node motifNode = sequenceNodes.get(i);
	    String id = motifNode.query("@id").get(0).getValue();
	    String name = motifNode.query("@name").get(0).getValue();
//	    String probeName = motifNode.query("@probeName").get(0).getValue();
	    int length = Integer.valueOf(motifNode.query("@length").get(0).getValue());
	    
	    Sequence tempSequence = new Sequence(id, name, length);
	    
	    for(Probe p: allProbes) {
	    	if(name.equals(p.getDisplayName())) {
	    		tempSequence.addProbe(p);
	    	}
	    }
	    
	    sequencesHashMap.put(tempSequence.getID(), tempSequence);

	}

	return sequencesHashMap;
    }

    private ConcurrentHashMap<String, Motif> parseMotifs() throws Exception{
	ConcurrentHashMap<String, Motif> motifsHashMap = new ConcurrentHashMap<String, Motif>();
	XQuery xquery = new XQuery("//motifs/motif", null);
	ResultSequence resultSequence = xquery.execute(this.document);

	Nodes motifNodes = resultSequence.toNodes();

	int numMotifs = motifNodes.size();
	for (int i = 0; i<numMotifs; i++){
	    Node motifNode = motifNodes.get(i);
	    String id = motifNode.query("@id").get(0).getValue();
	    String name = motifNode.query("@name").get(0).getValue();
	    double significanceValue = Double.valueOf(motifNode.query("@e_value").get(0).getValue());

	    /* scores or probabilities*/
	    String matrixStyle = "probabilities"; //"scores";
	    Nodes aInMatrix = motifNode.query(matrixStyle + "/alphabet_matrix/alphabet_array/value[@letter_id = \"letter_A\"]");
	    Nodes cInMatrix = motifNode.query(matrixStyle + "/alphabet_matrix/alphabet_array/value[@letter_id = \"letter_C\"]");
	    Nodes gInMatrix = motifNode.query(matrixStyle + "/alphabet_matrix/alphabet_array/value[@letter_id = \"letter_G\"]");
	    Nodes tInMatrix = motifNode.query(matrixStyle + "/alphabet_matrix/alphabet_array/value[@letter_id = \"letter_T\"]");

	    int matrixLength = aInMatrix.size();

	    double[] aInMatrixTemp = new double[matrixLength];
	    double[] cInMatrixTemp = new double[matrixLength];
	    double[] gInMatrixTemp = new double[matrixLength];
	    double[] tInMatrixTemp = new double[matrixLength];

	    for (int j = 0; j<matrixLength; j++){
		aInMatrixTemp[j] = (Double.valueOf(aInMatrix.get(j).getValue()));
		cInMatrixTemp[j] = (Double.valueOf(cInMatrix.get(j).getValue()));
		gInMatrixTemp[j] = (Double.valueOf(gInMatrix.get(j).getValue()));
		tInMatrixTemp[j] = (Double.valueOf(tInMatrix.get(j).getValue()));
	    }

	    double[][] values =  new double[4][matrixLength];

	    values[0] = aInMatrixTemp;
	    values[1] = cInMatrixTemp;
	    values[2] = gInMatrixTemp;
	    values[3] = tInMatrixTemp;

	    DoubleMatrix bindingMatrix = new DoubleMatrix(values, true);

	    Motif tempMotif = new Motif(id, bindingMatrix);
	    tempMotif.setName(name);
	    tempMotif.setSignificanceValue(significanceValue);
	    motifsHashMap.put(tempMotif.getID(), tempMotif);

	}
	return motifsHashMap;
    }

    private ConcurrentHashMap<String, Sequence> parseSitesToSequencesAndMotifs(ConcurrentHashMap<String, Motif> motifsHashMap, ConcurrentHashMap<String, Sequence> sequencesHashMap) throws Exception{

	XQuery xquery = new XQuery("//scanned_sites", null);
	ResultSequence resultSequence = xquery.execute(this.document);

	Nodes sitesNodes = resultSequence.toNodes();
	int numSitesSequence = sitesNodes.size();

	for (int j = 0; j<numSitesSequence; j++){
	    Sequence actSequence = sequencesHashMap.get(sitesNodes.get(j).query("@sequence_id").get(0).getValue());

	    Nodes siteNodes = sitesNodes.get(j).query("scanned_site");

	    int numSites = siteNodes.size();
	    for (int i = 0; i<numSites; i++){
		Node motifNode = siteNodes.get(i);
		String motifID = motifNode.query("@motif_id").get(0).getValue();
		int position = Integer.valueOf(motifNode.query("@position").get(0).getValue());
		double significanceValue = Double.valueOf(motifNode.query("@pvalue").get(0).getValue());

		Site tempSite = new Site(motifsHashMap.get(motifID), position);
		tempSite.setSignificanceValue(significanceValue);
		actSequence.addSite(tempSite);
	    }
	}

	return sequencesHashMap;
    }

}
