package routes;

import database.entrybuilders.EndpointEntry;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import routes.util.ResponseCreator;
import spark.Request;
import spark.Response;
import util.ConfigManager;

import static routes.util.ParamsName.Endpoint.*;

public class EndpointIngressUpdateRoute extends EndpointAbsUpdateRoute {

    final private static Logger LOG = ConfigManager.getConfig().getApplicationLogger(EndpointIngressUpdateRoute.class);

    @Override
    public Object handle(Request request, Response response) {

        LOG.debug("Launching " + this.getClass().getName());

        final JSONObject body = new JSONObject(request.body());
        final EndpointEntry data = new EndpointEntry()
                .setProtocol(request.params(PROTOCOL))
                .setSocketIdEgress(request.params(SOCKET_ID_EGRESS))
                .setEgressIP(request.params(EGRESS_IP))
                .setSrcIP(request.params(SRC_IP))
                .setDestIP(request.params(DEST_IP))
                .setSrcPort(Integer.parseInt(request.params(SRC_PORT)))
                .setDestPort(Integer.parseInt(request.params(DEST_PORT)))
                .setSfcId(request.params(SFC_ID));

        final Document queryResult = endpoints.find(data.build()).first();

        LOG.info("Data value:\n" + data.build().toString());

        final ResponseCreator stringCheckRes = voidCheck(body);

        if (stringCheckRes != null) {
            return stringCheckRes;
        }
        return checkAndUpdate(queryResult, data, body, toUpdate -> toUpdate.setIngressIP(body.getString(INGRESS_IP))
                .setSocketIdIngress(body.getString(SOCKET_ID_INGRESS))).getRes();
    }
}
