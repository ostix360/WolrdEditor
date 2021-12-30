package fr.ostix.worldCreator.audio;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

public class SoundListener {

    public SoundListener(Vector3f pos, Vector3f velocity, Vector3f rot) {
        this.setVelocity(velocity);
        this.updateTransform(pos, rot);
    }


    public void setVelocity(Vector3f v) {
        alListener3f(AL_VELOCITY, v.x(), v.y(), v.z());
    }

    public void updateTransform(Vector3f pos, Vector3f rotation) {
        alListener3f(AL_POSITION, pos.x(), pos.y(), pos.z());
        Matrix4f matrix4f = new Matrix4f().identity();
        matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        matrix4f.translate(-pos.x(), -pos.y(), -pos.z());
        Vector3f at = new Vector3f();
        matrix4f.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        matrix4f.positiveY(up);
        float[] data = new float[6];
        data[0] = at.x();
        data[1] = at.y();
        data[2] = -at.z();
        data[3] = up.x();
        data[4] = up.y();
        data[5] = up.z();
        alListenerfv(AL_ORIENTATION, data);
    }
}
