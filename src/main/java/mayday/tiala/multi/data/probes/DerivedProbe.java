package mayday.tiala.multi.data.probes;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.tiala.multi.data.mastertables.DerivedMasterTable;

/**
 * @author jaeger
 *
 */
public abstract class DerivedProbe extends Probe {

	protected String sourceName;
	
	/**
	 * @param masterTable
	 * @param sourceName
	 */
	public DerivedProbe( DerivedMasterTable masterTable, String sourceName ) {
		super(masterTable);
		this.sourceName = sourceName;
	}
	
	protected DerivedMasterTable getDerivedMasterTable() {
		return (DerivedMasterTable)super.getMasterTable();
	}
	
	protected MasterTable getParentMasterTable(int which) {
		return getDerivedMasterTable().getMasterTable(which);
	}
	
	protected abstract double[] values(); // this should be very fast, ideally only a field access

	public Double getValue(int experiment)
	throws RuntimeException
	{
		if(experiment >= getNumberOfExperiments()){
			throw(new RuntimeException("Invalid experiment number (" + experiment + ")." ));
		}

		double ret = values()[experiment];
		if (Double.isNaN(ret))
			return null;
		return ret;
	}

	public void setValue( Double value, int experiment ) {
		throw new RuntimeException("Unable to change values of a derived probe");
	}

	public int getNumberOfExperiments()	{
		return values() != null ? values().length : 0;
	}

	public String toDebugString() {
		return "Derived Probe: "+super.toDebugString();	
	}  


	public String toString() {
		return "Derived Probe: "+super.toString();	
	}  

	public void addExperiment( Double experiment ) {
		throw new RuntimeException("Unable to change values of a derived probe");
	}

	public Object clone() {
		throw new RuntimeException("Unable to clone a derived probe");
	}

	public double[] getValues() {
		return values();
	}

	public void setValues(double[] values) {
		throw new RuntimeException("Unable to change values of a derived probe");
	}
	
	/**
	 * @return name of source probe
	 */
	public String getSourceName() {
		return sourceName;
	}
}
