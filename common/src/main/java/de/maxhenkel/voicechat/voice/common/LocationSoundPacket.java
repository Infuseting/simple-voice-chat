package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.api.VoiceMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class LocationSoundPacket extends SoundPacket<LocationSoundPacket> {

    protected Vec3 location;
    protected float distance;
    protected VoiceMode voiceMode;

    public LocationSoundPacket(UUID channelId, UUID sender, Vec3 location, byte[] data, long sequenceNumber, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.location = location;
        this.distance = distance;
        this.voiceMode = VoiceMode.NORMAL;
    }

    public LocationSoundPacket(UUID channelId, UUID sender, Vec3 location, byte[] data, long sequenceNumber, VoiceMode voiceMode, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.location = location;
        this.distance = distance;
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
    }

    public LocationSoundPacket(UUID channelId, UUID sender, short[] data, Vec3 location, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.location = location;
        this.distance = distance;
        this.voiceMode = VoiceMode.NORMAL;
    }

    public LocationSoundPacket(UUID channelId, UUID sender, short[] data, Vec3 location, VoiceMode voiceMode, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.location = location;
        this.distance = distance;
        this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
    }

    public LocationSoundPacket() {
        this.voiceMode = VoiceMode.NORMAL;
    }

    public Vec3 getLocation() {
        return location;
    }

    public float getDistance() {
        return distance;
    }

    public VoiceMode getVoiceMode() {
        return voiceMode != null ? voiceMode : VoiceMode.NORMAL;
    }

    @Override
    public LocationSoundPacket fromBytes(FriendlyByteBuf buf) {
        LocationSoundPacket soundPacket = new LocationSoundPacket();
        soundPacket.channelId = buf.readUUID();
        soundPacket.sender = buf.readUUID();
        soundPacket.location = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        soundPacket.data = buf.readByteArray(AudioUtils.MAX_OPUS_PAYLOAD_SIZE);
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.distance = buf.readFloat();

        byte data = buf.readByte();
        if (hasFlag(data, WHISPER_MASK)) {
            soundPacket.voiceMode = VoiceMode.WHISPER;
        } else if (hasFlag(data, SHOUT_MASK)) {
            soundPacket.voiceMode = VoiceMode.SHOUT;
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
        buf.writeDouble(location.x);
        buf.writeDouble(location.y);
        buf.writeDouble(location.z);
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
        buf.writeFloat(distance);

        byte data = 0b0;
        if (voiceMode != null && voiceMode.isWhispering()) {
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
