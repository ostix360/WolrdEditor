package fr.ostix.worldCreator.toolBox;


import com.jme3.math.*;
import fr.ostix.worldCreator.entity.camera.Camera;
import org.joml.Math;
import org.joml.*;

import java.nio.*;
import java.util.Random;

public class Maths {

    // public static final Logger LOGGER = LogManager.getLogger(Math.class);

    /**
     * Shared instance of the +X direction (1,0,0). Do not modify!
     */
    public final static Vector3f UNIT_X = new Vector3f(1, 0, 0);
    /**
     * Shared instance of the +Y direction (0,1,0). Do not modify!
     */
    public final static Vector3f UNIT_Y = new Vector3f(0, 1, 0);
    /**
     * Shared instance of the +Z direction (0,0,1). Do not modify!
     */
    public final static Vector3f UNIT_Z = new Vector3f(0, 0, 1);
    /**
     * Shared instance of the all-ones vector (1,1,1). Do not modify!
     */
    public final static Vector3f UNIT_XYZ = new Vector3f(1, 1, 1);

    /**
     * Shared instance of the all-plus-infinity vector (+Inf,+Inf,+Inf). Do not
     * modify!
     */
    public final static Vector3f POSITIVE_INFINITY = new Vector3f(
            Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY);
    /**
     * Shared instance of the all-negative-infinity vector (-Inf,-Inf,-Inf). Do
     * not modify!
     */
    public final static Vector3f NEGATIVE_INFINITY = new Vector3f(
            Float.NEGATIVE_INFINITY,
            Float.NEGATIVE_INFINITY,
            Float.NEGATIVE_INFINITY);

    public static float clampf(float value, float min, float max) {
        if (value > max) {
            //   LOGGER.warn("Value : " + value + " > " + "max/!\\");
            return max;
        } else if (value < min) {
            //   LOGGER.warn("Value : " + value + " < " + "min/!\\");
            return min;
        }
        return value;
    }

