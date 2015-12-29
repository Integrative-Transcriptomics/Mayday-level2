package mayday.wapiti.experiments.properties.valuetype;

public abstract class Expression extends AbstractValueType {

	@Override
	public String toString() {
		return getKind()+" expression";
	}
	
	public abstract String getKind();

}
