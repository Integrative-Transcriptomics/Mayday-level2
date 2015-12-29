/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mayday.expressionmapping.view.information;


import java.util.List;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 *
 * @author Stephan Gade
 */
public interface AttractorTable {
	
	public boolean isMainListSet();
	
	public boolean isSubListSet();
	
	public TIntArrayList getMainList();
	
	public TIntArrayList getSubList();
	
	public PointList<? extends DataPoint> getPoints();
	
	public List<String> getAnnotations();

}
