package comm;

public class FiFo {
	
	private FiElement first;
	
	public FiFo(){
		first = null;
	}
	
	public String out(){
		if(first != null){
			String s = first.str;
			first = first.next;
			return s;
		}
		return null;
	}
	
	public boolean contains(){
		return first != null;
	}
	
	public void in(String s){
		if(first == null){
			first = new FiElement(s);
		}else{
			first.add(new FiElement(s));
		}
	}
	
	public int lenght(){
		if(first == null)return 0;
		return first.lenght();
	}

}
