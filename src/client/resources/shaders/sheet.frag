#version 450 core

uniform int spacing = 25;

in vec2 fragTexCoord;

out vec4 FragColor;

void main() {
    if(int(gl_FragCoord.x) % spacing == 0 || int(gl_FragCoord.y) % spacing == 0)
        FragColor = vec4(0.2f, 0.2f, 0.2f, 0.7f);
    else
        FragColor = vec4(0.1f, 0.1f, 0.1f, 0.7f);
}