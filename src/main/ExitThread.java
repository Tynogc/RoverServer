package main;

public class ExitThread extends Thread{

	public static String shutDownCause = "UNKNOWN";
	
	public static Exception exeption;
	
	public static final String SHDN_NORMAL = "normal";
	
	public ExitThread(){
		
	}
	
	public void run(){
		if(shutDownCause.compareTo(SHDN_NORMAL) != 0 || exeption != null){
			FaultNotification.ThrowFaultNotification("Unplaned Shutdown: System Stoped!"
					,"Cause: "+shutDownCause, FaultNotification.SERIOUS, true, exeption);
		}
	}
}
