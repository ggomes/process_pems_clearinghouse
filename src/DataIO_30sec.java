import java.io.File;


/**
 * Timestamp			Sample time as reported by the field element as MM/DD/YYYY HH24:MI:SS.	 
 * Station				Unique station identifier. Use this value to cross-reference with Metadata files.	 
 * Lane N Flow			Number of vehicle that passed over the detector during the sample period. 
 * 						N ranges from 1 to the number of lanes at the location.	Veh/Sample Period
 * Lane N Occupancy		Occupancy of the lane during the sample period expressed as a decimal number 
 * 						between 0 and 1. N ranges from 1 to the number of lanes at the location.	
 * Lane N Speed			Speed as measured by the detector. Empty if the detector does not report speed. 
 * 						N ranges from 1 to the number of lanes at the location.
 */
public class DataIO_30sec extends BaseDataIO {

	public DataIO_30sec(String datafolder,String outfolder,int district,int day,int month,int year) {
		String daystring = String.format("%d",year) + "_" + String.format("%02d",month) + "_" + String.format("%02d",day);
		data_file_name = datafolder + File.separator + "d" + String.format("%02d",district) +"_text_station_raw_" + daystring + ".txt";
		outprefix = outfolder + File.separator + "pems30sec" + "_" + daystring + "_";;
		delimiter = ",";
		laneblocksize = 3;
		flwoffset = -1;
		occoffset = 0;
		spdoffset = 1;
		maxlanes = 8;
		hasheader = false;
		flow_mult = 120f;
	}
	
}
