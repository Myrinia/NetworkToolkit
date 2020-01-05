package NetworkToolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class StatisticHandler {

	private HashMap<String,HashMap<Integer,Double>> m_SpeedTests;	// HashMap<HostName,HashMap<passedTime,bytesloaded>>
	private HashMap<String,ArrayList<Long>> m_PingTests; 			// HashMap<HostName,ArrayList<milliseconds>>
	private HashMap<String,String> m_TraceRoutes;           	// HashMap<Host,Traceroute>
	private StatisticSaver m_StatisticSaver;
	
	public StatisticHandler()
	{
		m_StatisticSaver = new StatisticSaver();
		reset();
	}
	
	public void reset()
	{
		m_SpeedTests = new HashMap<String,HashMap<Integer,Double>>();
		m_PingTests  = new HashMap<String,ArrayList<Long>>();
		m_TraceRoutes = new HashMap<String,String>();
	}
	
	public void startTest()
	{
		m_StatisticSaver.onTestStart();
		reset();
	}
	
	public void finishTest()
	{
		m_StatisticSaver.saveStatisticFile("AllSpeedTests.json", getSpeedTestStatisticAsJSON());
		m_StatisticSaver.saveStatisticFile("AllPingTests.json", getPingTestStatisticAsJSON());
		m_StatisticSaver.saveStatisticFile("AllTraceTests.json", getTraceTestStatisticAsJSON());
		
		m_StatisticSaver.saveStatisticFile("AllTests.json", getAllTestStatisticsAsJSON());
		
		m_StatisticSaver.onTestFinish();
	}
	
	public HashMap<String,HashMap<Integer,Double>> getSpeedTestStatistic()
	{
		return m_SpeedTests;
	}
	
	public JSONObject getSpeedTestStatisticAsJSON()
	{
		JSONObject ret = new JSONObject();
		
		Iterator<String> val = m_SpeedTests.keySet().iterator();

		while(val.hasNext()){
			String host = val.next();
			float maxMBitps = 0.f;
			float maxMBps = 0.f;
			
			float avgMBps = 0.f;
			float avgMBitps = 0.f;
				
			JSONObject hostdata = new JSONObject();
			
			Iterator<Integer> dd = m_SpeedTests.get(host).keySet().iterator();
			
			int speedindexes = 0;
			while(dd.hasNext()){
				int key = dd.next();
				float mbit;
				float mbps;
				Double a = m_SpeedTests.get(host).get(key);
				
				if(a.isInfinite())
				{
					mbit = 0;
					mbps = 0;
				}else
				{
					mbit = (float) ( (a/1024/1024)* 8 / ((float)key/1000));
					mbps = (float) ( (a/1000/1000) / ((float)key/1000));
				}

				if(maxMBitps < mbit)
					maxMBitps = mbit;
				
				if(maxMBps < mbps)
					maxMBps = mbps;
				
				avgMBps += mbps;
				avgMBitps += mbit;
				speedindexes++;
				hostdata.put(""+key, a);
			}
			
			hostdata.put("maxMBps",maxMBps);
			hostdata.put("maxMBitps",maxMBitps);
			hostdata.put("averageMBps",avgMBps/speedindexes);
			hostdata.put("averageMBitps",avgMBitps/speedindexes);
			
			ret.put(host, hostdata);
			
			
		}
		return ret;
	}
	
	public JSONObject getPingTestStatisticAsJSON()
	{
		JSONObject ret = new JSONObject();
		Iterator<String> iterator = m_PingTests.keySet().iterator();
		
		while(iterator.hasNext()){
			String host = iterator.next();
			JSONObject obj = new JSONObject();
			
			int elements = 1;
			float minping = 10000.f;
			float maxping = 0.f;
			float avgping = 0;
			for(long l : m_PingTests.get(host))
			{
				obj.put(""+elements,l);
				elements++;
				
				avgping += l;
				
				if(minping > l)
					minping = l;
				
				if(maxping < l)
					maxping = l;
			}
			
			obj.put("minpingms", minping);
			obj.put("maxpingms", maxping);
			obj.put("avgpingms", avgping/elements);
			obj.put("jitterms", maxping-minping);
			ret.put(host, obj);
		
		}
		
		return ret;
		
	}
	
	public JSONObject getTraceTestStatisticAsJSON()
	{
		JSONObject tracejson = new JSONObject();
		Iterator<String> itrtrace = m_TraceRoutes.keySet().iterator();
		
		while(itrtrace.hasNext())
		{
			String key = itrtrace.next();
			String trace = m_TraceRoutes.get(key);
			
			tracejson.put(key, trace);
		}
		
		
		return tracejson;
	}
	
	public JSONObject getAllTestStatisticsAsJSON()
	{
		JSONObject ret = new JSONObject();
		
		Iterator<String> itrping = m_PingTests.keySet().iterator();
		
		while(itrping.hasNext()){
			String host = itrping.next();
			float minPing = 10000.f;
			float maxPing = 0.f;
			float avgPing = 0;
			
			if( !ret.has(host))
			{
				JSONObject tmpar = new JSONObject();
				tmpar.put("rawspeeddata",new JSONObject());
				tmpar.put("avgspeedMBITps",new JSONObject());
				tmpar.put("avgspeedMBps",new JSONObject());
				tmpar.put("ping", new JSONObject());
				
				ret.put(host, tmpar);
			}
			
			int pingscaptures = m_PingTests.get(host).size();
			for(int i = 0; i < pingscaptures; i++ )
			{
				long l = m_PingTests.get(host).get(i);
				
				if(l > maxPing)
					maxPing = l;
				
				if(l < minPing)
					minPing = l;
				
				avgPing += l;
				
				ret.getJSONObject(host).getJSONObject("ping").put(""+i,l);
			}
			
			ret.getJSONObject(host).put("maxpingms",maxPing);
			ret.getJSONObject(host).put("minpingms",minPing);
			ret.getJSONObject(host).put("avgpingms",avgPing/pingscaptures );
			ret.getJSONObject(host).put("jitterms", maxPing-minPing);
		}
		
		Iterator<String> itrspeed = m_SpeedTests.keySet().iterator();
		
		while(itrspeed.hasNext()){
			String host = itrspeed.next();
			float maxMBitps = 0.f;
			float maxMBps = 0.f;
			
			float avgMBps = 0.f;
			float avgMBitps = 0.f;
			
			if( !ret.has(host))
			{
				JSONObject tmpar = new JSONObject();
				tmpar.put("rawspeeddata",new JSONObject());
				tmpar.put("avgspeedMBITps",new JSONObject());
				tmpar.put("avgspeedMBps",new JSONObject());
				tmpar.put("ping", new JSONObject());
				
				ret.put(host, tmpar);
			}
			
			HashMap<Integer,Double> map = m_SpeedTests.get(host);
			
			Iterator<Integer> mapitr = map.keySet().iterator();
			
			float mbit = 0;
			float mbps = 0;
			int speedindexes = 0;
			while(mapitr.hasNext())
			{
				int v = mapitr.next();
				Double a = map.get(v);

				if(a.isInfinite())
				{
					mbit = 0;
					mbps = 0;
				}else
				{
					mbit = (float) ( (a/1024/1024)* 8 / ((float)v/1000));
					mbps = (float) ( (a/1000/1000) / ((float)v/1000));
				}

				if(maxMBitps < mbit)
					maxMBitps = mbit;
				
				if(maxMBps < mbps)
					maxMBps = mbps;
				
				avgMBps += mbps;
				avgMBitps += mbit;
				speedindexes++;
				
				ret.getJSONObject(host).getJSONObject("rawspeeddata").put(""+v,a);
				ret.getJSONObject(host).getJSONObject("avgspeedMBITps").put(""+v, mbit  );
				ret.getJSONObject(host).getJSONObject("avgspeedMBps").put(""+v, mbps  );
			}
			
			ret.getJSONObject(host).put("maxMBps", maxMBps);
			ret.getJSONObject(host).put("maxMBitps", maxMBitps);
			ret.getJSONObject(host).put("averageMBps", avgMBps/speedindexes);
			ret.getJSONObject(host).put("averageMBitps", avgMBitps/speedindexes);
		}
		
		Iterator<String> itrtrace = m_TraceRoutes.keySet().iterator();
		
		while(itrtrace.hasNext())
		{
			String host = itrtrace.next();
			String trace = m_TraceRoutes.get(host);
			if( !ret.has(host))
			{
				JSONObject tmpar = new JSONObject();
				tmpar.put("rawspeeddata",new JSONObject());
				tmpar.put("avgspeedMBITps",new JSONObject());
				tmpar.put("avgspeedMBps",new JSONObject());
				tmpar.put("ping", new JSONObject());
				
				ret.put(host, tmpar);
			}
			
			ret.getJSONObject(host).put("trace",trace);
			
		}
		
		return ret;
	}
	
	public void addHostPingTimes(String hostname, ArrayList<Long> values)
	{
		m_PingTests.put(hostname, values);
	}
	
	public void addHostTraceroute(String host, String trace)
	{
		m_TraceRoutes.put(host, trace);
	}
	
	public void addHostSpeed(String hostname, int time, double m_BytesLoaded)
	{
		// Check if hostname exists
		Iterator<String> currenthosts = m_SpeedTests.keySet().iterator();
		
		boolean found = false;
		
		while(currenthosts.hasNext()){
			String host = currenthosts.next();
			
			if(hostname.equals(host))
			{
				HashMap<Integer,Double> hostpings = m_SpeedTests.get(host);
				
				hostpings.put(time, m_BytesLoaded);
				found = true;
			}
		}
		
		if(!found)
		{
			HashMap<Integer,Double> hostpings = new HashMap<Integer,Double>();
			
			hostpings.put(time, m_BytesLoaded);
			m_SpeedTests.put(hostname, hostpings);
		}
	}
	
	
}
