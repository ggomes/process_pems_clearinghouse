import java.io.File;


public class DataIO_CaltransDBX extends BaseDataIO {
	
	public DataIO_CaltransDBX(String datafolder,String outfolder,int district,int day,int month,int year) {
		String daystring = String.format("%d",year) + "_" + String.format("%02d",month) + "_" + String.format("%02d",day);
		data_file_name = datafolder + File.separator + daystring + ".txt";
		outprefix = outfolder + File.separator + "dbx" + "_" + daystring + "_";;
		delimiter = "\t";
		laneblocksize = 6;
		flwoffset = 20;
		occoffset = 22;
		spdoffset = 23;
		maxlanes = 8;
		hasheader = true;
		flow_mult = Float.NaN;	// WHAT SHOULD THIS BE?
	}

}
