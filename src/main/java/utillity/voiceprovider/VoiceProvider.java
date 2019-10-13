package utillity.voiceprovider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VoiceProvider {

    private VoiceProvider(){}

    public static void createFileMp3WordPronounce(String word) {

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
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setConnectTimeout(60000);
            conn.setDoOutput(true);

            outStream = new DataOutputStream(conn.getOutputStream());
            writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
            writer.write(buildParameters(params));
            writer.close();
            outStream.close();

            if (conn.getResponseCode() != 200) {
                throw new Exception(conn.getResponseMessage());
            } else {
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
            }
        } catch (Exception e) {
            System.out.println("Sound input stream ERROR!!!");
        } finally {
            try {
                if(outStream != null) outStream.close();
                if(writer != null) writer.close();
                if(outArray != null) outArray.close();
                if(fos != null) fos.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
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
}
