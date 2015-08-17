package noppes.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DialogCategory {
	public Json data = new Json.JsonMap();
	public Map<Integer,Dialog> dialogs = new HashMap<Integer,Dialog>();

	public DialogCategory(Json data){
		this.data = data;
	}

	public DialogCategory(){}
	
	public int getID(){
		Json json = data.get("Slot");
		if(json == null)
			return -1;
		return json.getInt();
	}

	public void setID(int id) {
		data.put("Slot", id);
	}
	
	public String getTitle(){
		Json json = data.get("Title");
		if(json == null)
			return "New";
		return json.getString();
	}

	public void setTitle(String title) {
		data.put("Title", title);
	}
	
	public String toString(){
		return getTitle();
	}
}
