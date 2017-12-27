
package au.org.noojee.irrigation.weather.bureaus.australia.json;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONHeader {

    @SerializedName("refresh_message")
    @Expose
    private String refreshMessage;
    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("main_ID")
    @Expose
    private String mainID;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("state_time_zone")
    @Expose
    private String stateTimeZone;
    @SerializedName("time_zone")
    @Expose
    private String timeZone;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("state")
    @Expose
    private String state;

    public String getRefreshMessage() {
        return refreshMessage;
    }

  
    public String getID() {
        return iD;
    }

    public String getMainID() {
        return mainID;
    }

  
    public String getName() {
        return name;
    }

  
    public String getStateTimeZone() {
        return stateTimeZone;
    }

  
    public String getTimeZone() {
        return timeZone;
    }

  
    public String getProductName() {
        return productName;
    }

    public String getState() {
        return state;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("refreshMessage", refreshMessage).append("iD", iD).append("mainID", mainID).append("name", name).append("stateTimeZone", stateTimeZone).append("timeZone", timeZone).append("productName", productName).append("state", state).append("\n").toString();
    }

}
