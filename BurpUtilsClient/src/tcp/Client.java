package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import utils.ConfigUtils;

public class Client {
    
	public static String oldRegex; 
	public static String newChar;
	
    public static void main(String[] args) {

    	oldRegex = ConfigUtils.getProperty("oldRegex", "2233");
    	newChar = ConfigUtils.getProperty("newChar", "Java");
    	
        Socket socket = null;

        BufferedWriter writer = null;
        BufferedReader reader = null;
        
        try {
            socket = new Socket("localhost", 38693);
            
            
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
            writer.write("regex:" + oldRegex + "\r\n");
            writer.write("newChar:" + newChar + "\r\n");
            writer.flush();
            
            socket.shutdownOutput();
            
            // 接收服务器的反馈
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            String line = null;
            String reString = "";
            while ((line = reader.readLine())!=null) {
            	reString = reString + line + "\r\n";
            }
            System.out.println(reString);
            
            socket.shutdownInput();
            
            reader.close();
            writer.close();
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}