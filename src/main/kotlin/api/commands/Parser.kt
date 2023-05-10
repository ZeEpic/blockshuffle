package api.commands

import kotlin.reflect.KClass

annotation class Parser(val type: KClass<*>)
