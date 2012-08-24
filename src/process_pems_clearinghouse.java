import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class process_pems_clearinghouse {

	private String datafolder;
	private int district;
	private int year;
	private int month;
	private int day;
	private String daystring;
	private String outfolder = "pemsdataclean";
	private Vector<Integer> vds = new Vector<Integer>();
	private URL url;
	protected HashMap <Integer,FiveMinuteData> data = new HashMap <Integer,FiveMinuteData> ();
	public boolean aggregatelanes;

	/** Input: 1) name of file with list of VDS stations
	 * 2) input file url
	 * 3) input file format
	 * 4) output folder
	 * 5) aggregate over lanes (true/false)
	 */
	public static void main(String[] args) {

		int i;
		String informat = "";
		//String inURL = "";
		String vdsfile = "";

		if(args.length!=9){
			System.out.println("Usage:");
			System.out.println("	args[0]: File with list of vds.");
			System.out.println("	args[1]: Data folder.");
			System.out.println("	args[2]: District.");
			System.out.println("	args[3]: Year.");
			System.out.println("	args[4]: Month.");
			System.out.println("	args[5]: Day.");
			System.out.println("	args[6]: Input format (caltransdbx,pems5min,pemshourly)");
			System.out.println("	args[7]: Output folder");
			System.out.println("	args[8]: Aggregate lanes (true,false)");
			return;
		}
			
		process_pems_clearinghouse X = new process_pems_clearinghouse();
		
		// read input parameters
		vdsfile = args[0];
		
		X.datafolder = args[1];
		X.district = Integer.parseInt(args[2]);
		X.year = Integer.parseInt(args[3]);
		X.month = Integer.parseInt(args[4]);
		X.day = Integer.parseInt(args[5]);

		//inURL = args[1];
		informat = args[6];
		X.outfolder = args[7];
		X.aggregatelanes = Boolean.parseBoolean(args[8]);
		
		try {
			
			String inURL = "";
			if(informat.equalsIgnoreCase("caltransdbx")){
				X.daystring = String.format("%d",X.year) + "_" + String.format("%02d",X.month) + "_" + String.format("%02d",X.day);
				inURL = X.datafolder + File.separator + X.daystring + ".txt";
			}
			else if(informat.equalsIgnoreCase("pems5min")){
				X.daystring = String.format("%d",X.year) + "_" + String.format("%02d",X.month) + "_" + String.format("%02d",X.day);
				inURL = X.datafolder + File.separator + "d" + String.format("%02d",X.district) +"_text_station_5min_" + X.daystring + ".txt";
			}
			else if(informat.equalsIgnoreCase("pemshourly")){
				X.daystring = String.format("%d",X.year) + "_" + String.format("%02d",X.month);
				inURL = X.datafolder + File.separator + "d" + String.format("%02d",X.district) +"_text_station_hour_" + X.daystring + ".txt";
			}
			else
				System.out.println("ERROR");
			
			X.url = new URL(inURL);
			
			// read detector stations from file
			ReadVDSFile(vdsfile,X.vds);

			// initialize data map
			for(i=0;i<X.vds.size();i++){
				int thisvds = X.vds.get(i);
				X.data.put(thisvds, new FiveMinuteData(thisvds,X.aggregatelanes));
			}
			
//			if(informat.equalsIgnoreCase("caltransdbx"))
//				X.myformat = X.new FileFormat("Caltrans DBX","dbx","\t",6,20,22,23,8,true);
//			else if(informat.equalsIgnoreCase("pems5min"))
//				X.myformat = X.new FileFormat("PeMS 5 min","pems5min",",",5,8,9,10,8,false);
//			else if(informat.equalsIgnoreCase("pemshourly"))
//				X.myformat = X.new FileFormat("PeMS Hourly","pemshour",",",3,15,16,17,8,false);
//			else
//				System.out.println("ERROR");
			
			AbstractDataIO dataIO = new DataIO_5min();
			
			dataIO.read(url,vds,data,aggregatelanes);

			// Write to text file
			dataIO.write(data,aggregatelanes,outfolder,daystring);
			
			System.out.println("Wrote to " + X.outfolder);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	private static void ReadVDSFile(String vdsfile,Vector<Integer> vds) throws Exception{
		String strLine;
		BufferedReader ba = new BufferedReader(new FileReader(vdsfile));
		while( (strLine = ba.readLine())!=null)
			vds.add( Integer.parseInt(strLine));
	}

}
