package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class CommonsCliWrapper extends AbstractPlugin {
   public Object execute(Object var1) {
      return null;
   }

   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.Commons.cli", new String[0], "Libraries", (HashMap)null, "Apache Foundation", "http://www.apache.org", "Various functions for command line parsing.", "Jakarta Commons CLI");
   }
}
