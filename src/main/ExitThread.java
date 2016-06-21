package main;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class ExitThread extends Thread{

	public static String shutDownCause = "UNKNOWN";
	
	public static Exception exeption;
	
	public static final String SHDN_NORMAL = "normal";
	
	public static boolean restart = false;
	
	public ExitThread(){
		
	}
	
	public void run(){
		if(shutDownCause.compareTo(SHDN_NORMAL) != 0 || exeption != null){
			FaultNotification.ThrowFaultNotification("Unplaned Shutdown: System Stoped!"
					,"Cause: "+shutDownCause, FaultNotification.SERIOUS, true, exeption);
		}
		if(restart){
			StringBuilder cmd = new StringBuilder();
			cmd.append(System.getProperty("java.home")+File.separator+"bin"+File.separator+"java ");
			for(String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()){
				cmd.append(jvmArg+" ");
			}
			cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
			cmd.append(RoverServer.class.getName()).append(" ");
			try {
				Runtime.getRuntime().exec(cmd.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
