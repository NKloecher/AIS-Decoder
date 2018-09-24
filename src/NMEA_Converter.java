import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class NMEA_Converter {



    public static void main(String[] args) {

        //TODO create tables for msgTypes + prepared statements

        //TODO handle padding of bits to line, see multiline messages
        //TODO checksum messages -> validate before decoding!
        //TODO db object inner classes (not the prettiest, but oh well)

        //Todo check COG method --  rl.se/aivdm vs libais
        //todo extra flags + radio status
        //todo finalize ship types
        //todo better solution than format %s + replace " " -> "0"
        //todo unit test all this :P

        String user = "nk";
        String pass = "????";
        String db = "jdbc:postgresql://localhost:5432/ais_data";

        //alternatively make into arg call for jar
        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\picit_nk\\IdeaProjects\\NMEA_Conversion\\nmea-sample"));
             Connection conn = DriverManager.getConnection(db,user,"1222");
//            BufferedWriter type1 = Files.newBufferedWriter(Paths.get("Type1.txt"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            //more writers... but still need catches inside switch... because lambda
            ){


            ArrayList<String> multiLineArray = new ArrayList<>();

            lines.limit(8).forEach(line -> {
                String[] splitLine = line.split(",");

                int numberOfMultiLines = Integer.parseInt(splitLine[1]);
                int currentNumberInMultiLine = Integer.parseInt(splitLine[2]);
                //todo int bitPadding = splitLine[3].equals("") ? 0 : Integer.parseInt(splitLine[3]);

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
                AISDecoder ais;
                    switch (msgType) {
                        case 1:
                        case 2:
                        case 3:
                            ais = new AISTypeOneDecoder(payload,msgType);
                            System.out.println(ais);
                            System.out.println(ais.decode());
                            AISTypeOneDecoder.dbClass obj = ((AISTypeOneDecoder) ais).toDBObject();
                            try {
                                PreparedStatement p = conn.prepareStatement(
                                        "INSERT into type_one (\"Message_Type\", \"MMSI\", \"Longitude\", \"Latitude\", \"Navigation_Status\", \"Rate_of_Turn\", \"Speed_over_Ground\", \"Course_over_Ground\", \"Positional_Accuracy\", \"Heading\", \"Timestamp\", \"Maneuver\")" +
                                                "values (?,?,?,?,?,?,?,?,?,?,?,?)" +
                                                "on conflict (\"MMSI\")" +
                                                "do update set \"MMSI\" = Excluded.\"MMSI\"");
                                p.setInt(1,obj.msgType);
                                p.setString(2,obj.MMSI);
                                p.setDouble(3,obj.longitude);
                                p.setDouble(4,obj.latitude);
                                p.setString(5,obj.navigationStatus);
                                p.setString(6,obj.rateOfTurn);
                                p.setString(7,obj.speedOverGround);
                                p.setString(8,obj.courseOverGround);
                                p.setString(9,obj.positionalAccuracy);
                                p.setString(10,obj.heading);
                                p.setInt(11,obj.timestamp);
                                p.setString(12,obj.maneuver);
                                p.execute();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 4:
                            ais = new AISTypeFourDecoder(payload,msgType);
                            System.out.println(ais);
                            System.out.println(ais.decode());
                            break;
                        case 5:
                            ais = new AISTypeFiveDecoder(payload,msgType);
                            System.out.println(ais);
                            System.out.println(ais.decode());
                            break;
                        case 18:
                            ais = new AISTypeEighteenDecoder(payload,msgType);
                            System.out.println(ais);
                            System.out.println(ais.decode());
                            break;
                        case 24:
                            ais = new AISTypeTwentyFourDecoder(payload,msgType);
                            System.out.println(ais.decode());
                            break;
                        default:
                            System.out.println("You reached the default switch lvl, congratulations!");
                            break;
                    }
            });

        }catch (IOException|SQLException ex){
            ex.printStackTrace();
        }

    }

    private static int getMsgType(char msg){
        return  (msg - 48) > 40 ? msg - 48 - 8 : msg - 48;
    }

}
