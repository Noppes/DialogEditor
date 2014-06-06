package noppes.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import noppes.dialog.nbt.CompressedStreamTools;
import noppes.dialog.nbt.NBTTagCompound;
import noppes.dialog.nbt.NBTTagList;

public class DialogController {
	public HashMap<Integer,DialogCategory> categories = new HashMap<Integer, DialogCategory>();
	public HashMap<Integer,Dialog> dialogs = new HashMap<Integer, Dialog>();
	public static DialogController instance;

	private int lastUsedCatID = 0;
	private int lastUsedDialogID = 0;
	
	public DialogController(){
		instance = this;
	}
	
	public void loadCategories(File file) throws Exception{
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
        lastUsedCatID = nbttagcompound1.getInteger("lastID");
        lastUsedDialogID = nbttagcompound1.getInteger("lastDialogID");
        NBTTagList list = nbttagcompound1.getTagList("Data", 10);
        HashMap<Integer,DialogCategory> categories = new HashMap<Integer, DialogCategory>();
        HashMap<Integer,Dialog> dialogs = new HashMap<Integer, Dialog>();
        if(list != null){
            for(int i = 0; i < list.tagCount(); i++)
            {
                DialogCategory category = new DialogCategory();
                category.readNBT(list.getCompoundTagAt(i));
                categories.put(category.id,category);
                Iterator<Map.Entry<Integer, Dialog>> ita = category.dialogs.entrySet().iterator();
                while(ita.hasNext()){
                	Map.Entry<Integer, Dialog> entry = ita.next();
                	if(dialogs.containsKey(entry.getValue().id))
                		ita.remove();
                	else
                		dialogs.put(entry.getValue().id, entry.getValue());
                }
                
            }
        }
        this.categories = categories;
        this.dialogs = dialogs;
	}
	public void saveCategories(File save) throws Exception{
        NBTTagList list = new NBTTagList();
        for(DialogCategory category : categories.values()){
            NBTTagCompound nbtfactions = new NBTTagCompound();
            category.writeNBT(nbtfactions);
        	list.appendTag(nbtfactions);
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setInteger("lastID", lastUsedCatID);
        nbttagcompound.setInteger("lastDialogID", lastUsedDialogID);
        nbttagcompound.setTag("Data", list);
        CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(save));

	}

	public void removeDialog(Dialog dialog) {
		DialogCategory category = dialog.category;
		category.dialogs.remove(dialog.id);
		dialogs.remove(dialog.id);
	}
	
	public void saveCategory(DialogCategory category) throws IOException {
		if(category.id < 0){
			if(lastUsedCatID == 0){
				for(int catid : categories.keySet())
					if(catid > lastUsedCatID)
						lastUsedCatID = catid;
			}
			lastUsedCatID++;
			category.id = lastUsedCatID;
		}
		if(categories.containsKey(category.id)){
			DialogCategory currentCategory = categories.get(category.id);
			if(!currentCategory.title.equals(category.title)){
				while(containsCategoryName(category.title))
					category.title += "_";
			}
			category.dialogs = currentCategory.dialogs;
		}
		else{
			while(containsCategoryName(category.title))
				category.title += "_";
		}
		categories.put(category.id, category);
	}
	public void removeCategory(int category){
		DialogCategory cat = categories.get(category);
		if(cat == null)
			return;
		for(int dia : cat.dialogs.keySet())
			dialogs.remove(dia);
		categories.remove(category);
	}
	
	private boolean containsCategoryName(String name) {
		name = name.toLowerCase();
		for(DialogCategory cat : categories.values()){
			if(cat.title.toLowerCase().equals(name))
				return true;
		}
		return false;
	}
	private boolean containsDialogName(DialogCategory category, String name) {
		name = name.toLowerCase();
		for(Dialog dia : category.dialogs.values()){
			if(dia.title.toLowerCase().equals(name))
				return true;
		}
		return false;
	}
	public void saveDialog(int categoryId,Dialog dialog){
		DialogCategory category = categories.get(categoryId);
		if(category == null)
			return;
		
		dialog.category = category;

		if(dialog.id < 0){
			if(lastUsedDialogID == 0){
				for(int catid : dialogs.keySet())
					if(catid > lastUsedDialogID)
						lastUsedDialogID = catid;
			}
			lastUsedDialogID++;
			dialog.id = lastUsedDialogID;
    		while(containsDialogName(dialog.category, dialog.title))
    			dialog.title += "_";
		}
    	dialogs.put(dialog.id, dialog);
    	dialog.category.dialogs.put(dialog.id, dialog);
	}
	public boolean hasDialog(int dialogId) {
		return dialogs.containsKey(dialogId);
	}

	public Map<String,Integer> getScroll() {
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(DialogCategory category : categories.values()){
			map.put(category.title, category.id);
		}
		return map;
	}
}
