#version 450 core

layout(location = 0) in vec3 aLocalPos;
layout(location = 1) in vec3 aWorldPos;
layout(location = 2) in vec3 aScale;
layout(location = 3) in vec4 aColor;

uniform mat4 uProjView;

out vec4 fragColor;

void main() {
    vec3 scaledLocalPos = vec3(
        aLocalPos.x * aScale.x,
        aLocalPos.y * aScale.y,
        aLocalPos.z * aScale.z
    );

    vec3 translatedLocalPos = vec3(
        aLocalPos.x + aWorldPos.x,
        aLocalPos.y + aWorldPos.y,
        aLocalPos.z + aWorldPos.z
    );

    gl_Position = uProjView * vec4(translatedLocalPos, 1.0f);
    fragColor = aColor;
}
