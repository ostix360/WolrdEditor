package fr.ostix.worldCreator.toolBox;


import com.flowpowered.react.math.*;
import com.flowpowered.react.math.Quaternion;
import fr.ostix.worldCreator.entity.camera.Camera;
import org.joml.Math;
import org.joml.*;

import java.util.Random;

public class Maths {

    // public static final Logger LOGGER = LogManager.getLogger(Math.class);

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
    public static Vector3 toVector3(Vector3f value) {
        return new Vector3(value.x(), value.y(), value.z());
    }

    public static Vector3f toVector3f(Vector3 value) {
        return new Vector3f(value.getX(), value.getY(), value.getZ());
    }
}