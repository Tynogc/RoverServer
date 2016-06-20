package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FaultNotification {

	public static final int LOW = 0;
	public static final int NORMAL = 1;
	public static final int SERIOUS = 2;
	
	public static final String exStr = "#Exeption:";
	public static final String exStrEnd = "ExeptionEnd";
	
	public static void ThrowFaultNotification(String cause, int intensity, boolean shutDown){
		ThrowFaultNotification(cause, null, null, intensity, shutDown, null);
	}
	public static void ThrowFaultNotification(String cause, String l1, int intensity, boolean shutDown){
		ThrowFaultNotification(cause, l1, null, intensity, shutDown, null);
	}
	public static void ThrowFaultNotification(String cause, String l1, int intensity, boolean shutDown, Exception e){
		ThrowFaultNotification(cause, l1, null, intensity, shutDown, e);
	}
	public static void ThrowFaultNotification(String cause, String l1, String l2, int intensity, boolean shutDown){
		ThrowFaultNotification(cause, l1, l2, intensity, shutDown, null);
	}
	public static void ThrowFaultNotification(String cause, String l1, String l2, int intensity, boolean shutDown, Exception e){
		Settings s = new Settings();
		s.upCurrentError();
		s.shutdownErrors=shutDown;
		int pos = s.getCurrentError();
		s.end();
		
		String filepath = "log/err/"+pos+".txt";
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(filepath));
			writer.println(cause);
			if(l1 != null)
			writer.println(l1);
			if(l2 != null)
			writer.println(l2);
			
			if(e != null){
				writer.println(exStr);
				//writer.println("#"+e.toString());
				e.printStackTrace(writer);
				writer.println(exStrEnd);
			}
			
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
}
