package com.ferra13671.overtone.api;

import org.joml.Vector3f;

public interface Listener {

    float getX();

    float getY();

    float getZ();

    Vector3f getPosition();

    float getYaw();

    float getPitch();

    float getRoll();

    void setPosition(Vector3f position);

    void setRotation(float yaw, float pitch);

    void setRotation(float yaw, float pitch, float roll);
}
