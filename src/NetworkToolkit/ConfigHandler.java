package NetworkToolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigHandler {
	
	private HashMap<String, String> m_ConfigVariables;
	private String m_ConfigFileName;
	private ArrayList<HostConfigDataSet> m_ConfigHosts;
	
	public static int HOST_STATE_SPEED = 1;
	public static int HOST_STATE_PING = 2;
	public static int HOST_STATE_TRACE = 3;
	
	
	public ConfigHandler()
	{
		CheckConfigDirExist();
		m_ConfigVariables = new HashMap<String, String>();
		m_ConfigHosts = new ArrayList<HostConfigDataSet>();
		m_ConfigFileName = "data/config.json";
		loadConfig();
	}
	
	public ArrayList<HostConfigDataSet> getHostList()
	{
		return m_ConfigHosts;
	}
	
	public void saveConfig()
	{	
		try {
			JSONObject config = new JSONObject();
			for (HashMap.Entry<String, String> entry : m_ConfigVariables.entrySet()) {
				config.put(entry.getKey(),entry.getValue());
			}
			
			JSONObject hosts = new JSONObject();
			JSONArray dataset = new JSONArray();
			
			for(HostConfigDataSet entry : m_ConfigHosts)
			{
				dataset = new JSONArray();
				dataset.put(entry.getDownloadFile());
				dataset.put(entry.getDoSpeedTest());
				dataset.put(entry.getDoPingTest());
				dataset.put(entry.getDoTraceTest());
				
				hosts.put(entry.getHostName(), dataset);
			}
			
			config.put("hosts", hosts);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(m_ConfigFileName));
			writer.write(config.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setISPData(String name, String Download, String Upload, String ConnectionType)
	{
		setConfigVariable("ISPName",name);
		setConfigVariable("ISPDown",Download);
		setConfigVariable("ISPUp",Upload);
		setConfigVariable("ISPConnectionType",ConnectionType);
		
	}
	
	private void loadConfig()
	{
		try {
			String s = new String(Files.readAllBytes(Paths.get(m_ConfigFileName)));
			JSONObject o = new JSONObject(s.trim());
			JSONObject hosts = new JSONObject();
			JSONArray hostdata = new JSONArray();
			Iterator<String> keys = o.keys();
			m_ConfigVariables.clear();
			m_ConfigHosts.clear();
			String key;
			String HostKey;
			while(keys.hasNext()) {
			    key = keys.next();
			    if(key.equals("hosts"))
			    {
			    	hosts = o.getJSONObject("hosts");
			    	Iterator<String> HostKeys = hosts.keys();
			    	while(HostKeys.hasNext()) {
			    		HostKey = HostKeys.next();
			    		hostdata = hosts.getJSONArray(HostKey);
			    		
			    		HostConfigDataSet set = new HostConfigDataSet();
			    		
			    		set.setHostName(HostKey);
			    		set.setFileToDownload(hostdata.getString(0));
			    		
			    		try
			    		{
			    			set.setSpeedTest(hostdata.getBoolean(1));
			    			set.setPingTest(hostdata.getBoolean(2));
			    			set.setTraceTest(hostdata.getBoolean(3));
			    		}catch(Exception e)
			    		{
			    			
			    		}
			    		
			    		System.out.println("Loading Config Host: " + HostKey + " : "+ hostdata);
			    		
			    		m_ConfigHosts.add(set);
			    	}
			    }else
			    {
			    	System.out.println("Loading Config Variable: " + key + " : "+o.getString(key));
			    	m_ConfigVariables.put(key, o.getString(key));
			    }
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void CheckConfigDirExist()
	{
		File f = new File("data/");
		f.mkdir();
	}
	
	public boolean getBoolean(String string)
	{
		if(m_ConfigVariables.containsKey(string))
		{
			return Boolean.valueOf(m_ConfigVariables.get(string));
		}
		return false;
	}
	
	public String getString(String string)
	{
		if(m_ConfigVariables.containsKey(string))
		{
			return m_ConfigVariables.get(string);
		}
		return "";
	}
	
	public int getInt(String string)
	{
		if(m_ConfigVariables.containsKey(string))
		{
			return Integer.valueOf(m_ConfigVariables.get(string));
		}
		return 0;
	}
		
	public void setConfigVariable(String key, boolean value)
	{
		setConfigVariable(key, String.valueOf(value));
	}

	public void setConfigVariable(String key, String valueOf) {
		m_ConfigVariables.put(key, valueOf);
	}
	
	public void addHost(String hostkey, String DLURL)
	{
		HostConfigDataSet s = new HostConfigDataSet();
		s.setFileToDownload(DLURL);
		s.setHostName(hostkey);
		
		m_ConfigHosts.add(s);
	}
	
	public void setHostState(String hostkey, boolean state, int SpeedPingTrace) // SpeedPingTrace ==> 1=> Speed, 2=> Ping, 3=> Trace
	{
		for(HostConfigDataSet s : m_ConfigHosts)
		{
			if(s.getHostName().equals(hostkey))
			{
				switch(SpeedPingTrace)
				{
					case 1:
					{	s.setSpeedTest(state);
						break;
					}
					case 2:
					{	s.setPingTest(state);
						break;
					}
					case 3:
					{	s.setTraceTest(state);
						break;
					}
				}
			}
		}
	}

	public void removeHost(String hostName) {
		Iterator<HostConfigDataSet> it = m_ConfigHosts.iterator();
		
		ArrayList<HostConfigDataSet> newConfig = new ArrayList<HostConfigDataSet>();
		
		while(it.hasNext()){
			HostConfigDataSet data = it.next();
			if(data.getHostName().equals(hostName))
			{
				it.remove();
				System.out.println("Removing: " + data.getHostName() + " from HostList.");
			}else
			{
				newConfig.add(data);
			}
		}
	
		m_ConfigHosts = newConfig;
		saveConfig();
		loadConfig();
	}
	
}
