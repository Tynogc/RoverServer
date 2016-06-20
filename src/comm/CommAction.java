package comm;

public class CommAction {
	
	public static void handleExtern(String s){
		if(s.length()<2){
			debug.Debug.println("* Problem handling String: To Short!", debug.Debug.WARN);
			return;
		}
		char c = s.charAt(0);
		
		if(c=='*'){
			command(s.substring(1));
		}else if(c=='E'){
			ecam(s.substring(1));
		}
	}
	
	private static void command(String s){
		String[] h = s.split("_");
		if(h.length<=0)return;
		
		if(h[0].compareToIgnoreCase("SSK")==0){
			rover.RoverControle.rover.ssk.process(h);
		}
	}
	
	private static void ecam(String s){
		if(s.contains("Telem")){
			if(s.contains("STOP")){
				main.RoverServer.sendTelem = false;
			}else if(s.contains("RS")){
				main.RoverServer.sendTelem = true;
			}
		}
	}

}
