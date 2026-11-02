package com.aether.application.core.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

/**
 * Pops the back stack only while the current screen is still active.
 *
 * Tapping back several times quickly queues one pop per tap; each would remove another screen
 * and leave the user on an empty destination. Once the first pop starts, the leaving entry is no
 * longer RESUMED, so the extra taps are ignored.
 */
fun NavController.safePopBackStack(): Boolean {
    if (currentBackStackEntry?.lifecycle?.currentState != Lifecycle.State.RESUMED) return false
    return popBackStack()
}
