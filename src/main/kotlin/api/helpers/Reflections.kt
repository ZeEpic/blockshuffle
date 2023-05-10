package api.helpers

import org.reflections.Reflections
import kotlin.reflect.KClass

inline fun <reified T : Any> Any.typesAnnotatedWith(annotation: KClass<out Annotation>): Set<Class<T>> {
    return Reflections(this::class.java.packageName)
        .getTypesAnnotatedWith(annotation.java)
        .filterIsInstance<Class<T>>()
        .toSet()
}