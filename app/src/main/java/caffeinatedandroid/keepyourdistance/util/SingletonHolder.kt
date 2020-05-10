package caffeinatedandroid.keepyourdistance.util

/**
 * Provides ability to create a Singleton that receives an argument.
 *
 * This is typically used to create a Singleton object which (e.g.) requires access to
 * a Context parameter. Objects in kotlin are inherently Singleton, but you are unable
 * to send arguments to one. This `SingletonHolder` rectifies that.
 *
 * {@see https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e}
 *
 * To implement a Singleton that accepts an argument, do the following:
 * ```kotlin
 * class Manager private constructor(context: Context) {
 *   init {
 *     // Init using context argument
 *   }
 *
 *   companion object : SingletonHolder<Manager, Context>(::Manager)
 * }
 * ```
 *
 * Then access it through: `Manager.getInstance(context)`
 */
open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    /**
     * Returns an instance of the Singleton object, whilst also passing an argument to it.
     */
    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}