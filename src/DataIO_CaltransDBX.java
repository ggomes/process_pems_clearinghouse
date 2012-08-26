
public class DataIO_CaltransDBX extends AbstractDataIO {
	
	public DataIO_CaltransDBX() {
		outprefix = "dbx";
		delimiter = "\t";
		laneblocksize = 6;
		flwoffset = 20;
		occoffset = 22;
		spdoffset = 23;
		maxlanes = 8;
		hasheader = true;
	}

}
