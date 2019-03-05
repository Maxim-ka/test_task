package data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;

public class RowData{

    private final StringProperty extensionProperty;
    private final StringProperty typeProperty;
    private final StringProperty exampleProperty;
    private final HashMap<String, String> hashMap;

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public RowData(String extension, String type, String example) {
        extensionProperty = new SimpleStringProperty(extension);
        typeProperty = new SimpleStringProperty(type);
        exampleProperty = new SimpleStringProperty(example);
        hashMap = new HashMap<>();
        hashMap.put(extension, example);
    }

    public String getExtension() {
        return extensionProperty.get();
    }

    public StringProperty extensionProperty() {
        return extensionProperty;
    }

    public String getType() {
        return typeProperty.get();
    }

    public StringProperty typeProperty() {
        return typeProperty;
    }

    public String getExample() {
        return exampleProperty.get();
    }

    public StringProperty exampleProperty() {
        return exampleProperty;
    }
}
