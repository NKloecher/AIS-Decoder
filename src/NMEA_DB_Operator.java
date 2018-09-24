import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
class NMEA_DB_Operator {
    //private static String pass = "????";
    private static ArrayList<String> multiLineArray = new ArrayList<>();
    private static Connection conn = null;
    static {
        try {
            String db = "jdbc:postgresql://localhost:5432/ais_data";
            String user = "nk";
            conn = DriverManager.getConnection(db, user,"1222");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    static void decode(String ais_message) throws SQLException {

        String[] splitLine = ais_message.split(",");

        int numberOfMultiLines = Integer.parseInt(splitLine[1]);
        int currentNumberInMultiLine = Integer.parseInt(splitLine[2]);

        String payload = splitLine[5];

        //if multiline message, store payload
        if (numberOfMultiLines != 1){
            multiLineArray.add(payload);
            //skip to next in stream if not last message
            if (currentNumberInMultiLine != numberOfMultiLines) return;
        }
        //concatenate payload at last message and clear array
        if (currentNumberInMultiLine == numberOfMultiLines && currentNumberInMultiLine != 1){
            StringBuilder newPayload = new StringBuilder();
            for (String oldPayload : multiLineArray){
                newPayload.append(oldPayload);
            }
            multiLineArray.clear();
            payload = newPayload.toString();
        }

        System.out.println("Payload: " + payload);

        int msgType = getMsgType(payload.charAt(0));
        switch (msgType) {
            case 1:
            case 2:
            case 3:
                decode_one(payload, msgType);
                System.out.println("Successfully inserted " + payload);
                break;
            case 4:
                decode_four(payload, msgType);
                System.out.println("Successfully inserted " + payload);
                break;
            case 5:
                decode_five(payload, msgType);
                System.out.println("Successfully inserted " + payload);
                break;
            case 18:
                decode_eighteen(payload, msgType);
                System.out.println("Successfully inserted " + payload);
                break;
            case 24:
                decode_twentyfour(payload, msgType);
                System.out.println("Successfully inserted " + payload);
                break;
            default:
                System.out.println("You reached the default switch lvl, congratulations!");
                break;
        }

    }

    private static void decode_twentyfour(String payload, int msgType) throws SQLException {
        AISTypeTwentyFourDecoder.dbClass obj_twentyfour = new AISTypeTwentyFourDecoder(payload,msgType).toDBObject();
        if (obj_twentyfour.isTypeA()){
            PreparedStatement p24A = conn.prepareStatement(
            "insert into type_twentyfour (message_type, mmsi, ship_name) " +
                "values (?,?,?) " +
                "on conflict (mmsi) " +
                "do update set " +
                    "ship_name = EXCLUDED.ship_name"
            );
            p24A.setInt(1,obj_twentyfour.msgType);
            p24A.setString(2,obj_twentyfour.MMSI);
            p24A.setString(3,obj_twentyfour.shipName);
            p24A.execute();
            System.out.println("Successfully inserted part A: " + payload);
        }
        else {
            PreparedStatement p24B = conn.prepareStatement(
            "insert into type_twentyfour (message_type, mmsi, ship_type, callsign, vendorid, unit_model_code, serial_number, m_to_bow, m_to_stern, m_to_port, m_to_starboard, mothership_mmsi) " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?) " +
                    "on conflict (mmsi) " +
                    "do update set " +
                    "(ship_type, callsign, vendorid, unit_model_code, serial_number, m_to_bow, m_to_stern, m_to_port, m_to_starboard, mothership_mmsi) " +
                    "= " +
                    "(EXCLUDED.ship_type, EXCLUDED.callsign, EXCLUDED.vendorid, EXCLUDED.unit_model_code, " +
                    "EXCLUDED.serial_number, EXCLUDED.m_to_bow, EXCLUDED.m_to_stern, EXCLUDED.m_to_port, " +
                    "EXCLUDED.m_to_starboard, EXCLUDED.mothership_mmsi)"
            );

            p24B.setInt(1,obj_twentyfour.msgType);
            p24B.setString(2,obj_twentyfour.MMSI);
            p24B.setString(3,obj_twentyfour.shipType);
            p24B.setString(4,obj_twentyfour.callsign);
            p24B.setString(5,obj_twentyfour.vendorID);
            p24B.setInt(6,obj_twentyfour.umc);
            p24B.setInt(7,obj_twentyfour.serialNumber);
            p24B.setInt(8,obj_twentyfour.mToBow);
            p24B.setInt(9,obj_twentyfour.mToStern);
            p24B.setInt(10,obj_twentyfour.mToPort);
            p24B.setInt(11,obj_twentyfour.mToStarboard);
            p24B.setString(12,obj_twentyfour.mothershipMMSI);
            p24B.execute();

            System.out.println("Successfully inserted part B: " + payload);
        }
    }

    private static void decode_eighteen(String payload, int msgType) throws SQLException {
        AISTypeEighteenDecoder.dbClass obj_eighteen = new AISTypeEighteenDecoder(payload,msgType).toDBObject();
        PreparedStatement p18 = conn.prepareStatement(
                "insert into type_eighteen (message_type, mmsi, speed_over_ground, positional_accuracy, longitude, latitude, course_over_ground, heading, timestamp) " +
                        "values (?,?,?,?,?,?,?,?,?) " +
                        "on conflict (mmsi) " +
                        "do update set " +
                        "(speed_over_ground, positional_accuracy, longitude, latitude, course_over_ground, heading, timestamp) " +
                        "= " +
                        "(Excluded.speed_over_ground, Excluded.positional_accuracy, Excluded.longitude, Excluded.latitude, " +
                        "Excluded.course_over_ground, Excluded.heading, Excluded.timestamp)"
        );

        p18.setInt(1,obj_eighteen.msgType);
        p18.setString(2,obj_eighteen.MMSI);
        p18.setString(3,obj_eighteen.speedOverGround);
        p18.setString(4,obj_eighteen.fixQuality);
        p18.setDouble(5,obj_eighteen.longitude);
        p18.setDouble(6,obj_eighteen.latitude);
        p18.setString(7,obj_eighteen.courseOverGround);
        p18.setString(8,obj_eighteen.heading);
        p18.setInt(9,obj_eighteen.timestamp);
        p18.execute();
    }

    private static void decode_five(String payload, int msgType) throws SQLException {
        AISTypeFiveDecoder.dbClass obj_five = new AISTypeFiveDecoder(payload,msgType).toDBObject();
        PreparedStatement p5 =
                conn.prepareStatement("insert into type_five (\"Message_Type\", \"MMSI\", " +
                        "\"AIS_Version\", \"IMO\", \"Call_Sign\", \"Ship_Name\", \"Ship_Type\", " +
                        "\"M_To_Bow\", \"M_To_Stern\", \"M_To_Port\", \"M_To_Starboard\", \"Month\", " +
                        "\"Day\", \"Hour\", \"Minute\", \"Draught\", \"Destination\") " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                        "on conflict (\"MMSI\") " +
                        "do update set " +
                        "(\"AIS_Version\", \"IMO\", " +
                        "\"Call_Sign\", \"Ship_Name\", \"Ship_Type\", " +
                        "\"M_To_Bow\", \"M_To_Stern\", \"M_To_Port\", \"M_To_Starboard\", \"Month\", " +
                        "\"Day\", \"Hour\", \"Minute\", \"Draught\", \"Destination\") " +
                        "= " +
                        "(EXCLUDED.\"AIS_Version\", EXCLUDED.\"IMO\", " +
                        "EXCLUDED.\"Call_Sign\", EXCLUDED.\"Ship_Name\", EXCLUDED.\"Ship_Type\", " +
                        "EXCLUDED.\"M_To_Bow\", EXCLUDED.\"M_To_Stern\", EXCLUDED.\"M_To_Port\", " +
                        "EXCLUDED.\"M_To_Starboard\", EXCLUDED.\"Month\", " +
                        "EXCLUDED.\"Day\", EXCLUDED.\"Hour\", EXCLUDED.\"Minute\", " +
                        "EXCLUDED.\"Draught\", EXCLUDED.\"Destination\")" );

        p5.setInt(1,obj_five.msgType);
        p5.setString(2,obj_five.MMSI);
        p5.setString(3,obj_five.AIS_version);
        p5.setString(4,obj_five.IMO);
        p5.setString(5,obj_five.callsign);
        p5.setString(6,obj_five.vesselName);
        p5.setString(7,obj_five.shipType);
        p5.setInt(8,obj_five.mToBow);
        p5.setInt(9,obj_five.mToStern);
        p5.setInt(10,obj_five.mToPort);
        p5.setInt(11,obj_five.mToStarboard);
        p5.setInt(12,obj_five.month);
        p5.setInt(13,obj_five.day);
        p5.setInt(14,obj_five.hour);
        p5.setInt(15,obj_five.minute);
        p5.setDouble(16,obj_five.draught);
        p5.setString(17,obj_five.destination);
        p5.execute();
    }

    private static void decode_four(String payload, int msgType) throws SQLException {
        AISTypeFourDecoder.dbClass obj_four = new AISTypeFourDecoder(payload,msgType).toDBObject();
        PreparedStatement p4 = conn.prepareStatement(
                "insert into type_four (message_type,mmsi,year,month,day,hour,minute,second,positional_accuracy,longitude,latitude,epfd) " +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?) " +
                        "on conflict (mmsi) " +
                        "do update set " +
                        "(year,month,day,hour,minute,second,positional_accuracy,longitude,latitude,epfd) " +
                        "= " +
                        "(Excluded.year,Excluded.month,Excluded.day,Excluded.hour," +
                        "Excluded.minute,Excluded.second,Excluded.positional_accuracy," +
                        "Excluded.longitude,Excluded.latitude,Excluded.epfd)");
        p4.setInt(1,obj_four.msgType);
        p4.setString(2,obj_four.MMSI);
        p4.setInt(3,obj_four.year);
        p4.setInt(4,obj_four.month);
        p4.setInt(5,obj_four.day);
        p4.setInt(6,obj_four.hour);
        p4.setInt(7,obj_four.minute);
        p4.setInt(8,obj_four.second);
        p4.setString(9,obj_four.fixQuality);
        p4.setDouble(10,obj_four.longitude);
        p4.setDouble(11,obj_four.latitude);
        p4.setString(12,obj_four.epfd);
        p4.execute();
    }

