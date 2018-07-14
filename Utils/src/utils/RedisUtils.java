package utils;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisUtils {

	public void jiqun(int start, int end) {

		String host1;
		String host2;
		String host3;

		try {
			System.out.println(Thread.currentThread().getName() + "startAT : " + start);

			Set<HostAndPort> hostAndPortSet = new HashSet<HostAndPort>();
			hostAndPortSet.add(new HostAndPort(host1, 2881));
			hostAndPortSet.add(new HostAndPort(host1, 2882));
			hostAndPortSet.add(new HostAndPort(host1, 2883));
			hostAndPortSet.add(new HostAndPort(host1, 2884));
			hostAndPortSet.add(new HostAndPort(host2, 2881));
			hostAndPortSet.add(new HostAndPort(host2, 2882));
			hostAndPortSet.add(new HostAndPort(host2, 2883));
			hostAndPortSet.add(new HostAndPort(host2, 2884));
			hostAndPortSet.add(new HostAndPort(host3, 2881));
			hostAndPortSet.add(new HostAndPort(host3, 2882));
			hostAndPortSet.add(new HostAndPort(host3, 2883));
			hostAndPortSet.add(new HostAndPort(host3, 2884));
			JedisCluster jedisCluster = new JedisCluster(hostAndPortSet, 1000000);

			for (int i = start; i < end; i++) {
				jedisCluster.set("USER_EVENT_" + i, ""); // 与Redis命令行操作基本一致
			}
			jedisCluster.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
