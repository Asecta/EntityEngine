package au.edu.federation.utils;

import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Class  : A series of static utility / helper methods to perform common operations.
 * Version: 0.4
 * Date   : 04/12/2015
 */
public final class Utils {


    /**
     * The maximum length in characters of any names which may be used for bones, chains or structures.
     */
    public static final int MAX_NAME_LENGTH = 100;

    private Utils() {
    }

    /**
     * Determine the sign of a float value.
     *
     * @param    value    The value to return the sign of.
     * @return 1.0f if the provided float value is positive, -1.0f otherwise.
     */
    public static float sign(float value) {
        return value >= .0f ? 1f : -1f;
    }

    /**
     * Validate a direction unit vector (Vec3f) to ensure that it does not have a magnitude of zero.
     * <p>
     * If the direction unit vector has a magnitude of zero then an IllegalArgumentException is thrown.
     *
     * @param    directionUV    The direction unit vector to validate
     */
    public static void validateDirectionUV(Vector3f directionUV) {
        // Ensure that the magnitude of this direction unit vector is greater than zero
        if (directionUV.length() <= 0.0f) {
            throw new IllegalArgumentException("Vec3f direction unit vector cannot be zero.");
        }
    }

    /**
     * Validate the length of a bone to ensure that it's a positive value.
     * <p>
     * If the provided bone length is not greater than zero then an IllegalArgumentException is thrown.
     *
     * @param    length    The length value to validate.
     */
    public static void validateLength(float length) {
        // Ensure that the magnitude of this direction unit vector is not zero
        if (length < 0.0f) {
            throw new IllegalArgumentException("Length must be a greater than or equal to zero.");
        }
    }

    /**
     * Return a boolean indicating whether a float approximately equals another to within a given tolerance.
     *
     * @param    a        The first value
     * @param    b        The second value
     * @param    tolerance    The difference within the <strong>a</strong> and <strong>b</strong> values must be within to be considered approximately equal.
     * @return Whether the a and b values are approximately equal or not.
     */
    public static boolean approximatelyEquals(float a, float b, float tolerance) {
        return (Math.abs(a - b) <= tolerance) ? true : false;
    }

    /**
     * Ensure we have a legal line width with which to draw.
     * <p>
     * Valid line widths are between 1.0f and 32.0f pixels inclusive.
     * <p>
     * Line widths outside this range will cause an IllegalArgumentException to be thrown.
     *
     * @param    lineWidth    The width of the line we are validating.
     */
    public static void validateLineWidth(float lineWidth) {
        if (lineWidth < 1.0f || lineWidth > 32.0f) {
            throw new IllegalArgumentException("Line widths must be within the range 1.0f to 32.0f - but only 1.0f is guaranteed to be supported.");
        }
    }

}