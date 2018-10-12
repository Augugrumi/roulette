package org.augugrumi.roulette.routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.augugrumi.roulette.database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.augugrumi.roulette.routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import org.augugrumi.roulette.util.ConfigManager;

import static org.augugrumi.roulette.database.DBValues.ENDPOINT_COLLECTION_NAME;
import static org.augugrumi.roulette.routes.util.ParamsName.Endpoint.*;
import static org.augugrumi.roulette.routes.util.ParamsName.MONGO_ID;

public class EndpointPutRoute implements Route {

    final public static Logger LOG = ConfigManager.getConfig().getApplicationLogger(EndpointPutRoute.class);

    // TODO perform a join when creating the object to check sfc existence
    @Override
    public Object handle(Request request, Response response) {


        try {
            LOG.debug("Launching " + this.getClass().getName());
            final MongoDatabase db = ConfigManager.getConfig().getDatabase();
            final MongoCollection<Document> endpoints = db.getCollection(ENDPOINT_COLLECTION_NAME);
            LOG.debug("Data received:\n" + request.body());
            final JSONObject body = new JSONObject(request.body());
            final EndpointEntry toAdd = new EndpointEntry();

            String objectId = null;

            toAdd.setDestIP(body.getString(DEST_IP))
                    .setDestPort(body.getInt(DEST_PORT))
                    .setSrcIP(body.getString(SRC_IP))
                    .setSrcPort(body.getInt(SRC_PORT))
                    .setSfcId(body.getString(SFC_ID))
                    .setProtocol(body.getString(PROTOCOL));

            final String ingressIP = body.optString(INGRESS_IP, null);
            if (ingressIP == null || ingressIP.equals("")) {
                // Ingress is null, so the request it's coming from an Egress endpoint
                final String egressIP = body.getString(EGRESS_IP);
                final String egressSocketId = body.getString(SOCKET_ID_EGRESS);
                toAdd.setEgressIP(egressIP)
                        .setSocketIdEgress(egressSocketId);
            } else {
                // Ingress is set, so the request it's coming from an Ingress endpoint
                final String ingressSocketId = body.getString(SOCKET_ID_INGRESS);
                toAdd.setIngressIP(ingressIP)
                        .setSocketIdIngress(ingressSocketId);
            }

            final Document oldDoc = endpoints.find(toAdd.build()).first();
            if (oldDoc == null) {

                endpoints.insertOne(toAdd.build());
                objectId = toAdd.build().getObjectId(MONGO_ID).toHexString();
            } else {
                endpoints.replaceOne(toAdd.build(), oldDoc);
                objectId = oldDoc.getObjectId(MONGO_ID).toHexString();
            }

            final ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.OK);
            final JSONObject id = new JSONObject();
            id.put("id", objectId);
            res.add(ResponseCreator.Fields.CONTENT, id);
            return res;

        } catch (JSONException e) {
            final ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, e.getMessage());
            return res;
        }
    }
}
