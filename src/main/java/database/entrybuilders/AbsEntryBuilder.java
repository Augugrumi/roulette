package database.entrybuilders;

import org.bson.Document;
import org.json.JSONObject;

public abstract class AbsEntryBuilder {

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
    };
}
