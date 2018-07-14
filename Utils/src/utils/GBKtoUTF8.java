package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class GBKtoUTF8 {

	// 遍历文件
	public static void fileList(File file) {

		File rootFile = file;
		File[] files = rootFile.listFiles();

		if (files != null) {
			for (File f : files) {
				if (!f.isDirectory()) {

					String tmp = f.getName();
					if (tmp.endsWith(".java") || tmp.endsWith(".log")) {
						System.out.println("change file : " + tmp);
						codeConvert(f);
					}
					System.out.println("skip file : " + tmp);
				}

				fileList(f);// 递归调用子文件夹下的文件
			}
		}
	}

	public static void main(String[] args) {

		File file = new File("C:\\Users\\FLY\\eclipse-workspace\\PerfAutoTest");
		GBKtoUTF8.fileList(file);
	}

	public static void codeConvert(File file) {

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), Charset.forName("GBK")));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}