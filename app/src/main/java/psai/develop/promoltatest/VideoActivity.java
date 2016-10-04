package psai.develop.promoltatest;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import psai.develop.promoltatest.databinding.ActivityVideoBinding;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String BAYMACK_KEY = "3b12383b4c2a228211955d56170ce0f5";
    private static final String API_KEY = "AIzaSyBuzO8-Mg88mILQC5CVlLJPXyp2x-PXjaI";
    private static final String part_KEY = "snippet,statistics";
    private static final int timeLimit = 30;
    private static int tickTimer = 0;   // to keep track of time
    private static int videoIndex = 0;  // current video index
    private Subscription subscription;  // subscription for baymack server

    private List<String> videoIds = new ArrayList<String>();
    private YouTubePlayerView youTubePlayerView;

    /*private ProgressBar progressBar;
    private TextView timerTV;
    private CountDownTimer countDownTimer;*/

    private ImageView imageView;
    /*private TextView channelTitleTV;
    private TextView viewsTV;
    private TextView titleTV;
    private TextView publishTV;*/
    private uidata bindingData;
    ActivityVideoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_video);
        bindingData = new uidata();
        binding.setData(bindingData);

        //channelTitleTV = (TextView) findViewById(R.id.channelTV);
        //viewsTV = (TextView) findViewById(R.id.viewsValueTV);
        //titleTV = (TextView) findViewById(R.id.titleTV);
        //publishTV = (TextView) findViewById(R.id.timeTV);
        imageView = (ImageView) findViewById(R.id.imageView);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        //timerTV = (TextView) findViewById(R.id.timerTV);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get Video List from Baymack Server
        BaymackInterface baymackService = BaymackClient.getClient().create(BaymackInterface.class);
        Observable<BaymackResponse> data = baymackService.getVideoIds(BAYMACK_KEY);
        subscription = baymackService.getVideoIds(BAYMACK_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaymackResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.i("Completed", "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error!", e.toString());
                    }

                    @Override
                    public void onNext(BaymackResponse baymackResponse) {
                        //Log.e("Video Ids:", baymackResponse.getData().toString());
                        videoIds = baymackResponse.getData();
                        youTubePlayerView.initialize(API_KEY, VideoActivity.this);
                    }
                });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if(!b) {
            int size = videoIds.size();
            if(size > 0) {
                tickTimer = 0;
                // Start playing videos on successfull initialization
                playVideos(youTubePlayer);
            }
        }
    }

    private void playVideos(final YouTubePlayer youTubePlayer){
        if(videoIndex < videoIds.size()){
            youTubePlayer.loadVideo(videoIds.get(videoIndex));
            //youTubePlayer.cueVideo(videoIds.get(videoIndex));
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            // Get video Details
            YoutubeInterface youtubeService = YoutubeClient.getClient().create(YoutubeInterface.class);
            Observable<JsonObject> videoData = youtubeService.getVideoDetails(videoIds.get(videoIndex),API_KEY,
                    part_KEY);

            videoData.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<JsonObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Log.e("Video Details Error:", e.toString());
                        }

                        @Override
                        public void onNext(JsonObject jsonObject) {
                            //Log.e("Video Details: ", jsonObject.toString());
                            JsonElement items = jsonObject.get("items");
                            JsonArray itemsArray = items.getAsJsonArray();

                            JsonElement stats = itemsArray.get(0).getAsJsonObject().get("statistics");
                            //Log.e("Video statistics: ", stats.getAsJsonObject().get("viewCount").getAsString());
                            //viewsTV.setText(stats.getAsJsonObject().get("viewCount").getAsString());
                            bindingData.setViewsValue(stats.getAsJsonObject().get("viewCount").getAsString());
                            JsonElement snippet = itemsArray.get(0).getAsJsonObject().get("snippet");
                            //channelTitleTV.setText(snippet.getAsJsonObject().get("channelTitle").getAsString());
                            bindingData.setChannelTitle(snippet.getAsJsonObject().get("channelTitle").getAsString());

                            //titleTV.setText(snippet.getAsJsonObject().get("title").getAsString());
                            bindingData.setVideoTitle(snippet.getAsJsonObject().get("title").getAsString());

                            //publishTV.setText(snippet.getAsJsonObject().get("publishedAt").getAsString());
                            bindingData.setPublishDate(snippet.getAsJsonObject().get("publishedAt").getAsString());
                            String url = snippet.getAsJsonObject().get("thumbnails").getAsJsonObject().get("default").getAsJsonObject().get("url").getAsString();
                            //Log.e("Video url: ", url);

                            Picasso.with(VideoActivity.this).load(url).into(imageView);
                        }
                    });

            //startTimer(youTubePlayer);
            // Timer
            Observable<Integer> timerObservable = Observable.create(new Observable.OnSubscribe<Integer>(){
                @Override
                public void call(Subscriber<? super Integer> subscriber){
                    subscriber.onStart();
                    while(tickTimer < timeLimit*10){
                        subscriber.onNext(tickTimer++);
                        try{
                            Thread.sleep(100);
                        }
                        catch(Exception e){
                            subscriber.onCompleted();
                        }
                    }
                    subscriber.onCompleted();
                }

            });

            timerObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onCompleted() {
                            bindingData.setProgressValue(0);
                            //progressBar.setProgress(0);
                            //timerTV.setText(Integer.toString(0));
                            bindingData.setTimerText(Integer.toString(0));
                            tickTimer = 0;
                            videoIndex++;
                            playVideos(youTubePlayer);
                        }

                        @Override
                        public void onError(Throwable e) {
                            bindingData.setProgressValue(0);
                            //progressBar.setProgress(0);
                            //timerTV.setText(Integer.toString(0));
                            bindingData.setTimerText(Integer.toString(0));
                        }

                        @Override
                        public void onNext(Integer integer) {
                            bindingData.setProgressValue(integer);
                            //progressBar.setProgress((int) (integer));
                            if(integer%10 == 0) {
                                //timerTV.setText(Integer.toString(timeLimit - (integer/10)));
                                bindingData.setTimerText(Integer.toString(timeLimit - (integer/10)));
                            }
                        }
                    });
        }
        else{
            //timerTV.setText("");
            bindingData.setTimerText("");
            bindingData.setViewsValue("");
            bindingData.setPublishDate("");
            bindingData.setProgressValue(0);
            bindingData.setChannelTitle("");
            bindingData.setVideoTitle("");
            //progressBar.setProgress(0);
            youTubePlayer.release();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        }
        else{
            Toast.makeText(this, youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            getYouTubeProvider().initialize(API_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubeProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
