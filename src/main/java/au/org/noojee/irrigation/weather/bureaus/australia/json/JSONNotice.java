
package au.org.noojee.irrigation.weather.bureaus.australia.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONNotice {

    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("copyright_url")
    @Expose
    private String copyrightUrl;
    @SerializedName("disclaimer_url")
    @Expose
    private String disclaimerUrl;
    @SerializedName("feedback_url")
    @Expose
    private String feedbackUrl;

    public String getCopyright() {
        return copyright;
    }

 
    public String getCopyrightUrl() {
        return copyrightUrl;
    }

 
    public String getDisclaimerUrl() {
        return disclaimerUrl;
    }

    public String getFeedbackUrl() {
        return feedbackUrl;
    }

 
    @Override
    public String toString() {
        return ""; // new ToStringBuilder(this).append("copyright", copyright).append("copyrightUrl", copyrightUrl).append("disclaimerUrl", disclaimerUrl).append("feedbackUrl", feedbackUrl).toString();
    }

}
