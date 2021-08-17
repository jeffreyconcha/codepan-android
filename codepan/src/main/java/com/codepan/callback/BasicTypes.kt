package com.codepan.callback

typealias VoidCallback = () -> Unit

typealias ValueChanged<P, R> = (P) -> R

typealias ValueSetter<P> = (P) -> Unit

typealias ValueGetter<R> = () -> R