public class AISTypeTwentyFourDecoder extends AISDecoder{

    private int partNumber;
    //Part A
    private String shipName;
    //Part B
    private int shipType;
    private String vendorID;
    private int unitModelCode;
    private int serialNumber;
    private String callsign;
    private int mToBow;
    private int mToStern;
    private int mToPort;
    private int mToStarboard;
    private int mothershipMMSI;

    AISTypeTwentyFourDecoder(String payload, int msgType){
        super(msgType);
        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        //msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));

        partNumber = Integer.parseInt(sb.substring(38,40),2);
        if (partNumber == 0){ //A
            shipName = binaryAscii8BitToString(sb.substring(40,160));
        }
        else if (partNumber == 1){ //B
            shipType = Integer.parseInt(sb.substring(40,48),2);
            vendorID = binaryAscii8BitToString(sb.substring(48,66));
            unitModelCode = Integer.parseInt(sb.substring(66,70),2);
            serialNumber = Integer.parseInt(sb.substring(70,90),2);
            callsign = binaryAscii8BitToString(sb.substring(90,132));

            if (isAuxillaryVessel(MMSI)){
                mothershipMMSI = Integer.parseInt(sb.substring(132,162),2);
            }else {
                mToBow = Integer.parseInt(sb.substring(132,141),2);
                mToStern = Integer.parseInt(sb.substring(141,150),2);
                mToPort = Integer.parseInt(sb.substring(150,156),2);
                mToStarboard = Integer.parseInt(sb.substring(156,162),2);
            }
        }
    }



    @Override
    dbClass toDBObject() {
        return new dbClass();
    }
    class dbClass implements AisDbClass {
        int msgType;
        String MMSI;
        String shipName;
        String shipType;
        String callsign;
        String vendorID;
        int umc;
        int serialNumber;
        int mToBow;
        int mToStern;
        int mToPort;
        int mToStarboard;
        String mothershipMMSI;
        private dbClass(){
            msgType = Integer.parseInt(AISTypeTwentyFourDecoder.this.msgType);
            MMSI = AISTypeTwentyFourDecoder.this.MMSI;
            shipName = AISTypeTwentyFourDecoder.this.shipName;
            shipType = SHIP_TYPE[AISTypeTwentyFourDecoder.this.shipType];
            callsign = AISTypeTwentyFourDecoder.this.callsign;
            vendorID = AISTypeTwentyFourDecoder.this.vendorID;
            umc = unitModelCode;
            serialNumber = AISTypeTwentyFourDecoder.this.serialNumber;
            mToBow = AISTypeTwentyFourDecoder.this.mToBow;
            mToStern = AISTypeTwentyFourDecoder.this.mToStern;
            mToPort = AISTypeTwentyFourDecoder.this.mToStern;
            mToStarboard = AISTypeTwentyFourDecoder.this.mToStarboard;
            mothershipMMSI = String.valueOf(AISTypeTwentyFourDecoder.this.mothershipMMSI);
        }
        boolean isTypeA(){
            return partNumber == 0;
        }
    }

    @Override
    String decode() {
        String type_s;
        type_s = SHIP_TYPE[shipType];
        String start = String.format(
                "AISTypeTwentyFourDecoder{\n" +
                        "Message Type='%s'\n" +
                        "Repeats='%s'\n" +
                        "MMSI='%s'",
                        msgType,repeats,MMSI);

        if (partNumber == 0){
            return String.format(
                    "%s\n" +
                    "Ship Name='%s'\n" +
                    "}",
                    start, shipName);
        }
        else if (partNumber == 1){
            String partBmid = String.format(
                                "%s\n" +
                                "Ship Type='%s'\n" +
                                "Vendor ID='%s'\n" +
                                "Unit Model Code='%s'\n" +
                                "Serial Number='%s'\n" +
                                "Call Sign='%s'",
                                start, type_s, vendorID, unitModelCode, serialNumber, callsign);
            if (isAuxillaryVessel(MMSI)){
                return String.format(
                        "%s\n" +
                        "Mothership MMSI='%s'\n" +
                        "}",
                        partBmid,mothershipMMSI
                );
            }else {
                return String.format(
                        "%s\n" +
                        "Length='%s'\n" +
                        "Width='%s'\n" +
                        "}",
                        partBmid,mToBow+mToStern,mToPort+mToStarboard
                );
            }
        }
        else return "Wrong kinda message";
    }

    @Override
    public String toString() {
        return "AIS type 24 message, please decode";
    }
}
