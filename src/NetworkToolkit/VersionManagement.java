package NetworkToolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class VersionManagement {

	private final static String NETWORK_TOOLKIT_VERSION = "0.0.0";
	
	
	public VersionManagement()
	{
		
	}
	
	public boolean isLatestVersion()
	{
		String latestVersion = getLatestVersionString();
		
		return false;
	}

	private String getLatestVersionString() {
		
        try
        {
        	URL url = new URL("http://webcode.me");
        
        	BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream());
        
            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                sb.append(System.lineSeparator());
            }

            System.out.println(sb);
        }catch(Exception e)
        {
        	return NETWORK_TOOLKIT_VERSION;
        }
	}
	
}
