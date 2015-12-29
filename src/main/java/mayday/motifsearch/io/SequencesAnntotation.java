package mayday.motifsearch.io;

import java.util.*;

import mayday.motifsearch.preparation.GeneLocation;

/**
 * Class which holds the Information of a Sequence annotation used to store
 * annotation information from a extraction of Sequences.
 * 
 * @author Frederik Weber
 */

public class SequencesAnntotation {

	private ArrayList<GeneLocation> geneLocs;
	private ArrayList<String> geneSequences;
	private int upstreamLength;
	private int downstreamLength;
	private int minUpstreamLength;
	private ArrayList<Long> takenFromPosition;
	private ArrayList<Long> takenToPosition;

	public SequencesAnntotation() {
		super();
		this.geneLocs = new ArrayList<GeneLocation>();
		this.geneSequences = new ArrayList<String>();
		this.takenFromPosition = new ArrayList<Long>();
		this.takenToPosition = new ArrayList<Long>();
	}

	/**
	 * add to a new annotation for a sequence
	 * 
	 */
	public final void addGeneLocAndGeneSequence(GeneLocation geneLoc,
			String geneSequence, long takenFromPosition, long takenToPosition) {
		this.geneLocs.add(geneLoc);
		this.geneSequences.add(geneSequence);
		this.takenFromPosition.add(takenFromPosition);
		this.takenToPosition.add(takenToPosition);
	}

	public static final String geneLocAndSequencestoXML(GeneLocation geneLoc,
			String geneSequence, long takenFromPosition, long takenToPosition) {
		return "<sequence geneOriginSynonym=\"" + geneLoc.getSynonym() + "\""
				+ " geneFromPos=\"" + geneLoc.getFrom() + "\""
				+ " geneToPos=\"" + geneLoc.getTo() + "\"" + " code=\""
				+ geneLoc.getCode() + "\"" + " COG=\"" + geneLoc.getCOG()
				+ "\"" + " geneOriginName=\"" + geneLoc.getGeneName() + "\""
				+ " PID=\"" + geneLoc.getPID() + "\"" + " length=\""
				+ geneLoc.getLengthProtein() + "\"" + " strand=\""
				+ (geneLoc.isPlusStrand() ? "+" : "-") + "\""
				+ " takenfromposition=\"" + takenFromPosition + "\""
				+ " takentoposition=\"" + takenToPosition + "\""
				+ " product=\"" + geneLoc.getProduct().split(" ")[0] + "\""
				+ ">\n" + geneSequence + "\n</sequence>";
	}

	public ArrayList<GeneLocation> getGeneLocs() {
		return this.geneLocs;
	}

	public ArrayList<String> getGeneSequences() {
		return this.geneSequences;
	}

	public int getUpstreamLength() {
		return upstreamLength;
	}

	public void setUpstreamLength(int upstreamLength) {
		this.upstreamLength = upstreamLength;
	}

	public int getDownstreamLength() {
		return downstreamLength;
	}

	public void setDownstreamLength(int downstreamLength) {
		this.downstreamLength = downstreamLength;
	}

	public int getMinUpstreamLength() {
		return minUpstreamLength;
	}

	public void setMinUpstreamLength(int minUpstreamLength) {
		this.minUpstreamLength = minUpstreamLength;
	}

	public ArrayList<Long> getTakenFromPosition() {
		return takenFromPosition;
	}

	public ArrayList<Long> getTakenToPosition() {
		return takenToPosition;
	}

	public boolean isEmpty() {
		return this.geneSequences.isEmpty();
	}
}
