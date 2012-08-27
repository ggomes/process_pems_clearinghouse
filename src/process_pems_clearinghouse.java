import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

public class process_pems_clearinghouse {

	public static void main(String[] args) {
	
		try {

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
			String vdsfile = args[0];
			String datafolder = args[1];
			int district = Integer.parseInt(args[2]);
			int year = Integer.parseInt(args[3]);
			int month = Integer.parseInt(args[4]);
			int day = Integer.parseInt(args[5]);
			String informat = args[6];
			String outfolder = args[7];
			boolean aggregatelanes = Boolean.parseBoolean(args[8]);
			
			// read detector stations from file
			Vector<Integer> vds = new Vector<Integer>();
			ReadVDSFile(vdsfile,vds);

			// initialize data map
			HashMap <Integer,TrafficData> data = new HashMap <Integer,TrafficData> ();
			for(int i=0;i<vds.size();i++)
				data.put(vds.get(i), new TrafficData(vds.get(i),aggregatelanes));
			
			// Construct read/writer
			BaseDataIO dataIO;
			if(informat.equalsIgnoreCase("caltransdbx"))
				dataIO = new DataIO_CaltransDBX(datafolder,outfolder,district,day,month,year);
			else if(informat.equalsIgnoreCase("pems30sec"))
				dataIO = new DataIO_30sec(datafolder,outfolder,district,day,month,year);
			else if(informat.equalsIgnoreCase("pems5min"))
				dataIO = new DataIO_5min(datafolder,outfolder,district,day,month,year);
			else if(informat.equalsIgnoreCase("pemshourly"))
				dataIO = new DataIO_1hour(datafolder,outfolder,district,day,month,year);
			else{
				System.out.println("ERROR");
				return;
			}

			// Read data file
			dataIO.read(vds,data,aggregatelanes);

			// Write to text file
			dataIO.write(data,aggregatelanes);
			
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
