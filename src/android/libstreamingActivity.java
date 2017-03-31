package br.com.stek.rtsplayer;

import net.majorkernelpanic.streaming.MediaStream;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class libstreamingActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnErrorListener , MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private LinearLayout layout;
    private SurfaceView mSurfaceView;
    private SurfaceHolder surfaceHolder;
    private String ip;
    private Number port;
    private String path;
    private String username;
    private String password;
    private SessionBuilder mSession;
    private RtspClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            return;
        }

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            ip = extras.getString("IP");
            port = Integer.parseInt(extras.getString("PORT"));
            path = extras.getString("PATH");
            username = extras.getString("USERNAME");
            password  = extras.getString("PASSWORD");
        } else {
            finishWithError();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // create the linear layout to hold our video
        layout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        // add the mSurfaceView with the current video
        createVideoView();

        // add to the view
        setContentView(layout);
    }

    private void createVideoView() {
        mSurfaceView = new SurfaceView(getApplicationContext());
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Para sair clique em voltar",Toast.LENGTH_SHORT).show();
            }
        });
        mSession = SessionBuilder.getInstance()
				.setContext(getApplicationContext())
				.setAudioEncoder(SessionBuilder.AUDIO_NONE)
				.setVideoEncoder(SessionBuilder.VIDEO_H264)
				.setSurfaceView(mSurfaceView)
				.setPreviewOrientation(0)
                .setVideoQuality(VideoQuality.DEFAULT_VIDEO_QUALITY)
				.setCallback(this)
				.build();

		// Configures the RTSP client
		mClient = new RtspClient();
		mClient.setSession(mSession);
		mClient.setCallback(this);
        mClient.setTransportMode(RtspClient.TRANSPORT_TCP);
        surfaceHolder = mSurfaceView.getHolder(); 
        surfaceHolder.addCallback(this);
        layout.addView(mSurfaceView);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mClient.setCredentials(username, password);
			mClient.setServerAddress(ip, port);
			mClient.setStreamPath("/"+path);
			mClient.startStream();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Falha ao abrir video",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finishWithError();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mClient.isStreaming()) {
            mClient.stopStream();
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("FLP", "onError fired");
        Toast.makeText(getApplicationContext(), "Falha ao abrir video", Toast.LENGTH_SHORT).show();
        finishWithError();
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.d("FLP", "DO NOTHING");
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void finishWithError() {
        setResult(100);
        finish();
    }

    @Override
	public void onDestroy(){
		super.onDestroy();
		mClient.release();
		mSession.release();
		mSurfaceView.getHolder().removeCallback(this);
	}


}
