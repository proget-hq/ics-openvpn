package pl.proget.openvpn.tools

import android.os.Build

fun isAndroidLmr1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
fun isAndroidM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isAndroidN(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isAndroidO(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isAndroidOmr1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isAndroidP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun isAndroidQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isAndroidR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
