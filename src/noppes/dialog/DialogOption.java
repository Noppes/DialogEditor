package noppes.dialog;


public class DialogOption {
	public Json data = new Json.JsonMap();
	public int optionID;
	
	public DialogOption(Json data, int optionID){
		this(optionID);
		this.data = data;
	}

	public DialogOption(int optionID){
		this.optionID = optionID;
	}
	
	public String getTitle(){
		Json json = data.get("Title");
		if(json == null)
			return "Talk";
		return json.getString();
	}

	public void setTitle(String title) {
		data.put("Title", title);
	}
	
	public String toString(){
		return getTitle() + " : " + optionID ;
	}
}
