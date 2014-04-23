
import java.util.ArrayList;
import java.util.Locale;

public class TrafficData {

	public boolean isaggregate;	// true if object holds only averages over all lanes
	public int vds;
	public ArrayList<Long> time = new ArrayList<Long>();
	public ArrayList<ArrayList<Float>> flw = new ArrayList<ArrayList<Float>>();
	public ArrayList<ArrayList<Float>> occ = new ArrayList<ArrayList<Float>>();
	public ArrayList<ArrayList<Float>> spd = new ArrayList<ArrayList<Float>>();
	
	public TrafficData(int vds,boolean isaggregate) {
		this.vds=vds;
		this.isaggregate = isaggregate;
	}
	
	// number of time steps ....................................
	public int numflw(){
		if(flw.isEmpty())
			return 0;
		if(isaggregate)	
			return flw.get(0).size();
		else
			return flw.size();
	}
	
	public int numocc(){
		if(occ.isEmpty())
			return 0;
		if(isaggregate)	
			return occ.get(0).size();
		else
			return occ.size();
	}
	
	public int numspd(){
		if(spd.isEmpty())
			return 0;
		if(isaggregate)	
			return spd.get(0).size();
		else
			return spd.size();
	}
	
	// number of lanes for time index ..........................
	public int num_flw_lanes(int i){
		try {
			if(flw.isEmpty())
				return 0;
			if(isaggregate)	
				return 1;
			else
				return flw.get(i).size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int num_occ_lanes(int i){
		try {
			if(occ.isEmpty())
				return 0;
			if(isaggregate)	
				return 1;
			else
				return occ.get(i).size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int num_spd_lanes(int i){
		try {
			if(spd.isEmpty())
				return 0;
			if(isaggregate)	
				return 1;
			else
				return spd.get(i).size();
		} catch (Exception e) {
			return 0;
		}
	}

	// convert to string .......................................
	public String getTimeString(int i){
		return String.format(Locale.US, "%d",time.get(i));
	}
	
	public String getFlwString(int i,int maxlanes){
		if(isaggregate){
			return String.format(Locale.US, "%f", flw.get(0).get(i));
		}
		else{
			return tabformat(flw.get(i),maxlanes);
		}
	}

	public String getOccString(int i,int maxlanes){
		if(isaggregate){
			return String.format(Locale.US, "%f", occ.get(0).get(i));
		}
		else{
			return tabformat(occ.get(i),maxlanes);
		}
	}

	public String getSpdString(int i,int maxlanes){
		if(isaggregate){
			return String.format(Locale.US, "%f", spd.get(0).get(i));
		}
		else{
			return tabformat(spd.get(i),maxlanes);
		}
	}
	
	private static String tabformat(ArrayList<Float> V,int strlength){
		String out = "";
		int i;
		for(i=0;i<strlength;i++){
			if(i>V.size()-1)
				out = out + "NaN\t";
			else
				out = out + String.format(Locale.US, "%f", V.get(i))+ "\t";
		}
		return out;
	}
	
	// append aggregated data ......................................
	public void addAggFlw(float val){
		if(flw.isEmpty())
			flw.add(new ArrayList<Float>());
		if(isaggregate)
			flw.get(0).add(val);
	}
	
	public void addAggOcc(float val){
		if(occ.isEmpty())
			occ.add(new ArrayList<Float>());
		if(isaggregate)
			occ.get(0).add(val);
	}
	
	public void addAggSpd(float val){
		if(spd.isEmpty())
			spd.add(new ArrayList<Float>());
		if(isaggregate)
			spd.get(0).add(val);
	}

	// append per lane data .......................................
	public void flwadd(ArrayList<Float> row,int start,int end){
		ArrayList<Float> x = new ArrayList<Float>();
		for(int i=start;i<end;i++)
			x.add(row.get(i));
		flw.add(x);
	}

	public void occadd(ArrayList<Float> row,int start,int end){
		ArrayList<Float> x = new ArrayList<Float>();
		for(int i=start;i<end;i++)
			x.add(row.get(i));
		occ.add(x);
	}

	public void spdadd(ArrayList<Float> row,int start,int end){
		ArrayList<Float> x = new ArrayList<Float>();
		for(int i=start;i<end;i++)
			x.add(row.get(i));
		spd.add(x);
	}

}
