package ru.thstdio.clientthbox.fileutil;

import org.json.JSONException;
import org.json.JSONObject;

public class PFile {
    public long id;
    public String name;
    public int size;
    public long parent;
    public FileType type;

    public PFile setId(long id) {
        this.id = id;
        return this;
    }

    public PFile setName(String name) {
        this.name = name;
        return this;
    }

    public PFile setSize(int size) {
        this.size = size;
        return this;
    }

    public PFile setParent(long parent) {
        this.parent = parent;
        return this;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject json=new JSONObject();
        json.put("id",id);
        json.put("name",name);
        json.put("parent",parent);
        json.put("size",size);
        return json;
    }
    public FileType getType(){
        if(type==null){
            String[] temp =name.split("\\.");
            if(temp.length>1) type=(FileType.convertType(temp[temp.length-1]));
            else type=FileType.None;
        }
        return type;
    }
}
