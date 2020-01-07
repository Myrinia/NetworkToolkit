package NetworkToolkit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TraceRoute
{
	private final String m_OperatingSystem;
	private String m_Host;
	private String m_LatestTraceRouteLine;
	
	public TraceRoute() {
		m_OperatingSystem =  System.getProperty("os.name").toLowerCase();
		m_LatestTraceRouteLine = "";
	}
	
	public void setHost(String url) {
		m_Host = "";
		try {
			m_Host = new URL(url).getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> convertStreamToString(InputStream stream) {
		ArrayList<String> streamcontents = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader(stream, StandardCharsets.UTF_8.name()	) );
			String line;
			while(( line = br.readLine()) != null ) {
				streamcontents.add(line);
		    	
				if(!line.equals("")) { // If the line is empty, just continue
		        	System.out.println(">"+line+"<");
		        	m_LatestTraceRouteLine = line;
		        }
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return streamcontents;
	}
	
	public ArrayList<String> getRoute() {
		System.out.println("Starting trace to: " + m_Host);
		ArrayList<String> route = new ArrayList<String>();
	    try {
	    	Process traceRt;
	        if(m_OperatingSystem.contains("win"))  {
	        	traceRt = Runtime.getRuntime().exec("tracert " + m_Host);
	        }else {
	        	traceRt = Runtime.getRuntime().exec("traceroute " + m_Host);
        	}

	        // read the output from the command
	        route = convertStreamToString(traceRt.getInputStream());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    System.out.println("Finished trace to: " + m_Host);
	    return route;
	}

	public String getLatestLine() {
		return m_LatestTraceRouteLine;
	}
}