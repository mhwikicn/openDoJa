package com.acrodea.xf3;

import java.io.InputStream;
import opendoja.host.DoJaRuntime;

public class xfeTextureResource implements xfeResource {
    private String resourceName;

    public boolean load(String resourceName) {
        this.resourceName = resourceName;
        try (InputStream stream = DoJaRuntime.openLaunchResourceStream(resourceName)) {
            return stream != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getResourceName() {
        return resourceName;
    }
}
