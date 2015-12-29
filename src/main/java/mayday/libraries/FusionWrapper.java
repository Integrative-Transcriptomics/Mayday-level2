package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class FusionWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.Fusion", new String[0], "Libraries", (HashMap)null, "Affymetrix Inc.", "http://www.affymetrix.com/", "The Affymetrix fusion SDK", "Fusion SDK");
   }
}
