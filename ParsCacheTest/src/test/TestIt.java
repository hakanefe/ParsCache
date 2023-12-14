package test;

import java.util.Random;

import db.CacheManager;
import db.DataSource;

public class TestIt {

	public static void main(String[] args) {
		DataSource.poolSize = 1;
		ParsCache cache = new ParsCache(1000);
		Random random = new Random();
		for (int i = 0; i < 1; i++) {
			ParsCacheService svc = new ParsCacheService(cache);
			System.out.println("-------------PUT 1-------------");
			String value1 = Integer.toString(random.nextInt(1000));
			svc.put("denemex", "put1");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("from memory cache:" + svc.get("denemex"));
			System.out.println("from db:" + CacheManager.get("denemex"));

			System.out.println("-------------PUT 2-------------");
			String value2 = Integer.toString(random.nextInt(1000));

			svc.put("denemex", "put2");
			System.out.println("from memory cache2:" + svc.get("denemex"));
			System.out.println("from db2:" + CacheManager.get("denemex"));
			System.out.println("-------------PUT 3-------------");
			svc.put("denemex", "put3");
			System.out.println("from memory cache:" + svc.get("denemex"));
			System.out.println("from db:" + CacheManager.get("denemex"));
			if (value2.equals(value1))
				System.err.println("values are  same value2:" + value2 + " value1:" + value1);

			if (!value2.equals(svc.get("denemex")))
				System.err.println("values are not same value2:" + value2 + " memory:" + svc.get("denemex"));

			if (!value2.equals(svc.get("denemex")))
				System.err.println("values are not same value2:" + value2 + " DB:" + CacheManager.get("denemex"));
		}

	}

}
