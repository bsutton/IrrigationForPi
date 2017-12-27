
package au.org.noojee.irrigation.weather.bureaus.australia.json;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONWeatherStationData {

    @SerializedName("observations")
    @Expose
    private JSONObservations observations;

    public JSONObservations getObservations() {
        return observations;
    }

    public void setObservations(JSONObservations observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("observations", observations).toString();
    }

}
