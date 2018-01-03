
package innovationsquare.com.alternatebrain.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("attachment")
    @Expose
    public String attachment;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;

    @SerializedName("place_name")
    @Expose
    public String place_name;

    @SerializedName("location")
    @Expose
    public String placeLocation;

    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("remark")
    @Expose
    public String remark;

    @SerializedName("bill_date")
    @Expose
    public String bill_date;

    public boolean isSelected = false;

}
