package noppes.dialog;

import java.util.HashMap;
import java.util.Set;

import noppes.dialog.nbt.NBTTagCompound;
import noppes.dialog.nbt.NBTTagList;

public class Dialog{
	public int version;
	public int id = -1;;
	public String title = "New";
	public String text = "";
	public int quest = -1;
	public DialogCategory category;
	public HashMap<Integer,DialogOption> options = new HashMap<Integer,DialogOption>();
	public String sound;
	public String command = "";
	
	private NBTTagCompound data = new NBTTagCompound();
	
	public void readNBT(NBTTagCompound compound) {
    	version = compound.getInteger("ModRev");
		
    	id = compound.getInteger("DialogId");
    	title = compound.getString("DialogTitle");
    	text = compound.getString("DialogText");
    	quest = compound.getInteger("DialogQuest");
    	sound = compound.getString("DialogSound");
		command = compound.getString("DialogCommand");
    	
		NBTTagList options = compound.getTagList("Options", 10);
		HashMap<Integer,DialogOption> newoptions = new HashMap<Integer,DialogOption>();
		for(int iii = 0; iii < options.tagCount();iii++){
            NBTTagCompound option = options.getCompoundTagAt(iii);
            int opslot = option.getInteger("OptionSlot");
            DialogOption dia = new DialogOption();
            dia.readNBT(option.getCompoundTag("Option"));
            newoptions.put(opslot, dia);
		}
		this.options = newoptions;
		this.data = compound;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Set<String> keys = data.func_150296_c();
		for(String key : keys)
			compound.setTag(key, compound.getTag(key));
		compound.setInteger("ModRev", version);
		compound.setInteger("DialogId", id);
		compound.setString("DialogTitle", title);
		compound.setString("DialogText", text);
		compound.setInteger("DialogQuest", quest);
		compound.setString("DialogCommand", command);
		if(sound != null && !sound.isEmpty())
			compound.setString("DialogSound", sound);

		NBTTagList options = new NBTTagList();
		for(int opslot : this.options.keySet()){
			NBTTagCompound listcompound = new NBTTagCompound();
			listcompound.setInteger("OptionSlot", opslot);
			listcompound.setTag("Option", this.options.get(opslot).writeNBT());
			options.appendTag(listcompound);
		}
		compound.setTag("Options", options);
		return compound;
	}

	public boolean hasOtherOptions() {
		for(DialogOption option: options.values())
			if(option != null && option.optionType != EnumOptionType.Disabled)
				return true;
		return false;
	}	
	
	public String toString(){
		return title;
	}
}
