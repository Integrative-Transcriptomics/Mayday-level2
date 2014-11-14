package mayday.motifsearch.preparation;

/**
 * This class contains the header information
 * 
 * @author Frederik Weber
 */

public class Header {

    private String content;
    private String[] splitedContent; // the splitted content of a header
    private String splitRegex;

    /**
     * Constructs the header and splits the given content and stores it
     * 
     * @param content
     *            the complete content of a header
     */
    public Header(String content) {
	this.splitRegex = "\\;";
	this.content = content;
	this.splitedContent = content.split(splitRegex);
    }

    public final String getSequenceName() {
	return this.splitedContent[0];
    }

    public String getContent() {
	return content;
    }

    @Override
    public final String toString() {
	return this.getSequenceName();
    }

    public final void changeSplitRegex(String splitRegex) {
	this.splitRegex = splitRegex;
	this.splitedContent = this.content.split(splitRegex);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((content == null) ? 0 : content.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Header other = (Header) obj;
	if (content == null) {
	    if (other.content != null)
		return false;
	}
	else if (!content.equals(other.content))
	    return false;
	return true;
    }
}
