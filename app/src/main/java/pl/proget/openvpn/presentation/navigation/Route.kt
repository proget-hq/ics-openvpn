package pl.proget.openvpn.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object MainRoute : Route()
    @Serializable
    data object AboutRoute : Route()
    @Serializable
    data object LogsRoute : Route()
}
