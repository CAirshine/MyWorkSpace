package utils;

import java.util.Date;
import java.util.Iterator;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class ExecuteCql {

	// 适合批量插入的操作
	public static void batchAdd() {

		try {
			String STATEMENT = "insert into t_user_eventcard (sds_uid, app_id, request_id, card_info, sds_ctime, sds_mtime) values (?,?,?,?,?,?)";

			QueryOptions options = new QueryOptions();
			options.setConsistencyLevel(ConsistencyLevel.QUORUM);
			Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withCredentials("cassandra", "cassandra")
					.withQueryOptions(options).build();
			Session session = cluster.connect("KEYSPACE");
			BatchStatement bstmt = new BatchStatement();
			// 插入多条记录，把每个记录构造成一个PreparedStatement，放进BatchStatement
			PreparedStatement pstmt = session.prepare(STATEMENT);

			for (int i = 0; i < 10000; i++) {

				bstmt.add(pstmt.bind("", "", "", "", "", "", "", "", "", new Date(), new Date()));

				// 执行BatchStatement
				session.execute(bstmt);

				bstmt = new BatchStatement();
				pstmt = session.prepare(STATEMENT);
			}

			Thread.sleep(1000);
			session.close();
			cluster.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Demo By zhaoyan
	public static void main() {

		QueryOptions options = new QueryOptions();

		options.setConsistencyLevel(ConsistencyLevel.QUORUM);

		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withCredentials("cassandra", "cassandra")
				.withQueryOptions(options).build();

		Session session = cluster.connect();

		session.execute("CREATE  KEYSPACE kp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");

		// 针对keyspace的session，后面表名前面不用加keyspace
		Session kpSession = cluster.connect("kp");

		kpSession.execute("CREATE TABLE tbl(a INT,  b INT, c INT, PRIMARY KEY(a));");

		RegularStatement insert = QueryBuilder.insertInto("kp", "tbl").values(new String[] { "a", "b", "c" },
				new Object[] { 1, 2, 3 });
		kpSession.execute(insert);

		RegularStatement insert2 = QueryBuilder.insertInto("kp", "tbl").values(new String[] { "a", "b", "c" },
				new Object[] { 3, 2, 1 });
		kpSession.execute(insert2);

		RegularStatement delete = QueryBuilder.delete().from("kp", "tbl").where(QueryBuilder.eq("a", 1));
		kpSession.execute(delete);

		RegularStatement update = QueryBuilder.update("kp", "tbl").with(QueryBuilder.set("b", 6))
				.where(QueryBuilder.eq("a", 3));
		kpSession.execute(update);

		RegularStatement select = QueryBuilder.select().from("kp", "tbl").where(QueryBuilder.eq("a", 3));
		ResultSet rs = kpSession.execute(select);
		Iterator<Row> iterator = rs.iterator();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			System.out.println("a=" + row.getInt("a"));
			System.out.println("b=" + row.getInt("b"));
			System.out.println("c=" + row.getInt("c"));
		}
		kpSession.close();
		session.close();
		cluster.close();
	}
}
