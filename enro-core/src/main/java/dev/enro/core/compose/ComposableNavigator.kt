package dev.enro.core.compose

import dev.enro.core.NavigationKey
import dev.enro.core.Navigator
import kotlin.reflect.KClass

class ComposableNavigator<KeyType : NavigationKey, ComposableType : ComposableDestination> @PublishedApi internal constructor(
    override val keyType: KClass<KeyType>,
    override val contextType: KClass<ComposableType>
) : Navigator<KeyType, ComposableType>

fun <KeyType : NavigationKey, ComposableType : ComposableDestination> createComposableNavigator(
    keyType: Class<KeyType>,
    composableType: Class<ComposableType>
): Navigator<KeyType, ComposableType> = ComposableNavigator(
    keyType = keyType.kotlin,
    contextType = composableType.kotlin
)

inline fun <reified KeyType : NavigationKey, reified ComposableType : ComposableDestination> createComposableNavigator() =
    createComposableNavigator(
        KeyType::class.java,
        ComposableType::class.java
    )