package com.xcaret.xcaret_hotel.view.general.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.Constants
import kotlinx.android.synthetic.main.activity_youtube_player.*

@Suppress("DEPRECATION")
class YouTubePlayerActivity: YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private lateinit var videoId: String

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        setContentView(R.layout.activity_youtube_player)
        initYoutubePlayer()
    }

    private fun initYoutubePlayer(){
        videoId = intent.getStringExtra(Constants.VIDEO_ID) ?: ""
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ytbView.initialize(getApiKey(), this)
    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        if (!wasRestored) {
            player?.setFullscreen(true)
            player?.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
            player?.loadVideo(videoId, RECOVERY_REQUEST)
            player?.play()
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, errorReason: YouTubeInitializationResult?) {
        errorReason?.let {
            if(errorReason.isUserRecoverableError) it.getErrorDialog(this@YouTubePlayerActivity, RECOVERY_REQUEST).show()
        } ?: kotlin.run {
            Toast.makeText(this@YouTubePlayerActivity, errorReason.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RECOVERY_REQUEST) {
            ytbView.initialize(getApiKey(), this)
        }
    }

    private fun getApiKey(): String = getString(R.string.google_key_app_id)

    companion object{
        const val RECOVERY_REQUEST = 1
    }
}