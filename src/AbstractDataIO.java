import java.io.IOException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public abstract class AbstractDataIO {

	public String name;
	public String outprefix;
	public String delimiter;
	public int laneblocksize;
	public int flwoffset;
	public int occoffset;
	public int spdoffset;
	public int maxlanes;
	public boolean hasheader;
		
	public abstract void read(URL url,Vector<Integer> vds,HashMap <Integer,FiveMinuteData> data,boolean aggregatelanes) throws NumberFormatException, IOException;
	
	public abstract void write(HashMap <Integer,FiveMinuteData> data,boolean aggregatelanes,String outfolder,String daystring) throws Exception;

    public static Date ConvertTime(final String timestr) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        ParsePosition pp = new ParsePosition(0);
        return formatter.parse(timestr,pp);
    }

//    public class FileFormat {
//    	public String outputprefix;
//    	public String name;
//    	public int laneblocksize;
//    	public int flwoffset;
//    	public int occoffset;
//    	public int spdoffset;
//    	public int maxlanes;
//    	public boolean hasheader;
//    	public String delimiter;
//
//    	public FileFormat(String name,String outprefix,String delimiter,int laneblocksize, int flwoffset,int occoffset,int spdoffset, int maxlanes,boolean hasheader) {
//    		super();
//    		this.name = name;
//    		this.outputprefix = outprefix;
//    		this.delimiter = delimiter;	
//    		this.laneblocksize = laneblocksize;
//    		this.flwoffset = flwoffset;
//    		this.occoffset = occoffset;
//    		this.spdoffset = spdoffset;
//    		this.maxlanes = maxlanes;
//    		this.hasheader = hasheader;
//    	}
//    }
    
}
