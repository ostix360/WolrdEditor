package fr.ostix.worldCreator.audio;

import fr.ostix.worldCreator.toolBox.Config;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class AudioManager {

    private static long device;
    private static long context;

    private static final List<Integer> buffers = new ArrayList<>();
    private static final List<SoundSource> sounds = new ArrayList<>();


    public static void init(int attenuationModel) {
        device = ALC10.alcOpenDevice((ByteBuffer) null);

        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        alDistanceModel(attenuationModel);

    }


    public static SoundSource loadSound(String path, boolean isAmbient) {
        int bufferID = AL10.alGenBuffers();
        buffers.add(bufferID);
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ByteBuffer data = loadOggSound(path, info);
            AL10.alBufferData(bufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, data, info.sample_rate());

            int error = AL10.alGetError();
            if (error != 0) {
                System.err.println("Problem loading sound data into buffer. " + error);
            }
        }
        SoundSource sound = new SoundSource(isAmbient);
        sound.setSound(bufferID);
        sounds.add(sound);
        return sound;
    }


    private static ByteBuffer loadOggSound(String path, STBVorbisInfo info) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_filename(Config.REPOSITORY_FOLDER + "/sounds/" + path + ".ogg", error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            ByteBuffer pcm = BufferUtils.createByteBuffer(lengthSamples * Short.BYTES * channels);
            stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm.asShortBuffer());
            stb_vorbis_close(decoder);
            return pcm;
        }
    }


    public static void cleanUp() {
        for (SoundSource sound : sounds) {
            sound.cleanup();
        }
        sounds.clear();
        for (int buffer : buffers) {
            AL10.alDeleteBuffers(buffer);
        }
        buffers.clear();

        if (context != NULL) {
            ALC10.alcDestroyContext(context);
        }
        if (device != NULL) {
            ALC10.alcCloseDevice(device);
        }
    }
}
