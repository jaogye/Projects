package sri;

import java.util.Comparator;     

public class OutlierCandidate implements Comparable<OutlierCandidate> {     
  private int id;     
  private double value;       
  private String str;
  
  public OutlierCandidate(int id,  double value) {         
    this.id = id;                  
    this.value = value;
    this.str = "" ;     
  }       

  public OutlierCandidate(int id, String s,  double value) {         
	    this.id = id;                  
	    this.value = value;
	    this.str = s  ;     
	  }       
  
  public int getId() {         
    return id;     
  }       

  public double getValue() {         
    return value;     
  }       

  public String getString() {         
	    return str;     
	  }       

  @Override     
  public int compareTo(OutlierCandidate candidate) {          
    return (this.getValue() > candidate.getValue() ? 1 : 
            (this.getValue() == candidate.getValue() ? 0 : -1));     
  }       

  @Override     
  public String toString() {         
    return " Id: " + this.id + " String:" + str + ", Value:" + this.value+"\n";     
  } 
}
