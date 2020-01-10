package NetworkToolkit;

import java.awt.BorderLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.json.JSONObject;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class StatisticViewer {

	private final int STATISTIC_TYPE_UNKNOWN = 0;
	private final int STATISTIC_TYPE_ALL = 1;
	
	private final int STATISTIC_TYPE_SPEED = 2;
	private final int STATISTIC_TYPE_PING = 3;
	private final int STATISTIC_TYPE_TRACE = 4;
	
	
	public StatisticViewer() throws Exception
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
			
			System.out.println(statisticData);
			
			switch(validateData(statisticData))
			{
				case STATISTIC_TYPE_SPEED:
				{
					System.out.println(f.getAbsolutePath() + "=> STATISTIC_TYPE_SPEED.");
					break;
				}
				case STATISTIC_TYPE_PING:
				{
					System.out.println(f.getAbsolutePath() + "=> STATISTIC_TYPE_PING.");
					showPingStatistics(statisticData,f);
					break;
				}
				case STATISTIC_TYPE_TRACE:
				{
					System.out.println(f.getAbsolutePath() + "=> STATISTIC_TYPE_TRACE.");
					break;
				}
				case STATISTIC_TYPE_ALL:
				{
					System.out.println(f.getAbsolutePath() + "=> STATISTIC_TYPE_ALL.");
					break;
				}
				case STATISTIC_TYPE_UNKNOWN:
				{
					System.out.println(f.getAbsolutePath() + "=> STATISTIC_TYPE_UNKNOWN.");
					break;
				}
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

	private void showPingStatistics(final JSONObject statisticData,final File file)
	{
		// Create Chart
		
		final XYChart chart = new XYChartBuilder().width(600).height(400).xAxisTitle("Zeit").yAxisTitle("Pingzeit").build();
		
		// Customize Chart
		chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
	    chart.getStyler().setChartPadding(30);
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		chart.getStyler().setAntiAlias(true);
		// Series
		
		Iterator<String> pingItr = statisticData.keySet().iterator();
		while(pingItr.hasNext()){
			String hostname = pingItr.next();
			JSONObject subData = statisticData.getJSONObject(hostname);
			ArrayList<Double> pingtimes = new ArrayList<Double>();
			Iterator<String> subItr = subData.keySet().iterator();
			chart.addInfoContent("Test info content");
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
				pingtimes.add(subData.getDouble(subKey));
			}

			double[] steps = new double[pingtimes.size()];
			double[] timings = new double[pingtimes.size()];
			
			for(int i = 0; i < pingtimes.size(); i++) {
				steps[i] = i;
				timings[i] = pingtimes.get(i);
			}
			
			chart.addSeries(hostname, steps, timings);
			
		}
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

		  @Override
		  public void run() {

		    // Create and set up the window.
		    JFrame frame = new JFrame(file.getName());
		    frame.setLayout(new BorderLayout());
		    frame.setBounds(0,0, 900,900);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		    // chart
		    JPanel chartPanel = new XChartPanel<XYChart>(chart);
		    frame.add(chartPanel, BorderLayout.CENTER);

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
