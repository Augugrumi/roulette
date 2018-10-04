package routes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.DBValues;
import database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import spark.Route;
import util.ConfigManager;

public class EndpointPutRoute implements Route {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(EndpointPutRoute.class);
    final private static String INGRESS_IP = "ipIngress";
    final private static String EGRESS_IP = "ipEgress";
    final private static String SRC_IP = "ipSrc";
    final private static String DEST_IP = "ipDst";
    final private static String SRC_PORT = "portSrc";
    final private static String DEST_PORT = "portDst";
    final private static String SFC_ID = "idSfc"; // TODO perform a join when creating the object to check sfc existence
    final private static String SOCKET_ID_INGRESS = "socketIdIngress";
    final private static String SOCKET_ID_EGRESS = "socketIdEgress";
    final private static String PROTOCOL = "protocol";

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("Launching " + this.getClass().getName());
        final MongoDatabase db = ConfigManager.getConfig().getDatabase();
        final MongoCollection<Document> endpoints = db.getCollection(DBValues.ENDPOINT_COLLECTION_NAME);
        final JSONObject body = new JSONObject(request.body());
        final EndpointEntry toAdd = new EndpointEntry();

        String objectId = null;
        try {

            toAdd.setDestIP(body.getString(DEST_IP))
                    .setDestPort(body.getInt(DEST_PORT))
                    .setSrcIP(body.getString(SRC_IP))
                    .setSrcPort(body.getInt(SRC_PORT))
                    .setSfcId(body.getString(SFC_ID))
                    .setProtocol(body.getString(PROTOCOL));

            final String ingressIP = body.getString(INGRESS_IP);
            if (ingressIP == null | ingressIP.equals("")) {
                final String egressIP = body.getString(EGRESS_IP);
                final String egressSocketId = body.getString(SOCKET_ID_EGRESS);
                toAdd.setEgressIP(egressIP)
                        .setSocketIdEgress(egressSocketId);
            } else {
                final String ingressSocketId = body.getString(SOCKET_ID_INGRESS);
                toAdd.setIngressIP(ingressIP)
                        .setSocketIdIngress(ingressSocketId);
            }

            final Document oldDoc = endpoints.find(toAdd.build()).first();
            if (oldDoc == null) {

                endpoints.insertOne(toAdd.build());
                objectId = toAdd.build().getObjectId("_id").toHexString();
            } else {
                endpoints.replaceOne(toAdd.build(), oldDoc);
                objectId = oldDoc.getObjectId("_id").toHexString();
            }

        } catch (JSONException e) {
            final ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.ERROR);
            res.add(ResponseCreator.Fields.REASON, e.getMessage());
            return res;
        }

        final ResponseCreator res = new ResponseCreator(ResponseCreator.ResponseType.OK);
        final JSONObject id = new JSONObject();
        id.put("id", objectId);
        res.add(ResponseCreator.Fields.CONTENT, id);
        return res;
    }
}
