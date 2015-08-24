package noppes.dialog;


public class DialogOption {
	public static int DefaultColor = 0xe0e0e0;
	public Json data = new Json.JsonMap();
	public int optionID;
	
	public DialogOption(Json data, int optionID){
		this(optionID);
		this.data = data;
	}

	public DialogOption(int optionID){
		this.optionID = optionID;
		data.put("Title", "Talk");
		data.put("OptionType", 0);
		data.put("Dialog", -1);
		data.put("DialogCommand", "");
		data.put("DialogColor", DefaultColor);
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
	
	public int getColor(){
		Json json = data.get("DialogColor");
		if(json == null)
			return DefaultColor;
		return json.getInt();
	}

	public void setColor(int color) {
		data.put("DialogColor", color);
		DefaultColor = color;
	}

    
    public String getColorHex(){
		String str = Integer.toHexString(getColor());
    	while(str.length() < 6)
    		str = "0" + str;
    	return str;
    }
    
    public void setColorHex(String color){
    	if(color.startsWith("#"))
    		color = color.substring(1);
		setColor(Integer.parseInt(color, 16));
    }
	
	public int getDialogID(){
		Json json = data.get("Dialog");
		if(json == null)
			return -1;
		return json.getInt();
	}

	public void setDialogID(int id) {
		data.put("Dialog", id);
	}
	
	public String toString(){
		return getTitle() + " : " + optionID ;
	}

	public int getType() {
		Json json = data.get("OptionType");
		if(json == null)
			return 2;
		return json.getInt();
	}
	
	public void setType(int type){
		data.put("OptionType", type);
	}
}
