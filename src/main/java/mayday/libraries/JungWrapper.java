package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class JungWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.JUNG", (String[])null, "Libraries", (HashMap)null, "Joshua O\'Madadhain, Danyel Fisher, and Scott White", "http://jung.sourceforge.net/", "Java Universal Network and Graph Framework", "JUNG");
   }
}
