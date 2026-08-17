package de.maxhenkel.voicechat.api.events;

import de.maxhenkel.voicechat.api.Position;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This event is emitted before the sound is played on the client.
 */
public interface ClientReceiveSoundEvent extends ClientEvent {

    /**
     * @return the ID of the sender
     */
    UUID getId();

    /**
     * The unencoded audio data.
     * <br/>
     * Returns an empty array in case of the end of transmission
     *
     * @return the raw 16 bit PCM audio frame
     */
    short[] getRawAudio();

    /**
     * Overrides the actual audio data that is played.
     * <p>
     * This is ignored, if the {@link #getRawAudio} array is empty
     *
     * @param rawAudio the raw 16 bit PCM audio frame
     */
    void setRawAudio(@Nullable short[] rawAudio);

    public static interface EntitySound extends ClientReceiveSoundEvent {

        /**
         * @return the UUID of the entity
         */
        UUID getEntityId();

        /**
         * @return if the player is whispering
         */
        boolean isWhispering();

        /**
         * @return the voice mode of the player
         */
        default de.maxhenkel.voicechat.api.VoiceMode getVoiceMode() {
            return isWhispering() ? de.maxhenkel.voicechat.api.VoiceMode.WHISPER : de.maxhenkel.voicechat.api.VoiceMode.NORMAL;
        }

        /**
         * @return the distance, the audio can be heard
         */
        float getDistance();
    }

    public static interface LocationalSound extends ClientReceiveSoundEvent {
        /**
         * @return the position of the sound
         */
        Position getPosition();

        /**
         * @return the distance, the audio can be heard
         */
        float getDistance();
    }

    public static interface StaticSound extends ClientReceiveSoundEvent {

    }

}
