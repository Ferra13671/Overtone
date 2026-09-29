package com.ferra13671.overtone.api;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Spatial {

    float getX();

    float getY();

    float getZ();

    Vector3f getPosition();

    float getYaw();

    float getElevation();

    float getRoll();

    void setPosition(Vector3f position);

    void setRotation(float yaw, float elevation);

    void setRotation(float yaw, float elevation, float roll);

    Vector3fc getLookVector();

    Vector3fc getUpVector();
}
