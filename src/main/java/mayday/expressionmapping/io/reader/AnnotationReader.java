
package mayday.expressionmapping.io.reader;

import java.util.ArrayList;
import java.util.List;


/**
 * The reader class for the probe Annotation. It implements the AnnotatinReaderInterface.
 * It builds the list of probe annotation with help of an AnnotationSource to read the annotations from.
 *
 * @author Stephan Gade <stephan.gade@googlemail.com>
 */
public class AnnotationReader implements AnnotationReaderInterface {
    

	private AnnotationSource source;

	/**
	 * The
	 * @param source
	 */
	public AnnotationReader(AnnotationSource source) {

		this.source = source;
		
	}


	/**
	 * 
	 * @return A list with probe annotations
	 */
	@Override
	public List<String> readAnnotations() {

		List<String> annotationList = new ArrayList<String>();

		while (this.source.hasNext()) {

			annotationList.add(this.source.next());

		}

		return annotationList;

	}


}
