package de.maxhenkel.voicechat.plugins.impl.events;

import de.maxhenkel.voicechat.api.VoiceMode;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;

public class ClientSoundEventImpl extends ClientEventImpl implements ClientSoundEvent {

    private short[] rawAudio;
    private boolean whispering;
    private VoiceMode voiceMode;

    public ClientSoundEventImpl(short[] rawAudio, boolean whispering) {
        this.rawAudio = rawAudio;
        this.whispering = whispering;
        this.voiceMode = whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL;
    }

    public ClientSoundEventImpl(short[] rawAudio, VoiceMode voiceMode) {
        this.rawAudio = rawAudio;
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
        this.whispering = this.voiceMode == VoiceMode.WHISPER;
    }

    @Override
    public short[] getRawAudio() {
        return rawAudio;
    }

    @Override
    public void setRawAudio(short[] rawAudio) {
        this.rawAudio = rawAudio;
    }

    @Override
    public boolean isWhispering() {
        return voiceMode == VoiceMode.WHISPER || whispering;
    }

    @Override
    public VoiceMode getVoiceMode() {
        return voiceMode != null ? voiceMode : (whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL);
    }
}
