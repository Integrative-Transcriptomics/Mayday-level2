package mayday.transkriptorium.properties;

import mayday.core.gui.properties.items.InfoItem;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

@SuppressWarnings("serial")
public class CoordinateItem extends InfoItem {

	
	
	public CoordinateItem(String title, AbstractGeneticCoordinate agc) {
		super(title, makeString(agc));
	}
	
	public static String makeString (AbstractGeneticCoordinate agc) {
		return "<html><table cellspacing=0 cellpadding=1>"+
				"<tr><td>Species:    </td><td><b>"+agc.getChromosome().getSpecies().getName()+"</b></td></tr>"+
				"<tr><td>Chromosome: </td><td><b>"+agc.getChromosome().getId()+"</b></td></tr>"+
				"<tr><td>Position:   </td><td><b>"+agc.getStrand()+"</b> Strand, <b>"+agc.getFrom()+" - "+agc.getTo()+"</td></tr>"+
				"<tr><td>Length: </td><td><b>"+agc.length()+"</b></td></tr></table>";						
	}
	

}
