package innovationsquare.com.alternatebrain.utils;

import com.google.gson.Gson;

/**
 * Created by macbookpro on 13/12/2017.
 */

public class GsonFactory {
    private static final GsonFactory ourInstance = new GsonFactory();
    private Gson gson;

    public static GsonFactory getInstance() {
        return ourInstance;
    }

    public Gson getGson() {
        return gson;
    }

    private GsonFactory() {
        gson = new Gson();
    }
}
