import java.io.File;


/**
 * Timestamp			Date of data as MM/DD/YYYY HH24:MI:SS. Note that minute and second 
 * 						are always 0 for hourly data. For example, a time of 08:00:00 reports 
 * 						measurements from between 08:00:00 and 08:59:59.	 
 * Station				Unique station identifier. Use this value to cross-reference with Metadata files.	 
 * District				District #	 
 * Route				Route #	
 * Direction of Travel	N | S | E | W	 
 * Lane Type			The type of lane (for example, ML=Mainline, FR=Off-ramp, OR=On-ramp, 
 * 						HV=High Occupancy Vehicle, CD=Coll/Dist, FF=Freeway-to-Freeway)	 
 * Station Length		Segment length covered by the station in miles/km.	 
 * Samples				Total number of samples received for all lanes.	 
 * % Observed			[%] Percentage of 5-minute lane points that were observed (e.g. not imputed).
 * Total Flow			[Veh/Hour] Sum of 5-minute flows over the hour. Note that the basic 5-minute rollup normalizes 
 * 						flow by the number of good samples received from the controller.	
 * Avg Occupancy		[%] Average of 5-minute station occupancies over the hour expressed as a decimal number 
 * 						between 0 and 1.
 * Avg Speed			[Mph] Flow-weighted average of 5-minute station speeds. If flow is 0, mathematical average 
 * 						of 5-minute station speeds.	
 * Delay (V_t=35)		The average delay over the station length, with respect to a threshold speed of 35 mph.	 
 * Delay (V_t=40)		The average delay over the station length, with respect to a threshold speed of 40 mph.	 
 * Delay (V_t=45)		The average delay over the station length, with respect to a threshold speed of 45 mph.	 
 * Delay (V_t=50)		The average delay over the station length, with respect to a threshold speed of 50 mph.	 
 * Delay (V_t=55)		The average delay over the station length, with respect to a threshold speed of 55 mph.	 
 * Delay (V_t=60)		The average delay over the station length, with respect to a threshold speed of 60 mph.	 
 * Lane N Flow			[Veh/Hour] Sum of 5-minute flows for lane N over the hour. Note that the basic 5-minute rollup 
 * 						normalizes flow by the number of good samples received from the controller. N ranges from 
 * 						1 to the number of lanes at the location.	
 * Lane N Avg Occ		[%] Average of 5-minute occupancies for lane N over the hour expressed as a decimal number 
 * 						between 0 and 1. N ranges from 1 to the number of lanes at the location.	
 * Lane N Avg Speed		[Mph] Flow-weighted average of 5-minute lane N speeds. If flow is 0, mathematical average of 5-minute 
 * 						lane speeds. N ranges from 1 to the number of lanes at the location.
 */
public class DataIO_1hour extends AbstractDataIO {
	
	public DataIO_1hour(String datafolder,String outfolder,int district,int day,int month,int year) {
		String daystring = String.format("%d",year) + "_" + String.format("%02d",month);
		data_file_name = datafolder + File.separator + "d" + String.format("%02d",district) +"_text_station_hour_" + daystring + ".txt";
		outprefix = outfolder + File.separator + "pemshour" + "_" + daystring + "_";;
		delimiter = ",";
		laneblocksize = 3;
		flwoffset = 15;
		occoffset = 16;
		spdoffset = 17;
		maxlanes = 8;
		hasheader = false;
		flow_mult = 1f;
	}
	
}
