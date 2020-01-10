package NetworkToolkit;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
				break;
			}
			case STATISTIC_TYPE_UNKNOWN:
			{
				System.out.println(path + "=> STATISTIC_TYPE_UNKNOWN.");
				break;
			}
		}
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

	private void showPingStatistics(final JSONObject statisticData,final String file, boolean showGraph)
	{
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		
		Iterator<String> pingItr = statisticData.keySet().iterator();
		while(pingItr.hasNext()){
			String hostname = pingItr.next();
			JSONObject subData = statisticData.getJSONObject(hostname);
			Iterator<String> subItr = subData.keySet().iterator();
			final XYSeries chartdata = new XYSeries(hostname);
			int id = 0;
			while(subItr.hasNext()){
				String subKey = subItr.next();
				
				if(
						subKey.equals("minpingms") || 
						subKey.equals("maxpingms") || 
						subKey.equals("avgpingms") || 
						subKey.equals("jitterms")
					)
				{
					continue;
				}
				chartdata.add(id, subData.getDouble(subKey));
				id ++;
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
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	
				  @Override
				  public void run() {
	
				    // Create and set up the window.
				    JFrame frame = new JFrame(file);
				    frame.setLayout(new BorderLayout());
				    frame.setBounds(0,0, 900,900);
				    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
				    // chart
				    ChartPanel pnl = new ChartPanel(chart);
				    frame.add(pnl, BorderLayout.CENTER);
	
				    // label
				    JLabel label = new JLabel("", SwingConstants.CENTER);
				    frame.add(label, BorderLayout.SOUTH);
	
				    // Display the window.
				    frame.pack();
				    frame.setVisible(true);
				  }
				});
	    }
	}
	
}
