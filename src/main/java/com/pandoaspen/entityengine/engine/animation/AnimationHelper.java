package com.pandoaspen.entityengine.engine.animation;

import com.pandoaspen.entityengine.engine.EEntity;

import java.io.File;
import java.net.URI;

public class AnimationHelper {

    public static DefaultAnimationHandler getStandAnimationHandler(File file, EEntity host) {
        return getStandAnimationHandler(file.toURI(), host);
    }

    public static DefaultAnimationHandler getStandAnimationHandler(URI uri, EEntity host) {
        try {
            return new DefaultAnimationHandler(uri, host, StandEntityAnimationObject::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
