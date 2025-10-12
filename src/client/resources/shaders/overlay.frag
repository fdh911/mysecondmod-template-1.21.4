#version 450 core

uniform sampler2D uTex;

in vec2 fragTexColor;

out vec4 FragColor;

void main() {
    FragColor = texture(uTex, fragTexColor);
}