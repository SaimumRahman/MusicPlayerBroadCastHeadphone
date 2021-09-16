package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.musicplayer.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MediaPlayer mediaPlayer;
    Handler handler=new Handler();
    Runnable runnable;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaPlayer=MediaPlayer.create(this,R.raw.doba);

        runnable=new Runnable() {
            @Override
            public void run() {
                binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());

                handler.postDelayed(this,500);
            }
        };

        int duration=mediaPlayer.getDuration();
        String conversion=durationCOnversion(duration);

        binding.playerDuration.setText(conversion);




            //broadcasting
            headsetPlugReceiver = new HeadsetPlugReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            registerReceiver(headsetPlugReceiver, intentFilter);


            binding.seekBar.setMax(mediaPlayer.getDuration());

            handler.postDelayed(runnable,0);


        binding.btnPause.setOnClickListener(v ->{
            binding.btnPlay.setVisibility(View.VISIBLE);
            binding.btnPause.setVisibility(View.GONE);
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        });

        binding.btnForward.setOnClickListener(v ->{
            int currentPosition=mediaPlayer.getCurrentPosition();
            int durations=mediaPlayer.getDuration();
            if (mediaPlayer.isPlaying() && durations!=currentPosition){
                currentPosition=currentPosition+5000;
                binding.playerPosition.setText(durationCOnversion(currentPosition));
                mediaPlayer.seekTo(currentPosition);
            }
        });

        binding.btnRewind.setOnClickListener(v ->{
            int currentPositionR=mediaPlayer.getCurrentPosition();
            if (mediaPlayer.isPlaying() && currentPositionR>5000){
                currentPositionR=currentPositionR-5000;
                binding.playerPosition.setText(durationCOnversion(currentPositionR));
                mediaPlayer.seekTo(currentPositionR);
            }
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    mediaPlayer.seekTo(i);
                }

                binding.playerPosition.setText(durationCOnversion(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.btnPause.setVisibility(View.GONE);
                binding.btnPlay.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });

    }
    @Override
    public void onDestroy() {
        unregisterReceiver(headsetPlugReceiver);
        super.onDestroy();
    }

    private String durationCOnversion(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        private static final String TAG = "HeadsetPlugReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")){
                if (intent.getIntExtra("state", 0) == 0){
                    Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();


                }
                else if (intent.getIntExtra("state", 0) == 1){
                    binding.btnPlay.setVisibility(View.GONE);
                    binding.btnPause.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                    Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
                }
            }
        }


    }
}