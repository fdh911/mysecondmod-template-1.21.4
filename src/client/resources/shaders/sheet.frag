#version 450 core

uniform int spacing = 50;

in vec2 fragTexCoord;

out vec4 FragColor;

vec2 corners[4] = vec2[](
    vec2(0, 0),
    vec2(0, spacing),
    vec2(spacing, spacing),
    vec2(spacing, 0)
);

void main() {
    vec2 coords = vec2(gl_FragCoord);
    coords.x += 0.5f * spacing;
    while(coords.x > spacing) coords.x -= spacing;
    while(coords.y > spacing) coords.y -= spacing;

    vec4 finalColor = vec4(0.05f, 0.05f, 0.05f, 0.8f);
    for(int i = 0; i < 4; i++) {
        if(distance(corners[i], coords) <= 2.0f)
            finalColor = vec4(0.15f, 0.15f, 0.15f, 0.8f);
    }

    FragColor = finalColor;
}