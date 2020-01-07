package NetworkToolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class StatisticHandler {

	private HashMap<String,HashMap<Integer,Long>> m_SpeedTests;		// HashMap<HostName,HashMap<passedTime,bytesloaded>>
	private HashMap<String,ArrayList<Float>> m_PingTests; 			// HashMap<HostName,ArrayList<milliseconds>>
	private HashMap<String,ArrayList<String>> m_TraceRoutes;        // HashMap<Host,Traceroute>
	private StatisticSaver m_StatisticSaver;
	
	public StatisticHandler() {
		m_StatisticSaver = new StatisticSaver();
		reset();
	}
	
	public void reset() {
		m_SpeedTests = new HashMap<String,HashMap<Integer,Long>>();
		m_PingTests  = new HashMap<String,ArrayList<Float>>();
		m_TraceRoutes = new HashMap<String,ArrayList<String>>();
	}
	
	public void startTest() {
		m_StatisticSaver.onTestStart();
		reset();
	}
	
	public void finishTest() {
		JSONObject SpeedTests = getSpeedTestStatisticAsJSON();
		JSONObject PingTests = getPingTestStatisticAsJSON();
		JSONObject TraceTests = getTraceTestStatisticAsJSON();
		
		m_StatisticSaver.saveStatisticFile("AllSpeedTests.json", SpeedTests);
		m_StatisticSaver.saveStatisticFile("AllPingTests.json", PingTests);
		m_StatisticSaver.saveStatisticFile("AllTraceTests.json", TraceTests);
		
		m_StatisticSaver.savePerHostSpeedTests(SpeedTests);
		m_StatisticSaver.savePerHostPingTests(PingTests);
		m_StatisticSaver.savePerHostTraceTests(TraceTests);
		
		m_StatisticSaver.saveStatisticFile("AllTests.json", getAllTestStatisticsAsJSON());
		m_StatisticSaver.saveStatisticFile("Forum_BBCode_AllUsefulData.txt", getAllTestStatisticsAsForumVersion());
		
	}
	
	public HashMap<String,HashMap<Integer,Long>> getSpeedTestStatistic() {
		return m_SpeedTests;
	}
	
	public JSONObject getSpeedTestStatisticAsJSON() {
		JSONObject ret = new JSONObject();
		
		Iterator<String> val = m_SpeedTests.keySet().iterator();

		while(val.hasNext()) {
			String host = val.next();
			float maxMBitps = 0f;
			float maxMBps = 0f;
			
			float avgMBps = 0f;
			float avgMBitps = 0f;
				
			JSONObject hostdata = new JSONObject();
			
			Iterator<Integer> dd = m_SpeedTests.get(host).keySet().iterator();
			
			int speedindexes = 0;
			
			while(dd.hasNext()){
				int key = dd.next();
				float mbit = 0f;
				float mbps = 0f;
				Double a = m_SpeedTests.get(host).get(key).doubleValue();
				
				float keyfactor = key / 1000.f;
				
				if(keyfactor > 0)
				{
					mbit =  (((a.floatValue()*8f / keyfactor) / 1024f) / 1024f) ;
					mbps =  (((a.floatValue()    / keyfactor) / 1024f) / 1024f) ; 
				}
				
				if(maxMBitps < mbit) {
					maxMBitps = mbit;
				}
				
				if(maxMBps < mbps) {
					maxMBps = mbps;
				}
				
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
		
		while(iterator.hasNext()) {
			String host = iterator.next();
			JSONObject obj = new JSONObject();
			
			int elements = 1;
			float minping = 10000.f;
			float maxping = 0.f;
			float avgping = 0;
			for(float f : m_PingTests.get(host)) {
				obj.put(""+elements,f);
				elements++;
				
				avgping += f;
				
				if(minping > f) {
					minping = f;
				}
				
				if(maxping < f) {
					maxping = f;
				}
			}
			
			obj.put("minpingms", minping);
			obj.put("maxpingms", maxping);
			obj.put("avgpingms", avgping/elements);
			obj.put("jitterms", maxping-minping);
			ret.put(host, obj);
		}
		
		return ret;
		
	}
	
	public JSONObject getTraceTestStatisticAsJSON() {
		JSONObject tracejson = new JSONObject();
		Iterator<String> itrtrace = m_TraceRoutes.keySet().iterator();
		
		while(itrtrace.hasNext()) {
			String key = itrtrace.next();
			ArrayList<String> trace = m_TraceRoutes.get(key);
			
			tracejson.put(key, trace);
		}
		
		return tracejson;
	}
	
	public JSONObject getAllTestStatisticsAsJSON() {
		JSONObject ret = new JSONObject();
		
		Iterator<String> itrping = m_PingTests.keySet().iterator();
		
		while(itrping.hasNext()) {
			String host = itrping.next();
			float minPing = 10000.f;
			float maxPing = 0.f;
			float avgPing = 0.f;
			
			if( !ret.has(host)) {
				JSONObject tmpar = new JSONObject();
				tmpar.put("rawspeeddata",new JSONObject());
				tmpar.put("avgspeedMBITps",new JSONObject());
				tmpar.put("avgspeedMBps",new JSONObject());
				tmpar.put("ping", new JSONObject());
				
				ret.put(host, tmpar);
			}
			
			int pingscaptures = m_PingTests.get(host).size();
			for(int i = 0; i < pingscaptures; i++ ) {
				float f = m_PingTests.get(host).get(i);
				
				if(f > maxPing) {
					maxPing = f;
				}
				
				if(f < minPing) {
					minPing = f;
				}
				
				avgPing += f;
				
				ret.getJSONObject(host).getJSONObject("ping").put(""+i,f);
			}
			
			ret.getJSONObject(host).put("maxpingms",maxPing);
			ret.getJSONObject(host).put("minpingms",minPing);
			ret.getJSONObject(host).put("avgpingms",avgPing/pingscaptures );
			ret.getJSONObject(host).put("jitterms", maxPing-minPing);
		}
		
		Iterator<String> itrspeed = m_SpeedTests.keySet().iterator();
		
		while(itrspeed.hasNext()){
			String host = itrspeed.next();
			float maxMBitps = 0f;
			float maxMBps = 0f;
			
			float avgMBps = 0f;
			float avgMBitps = 0f;
			
			if( !ret.has(host))
			{
				JSONObject tmpar = new JSONObject();
				tmpar.put("rawspeeddata",new JSONObject());
				tmpar.put("avgspeedMBITps",new JSONObject());
				tmpar.put("avgspeedMBps",new JSONObject());
				tmpar.put("ping", new JSONObject());
				
				ret.put(host, tmpar);
			}
			
			HashMap<Integer,Long> map = m_SpeedTests.get(host);
			
			Iterator<Integer> mapitr = map.keySet().iterator();
			
			float mbit = 0f;
			float mbps = 0f;
			int speedindexes = 0;
			while(mapitr.hasNext()) {
				int key = mapitr.next();
				Long a = map.get(key);
				
				float keyfactor = key / 1000.f;
				
				if(keyfactor > 0) {
					mbit =  (((a.floatValue()*8f / keyfactor) / 1024f) / 1024f) ;
					mbps =  (((a.floatValue()    / keyfactor) / 1024f) / 1024f) ; 
				}
				
				if(maxMBitps < mbit) {
					maxMBitps = mbit;
				}
				
				if(maxMBps < mbps) {
					maxMBps = mbps;
				}
				
				avgMBps += mbps;
				avgMBitps += mbit;
				speedindexes++;
				
				ret.getJSONObject(host).getJSONObject("rawspeeddata").put(""+key,a);
				ret.getJSONObject(host).getJSONObject("avgspeedMBITps").put(""+key, mbit  );
				ret.getJSONObject(host).getJSONObject("avgspeedMBps").put(""+key, mbps  );
			}
			
			ret.getJSONObject(host).put("maxMBps", maxMBps);
			ret.getJSONObject(host).put("maxMBitps", maxMBitps);
			ret.getJSONObject(host).put("averageMBps", avgMBps/speedindexes);
			ret.getJSONObject(host).put("averageMBitps", avgMBitps/speedindexes);
		}
		
		Iterator<String> itrtrace = m_TraceRoutes.keySet().iterator();
		
		while(itrtrace.hasNext()) {
			String host = itrtrace.next();
			ArrayList<String> trace = m_TraceRoutes.get(host);
			if( !ret.has(host)) {
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
	
	public void addHostPingTimes(String hostname, ArrayList<Float> values) {
		m_PingTests.put(hostname, values);
	}
	
	public void addHostTraceroute(String host, ArrayList<String> trace) {
		m_TraceRoutes.put(host, trace);
	}
	
	public void addHostSpeed(String hostname, int time, long m_BytesLoaded) {
		// Check if hostname exists
		Iterator<String> currenthosts = m_SpeedTests.keySet().iterator();
		
		boolean found = false;
		
		while(currenthosts.hasNext()) {
			String host = currenthosts.next();
			
			if(hostname.equals(host)) {
				HashMap<Integer,Long> hostspeeds = m_SpeedTests.get(host);
				
				hostspeeds.put(time, m_BytesLoaded);
				found = true;
			}
		}
		
		if(!found) {
			HashMap<Integer,Long> hostpings = new HashMap<Integer,Long>();
			
			hostpings.put(time, m_BytesLoaded);
			m_SpeedTests.put(hostname, hostpings);
		}
	}
	

	private String getAllTestStatisticsAsForumVersion() {
		StringBuilder BBCode = new StringBuilder();
		BBCode.append(" Testergebnis für: " + m_StatisticSaver.getStatisticFolderName());
		BBCode.append("\n");
		
		JSONObject all = getAllTestStatisticsAsJSON();
		
		Iterator<String> hosts = all.keySet().iterator();
		
		while(hosts.hasNext()){
			String host = hosts.next();
			JSONObject hostdata = all.getJSONObject(host);
			
			if(hostdata.has("avgspeedMBps")) {
				int SpeedtestsCount = hostdata.getJSONObject("avgspeedMBps").length();
				BBCode.append(host);
				BBCode.append("[TABLE]\n");
				// Start Speedtest Data
				if(SpeedtestsCount > 0) { // Has SpeedTest Data
					float maxMBps = hostdata.getFloat("maxMBps");
					float maxMBitps = hostdata.getFloat("maxMBitps");
					
					float avgMBps = hostdata.getFloat("averageMBps");
					float avgMBitps = hostdata.getFloat("averageMBitps");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Max. MBit/s\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(maxMBitps);
					BBCode.append("\n[/TD]\n");
					BBCode.append("[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Max. MB/s\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(maxMBps);
					BBCode.append("\n[/TD]\n");
					BBCode.append("[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Avg. MBit/s\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(avgMBitps);
					BBCode.append("[/TD]\n");
					BBCode.append("\n[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Avg. MB/s\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(avgMBps);
					BBCode.append("\n[/TD]\n");
					BBCode.append("[/TR]\n");
				}
			}
			// End Speedtest Data
			
			// Start Ping Test
			
			if(hostdata.has("ping"))
			{
				JSONObject pingdata = hostdata.getJSONObject("ping");
				
				if(pingdata.length() > 0 ) // We have Ping Data
				{
					float maxping = hostdata.getFloat("maxpingms");
					float minping = hostdata.getFloat("minpingms");
					float avgping = hostdata.getFloat("avgpingms");
					float jitter = hostdata.getFloat("jitterms");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Min:\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(minping);
					BBCode.append("ms\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Max:\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(maxping);
					BBCode.append("ms\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Avg.\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(avgping);
					BBCode.append("ms\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[/TR]\n");
					
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Jitter:\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(jitter);
					BBCode.append("ms\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[/TR]\n");
				}
			}
			// End Ping Test
			
			// Start TraceTest Data
			
			if(hostdata.has("trace"))
			{
				JSONArray tracedata = hostdata.getJSONArray("trace");
				if(tracedata.length() > 0 )
				{
					BBCode.append("[TR]\n");
					BBCode.append("[TD]\n");
					BBCode.append("Traceroute\n");
					BBCode.append("[/TD]\n");
					BBCode.append("[TD]\n");
					BBCode.append(m_StatisticSaver.generateBBCodeForumTraceTest(host, tracedata));
					BBCode.append("\n[/TD]\n");
					BBCode.append("[/TR]\n");
				}
			}
			// End TraceTest Data
			
			BBCode.append("[/TABLE]\n");
			
		}
		
		return BBCode.toString();
	}
}
