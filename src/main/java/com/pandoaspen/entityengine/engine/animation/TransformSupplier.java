package com.pandoaspen.entityengine.engine.animation;

import lombok.Value;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

@Value
public class TransformSupplier {

    private float[] translation;
    private float[] rotation;
    private float[] meshTranslation;

    private Supplier<float[]> globalTransform;
    private Supplier<float[]> localTransform;

    public Matrix4f getGlobalTransformMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(globalTransform.get());
        return matrix4f;
    }

    public Matrix4f getLocalTransformMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(localTransform.get());
        return matrix4f;
    }

    public Vector3f getTranslationVec() {
        return new Vector3f(translation[0], translation[1], translation[2]);
    }

    public Quaternionf getRotationQuat() {
        return new Quaternionf(rotation[0], rotation[1], rotation[2], rotation[3]);
    }

    public Vector3f getMeshTranslationVec() {
        return new Vector3f(meshTranslation[0], meshTranslation[1], meshTranslation[2]);
    }
}
