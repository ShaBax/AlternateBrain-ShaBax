
package innovationsquare.com.alternatebrain.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("sector")
    @Expose
    public String sector;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("phonenumber")
    @Expose
    public String phonenumber;
    @SerializedName("referral")
    @Expose
    public String referral;
    @SerializedName("api_key")
    @Expose
    public String apiKey;

}
