package psai.develop.promoltatest;


import rx.Observable;

import psai.develop.promoltatest.BaymackResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by psai on 10/2/2016.
 */

public interface BaymackInterface {
    @GET("test/andriodTest.php?")
    Observable<BaymackResponse> getVideoIds(@Query("key") String key);
}
