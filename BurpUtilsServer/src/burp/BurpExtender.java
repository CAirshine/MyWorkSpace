package burp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcp.Server;
import utils.ConfigUtils;
import utils.RequestAndResponse;

/**
 * @author pengfei_2016@163.com
 * 
 * BurpSuite插件开发指南之 API 上篇
 * https://www.tuicool.com/articles/aaaa6fA
 * 
 * BurpSuite插件开发指南之 API 下篇
 * https://www.tuicool.com/articles/eU7vUjA
 * 
 * BurpSuite插件开发指南之Java篇
 * 未找到
 * */

/**
 * 1.所有的扩展必须实现IBurpExtender接口，实现的类名必须为"BurpExtender"
 * 
 * 2.扩展可以实现IExtensionStateListener接口，然后调用
 * IBurpExtenderCallbacks.registerExtensionStateListener() 注册一个扩展的状态监听器
 * 在扩展的状态发生改变时，监听器将会收到通知
 * 注意：任何启动后台线程或打开系统资源（如文件或数据库连接）的扩展插件都应该注册一个监听器，并在被卸载后终止线程/关闭资源
 * 
 * 3.扩展可以实现IHttpListener接口 通过调用 IBurpExtenderCallbacks.registerHttpListener() 注册一个
 * HTTP 监听器 Burp 里的任何一个工具发起 HTTP 请求或收到 HTTP 响应都会通知此监听器 扩展可以得到这些交互的数据，进行分析和修改
 * 
 */
public class BurpExtender implements IBurpExtender, IExtensionStateListener, IHttpListener {

	/**
	 * 此接口提供了很多常用的辅助方法，IBurpExtenderCallbacks.getHelpers 获得此接口的实例。
	 * 
	 * 常用方法有： byte[] addParameter(byte[] request, IParameter parameter)
	 * 此方法会添加一个新的参数到 HTTP 请求中，并且会适当更新 Content-Length
	 * 
	 * IRequestInfo analyzeRequest(byte[] request) 此方法用于分析 HTTP 请求信息以便获取到多个键的值
	 * 
	 * IResponseInfo analyzeResponse(byte[] response) 此方法用于分析 HTTP 响应信息以便获取到多个键的值
	 * 
	 * byte[] buildHttpMessage(java.util.List<java.lang.String> headers, byte[]
	 * body) 构建包含给定的 HTTP 头部，消息体的 HTTP 消息
	 * 
	 * byte[] buildHttpRequest(java.net.URL url) 对给定的 URL 发起 GET 请求
	 * 
	 * java.lang.String bytesToString(byte[] data) bytes 到 String 的转换
	 * 
	 * java.lang.String bytesToString(byte[] data) String 到 bytes 的转
	 */
	public IExtensionHelpers helpers;

	// Burp内置控制台的输出对象
	public PrintWriter stdout;
	
	// 格式化日期对象
	public SimpleDateFormat dataFormat;

	// 待替换的字符串 - 采用正则模式，该数值从插件的客户端接收过来，默认为空字符串，免得出现空指针异常
	// 设置为静态变量，可以随时通过tcp消息进行修改
	public static String oldRegex;

	// 替换为的字符串，该数值从插件的客户端接收过来，默认为空字符串，免得出现空指针异常
	// 设置为静态变量，可以随时通过tcp消息进行修改
	public static String newChar;

	// 统一插件中的名字
	public String name = "BurpUtilsServer";

	// 监听本地端口的对象
	public Server server;
	
	// 保存符合条件的请求&响应信息
	public TreeMap<Integer, RequestAndResponse> messageMap = new TreeMap<Integer, RequestAndResponse>();

	/**
	 * IBurpExtenderCallbacks 接口提供了许多在开发插件过程中常用的一些操作 Burp Suite
	 * 利用此接口向扩展中传递了许多回调方法，这些回调方法可被用于在 Burp中执行多个操作 当扩展被加载后，Burp 会调用
	 * registerExtenderCallbacks() 方法，并传递一个 IBurpExtenderCallbacks 的实例
	 * 扩展插件可以通过这个实例调用很多扩展 Burp 功能必需的方法
	 * 
	 * 如：设置扩展插件的属性，操作 HTTP 请求和响应以及启动其他扫描功能等等
	 */
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {

		// 初始化匹配参数
		oldRegex = ConfigUtils.getProperty("oldRegex", "2233");
		newChar = ConfigUtils.getProperty("newChar", "warning");
		
		// 设置扩展名称
		callbacks.setExtensionName(name);

		// 获取IExtensionHelpers实例
		helpers = callbacks.getHelpers();

		// 获取Burp内置控制台的输出对象实例
		stdout = new PrintWriter(callbacks.getStdout(), true);

		// 获取格式化日期对象实例
		dataFormat = new SimpleDateFormat("[yyyy-mm-dd HH:MM:ss:sss]");

		// 由于本身就实现了IExtensionStateListener对象，因此可以把this作为监听器对象传入
		callbacks.registerExtensionStateListener(this);

		// 由于本身就实现了IHttpListener对象，因此可以把this作为监听器对象传入
		callbacks.registerHttpListener(this);

		// 开启监听方法
		server = new Server(stdout);
		new Thread() {
			public void run() {
				server.start();
			};
		}.start();

		// 打印日志，加载插件
		// 放到该方法最后，以上步骤未出错情况下打印插件加载消息
		print(name + " is loading ...");
	}

	/**
	 * 在插件被 unload （卸载）时，会调用此方法 可以通过重写此方法，在卸载插件时，做一些善后处理工作，如关闭数据库连接等操作
	 */
	@Override
	public void extensionUnloaded() {

		try {
			server.serverSocket.close();
		} catch (IOException e) {
			print("extensionUnloaded:" + e.toString());
		}

		print(name + " is unloaded ...");
	}

	/**
	 * 输出日志方法封装
	 */
	public void print(String string) {

		this.stdout.println(dataFormat.format(new Date()) + string);
	}

	/**
	 * @toolFlag 指示了发起请求或收到响应的 Burp 工具的 ID，所有的 toolFlag 定义在 IBurpExtenderCallbacks
	 *           接口中
	 * @messageIsRequest 指示该消息是请求消息（值为True）还是响应消息（值为False）
	 * @messageInfo 被处理的消息的详细信息，是一个 IHttpRequestResponse 对象
	 */
	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {

		// Demo：将request中所有的2233替换成bilibili
		if (messageIsRequest) {
			print(toolFlag + "请求");
			// 获取请求字段
			String request = helpers.bytesToString(messageInfo.getRequest());

			// 由于oldRegex可能会变，所以每次都要重新生成一个pattern
			Pattern pattern = Pattern.compile(oldRegex);
			Matcher matcher = pattern.matcher(request);
			if (matcher.find()) {
				RequestAndResponse requestAndResponse = new RequestAndResponse();
				requestAndResponse.toolFlag = new Integer(toolFlag);
				messageMap.put(new Integer(toolFlag), requestAndResponse);
				
				messageInfo.setHighlight("green");
				request = request.replaceAll(matcher.group(0), newChar);
//				print("请求中匹配到符合正则" + oldRegex + "的内容，将所有符合条件的字段替换为：" + newChar);
//				print("修改后的请求为：");
//				print(request);
			}

			messageInfo.setRequest(helpers.stringToBytes(request));
		}
		
		if (!messageIsRequest) {
			print(toolFlag + "响应");
		}
	}
}
