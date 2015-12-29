/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mayday.expressionmapping.io.reader;

import java.util.Iterator;

import mayday.core.Probe;
import mayday.core.ProbeList;

/**
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class ProbeListAnnotationSource implements AnnotationSource {

//	private ProbeList probes;

	private Iterator<Probe> probeIterator;

//	private int nameMode;

	public static int DISPLAYNAME = 0;

	public static int NAME = 1;

	public ProbeListAnnotationSource(ProbeList probes) {

//		this.probes = probes;

		this.probeIterator = probes.getAllProbes().iterator();

//		this.nameMode = ProbeListAnnotationSource.DISPLAYNAME;
		
	}


	public ProbeListAnnotationSource(ProbeList probes, int nameMode) {

//		this.probes = probes;

		this.probeIterator = probes.getAllProbes().iterator();

		if(nameMode < 0 || nameMode > 1) {

			System.err.println("Wrong name mode, taking the default: Display Name!");

//			this.nameMode = ProbeListAnnotationSource.DISPLAYNAME;
		}
		else {
//			this.nameMode = nameMode;
		}

	}


	@Override
	public boolean hasNext() {

		return this.probeIterator.hasNext();

	}

	@Override
	public String next() {

		return this.probeIterator.next().getDisplayName();

	}

}
