package data;

import java.util.*;

public class Data {

    private final HashMap<String, String> hashMap;
    private String type;

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public String getType(){
        return type;
    }

    public Data(String type) {
        this.type = type;
        hashMap = new HashMap<>();
    }

    public String getTypeData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < hashMap.keySet().size(); i++) {
            list.add(type);
        }
        return String.join("\n", list);
    }

    public String getExtension() {
        Set<String> extensions = hashMap.keySet();
        return String.join("\n", extensions);
    }

    public String getExample() {
        Collection<String> values = hashMap.values();
        return String.join("\n", values);
    }
}
