package mayday.wapiti.experiments.generic.reads.bamsam;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

import java.util.HashMap;

/**
 * Created by adrian on 4/6/16.
 */
public class PicardWrapper extends AbstractPlugin {
    public void init() {
    }

    public PluginInfo register() throws PluginManagerException {
        return new PluginInfo(this.getClass(), "LIB.PICCARD", new String[0], "Libraries",
                (HashMap)null, "Broad Institute",
                "http://broadinstitute.github.io/picard/",
                "A set of command line tools (in Java) for manipulating high-throughput " +
                        "sequencing (HTS) data and formats such as SAM/BAM/CRAM and VCF.",
                "PICARD API");
    }
}
