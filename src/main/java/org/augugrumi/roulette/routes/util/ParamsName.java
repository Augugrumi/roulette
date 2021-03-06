package org.augugrumi.roulette.routes.util;

public interface ParamsName {

    String MONGO_ID = "_id";

    interface Configuration {
        String DEFAULT_INGRESS = "defaultIngress";
        String DEFAULT_EGRESS = "defaultEgress";
    }

    interface Route {

        String SPI="spi";
        String SI="si";

    }

    interface Endpoint {

        String INGRESS_IP = "ipIngress";
        String EGRESS_IP = "ipEgress";
        String SRC_IP = "ipSrc";
        String DEST_IP = "ipDst";
        String SRC_PORT = "portSrc";
        String DEST_PORT = "portDst";
        String SFC_ID = "idSfc";
        String SOCKET_ID_INGRESS = "socketIdIngress";
        String SOCKET_ID_EGRESS = "socketIdEgress";
        String PROTOCOL = "protocol";
    }
}
