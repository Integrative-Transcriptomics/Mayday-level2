package mayday.Reveal.utilities;

import java.io.IOException;

import javax.swing.JOptionPane;

public class BrowserToolkit {

	public static void openURL(String s) {
        String s1 = System.getProperty("os.name").toLowerCase();
        Runtime runtime = Runtime.getRuntime();
        try {
            if (s1.indexOf("windows") >= 0)
                runtime.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", s });
            else if (s1.indexOf("mac") >= 0) {
                Runtime.getRuntime().exec(new String[] { "open", s });
            } else {
                // linux
                String as[] = { "firefox", "mozilla-firefox", "mozilla", "konqueror", "netscape", "opera" };
                boolean isSuccess = false;
                int i = 0;
                do {
                    if (i >= as.length)
                        break;
                    try {
                        runtime.exec(new String[] { as[i], s });
                        isSuccess = true;
                        break;
                    } catch (Exception exception) {
                        i++;
                    }
                } while (true);
                if (!isSuccess) {
                	JOptionPane.showMessageDialog(null, "Please open a browser and go to " + s, "Default browser not recognized", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (IOException ioexception) {
        	JOptionPane.showMessageDialog(null, "Failed to start a browser to open the URL " + s, "URL Error", JOptionPane.ERROR_MESSAGE);
            ioexception.printStackTrace();
        }
    }
}
