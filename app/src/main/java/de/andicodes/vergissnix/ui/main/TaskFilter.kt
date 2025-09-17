package de.andicodes.vergissnix.ui.main

enum class TaskFilter(val position: Int) {
    DONE(0), COMING_WEEK(1), COMING_MONTH(2), COMING_ALL(3);

    companion object {
        @JvmStatic
        fun of(position: Int): TaskFilter {
            for (filter in entries) {
                if (filter.position == position) {
                    return filter
                }
            }
            return COMING_WEEK
        }
    }
}