package com.ferra13671.overtone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Listener implements Spatial {
    private final Vector3f position = new Vector3f();
    //x — yaw, y — elevation, z — roll
    private final Vector3f rotation = new Vector3f();

    private final Vector3f lookVector = new Vector3f(0f, 0f, -1f);
    private final Vector3f upVector = new Vector3f(0f, 1f, 0f);

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean dirty = true;

    Listener() {}

    @Override
    public Vector3fc getPosition() {
        return this.position;
    }

    @Override
    public Vector3fc getRotation() {
        return this.rotation;
    }

    @Override
    public Vector3fc getLookVector() {
        return this.lookVector;
    }

    @Override
    public Vector3fc getUpVector() {
        return this.upVector;
    }

    @Override
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

    @Override
    public void setRotation(float yaw, float elevation, float roll) {
        if (
                yaw != this.rotation.x ||
                elevation != this.rotation.y ||
                roll != this.rotation.z
        ) {
            this.rotation.set(yaw, elevation, roll);
            recalculateRotation(this.rotation, this.lookVector, this.upVector);
            setDirty(true);
        }
    }
}
