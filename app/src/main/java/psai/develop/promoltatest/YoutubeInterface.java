package psai.develop.promoltatest;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by psai on 10/3/2016.
 */

public interface YoutubeInterface {
    @GET("v3/videos/?")
    Observable<JsonObject> getVideoDetails(@Query("id") String id,
                                           @Query("key") String key,
                                           @Query("part") String parts);
}
