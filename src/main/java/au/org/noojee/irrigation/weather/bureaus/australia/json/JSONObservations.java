
package au.org.noojee.irrigation.weather.bureaus.australia.json;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONObservations {

    @SerializedName("notice")
    @Expose
    private List<JSONNotice> notice = null;
    @SerializedName("header")
    @Expose
    private List<JSONHeader> header = null;
    @SerializedName("data")
    @Expose
    private List<JSONObservation> observations = null;

    public List<JSONNotice> getNotice() {
        return notice;
    }

    public List<JSONHeader> getHeader() {
        return header;
    }

 
    public List<JSONObservation> getObservations() {
        return observations;
    }

  
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("notice", notice)
        		.append("header", header).append("observations", observations).toString();
    }

}
