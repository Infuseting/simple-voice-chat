package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.api.VoiceMode;
import net.minecraft.network.FriendlyByteBuf;

public class MicPacket implements Packet<MicPacket> {

    private byte[] data;
    private boolean whispering;
    private VoiceMode voiceMode;
    private long sequenceNumber;

    public MicPacket(byte[] data, boolean whispering, long sequenceNumber) {
        this.data = data;
        this.whispering = whispering;
        this.voiceMode = whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL;
        this.sequenceNumber = sequenceNumber;
    }

    public MicPacket(byte[] data, VoiceMode voiceMode, long sequenceNumber) {
        this.data = data;
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
        this.whispering = this.voiceMode == VoiceMode.WHISPER;
        this.sequenceNumber = sequenceNumber;
    }

    public MicPacket() {
        this.voiceMode = VoiceMode.NORMAL;
    }

    @Override
    public long getTTL() {
        return 500L;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public boolean isWhispering() {
        return voiceMode == VoiceMode.WHISPER || whispering;
    }

    public VoiceMode getVoiceMode() {
        return voiceMode != null ? voiceMode : (whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL);
    }

    public void setVoiceMode(VoiceMode voiceMode) {
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
        this.whispering = this.voiceMode == VoiceMode.WHISPER;
    }

    @Override
    public MicPacket fromBytes(FriendlyByteBuf buf) {
        MicPacket soundPacket = new MicPacket();
        soundPacket.data = buf.readByteArray(AudioUtils.MAX_OPUS_PAYLOAD_SIZE);
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.whispering = buf.readBoolean();
        if (buf.isReadable()) {
            soundPacket.voiceMode = VoiceMode.fromId(buf.readByte());
            soundPacket.whispering = soundPacket.voiceMode == VoiceMode.WHISPER;
        } else {
            soundPacket.voiceMode = soundPacket.whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL;
        }
        return soundPacket;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
        buf.writeBoolean(isWhispering());
        buf.writeByte(getVoiceMode().getId());
    }
}
