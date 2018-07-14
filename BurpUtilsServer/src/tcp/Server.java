package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import burp.BurpExtender;
import utils.ConfigUtils;

/**
 * @author pengfei_2016@163.com
 * 
 *         BurpUtilsServer 服务端 监听本地的38693端口，请确保该端口可用且未被占用
 */
public class Server {

	// 服务端socket对象
	public ServerSocket serverSocket;
	// 用来接收Burp内置控制台的输出对象
	public PrintWriter stdout;
	// 格式化日期对象
	public SimpleDateFormat dataFormat;
	// 统一插件中的名字
	public String name = "BurpUtilsServer";
	// 创建一个线程池，多线程进行socket处理
	public ExecutorService executorService;
	// 服务端使用的端口
	public int port;

	public Server(PrintWriter stdout) {

		// 初始化日志输出对象
		this.stdout = stdout;
		// 获取格式化日期对象实例
		dataFormat = new SimpleDateFormat("[yyyy-mm-dd HH:MM:ss:sss]");
		// 初始化线程池，最多10个线程
		executorService = Executors.newFixedThreadPool(10);
		// 从配置文件初始化端口
		port = Integer.parseInt(ConfigUtils.getProperty("server_port", "11086"));
	}

	// 输出日志方法封装
	public void print(String string) {

		this.stdout.println(dataFormat.format(new Date()) + string);
	}

	// 开始监听本地38693端口
	public void start() {

		print("开始监听本地" + port + "端口...");
		try {
			// 监听本地的38693端口
			serverSocket = new ServerSocket(port);
			Socket socket = null;
			// 持续监听
			while (true) {
				// 等待客户端接入
				socket = serverSocket.accept();
				print("socketjieru1 " + socket.toString());
				// 多线程处理
				executorService.execute(new Handler(socket, stdout));
			}
		} catch (Exception e) {
			// 异常日志打印
			print("监听本地" + port + "端口异常：" + e.toString());
		} finally {
			try {
				// 关闭资源
				serverSocket.close();
			} catch (IOException e) {
				// 异常日志打印
				print("关闭" + port + "端口异常：" + e.getMessage());
			}
		}
	}
}

// Server端socket处理类
class Handler implements Runnable {

	// 用来接收Burp内置控制台的输出对象
	public PrintWriter stdout;
	// 格式化日期对象
	public SimpleDateFormat dataFormat;
	// 接收Socket对象
	public Socket socket;

	// 初始化处理类对象
	public Handler(Socket socket, PrintWriter stdout) {

		// 初始化日志输出对象
		this.stdout = stdout;
		// 获取格式化日期对象实例
		this.dataFormat = new SimpleDateFormat("[yyyy-mm-dd HH:MM:ss:sss]");
		// 获取Socket对象
		this.socket = socket;

		print("初始化Handler类");
	}

	@Override
	public void run() {

		// 定义输入输出流对象
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			// 获取输入输出流对象实例
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			// 接收读到的消息
			String line = null;
			String msg = "";
			TreeMap<String, String> msgMap = new TreeMap<String, String>();

			while ((line = reader.readLine()) != null) {
				msg = msg + line + "\r\n";
				String[] ss = line.split(":");
				msgMap.put(ss[0], ss[1]);
			}

			BurpExtender.oldRegex = msgMap.get("regex");
			BurpExtender.newChar = msgMap.get("newChar");

			print("Client says：");
			print(msg.substring(0, msg.length() - 2));
			print("--over--");

			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			writer.write("success");
			writer.flush();

			socket.shutdownOutput();

			print(socket.isClosed() + "");

			reader.close();
			writer.close();
			socket.close();
		} catch (IOException e) {
			print("异常 " + e.getMessage());
		} finally {

		}
	}

	/**
	 * 输出日志方法封装
	 */
	public void print(String string) {

		this.stdout.println(dataFormat.format(new Date()) + string);
	}
}