package database.entrybuilders;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static routes.util.ParamsName.MONGO_ID;

public abstract class AbsEntryBuilder implements Cloneable {

    final static String METADATA = "metadata";

    Document entry;

    AbsEntryBuilder() {
        entry = new Document();
    }

    public AbsEntryBuilder setMetadata(JSONObject json) {

        entry.append(METADATA, json);
        return this;
    }

    public AbsEntryBuilder setId(ObjectId id) {
        entry.put(MONGO_ID, id);
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
