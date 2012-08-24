import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

public class DataIO_5min extends AbstractDataIO {

	public DataIO_5min() {
		this.name = "PeMS 5 min";
		this.outprefix = "pems5min";
		this.delimiter = ",";
		this.laneblocksize = 5;
		this.flwoffset = 8;
		this.occoffset = 9;
		this.spdoffset = 10;
		this.maxlanes = 8;
		this.hasheader = false;
	}

	@Override
	public void read(URL url,Vector<Integer> vds,HashMap <Integer,FiveMinuteData> data,boolean aggregatelanes) throws NumberFormatException, IOException {
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
    	System.out.println("\t+ format: " + name);
    	
    	int largestoffset = Math.max(Math.max(flwoffset,occoffset),spdoffset);
    		
		URLConnection uc = url.openConnection();
		BufferedReader fin = new BufferedReader(new InputStreamReader(uc.getInputStream()));
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
            		val = Float.parseFloat(str)*12f;
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

	@Override
	public void write(HashMap <Integer,FiveMinuteData> data,boolean aggregatelanes,String outfolder,String daystring) throws Exception {
		int i;
		
		String aggstring;
        if(aggregatelanes)
        	aggstring = "agg";
        else
        	aggstring = "lanes";
        	
		for(Integer thisvds : data.keySet()) {
			FiveMinuteData D = data.get(thisvds);

			PrintStream flw_out = new PrintStream(new FileOutputStream(outfolder + File.separator + outprefix + "_" + daystring + "_" + thisvds + "_flw_" + aggstring + ".txt"));
    		for(i=0;i<D.numflw();i++)
    			flw_out.println(D.getTimeString(i)+"\t"+D.getFlwString(i));
            flw_out.close();

            PrintStream occ_out;
            if(aggregatelanes)
            	occ_out = new PrintStream(new FileOutputStream(outfolder + File.separator + outprefix + "_" + daystring+ "_" + thisvds + "_dty_" + aggstring + ".txt"));
            else
            	occ_out = new PrintStream(new FileOutputStream(outfolder + File.separator + outprefix + "_" + daystring+ "_" + thisvds + "_occ_" + aggstring + ".txt"));
			for(i=0;i<D.numocc();i++)
				occ_out.println(D.getTimeString(i)+"\t"+D.getOccString(i));
            occ_out.close();
            
			PrintStream spd_out = new PrintStream(new FileOutputStream(outfolder + File.separator + outprefix + "_" + daystring+ "_" + thisvds + "_spd_" + aggstring + ".txt"));
			for(i=0;i<D.numspd();i++)
				spd_out.println(D.getTimeString(i)+"\t"+D.getSpdString(i));
            spd_out.close();
			
		}		
	}

}
