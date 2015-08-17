package noppes.dialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Json {
	public static final int STRING = 0;
	public static final int INTEGER = 1;
	public static final int DOUBLE = 2;
	public static final int LIST = 3;
	public static final int MAP = 4;
	public static final int LONG = 5;
	public static final int FLOAT = 6;
	public static final int BYTE = 7;
	
	public int type = -1;
	
	public Json get(String s) {
		return null;
	}

	public boolean has(String s) {
		return false;
	}

	public List<Json> getList() {
		throw new IllegalArgumentException("Not a List");
	}
	
	public int getListType() {
		throw new IllegalArgumentException("Not a List");
	}

	public void add(Object ob){
		throw new IllegalArgumentException("Not a List");
	}

	public boolean remove(Object ob){
		throw new IllegalArgumentException("Not a List or Map");
	}

	public void put(String key, Object ob){
		throw new IllegalArgumentException("Not a Map");
	}

	public Map<String, Json> getMap() {
		throw new IllegalArgumentException("Not a Map");
	}
	
	public int getInt(){
		throw new IllegalArgumentException("Not an Integer");
	}
	
	public int getByte(){
		throw new IllegalArgumentException("Not a Byte");
	}

	public int getInt(String key, int... val) {
		Json json = get(key);
		if(json != null)
			return json.getInt();
		return val.length == 0?0 : val[0];
	}
	
	public double getDouble(){
		throw new IllegalArgumentException("Not a Double");
	}
	
	public float getFloat(){
		throw new IllegalArgumentException("Not a Float");
	}
	
	public long getLong(){
		throw new IllegalArgumentException("Not a Long");
	}
	
	public String getString(){
		throw new IllegalArgumentException("Not a String");
	}
	
	public String convertToString(int depth) {
		return toString();
	}

	public abstract Json copy();
	
	public static Json convertToJson(Object ob){
		Json j = null;
		if(ob instanceof Json){
			j = (Json) ob;
		}
		else if(ob instanceof Map){
			j = new JsonMap();
			for(Entry<?,?> entry : ((Map<?, ?>) ob).entrySet()){
				j.put(entry.getKey().toString(), entry.getValue());
			}
		}
		else if(ob instanceof Iterable<?>){
			j = new JsonList();
			for(Object value : ((Iterable<?>) ob)){
				j.add(value);
			}
		}
		else if(ob instanceof Integer){
			j = new JsonInt((Integer) ob);
		}
		else if(ob instanceof Boolean){
			j = new JsonInt((Boolean) ob?1:0);
		}
		else if(ob instanceof Long){
			j = new JsonLong((Long) ob);
		}
		else if(ob instanceof Float){
			j = new JsonFloat((Float) ob);
		}
		else if(ob instanceof Double){
			j = new JsonDouble((Double) ob);
		}
		else{
			j = new JsonString(ob.toString());
		}
		
		return j;
	}
	
	private static String getTabbedDepth(int depth){
		String s = "";
		for(int i = 0; i < depth; i++){
			s += "\t";
		}
		return s;
	}

	
	public void save(File file) throws IOException{
		if(type != MAP)
			throw new IllegalArgumentException("Can only save a map");
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
		try{
			writer.write(toString());
		}
		finally{
			writer.close();
		}
	}
	
	public byte[] toBytes() throws IOException{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		OutputStreamWriter oos = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
		try {
			oos.write(toString());
		}
		finally{
			oos.close();
		}
		return stream.toByteArray();
	}

	public static Json readFromBytes(byte[] bytes) throws IOException, JsonException{
		JsonFile jsonFile = new JsonFile(new String(bytes, "UTF-8"));
		Json json = new JsonMap();
		FillJsonFile(json, jsonFile);
		return json;
	}

	public static Json Load(File file) throws IOException, JsonException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			JsonFile jsonFile = new JsonFile(new String(data, "UTF-8"));
			if(!jsonFile.startsWith("{") || !jsonFile.endsWith("}"))
				throw new JsonException("Not properly incapsulated between { }", jsonFile);
			Json json = new JsonMap();
			FillJsonFile(json, jsonFile);
			return json;
		}
		finally{
			if(fis != null)
				fis.close();
		}
	}

	public static void FillJsonFile(Json json, JsonFile jsonFile) throws JsonException{
		if(jsonFile.startsWith("{") || jsonFile.startsWith(","))
			jsonFile.cut(1);
		if(jsonFile.startsWith("}"))
			return;
		int index = jsonFile.indexOf(":");
		if(index < 1)
			throw new JsonException("Expected key after ," ,jsonFile);
		
		String key = jsonFile.substring(0, index);
		jsonFile.cut(index + 1);
		
		Json base = ReadValue(jsonFile);
		
		if(base == null)
			base = new JsonString("");
		
		if(key.startsWith("\""))
			key = key.substring(1);
		if(key.endsWith("\""))
			key = key.substring(0, key.length() - 1);
		
		json.put(key, base);
		if(jsonFile.startsWith(","))
			FillJsonFile(json, jsonFile);
		
	}
	
	private static Json ReadValue(JsonFile json) throws JsonException{
		if(json.startsWith("{")){
			Json compound = new JsonMap();
			FillJsonFile(compound, json);
			if(!json.startsWith("}")){
				throw new JsonException("Expected }", json);
			}
			json.cut(1);
			
			return compound;
		}
		if(json.startsWith("[")){
			json.cut(1);
			Json list = new JsonList();
			
			Json value = ReadValue(json);
			while(value != null){
				list.add(value);
				if(!json.startsWith(","))
					break;
				json.cut(1);
				value = ReadValue(json);
			}
			if(!json.startsWith("]")){
				throw new JsonException("Expected ]", json);
			}
			json.cut(1);			
			return list;
		}
		if(json.startsWith("\"")){
			json.cut(1);
			String s = "";
			boolean ignore = false;
			while(!json.startsWith("\"") || ignore){
				String cut = json.cutDirty(1);
				ignore = cut.equals("\\");
				s += cut;
			}
			json.cut(1);
			return new JsonString(s.replace("\\\"", "\""));
		}
		String s = "";
		while(!json.startsWith(",", "]", "}")){
			s += json.cut(1);
		}
		s = s.trim().toLowerCase();
		if(s.isEmpty())
			return null;
		try{
			if(s.endsWith("b")){
				return new JsonByte(Byte.parseByte(s.substring(0, s.length() - 1)));
			}
			if(s.endsWith("d")){
				return new JsonDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
			}
			if(s.endsWith("f")){
				return new JsonFloat(Float.parseFloat(s.substring(0, s.length() - 1)));
			}
			if(s.endsWith("l")){
				return new JsonLong(Long.parseLong(s.substring(0, s.length() - 1)));
			}
			if(s.contains("."))
				return new JsonDouble(Double.parseDouble(s));
			else
				return new JsonInt(Integer.parseInt(s));
		}
		catch(NumberFormatException ex){
			throw new JsonException("Unable to convert: "+ s +" to a number", json);
		}
	}
	
	public static class JsonMap extends Json{
		private final Map<String, Json> data = new HashMap<String, Json>();
		public JsonMap(){
			type = MAP;
		}
		
		@Override
		public Json get(String s){
			return data.get(s);
		}
		
		@Override
		public boolean remove(Object ob){
			return data.remove(ob) != null;
		}

		@Override
		public boolean has(String s) {
			return data.containsKey(s);
		}

		@Override
		public void put(String key, Object ob){
			data.put(key, convertToJson(ob));
		}

		@Override
		public Map<String, Json> getMap() {
			return data;
		}
		
		@Override
		public String convertToString(int depth) {
			String s = getTabbedDepth(depth) + "{"+ System.getProperty("line.separator");
			Iterator<Entry<String, Json>> ita = data.entrySet().iterator();
			while(ita.hasNext()){
				Entry<String, Json> entry = ita.next();
				s += getTabbedDepth(depth + 1) + "\"" + entry.getKey() + "\": " + entry.getValue().convertToString(depth + 1);
				if(ita.hasNext())
					s += "," + System.getProperty("line.separator");
			}
			s += System.getProperty("line.separator") + getTabbedDepth(depth) + "}";
			return s;
		}

		@Override
		public String toString(){
			return convertToString(0);
		}

		@Override
		public Json copy(){
			Json json = new JsonMap();
			for(Entry<String, Json> entry : data.entrySet()){
				json.put(entry.getKey(), entry.getValue().copy());
			}
			return json;
		}
	}
	
	static class JsonInt extends Json{
		private final int data;
		public JsonInt(int data){
			type = INTEGER;
			this.data = data;
		}

		@Override
		public int getInt(){
			return data;
		}

		@Override
		public String toString() {
			return data + "";
		}

		@Override
		public Json copy(){
			return new JsonInt(data);
		}
	}

	
	static class JsonByte extends Json{
		private final byte data;
		public JsonByte(byte data){
			type = BYTE;
			this.data = data;
		}

		@Override
		public int getByte(){
			return data;
		}

		@Override
		public String toString() {
			return data + "b";
		}

		@Override
		public Json copy(){
			return new JsonByte(data);
		}
	}
	
	static class JsonDouble extends Json{
		private final double data;
		public JsonDouble(double data){
			type = DOUBLE;
			this.data = data;
		}

		@Override
		public double getDouble(){
			return data;
		}

		@Override
		public String toString() {
			return data + "";
		}

		@Override
		public Json copy(){
			return new JsonDouble(data);
		}
	}
	
	static class JsonLong extends Json{
		private final long data;
		public JsonLong(long data){
			type = LONG;
			this.data = data;
		}

		@Override
		public long getLong(){
			return data;
		}

		@Override
		public String toString() {
			return data + "L";
		}

		@Override
		public Json copy(){
			return new JsonLong(data);
		}
	}
	
	static class JsonFloat extends Json{
		private final float data;
		public JsonFloat(float data){
			type = FLOAT;
			this.data = data;
		}

		@Override
		public float getFloat(){
			return data;
		}

		@Override
		public String toString() {
			return data + "F";
		}

		@Override
		public Json copy(){
			return new JsonFloat(data);
		}
	}
	
	static class JsonString extends Json{
		private final String data;
		public JsonString(String data){
			type = STRING;
			this.data = data;
		}
		
		@Override
		public String getString(){
			return data;
		}

		@Override
		public String toString() {
			return "\"" + data + "\"";
		}

		@Override
		public Json copy(){
			return new JsonString(data);
		}
	}
	
	public static class JsonList extends Json{
		private final List<Json> data = new ArrayList<Json>();
		private int listType = -1;
		public JsonList(){
			type = LIST;
		}

		@Override
		public void add(Object ob){
			data.add(convertToJson(ob));
		}

		
		@Override
		public boolean remove(Object ob){
			return data.remove(ob);
		}

		@Override
		public List<Json> getList(){
			return data;
		}

		@Override
		public int getListType() {
			return listType;
		}

		@Override
		public String convertToString(int depth) {
			String s = "[ " + System.getProperty("line.separator");
			Iterator<Json> ita = data.iterator();
			while(ita.hasNext()){
				s += ita.next().convertToString(depth + 1);
				if(ita.hasNext())
					s += "," + System.getProperty("line.separator");
			}
			s += System.getProperty("line.separator") + getTabbedDepth(depth) + "]";
			return s;
		}

		@Override
		public String toString() {
			return convertToString(0);
		}

		@Override
		public Json copy(){
			Json json = new JsonList();
			for(Json j : data){
				json.add(j.copy());
			}
			return json;
		}
	}

	public static class JsonException extends Exception{
		public JsonException(String message, JsonFile json){
			super(message + ": " + json.getCurrentPos());
		}
	}
	static class JsonFile{
		private String original;
		private String text;
		
		public JsonFile(String text){
			this.text = text.trim();
			this.original = this.text;
		}

		public String cutDirty(int i) {
			String s = text.substring(0, i);
			text = text.substring(i);
			return s;
		}

		public String cut(int i) {
			String s = text.substring(0, i);
			text = text.substring(i).trim();
			return s;
		}

		public String substring(int beginIndex, int endIndex) {
			return text.substring(beginIndex, endIndex);
		}

		public int indexOf(String s) {
			return text.indexOf(s);
		}
		
		public String getCurrentPos(){
			int lengthOr = original.length();
			int lengthCur = text.length();
			int currentPos = lengthOr - lengthCur;
			String done = original.substring(0, currentPos);
			String[] lines = done.split("\r\n|\r|\n");
			
			int pos = 0;
			String line = "";
			if(lines.length > 0){
				pos = lines[lines.length - 1].length();
				line = original.split("\r\n|\r|\n")[lines.length - 1].trim();
			}
			return "Line: " + lines.length + ", Pos: " + pos + ", Text: " + line;
		}

		public boolean startsWith(String... ss) {
			for(String s : ss)
				if(text.startsWith(s))
					return true;
			return false;
		}
		public boolean endsWith(String s) {
			return text.endsWith(s);
		}
	}
}
