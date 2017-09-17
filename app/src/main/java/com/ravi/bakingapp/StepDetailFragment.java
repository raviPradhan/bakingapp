package com.ravi.bakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.Constants;
import com.ravi.bakingapp.utils.JsonKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.tv_step_detail_description)
    TextView stepDescription;
    @BindView(R.id.tv_step_detail_noVideo)
    TextView noVideoText;
    @BindView(R.id.iv_step_detail_noVideo)
    ImageView noVideoIcon;
    @BindView(R.id.epv_step_detail_player)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.rl_step_detail_noVideo)
    RelativeLayout noVideoLayout;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    // An object of @Steps to hold the data passed in through arguments
    Steps stepItem;
    Uri videoUri;

    long videoPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.v(Constants.TAG, "CALLED CREATE ");

        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        if(savedInstanceState == null){
            noVideoLayout.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(GONE);
            noVideoIcon.setVisibility(View.GONE);
            noVideoText.setText(getString(R.string.select_step));
            stepDescription.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            if (savedInstanceState != null && savedInstanceState.containsKey(JsonKeys.VIDEO_DATA_KEY))
                stepItem = savedInstanceState.getParcelable(JsonKeys.VIDEO_DATA_KEY);
            else
                stepItem = getArguments().getParcelable(JsonKeys.DATA_KEY);
            if (getUrl() == null) {
                noVideoLayout.setVisibility(View.VISIBLE);
                mPlayerView.setVisibility(GONE);
            } else {
                if (savedInstanceState != null && savedInstanceState.containsKey(JsonKeys.VIDEO_POSITION_KEY)) {
                    videoPosition = savedInstanceState.getLong(JsonKeys.VIDEO_POSITION_KEY);
//                    Log.v(Constants.TAG, "RETURN AT POSITION " + videoPosition);
                } else
                    videoPosition = 0;

                videoUri = Uri.parse(getUrl());
//                Log.v(Constants.TAG, "VIDEO CHANGED " + videoPosition);
                // Initialize the Media Session.
                initializeMediaSession();
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize the player.
        if(getActivity() instanceof MasterActivity){ // if its a master-child view then only take steps to initialize vidoe player
            if(((MasterActivity)getActivity()).twoPane){ // if the view is twoPane then only initialize the video player
                if (videoUri != null) {
//                    Log.v(Constants.TAG, "resume called from masteractivity " + videoPosition);
                    initializePlayer(videoUri);
                }
            }
        }else{
            if (videoUri != null) {
//                Log.v(Constants.TAG, "resume called from stepactivity " + videoPosition);
                initializePlayer(videoUri);
            }
        }
    }

    private String getUrl() {
        if (getArguments() != null) {
            noVideoLayout.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);
            stepDescription.setVisibility(View.VISIBLE);
            stepDescription.setText(stepItem.getDescription());
            if (!stepItem.getVideoUrl().isEmpty())
                return stepItem.getVideoUrl();
            else if (!stepItem.getThumbnailUrl().isEmpty())
                return stepItem.getThumbnailUrl();
            else {
                noVideoText.setText(getString(R.string.no_video));
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), Constants.TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        /*PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |*/
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            if (videoPosition > 0) {
//                Log.v(Constants.TAG, "SEEKING PLAYER " + videoPosition);
                mExoPlayer.seekTo(videoPosition);
            }
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release the player when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            videoPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

        if (mMediaSession != null)
            mMediaSession.setActive(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

//        Log.v(Constants.TAG, "SAVED POSITION AT " + videoPosition);
        outState.putLong(JsonKeys.VIDEO_POSITION_KEY, videoPosition);
        outState.putParcelable(JsonKeys.VIDEO_DATA_KEY, stepItem);
        super.onSaveInstanceState(outState);
    }
}
