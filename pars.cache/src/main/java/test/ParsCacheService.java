package test;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.CacheManager;

public class ParsCacheService {
	static Logger log = LoggerFactory.getLogger(ParsCacheService.class);

	   	private static int DEFAULT_TIMEOUT_MINS = 1; // 1 mins
	    private static int DEFAULT_TIMEOUT_MILLS = DEFAULT_TIMEOUT_MINS*1000*2;
	    private static int PUT_TIMEOUT_MILLS = DEFAULT_TIMEOUT_MILLS ;
	    private ParsCache cache;


	    public ParsCacheService(ParsCache cache) {
			this.cache = cache;
		}
	 
	    public String get(String key) {
	        ParsCacheItem item = cache.get(key); //local cache'e bak
	        log.debug("item :"+item+" diff:"+(System.currentTimeMillis()-item.getExpireTimeInMills()));
	        if ( item != null && !item.readExpired() ) 
	        	return item.getValue(); //bulduysan bulduğun değeri dön
	        else if ( item.readExpired() ) {
	        		log.info("key expired "+key);
	        	}
	        String dbCachedItem = CacheManager.get(key); //bulamadıysan dbye bak
	        log.debug("dbCachedItem :"+dbCachedItem);

	        if ( dbCachedItem != null ) { //dbde varsa
	            //local cache'e yaz
//	            cache.put(key,new ParsCacheItem(dbCachedItem,System.currentTimeMillis()+DEFAULT_TIMEOUT_MILLS));
//	            return dbCachedItem; //dbdeki değeri dön
	        	put(key,dbCachedItem);
//	            return dbCachedItem; //dbdeki değeri dön

	        }

	        return null; //localde dbde yoksa null dön
	    }

	
	    public String put(String key, String value) {
	        return this.put(key , value, DEFAULT_TIMEOUT_MINS);

	    }

	    public void dumpCache() {
	    	System.out.println(Arrays.toString( cache.entrySet().toArray() ));
//	    	for ( String key: cache.keySet()) {
//	    		ParsCacheItem item = cache.get(key) ;
//	    		System.out.println(item.toString());
//	    	}
	    }
	    
	    
	    public String put(String key, String value, int expireMinutes) {

	        ParsCacheItem localCachedItem = cache.get(key); //localde var mı?
	        log.debug("localCachedItem:"+localCachedItem);
	        ParsCacheItem item = new ParsCacheItem(value,System.currentTimeMillis()+(expireMinutes*200)); //mills = mins * 6* * 1000

	        ParsCacheItem itemPut = cache.put(key,item);
	        
        	boolean itemNull = localCachedItem == null;//local cachede yoksa

	        //putTimeout = expireTime+PUT
	        boolean putTimout = !itemNull 
	        		&& localCachedItem.persistExpired();
//        	if (  putTimout) {
//            	log.info("Cache expired for PUT localCachedItem:"+ localCachedItem.toString()+" diff:"+(localCachedItem.expireTimeInMills-System.currentTimeMillis()));
//        	}

	        //if (saveDb ) log.info("localCachedItem null key:"+key+" value:"+value);
	        boolean valueChange = !itemNull && !localCachedItem.valueEquals(value); //ya da farklı değerse
	        boolean saveDb = itemNull	|| valueChange ||  putTimout;
	       // if (valueChange ) log.info("localCachedItem:"+localCachedItem.toString()+" key:"+key+" value:"+value);//local cachede yoksa


	        if ( saveDb) { 
	        	long expire = System.currentTimeMillis()+(expireMinutes*2000);
	        	CacheManager.set(key,value,expire);
	        	ParsCacheItem itemNew = new ParsCacheItem(value, expire,expire);
	        	cache.put(key, itemNew);
	        	log.debug("DB SAVE localCachedItemNull:"+itemNull+" puttimeout:"+putTimout+" valueChanged:"+valueChange+" key:"+key+" timediff:"+( localCachedItem==null?-999:(localCachedItem.getPersistExpireTimeInMills()-System.currentTimeMillis()) ) );
	        } 
	        else log.debug("DB NOSAVE localCachedItemNull:"+(localCachedItem==null)+" puttimeout:"+putTimout+" valueChanged:"+valueChange+" key:"+key+" timediff:"+( localCachedItem==null?-999:(localCachedItem.getExpireTimeInMills()-System.currentTimeMillis()) ) );

	        if ( itemPut == null ) return null;

	        return itemPut.getValue();
	    }

		public ParsCache getCache() {
			return cache;
		}

		public void setCache(ParsCache cache) {
			this.cache = cache;
		}
	    
	    
}
