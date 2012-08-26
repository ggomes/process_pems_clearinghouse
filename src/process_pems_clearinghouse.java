import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

public class process_pems_clearinghouse {

	/** Input: 1) name of file with list of VDS stations
	 * 2) input file url
	 * 3) input file format
	 * 4) output folder
	 * 5) aggregate over lanes (true/false)
	 */
	public static void main(String[] args) {

		int i;
		String informat = "";
		String vdsfile = "";

		String datafolder;
		int district;
		int year;
		int month;
		int day;
		String daystring = "";
		String outfolder = "pemsdataclean";
		Vector<Integer> vds = new Vector<Integer>();
		String data_file_name = "";
		HashMap <Integer,FiveMinuteData> data = new HashMap <Integer,FiveMinuteData> ();
		boolean aggregatelanes;

		
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
					
		// read input parameters
		vdsfile = args[0];
		
		datafolder = args[1];
		district = Integer.parseInt(args[2]);
		year = Integer.parseInt(args[3]);
		month = Integer.parseInt(args[4]);
		day = Integer.parseInt(args[5]);

		//inURL = args[1];
		informat = args[6];
		outfolder = args[7];
		aggregatelanes = Boolean.parseBoolean(args[8]);
		
		try {
			
			if(informat.equalsIgnoreCase("caltransdbx")){
				daystring = String.format("%d",year) + "_" + String.format("%02d",month) + "_" + String.format("%02d",day);
				data_file_name = datafolder + File.separator + daystring + ".txt";
			}
			else if(informat.equalsIgnoreCase("pems5min")){
				daystring = String.format("%d",year) + "_" + String.format("%02d",month) + "_" + String.format("%02d",day);
				data_file_name = datafolder + File.separator + "d" + String.format("%02d",district) +"_text_station_5min_" + daystring + ".txt";
			}
			else if(informat.equalsIgnoreCase("pemshourly")){
				daystring = String.format("%d",year) + "_" + String.format("%02d",month);
				data_file_name = datafolder + File.separator + "d" + String.format("%02d",district) +"_text_station_hour_" + daystring + ".txt";
			}
			else{
				System.out.println("ERROR");
				return;
			}
			
			// read detector stations from file
			ReadVDSFile(vdsfile,vds);

			// initialize data map
			for(i=0;i<vds.size();i++)
				data.put(vds.get(i), new FiveMinuteData(vds.get(i),aggregatelanes));
			
			AbstractDataIO dataIO;
			if(informat.equalsIgnoreCase("caltransdbx"))
				dataIO = new DataIO_CaltransDBX();
			else if(informat.equalsIgnoreCase("pemsraw"))
				dataIO = new DataIO_30sec();
			else if(informat.equalsIgnoreCase("pems5min"))
				dataIO = new DataIO_5min();
			else if(informat.equalsIgnoreCase("pemshourly"))
				dataIO = new DataIO_1hour();
			else{
				System.out.println("ERROR");
				return;
			}

			dataIO.read(data_file_name,vds,data,aggregatelanes);

			// Write to text file
			dataIO.write(data,aggregatelanes,outfolder,daystring);
			
			System.out.println("Wrote to " + outfolder);
			
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
