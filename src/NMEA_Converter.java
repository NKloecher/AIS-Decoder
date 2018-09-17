import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class NMEA_Converter {



    public static void main(String[] args) {

        //TODO Types {1,2,3}, {4}, {5}, {18}, {24} - 1,2,3,4 done
        //TODO handle multiline messages
        //TODO handle padding of bits to line, see multiline messages
        //TODO handle string returns in ascii (fx name)
        //TODO check COG method --  rl.se/aivdm vs libais

        //todo better solution than format %s + replace " " -> "0"
        //todo unit test all this :P
        //type 4 test
        //!AIVDM,1,1,,B,402Fh`1uho;NC0SLE0I6SqQ00D2l,0*64

        //type 5 test --> multiline message
        //!AIVDM,2,1,7,B,533n<?P1S?I0uC?K361ADN3;622222222222220l2P<664000<hCU5iDT855,0*3C
        //!AIVDM,2,2,7,B,AhShE888880,2*4F

        //type 18 test
        //!AIVDM,1,1,,B,B43JRq00LhTWc5VejDI>wwWUoP06,0*29

        //type 24 test
        //!AIVDM,1,1,,B,H0HN<8QLTdTpN22222222222223,2*1B

        try (Stream<String> lines = Files.lines(Paths.get("nmea-sample"))){

            ArrayList<String> multiLineArray = new ArrayList<>();

            lines.limit(18).forEach(line -> {
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
                //concatenate payload at last message
                if (currentNumberInMultiLine == numberOfMultiLines && currentNumberInMultiLine != 1){
                    StringBuilder newPayload = new StringBuilder();
                    for (String l : multiLineArray){
                        newPayload.append(l);
                    }
                    payload = newPayload.toString();
                }

                System.out.println("Payload: " + payload);

                int msgType = getMsgType(payload.charAt(0));
                AISDecoder ais;
                switch (msgType){
                    case 1: case 2: case 3:
                        ais = new AISTypeOneDecoder(payload);
                        System.out.println(ais);
                        System.out.println(ais.decode());
                        break;
                    case 4:
                        ais = new AISTypeFourDecoder(payload);
                        System.out.println(ais);
                        System.out.println(ais.decode());
                        break;
                    case 5:
                        ais = new AISTypeFiveDecoder(payload);
                        System.out.println(ais);
                        break;
                    case 18:
                        System.out.println("==========THIS ONE========");
                        System.out.println("Unhandled message type 18");
                        System.out.println("==========================");
                        break;
                    case 24:
                        System.out.println("Unhandled message type 24");
                        break;
                    default:
                        System.out.println("You reached the default switch lvl, congratulations!");
                        break;
                }

            });

        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    private static int getMsgType(char msg){
        return  (msg - 48) > 40 ? msg - 48 - 8 : msg - 48;
    }

}
