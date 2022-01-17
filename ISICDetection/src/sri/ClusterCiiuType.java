package sri;

import java.util.List;

public interface ClusterCiiuType {

	public void addTaxpayer(int x) ; 
    public double threshold() ; 
    public int numEntries() ; 
    public int numTaxpayers() ; 
    public boolean nullIntersection( int x)  ;
    public List<Integer> getTaxpayers() ; 
    public List<Integer> getOutliers() ; 
    public void UpdateStats() ; 
    public double getmean(int i) ; 
    public double getDeltaScore(int nold, int nnew, int x, double fraction) ; 
    public double getScore(int x) ; 
 
 
}
