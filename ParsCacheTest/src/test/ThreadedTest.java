package test;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.CacheManager;
import db.DataSource;


public class ThreadedTest implements Runnable {
	int id = -1;
	ParsCacheService svc;
	static Logger log = LoggerFactory.getLogger(ThreadedTest.class);

	static  final int minDuration = 300; // Minimum sleep duration in milliseconds
	static  final int maxDuration = 1300; // Maximum sleep duration in milliseconds
	
	static int THREADS = 500;
	static int ERRORS = 0;
	static int MAX_CACHE_SIZE = 20;
	
	static CountDownLatch latch = new CountDownLatch(THREADS);
	
	public ThreadedTest(int id, ParsCacheService svc) {
		this.id = id;
		this.svc = svc;
	}
	
	public static void main(String[] args) {
		try {
			DataSource.getConnection();
			CacheManager.clearDBCache();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		ParsCacheService svc = new ParsCacheService(new ParsCache(1000));
		
		
		long start = System.currentTimeMillis();
		log.info("------------THREAD_TEST STARTED WITH "+THREADS+" threads.");
		for ( int i =0 ; i< THREADS ; i++) {
			new Thread(new ThreadedTest(i,svc)).start();;
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int cacheSize = CacheManager.getDbCacheSize();
		if ( cacheSize!= Math.min( THREADS , MAX_CACHE_SIZE) ) {
			log.error("Cache size is invalid "+cacheSize+" should be "+Math.min( THREADS , MAX_CACHE_SIZE));
			ERRORS++;
		}
		else {
			log.info("Final cache size :"+cacheSize);
			svc.dumpCache();
		}
		long end = System.currentTimeMillis();
		log.info("------------THREAD_TEST FINISHED WITH "+THREADS+" threads and "+ERRORS+" errors in ms:"+(end-start));
		//svc.dumpCache();
	}

	@Override
	public void run() {
			Random random = new Random();
			//CacheManager.set("campaign", "thread:"+id);
			for ( int i=0;i<THREADS;i++) {
				int randomDuration = minDuration + random.nextInt(maxDuration - minDuration + 1);

				try {
					//System.out.println("Thread:"+id+ " sleeping in ms... "+randomDuration);
					Thread.sleep(randomDuration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String key = "campaign"+id%MAX_CACHE_SIZE;
				String value = "thread:"+randomDuration%3;
				svc.put( key , value );
	
				//String cached = CacheManager.get("campaign");
				String cached = svc.get(key);
				
				if ( cached == null ) {
					log.info("Cached null key:"+key);
					ERRORS++;
				}
				
				if ( svc.getCache().size() > MAX_CACHE_SIZE ) {
					log.info("Memory Cache oversize "+svc.getCache().size());
					ERRORS++;
				}
//				else if ( !cached.equals(value) ) System.out.println("Cached not equal +"+cached+" value:"+value);
				
				//System.out.println(value+"->"+cached);
			}
			latch.countDown();
			//log.info("Thread "+id+" finished.");
	}

}
