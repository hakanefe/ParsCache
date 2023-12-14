package test;

import java.awt.DisplayMode;

public class ParsCacheItem {
    private String value=null;
    private long expireTimeInMills=0;
    private long persistExpireTimeInMills = 0;
    
    public ParsCacheItem(String value) {
        this.value = value;
    }

    public ParsCacheItem(String value, long expireTimeInMills) {
        this.value = value;
        this.expireTimeInMills = expireTimeInMills;
		//this.persistExpireTimeInMills = expireTimeInMills;

    }
    
    public ParsCacheItem(String value, long expireTimeInMills, long persistExpireTimeInMills) {
		super();
		this.value = value;
		this.expireTimeInMills = expireTimeInMills;
		this.persistExpireTimeInMills = persistExpireTimeInMills;
	}

	boolean readExpired() {
    	return System.currentTimeMillis()>=getExpireTimeInMills();
    }
    
    boolean persistExpired() {
    	return System.currentTimeMillis()>=getPersistExpireTimeInMills();
    }
    
    boolean valueEquals(String value) {
    	if ( value == null || this.value == null  ) return true;
    	else if ( this.value == null ) return false;
    	
    	return this.value.equals(value);
    }
    
    @Override
    public String toString() {
    	return "value:"+value+" read expire:"+readExpired()+" persistExpire:"+persistExpired()  ;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getExpireTimeInMills() {
		return expireTimeInMills;
	}

	public void setExpireTimeInMills(long expireTimeInMills) {
		this.expireTimeInMills = expireTimeInMills;
	}

	public long getPersistExpireTimeInMills() {
		return persistExpireTimeInMills;
	}

	public void setPersistExpireTimeInMills(long persistExpireTimeInMills) {
		this.persistExpireTimeInMills = persistExpireTimeInMills;
	}
    
    
}
