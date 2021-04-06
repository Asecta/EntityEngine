package com.pandoaspen.entityengine.engine.api;

import de.javagl.jgltf.model.NodeModel;

import java.util.Collection;
import java.util.Set;

public interface AnimationHandler {

    /**
     * Gets the name of the currently running animation
     *
     * @return the current animation name
     */

    String getCurrentAnimation();

    /**
     * Sets the currently running animation to the one named as provided
     *
     * @param currentAnimation The name of the new animation to use
     * @return true if the animation was found and applied successfully.
     */

    boolean setCurrentAnimation(String currentAnimation);

    /**
     * Returns a collection of all the available animation names known to this handler.
     *
     * @return A collection of known animation names
     */

    Collection<String> listAnimations();

    /**
     * Steps the animation forward by the given delta time (time since started)
     *
     * @param deltaNs The new delta time
     */

    void update(long deltaNs);

    /**
     * Clears the current animation and destroys all attached animation objects.
     */

    void destroy();

    NodeModel getNodeModel(String name);

}
