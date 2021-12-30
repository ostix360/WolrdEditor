package fr.ostix.worldCreator.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL11.*;

public class SoundSource {

    private final int sourceID;

    public SoundSource(boolean isAmbient) {
        this.sourceID = alGenSources();
        alSourcei(sourceID, AL_SOURCE_RELATIVE, isAmbient ? AL_TRUE : AL_FALSE);
//        alSourcef(sourceID, AL_ROLLOFF_FACTOR, rollOffFactor);
//        alSourcef(sourceID, AL_REFERENCE_DISTANCE, referenceDistance);
//        alSourcef(sourceID, AL_MAX_DISTANCE, maxDistance);
    }

    public void setLooping(boolean looping) {
        alSourcei(sourceID, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    public void setSound(int bufferId) {
        stop();
        alSourcei(sourceID, AL_BUFFER, bufferId);
    }

    public void setPosition(Vector3f position) {
        alSource3f(sourceID, AL_POSITION, position.x(), position.y(), position.z());
    }

    public void setSpeed(Vector3f speed) {
        alSource3f(sourceID, AL_VELOCITY, speed.x(), speed.y(), speed.z());
    }

    public void setGain(float gain) {
        alSourcef(sourceID, AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        alSourcef(sourceID, param, value);
    }

    public void play() {
        alSourcePlay(sourceID);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceID);
    }

    public void stop() {
        alSourceStop(sourceID);
    }

    public void cleanup() {
        stop();
        alDeleteSources(sourceID);
    }
}
