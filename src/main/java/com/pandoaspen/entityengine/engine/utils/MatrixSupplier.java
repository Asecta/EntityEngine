package com.pandoaspen.entityengine.engine.utils;

import de.javagl.jgltf.model.NodeModel;
import lombok.Value;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

@Value
public class MatrixSupplier {

    private NodeModel nodeModel;
    private Supplier<float[]> floatSupplier;

    public Matrix4f get(Vector3f origin, Vector3f direction) {
        float[] floats = floatSupplier.get();
        Matrix4f positionMatrix = new Matrix4f();
        positionMatrix.set(floats);

        Matrix4f matrix = new Matrix4f();
        matrix.translate(origin);
        matrix.lookAlong(new Vector3f(direction.x, direction.y, direction.z), new Vector3f(0, -1, 0));
        matrix.rotate(new Quaternionf(1, 0, 0, 0));

        matrix.mul(positionMatrix);

        return matrix;
    }
}