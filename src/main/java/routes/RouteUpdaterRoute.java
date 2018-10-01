package routes;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DBValues;
import database.entrybuilders.RouteEntry;
import org.bson.BsonDocument;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class RouteUpdaterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);
    final private static String SPI_PARAM_NAME = "spi";
    final private static String SI_PARAM_NAME = "si";

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.info("Update route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(DBValues.COLLECTION_NAME);
        final String SPId = request.params(SPI_PARAM_NAME);
        JSONObject body = new JSONObject(request.body());
        final JSONArray addressList = body.getJSONArray(SI_PARAM_NAME);
        ResponseCreator res;

        final MongoCollection<Document> collection = db.getCollection(DBValues.COLLECTION_NAME);

        Document toUpdate = routes.find(new RouteEntry().addSPI(SPId).build()).first();

        if (toUpdate == null) {
            LOG.info("Miss");
            res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, "Route does not existing");
        }  else {
            LOG.info("Hit");
            Document query = new Document();
            query.append(SPI_PARAM_NAME, SPId);
            toUpdate.put(SI_PARAM_NAME, addressList);
            routes.replaceOne(query, toUpdate);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);

        }


        return res;
    }
}
