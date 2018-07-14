package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.Vector;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ExecuteSql {

	public static final String url = "jdbc:mysql://192.168.5.124/test";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "huawei@123";

	// 适合批量插入的操作
	public static void batchAdd() {

		// sql前缀
		String prefix = "INSERT INTO t_app VALUES ";
		// 保存sql后缀
		StringBuffer suffix = new StringBuffer();

		try {

			Class.forName(name);
			Connection conn = DriverManager.getConnection(url, user, password);

			// 设置事务为非自动提交
			conn.setAutoCommit(false);
			// 比起st，pst会更好些
			PreparedStatement pst = (PreparedStatement) conn.prepareStatement("");// 准备执行语句

			// 根据需要设置循环次数
			for (int i = 1; i <= 1; i++) {

				// 批量组装Sql，此处应使用循环实现批量快速插入数据
				suffix.append("('value1', 'value2', 'value3', 'value4', 'value5'),");

				// 构建完整SQL
				String sql = prefix + suffix.substring(0, suffix.length() - 1);
				// 添加执行SQL
				pst.addBatch(sql);
				// 执行操作
				pst.executeBatch();
				// 提交事务
				conn.commit();
				// 清空上一次添加的数据
				suffix = new StringBuffer();
			}
			// 头等连接
			pst.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(suffix.toString());
			e.printStackTrace();
		}

	}

	public static JSONArray executeQuery(String sql) {

		ResultSet rs = null;
		Connection conn = null;
		JSONArray rs4JsonArray = new JSONArray();

		try {
			// mysql驱动程序，获取connection对象
			Class.forName(name);
			conn = DriverManager.getConnection(url, user, password);

			// 连接成功，执行操作
			if (!conn.isClosed()) {
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData resultSetMD = rs.getMetaData();

				Vector<Object> colNameVec = new Vector<Object>();
				for (int i = 1; i <= resultSetMD.getColumnCount(); i++) {
					colNameVec.add(resultSetMD.getColumnName(i));
				}
				while (rs.next()) {
					JSONObject recordJsonObj = new JSONObject();
					for (int j = 0; j < colNameVec.size(); j++) {
						String colValue = rs.getObject((String) colNameVec.get(j)) == null ? null
								: rs.getString((String) colNameVec.get(j));
						recordJsonObj.put(colNameVec.get(j).toString(), colValue);
					}
					rs4JsonArray.add(recordJsonObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return rs4JsonArray;
	}

	public static int executeUpdate(String sql) throws Exception {

		int count = 0;
		Connection conn = null;

		try {
			// mysql驱动程序，获取connection对象
			Class.forName(name);
			conn = DriverManager.getConnection(url, user, password);

			// 连接成功，执行操作
			if (!conn.isClosed()) {
				try {
					Statement stmt = conn.createStatement();
					count = stmt.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
					count = 0;
				} finally {
					conn.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return count;
	}
	
	public static void main(String[] args) {

		System.out.println(executeQuery("SELECT * FROM fff").toString());
	}
}