    private static void decode_one(String payload, int msgType) throws SQLException {
        AISTypeOneDecoder.dbClass obj = new AISTypeOneDecoder(payload,msgType).toDBObject();

        PreparedStatement p1 = conn.prepareStatement(
                "INSERT into type_one (\"Message_Type\", \"MMSI\", \"Longitude\", \"Latitude\", \"Navigation_Status\", \"Rate_of_Turn\", \"Speed_over_Ground\", \"Course_over_Ground\", \"Positional_Accuracy\", \"Heading\", \"Timestamp\", \"Maneuver\")" +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?) " +
                        "on conflict (\"MMSI\") " +
                        "do update set " +
                        "(\"Longitude\",\"Latitude\",\"Navigation_Status\",\"Rate_of_Turn\"," +
                        "\"Speed_over_Ground\",\"Course_over_Ground\"," +
                        "\"Positional_Accuracy\",\"Heading\",\"Timestamp\",\"Maneuver\") " +
                        "= (Excluded.\"Longitude\",Excluded.\"Latitude\",Excluded.\"Navigation_Status\"," +
                        "Excluded.\"Rate_of_Turn\"," +
                        "Excluded.\"Speed_over_Ground\",Excluded.\"Course_over_Ground\"," +
                        "Excluded.\"Positional_Accuracy\",Excluded.\"Heading\"," +
                        "Excluded.\"Timestamp\",Excluded.\"Maneuver\")");
        p1.setInt(1,obj.msgType);
        p1.setString(2,obj.MMSI);
        p1.setDouble(3,obj.longitude);
        p1.setDouble(4,obj.latitude);
        p1.setString(5,obj.navigationStatus);
        p1.setString(6,obj.rateOfTurn);
        p1.setString(7,obj.speedOverGround);
        p1.setString(8,obj.courseOverGround);
        p1.setString(9,obj.positionalAccuracy);
        p1.setString(10,obj.heading);
        p1.setInt(11,obj.timestamp);
        p1.setString(12,obj.maneuver);
        p1.execute();
    }

    private static int getMsgType(char msg){
        return  (msg - 48) > 40 ? msg - 48 - 8 : msg - 48;
    }
}
