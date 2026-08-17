package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.api.VoiceMode;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerSoundPacket extends SoundPacket<PlayerSoundPacket> {

    protected boolean whispering;
    protected VoiceMode voiceMode;
    protected float distance;

    public PlayerSoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, boolean whispering, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.whispering = whispering;
        this.voiceMode = whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL;
        this.distance = distance;
    }

    public PlayerSoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, VoiceMode voiceMode, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
        this.whispering = this.voiceMode == VoiceMode.WHISPER;
        this.distance = distance;
    }

    public PlayerSoundPacket(UUID channelId, UUID sender, short[] data, boolean whispering, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.whispering = whispering;
        this.voiceMode = whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL;
        this.distance = distance;
    }

    public PlayerSoundPacket(UUID channelId, UUID sender, short[] data, VoiceMode voiceMode, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
        this.whispering = this.voiceMode == VoiceMode.WHISPER;
        this.distance = distance;
    }

    public PlayerSoundPacket() {
        this.voiceMode = VoiceMode.NORMAL;
    }

    public UUID getSender() {
        return sender;
    }

    public boolean isWhispering() {
        return (voiceMode != null && voiceMode.isWhispering()) || whispering;
    }

    public VoiceMode getVoiceMode() {
        return voiceMode != null ? voiceMode : (whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL);
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public PlayerSoundPacket fromBytes(FriendlyByteBuf buf) {
        PlayerSoundPacket soundPacket = new PlayerSoundPacket();
        soundPacket.channelId = buf.readUUID();
        soundPacket.sender = buf.readUUID();
        soundPacket.data = buf.readByteArray(AudioUtils.MAX_OPUS_PAYLOAD_SIZE);
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.distance = buf.readFloat();

        byte data = buf.readByte();
        soundPacket.whispering = hasFlag(data, WHISPER_MASK);
        if (hasFlag(data, SHOUT_MASK)) {
            soundPacket.voiceMode = VoiceMode.SHOUT;
        } else if (soundPacket.whispering) {
            soundPacket.voiceMode = VoiceMode.WHISPER;
        } else {
            soundPacket.voiceMode = VoiceMode.NORMAL;
        }
        if (hasFlag(data, HAS_CATEGORY_MASK)) {
            soundPacket.category = buf.readUtf(16);
        }
        return soundPacket;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(channelId);
        buf.writeUUID(sender);
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
        buf.writeFloat(distance);

        byte data = 0b0;
        if (whispering || (voiceMode != null && voiceMode.isWhispering())) {
            data = setFlag(data, WHISPER_MASK);
        }
        if (voiceMode != null && voiceMode.isShouting()) {
            data = setFlag(data, SHOUT_MASK);
        }
        if (category != null) {
            data = setFlag(data, HAS_CATEGORY_MASK);
        }
        buf.writeByte(data);
        if (category != null) {
            buf.writeUtf(category, 16);
        }
    }

}
