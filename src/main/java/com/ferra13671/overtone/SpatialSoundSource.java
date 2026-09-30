package com.ferra13671.overtone;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class SpatialSoundSource extends SoundSource {
    private final Vector3f position = new Vector3f();
    //x — yaw, y — elevation, z — roll
    private final Vector3f rotation = new Vector3f();

    private final Vector3f lookVector = new Vector3f(0f, 0f, -1f);
    private final Vector3f upVector = new Vector3f(0f, 1f, 0f);

    SpatialSoundSource() {}

    public Vector3fc getPosition() {
        return this.position;
    }

    public Vector3fc getRotation() {
        return this.rotation;
    }

    public Vector3fc getLookVector() {
        return this.lookVector;
    }

    public Vector3fc getUpVector() {
        return this.upVector;
    }

    public void setPosition(float x, float y, float z) {
        if (
                x != this.position.x ||
                y != this.position.y ||
                z != this.position.z
        ) {
            this.position.set(x, y, z);
            setDirty(true);
        }
    }

    public void setRotation(float yaw, float elevation, float roll) {
        if (
                yaw != this.rotation.x ||
                elevation != this.rotation.y ||
                roll != this.rotation.z
        ) {
            this.rotation.set(yaw, elevation, roll);
            recalculateRotation();
            setDirty(true);
        }
    }

    private void recalculateRotation() {
        float cosPitch = (float) Math.cos(this.rotation.y);
        float sinPitch = (float) Math.sin(this.rotation.y);
        float cosYaw   = (float) Math.cos(this.rotation.x);
        float sinYaw   = (float) Math.sin(this.rotation.x);

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

        if (this.rotation.z != 0f)
            this.upVector.rotateAxis(this.rotation.z, this.lookVector.x, this.lookVector.y, this.lookVector.z);
    }
}
