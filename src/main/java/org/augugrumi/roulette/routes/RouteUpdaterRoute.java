package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.RouteEntry;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import org.augugrumi.roulette.util.ConfigManager;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.augugrumi.roulette.database.DBValues.ROUTE_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.Route.SPI;


public class RouteUpdaterRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(RouteAdderRoute.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.debug("Update route called");
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> routes = db.getCollection(ROUTE_COLLECTION_NAME);
        final String SPId = request.params(SPI);

        Document toUpdate = routes.find(new RouteEntry().addSPI(SPId).build()).first();

        if (toUpdate != null) {
            Route delete = new RouteDeleterRoute();
            JSONObject deleteRes = new JSONObject(delete.handle(request, response).toString());
            if (!deleteRes.getString(ResponseCreator.Fields.RESULT.toString().toLowerCase())
                    .equalsIgnoreCase(ResponseCreator.ResponseType.OK.toString())) {
                // Something went wrong
                return deleteRes;
            }
        }

        Route add = new RouteAdderRoute();

        return add.handle(request, response);
    }
}
