package NetworkToolkit;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.JSONObject;

public class StatisticViewer {

	private final int STATISTIC_TYPE_UNKNOWN = 0;
	private final int STATISTIC_TYPE_ALL = 1;
	
	private final int STATISTIC_TYPE_SPEED = 2;
	private final int STATISTIC_TYPE_PING = 3;
	private final int STATISTIC_TYPE_TRACE = 4;
	
	
	public StatisticViewer() 
	{
		
	}

	public void startUsingFileSelection() throws Exception
	{
		JFileChooser c = new JFileChooser();
		c.setCurrentDirectory(new File("./data/statistics"));
		c.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				
				if(arg0.isDirectory())
					return true;
				
				if(arg0.getName().endsWith(".json"))
					return true;
				
				
				return false;
			}

			@Override
			public String getDescription() {
				return "StatisticFile[*.json]";
			}});
		
		int result = c.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION)
		{
			File f = c.getSelectedFile();
			
			JSONObject statisticData = loadStatisticFile(f);
			
			runViewer(statisticData,f.getAbsolutePath());
			
		}
	}
	
	public void runViewer(final JSONObject statisticData,final String path) {
		runViewer(statisticData, path, true);
	}
	
	public void runViewer(final JSONObject statisticData,final String path, final boolean showGraph) {
		switch(validateData(statisticData))
		{
			case STATISTIC_TYPE_SPEED:
			{
				System.out.println(path + "=> STATISTIC_TYPE_SPEED.");
				showSpeedStatistics(statisticData, path, showGraph );
				break;
			}
			case STATISTIC_TYPE_PING:
			{
				System.out.println(path + "=> STATISTIC_TYPE_PING.");
				showPingStatistics(statisticData, path, showGraph );
				break;
			}
			case STATISTIC_TYPE_TRACE:
			{
				System.out.println(path + "=> STATISTIC_TYPE_TRACE.");
				break;
			}
			case STATISTIC_TYPE_ALL:
			{
				System.out.println(path + "=> STATISTIC_TYPE_ALL.");
				showPingStatistics(convertAllTestDataToPingOnlyData(statisticData), path, showGraph );
				showSpeedStatistics(convertAllTestDataToSpeedOnlyData(statisticData), path, showGraph );
				System.out.println(statisticData);
				break;
			}
			case STATISTIC_TYPE_UNKNOWN:
			{
				System.out.println(path + "=> STATISTIC_TYPE_UNKNOWN.");
				break;
			}
		}
	}

	private JSONObject convertAllTestDataToSpeedOnlyData(JSONObject statisticData) {
		JSONObject returnObject = new JSONObject();
		
		Iterator<String> mainItr = statisticData.keySet().iterator();
		while(mainItr.hasNext()){
			String host = mainItr.next();
			JSONObject hostobject = statisticData.getJSONObject(host);
			JSONObject newHostObjey = new JSONObject();
			if(hostobject.has("rawspeeddata"))
			{

				newHostObjey.put("averageMBitps", hostobject.getFloat("averageMBitps"));
				newHostObjey.put("averageMBps", hostobject.getFloat("averageMBps"));
				newHostObjey.put("maxMBitps", hostobject.getFloat("maxMBitps"));
				newHostObjey.put("maxMBps", hostobject.getFloat("maxMBps"));
				
				// Now add the Raw speed data
				JSONObject rawspeedobj = statisticData.getJSONObject(host).getJSONObject("rawspeeddata");
				Iterator<String> rawspeeditr = rawspeedobj.keySet().iterator();
				while(rawspeeditr.hasNext()){
					String rawspeedkey = rawspeeditr.next();
					long bytes = rawspeedobj.getLong(rawspeedkey);
					newHostObjey.put(rawspeedkey, bytes);
				}
				returnObject.put(host, newHostObjey);
				
			}
		}
		return returnObject;
	}

	private JSONObject convertAllTestDataToPingOnlyData(JSONObject statisticData) {
		JSONObject returnObject = new JSONObject();
		
		Iterator<String> mainItr = statisticData.keySet().iterator();
		while(mainItr.hasNext()){
			String host = mainItr.next();
			JSONObject hostobject = statisticData.getJSONObject(host);
			JSONObject newHostObjey = new JSONObject();
			if(hostobject.has("minpingms"))
			{
				newHostObjey.put("minpingms", hostobject.getFloat("minpingms"));
				newHostObjey.put("maxpingms", hostobject.getFloat("maxpingms"));
				newHostObjey.put("avgpingms", hostobject.getFloat("avgpingms"));
				newHostObjey.put("jitterms", hostobject.getFloat("jitterms"));
				
				// Now add the Raw speed data
				JSONObject rawspeedobj = statisticData.getJSONObject(host).getJSONObject("ping");
				Iterator<String> rawspeeditr = rawspeedobj.keySet().iterator();
				while(rawspeeditr.hasNext()){
					String rawspeedkey = rawspeeditr.next();
					float pingms = rawspeedobj.getFloat(rawspeedkey);
					newHostObjey.put(rawspeedkey, pingms);
				}
				
				returnObject.put(host, newHostObjey);
				
			}
		}
		return returnObject;
	}


	private int validateData(JSONObject statisticData) {
		
		Iterator<String> dataitr = statisticData.keySet().iterator();
		
		boolean hasSpeedData = false;
		boolean hasPingData = false;
		boolean hasTraceData = false;
		
		while(dataitr.hasNext()){
			String key = dataitr.next();
			try
			{
				JSONObject subData = statisticData.getJSONObject(key);
				if(subData.has("maxpingms")) {
					hasPingData = true;
				}
				
				if(subData.has("maxMBps")) {
					hasSpeedData = true;
				}
			}catch(Exception e)
			{
				System.out.println("Maybe Tracetest?");
				hasTraceData = true;
			}
		}
		
		
		if(
				hasSpeedData && hasPingData  ||
				hasSpeedData && hasTraceData ||
				hasTraceData && hasPingData
				) {

			return STATISTIC_TYPE_ALL;
		}
		if(hasSpeedData) {
			return STATISTIC_TYPE_SPEED;
		}
		
		if(hasPingData) {
			return STATISTIC_TYPE_PING;
		}
		if(hasTraceData)
		{
			return STATISTIC_TYPE_TRACE;
		}
		return STATISTIC_TYPE_UNKNOWN;
	}

	private JSONObject loadStatisticFile(final File f) throws Exception {
		return new JSONObject(new String(Files.readAllBytes(Paths.get(f.getAbsolutePath()))).trim());
	}

	private void showSpeedStatistics(final JSONObject statisticData,final String file, boolean showGraph)
	{
		HashMap<String, HashMap<String, Float>> testDetails = new HashMap<String, HashMap<String, Float>>();
		
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		Iterator<String> speedItr = statisticData.keySet().iterator();
		while(speedItr.hasNext()){
			String hostname = speedItr.next();
			JSONObject subData = statisticData.getJSONObject(hostname);
			Iterator<String> subItr = subData.keySet().iterator();
			final XYSeries chartdataMB = new XYSeries(hostname+"[MB/s]");
			final XYSeries chartdataMBit = new XYSeries(hostname+"[MBit/s]");

			ArrayList<Integer> speedids = new ArrayList<Integer>();
			HashMap<String,Float> detailDataSet = new HashMap<String,Float>();
			while(subItr.hasNext()){
				String subKey = subItr.next();
				
				if(
						subKey.equals("averageMBitps") || 
						subKey.equals("averageMBps") || 
						subKey.equals("maxMBitps") || 
						subKey.equals("maxMBps")
					)
				{
					
					detailDataSet.put(subKey, statisticData.getJSONObject(hostname).getFloat(subKey));
					
					continue;
				}else
				{
					if(
							subKey.equals("minpingms") || 
							subKey.equals("maxpingms") || 
							subKey.equals("avgpingms") || 
							subKey.equals("jitterms")
						)
					{
						continue;
					}
				}
				
				speedids.add(Integer.valueOf(subKey));
			}
			testDetails.put(hostname, detailDataSet);
			Collections.sort(speedids);
			
			for(int i = 0; i < speedids.size(); i++){
				
				int key = speedids.get(i);
				long value = subData.getLong(""+key);
				System.out.println("Key: " + key + " --- " + value);
				
				float calckey = ((float)key)/1000;
				
				if(calckey==0)
				{
					calckey = 1;
				}
				
				double toMbit = ((double)value)/1024/1024/calckey;
				
				chartdataMB.add(key, toMbit);
				chartdataMBit.add(key, toMbit*8);
			}
			
			xyDataset.addSeries(chartdataMB);
			xyDataset.addSeries(chartdataMBit);
			
		}
		
		final JFreeChart chart = ChartFactory.createXYLineChart("SpeedVerlauf zu: "+file, "Zeitlicher Verlauf(Sekunden)", "Speed", xyDataset);
		
	    try {
	    	OutputStream out = new FileOutputStream(file+".png");
			ChartUtils.writeChartAsPNG(out,
					chart,
					500,
					300);
			System.out.println("Saved: Image_File: "+ file + ".png");
		} catch (IOException e) {
			System.out.println("Error saving Image_File: "+ file+".png");
		}
	    
	    if ( showGraph )
	    {
	    	showGraph(chart,file, testDetails);
	    }
	}
	
	private void showGraph(final JFreeChart chart,final String file,final HashMap<String,HashMap<String,Float>> testDetails) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			  @Override
			  public void run() {
				 
				  
				Font font = new Font("Times New Roman", Font.BOLD , 14);
				chart.getTitle().setFont(font);
				  
			    // Create and set up the window.
			    JFrame frame = new JFrame(file);
			    frame.setBounds(0,0,1024,1000);
			    frame.setLayout(null);
			    
			    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			    // chart
			    ChartPanel chartPanel = new ChartPanel(chart);
			    chartPanel.setBounds(0,0,800,900);
			    JPanel DetailsPanel = new JPanel();
			    DetailsPanel.setLayout(null);
			    DetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Details:"));
			    
			    
			    // Set Detail Data
			    int paddingtop = 5;
			    if(testDetails != null)
				{
			    	
					Iterator<String> DetailsItr = testDetails.keySet().iterator();
					
					while(DetailsItr.hasNext()) {
						String host = DetailsItr.next();
						System.out.println("Hostdata:" + host);
						
						JLabel hostnamelbl = new JLabel(host,SwingConstants.CENTER);
						hostnamelbl.setBounds(0,paddingtop, 200, 25);
						
						paddingtop += 35;
						
						Iterator<String> HostDetailItr = testDetails.get(host).keySet().iterator();  
						DetailsPanel.add(hostnamelbl);
						
						while(HostDetailItr.hasNext())
						{
							String detailkey = HostDetailItr.next();
							float detailvalue = testDetails.get(host).get(detailkey);
							System.out.println("-" + keyToNiceName(detailkey) + " => " + detailvalue);
							
							JLabel detaillblname = new JLabel(keyToNiceName(detailkey),SwingConstants.CENTER);
							detaillblname.setBounds(0,paddingtop, 100, 25);
							
							JLabel detaillbldata = new JLabel(" " + detailvalue,SwingConstants.CENTER);
							detaillbldata.setBounds(95,paddingtop, 100, 25);
							paddingtop += 20;
							
							DetailsPanel.add(detaillblname);
							DetailsPanel.add(detaillbldata);

						}
						paddingtop += 35;
					}
					
					DetailsPanel.setPreferredSize(new Dimension(200,paddingtop));
				    
				}
			    
			    // End Set Detail Data
			    
			    JPanel FrameLayout = new JPanel();
			    FrameLayout.setBounds(0,0,1024,1000);
			    FrameLayout.setLayout(null);
			    
			    FrameLayout.add(chartPanel);
			    
			    JScrollPane scrollDetailsPane = new JScrollPane(DetailsPanel);
			    scrollDetailsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			    scrollDetailsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			    scrollDetailsPane.setBounds(800,0,200,900);
			    
			    FrameLayout.add(scrollDetailsPane);
			    
			    frame.add(FrameLayout);
			    
			    frame.setVisible(true);
			  }

			private String keyToNiceName(String detailkey) {
				
				switch(detailkey)
				{
					// PingTests
					case "maxMBitps":
					{
						return "Max. MBit/s:";
					}
					case "maxMBps":
					{
						return "Max. MB/s:";
					}
					case "averageMBps":
					{
						return "Avg. MB/s:";
					}
					case "averageMBitps":
					{
						return "Avg. MBit/s:";
					}
					// SpeedTests
					case "maxpingms":
					{
						return "Max. Ping[ms]:";
					}
					case "minpingms":
					{
						return "Min. Ping[ms]:";
					}
					case "jitterms":
					{
						return "Jitter[ms]:";
					}
					case "avgpingms":
					{
						return "Avg. Ping[ms]:";
					}
					
				}
				
				return "unk. - " + detailkey;
			}
		});
	}

	private void showPingStatistics(final JSONObject statisticData,final String file, boolean showGraph)
	{
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		HashMap<String, HashMap<String, Float>> testDetails = new HashMap<String, HashMap<String, Float>>();
		
		Iterator<String> pingItr = statisticData.keySet().iterator();
		while(pingItr.hasNext()){
			String hostname = pingItr.next();
			JSONObject subData = statisticData.getJSONObject(hostname);
			Iterator<String> subItr = subData.keySet().iterator();
			final XYSeries chartdata = new XYSeries(hostname);
			int id = 0;
			HashMap<String,Float> detailDataSet = new HashMap<String,Float>();
			
			while(subItr.hasNext()){
				String subKey = subItr.next();
				
				if(
						subKey.equals("minpingms") || 
						subKey.equals("maxpingms") || 
						subKey.equals("avgpingms") || 
						subKey.equals("jitterms")
					)
				{
					detailDataSet.put(subKey, statisticData.getJSONObject(hostname).getFloat(subKey));
					continue;
				}else
				{
					if(
							subKey.equals("averageMBitps") || 
							subKey.equals("averageMBps") || 
							subKey.equals("maxMBitps") || 
							subKey.equals("maxMBps")
						)
					{
						continue;
					}
				}
				chartdata.add(id, subData.getDouble(subKey));
				id ++;

				testDetails.put(hostname, detailDataSet);

			}
			xyDataset.addSeries(chartdata);
			
		}
		
		final JFreeChart chart = ChartFactory.createXYLineChart("Pingverlauf zu: "+file, "Pings", "Milliseconds", xyDataset);
		chart.setAntiAlias(true);
		
		
	    try {
	    	OutputStream out = new FileOutputStream(file+".png");
			ChartUtils.writeChartAsPNG(out,
					chart,
					500,
					300);
			System.out.println("Saved: Image_File: "+ file + ".png");
		} catch (IOException e) {
			System.out.println("Error saving Image_File: "+ file+".png");
		}
	    
	    if ( showGraph )
	    {	
	    	showGraph(chart,file,testDetails);
	    }
	}
}
