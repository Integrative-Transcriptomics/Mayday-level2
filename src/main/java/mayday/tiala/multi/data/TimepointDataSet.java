package mayday.tiala.multi.data;

import java.util.ArrayList;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.TimeseriesMIO;

@SuppressWarnings("serial")
public class TimepointDataSet extends ArrayList<Double> {

	protected DataSet dataSet;
	
	public TimepointDataSet( DataSet ds ) {
		fill(ds);
	}
	
	@Deprecated 
	public TimepointDataSet( DataSet ds, boolean suppressFilling ) {}
	
	private TimepointDataSet() {};
	
	public void fill( DataSet ds ) throws RuntimeException {
		ensureCapacity(ds.getMasterTable().getNumberOfExperiments());
		
		TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(ds, true, false);
		
		boolean done = tsm!=null;
		
		if (tsm!=null)
			done = parseTimeseriesMIO(ds, tsm);
		
		if (!done) {
			throw new RuntimeException( 
					"Could not determine experiment timepoints for DataSet \""+ds.getName()+"\".\n" +
					"Rename experiments with numeric names or attach Timepoint meta information." );
		} else {
			dataSet = ds;
		}
	}

	public void addTimeSeriesMIO(DataSet ds) {
		MIGroupSelection<MIType> mgs = ds.getMIManager().getGroupsForType(TimeseriesMIO.myType);
		MIGroup mg;
		if (mgs.size()==0) {
			mg = ds.getMIManager().newGroup(TimeseriesMIO.myType, "Timepoints");
		} else {
			mg = mgs.get(0);
		}
		TimeseriesMIO tsm = new TimeseriesMIO();
		tsm.setValue(new LinkedList<Double>(this));
		mg.add(ds, tsm);
	}
	
	protected boolean parseTimeseriesMIO(DataSet ds, TimeseriesMIO tsm) {
		if (tsm.applicableTo(ds)) {
			addAll(tsm.getValue());
			return true;
		}
		return false;
	}
	
	public DataSet getDataSet() {
		return dataSet;		
	}
	
	public TimepointDataSet createShifted( double timeShift ) {
		TimepointDataSet result = new TimepointDataSet();
		result.dataSet=dataSet;
		for (Double tp : this)
			result.add(tp+timeShift);
		result.addTimeSeriesMIO(dataSet);
		return result;
	}
}
