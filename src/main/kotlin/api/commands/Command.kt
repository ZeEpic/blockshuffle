package api.commands

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(val name: String = "", val description: String = "", val aliases: Array<String> = [])
