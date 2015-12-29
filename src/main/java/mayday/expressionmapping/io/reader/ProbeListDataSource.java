package mayday.expressionmapping.io.reader;

import java.util.Iterator;
import mayday.core.Probe;
import mayday.core.ProbeList;

/**
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class ProbeListDataSource implements DataSource {

	private ProbeList probes;

	private Iterator<Probe> probeIterator;

//	private int index;

//	private int numProbes;

	private int dim;

	public ProbeListDataSource(ProbeList probes) {

		this.probes = probes;

		this.probeIterator = probes.getAllProbes().iterator();

//		this.numProbes = probes.getNumberOfProbes();

//		this.index = 0;

	/*
	 * get the number of experiments of the first probe
	 * in the probe list
	 * therewith we get the number of values of one probe
	 */
		this.dim = this.probes.getAllProbes().iterator().next().getNumberOfExperiments();

	}


	@Override
	/**
	 * Get the dimension of the data set, that is the number of experiments
	 * and therwith the double values of one probe
	 */
	public int getDim() {

		return this.dim;

	}

	@Override
	/**
	 * Check if there is a next element in the set.
	 */
	public boolean hasNext() {

		// return this.index < (this.numProbes - 1);
		return this.probeIterator.hasNext();
	}

	@Override
	/**
	 * Get the next double array of a probe
	 */
	public double[] next() {

		return this.probeIterator.next().getValues();
	}

}
