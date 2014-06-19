package noppes.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	
	public void saveCategory(DialogCategory category){
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
			while(containsCategoryName(category, category.title))
				category.title += "_";
			
			category.dialogs = currentCategory.dialogs;
		}
		else{
			while(containsCategoryName(category, category.title))
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
	
	public boolean containsCategoryName(DialogCategory category, String title) {
		return getCategoryFromName(category, title) != null;
	}

	public DialogCategory getCategoryFromName(DialogCategory category, String title) {
		for(DialogCategory cat : categories.values()){
			if((category.id != cat.id || category == null) && cat.title.equalsIgnoreCase(category.title))
				return cat;
		}
		return null;
	}
	
	public boolean containsDialogName(DialogCategory category, Dialog dialog, String name) {
		return getDialogFromName(category, dialog, name) != null;
	}

	public Dialog getDialogFromName(DialogCategory category, Dialog dialog, String name) {
		name = name.toLowerCase();
		for(Dialog dia : category.dialogs.values()){
			if(dialog.id != dia.id && dia.title.toLowerCase().equals(name))
				return dia;
		}
		return null;
	}
	
	public int getUniqueDialogID(){
		if(lastUsedDialogID == 0){
			for(int catid : dialogs.keySet())
				if(catid > lastUsedDialogID)
					lastUsedDialogID = catid;
		}
		lastUsedDialogID++;
		return lastUsedDialogID;
		
	}
	public void saveDialog(int categoryId,Dialog dialog){
		DialogCategory category = categories.get(categoryId);
		if(category == null)
			return;
		
		dialog.category = category;

		if(dialog.id < 0){
			dialog.id = getUniqueDialogID();
    		while(containsDialogName(dialog.category, dialog, dialog.title))
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

	public int getUniqueCategoryID() {
		if(lastUsedCatID == 0){
			for(int catid : categories.keySet())
				if(catid > lastUsedCatID)
					lastUsedCatID = catid;
		}
		lastUsedCatID++;
		return lastUsedCatID;
	}

}
