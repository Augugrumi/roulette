package database.entrybuilders;

public class EndpointEntry extends AbsEntryBuilder {

    final private static String INGRESS_IP = "ipIngress";
    final private static String EGRESS_IP = "ipEgress";
    final private static String SRC_IP = "ipSrc";
    final private static String DEST_IP = "ipDst";
    final private static String SRC_PORT = "portSrc";
    final private static String DEST_PORT = "portDst";
    final private static String SFC_ID = "idSfc";
    final private static String SOCKET_ID_INGRESS = "socketIdIngress";
    final private static String SOCKET_ID_EGRESS = "socketIdEgress";
    final private static String PROTOCOL = "protocol";

    //final private JSONObject entry;

    public EndpointEntry() {
        super();

        //entry = new JSONObject();
    }

    public interface ProtocolType {
        String TCP = "tcp";
        String UDP = "udp";
    }
    public EndpointEntry setIngressIP(String ingressIP) {
        entry.append(INGRESS_IP, ingressIP);
        return this;
    }
    public EndpointEntry setEgressIP(String egressIP) {
        entry.append(EGRESS_IP, egressIP);
        return this;
    }
    public EndpointEntry setSrcIP(String srcIP) {
        entry.append(SRC_IP, srcIP);
        return this;
    }
    public EndpointEntry setDestIP(String destIP) {
        entry.append(DEST_IP, destIP);
        return this;
    }
    public EndpointEntry setSrcPort(int srcPort) {
        entry.append(SRC_PORT, srcPort);
        return this;
    }
    public EndpointEntry setDestPort(int destPort) {
        entry.append(DEST_PORT, destPort);
        return this;
    }
    public EndpointEntry setSfcId(String sfcId) {
        entry.append(SFC_ID, sfcId);
        return this;
    }
    public EndpointEntry setSocketIdIngress(String socketIdIngress) {
        entry.append(SOCKET_ID_INGRESS, socketIdIngress);
        return this;
    }
    public EndpointEntry setSocketIdEgress(String socketIdEgress) {
        entry.append(SOCKET_ID_EGRESS, socketIdEgress);
        return this;
    }
    public EndpointEntry setProtocol(String type) {
        if (ProtocolType.TCP.equals(type) | ProtocolType.UDP.equals(type)) {
            entry.append(PROTOCOL, type);
        }
        return this;
    }
}
