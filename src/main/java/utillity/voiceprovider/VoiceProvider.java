package utillity.voiceprovider;

import javafx.scene.control.Alert;
import run.AppRun;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class VoiceProvider {

    private static final String css = VoiceProvider.class.getResource("/css/theme.css").toExternalForm();
    private VoiceProvider(){}

    public static int createFileMp3WordPronounce(String word) throws IOException {

        DataOutputStream outStream =    null;
        BufferedWriter writer =         null;
        ByteArrayOutputStream outArray =null;
        FileOutputStream fos =          null;

        try {
            VoiceParameters params = new VoiceParameters(word, Languages.English_GreatBritain);
            params.setCodec(AudioCodec.MP3);
            params.setFormat(AudioFormat.Format_16KHZ.AF_16khz_16bit_stereo);
            params.setBase64(false);
            params.setSSML(false);
            params.setRate(0);

            URL url = new URL("http://api.voicerss.org/");
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
               alertIOErr("Error occurs while opening the connection.");
               return -1;
            }
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setConnectTimeout(60000);
            conn.setDoOutput(true);

            try {
                outStream = new DataOutputStream(conn.getOutputStream());
            } catch (UnknownHostException une) {
                alertUnknownHostExceptionErr();
                return -1;
            }
            writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
            writer.write(buildParameters(params));
            try {
                writer.close();
                outStream.close();
            } catch (IOException e) {
                alertIOErr("Error occurs while close I/O Object.");
                return -1;
            }
            int code = 0;
            try {
                 code = conn.getResponseCode();
            } catch (IOException e) {
               alertStatusErr(Integer.toString(code));
               return -1;
            }
            outArray = new ByteArrayOutputStream();
            InputStream inStream = conn.getInputStream();
            byte[] buffer = new byte[4096];

            int n;
            while((n = inStream.read(buffer)) > 0) {
                outArray.write(buffer, 0, n);
            }
            byte[] response = outArray.toByteArray();
            fos = new FileOutputStream("voice/"+word+".mp3");
            fos.write(response, 0, response.length);


        } finally {
            try {
                if(outStream != null) outStream.close();
                if(writer != null) writer.close();
                if(outArray != null) outArray.close();
                if(fos != null) fos.close();

            } catch (IOException ioe) {
                alertIOErr("Error occurs while close I/O Object.");
                return -1;
            }

        }
        return 1;
    }
    private static String buildParameters(VoiceParameters params) {
        StringBuilder sb = new StringBuilder();
        sb.append("key=" + "6e14b1b2432c43e7be7c670b858f964c");
        sb.append("&src=" + (params.getText() != null ? params.getText() : ""));
        sb.append("&hl=" + (params.getLanguage() != null ? params.getLanguage() : ""));
        sb.append("&r=" + (params.getRate() != null ? params.getRate() : ""));
        sb.append("&c=" + (params.getCodec() != null ? params.getCodec() : ""));
        sb.append("&f=" + (params.getFormat() != null ? params.getFormat() : ""));
        sb.append("&ssml=" + (params.getSSML() != null ? params.getSSML() : ""));
        sb.append("&b64=" + (params.getBase64() != null ? params.getBase64() : ""));
        return sb.toString();
    }
    private static void alertStatusErr(String status) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Status Server HTTP/1.0 "+status);
        alert.setHeaderText("Error status code from an HTTP response message HTTP/1.0+"+status);
        alert.showAndWait();
    }
    private static void alertUnknownHostExceptionErr() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Unknown host Err");
        alert.setHeaderText("Unknown host for connection: check your internet connection!");
        alert.showAndWait();
    }
    private static void alertIOErr(String ioerr) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Input/Output Err");
        alert.setHeaderText(ioerr);
        alert.showAndWait();
    }

}
