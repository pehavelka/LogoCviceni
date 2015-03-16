package cz.havpe.logocviceni.pozicepismena;

import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class Utils {


    /*
    Zobrazení zprávy.
     */
    public static void showText(String aMessage, TextView tv1) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        tv1.setText(dateFormat.format(date) + "-" + aMessage + "\n" + tv1.getText());
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        @SuppressWarnings("UnnecessaryLocalVariable") int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /*
    static public String readStringFromResource(Context ctx, int resourceID) {
        StringBuilder contents = new StringBuilder();
        String sep = System.getProperty("line.separator");

        try {
            InputStream is = ctx.getResources().openRawResource(resourceID);

            BufferedReader input =  new BufferedReader(new InputStreamReader(is), 1024*8);
            try {
                String line;
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    contents.append(sep);
                }
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            return null;
        }
        catch (IOException ex){
            return null;
        }

        return contents.toString();
    }
*/

}
