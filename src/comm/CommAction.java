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
		}else if(c=='>'){
			//Sicherheits-Relevantes Komando
			scC(s.substring(1));
		}else if(c=='<'){
			//Sicherheits-Relevantes KoSchluessel
			scP(s.substring(1));
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
	
	private static String lastSecureComand = null;
	private static String lastSecureCode = null;
	private static FingerPrint fingerPrint;
	private static void scC(String s){
		if(fingerPrint == null){
			fingerPrint = new FingerPrint();
		}
		
		int i = (int)(Math.random()*fingerPrint.lenght);
		lastSecureCode = fingerPrint.getFingerprintAt(i);
		lastSecureComand = s;
		Communication.com.printExternal(">"+i);
	}
	
	private static void scP(String s){
		if(lastSecureCode != null){
			if(s.compareTo(lastSecureCode)==0){
				if(lastSecureComand == null){
					debug.Debug.println("* ERROR CommAction01a: Comand is null!", debug.Debug.ERROR);
					return;
				}
				processSecureComand(lastSecureComand);
				lastSecureCode = null;
				lastSecureComand = null;
			}else{
				debug.Debug.println("* Insecure Response, Comand isn't Processed!", debug.Debug.ERROR);
				debug.Debug.println(" "+lastSecureComand, debug.Debug.SUBERR);
			}
		}else{
			debug.Debug.println("* ERROR CommAction01b: Key is null!", debug.Debug.ERROR);
		}
	}
	
	private static void processSecureComand(String s){
		if(s.compareTo("RUN")==0){
			debug.Debug.println("***Power Circuit Started***", debug.Debug.MASSAGE);
			Communication.com.printExternal("/CPower Circuit Started!");
			//TODO
		}
	}

}
