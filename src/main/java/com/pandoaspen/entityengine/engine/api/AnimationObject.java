package com.pandoaspen.entityengine.engine.api;

import de.javagl.jgltf.model.NodeModel;
import org.joml.Matrix4f;

public interface AnimationObject {

    /**
     * Updates the objects position with the new world transformation matrix
     * @param matrix4f Matrix4x4 describing world transforms for this animation object
     */

    void update(Matrix4f matrix4f);

    /**
     * Initializes the animation object with the given world transform and NodeModel
     *
     * @param matrix4f Matrix4x4 describing world transforms for this animation object
     * @param nodeModel The parent NodeModel, used to extract visual metadata etc.
     */

    void create(Matrix4f matrix4f, NodeModel nodeModel);

    /**
     * Destroys the animation object instance in and deconstructs the object.
     */

    void destroy();

}
