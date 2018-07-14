package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class ConfigUtils {

	// 配置文件对象
	public static File configFile;
	public static Properties properties;

	// 静态代码块，初始化对象
	public static void init() {

		properties = new Properties();

		try {
			// 初始化文件对象，如果文件不存在则创建默认文件
			configFile = new File("D:\\BurpUtils\\config.properties");
			if (!configFile.exists()) {
				initConfigFile();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "utf-8"));
			properties.load(reader);
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化配置文件对象
	public static void initConfigFile() {

		try {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(configFile), "utf-8"));

			writer.write("server_port=11086" + "\r\n");
			writer.write("oldRegex=(wd=\\\\S*?&)" + "\r\n");
			writer.write("newChar=wd=wumafanhao&" + "\r\n");

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据key获取value
	public static String getProperty(String key, String defaultValue) {

		// 重新初始化一下，确保是最新文件
		init();
		return properties.getProperty(key, defaultValue);
	}

	// 根据key设置value
	public static void setProperty(String key, String value) {

		init();
		try {
			properties.setProperty(key, value);
			properties.store(new OutputStreamWriter(new FileOutputStream(configFile), "utf-8"), "Config For BurpUtils");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		System.out.println(getProperty("oldRegex", "222"));
	}
}