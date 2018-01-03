
package innovationsquare.com.alternatebrain.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResponse {

    @SerializedName("images")
    @Expose
    public List<Image> images = null;
    @SerializedName("error")
    @Expose
    public Boolean error;

}
