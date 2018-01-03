
package innovationsquare.com.alternatebrain.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessResponse {

    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("message")
    @Expose
    public String message;

}
