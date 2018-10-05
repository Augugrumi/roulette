package database.entrybuilders;

import org.bson.Document;
import org.json.JSONObject;

public abstract class AbsEntryBuilder implements Cloneable {

    final static String METADATA = "metadata";

    Document entry;

    AbsEntryBuilder() {
        entry = new Document();
    }

    public AbsEntryBuilder addMetadata(JSONObject json) {

        entry.append(METADATA, json);
        return this;
    }

    public Document build() {
        return entry;
    }

    @Override
    public String toString() {
        return entry.toString();
    }

    @Override
    public AbsEntryBuilder clone() throws CloneNotSupportedException {
        AbsEntryBuilder clone = (AbsEntryBuilder)super.clone();
        clone.entry = new Document(entry);

        return clone;
    }
}
