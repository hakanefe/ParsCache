package test;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// PAKET TARIFE KAMPANYA GIBI DEGISIKLIGI AZ VE KULLANICI BAZLI OLMAYAN NESNELER ICIN KULLANILMALIDIR
// KULLANICI (MSISDN) BAZLI CACHE ICIN DB BAZLI CACHEITEM KULLANILMALIDIR

public class ParsCache extends LinkedHashMap<String,ParsCacheItem> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private int maxSize;
    public ParsCache(int maxSize) {
        super(maxSize + 1, 1.0f, true);
        this.maxSize = maxSize;
    }

    @Override
    public ParsCacheItem get(Object key) {
        boolean unlocked = false;
            readLock.lock();
            try {
                ParsCacheItem item = super.get(key);
                if ( item == null ) return null;
//                if ( item.expireTimeInMills <= System.currentTimeMillis() ) {
//                    readLock.unlock(); //deadlock olmamasi icin
//                    super.remove(key);
//                    unlocked = true;
//                    return null;
//                }
               return item;
            } finally {
                if (!unlocked) readLock.unlock();
            }
        }

    @Override
    public boolean remove(Object key, Object value) {
        writeLock.lock();
        try {
            return super.remove(key, value);
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public ParsCacheItem put(String key, ParsCacheItem value) {
        writeLock.lock();
        try {
            return super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public ParsCacheItem put(String key, ParsCacheItem value , int expireMinutes) {
        writeLock.lock();
        try {

            return super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > maxSize;
    }
}
