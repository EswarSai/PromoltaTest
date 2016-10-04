package psai.develop.promoltatest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import java.util.Observable;

/**
 * Created by psai on 10/3/2016.
 */

public class uidata extends BaseObservable {
    private String channelTitle;
    private String viewsValue;
    private String videoTitle;
    private String publishDate;
    private String timerText;
    private Integer progressValue;

    public uidata(){
        this.channelTitle = "";
        this.viewsValue = "";
        this.videoTitle = "";
        this.publishDate = "";
        this.timerText = "";
        this.progressValue = 0;
    }

    @Bindable
    public String getChannelTitle(){
        return this.channelTitle;
    }

    public void setChannelTitle(String channelTitle){
        this.channelTitle = channelTitle;
        notifyPropertyChanged(BR.channelTitle);
    }

    @Bindable
    public String getViewsValue(){
        return this.viewsValue;
    }

    public void setViewsValue(String views){
        this.viewsValue = views;
        notifyPropertyChanged(BR.viewsValue);
    }

    @Bindable
    public String getVideoTitle(){
        return this.videoTitle;
    }

    public void setVideoTitle(String videoTitle){
        this.videoTitle = videoTitle;
        notifyPropertyChanged(BR.videoTitle);
    }

    @Bindable
    public String getPublishDate(){
        return this.publishDate;
    }

    public void setPublishDate(String publishDate){
        this.publishDate = publishDate;
        notifyPropertyChanged(BR.publishDate);
    }

    @Bindable
    public String getTimerText(){
        return this.timerText;
    }

    public void setTimerText(String timerText){
        this.timerText = timerText;
        notifyPropertyChanged(BR.timerText);
    }

    @Bindable
    public Integer getProgressValue(){
        return this.progressValue;
    }

    public void setProgressValue(Integer progressValue){
        this.progressValue = progressValue;
        notifyPropertyChanged(BR.progressValue);
    }
}
