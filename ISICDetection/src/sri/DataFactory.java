package sri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;

/*
 * This class provides the access to data
 */


public class DataFactory {

    /**
     * Connect to the test.db database
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        // String url = "jdbc:sqlite:C://sqlite/db/test.db";
    	
    	Connection conn = null;
        try {
            conn = DriverManager.getConnection(Sam.url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    
    /**
     * select all rows frolm CIIU table 
     */
    public CIIU[]  getCiiu(){
        String sql = "SELECT id, cod FROM ciiu";
        String sqlcount = "SELECT count(*) cnt FROM ciiu";
        CIIU[] aCIIU = null ;
        try 
        {
        	Connection conn = this.connect();
        	Statement stmtcount  = conn.createStatement();
        	ResultSet rscount    = stmtcount.executeQuery(sqlcount) ;
            rscount.next(); 
            int rowcount = rscount.getInt("cnt");              
            
        	Statement stmt  = conn.createStatement();            
            ResultSet rs    = stmt.executeQuery(sql) ;
            aCIIU = new CIIU[rowcount] ;             
            // loop through the result set
            int i = 0; 
            // loop through the result set
            while (rs.next()) {
            	aCIIU[i] = new CIIU( ); 
                aCIIU[i].id = rs.getInt("id"); 
                aCIIU[i].cod = rs.getString("cod");
                 i++ ; 
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        aCIIU[0].id = 0 ;
        aCIIU[0].cod = "999" ;
        return aCIIU ; 
    }
    
    

    public Transaction[]  getAT(){
        String sql = "SELECT informante_id, informado_id ,sum(BASE_IMPONIBLE_NETA) value "+
        " FROM AT2012a WHERE BASE_IMPONIBLE_NETA > 0 GROUP BY informante_id, informado_id ";
        String sqlcount = "SELECT count(*) cnt FROM AT2012a  WHERE BASE_IMPONIBLE_NETA > 0";
 
        int i = 0;  int rowcount = 0;        
        Transaction[] aAT = null ;
        try 
        {
        	Connection conn = this.connect(); 
            Statement stmtcount  = conn.createStatement();
            ResultSet rscount    = stmtcount.executeQuery(sqlcount) ;
            rscount.next(); 
            rowcount = rscount.getInt("cnt");
            System.out.printf("getAT() \n");
            System.out.printf("Row count %d \n", rowcount); 
            
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql) ;                                 
            aAT = new Transaction[rowcount] ;             
            // loop through the result set
            while (rs.next() ) {
            	aAT[i] = new Transaction( );
            	aAT[i].id1 = rs.getInt("informante_id"); 
            	aAT[i].id2 = rs.getInt("informado_id");
            	aAT[i].value = rs.getInt("value" );
                i++; 
            }

        } catch (SQLException e) {
        	System.out.println(rowcount);
        	System.out.println(i);
            System.out.println(e.getMessage());
        }
        return aAT; 
    }
    

    /**
     * select all rows from CIIU table 
     */
    public Taxpayer[]  getTaxpayer(){
        String sql = "SELECT a.id,  b.id ciiu_id,  idactive, ciiu FROM Taxpayer a, ciiu b WHERE substr(a.ciiu, 1,3)==b.cod";
        String sqlcount = "SELECT count(*) cnt FROM Taxpayer a";

        Taxpayer[] aTaxpayer = null ;
        int i = 0; 
        try 
        {
        	Connection conn = this.connect(); 

            Statement stmtcount  = conn.createStatement();
            ResultSet rscount    = stmtcount.executeQuery(sqlcount) ;
            rscount.next(); 
            int rowcount = rscount.getInt("cnt");              

        	
            int nid, nciiu_id, nidactive; boolean bspecial; String sciiu ; 
        	Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql) ;
            aTaxpayer = new Taxpayer[rowcount] ;             
            // loop through the result set 
            // loop through the result set
            while (rs.next()) {            	
            	nid = rs.getInt("id"); 
                nciiu_id = rs.getInt("ciiu_id");
                nidactive = rs.getInt("idactive"); // sequential id of taxpayer that have at least one record on AT 
                bspecial = false; 
                sciiu  = rs.getString("ciiu");
                aTaxpayer[i] = new Taxpayer(nid, nciiu_id,  nidactive, bspecial, sciiu );
                i++ ; 
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return aTaxpayer ; 
    }


   // CREATE TABLE movisic( ciiuold char(7), ciiunew char(7), cnt integer);
    public void saveMovisic() {

         String delete = "Delete from movisic";
         String insert = "insert into movisic (ciiuold, ciiunew, cnt) values (?,?,?)";
         
         Connection conn = this.connect();
      	 int i = 0;   		 
      	 int tot = 0 ; 

         try {
      	      PreparedStatement stmdelete = conn.prepareStatement(delete);
      	      stmdelete.execute() ; 
      	         	 
      	    PreparedStatement stminsert = conn.prepareStatement(insert);
  		 ArrayList< TabIntegerStringString > lstgrp =  Sam.getMovISIC();
         for (TabIntegerStringString o:lstgrp  ) {
         	 stminsert.setString(1, o.s1 );
         	 stminsert.setString(2, o.s2 );
         	 stminsert.setInt(3, o.id);
         	 stminsert.addBatch();
      	     i++;

      	     tot = tot + o.id; 
      	     if (i % 1000 == 0 || i == lstgrp.size()) {
      	    	stminsert.executeBatch(); // Execute every 1000 items.
      	     }
      	}          	        	

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
        System.out.printf("saveMovisic() totcnt % d tot reg %d \n", tot, i ) ;   
          
    }

    // CREATE TABLE taxpayer( id integer, .., ciiunew char(7) );
    public void UpdatetNewCiiu() {

    	System.out.println("UpdatetNewCiiu( )" );
        String update = "Update Taxpayer set  ciiunew = ? where id = ?";
        
        Connection conn = this.connect();
         try {
     	     PreparedStatement preparedStmt = conn.prepareStatement(update);
     	        		 		 
        	 
        for ( int i=0; i < Sam.TP.length; i++ ) {
     	      //PreparedStatement preparedStmt = conn.prepareStatement(update);        	        	
 
        	if (Sam.TP[i].active ) {
       	      preparedStmt.setString(1, Sam.TP[i].ciiu );
   	          preparedStmt.setInt   (2, Sam.TP[i].id );
   	          preparedStmt.addBatch();        		
        	}   	          
   	           // execute the java preparedstatement
   	           // preparedStmt.executeUpdate();
        	   	           
   	         // stmupdate.setString(1, Sam.TP[i].ciiu );
     	     if ( i % 1000 == 0 || i == Sam.TP.length ) {
     	    	preparedStmt.executeBatch(); // Execute every 1000 items.
	        	System.out.printf(" UpdatetNewCiiu() Number of updates %d  \n ", i ) ; 
     	     }
   	            
     	     
     	}          	        	

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    }

    
    // CREATE TABLE outliers( id integer );
    public void saveOutliers( ) {
        System.out.println("saveOutliers()" );    	
    	
        String delete = "Delete from outliers";
        String insert = "insert into outliers (id) values (?)";
        
        Connection conn = this.connect();
         try {
     	      PreparedStatement stmdelete = conn.prepareStatement(delete);
     	      stmdelete.execute() ; 
     	         	 
     	    PreparedStatement stminsert = conn.prepareStatement(insert);
      	     int i = 0;
  		 		 
             for (Integer x: Sam.oAlgo.getOutliers()  ) {
        	     stminsert.setInt( 1,x );
        	     stminsert.addBatch();
     	         i++;

     	         if (i % 1000 == 0 || i == Sam.oAlgo.getOutliers().size()) {
     	    	     stminsert.executeBatch(); // Execute every 1000 items.
     	         }
     	      }          	        	

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    }

    
    
    // CREATE TABLE movtaxpayers( id integer, ciiuold char(7), ciiunew char(7));
    public void saveMovTaxpayers( ) {
    	
        System.out.println("saveMovTaxpayers( )" );
        String delete = "Delete from movtaxpayers";
        String insert = "insert into movtaxpayers (id, ciiuold, ciiunew) values (?,?,?)";
        
        Connection conn = this.connect();
         try {
     	    PreparedStatement stmdelete = conn.prepareStatement(delete);
     	    stmdelete.execute() ;      	         	 
     	    PreparedStatement stminsert = conn.prepareStatement(insert);
 
 		 for (int i=0; i < Sam.TP.length; i++ ) {
 			String sCiiu = Sam.TP[i].ciiu_old ; 
 			if (Sam.TP[i].active && Sam.TP[i].ciiu.compareTo( sCiiu )!=0) {
 			 	  stminsert.setInt( 1, Sam.TP[i].id );
 		          stminsert.setString( 2, sCiiu );
 		          stminsert.setString( 3, Sam.TP[i].ciiu );
 		          stminsert.addBatch();

 			 }
	     	  if (i % 1000 == 0 || i - 1 == Sam.TP.length-1 ) {
	     	    	stminsert.executeBatch(); // Execute every 1000 items.
	     	 } 				

		 }
     	 	      
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    }

    
// CREATE TABLE isicprofile( isic char(7), type char(1), p1 numeric(10,2), ...  p66 numeric(10,2) )    
    public void saveIsicprofile( ) {

    	System.out.println("saveIsicprofile( )" );
        String delete = "Delete from isicprofile";
        String insert = "insert into isicprofile (isic, type ";
        for (int i=0; i < 66; i++ ) {
        	insert = insert + ", p" + Integer.toString(i+1) ; 
        }        
        insert = insert +  ") values (?,?" ; 
        for (int i=0; i < 66; i++ ) {
        	insert = insert + ",?"  ; 
        }        
        insert = insert +  ")" ; 
        
        Connection conn = this.connect();
         try {
     	    PreparedStatement stmdelete = conn.prepareStatement(delete);
     	    stmdelete.execute() ;      	         	 
     	    PreparedStatement stminsert = conn.prepareStatement(insert);
     	    
     	    int k=0; 
    		for (Entry<String, ClusterCiiuMaster > entry : Sam.dicCiiu.entrySet()) {
    	         ClusterCiiuMaster o = entry.getValue() ; 
    			 boolean hasSales = false ; 
    			 for (int j=0; j < Sam.nCiiu ; j++)
    				 if (o.Sales.getmean(j)  > 0) {
    					 hasSales = true ;
    					 break; 
    				 }
    	                  			    
    			   if (hasSales) {
    				   stminsert.setString( 1, o.ciiu );
    				   stminsert.setString( 2, "S" );
    				       				   
    				   for (int j=0; j < Sam.nCiiu ; j++) {
    					   stminsert.setDouble( j+3, o.Sales.getmean(j) );
    				   }	 			      
    			   }
  		          stminsert.addBatch();
                  k++;
 		     	  if (k % 10 == 0 || k == Sam.dicCiiu.size() ) {
 		     	    	stminsert.executeBatch(); // Execute every 1000 items.
 		     	  } 				

    		}     	    
     	    

     	    k=0; 
    		for (Entry<String, ClusterCiiuMaster > entry : Sam.dicCiiu.entrySet()) {
    	         ClusterCiiuMaster o = entry.getValue() ; 
    			 boolean hasPurchases = false ; 
    			 for (int j=0; j < Sam.nCiiu ; j++)
    				 if (o.Purchases.getmean(j)  > 0) {
    					 hasPurchases = true ;
    					 break; 
    				 }
    	                  			    
    			   if (hasPurchases) {
    				   stminsert.setString( 1, o.ciiu );
    				   stminsert.setString( 2, "P" );
    				       				   
    				   for (int j=0; j < Sam.nCiiu ; j++) {
    					   stminsert.setDouble( j+3, o.Purchases.getmean(j) );
    				   }	 			      
    			   }
  		          stminsert.addBatch();
                  k++;
 		     	  if (k % 10 == 0 || k == Sam.dicCiiu.size() ) {
 		     	    	stminsert.executeBatch(); // Execute every 1000 items.
 		     	  } 				
    		}     	    
    		
    		
     } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
         
    }
    
}


