package de.maxhenkel.voicechat.api;

public enum VoiceMode {

    NORMAL((byte) 0),
    WHISPER((byte) 1),
    SHOUT((byte) 2);

    private final byte id;

    VoiceMode(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public boolean isWhispering() {
        return this == WHISPER;
    }

    public boolean isShouting() {
        return this == SHOUT;
    }

    public static VoiceMode fromId(byte id) {
        for (VoiceMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return NORMAL;
    }

    public static VoiceMode fromId(int id) {
        return fromId((byte) id);
    }

    public static VoiceMode fromWhispering(boolean whispering) {
        return whispering ? WHISPER : NORMAL;
    }

}
