package NetworkToolkit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TraceRoute
{
	private final String m_OperatingSystem;
	private String m_Host;
	private String m_LatestTraceRouteLine;
	
	public TraceRoute()
	{
		m_OperatingSystem =  System.getProperty("os.name").toLowerCase();
		m_LatestTraceRouteLine = "";
	}
	
	public void setHost(String url)
	{
		m_Host = "";
		try {
			m_Host = new URL(url).getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private String convertStreamToString(InputStream stream)
	{
		
		try( BufferedReader br =
		           new BufferedReader( new InputStreamReader(stream, StandardCharsets.UTF_8.name() )))
		   {
		      StringBuilder sb = new StringBuilder();
		      String line;
		      while(( line = br.readLine()) != null ) {
		    	 sb.append( line );
		         sb.append( '\n' );
		         if(!line.equals("")) // If the line is empty, just continue
		         {
		        	 System.out.println(">"+line+"<");
		        	 m_LatestTraceRouteLine = line;
		         }
		      }
		      return sb.toString();
		   
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	public String getRoute()
	{
		System.out.println("Starting trace to: " + m_Host);
		String route = "";
	    try {
	        Process traceRt;
	        if(m_OperatingSystem.contains("win"))
	        {
	        	traceRt = Runtime.getRuntime().exec("tracert -w 30 " + m_Host);
	        }else
        	{
	        	traceRt = Runtime.getRuntime().exec("traceroute " + m_Host);
        	}

	        // read the output from the command
	        route = convertStreamToString(traceRt.getInputStream());
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    System.out.println("Finished trace to: " + m_Host);
	    return route;
	}

	public String getLatestLine() {
		return m_LatestTraceRouteLine;
	}
}