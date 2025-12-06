package com.example.pitabmdmstudent.data.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.models.PeerTrackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSLocalVideoTrack
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.media.tracks.HMSTrackType
import live.hms.video.media.tracks.HMSVideoTrack
import live.hms.video.sdk.HMSActionResultListener
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.*
import live.hms.video.sdk.models.enums.*
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import javax.inject.Inject
@HiltViewModel
class HmsViewModel @Inject constructor(
    private val hmsSdk: HMSSDK,
    application: Application
) : ViewModel(), HMSUpdateListener {

    private val TAG = "HMS-FIXED"

    private val _callType = MutableStateFlow("video_call")
    val callType = _callType.asStateFlow()

    fun setCallType (value: String) {
        _callType.value = value
    }

    // Unified list of peers + tracks
    private val _peerTrackState = MutableStateFlow<List<PeerTrackState>>(emptyList())
    val peerTrackState = _peerTrackState.asStateFlow()

    private val _shouldStartScreenShare = MutableStateFlow(false)
    val shouldStartScreenShare = _shouldStartScreenShare.asStateFlow()


    init {
        // Optional: prepare preview / network quality
    }

    // ------------------------
    // JOIN ROOM
    // ------------------------
    fun joinRoom(userName: String, authToken: String) {
        Log.d(TAG, "🚀 joinRoom() called | user=$userName")
        val config = HMSConfig(
            userName = userName,
            authtoken = authToken
        )
        hmsSdk.join(config, this)
    }

    // ------------------------
    // HMS LISTENER METHODS
    // ------------------------
    override fun onJoin(room: HMSRoom) {
        Log.d(TAG, "✅ onJoin() triggered | room=${room.name}")
        Log.d(TAG, "callType.value = ${callType.value}")

        updatePeerTrackState()

        if (callType.value == "screen_share") {
            Log.d(TAG, "🎯 callType=screen_share → Requesting screen share start")
            _shouldStartScreenShare.value = true
        } else {
            Log.d(TAG, "ℹ️ Not a screen share call, skipping auto-start")
        }
    }

    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
        Log.d(TAG, "👥 onPeerUpdate: ${type.name} | peer=${peer.name}")
        updatePeerTrackState()
    }

    override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
        Log.d(TAG, "🎥 onTrackUpdate: ${type.name} | track=${track.source} | peer=${peer.name}")
        updatePeerTrackState()
    }

    override fun onMessageReceived(message: HMSMessage) {}
    override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {}
    override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {}
    override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {}

    override fun onError(error: HMSException) {
        Log.e(TAG, "❌ HMS Error: ${error.code} – ${error.message}")
    }

    // ------------------------
    // CORE LOGIC
    // ------------------------
    private fun updatePeerTrackState() {
        val list = hmsSdk.getPeers().map { peer ->

            val cameraTrack = peer.videoTrack     // nullable
            val screenShareTrack =
                peer.auxiliaryTracks.filterIsInstance<HMSVideoTrack>().firstOrNull()

            PeerTrackState(
                peer = peer,
                videoTrack = cameraTrack,
                screenTrack = screenShareTrack
            )
        }

        _peerTrackState.value = list
        Log.d(TAG, "Updated PeerTrackState: count=${list.size}")
    }

    // ------------------------
    // CONTROLS
    // ------------------------
    fun toggleMute() {
        val audio = hmsSdk.getLocalPeer()?.audioTrack
        audio?.setMute(!(audio.isMute))
    }

    fun toggleVideo() {
        hmsSdk.getLocalPeer()?.videoTrack?.let {
            it.setMute(!it.isMute)
        }
        updatePeerTrackState()
    }

    fun switchCamera() {
        (hmsSdk.getLocalPeer()?.videoTrack as? HMSLocalVideoTrack)?.switchCamera()
    }

    fun leaveRoom() {
        hmsSdk.leave()
        _peerTrackState.value = emptyList()
    }

    fun startScreenShare(data: Intent) {
        Log.d(TAG, "▶️ startScreenShare() called with mediaProjectionIntent=$data")

        hmsSdk.startScreenshare(object : HMSActionResultListener {
            override fun onSuccess() {
                Log.d(TAG, "🎉 Screen share successfully started!")
            }

            override fun onError(error: HMSException) {
                Log.e(TAG, "❌ Failed to start screenshare: ${error.code} | ${error.message}")
            }
        }, data)
    }

    fun stopScreenShare() {
        Log.d(TAG, "🛑 stopScreenShare() called")

        hmsSdk.stopScreenshare(object : HMSActionResultListener {
            override fun onSuccess() {
                Log.d(TAG, "🧹 Screen share stopped cleanly")
            }
            override fun onError(error: HMSException) {
                Log.e(TAG, "❌ Failed to stop screenshare: ${error.code} | ${error.message}")
            }
        })
    }
}
