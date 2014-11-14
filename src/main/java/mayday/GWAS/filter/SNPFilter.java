package mayday.GWAS.filter;

import javax.swing.event.ChangeListener;

import mayday.GWAS.data.SNP;


/**
 * @author jaeger
 *
 */
public interface SNPFilter {
	
	public Boolean passesFilter(SNP snp);
	
	public void addChangeListener( ChangeListener listener );
	public void removeChangeListener( ChangeListener listener );

	public String toDescription();
	
	// call this method to make sure that the filter removes all listeners added to other objects *//
	public void dispose();
}
