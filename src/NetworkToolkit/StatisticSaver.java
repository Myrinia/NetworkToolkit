package NetworkToolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class StatisticSaver {

	
	private String m_StatisticFolderName;
	
	public StatisticSaver() {
		
	}
	
	public void onTestStart() {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		setFolderName(timeStamp);
	}
	
	public void setFolderName(String name) {
		m_StatisticFolderName = "data/statistics/"+name+"/";
		CreateFolderIfNotExist(m_StatisticFolderName);
	}
	
	private void CreateFolderIfNotExist(String foldername) {
		new File(foldername).mkdirs();
	}
	
	private boolean writeFile(String filename,String content) {
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter( new FileWriter( m_StatisticFolderName + filename ));
		    writer.write( content );

		}
		catch ( Exception e) {
			return false;
		} finally {
		    try {
		        if ( writer != null) {
		        	writer.close( );
		        }
		    } catch ( Exception e) {
		    	return false;
		    }
		}
		
		return true;
	}

	public void saveStatisticFile(String filename, JSONObject json) {
		saveStatisticFile(filename,json.toString());
	}
	
	public void saveStatisticFile(String filename, String content) {
		if(writeFile(filename,content)) {
			System.out.println("Statistic File: " + filename + " saved!");
		}else {
			System.out.println("Error saving Statistic " + filename + " !");
		}
	}

	public void savePerHostSpeedTests(JSONObject speedTests) {
		
		Iterator<String> SpeedItr = speedTests.keys();
		
		while(SpeedItr.hasNext()) {
			String key = SpeedItr.next();
			String folderkey = removeToxicChars(key);
			String folder = m_StatisticFolderName+folderkey;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(folderkey+"/speedtest.json", new JSONObject().put(key, speedTests.getJSONObject(key)));
			saveStatisticFile(folderkey+"/Readable_speedtest.txt", toRawStringSpeedData(speedTests.getJSONObject(key)));
		}
	}

	public void savePerHostPingTests(JSONObject pingTests)
	{
		Iterator<String> PingItr = pingTests.keys();
		
		while(PingItr.hasNext()) {
			String key = PingItr.next();
			String folderkey = removeToxicChars(key);
			
			String folder = m_StatisticFolderName+folderkey;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(folderkey+"/pingtest.json", new JSONObject().put(key, pingTests.getJSONObject(key)));
			saveStatisticFile(folderkey+"/Readable_pingtest.txt", toRawStringPingData(pingTests.getJSONObject(key)));
			saveStatisticFile(folderkey+"/Forum_BBCode_pingtest.txt", generateBBCodeForumPingTest(key, pingTests.getJSONObject(key)));
		}
	}

	public void savePerHostTraceTests(JSONObject traceTests) {
		
		Iterator<String> TraceItr = traceTests.keys();
		
		while(TraceItr.hasNext()) {
			String key = TraceItr.next();
			String folderkey = removeToxicChars(key);
			
			String folder = m_StatisticFolderName+folderkey;
			CreateFolderIfNotExist(folder);
			saveStatisticFile(folderkey+"/tracetest.json", new JSONObject().put(key, traceTests.getJSONArray(key)));
			saveStatisticFile(folderkey+"/Readable_tracetest.txt", toRawStringTraceData(traceTests.getJSONArray(key)));	
			saveStatisticFile(folderkey+"/Forum_BBCode_tracetest.txt", generateBBCodeForumTraceTest(key, traceTests.getJSONArray(key)));	
		}
	}
	
	private String generateBBCodeForumTraceTest(String key, JSONArray traceData) {
		
		StringBuilder BBCoded = new StringBuilder();
		BBCoded.append("[spoiler=Trace: "+key+"]\n[code]\n");
		
		BBCoded.append(toRawStringTraceData(traceData));
		
		BBCoded.append("[/code]\n");
		BBCoded.append("[/spoiler]\n");
		
		return BBCoded.toString();
	}
	
	private String toRawStringTraceData(JSONArray traceData) {
		StringBuilder RawString = new StringBuilder();
		
		
		int keys = traceData.length();
		
		for(int i = 0; i < keys; i ++) {
			RawString.append(traceData.get(i)+"\n");
		}
		
		return RawString.toString();
	}
	
	private String removeToxicChars(String name) {
		name = name.replace("http://", "");
		name = name.replace("https://", "");
		name = name.replace("/", "_");
		
		return name;
	}

	private String generateBBCodeForumPingTest(String key, JSONObject jsonObject) {

			float min, max, avg, jitter;
			
			min 	= jsonObject.getFloat("minpingms");
			max 	= jsonObject.getFloat("maxpingms");
			jitter  = jsonObject.getFloat("jitterms");
			avg     = jsonObject.getFloat("avgpingms");
		
			StringBuilder BBCoded = new StringBuilder();
			BBCoded.append("[spoiler=PingTest to: ");
			BBCoded.append(key + " ");
			BBCoded.append("Min: ");
			BBCoded.append(min);
			BBCoded.append("ms Max: ");
			BBCoded.append(max);
			BBCoded.append("ms Avg: ");
			BBCoded.append(avg);
			BBCoded.append("ms Jitter: ");
			BBCoded.append(jitter);
			BBCoded.append("ms");
			BBCoded.append("]"); // end of  spoiler TAG  [spoiler]
			BBCoded.append("\n");
			BBCoded.append("[code]");
			BBCoded.append("\n");
			
			for(int i = 1; i < jsonObject.length()-3; i++) {
				Float data = jsonObject.getFloat(""+i);
				
				BBCoded.append(String.format("#%02d" , i)+" : " + data + " ms");
				BBCoded.append("\n");
			}
			
			BBCoded.append("[/code]\n");
			BBCoded.append("[/spoiler]\n");
			
			return BBCoded.toString();
	}

	private String toRawStringPingData(JSONObject jsonObject) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Max:" + jsonObject.getFloat("maxpingms"));
		sb.append("\n");
		sb.append("Min:" + jsonObject.getFloat("minpingms"));
		sb.append("\n");
		sb.append("Durchschnitt:" + jsonObject.getFloat("avgpingms"));
		sb.append("\n");
		sb.append("Jitter:" + jsonObject.getFloat("jitterms"));
		sb.append("\n");
		
		for(int i = 1; i < jsonObject.length()-3; i++) {
			Float data = jsonObject.getFloat(""+i);
			
			sb.append(String.format("#%03d" , i)+" : " + data + " ms");
			sb.append("\n");
		}
		
		return sb.toString();
	}

	private String toRawStringSpeedData(JSONObject jsonObject) {
		System.out.println(jsonObject);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("MaxMB/s:" + jsonObject.getFloat("maxMBps"));
		sb.append("\n");
		sb.append("AverageMB/s:" + jsonObject.getFloat("averageMBps"));
		sb.append("\n");
		sb.append("MaxMbit:" + jsonObject.getFloat("maxMBitps"));
		sb.append("\n");
		sb.append("AverageMBit/s:" + jsonObject.getFloat("averageMBps"));
		sb.append("\n");
		
		Iterator<String> itr = jsonObject.keySet().iterator();
		HashMap<Integer,Float> Bits = new HashMap<Integer,Float>();
		
		while(itr.hasNext()) {
			String key = itr.next();
			
			if (
					key.equals("maxMBps") ||
					key.equals("averageMBitps") ||
					key.equals("averageMBps") ||
					key.equals("maxMBitps")
				) {
				continue;
			}
			
			Bits.put(Integer.valueOf(key), jsonObject.getFloat(key));
		}
		
		// not the hashmap should be sorted so we can iterate over it for the keys
		
		Iterator<Integer> bititr = Bits.keySet().iterator();
		
		while(bititr.hasNext()) {
			int key = bititr.next();
			float value = Bits.get(key);
			if(key == 0) {
				key = 1;
			}
			
			String MiB = BitByteManager.humanReadableByteCountBin(  (long)value,true);
			String Mibit = BitByteManager.humanReadableBitCountBin( (long)value,true);
			String MB = BitByteManager.humanReadableByteCountSI(  (long)value,true);
			String Mbit = BitByteManager.humanReadableBitCountSI( (long)value );
			
			sb.append( key );
			sb.append(" - ");
			sb.append( value );

			sb.append(" -- [");
			sb.append( MiB );
			sb.append("] / [");
			sb.append( Mibit );
			sb.append("]");
			sb.append(" -- [");
			sb.append( MB );
			sb.append("] / [");
			sb.append( Mbit );
			sb.append("it/s]"); // Fix for  MB  to MBit/s
			sb.append("\n");
		}
		return sb.toString();
	}
}
