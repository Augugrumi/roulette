package routes.util;

public interface ParamsName {

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
