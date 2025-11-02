package com.github.fdh911.utils

fun String.noModifiers() = replace("\u00A7.".toRegex(), "")