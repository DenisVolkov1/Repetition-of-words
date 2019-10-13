package utillity.voiceprovider;

public class VoiceParameters {

    private String _text;
    private String _language;
    private Integer _rate;
    private String _codec;
    private String _format;
    private Boolean _ssml;
    private Boolean _base64;

    public VoiceParameters(String text, String language) {
        this._text = text;
        this._language = language;
    }

    public String getText() {
        return this._text;
    }

    public void setText(String value) {
        this._text = value;
    }

    public String getLanguage() {
        return this._language;
    }

    public void setLanguage(String value) {
        this._language = value;
    }

    public Integer getRate() {
        return this._rate;
    }

    public void setRate(Integer value) {
        this._rate = value;
    }

    public String getCodec() {
        return this._codec;
    }

    public void setCodec(String value) {
        this._codec = value;
    }

    public String getFormat() {
        return this._format;
    }

    public void setFormat(String value) {
        this._format = value;
    }

    public Boolean getSSML() {
        return this._ssml;
    }

    public void setSSML(Boolean value) {
        this._ssml = value;
    }

    public Boolean getBase64() {
        return this._base64;
    }

    public void setBase64(Boolean value) {
        this._base64 = value;
    }

}
