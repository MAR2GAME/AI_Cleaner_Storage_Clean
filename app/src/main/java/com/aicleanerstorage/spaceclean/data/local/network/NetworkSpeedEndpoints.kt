package com.aicleanerstorage.spaceclean.data.local.network

import java.net.URL

internal data class NetworkSpeedEndpoints(
    val latency: URL = URL("https://www.google.com/generate_204"),
    val download: URL = URL("https://speed.cloudflare.com/__down?bytes=1048576"),
    val upload: URL = URL("https://speed.cloudflare.com/__up"),
)
