package com.murach.newsreader;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.util.Log;

public class NewsReaderApp extends Application {

    private static final int JOB_ID=3;
    private long feedMillis = -1;

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    private JobScheduler jobScheduler;
    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }
    
    public long getFeedMillis() {
        return feedMillis;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("News reader", "App started");

        //Creating job scheduler at the start of the app.
        createScheduler();
    }
    /*Function: createScheduler
    * Returns: void
    * Functionality: creates job scheduler with 1 hour interval*/
    private void createScheduler(){
        ComponentName componentName=new ComponentName(getApplicationContext(),NewsReaderJob.class);
        int duration = 1000 * 60 * 60;//One hour
        JobInfo jobInfo=new JobInfo.Builder(JOB_ID,componentName).setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setBackoffCriteria(5000,JobInfo.BACKOFF_POLICY_LINEAR).setPeriodic(duration).build();
        jobScheduler=(JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }
}