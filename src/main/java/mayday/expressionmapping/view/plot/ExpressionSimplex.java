package mayday.expressionmapping.view.plot;

import java.util.Collection;

import mayday.core.Probe;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
/**
 * @author Stephan Gade
 *
 */
public interface ExpressionSimplex {
    
    /**
     * painting
     */
    public void plot();
    
    /**
     * @param mainAccessList (list of probe identifier)
     */
    public void changeProbeSelection(TIntArrayList mainAccessList);
    
    /**
     * @param probes
     */
    public void changeProbeSelection(Collection<Probe> probes);
    
    /**
     * clear probe selection
     */
    public void clearProbeSelection();
}
