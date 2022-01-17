package sri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
	
	public static  List<Integer> getUnion(List<Integer> list1, List<Integer> list2) {
		
		Set<Integer> set = new HashSet<>();
		set.addAll(list1);
		set.addAll(list2);
		
		return new ArrayList<>(set);
	}

	//A recursive binary search function. It returns
	//location of x in given array arr[l..r] is present,
	//otherwise -1
	public static int binarySearchCIIU(CIIU  arr[], int l, int r, String x)
	{
	 if (r >= l) {
	     int mid = l + (r - l) / 2;

	     // If the element is present at the middle
	     // itself
	     if (arr[mid].cod.compareTo(x) == 0)
	         return mid;

	     // If element is smaller than mid, then
	     // it can only be present in left subarray
	     if (arr[mid].cod.compareTo(x) > 0)
	         return binarySearchCIIU(arr, l, mid - 1, x);

	     // Else the element can only be present
	     // in right subarray
	     return binarySearchCIIU(arr, mid + 1, r, x);
	 }

	 // We reach here when element is not
	 // present in array
	 return -1;
	}
  
	public static void main(String[] args){
		ArrayList<OutlierCandidate> lst = new ArrayList<>() ; 
		lst.add(new OutlierCandidate(1, 4.6));
		lst.add(new OutlierCandidate(13, 4.5));
		lst.add(new OutlierCandidate(11, 0.6));
		lst.add(new OutlierCandidate(21, 7.6));
		lst.add(new OutlierCandidate(24, 4.1));
		lst.add(new OutlierCandidate(31, 8.6));
		lst.add(new OutlierCandidate(100, 5.2));
		
		 Collections.sort(lst) ;
		 System.out.println(lst);
		 
		 int K = 2 ; 
		 for (int i=0; i < 20; i++) {
			 System.out.printf("%d \n", (int) Math.floor( K* Math.random() ) ) ; 
		 }
		 
	}
	
}




