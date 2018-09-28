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

        try {

            db.createCollection(DBValues.COLLECTION_NAME);
        } catch (MongoCommandException e) {
            if (e.getErrorCode() != 48) { // Resource already exists
                throw e;
            }
        }



                /*.subscribe(new Subscriber<Success>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(Success success) {
                LOG.info("Collection " + DBValues.COLLECTION_NAME + " set up");
                syncOP.complete(null);
            }

            @Override
            public void onError(Throwable throwable) {
                LOG.error("Impossible to create collection - already existing?");
            }

            @Override
            public void onComplete() {
            }
        });*/
    }
}
