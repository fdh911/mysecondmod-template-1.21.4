#version 450 core

uniform sampler2D uFbColorAttachment;
uniform vec4 uColor;

in vec2 fragTexCoord;

out vec4 FragColor;

void main() {
    FragColor = int(texture(uFbColorAttachment, fragTexCoord).a > 0) * uColor;
}
