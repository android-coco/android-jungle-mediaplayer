/**
 * Android Jungle-MediaPlayer-Demo project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.mediaplayer.demo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.widgets.JungleMediaPlayer;
import com.jungle.mediaplayer.widgets.SimpleJungleMediaPlayerListener;

public class PlayVideoActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO_URL = "extra_video_url";


    public static void start(Context context, String url) {
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_URL, url);
        context.startActivity(intent);
    }


    private JungleMediaPlayer mMediaPlayer;
    private boolean mIsFullScreenNow = false;
    private int mVideoZoneNormalHeight = 0;
    private String mVideoUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.play_video);
        setContentView(R.layout.activity_play_video);

        initMediaPlayer();

        mVideoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        TextView urlView = (TextView) findViewById(R.id.video_url);
        if (!TextUtils.isEmpty(mVideoUrl)) {
            urlView.setText(mVideoUrl);
            mMediaPlayer.playMedia(new VideoInfo(mVideoUrl));
        } else {
            urlView.setText(R.string.media_url_error);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchVideoContainer(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switchVideoContainer(false);
        }
    }

    private void switchVideoContainer(boolean fullScreen) {
        if (mIsFullScreenNow == fullScreen) {
            return;
        }

        mIsFullScreenNow = fullScreen;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (mIsFullScreenNow) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }

        updateVideoZoneSize(fullScreen);
    }

    private void updateVideoZoneSize(final boolean fullScreen) {
        ViewGroup.LayoutParams params = mMediaPlayer.getLayoutParams();
        params.height = fullScreen
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : mVideoZoneNormalHeight;
        mMediaPlayer.setLayoutParams(params);
    }

    private void initMediaPlayer() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mVideoZoneNormalHeight = (int) (metrics.widthPixels / 1.77f);

        FrameLayout panel = (FrameLayout) findViewById(R.id.adjust_panel_container);
        mMediaPlayer = (JungleMediaPlayer) findViewById(R.id.media_player);
        mMediaPlayer.setAdjustPanelContainer(panel);
        mMediaPlayer.setAutoReloadWhenError(false);
        mMediaPlayer.setPlayerListener(new SimpleJungleMediaPlayerListener() {

            @Override
            public void onTitleBackClicked() {
                if (mMediaPlayer.isFullscreen()) {
                    mMediaPlayer.switchFullScreen(false);
                    return;
                }

                finish();
            }
        });

        updateVideoZoneSize(false);
    }
}
