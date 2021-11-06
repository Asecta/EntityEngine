package au.edu.federation.utils;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public class VectorUtils {

    private static final float DEGS_TO_RADS = (float) Math.PI / 180.0f;
    private static final float RADS_TO_DEGS = 180.0f / (float) Math.PI;

    // Cardinal axes
    private static final Vector3f X_AXIS = new Vector3f(1.0f, 0.0f, 0.0f);
    private static final Vector3f Y_AXIS = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f Z_AXIS = new Vector3f(0.0f, 0.0f, 1.0f);

    private static final Vector3f NEG_X_AXIS = X_AXIS.negate(new Vector3f());
    private static final Vector3f NEG_Y_AXIS = Y_AXIS.negate(new Vector3f());
    private static final Vector3f NEG_Z_AXIS = Z_AXIS.negate(new Vector3f());

    public static final Vector3f getDirectionUV(Vector3f v1, Vector3f v2) {
        return v2.sub(v1, new Vector3f()).normalize();
    }

    public static final float getGlobalPitchDegs(Vector3f vector3f) {
        Vector3f xProjected = projectOntoPlane(vector3f, X_AXIS);

        float pitch = getAngleBetweenDegs(NEG_Z_AXIS, xProjected);
        return xProjected.y < 0.0f ? -pitch : pitch;
    }

    public static final float getGlobalYawDegs(Vector3f vector3f) {
        Vector3f yProjected = projectOntoPlane(vector3f, Y_AXIS);
        float yaw = getAngleBetweenDegs(NEG_Z_AXIS, yProjected);
        return yProjected.x < 0.0f ? -yaw : yaw;
    }

    public static final Vector3f projectOntoPlane(Vector3f vector3f, Vector3f planeNormal) {
        if (!(planeNormal.length() > 0.0f)) {
            throw new IllegalArgumentException("Plane normal cannot be a zero vector.");
        }

        Vector3f b = vector3f.normalize(new Vector3f());
        Vector3f n = planeNormal.normalize();

        float dot = b.dot(planeNormal);

        return b.sub(n.mul(dot)).normalize();
    }

    public static final float getAngleBetweenDegs(Vector3f v1, Vector3f v2) {
        return getAngleBetweenRads(v1, v2) * RADS_TO_DEGS;
    }

    public static final float getAngleBetweenRads(Vector3f v1, Vector3f v2) {
        return (float) Math.acos(v1.dot(v2));
    }

    public static Vector3f genPerpendicularVectorQuick(Vector3f u) {
        Vector3f perp;

        if (Math.abs(u.y) < 0.99f) {
            perp = new Vector3f(-u.z, 0.0f, u.x); // cross(u, UP)
        } else {
            perp = new Vector3f(0.0f, u.z, -u.y); // cross(u, RIGHT)
        }

        return perp.normalize();
    }

    public static boolean perpendicular(Vector3f a, Vector3f b) {
        return Utils.approximatelyEquals(a.dot(b), 0.0f, 0.01f) ? true : false;
    }

    public static Vector3f getAngleLimitedUnitVectorDegs(Vector3f vecToLimit, Vector3f vecBaseline, float angleLimitDegs) {
        // Get the angle between the two vectors
        // Note: This will ALWAYS be a positive value between 0 and 180 degrees.
        float angleBetweenVectorsDegs = getAngleBetweenDegs(vecBaseline, vecToLimit);

        if (angleBetweenVectorsDegs > angleLimitDegs) {
            // The axis which we need to rotate around is the one perpendicular to the two vectors - so we're
            // rotating around the vector which is the cross-product of our two vectors.
            // Note: We do not have to worry about both vectors being the same or pointing in opposite directions
            // because if they bones are the same direction they will not have an angle greater than the angle limit,
            // and if they point opposite directions we will approach but not quite reach the precise max angle
            // limit of 180.0f (I believe).

            Vector3f baselineNorm = vecBaseline.normalize(new Vector3f());
            Vector3f limitNorm = vecToLimit.normalize(new Vector3f());

            Vector3f correctionAxis = baselineNorm.cross(limitNorm, new Vector3f());

            // Our new vector is the baseline vector rotated by the max allowable angle about the correction axis
            return rotateAboutAxisDegs(vecBaseline, angleLimitDegs, correctionAxis).normalize();
        } else // Angle not greater than limit? Just return a normalised version of the vecToLimit
        {
            // This may already BE normalised, but we have no way of knowing without calcing the length, so best be safe and normalise.
            // TODO: If performance is an issue, then I could get the length, and if it's not approx. 1.0f THEN normalise otherwise just return as is.
            return vecToLimit.normalize(new Vector3f());
        }
    }

    public static Vector3f rotateAboutAxisRads(Vector3f source, float angleRads, Vector3f rotationAxis) {
        Matrix3f rotationMatrix = new Matrix3f();

        float sinTheta = (float) Math.sin(angleRads);
        float cosTheta = (float) Math.cos(angleRads);
        float oneMinusCosTheta = 1.0f - cosTheta;

        // It's quicker to pre-calc these and reuse than calculate x * y, then y * x later (same thing).
        float xyOne = rotationAxis.x * rotationAxis.y * oneMinusCosTheta;
        float xzOne = rotationAxis.x * rotationAxis.z * oneMinusCosTheta;
        float yzOne = rotationAxis.y * rotationAxis.z * oneMinusCosTheta;

        // Calculate rotated x-axis
        rotationMatrix.m00 = rotationAxis.x * rotationAxis.x * oneMinusCosTheta + cosTheta;
        rotationMatrix.m01 = xyOne + rotationAxis.z * sinTheta;
        rotationMatrix.m02 = xzOne - rotationAxis.y * sinTheta;

        // Calculate rotated y-axis
        rotationMatrix.m10 = xyOne - rotationAxis.z * sinTheta;
        rotationMatrix.m11 = rotationAxis.y * rotationAxis.y * oneMinusCosTheta + cosTheta;
        rotationMatrix.m12 = yzOne + rotationAxis.x * sinTheta;

        // Calculate rotated z-axis
        rotationMatrix.m20 = xzOne + rotationAxis.y * sinTheta;
        rotationMatrix.m21 = yzOne - rotationAxis.x * sinTheta;
        rotationMatrix.m22 = rotationAxis.z * rotationAxis.z * oneMinusCosTheta + cosTheta;

        // Multiply the source by the rotation matrix we just created to perform the rotation
        return matMulVec(rotationMatrix, source);
    }

    public static Vector3f rotateAboutAxisDegs(Vector3f source, float angleDegs, Vector3f rotationAxis) {
        return rotateAboutAxisRads(source, angleDegs * DEGS_TO_RADS, rotationAxis);
    }

    public static float getSignedAngleBetweenDegs(Vector3f referenceVector, Vector3f otherVector, Vector3f normalVector) {
        float unsignedAngle = getAngleBetweenDegs(referenceVector, otherVector);
        float sign = Utils.sign(referenceVector.cross(otherVector, new Vector3f()).dot(normalVector));
        return unsignedAngle * sign;
    }

    public static final Vector3f matMulVec(Matrix3f mat, Vector3f source) {
        //@formatter:off
        return new Vector3f(
                mat.m00 * source.x + mat.m10 * source.y + mat.m20 * source.z,
                mat.m01 * source.x + mat.m11 * source.y + mat.m21 * source.z,
                mat.m02 * source.x + mat.m12 * source.y + mat.m22 * source.z);
        //@formatter:on
    }

    public static Matrix3f createRotationMatrix(Vector3f v) {
        float m00, m01, m02;
        float m10, m11, m12;
        float m20, m21, m22;

        float x = v.x, y = v.y, z = v.z;

        //@formatter:off
        if (Math.abs(v.y) > 0.9999f) {
            m00 = 1; m01 =  0; m02 = 0;
            m10 = 0; m11 = -z; m12 = y;
            m20 = x; m21 =  y; m22 = z;
        } else {
            m00 = -z; m01 = 0; m02 = x;
            m20 =  x; m21 = y; m22 = z;
            m10 = -(m02 * m21); m11 = m02 * m20 - m00 * m22; m12 = m00 * m21;
        }
        //@formatter:on

        return new Matrix3f(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }
}

