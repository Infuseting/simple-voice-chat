package de.maxhenkel.voicechat.plugins.impl.packets;

import de.maxhenkel.voicechat.api.packets.EntitySoundPacket;
import de.maxhenkel.voicechat.voice.common.PlayerSoundPacket;
import de.maxhenkel.voicechat.voice.common.Utils;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntitySoundPacketImpl extends SoundPacketImpl implements EntitySoundPacket {

    private final PlayerSoundPacket packet;

    public EntitySoundPacketImpl(PlayerSoundPacket packet) {
        super(packet);
        this.packet = packet;
    }

    @Override
    public UUID getEntityUuid() {
        return packet.getSender();
    }

    @Override
    public boolean isWhispering() {
        return packet.isWhispering();
    }

    @Override
    public de.maxhenkel.voicechat.api.VoiceMode getVoiceMode() {
        return packet.getVoiceMode();
    }

    @Override
    public float getDistance() {
        return packet.getDistance();
    }

    @Override
    public PlayerSoundPacket getPacket() {
        return packet;
    }

    @Override
    public UUID getChannelId() {
        return packet.getChannelId();
    }

    public static class BuilderImpl extends SoundPacketImpl.BuilderImpl<BuilderImpl, EntitySoundPacket> implements EntitySoundPacket.Builder<BuilderImpl> {

        protected UUID entityUuid;
        protected boolean whispering;
        protected de.maxhenkel.voicechat.api.VoiceMode voiceMode;
        protected float distance;

        public BuilderImpl(SoundPacketImpl soundPacket) {
            super(soundPacket);
            if (soundPacket instanceof EntitySoundPacketImpl p) {
                entityUuid = p.getEntityUuid();
                whispering = p.isWhispering();
                voiceMode = p.getVoiceMode();
                distance = p.getDistance();
            } else if (soundPacket instanceof LocationalSoundPacketImpl p) {
                distance = p.getDistance();
            } else {
                distance = Utils.getDefaultDistanceServer();
            }
        }

        public BuilderImpl(UUID channelId, UUID sender, byte[] opusEncodedData, long sequenceNumber, @Nullable String category) {
            super(channelId, sender, opusEncodedData, sequenceNumber, category);
            this.distance = Utils.getDefaultDistanceServer();
        }

        @Override
        public BuilderImpl entityUuid(UUID entityUuid) {
            this.entityUuid = entityUuid;
            return this;
        }

        @Override
        public BuilderImpl whispering(boolean whispering) {
            this.whispering = whispering;
            this.voiceMode = whispering ? de.maxhenkel.voicechat.api.VoiceMode.WHISPER : de.maxhenkel.voicechat.api.VoiceMode.NORMAL;
            return this;
        }

        @Override
        public BuilderImpl voiceMode(de.maxhenkel.voicechat.api.VoiceMode voiceMode) {
            this.voiceMode = voiceMode;
            this.whispering = voiceMode != null && voiceMode.isWhispering();
            return this;
        }

        @Override
        public BuilderImpl distance(float distance) {
            this.distance = distance;
            return this;
        }

        @Override
        public EntitySoundPacket build() {
            if (entityUuid == null) {
                throw new IllegalStateException("entityUuid missing");
            }
            de.maxhenkel.voicechat.api.VoiceMode mode = voiceMode != null ? voiceMode : (whispering ? de.maxhenkel.voicechat.api.VoiceMode.WHISPER : de.maxhenkel.voicechat.api.VoiceMode.NORMAL);
            return new EntitySoundPacketImpl(new PlayerSoundPacket(channelId, sender, opusEncodedData, sequenceNumber, mode, distance, category));
        }

    }

}
