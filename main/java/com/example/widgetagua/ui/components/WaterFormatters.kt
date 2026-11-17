package com.example.widgetagua.ui.components

import java.util.Locale

/** Formata um volume em litros com uma casa decimal, respeitando o locale (ex.: 1,6 L). */
fun formatLiters(value: Double): String = String.format(Locale.getDefault(), "%.1f", value)
