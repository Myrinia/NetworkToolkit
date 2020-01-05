package NetworkToolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONObject;

public class StatisticSaver {

	
	private String m_StatisticFolderName;
	
	public StatisticSaver()
	{
		
	}
	
	public void onTestStart()
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		setFolderName(timeStamp);
	}
	
	public void setFolderName(String name)
	{
		m_StatisticFolderName = "data/statistics/"+name+"/";
		CreateFolderIfNotExist(m_StatisticFolderName);
	}
	
	private void CreateFolderIfNotExist(String foldername)
	{
		new File(foldername).mkdirs();
	}
	
	private boolean writeFile(String filename,String content)
	{
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter( m_StatisticFolderName + filename ));
		    writer.write( content);

		}
		catch ( Exception e)
		{
			return false;
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        {
		        	writer.close( );
		        }
		    }
		    catch ( Exception e)
		    {
		    	return false;
		    }
		}
		
		return true;
	}

	public void saveStatisticFile(String filename, JSONObject json) {
		saveStatisticFile(filename,json.toString());
	}
	
	public void saveStatisticFile(String filename, String content)
	{
		if(writeFile(filename,content))
		{
			System.out.println("Statistic File: " + filename + " saved!");
		}else
		{
			System.out.println("Error saving Statistic " + filename + " !");
		}
	}

	public void savePerHostSpeedTests(JSONObject speedTests)
	{
		
		Iterator<String> SpeedItr = speedTests.keys();
		
		while(SpeedItr.hasNext())
		{
			
			String key = SpeedItr.next();
			String folder = m_StatisticFolderName+key;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(key+"/speedtest.json", speedTests.getJSONObject(key));
		}
		
	}

	public void savePerHostPingTests(JSONObject pingTests)
	{
		Iterator<String> PingItr = pingTests.keys();
		
		while(PingItr.hasNext())
		{
			String key = PingItr.next();
			String folder = m_StatisticFolderName+key;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(key+"/pingtest.json", pingTests.getJSONObject(key));
		}
	}

	public void savePerHostTraceTests(JSONObject traceTests) {
		
		Iterator<String> TraceItr = traceTests.keys();
		
		while(TraceItr.hasNext())
		{
			String key = TraceItr.next();
			String folder = m_StatisticFolderName+key;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(key+"/tracetest.json", traceTests.getJSONObject(key));
		}
	}
	
}
