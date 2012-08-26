
/**
 * Timestamp			Date of data as MM/DD/YYYY HH24:MI:SS. Note that the indicates the 
 * 						beginning of the summary period. For example, a time of 08:00:00 
 * 						reports measurements from between 08:00:00 and 08:04:59.	 
 * Station				Unique station identifier. Use this value to cross-reference with Metadata files.	 
 * District				District #	 
 * Freeway				Freeway #	 
 * Direction of Travel	N | S | E | W	 
 * Lane Type			The type of lane (for example, ML=Mainline, FR=Off-ramp, OR=On-ramp, 
 * 						HV=High Occupancy Vehicle, CD=Coll/Dist, FF=Freeway-to-Freeway)	 
 * Station Length		Segment length covered by the station in miles/km.	 
 * Samples				Total number of samples received for all lanes.	 
 * % Observed			Percentage of individual lane points at this location that were observed (e.g. not imputed).	
 * Total Flow			Sum of flows over the 5-minute period across all lanes. Note that the basic 5-minute 
 * 						rollup normalizes flow by the number of good samples received from the controller.	Veh/5-min
 * Avg Occupancy		Average occupancy across all lanes over the 5-minute period expressed as a 
 * 						decimal number between 0 and 1.	
 * Avg Speed			Flow-weighted average speed over the 5-minute period across all lanes. 
 * 						If flow is 0, mathematical average of 5-minute station speeds.	Mph
 * Lane N Samples		Number of good samples received for lane N. N ranges from 1 to the number 
 * 						of lanes at the location.	 
 * Lane N Flow			[Veh/5-min] Total flow for lane N over the 5-minute period normalized by the number 
 * 						of good samples.	
 * Lane N Avg Occ		[%] Average occupancy for lane N expressed as a decimal number between 0 and 1. 
 * 						N ranges from 1 to the number of lanes at the location.	
 * Lane N Avg Speed		[Mph] Flow-weighted average of lane N speeds. If flow is 0, mathematical average of 
 * 						5-minute lane speeds. N ranges from 1 to the number of lanes	
 * Lane N Observed		1 indicates observed data, 0 indicates imputed.
 *
 */
public class DataIO_5min extends AbstractDataIO {

	public DataIO_5min() {
		outprefix = "pems5min";
		delimiter = ",";
		laneblocksize = 5;
		flwoffset = 8;
		occoffset = 9;
		spdoffset = 10;
		maxlanes = 8;
		hasheader = false;
	}

}
