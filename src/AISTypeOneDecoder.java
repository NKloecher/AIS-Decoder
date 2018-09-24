import java.util.Locale;
import java.util.stream.Stream;

/**
    This class handles message types 1,2,3
 */
public class AISTypeOneDecoder extends AISDecoder {

    private int navStatus; //see enum NavigationStatus
    private double ROT; //rate of turn
    private double SOG; //speed over ground
    private int posAcc; //position accuracy
    private double COG; //course over ground
    private int heading; //true heading
    private int timestamp; //second of UTC timestamp when msg was received
    private int maneuver; //part of a special maneuver

    AISTypeOneDecoder(){}
    AISTypeOneDecoder(String payload, int msgType){
        super(msgType);
        StringBuilder sb = binaryPayloadToStringBuilder(payload);

        //msgType = Integer.parseInt(sb.substring(0,6),2) + "";
        repeats = Integer.parseInt(sb.substring(6,8),2) + "";
        MMSI = String.valueOf(Integer.parseInt(sb.substring(8,38),2));
        navStatus = Integer.parseInt(sb.substring(38,42),2);
        ROT = Integer.parseInt(sb.substring(42,50),2);
        SOG = Integer.parseInt(sb.substring(50,60),2);
        posAcc = Integer.parseInt(sb.substring(60,61),2);

        String binLng = sb.substring(61,89);
        negLng = binLng.startsWith("1");
        lng = Integer.parseInt(binLng,2);

        String binLat = sb.substring(89,116);
        negLat = binLat.startsWith("1");
        lat = Integer.parseInt(binLat,2);

        COG = Integer.parseInt(sb.substring(116,128),2);
        heading = Integer.parseInt(sb.substring(128,137),2);
        timestamp = Integer.parseInt(sb.substring(137,143),2);
        maneuver = Integer.parseInt(sb.substring(143,145),2);

    }

    @Override
    dbClass toDBObject() {
        return new dbClass();
    }

    class dbClass implements AisDbClass{
        int msgType;
        String MMSI;
        double longitude;
        double latitude;
        String navigationStatus;
        String rateOfTurn;
        String speedOverGround;
        String courseOverGround;
        String positionalAccuracy;
        String heading;
        int timestamp;
        String maneuver;
        private dbClass(){
            msgType = Integer.parseInt(AISTypeOneDecoder.this.msgType);
            MMSI = AISTypeOneDecoder.this.MMSI;
            longitude = doubleLng(lng);
            latitude = doubleLat(lat);
            navigationStatus = NAVSTAT[navStatus];
            rateOfTurn = rateOfTurn(ROT);
            speedOverGround = speedOverGround(SOG);
            courseOverGround = calcCourse(COG);
            positionalAccuracy = positionalAccuracy(posAcc);
            heading = calcHeading(AISTypeOneDecoder.this.heading);
            timestamp = AISTypeOneDecoder.this.timestamp;
            maneuver = decodeManeuver(AISTypeOneDecoder.this.maneuver);
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
    public String decode(){
        String navStatus_s,ROT_s,SOG_s,posAcc_s,lng_s,lat_s,COG_s,heading_s,timestamp_s,maneuver_s;

        navStatus_s = NAVSTAT[navStatus];

        ROT_s = rateOfTurn(ROT);
        SOG_s = speedOverGround(SOG);
        posAcc_s = positionalAccuracy(posAcc);

        lng_s = calcLng(lng);
        lat_s = calcLat(lat);

        COG_s = calcCourse(COG);
        heading_s = calcHeading(heading);
        timestamp_s = calcTimestamp(timestamp);
        maneuver_s = decodeManeuver(maneuver);

        return "AISTypeOneDecoder{\n" +
                "msgType='" + msgType + '\'' +
                "\nrepeats='" + repeats + '\'' +
                "\nMMSI='" + MMSI + '\'' +
                "\nnavStatus='" + navStatus_s + '\'' +
                "\nROT='" + ROT_s + '\'' +
                "\nSOG='" + SOG_s + '\'' +
                "\nposAcc='" + posAcc_s + '\'' +
                "\nlng='" + lng_s + '\'' +
                "\nlat='" + lat_s + '\'' +
                "\nCOG='" + COG_s + '\'' +
                "\nheading='" + heading_s + '\'' +
                "\ntimestamp='" + timestamp_s + '\'' +
                "\nmaneuver='" + maneuver_s + '\'' +
                "\n}";

    }




    @Override
    public String toString() {
        return "AISTypeOneDecoder{(encoded) " +
                "msgType='" + msgType + '\'' +
                ", repeats='" + repeats + '\'' +
                ", MMSI='" + MMSI + '\'' +
                ", navStatus='" + navStatus + '\'' +
                ", ROT='" + ROT + '\'' +
                ", SOG='" + SOG + '\'' +
                ", posAcc='" + posAcc + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", COG='" + COG + '\'' +
                ", heading='" + heading + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", maneuver='" + maneuver + '\'' +
                "}";
    }


}
