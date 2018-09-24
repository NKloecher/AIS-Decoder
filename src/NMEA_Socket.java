import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class NMEA_Socket implements Runnable {
    //TODO error handling for db insertions (value ranges + conflicts?)

    public static void main(String[] args) {
        NMEA_Socket s = new NMEA_Socket();
            s.run();
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("192.168.10.100",2000);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            //noinspection InfiniteLoopStatement
            while (true){
                String line = reader.readLine();
                if (line.startsWith("!")) {
                    System.out.print('\n');
                    NMEA_DB_Operator.decode(line);
                }
                System.out.print(">");
            }
        } catch (IOException| SQLException e) {
            e.printStackTrace();
        }
    }
}
