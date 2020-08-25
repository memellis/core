package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class VersionInfoFile {
    private VersionInfo versionInfo;

    public VersionInfoFile() {
    }

    public VersionInfoFile(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public void saveVersionInfo(String filename) {
        Json json = new Json();
        json.toJson(versionInfo, VersionInfo.class, Gdx.files.local(filename));
    }

    public VersionInfo loadVersionInfo(String myVersionInfo) {
        Json json = new Json();
        return json.fromJson(VersionInfo.class, Gdx.files.local(myVersionInfo));
    }
}
