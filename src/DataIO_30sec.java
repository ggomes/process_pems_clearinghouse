
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
public class DataIO_30sec extends AbstractDataIO {

	public DataIO_30sec() {
		outprefix = "pems30sec";
		delimiter = ",";
		laneblocksize = 3;
		flwoffset = 0;
		occoffset = 1;
		spdoffset = 2;
		maxlanes = 8;
		hasheader = false;
	}
	
}
