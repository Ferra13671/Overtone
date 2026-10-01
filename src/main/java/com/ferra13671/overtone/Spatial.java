package com.ferra13671.overtone;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Spatial {

    Vector3fc getPosition();

    Vector3fc getRotation();

    Vector3fc getLookVector();

    Vector3fc getUpVector();

    void setPosition(float x, float y, float z);

    void setRotation(float yaw, float elevation, float roll);

    default void recalculateRotation(Vector3fc rotation, Vector3f lookVector, Vector3f upVector) {
        float cosPitch = (float) Math.cos(rotation.y());
        float sinPitch = (float) Math.sin(rotation.y());
        float cosYaw   = (float) Math.cos(rotation.x());
        float sinYaw   = (float) Math.sin(rotation.x());

        lookVector.set(
                -sinYaw * cosPitch,
                sinPitch,
                -cosYaw * cosPitch
        );
        upVector.set(
                sinYaw * sinPitch,
                cosPitch,
                cosYaw * sinPitch
        );

        if (rotation.z() != 0f)
            upVector.rotateAxis(
                    rotation.z(),
                    lookVector.x(),
                    lookVector.y(),
                    lookVector.z()
            );
    }
}
