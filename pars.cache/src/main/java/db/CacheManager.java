package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CacheManager {
	static Logger log = LoggerFactory.getLogger(CacheManager.class);
	
	public static String get(String key) {
		return get(key,false);
	}
	
	private static String get(String key, boolean noExpire) {
	    String SQL_QUERY = "select value, expire from parscache p where p.key = '"+key+"'";
	    if ( !noExpire) SQL_QUERY = SQL_QUERY + " and expire > "+System.currentTimeMillis();
	    log.debug(SQL_QUERY);
	    return getString(SQL_QUERY);
	   }

	public static int getDbCacheSize() {
		String s = getString("select count(*) from parscache");
		log.info("Cache size:"+s);
		return Integer.parseInt(s);
	}
	
	public static void clearDBCache() {
		dataChange("truncate table parscache");
		log.debug("Cache cleared");
	}
	
	private static String getString(String SQL_QUERY) {
	    log.debug(SQL_QUERY);
	    String ret = null;
	    PreparedStatement pst =   null;
	    Connection con = null;
	    try {
	    	 con = DataSource.getConnection();
	    
	       pst = con.prepareStatement( SQL_QUERY );
	        ResultSet rs = pst.executeQuery();
	            if ( rs.next() ) ret = rs.getString(1);
	           
	        } catch(Exception e ) {e.printStackTrace();}
	    	finally {
				try {
		    		if ( pst != null && !pst.isClosed() ) pst.close();
					if ( con != null && ! con.isClosed() )  con.close();
				} catch(Exception e ) {e.printStackTrace();}
			}
	    return ret;
	}
	
	public static void set(String key, String value,long expire) {
		String curr = get(key,true);
		String sql = null;
		if ( curr == null ) {
			sql = "insert into parscache  values('"+key+"','"+value+"',"+expire+") ON DUPLICATE KEY UPDATE parscache.key='"+key+"' , value='"+value+"' , expire =" +(System.currentTimeMillis()+1000);
		}
		else {
			sql = "update parscache p set value = '"+value+"' , expire = "+expire+" where p.key = '"+key+"'";
		}
		
		log.debug(sql);
		dataChange(sql);
	}
	
	private static void dataChange(String sql) {
		log.debug(sql);
	    PreparedStatement pst =   null;
	    Connection con = null;
	    try {
	    	 con = DataSource.getConnection();
	    
	       pst = con.prepareStatement( sql );
	       pst.execute(sql);
        } catch(Exception e ) {System.err.println("set() sqlErr:"+e.getMessage()+" sql:"+sql);}
    	finally {
			try {
	    		if ( pst != null && !pst.isClosed() ) pst.close();
				if ( con != null && ! con.isClosed() )  con.close();
			} catch(Exception e ) {e.printStackTrace();}
		}
	}

	
	public static void main(String[] args) {
		CacheManager.set("deneme1", "fener1",System.currentTimeMillis()+3000);
		System.out.println( CacheManager.get("deneme1"));
	}

}
