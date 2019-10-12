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
            client.subscribe("halloween/projector/hologram");
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
            playWaiting();
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

            if (video.getCommand().equals("Play") || video.getCommand().equals("PlayHologram")) {

                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

                if ( audioManager != null ) {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }

                if (video.getName().equals(Video.Name.WAITING)) {
                    vid = R.raw.waiting;
                    sub = R.raw.halloween_sam_nocostume_sub;
                }

                else if (video.getName().equals(Video.Name.SAM_NOCOSTUME)) {
                    vid = R.raw.halloween_sam_nocostume;
                    sub = R.raw.halloween_sam_nocostume_sub;
                }
                else if (video.getName().equals(Video.Name.SAM_SYMPHONY)) {
                    vid = R.raw.halloween_sam_symphony;
                    sub = R.raw.halloween_sam_symphony_sub;
                }
                else if (video.getName().equals(Video.Name.SAM_SCARE1)) {
                    vid = R.raw.halloween_sam_scare1;
                    sub = R.raw.halloween_sam_scare1_sub;
                }
                else if (video.getName().equals(Video.Name.SAM_SCARE2)) {
                    vid = R.raw.halloween_sam_scare2;
                    sub = R.raw.halloween_sam_scare1_sub;
                }
                else if (video.getName().equals(Video.Name.SAM_SCARE3)) {
                    vid = R.raw.halloween_sam_scare3;
                    sub = R.raw.halloween_sam_scare3_sub;
                }
                else if (video.getName().equals(Video.Name.SAM_SCARE4)) {
                    vid = R.raw.halloween_sam_scare4;
                    sub = R.raw.halloween_sam_scare4_sub;
                }

                else if (video.getName().equals(Video.Name.BONEYARD_BAND)) {
                    vid = R.raw.halloween_boneyard_band;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.BONEYARD_PUMPKIN)) {
                    vid = R.raw.halloween_boneyard_pumpkin;
                    sub = R.raw.halloween_sam_scare4_sub;
                }

                else if (video.getName().equals(Video.Name.OMINOUS_OCULI_SWARM)) {
                    vid = R.raw.halloween_ominous_oculi_swarm;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.DRAGON_EYE)) {
                    vid = R.raw.halloween_dragon_single_eye;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.DRAGON_FULL)) {
                    vid = R.raw.halloween_dragon_eyes_mouth;
                    sub = R.raw.halloween_sam_scare4_sub;
                }

                else if (video.getName().equals(Video.Name.SPECTRAL_SURFACES)) {
                    vid = R.raw.hallloween_spectral_surfaces;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.SPECTRAL_SCARE1)) {
                    vid = R.raw.hallloween_spectral_scare1;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.SPECTRAL_SCARE2)) {
                    vid = R.raw.hallloween_spectral_scare2;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.SPECTRAL_SCARE3)) {
                    vid = R.raw.hallloween_spectral_scare3;
                    sub = R.raw.halloween_sam_scare4_sub;
                }

                else if (video.getName().equals(Video.Name.MONSTERS_BAND)) {
                    vid = R.raw.halloween_monsters_band_1;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.MONSTERS_DANCE)) {
                    vid = R.raw.halloween_monsters_dance_1;
                    sub = R.raw.halloween_sam_scare4_sub;
                    if ( audioManager != null ) {
                        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    }
                }
                else if (video.getName().equals(Video.Name.MONSTERS_SCAREY)) {
                    vid = R.raw.halloween_monsters_scary;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.MONSTERS_SILY)) {
                    vid = R.raw.halloween_monsters_silly;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_BALLOON)) {
                    vid = R.raw.halloween_jester_balloon;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_BALLOON)) {
                    vid = R.raw.halloween_jester_balloon;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_CLOWNING)) {
                    vid = R.raw.halloween_jester_clowning_around;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_JOYRIDE)) {
                    vid = R.raw.halloween_jester_joy_ride;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_MECHANICAL)) {
                    vid = R.raw.halloween_jester_mechanical_mischief;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_NOWYOUSEEME)) {
                    vid = R.raw.halloween_jester_now_you_see_me;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.JESTER_SCARES)) {
                    vid = R.raw.halloween_jester_startle_scares;
                    sub = R.raw.halloween_sam_scare4_sub;
                }
                else if (video.getName().equals(Video.Name.MOON)) {
                    vid = R.raw.halloween_moon;
                    sub = R.raw.halloween_moon_sub;
                }
                else if (video.getName().equals(Video.Name.ZOMBIE_TWIST)) {
                    vid = R.raw.halloween_zombie_twist;
                    sub = R.raw.halloween_zombie_twist_sub;
                    if ( audioManager != null ) {
                        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    }
                }
                else if (video.getName().equals(Video.Name.YOU_AXED)) {
                    vid = R.raw.halloween_you_axed_for_it;
                    sub = R.raw.halloween_you_axed_for_it_sub;
                }
                else if (video.getName().equals(Video.Name.WALL_BANGERS)) {
                    vid = R.raw.halloween_wall_bangers;
                    sub = R.raw.halloween_wall_bangers_sub;
                }
                else if (video.getName().equals(Video.Name.BOO)) {
                    vid = R.raw.halloween_boo;
                    sub = R.raw.halloween_wall_bangers_sub;
                    if ( audioManager != null ) {
                        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    }
                }
                else {
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

                                        if (video.getName().equals(Video.Name.WAITING)) {
                                            mediaPlayer.setLooping(true);
                                        } else {
                                            mediaPlayer.setLooping(false);
                                        }

                                    }
                                });

                                vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer pMp) {
                                        try {

                                            Boolean next = Boolean.TRUE;

                                            if (video.getName().equals(Video.Name.MONSTERS_DANCE))
                                                next = Boolean.FALSE;

                                            MqttMessage m = new MqttMessage();
                                            Gson gson = new Gson();
                                            video.setEvent("playbackComplete");
                                            video.setNext(next);
                                            m.setPayload(gson.toJson(video).getBytes());
                                            client.publish("halloween/actor/hologram", m);
                                            Log.d("complete", "song finished!!!");
                                            playWaiting();
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
                    /*
                    try {
                        // client.publish("ActorSystem/Halloween", message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    */
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

    private void playWaiting() throws MqttException {
        MqttMessage m = new MqttMessage();
        String video = "{'command':'Play','name':'WAITING'}";
        m.setPayload(video.getBytes());
        client.publish("halloween/projector/hologram", m);
    }

    private void resetMqttClient(){

        if (client != null){
            try {
                client.unsubscribe("halloween/projector/hologram");
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

