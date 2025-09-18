#version 450 core

uniform sampler2D uFbColorAttachment;
uniform vec4 uOverlayColor;
uniform vec2 uScreenSize;

in vec2 fragTexCoord;

out vec4 FragColor;

float kernel[9] = float[](
    1,  1, 1,
    1, -8, 1,
    1,  1, 1
);

vec2 offsets[9] = vec2[](
    vec2(-1,  1), vec2(0,  1), vec2(1,  1),
    vec2(-1,  0), vec2(0,  0), vec2(1,  0),
    vec2(-1, -1), vec2(0, -1), vec2(1, -1)
);

void main() {
    vec4 finalColor = vec4(0);

    for(int i = 0; i < 9; i++) {
        vec2 o = vec2(
            offsets[i].x / uScreenSize.x,
            offsets[i].y / uScreenSize.y
        );
        finalColor += texture(uFbColorAttachment, fragTexCoord + o) * kernel[i];
    }

    if(finalColor.a > 0.01)
        FragColor = uOverlayColor;
    else
        FragColor = texture(uFbColorAttachment, fragTexCoord);
}