package de.maxhenkel.voicechat.api.packets;

import de.maxhenkel.voicechat.api.VoiceMode;

public interface MicrophonePacket extends Packet, ConvertablePacket {

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

    /**
     * Sets the voice mode of the packet.
     *
     * @param voiceMode the voice mode
     */
    default void setVoiceMode(VoiceMode voiceMode) {

    }

    /**
     * @return the opus encoded audio data from the player
     */
    byte[] getOpusEncodedData();

    /**
     * Allows you to modify or replace the opus encoded audio data.
     *
     * @param data the opus encoded audio data to replace
     */
    void setOpusEncodedData(byte[] data);

}
