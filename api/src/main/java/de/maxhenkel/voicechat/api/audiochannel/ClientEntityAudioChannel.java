package de.maxhenkel.voicechat.api.audiochannel;

import java.util.UUID;

public interface ClientEntityAudioChannel extends ClientAudioChannel {

    /**
     * @return the UUID of the entity
     */
    UUID getEntityId();

    /**
     * @param whispering if the entity should whisper
     */
    void setWhispering(boolean whispering);

    /**
     * @return if the entity is whispering
     */
    boolean isWhispering();

    /**
     * @return the voice mode of the entity
     */
    default de.maxhenkel.voicechat.api.VoiceMode getVoiceMode() {
        return isWhispering() ? de.maxhenkel.voicechat.api.VoiceMode.WHISPER : de.maxhenkel.voicechat.api.VoiceMode.NORMAL;
    }

    /**
     * @param voiceMode the voice mode to set
     */
    default void setVoiceMode(de.maxhenkel.voicechat.api.VoiceMode voiceMode) {
        setWhispering(voiceMode == de.maxhenkel.voicechat.api.VoiceMode.WHISPER);
    }

    /**
     * @return the distance, the audio can be heard
     */
    float getDistance();

    /**
     * @param distance the distance, the audio can be heard
     */
    void setDistance(float distance);

}
