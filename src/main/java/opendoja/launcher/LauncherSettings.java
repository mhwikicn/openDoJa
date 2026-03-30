package opendoja.launcher;

import opendoja.audio.mld.MldSynth;

record LauncherSettings(int hostScale, String synthId) {
    LauncherSettings {
        hostScale = normalizeHostScale(hostScale);
        synthId = normalizeSynthId(synthId);
    }

    static LauncherSettings defaults() {
        return new LauncherSettings(1, MldSynth.DEFAULT.id);
    }

    private static int normalizeHostScale(int candidate) {
        return Math.max(1, Math.min(4, candidate));
    }

    private static String normalizeSynthId(String candidate) {
        MldSynth synth = MldSynth.fromId(candidate);
        return synth == null ? MldSynth.DEFAULT.id : synth.id;
    }
}
