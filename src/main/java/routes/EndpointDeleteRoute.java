package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

import static database.DBValues.ENDPOINT_COLLECTION_NAME;
import static routes.util.ParamsName.MONGO_ID;

public class EndpointDeleteRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(EndpointDeleteRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("Launching " + this.getClass().getName());


        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> endpoints = db.getCollection(ENDPOINT_COLLECTION_NAME);

        endpoints.deleteOne(new EndpointEntry().addId(new ObjectId(request.params(MONGO_ID))).build());

        return new ResponseCreator(ResponseCreator.ResponseType.OK);
    }
}
