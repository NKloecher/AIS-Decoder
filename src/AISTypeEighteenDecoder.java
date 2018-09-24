public class AISTypeEighteenDecoder extends AISDecoder{

    private double SOG;
    private int posAcc;
    private double COG;
    private int heading;
    private int timestamp;
    //todo + multiple flags

    AISTypeEighteenDecoder(String payload, int msgType){
        super(msgType);
        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        //msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));

        SOG = Integer.parseInt(sb.substring(46,56),2);
        posAcc = Integer.parseInt(sb.substring(56,57),2);

        String binLng = sb.substring(57,85);
        negLng = binLng.startsWith("1");
        lng = Integer.parseInt(binLng,2);

        String binLat = sb.substring(85,112);
        negLat = binLat.startsWith("1");
        lat = Integer.parseInt(binLat,2);

        COG = Integer.parseInt(sb.substring(112,124),2);
        heading = Integer.parseInt(sb.substring(124,133),2);
        timestamp = Integer.parseInt(sb.substring(133,139),2);

        //flag stuff

    }

    @Override
    dbClass toDBObject() {return new dbClass();}
    class dbClass implements AisDbClass {
        int msgType;
        String MMSI;
        String speedOverGround;
        String fixQuality;
        double longitude;
        double latitude;
        String courseOverGround;
        String heading;
        int timestamp;
        private dbClass() {
            msgType = Integer.parseInt(AISTypeEighteenDecoder.this.msgType);
            MMSI = AISTypeEighteenDecoder.this.MMSI;
            speedOverGround = speedOverGround(SOG);
            fixQuality = positionalAccuracy(posAcc);
            longitude = doubleLng(lng);
            latitude = doubleLat(lat);
            courseOverGround = calcCourse(COG);
            heading = calcHeading(AISTypeEighteenDecoder.this.heading);
            timestamp = AISTypeEighteenDecoder.this.timestamp;
        }
        double doubleLat(double lat){
            int latitude = (int) lat;
            double latDbl = lat / 600000.0;
            if (negLat){
                latitude ^= AISDecoder.LAT_ONES;
                latitude += 1;
                latitude = -latitude;
                latDbl = latitude / 600000.0;
            }
            return latDbl;
        }
        double doubleLng(double lng){
            int longitude = (int) lng;
            double lngDbl = lng / 600000.0;
            if (negLng){
                longitude ^= LNG_ONES;
                longitude += 1;
                longitude = -longitude;
                lngDbl = longitude / 600000.0;
            }
            return lngDbl;
        }
    }


    @Override
    String decode() {
        String sog_s,acc_s,lng_s,lat_s,cog_s,heading_s;
        sog_s = speedOverGround(SOG);
        acc_s = positionalAccuracy(posAcc);
        lng_s = calcLng(lng);
        lat_s = calcLat(lat);
        cog_s = calcCourse(COG);
        heading_s = calcHeading(heading);
        return String.format(
                "AISTypeEighteenDecoder{\n" +
                        "msgType='%s'\n" +
                        "repeats='%s'\n" +
                        "MMSI='%s'\n" +
                        "SOG='%s'\n" +
                        "Accuracy='%s'\n" +
                        "Longitude='%s'\n" +
                        "Latitude='%s'\n" +
                        "COG='%s'\n" +
                        "Heading='%s'\n" +
                        "Timestamp='%s'\n" +
                        "}",
                msgType,repeats,MMSI,sog_s,acc_s,lng_s,lat_s,cog_s,heading_s,timestamp);
    }


    @Override
    public String toString() {
        return "AISTypeEighteenDecoder{" +
                "SOG=" + SOG +
                ", posAcc=" + posAcc +
                ", COG=" + COG +
                ", heading=" + heading +
                ", timestamp=" + timestamp +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