    public static Matrix4f createTransformationMatrix(Vector2f position, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position.x(), position.y(), 0);
        matrix.scale(scale.x(), scale.y(), 0);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(position);
//        matrix.rotateXYZ((float) Math.toRadians(rotation.x()),
//                        (float) Math.toRadians(rotation.y()),
//                        (float) Math.toRadians(rotation.z()));
        matrix.rotate(Math.toRadians(rotation.x()), new Vector3f(1, 0, 0));
        matrix.rotate(Math.toRadians(rotation.y()), new Vector3f(0, 1, 0));
        matrix.rotate(Math.toRadians(rotation.z()), new Vector3f(0, 0, 1));
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera cam) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.rotate(Math.toRadians(cam.getPitch()), new Vector3f(1, 0, 0));
        matrix4f.rotate(Math.toRadians(cam.getYaw()), new Vector3f(0, 1, 0));
        matrix4f.rotate(Math.toRadians(cam.getRoll()), new Vector3f(0, 0, 1));
        Vector3f cameraPos = cam.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        matrix4f.translate(negativeCameraPos);
        return matrix4f;
    }

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }


    /*
     *
     *
     * Math for the particle spawn
     *
     *
     */

    public static Vector3f rotateVector(Vector3f direction, float rotX, float rotY, float rotZ) {
        Matrix4f matrix = createTransformationMatrix(new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(rotX, rotY, rotZ), new Vector3f(1.0F));
        Vector4f direction4 = new Vector4f(direction.x, direction.y, direction.z, 1.0F);
        matrix.transform(direction4, direction4);
        return new Vector3f(direction4.x, direction4.y, direction4.z);
    }

    public static Vector3f generateRandomUnitVector() {
        java.util.Random random = new Random();
        float theta = (float) (random.nextFloat() * 2.0F * java.lang.Math.PI);
        float z = random.nextFloat() * 2.0F - 1.0F;
        float rootOneMinusZSquared = (float) java.lang.Math.sqrt(1.0F - z * z);
        float x = (float) (rootOneMinusZSquared * java.lang.Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * java.lang.Math.sin(theta));
        return new Vector3f(x, y, z);
    }

    public static Quaternion toQuaternion(Quaternionf qRotation) {
        return new Quaternion(qRotation.x(), qRotation.y(), qRotation.z(), qRotation.w());
    }

    /**
     * Interpolates linearly between the specified beginning and final vectors,
     * returning the (modified) current instance.
     *
     * <p>this = (1 - changeAmount) * beginVec + changeAmount * finalVec
     *
     * @param beginVec the desired value when changeAmount=0 (not null, unaffected
     *     unless it's <code>this</code>)
     * @param finalVec the desired value when changeAmount=1 (not null, unaffected
     *     unless it's <code>this</code>)
     * @param changeAmount the fractional change amount
     * @return the (modified) current instance (for chaining)
     */
    public static Vector3f interpolateLocal(Vector3f toInterpolate ,Vector3f beginVec, Vector3f finalVec, float changeAmount) {
        toInterpolate.x = (1 - changeAmount) * beginVec.x + changeAmount * finalVec.x;
        toInterpolate.y = (1 - changeAmount) * beginVec.y + changeAmount * finalVec.y;
        toInterpolate.z = (1 - changeAmount) * beginVec.z + changeAmount * finalVec.z;
        return toInterpolate;
    }

    public static Vector3f multLocal(Quaternionf q,Vector3f v) {
        float tempX, tempY;
        tempX = q.w * q.w * v.x + 2 * q.y * q.w * v.z - 2 * q.z * q.w * v.y + q.x * q.x * v.x
                + 2 * q.y * q.x * v.y + 2 * q.z * q.x * v.z - q.z * q.z * v.x - q.y * q.y * v.x;
        tempY = 2 * q.x * q.y * v.x + q.y * q.y * v.y + 2 * q.z * q.y * v.z + 2 * q.w * q.z
                * v.x - q.z * q.z * v.y + q.w * q.w * v.y - 2 * q.x * q.w * v.z - q.x * q.x
                * v.y;
        v.z = 2 * q.x * q.z * v.x + 2 * q.y * q.z * v.y + q.z * q.z * v.z - 2 * q.w * q.y * v.x
                - q.y * q.y * v.z + 2 * q.w * q.x * v.y - q.x * q.x * v.z + q.w * q.w * v.z;
        v.x = tempX;
        v.y = tempY;
        return v;
    }

    /**
     * Updates the values of the given vector from the specified buffer at the
     * index provided.
     *
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from
     *            the buf
     */
    public static void populateFromBuffer(Vector3f vector, FloatBuffer buf, int index) {
        vector.x = buf.get(index * 3);
        vector.y = buf.get(index * 3 + 1);
        vector.z = buf.get(index * 3 + 2);
    }

    public static void setInBuffer(Vector3f vector, FloatBuffer buf, int index) {
        if (buf == null) {
            return;
        }
        if (vector == null) {
            buf.put(index * 3, 0);
            buf.put((index * 3) + 1, 0);
            buf.put((index * 3) + 2, 0);
        } else {
            buf.put(index * 3, vector.x);
            buf.put((index * 3) + 1, vector.y);
            buf.put((index * 3) + 2, vector.z);
        }
    }

    public static Vector3f setByAxis(int index,float value, Vector3f dest){
        switch (index) {
            case 0:
                dest.x = value;
                return dest;
            case 1:
                dest.y = value;
                return dest;
            case 2:
                dest.z = value;
                return dest;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }


    public static float mulProj(Matrix4f m , Vector3f vec, Vector3f store) {
        float vx = vec.x, vy = vec.y, vz = vec.z;
        store.x = m.m00() * vx + m.m01() * vy + m.m02() * vz + m.m03();
        store.y = m.m10() * vx + m.m11() * vy + m.m12() * vz + m.m13();
        store.z = m.m20() * vx + m.m21() * vy + m.m22() * vz + m.m23();
        return m.m30() * vx + m.m31() * vy + m.m32() * vz + m.m33();
    }

    public static boolean isValidVector(Vector3f vector) {
        if (vector == null) {
            return false;
        }
        if (Float.isNaN(vector.x)
                || Float.isNaN(vector.y)
                || Float.isNaN(vector.z)) {
            return false;
        }
        if (Float.isInfinite(vector.x)
                || Float.isInfinite(vector.y)
                || Float.isInfinite(vector.z)) {
            return false;
        }
        return true;
    }

    public static void absolute(Matrix3f matrix) {
        matrix.m00 = FastMath.abs(matrix.m00);
        matrix.m01 = FastMath.abs(matrix.m01);
        matrix.m02 = FastMath.abs(matrix.m02);
        matrix.m10 = FastMath.abs(matrix.m10);
        matrix.m11 = FastMath.abs(matrix.m11);
        matrix.m12 = FastMath.abs(matrix.m12);
        matrix.m20 = FastMath.abs(matrix.m20);
        matrix.m21 = FastMath.abs(matrix.m21);
        matrix.m22 = FastMath.abs(matrix.m22);
    }


}