package caffeinatedandroid.keepyourdistance.media

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

/**
 * Allows audio to be played. Will only allow audio to be played one at a time.
 */
object AudioManager {

    // TODO manually test this plays audio from UI
    // TODO manually test this plays audio in service
    // TODO test it can only play one thing at a time (no overlapping or queued audio)
    // TODO Test playing works with `destroyOnCompletion` or manually calling `destroy`

    // TODO consider having audio alerts that override vibrate more (this object should work)
    // TODO consider notifications/vibrations/audio that respect the users system setting (ring/vibrate/silent)

    private var currentPlayer: MediaPlayer? = null

    /**
     * Plays audio through a [MediaPlayer]
     * @param destroyOnCompletion Whether the [MediaPlayer] automatically releases
     *  resources on completion, or if resources need manually releasing by calling [destroy].
     *  Defaults to `true`.
     * @return [Boolean] `true` if audio playing successfully, `false` if media player already
     *  playing previous audio.
     */
    fun playAudio(context: Context, destroyOnCompletion: Boolean = true): Boolean {
        when {
            currentPlayer?.isPlaying == true -> {
                // Player == alive && playing
                // Don't play more; already playing.
                return false
            }
            currentPlayer != null -> {
                // Player == alive && not playing
                currentPlayer?.start()
            }
            else -> {
                // Player != alive
                currentPlayer = MediaPlayer.create(
                    context,
                    Settings.System.DEFAULT_RINGTONE_URI
                )
                if (destroyOnCompletion) {
                    currentPlayer?.setOnCompletionListener { destroy() }
                }
                // Play alert
                currentPlayer?.start()
            }
        }
        return true
    }

    /**
     * Releases resources associated with the [MediaPlayer]
     */
    fun destroy() {
        currentPlayer?.release()
        currentPlayer = null
    }
}