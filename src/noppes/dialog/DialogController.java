package noppes.dialog;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.security.auth.login.LoginException;


public class DialogController {
	public HashMap<Integer,DialogCategory> categories = new HashMap<Integer, DialogCategory>();
	public HashMap<Integer,Dialog> dialogs = new HashMap<Integer, Dialog>();
	public static DialogController instance;

	private int lastUsedDialogID = 0;
	private int lastUsedCatID = 0;
	
	private File dir;
	
	public DialogController(){
		instance = this;
	}
	
	public void loadCategories(File dir){
		this.dir = dir;
		categories.clear();
		dialogs.clear();

		lastUsedCatID = 0;
		lastUsedDialogID = 0;

		for(File file : dir.listFiles()){
			if(!file.isDirectory())
				continue;
			DialogCategory category = loadCategoryDir(file);
			Iterator<Entry<Integer, Dialog>> ite = category.dialogs.entrySet().iterator();
			while(ite.hasNext()){
				Entry<Integer, Dialog> entry = ite.next();
				int id = entry.getKey();
				if(id > lastUsedDialogID)
					lastUsedDialogID = id;
				Dialog dialog = entry.getValue();
				if(dialogs.containsKey(id)){
					System.out.println("Duplicate id " + dialog.id + " from category " + category.getTitle());
					ite.remove();
				}
				else{
					dialogs.put(id, dialog);
				}
			}
			lastUsedCatID++;
			category.setID(lastUsedCatID);
			categories.put(category.getID(), category);
		}
		
	}
	private DialogCategory loadCategoryDir(File dir) {
		DialogCategory category = new DialogCategory();
		category.setTitle(dir.getName());
		for(File file : dir.listFiles()){
			if(!file.isFile() || !file.getName().endsWith(".json"))
				continue;
			try{
				Dialog dialog = new Dialog(Json.Load(file));
				dialog.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
				category.dialogs.put(dialog.id, dialog);
				dialog.category = category;
			}
			catch(Exception e){
				LogWriter.except(e);
			}
		}
		return category;
	}
	
	public void saveCategory(DialogCategory category){
		category.setTitle(cleanFileName(category.getTitle()));
		if(categories.containsKey(category.getID())){
			DialogCategory currentCategory = categories.get(category.getID());
			if(!currentCategory.getTitle().equals(category.getTitle())){
				while(containsCategoryName(category.getTitle()))
					category.setTitle(category.getTitle() + "_");
				File newdir = new File(this.dir, category.getTitle());
				File olddir = new File(this.dir, currentCategory.getTitle());
				if(newdir.exists())
					return;
				if(!olddir.renameTo(newdir))
					return;
			}
			category.dialogs = currentCategory.dialogs;
		}
		else{
			if(category.getID() < 0){
				lastUsedCatID++;
				category.setID(lastUsedCatID);
			}
			while(containsCategoryName(category.getTitle()))
				category.setTitle(category.getTitle() + "_");
			File dir = new File(this.dir, category.getTitle());
			if(!dir.exists())
				dir.mkdirs();
		}
		categories.put(category.getID(), category);
	}
	
	public void removeCategory(int category){
		DialogCategory cat = categories.get(category);
		if(cat == null)
			return;
		File dir = new File(this.dir, cat.getTitle());
		if(!dir.delete())
			return;
		for(int dia : cat.dialogs.keySet())
			dialogs.remove(dia);
		categories.remove(category);
	}
	
	private boolean containsCategoryName(String name) {
		name = name.toLowerCase();
		for(DialogCategory cat : categories.values()){
			if(cat.getTitle().toLowerCase().equals(name))
				return true;
		}
		return false;
	}
	
	private boolean containsDialogName(DialogCategory category, Dialog dialog) {
		for(Dialog dia : category.dialogs.values()){
			if(dia.id != dialog.id && dia.getTitle().equalsIgnoreCase(dialog.getTitle()))
				return true;
		}
		return false;
	}
	
	public boolean containsCategoryName(DialogCategory category, String title) {
		return getCategoryFromName(category, title) != null;
	}

	public DialogCategory getCategoryFromName(DialogCategory category, String title) {
		for(DialogCategory cat : categories.values()){
			if((category.getID() != cat.getID() || category == null) && cat.getTitle().equalsIgnoreCase(category.getTitle()))
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
			if(dialog.id != dia.id && dia.getTitle().toLowerCase().equals(name))
				return dia;
		}
		return null;
	}
	
	public Dialog saveDialog(int categoryId, Dialog dialog){
		DialogCategory category = categories.get(categoryId);
		if(category == null)
			return dialog;
		dialog.category = category;

		while(containsDialogName(dialog.category, dialog)){
			dialog.setTitle(dialog.getTitle() + "_");
		}
		if(dialog.id < 0){
			lastUsedDialogID++;
			dialog.id = lastUsedDialogID;
		}
		
    	dialogs.put(dialog.id, dialog);
    	category.dialogs.put(dialog.id, dialog);
    	
    	File dir = new File(this.dir, category.getTitle());
    	if(!dir.exists())
    		dir.mkdirs();

    	File file = new File(dir, dialog.id + ".json_new");
    	File file2 = new File(dir, dialog.id + ".json");
    	
    	try {
    		dialog.data.save(file);
			if(file2.exists())
				file2.delete();
			file.renameTo(file2);
		} catch (Exception e) {
			LogWriter.except(e);
		}
		return dialog;
	}
	
	public void removeDialog(Dialog dialog) {
		DialogCategory category = dialog.category;
		File file = new File(new File(dir, category.getTitle()), dialog.id + ".json");
		if(!file.delete())
			return;
		category.dialogs.remove(dialog.id);
		dialogs.remove(dialog.id);
		
	}

	public boolean hasDialog(int dialogId) {
		return dialogs.containsKey(dialogId);
	}
	
	final static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
	static {
	    Arrays.sort(illegalChars);
	}
	public static String cleanFileName(String badFileName) {
	    StringBuilder cleanName = new StringBuilder();
	    for (int i = 0; i < badFileName.length(); i++) {
	        int c = (int)badFileName.charAt(i);
	        if (Arrays.binarySearch(illegalChars, c) < 0) {
	            cleanName.append((char)c);
	        }
	    }
	    return cleanName.toString();
	}

	public int getUniqueCategoryID() {
		lastUsedCatID++;
		return lastUsedCatID;
	}

	public int getUniqueDialogID() {
		lastUsedDialogID++;
		return lastUsedDialogID;
	}
}
