package de.maxhenkel.voicechat.voice.client;

import de.maxhenkel.voicechat.api.VoiceMode;
import de.maxhenkel.voicechat.voice.common.AudioUtils;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class TalkCache {

    private static final long TIMEOUT = 250L;

    private static final PlayerCache DEFAULT = new PlayerCache(0L, VoiceMode.NORMAL, AudioUtils.LOWEST_DB);

    private final Map<UUID, PlayerCache> playerCache;
    private final Map<String, CategoryCache> categoryCache;
    private Supplier<Long> timestampSupplier;

    public TalkCache() {
        this.playerCache = new HashMap<>();
        this.categoryCache = new HashMap<>();
        this.timestampSupplier = System::currentTimeMillis;
    }

    public void setTimestampSupplier(Supplier<Long> timestampSupplier) {
        this.timestampSupplier = timestampSupplier;
    }

    private void updateTalking(UUID entity, VoiceMode voiceMode, double audioLevel) {
        PlayerCache talk = playerCache.get(entity);
        if (talk == null) {
            talk = new PlayerCache(timestampSupplier.get(), voiceMode, audioLevel);
            playerCache.put(entity, talk);
        } else {
            talk.timestamp = timestampSupplier.get();
            talk.voiceMode = voiceMode;
            talk.audioLevel = audioLevel;
        }
    }

    /**
     * Updates the audio level of a player talking or a specific category
     *
     * @param id         the entity UUID
     * @param category   the category name or null if it is a player
     * @param whispering if the entity is whispering
     * @param audio      the audio data to calculate the audio level from
     */
    public void updateLevel(UUID id, @Nullable String category, boolean whispering, short[] audio) {
        updateLevel(id, category, whispering ? VoiceMode.WHISPER : VoiceMode.NORMAL, audio);
    }

    /**
     * Updates the audio level of a player talking or a specific category
     *
     * @param id        the entity UUID
     * @param category  the category name or null if it is a player
     * @param voiceMode the voice mode
     * @param audio     the audio data to calculate the audio level from
     */
    public void updateLevel(UUID id, @Nullable String category, VoiceMode voiceMode, short[] audio) {
        double highestAudioLevel = AudioUtils.getHighestAudioLevel(audio);
        if (category != null) {
            updateCategoryVolume(category, highestAudioLevel);
        }
        // Update the player talking even if it is a category
        updateTalking(id, voiceMode, highestAudioLevel);
    }

    public boolean isTalking(Entity entity) {
        return isTalking(entity.getUUID());
    }

    public boolean isWhispering(Entity entity) {
        return isWhispering(entity.getUUID());
    }

    public boolean isShouting(Entity entity) {
        return isShouting(entity.getUUID());
    }

    public boolean isTalking(UUID entity) {
        if (entity.equals(ClientManager.getPlayerStateManager().getOwnID())) {
            ClientVoicechat client = ClientManager.getClient();
            if (client != null && client.getMicThread() != null) {
                if (client.getMicThread().isTalking()) {
                    return true;
                }
            }
        }

        PlayerCache lastTalk = playerCache.getOrDefault(entity, DEFAULT);
        return timestampSupplier.get() - lastTalk.timestamp < TIMEOUT;
    }

    public boolean isWhispering(UUID entity) {
        if (entity.equals(ClientManager.getPlayerStateManager().getOwnID())) {
            ClientVoicechat client = ClientManager.getClient();
            if (client != null && client.getMicThread() != null) {
                if (client.getMicThread().isWhispering()) {
                    return true;
                }
            }
        }

        PlayerCache lastTalk = playerCache.getOrDefault(entity, DEFAULT);
        return lastTalk.voiceMode == VoiceMode.WHISPER && timestampSupplier.get() - lastTalk.timestamp < TIMEOUT;
    }

    public boolean isShouting(UUID entity) {
        if (entity.equals(ClientManager.getPlayerStateManager().getOwnID())) {
            ClientVoicechat client = ClientManager.getClient();
            if (client != null && client.getMicThread() != null) {
                if (client.getMicThread().getVoiceMode() == VoiceMode.SHOUT && client.getMicThread().isTalking()) {
                    return true;
                }
            }
        }

        PlayerCache lastTalk = playerCache.getOrDefault(entity, DEFAULT);
        return lastTalk.voiceMode == VoiceMode.SHOUT && timestampSupplier.get() - lastTalk.timestamp < TIMEOUT;
    }

    public VoiceMode getVoiceMode(UUID entity) {
        if (entity.equals(ClientManager.getPlayerStateManager().getOwnID())) {
            ClientVoicechat client = ClientManager.getClient();
            if (client != null && client.getMicThread() != null) {
                return client.getMicThread().getVoiceMode();
            }
        }

        PlayerCache lastTalk = playerCache.getOrDefault(entity, DEFAULT);
        return lastTalk.voiceMode != null ? lastTalk.voiceMode : VoiceMode.NORMAL;
    }

    public void updateCategoryVolume(String category, double audioLevel) {
        CategoryCache cache = categoryCache.get(category);
        if (cache == null) {
            cache = new CategoryCache(timestampSupplier.get(), audioLevel);
            categoryCache.put(category, cache);
        } else {
            cache.timestamp = timestampSupplier.get();
            cache.audioLevel = audioLevel;
        }
    }

    public double getPlayerAudioLevel(UUID entity) {
        PlayerCache talk = playerCache.getOrDefault(entity, DEFAULT);
        if (timestampSupplier.get() - talk.timestamp >= TIMEOUT) {
            return AudioUtils.LOWEST_DB;
        }
        return talk.audioLevel;
    }

    public double getCategoryAudioLevel(String category) {
        CategoryCache cache = categoryCache.get(category);
        if (cache == null) {
            return AudioUtils.LOWEST_DB;
        }
        if (timestampSupplier.get() - cache.timestamp >= TIMEOUT) {
            return AudioUtils.LOWEST_DB;
        }
        return cache.audioLevel;
    }

    private static class PlayerCache {
        private long timestamp;
        private VoiceMode voiceMode;
        private double audioLevel;

        public PlayerCache(long timestamp, VoiceMode voiceMode, double audioLevel) {
            this.timestamp = timestamp;
            this.voiceMode = voiceMode != null ? voiceMode : VoiceMode.NORMAL;
            this.audioLevel = audioLevel;
        }
    }

    private static class CategoryCache {
        private long timestamp;
        private double audioLevel;

        public CategoryCache(long timestamp, double audioLevel) {
            this.timestamp = timestamp;
            this.audioLevel = audioLevel;
        }
    }

}
