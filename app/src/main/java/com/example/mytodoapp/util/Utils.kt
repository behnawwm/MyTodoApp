package com.example.mytodoapp.util

//type safety for sealed class events
val <T> T.exhaustive: T
    get() = this