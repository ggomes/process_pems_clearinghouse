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
	private FileFormat myformat;
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
			
			if(informat.equalsIgnoreCase("caltransdbx"))
				X.myformat = X.new FileFormat("Caltrans DBX","dbx","\t",6,20,22,23,8,true);
			else if(informat.equalsIgnoreCase("pems5min"))
				X.myformat = X.new FileFormat("PeMS 5 min","pems5min",",",5,8,9,10,8,false);
			else if(informat.equalsIgnoreCase("pemshourly"))
				X.myformat = X.new FileFormat("PeMS Hourly","pemshour",",",3,15,16,17,8,false);
			else
				System.out.println("ERROR");
			
			X.ReadDataSource();

			// Write to text file
			X.WriteDataToFile();
			
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
    
	public void ReadDataSource() throws NumberFormatException, IOException{
		int lane;
    	String line,str;
    	int indexof;
        Calendar calendar = Calendar.getInstance();
    	float totalflw,totalspd;
    	float val;
    	long time;
    	int actuallanes;
    	boolean hasflw,hasspd,hasocc;
    	    	
    	System.out.println("Reading data,");
    	System.out.println("\t+ url: " + url.getFile());
    	System.out.println("\t+ stations: " + vds.toString());
    	System.out.println("\t+ format: " + myformat.name);
    	
    	int largestoffset = Math.max(Math.max(myformat.flwoffset,myformat.occoffset),myformat.spdoffset);
    		
		URLConnection uc = url.openConnection();
		BufferedReader fin = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		if(myformat.hasheader)
			line=fin.readLine(); 	// discard the header
    	while ((line=fin.readLine()) != null) {
            String f[] = line.split(myformat.delimiter,-1);
            int thisvds = Integer.parseInt(f[1]);

            indexof = vds.indexOf(thisvds);
            if(indexof<0)
            	continue;
            
    		calendar.setTime(ConvertTime(f[0]));
    		time = calendar.getTime().getTime()/1000;
    
        	ArrayList<Float> laneflw = new ArrayList<Float>();
        	ArrayList<Float> laneocc = new ArrayList<Float>();
        	ArrayList<Float> lanespd = new ArrayList<Float>();
        
        	// store in lane-wise ArrayList
        	actuallanes = 0;
            totalflw = 0;
            totalspd = 0;
            int index;
            
            int maxlanes = (int)((f.length-largestoffset)/myformat.laneblocksize);
            maxlanes = Math.min(maxlanes,myformat.maxlanes);
            
            for (lane=0;lane<maxlanes;lane++) {
            	
            	index = myformat.laneblocksize*(lane+1)+myformat.flwoffset;
            	str = f[index];
            	hasflw = !str.isEmpty();
            	if(hasflw){
            		val = Float.parseFloat(str)*12f;
            		laneflw.add(val);
            		totalflw += val;
            	}
            	else
                	laneflw.add(Float.NaN); 
            	
            	index = myformat.laneblocksize*(lane+1)+myformat.occoffset;
            	str = f[index];
            	hasocc = !str.isEmpty();
            	if(hasocc)
            		laneocc.add(Float.parseFloat(str));
            	else
            		laneocc.add(Float.NaN); 
            	
            	index = myformat.laneblocksize*(lane+1)+myformat.spdoffset;
            	str = f[index];
            	hasspd = !str.isEmpty();
            	if(hasspd){
            		val = Float.parseFloat(str);
            		lanespd.add(val);
            		totalspd += val;
            	}
            	else
            		lanespd.add(Float.NaN); 
            	if(hasflw || hasocc || hasspd)
            		actuallanes++;
            }

            // find the data structure and store. 
            FiveMinuteData D = data.get(thisvds);
            if(aggregatelanes && actuallanes>0){
                totalspd /= actuallanes;
                D.addAggFlw(totalflw);
                D.addAggOcc(totalflw/totalspd);		// actually density
                D.addAggSpd(totalspd);
                D.time.add(time);	
            }
            else{
            
	            D.flwadd(laneflw,0,actuallanes);
	            D.occadd(laneocc,0,actuallanes);
	            D.spdadd(lanespd,0,actuallanes);
	            D.time.add(time);
            }
        }    	
        fin.close();
    }
    
	private void WriteDataToFile() throws Exception{
		
		int i;
		
		String aggstring;
        if(aggregatelanes)
        	aggstring = "agg";
        else
        	aggstring = "lanes";
        	
		for(Integer thisvds : data.keySet()) {
			FiveMinuteData D = data.get(thisvds);

			PrintStream flw_out = new PrintStream(new FileOutputStream(outfolder + File.separator + myformat.outputprefix + "_" + daystring + "_" + thisvds + "_flw_" + aggstring + ".txt"));
    		for(i=0;i<D.numflw();i++)
    			flw_out.println(D.getTimeString(i)+"\t"+D.getFlwString(i));
            flw_out.close();

            PrintStream occ_out;
            if(aggregatelanes)
            	occ_out = new PrintStream(new FileOutputStream(outfolder + File.separator + myformat.outputprefix + "_" + daystring+ "_" + thisvds + "_dty_" + aggstring + ".txt"));
            else
            	occ_out = new PrintStream(new FileOutputStream(outfolder + File.separator + myformat.outputprefix + "_" + daystring+ "_" + thisvds + "_occ_" + aggstring + ".txt"));
			for(i=0;i<D.numocc();i++)
				occ_out.println(D.getTimeString(i)+"\t"+D.getOccString(i));
            occ_out.close();
            
			PrintStream spd_out = new PrintStream(new FileOutputStream(outfolder + File.separator + myformat.outputprefix + "_" + daystring+ "_" + thisvds + "_spd_" + aggstring + ".txt"));
			for(i=0;i<D.numspd();i++)
				spd_out.println(D.getTimeString(i)+"\t"+D.getSpdString(i));
            spd_out.close();
			
		}		
	}

    private static Date ConvertTime(final String timestr) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        ParsePosition pp = new ParsePosition(0);
        return formatter.parse(timestr,pp);
    }

    private class FileFormat {
    	public String outputprefix;
    	public String name;
    	public int laneblocksize;
    	public int flwoffset;
    	public int occoffset;
    	public int spdoffset;
    	public int maxlanes;
    	public boolean hasheader;
    	public String delimiter;

    	public FileFormat(String name,String outprefix,String delimiter,int laneblocksize, int flwoffset,int occoffset,int spdoffset, int maxlanes,boolean hasheader) {
    		super();
    		this.name = name;
    		this.outputprefix = outprefix;
    		this.delimiter = delimiter;	
    		this.laneblocksize = laneblocksize;
    		this.flwoffset = flwoffset;
    		this.occoffset = occoffset;
    		this.spdoffset = spdoffset;
    		this.maxlanes = maxlanes;
    		this.hasheader = hasheader;
    	}
    
    
    }

}
