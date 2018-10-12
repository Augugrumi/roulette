package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.RouteEntry;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import org.augugrumi.roulette.util.ConfigManager;

import static org.augugrumi.roulette.database.DBValues.ROUTE_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.Route.SI;
import static org.augugrumi.roulette.routes.util.ParamsName.Route.SPI;


public class RouteUpdaterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.debug("Update route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(ROUTE_COLLECTION_NAME);
        final String SPId = request.params(SPI);
        final JSONObject body = new JSONObject(request.body());
        final JSONArray addressList = body.getJSONArray(SI);
        ResponseCreator res;

        Document toUpdate = routes.find(new RouteEntry().addSPI(SPId).build()).first();

        if (toUpdate == null) {
            LOG.debug("Miss");

            Route add = new RouteAdderRoute();

            return add.handle(request, response);
        }  else {
            LOG.debug("Hit");
            Document query = new Document();
            query.append(SPI, SPId);
            toUpdate.put(SI, addressList.toString());
            routes.replaceOne(query, toUpdate);
            res = new ResponseCreator(ResponseCreator.ResponseType.OK);

        }

        return res;
    }
}
