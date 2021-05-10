package com.example.mytodoapp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun convertLongDateToDate(long: Long): String = SimpleDateFormat("dd MMM").format(Date(long))

