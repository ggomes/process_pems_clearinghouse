import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public abstract class AbstractDataIO {

	public String data_file_name;
	public String outprefix;	// prefix added to all output files
	public String delimiter;	// column separator in the data file
	public int laneblocksize;	// number of data columns per lane
	public int flwoffset;		// offset of the flow value within the block
	public int occoffset;		// offset of the occupancy value within the block
	public int spdoffset;		// offset of the speed value within the block
	public int maxlanes;		// maximum number of lane blocks
	public boolean hasheader;	// true => ignore first line
	public float flow_mult;		// conversion factor to veh/hr
		
	public void read(Vector<Integer> vds,HashMap <Integer,TrafficData> data,boolean aggregatelanes) throws NumberFormatException, IOException{
		
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
    	System.out.println("\t+ file aname: " + data_file_name);
    	System.out.println("\t+ stations: " + vds.toString());
    	
    	int largestoffset = Math.max(Math.max(flwoffset,occoffset),spdoffset);
    		
    	BufferedReader fin = new BufferedReader(new FileReader(data_file_name));
		if(hasheader)
			line=fin.readLine(); 	// discard the header
    	while ((line=fin.readLine()) != null) {
            String f[] = line.split(delimiter,-1);
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
            
            int maxlanes = (int)((f.length-largestoffset)/laneblocksize);
            maxlanes = Math.min(maxlanes,maxlanes);
            
            for (lane=0;lane<maxlanes;lane++) {
            	
            	index = laneblocksize*(lane+1)+flwoffset;
            	str = f[index];
            	hasflw = !str.isEmpty();
            	if(hasflw){
            		val = Float.parseFloat(str)*flow_mult;
            		laneflw.add(val);
            		totalflw += val;
            	}
            	else
                	laneflw.add(Float.NaN); 
            	
            	index = laneblocksize*(lane+1)+occoffset;
            	str = f[index];
            	hasocc = !str.isEmpty();
            	if(hasocc)
            		laneocc.add(Float.parseFloat(str));
            	else
            		laneocc.add(Float.NaN); 
            	
            	index = laneblocksize*(lane+1)+spdoffset;
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
            TrafficData D = data.get(thisvds);
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
	
	public void write(HashMap <Integer,TrafficData> data,boolean aggregatelanes) throws Exception{

		int i;
		
		String aggstring;
        if(aggregatelanes)
        	aggstring = "agg";
        else
        	aggstring = "lanes";
        	
		for(Integer thisvds : data.keySet()) {
			TrafficData D = data.get(thisvds);

			PrintStream flw_out = new PrintStream(new FileOutputStream(outprefix + thisvds + "_flw_" + aggstring + ".txt"));
    		for(i=0;i<D.numflw();i++)
    			flw_out.println(D.getTimeString(i)+"\t"+D.getFlwString(i));
            flw_out.close();

            PrintStream occ_out;
            if(aggregatelanes)
            	occ_out = new PrintStream(new FileOutputStream(outprefix + thisvds + "_dty_" + aggstring + ".txt"));
            else
            	occ_out = new PrintStream(new FileOutputStream(outprefix + thisvds + "_occ_" + aggstring + ".txt"));
			for(i=0;i<D.numocc();i++)
				occ_out.println(D.getTimeString(i)+"\t"+D.getOccString(i));
            occ_out.close();
            
			PrintStream spd_out = new PrintStream(new FileOutputStream(outprefix + thisvds + "_spd_" + aggstring + ".txt"));
			for(i=0;i<D.numspd();i++)
				spd_out.println(D.getTimeString(i)+"\t"+D.getSpdString(i));
            spd_out.close();
		}		
	}

    public static Date ConvertTime(final String timestr) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        ParsePosition pp = new ParsePosition(0);
        return formatter.parse(timestr,pp);
    }
    
}
