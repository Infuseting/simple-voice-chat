package de.maxhenkel.voicechat.api.events;

import de.maxhenkel.voicechat.api.VoiceMode;

/**
 * This event is emitted before the client encodes the audio and sends it to the server.
 */
public interface ClientSoundEvent extends ClientEvent {

    /**
     * The unencoded audio data.
     * <br/>
     * <s>Returns an empty array in case of the end of transmission</s>
     *
     * @return the raw 16 bit PCM audio frame
     */
    short[] getRawAudio();

    /**
     * Overrides the actual audio data that's sent to the server.
     *
     * @param rawAudio the raw 16 bit PCM audio frame
     */
    void setRawAudio(short[] rawAudio);

    /**
     * @return if the player is whispering
     */
    boolean isWhispering();

    /**
     * @return the voice mode of the player
     */
    default VoiceMode getVoiceMode() {
        return isWhispering() ? VoiceMode.WHISPER : VoiceMode.NORMAL;
    }

}
