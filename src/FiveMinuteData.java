
import java.util.ArrayList;

public class FiveMinuteData {

	public boolean isaggregate;	// true if object holds only averages over all lanes
	public int vds;
	public ArrayList<Long> time = new ArrayList<Long>();
	private ArrayList<ArrayList<Float>> flw = new ArrayList<ArrayList<Float>>();
	private ArrayList<ArrayList<Float>> occ = new ArrayList<ArrayList<Float>>();
	private ArrayList<ArrayList<Float>> spd = new ArrayList<ArrayList<Float>>();
	
	public FiveMinuteData(int vds,boolean isaggregate) {
		this.vds=vds;
		this.isaggregate = isaggregate;
	}
	
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
	
	public String getTimeString(int i){
		return String.format("%d",time.get(i));
	}
	
	public String getFlwString(int i){
		if(isaggregate){
			return String.format("%f", flw.get(0).get(i));
		}
		else{
			return tabformat(flw.get(i));
		}
	}

	public String getOccString(int i){
		if(isaggregate){
			return String.format("%f", occ.get(0).get(i));
		}
		else{
			return tabformat(occ.get(i));
		}
	}

	public String getSpdString(int i){
		if(isaggregate){
			return String.format("%f", spd.get(0).get(i));
		}
		else{
			return tabformat(spd.get(i));
		}
	}
	
	// methods for aggregated data ............................
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

	// methods for per lane data .......................................
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
	
	private static String tabformat(ArrayList<Float> V){
		String out = "";
		for(int i=0;i<V.size();i++)
			out = out + String.format("%f", V.get(i))+ "\t";
		return out;
	}
	
}
