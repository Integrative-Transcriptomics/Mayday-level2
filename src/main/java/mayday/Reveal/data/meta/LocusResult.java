package mayday.Reveal.data.meta;

import java.util.HashMap;

/**
 * @author jaeger
 *
 * @param <V>
 * @param <E>
 */
@SuppressWarnings("serial")
public abstract class LocusResult<V,E> extends HashMap<V, E> {

	public abstract String serialize();
}
