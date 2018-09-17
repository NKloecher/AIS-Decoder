public class AISTypeFiveDecoder extends AISDecoder{

    private int ais_version;
    private String IMO;
    private String callsign;
    private String vesselName;
    private int shipType;
    private int mToBow;
    private int mToStern;
    private int mToPort;
    private int mToStarboard;
    private int epfd;
    private int ETA_month;
    private int ETA_day;
    private int ETA_hour;
    private int ETA_minute;
    private double draught;
    private String destination;

    AISTypeFiveDecoder(String payload){

        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));

        ais_version = Integer.parseInt(sb.substring(38,40),2);
        IMO = Integer.parseInt(sb.substring(40,70),2) + "";

        //todo handle strings
        //0-31 + 64 || 32-63 ==
        callsign = binaryAscii6ToString(sb.substring(70,112));
        vesselName = binaryAscii6ToString(sb.substring(112,232));

        shipType = Integer.parseInt(sb.substring(232,240),2);
        mToBow = Integer.parseInt(sb.substring(240,249),2);
        mToStern = Integer.parseInt(sb.substring(249,258),2);
        mToPort = Integer.parseInt(sb.substring(258,264),2);
        mToStarboard = Integer.parseInt(sb.substring(264,270),2);

        epfd = Integer.parseInt(sb.substring(270,274),2);
        //todo error handling
        ETA_month = Integer.parseInt(sb.substring(274,278),2);
        ETA_day = Integer.parseInt(sb.substring(278,283),2);
        ETA_hour = Integer.parseInt(sb.substring(283,288),2);
        ETA_minute = Integer.parseInt(sb.substring(288,294),2);
        //todo meters/10?
        draught = Integer.parseInt(sb.substring(294,302),2);

        //todo handle string + variable bit length
        destination = binaryAscii6ToString(sb.substring(302,422));

    }


    @Override
    String decode() {
        //todo pretty print
        return null;
    }


    @Override
    public String toString() {
        return "AISTypeFiveDecoder{\n" +
                "ais_version=" + ais_version +
                ", \nIMO='" + IMO + '\'' +
                ", \ncallsign='" + callsign + '\'' +
                ", \nvesselName='" + vesselName + '\'' +
                ", \nshipType=" + shipType +
                ", \nmToBow=" + mToBow +
                ", \nmToStern=" + mToStern +
                ", \nmToPort=" + mToPort +
                ", \nmToStarboard=" + mToStarboard +
                ", \nepfd=" + epfd +
                ", \nETA_month=" + ETA_month +
                ", \nETA_day=" + ETA_day +
                ", \nETA_hour=" + ETA_hour +
                ", \nETA_minute=" + ETA_minute +
                ", \ndraught=" + draught +
                ", \ndestination='" + destination + '\'' +
                "\n}";
    }
}
