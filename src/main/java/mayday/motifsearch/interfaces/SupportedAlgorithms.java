package mayday.motifsearch.interfaces;

/**
 * contains all motif search algorithms supported in Mayday
 * 
 */

import java.util.concurrent.ConcurrentHashMap;

public class SupportedAlgorithms extends ConcurrentHashMap<String, IMotifSearchAlgoStarter>{
    private static final long serialVersionUID = 1L;

    public SupportedAlgorithms(){
	super();
	new ConcurrentHashMap<String, IMotifSearchAlgoStarter>();

	IMotifSearchAlgoStarter tempMoSA;
	tempMoSA = new MEMEStarter();
	this.put(tempMoSA.toString(),tempMoSA);

	//	IMotifSearchAlgoStarter tempMoSA2 = new MEME2Starter();
	//	this.put(tempMoSA2.toString(),tempMoSA2);
	//	
	//	IMotifSearchAlgoStarter tempMoSA3 = new MEME3Starter();
	//	this.put(tempMoSA3.toString(),tempMoSA3);

    }
}
