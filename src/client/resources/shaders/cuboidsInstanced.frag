#version 450 core

in vec4 fragColor;
in vec3 fragPos;

out vec4 FragColor;

uniform vec4 uHighlightCol;
uniform vec3 uPlayerPos;
uniform float uRadius = 2.0f;

void main() {
    float distanceFromPlayer = distance(uPlayerPos, fragPos);
    float coefficient = max(0.0f, uRadius - distanceFromPlayer) / uRadius;

    FragColor = mix(fragColor, uHighlightCol, coefficient);
}