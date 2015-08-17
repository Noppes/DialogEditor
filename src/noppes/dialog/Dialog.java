package noppes.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Dialog{	
	public Json data = new Json.JsonMap();
	public int id = -1;
	public DialogCategory category = null;
	private Map<Integer,DialogOption> options = null;
	
	public Dialog(Json data){
		this.data = data;
	}
	
	public Dialog(){}
	
	public String getTitle(){
		Json json = data.get("DialogTitle");
		if(json == null)
			return "";
		return json.getString();
	}

	public void setTitle(String title) {
		data.put("DialogTitle", title);
	}
	
	public String getText(){
		Json json = data.get("DialogText");
		if(json == null)
			return "New";
		return json.getString();
	}

	public void setText(String title) {
		data.put("DialogText", title);
	}
	
	public Map<Integer,DialogOption> getOptions(){
		if(options != null)
			return options;
		Map<Integer,DialogOption> options = new HashMap<Integer,DialogOption>();
		Json json = data.get("Options");
		if(json == null)
			return options;
		for(Json entry : json.getList()){
			try{
				int id = entry.get("OptionSlot").getInt();
	            DialogOption option = new DialogOption(entry.get("Option"), id);
	            options.put(id, option);
			}
			catch(NumberFormatException ex){
				
			}
		}
		this.options = options;
		return options;
	}
	
	public void setOptions(Map<Integer, DialogOption> options){
		this.options = options;
		Json list = new Json.JsonList();
		for(Entry<Integer, DialogOption> entry : options.entrySet()){
			Json json = new Json.JsonMap();
			json.put("OptionSlot", entry.getKey());
			json.put("Option", entry.getValue().data);
			list.add(json);
		}
		data.put("Options", list);
	}

	public void setOption(int id, DialogOption option) {
		Map<Integer,DialogOption> options = getOptions();
		options.put(id, option);
		setOptions(options);
	}

	public int addOption(DialogOption option) {
		Map<Integer,DialogOption> options = getOptions();
		for(int i = 0; i < 6; i++){
			if(!options.containsKey(i)){
				options.put(i, option);
				option.optionID = i;
				setOptions(options);
				return i;
			}
		}
		return -1;
	}

	public void removeOption(int id) {
		Map<Integer,DialogOption> options = getOptions();
		options.remove(id);
		setOptions(options);
	}
	
	public String toString(){
		return getTitle() + " : " + id;
	}
	

}
