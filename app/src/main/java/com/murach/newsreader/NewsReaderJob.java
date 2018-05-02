package com.murach.newsreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Praveen Kn on 11-20-2017.
 */

public class NewsReaderJob extends JobService {
    private NewsReaderApp app;
    private FileIO io;
    private TaskUpdate taskUpdate=new TaskUpdate();
    @Override
    public void onCreate() {
        app = (NewsReaderApp) getApplication();
        io = new FileIO(getApplicationContext());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("News reader", "OnStartJob");
        taskUpdate.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("News reader", "OnStopJob");
        return true;
    }

    private void sendNotification(String text) {
        // create the intent for the notification
        Intent notificationIntent = new Intent(this, ItemsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, flags);

        // create the variables for the notification
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Updated news feed is available";
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;

        // create the notification and set its data
        Notification notification =
                new Notification.Builder(this)
                        .setSmallIcon(icon)
                        .setTicker(tickerText)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        // display the notification
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        final int NOTIFICATION_ID = 1;
        manager.notify(NOTIFICATION_ID, notification);
    }

    private class TaskUpdate extends AsyncTask<JobParameters, Void, JobParameters> {
        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            Log.d("News reader", "DoInBackground");
            executeTask(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters params) {
            Log.d("News reader", "OnPost");
                jobFinished(params,true);
        }
    }
    public void executeTask(JobParameters params){
        Log.d("News reader", "Task started");
        io.downloadFile();
        Log.d("News reader", "File downloaded");
        RSSFeed newFeed = io.readFile();
        Log.d("News reader", "File read");
        // if new feed is newer than old feed
        if (newFeed.getPubDateMillis() > app.getFeedMillis()) {
            Log.d("News reader", "Updated feed available.");
            // update app object
            app.setFeedMillis(newFeed.getPubDateMillis());
            // display notification
            sendNotification("Select to view updated feed.");
        } else {
            Log.d("News reader", "Updated feed NOT available.");
        }
        Log.d("News reader", "ExecEnd");
    }

}