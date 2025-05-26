package link.apparition.android.sample

sealed interface AppEvent {

    data object InitSession : AppEvent
    data class ExpandUrl(val url: String) : AppEvent
    data object CreateUrl : AppEvent
    data object Open : AppEvent
}