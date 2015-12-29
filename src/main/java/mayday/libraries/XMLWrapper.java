package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class XMLWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.XML", new String[0], "Libraries", (HashMap)null, "(various)", "-", "Nux, Saxon and Xom bundled for XML and XQuery/XPath processing.", "XML processing");
   }
}
