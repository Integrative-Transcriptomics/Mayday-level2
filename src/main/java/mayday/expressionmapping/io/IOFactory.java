
package mayday.expressionmapping.io;



import java.util.List;
import mayday.core.ProbeList;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.io.reader.AnnotationReader;
import mayday.expressionmapping.io.reader.AnnotationReaderInterface;
import mayday.expressionmapping.io.reader.AnnotationSource;
import mayday.expressionmapping.io.reader.DataSource;
import mayday.expressionmapping.io.reader.ExpressionReader;
import mayday.expressionmapping.io.reader.ExpressionReaderInterface;
import mayday.expressionmapping.io.reader.ProbeListAnnotationSource;
import mayday.expressionmapping.io.reader.ProbeListDataSource;

/**
 * The class IOFactory follows the factory design pattern providing creational methods for ReaderClasses.
 * These classes are used for reading expression values and annotations from streams, especially files.
 * 
 * @author Stephan Gade
 *
 */
public class IOFactory {


	private ProbeList probes;

	public IOFactory(ProbeList probes) {

		this.probes = probes;

	}


	/**
	 * Creates a DataSource for the ExpressionReader.
	 *
	 * @return a DataSource object which reads from a ProbeList
	 */
	public DataSource getDataSource() {
		return new ProbeListDataSource(this.probes);
	}

	/**
	 * Creates an AnnotationSource for the AnnotationReader
	 *
	 * @return an AnnotationSource object which reads from a ProbeList
	 */
	public AnnotationSource getAnnotationSource() {

		return new ProbeListAnnotationSource(this.probes);

	}

	/**
	 * Creates an ExpressionReader
	 * @param source the DataSource used by the ExpressionReader
	 * @param groupMappings the groupMappings used by the ExpressionReader to build groups
	 * @param groupMode the mode for grouuping, either mean or median. @see Constants
	 * @return an ExpressionReaderInterface
	 */
	public ExpressionReaderInterface getExpressionReader(DataSource source, List<TIntArrayList> groupMappings, int groupMode) {
	
		return new ExpressionReader(source, groupMappings, groupMode);
		
	}

	/**
	 * Creates an AnnotationReader
	 * @param annoSource the AnnotationSource used by the AnnotationReader
	 * @return an AnnotationReaderInterface
	 */
	public AnnotationReaderInterface getAnnotationReader(AnnotationSource annoSource) {

		return new AnnotationReader(annoSource);

	}




}
