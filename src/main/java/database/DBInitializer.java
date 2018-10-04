package database;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import util.ConfigManager;

public class DBInitializer {

    private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(DBInitializer.class);

    private DBInitializer() {

    }

    public static void init () throws Exception {

        // Check if the collection already exists
        MongoDatabase db = ConfigManager.getConfig().getDatabase();

        // Route db
        try {
            db.createCollection(DBValues.ROUTE_COLLECTION_NAME);
        } catch (MongoCommandException e) {
            if (e.getErrorCode() != 48) { // Resource already exists
                throw e;
            } else {
                LOG.info(DBValues.ROUTE_COLLECTION_NAME + " already existing, not recreating");
            }
        }

        // Endpoint db
        try {
            db.createCollection(DBValues.ENDPOINT_COLLECTION_NAME);
        } catch (MongoCommandException e) {
            if (e.getErrorCode() != 48) { // Resource already exists
                throw e;
            } else {
                LOG.info(DBValues.ENDPOINT_COLLECTION_NAME + " already existing, not recreating");
            }
        }
    }
}
