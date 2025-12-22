package com.github.fdh911.render

enum class Unicodes(val s: String) {
    REMOVE("\uf00d"),
    DUPLICATE("\uf24d"),
    ANGLE_UP("\uf106"),
    ANGLE_DOWN("\uf107"),
    CHECKBOX_0("\uf0c8"),
    CHECKBOX_1("\uf14a");

    companion object {
        override fun toString() = entries.joinToString { it.s }
    }

    override fun toString() = s
}