package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.Spatial;
import lombok.Getter;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

final class ALListener implements Spatial {
    private final ALScene scene;
    private final ALBackend backend;

    @Getter
    private float x = 0f;
    @Getter
    private float y = 0f;
    @Getter
    private float z = 0f;

    @Getter
    private float yaw = 0f;
    @Getter
    private float elevation = 0f;
    @Getter
    private float roll = 0f;

    @Getter
    private final Vector3f lookVector = new Vector3f(0f, 0f, -1f);
    @Getter
    private final Vector3f upVector = new Vector3f(0f, 1f, 0f);
    private final FloatBuffer cacheBuffer = MemoryUtil.memAllocFloat(6);

    public ALListener(ALScene scene, ALBackend backend) {
        this.scene = scene;
        this.backend = backend;
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(getX(), getY(), getZ());
    }

    @Override
    public void setPosition(Vector3f position) {
        if (
                this.x != position.x() ||
                this.y != position.y() ||
                this.z != position.z()
        ) {
            this.x = position.x();
            this.y = position.y();
            this.z = position.z();

            this.backend.ensureScene(this.scene);
            AL11.alListener3f(AL11.AL_POSITION, this.x, this.y, this.z);
        }
    }

    @Override
    public void setRotation(float yaw, float elevation) {
        setRotation(yaw, elevation, getRoll());
    }

    @Override
    public void setRotation(float yaw, float elevation, float roll) {
        if (
                this.yaw != yaw ||
                this.elevation != elevation ||
                this.roll != roll
        ) {
            this.yaw = yaw;
            this.elevation = elevation;
            this.roll = roll;

            recalculateRotation();
        }
    }

    public void close() {
        MemoryUtil.memFree(this.cacheBuffer);
    }

    private void recalculateRotation() {
        float cosPitch = (float) Math.cos(this.getElevation());
        float sinPitch = (float) Math.sin(this.getElevation());
        float cosYaw   = (float) Math.cos(getYaw());
        float sinYaw   = (float) Math.sin(getYaw());

        this.lookVector.set(
                -sinYaw * cosPitch,
                sinPitch,
                -cosYaw * cosPitch
        );
        this.upVector.set(
                sinYaw * sinPitch,
                cosPitch,
                cosYaw * sinPitch
        );

        if (getRoll() != 0f)
            this.upVector.rotateAxis(getRoll(), this.lookVector.x, this.lookVector.y, this.lookVector.z);

        this.cacheBuffer
                .put(0, this.lookVector.x).put(1, this.lookVector.y).put(2, this.lookVector.z)
                .put(3, this.upVector.x).put(4, this.upVector.y).put(5, this.upVector.z);

        this.backend.ensureScene(this.scene);
        AL11.alListenerfv(AL11.AL_ORIENTATION, this.cacheBuffer);
    }
}
