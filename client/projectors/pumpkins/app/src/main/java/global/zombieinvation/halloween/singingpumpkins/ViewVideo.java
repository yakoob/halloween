package global.zombieinvation.halloween.singingpumpkins;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.google.gson.Gson;


import android.media.MediaPlayer.OnTimedTextListener;
import android.media.MediaPlayer.TrackInfo;
import android.media.TimedText;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static java.lang.Thread.sleep;

public class ViewVideo extends Activity implements MqttCallback, OnTimedTextListener {

    private static final int INSERT_ID = Menu.FIRST;
    private static Handler handler = new Handler();
    private static final String TAG = "TimedTextTest";

    private static int vid = 0;
    private static int sub = 0;
    private static Video video;

    MqttClient client;

    VideoView vv = null;

    MediaPlayer mediaPlayer = null;


    private static Context mContext;

    public static ViewVideo instace;


    public ViewVideo() throws MqttException {


        // configure messaging client
        resetMqttClient();
        client = new MqttClient("tcp://192.168.20.114:1883", MqttClient.generateClientId(), null);
        client.setCallback(this);
        MqttConnectOptions options = new MqttConnectOptions();

        // connect to message broker
        try {
            client.connect(options);
        } catch (MqttException e) {
            Log.d(getClass().getCanonicalName(), "Connection attempt failed with reason code = " + e.getReasonCode() + ":" + e.getCause());
        }

        // subscribe to app topic
        try {
            // client.subscribe("halloween/projector");
            client.subscribe("halloween/projector/pumpkins");
            // client.subscribe("#");
        } catch (MqttException e) {
            Log.d(getClass().getCanonicalName(), "Subscribe failed with reason code = " + e.getReasonCode());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        new MyDeviceAdminReceiver().onEnabled(getApplicationContext(), new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN));

        vv = new VideoView(getApplicationContext());

        setContentView(vv);

        try {
            playWoods();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mContext = getApplicationContext();
        instace = this;

        startService(new Intent(this, StickyService.class));

        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this, ViewVideo.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0,"FullScreen");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
        }
        return true;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(getClass().getCanonicalName(), "MQTT Server connection lost" + cause);

