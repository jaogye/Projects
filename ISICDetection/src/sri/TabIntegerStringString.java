package sri;

public class TabIntegerStringString implements Comparable<TabIntegerStringString > {

    int id;	
	String s1;
    String s2;


    public TabIntegerStringString (int id, String s1, String s2) {
       this.id = id;
       this.s1= s1;
       this.s2= s2;
    }

	@Override
	public int compareTo(TabIntegerStringString o) {
		// TODO Auto-generated method stub
         if  (this.s1.compareTo(o.s1)!=0)
        	 return this.s1.compareTo(o.s1) ;
         else
        	 return this.s2.compareTo(o.s2) ;

	}
	
}