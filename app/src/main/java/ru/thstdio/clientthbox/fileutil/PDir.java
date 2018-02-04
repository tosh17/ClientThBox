package ru.thstdio.clientthbox.fileutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class PDir extends PFile {

    public Set<PFile> files=new HashSet<>();
    public Set<PDir> dirs=new HashSet<>();


    public void calcSize() {
        size = 0;
        for (PFile f : files) size += f.size;
        for (PDir d : dirs) {
            d.calcSize();
            size += d.size;
        }
    }

    @Override
    public PDir setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    public PDir setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public PDir setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public PDir setParent(long parent) {
        this.parent = parent;
        return this;
    }
    public void addElement(PFile file){
        file.setParent(this.id);
        if(file instanceof PDir) dirs.add((PDir) file);
        else files.add(file);
    }
    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json=new JSONObject();
        json.put("id",id);
        json.put("name",name);
        json.put("parent",parent);
        json.put("size",size);
        JSONArray fJson=new JSONArray();
        JSONArray dJson=new JSONArray();
        for(PFile f:files) fJson.put(f.getJson());
        for(PDir d:dirs) dJson.put(d.getJson());
        json.put("files",fJson);
        json.put("dirs",dJson);
        return json;
    }
}