        try {
            sleep(10000);
            client.connect(new MqttConnectOptions());
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {

        /**
         *
         *  http://localhost:8080/halloween/test/test?m={'command':'Play','name':'KIDNAP_SANDY_CLAWS'}
         *  http://localhost:8080/halloween/test/test?m={'command':'Pause'}
         */

        try {

            video = new Gson().fromJson(message.toString(), Video.class);

            Log.d("HalloweenVideoPlayer", "command:" + video.getCommand() + " | song:" + video.getName());

            if (video.getCommand().equals("Play")) {

                if (video.getName().equals(Video.Name.WOODS)) {
                    vid = R.raw.halloween_woods;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.ADDAMS_FAMILY)) {
                    vid = R.raw.halloween_addams_family;
                    sub = R.raw.halloween_addams_family_sub;
                } else if (video.getName().equals(Video.Name.BLUE_MOON)) {
                    vid = R.raw.halloween_blue_moon;
                    sub = R.raw.halloween_blue_moon_sub;
                } else if (video.getName().equals(Video.Name.DAY_O)) {
                    vid = R.raw.halloween_day_o;
                    sub = R.raw.halloween_day_o_sub;
                } else if (video.getName().equals(Video.Name.DEAD_MANS_PARTY)) {
                    vid = R.raw.halloween_dead_mans_party;
                    sub = R.raw.halloween_dead_mans_party_sub;
                } else if (video.getName().equals(Video.Name.GHOST_HOST)) {
                    vid = R.raw.halloween_ghost_host;
                    sub = R.raw.halloween_ghost_host_sub;
                } else if (video.getName().equals(Video.Name.GHOST_BUSTERS)) {
                    vid = R.raw.halloween_ghostbusters;
                    sub = R.raw.halloween_ghostbusters_sub;
                } else if (video.getName().equals(Video.Name.GRIM_GRINNING_GHOST)) {
                    vid = R.raw.halloween_grim_grinning_ghosts;
                    sub = R.raw.halloween_grim_grinning_ghosts_sub;
                } else if (video.getName().equals(Video.Name.JUMP_IN_THE_LINE)) {
                    vid = R.raw.halloween_jump_in_the_line;
                    sub = R.raw.halloween_jump_in_the_line_sub;
                } else if (video.getName().equals(Video.Name.MONSTER_MASH)) {
                    vid = R.raw.halloween_monster_mash;
                    sub = R.raw.halloween_monster_mash_sub;
                } else if (video.getName().equals(Video.Name.NIGHTMARE_ON_MY_STREET)) {
                    vid = R.raw.halloween_nightmare_on_my_street;
                    sub = R.raw.halloween_nightmare_on_my_street_sub;
                } else if (video.getName().equals(Video.Name.RED_RIDING_HOOD)) {
                    vid = R.raw.halloween_red_riding_hood;
                    sub = R.raw.halloween_red_riding_hood_sub;
                } else if (video.getName().equals(Video.Name.SEXY_AND_I_KNOW_IT)) {
                    vid = R.raw.halloween_sexy;
                    sub = R.raw.halloween_sexy_sub;
                } else if (video.getName().equals(Video.Name.SOMEBODYS_WATCHING_ME)) {
                    vid = R.raw.halloween_somebodys_watching_me;
                    sub = R.raw.halloween_sombodys_watching_me_sub;
                } else if (video.getName().equals(Video.Name.THEYRE_COMING)) {
                    vid = R.raw.halloween_theyre_coming;
                    sub = R.raw.halloween_theyre_coming_sub;
                } else if (video.getName().equals(Video.Name.THIS_IS_HALLOWEEN)) {
                    vid = R.raw.halloween_this_is_halloween;
                    sub = R.raw.halloween_this_is_halloween_sub;
                } else if (video.getName().equals(Video.Name.THRILLER)) {
                    vid = R.raw.halloween_thriller;
                    sub = R.raw.halloween_thriller_sub;
                } else if (video.getName().equals(Video.Name.TIME_WARP)) {
                    vid = R.raw.halloween_time_warp;
                    sub = R.raw.halloween_time_warp_sub;
                } else if (video.getName().equals(Video.Name.WEREWOLVES_IN_LONDON)) {
                    vid = R.raw.halloween_werewolves_of_london;
                    sub = R.raw.halloween_werewolveds_of_london_sub;
                } else if (video.getName().equals(Video.Name.YO_HO_HO)) {
                    vid = R.raw.halloween_yo_ho;
                    sub = R.raw.halloween_yo_ho_ho_sub;
                } else if (video.getName().equals(Video.Name.ZOMBIE_JAMBORE)) {
                    vid = R.raw.halloween_zombie_jamboree;
                    sub = R.raw.halloween_zombie_jamboree_sub;
                } else if (video.getName().equals(Video.Name.BOHEMIAN_RHAPSODY)) {
                    vid = R.raw.halloween_bohemian_rhapsody;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_COBWEBS)) {
                    vid = R.raw.haloween_jokes_cobwebs;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_DEADICATION)) {
                    vid = R.raw.haloween_jokes_deadication;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_EATING_CLOWN)) {
                    vid = R.raw.haloween_jokes_eating_clown;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_HOLLYWOOD_GHOST)) {
                    vid = R.raw.haloween_jokes_hollywood_ghosts;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_KNOCK_KNOCK)) {
                    vid = R.raw.haloween_jokes_knock_knock;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_LOOK_BOTH_WAYS)) {
                    vid = R.raw.haloween_jokes_look_both_ways;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_MUMMY)) {
                    vid = R.raw.haloween_jokes_mummy_joke;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_NOTHING_BUT_BEST)) {
                    vid = R.raw.haloween_jokes_nothing_but_the_best;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_PUMPKIN_PATCH)) {
                    vid = R.raw.haloween_jokes_pumpkin_patch;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_STAIRING_AT)) {
                    vid = R.raw.haloween_jokes_staring_at;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_TASTEFULL)) {
                    vid = R.raw.haloween_jokes_tasteful_joke;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_TOO_MUCH_CANDY)) {
                    vid = R.raw.haloween_jokes_too_muchcandy;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_UNDER_SKIN)) {
                    vid = R.raw.haloween_jokes_under_their_skin;
                    sub = R.raw.halloween_woods_sub;
                } else if (video.getName().equals(Video.Name.JOKE_ZOMBIE_EYES)) {
                    vid = R.raw.haloween_jokes_zombie_eyes;
                    sub = R.raw.halloween_woods_sub;
                } else {
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        vv.resume();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + vid));
                                vv.requestFocus();
                                vv.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer pMp) {
                                        mediaPlayer = pMp;

                                        setMediaTextCallBack(sub);

                                        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                                        if (video.getName().equals(Video.Name.WOODS)) {
                                            mediaPlayer.setLooping(false);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_LOWER, 0);
                                        } else {
                                            mediaPlayer.setLooping(false);
                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                                        }

                                        try {

                                            Gson gson = new Gson();
                                            MqttMessage m = new MqttMessage();
                                            video.setEvent("playbackStarted");
                                            m.setPayload(gson.toJson(video).getBytes());
                                            video.setNext(Boolean.FALSE);
                                            client.publish("halloween/actor/pumpkins", m);

                                        } catch (MqttException e) {
                                            Log.e("MQTT", e.getMessage());
                                        }



                                    }
                                });

                                vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer pMp) {

                                        try {

                                            MqttMessage m = new MqttMessage();
                                            Gson gson = new Gson();
                                            video.setEvent("playbackComplete");
                                            video.setNext(Boolean.FALSE);
                                            m.setPayload(gson.toJson(video).getBytes());
                                            client.publish("halloween/actor/pumpkins", m);
                                            Log.d("complete", "song finished!!!");

                                        } catch (MqttException e) {
                                            Log.e("MQTT", e.getMessage());
                                        }

                                    }
                                });


                                vv.start();




                            }
                        }, 200);
                    }
                });




            } else if (video.getCommand().equals("Pause")) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vv.pause();
                            }
                        }, 200);

                    }

                });

            } else if (video.getCommand().equals("Resume")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        vv.start();
                    }
                });


            } else {
                Log.d("dunno", "wtf command:"+video.getCommand());
            }

        } catch (Exception e) {
            Log.d(getClass().getCanonicalName(), e.getMessage());
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(getClass().getCanonicalName(), "Delivery complete");
    }

    @Override
    public void onTimedText(final MediaPlayer mp, final TimedText text) {
        if (text != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(text.getText().getBytes());
                    try {
                        client.publish("halloween/actor/pumpkins", message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    Log.d("timedText fired:", text.getText());

                }
            });
        }
    }

    public void setMediaTextCallBack(int sf){

        final ViewVideo thisVideoView = this;
        final int subFileName = sf;

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {

                try {

                    mediaPlayer.addTimedTextSource(getSubtitleFile(subFileName), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);

                    int textTrackIndex = findTrackIndexFor(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.getTrackInfo());
                    if (textTrackIndex >= 0) {
                        mediaPlayer.selectTrack(textTrackIndex);
                    } else {
                        Log.d("error", "Cannot find text track!");
                    }
                    mediaPlayer.setOnTimedTextListener(thisVideoView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void createNote() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.PROGRESS_VISIBILITY_OFF);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private int findTrackIndexFor(int mediaTrackType, TrackInfo[] trackInfo) {
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    private String getSubtitleFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File subtitleFile = getFileStreamPath(fileName);
        if (subtitleFile.exists()) {
            return subtitleFile.getAbsolutePath();
        }
        Log.d(TAG, "Subtitle does not exists, copy it from res/raw");

        // Copy the file from the res/raw folder to your app folder on the
        // device
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(resId);
            outputStream = new FileOutputStream(subtitleFile, false);
            copyFile(inputStream, outputStream);
            return subtitleFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, outputStream);
        }
        return "";
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    // A handy method I use to close all the streams
    private void closeStreams(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable stream : closeables) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void playWoods() throws MqttException {
        MqttMessage m = new MqttMessage();
        String video = "{'command':'Play','name':'WOODS',id:1,type='PUMPKINS'}";
        m.setPayload(video.getBytes());
        client.publish("halloween/projector/pumpkins", m);
    }

    private void resetMqttClient(){

        if (client != null){
            try {
                client.unsubscribe("halloween/projector/pumpkins");
                client.close();
                client = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static ViewVideo getIntance() {
        return instace;
    }
}

