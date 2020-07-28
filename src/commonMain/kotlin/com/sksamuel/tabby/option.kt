package com.sksamuel.tabby

sealed class Option<out A> {

   data class Some<A>(val value: A) : Option<A>()
   object None : Option<Nothing>()

   companion object {
      /**
       * Wraps a nullable value in an Option. If the value is null, then a [None] is returned,
       * otherwise a [Some] is returned that contains the value.
       */
      operator fun <T> invoke(t: T?): Option<T> = t?.some() ?: None
   }

   inline fun forEach(f: (A) -> Unit): Option<A> {
      when (this) {
         is Some -> f(value)
         is None -> {
         }
      }
      return this
   }

   inline fun <B> map(f: (A) -> B): Option<B> = flatMap { f(it).toOption() }

   inline fun <B> flatMap(f: (A) -> Option<B>): Option<B> = when (this) {
      is Some -> f(this.value)
      else -> None
   }

   inline fun <B> fold(ifEmpty: () -> B, ifDefined: (A) -> B): B = when (this) {
      is Some -> ifDefined(this.value)
      else -> ifEmpty()
   }

   inline fun <B> fold(ifEmpty: B, ifDefined: (A) -> B): B = when (this) {
      is Some -> ifDefined(this.value)
      else -> ifEmpty
   }

   fun orNull(): A? = when (this) {
      is Some -> this.value
      else -> null
   }

   fun filter(p: (A) -> Boolean): Option<A> = flatMap { if (p(it)) this else None }

   fun isDefined(): Boolean = !isEmpty()
   fun isEmpty(): Boolean = this is None

   fun exists(p: (A) -> Boolean) = fold(false, p)

   fun toList(): List<A> = fold(emptyList(), { listOf(it) })

   fun <B> toEither(ifEmpty: () -> B): Either<B, A> = fold({ ifEmpty().left() }, { it.right() })

   fun <B, C> combine(other: Option<B>, f: (A, B) -> C): Option<C> = when (this) {
      is Some -> when (other) {
         is Some -> f(this.value, other.value).some()
         else -> None
      }
      else -> None
   }

   fun getUnsafe(): A = fold({ throw IllegalStateException() }, { it })

   fun <E> toValidated(isEmpty: () -> E): Validated<E, A> = fold({ isEmpty().invalid() }, { it.valid() })
}

inline fun <A> Option<A>.getOrElse(a: A): A = when (this) {
   is Option.None -> a
   is Option.Some -> this.value
}

inline fun <A> Option<A>.getOrElse(f: () -> A): A = when (this) {
   is Option.None -> f()
   is Option.Some -> this.value
}

fun <A, B, R> applicative(a: Option<A>, b: Option<B>, f: (A, B) -> R): Option<R> {
   return when (a) {
      is Option.Some -> when (b) {
         is Option.Some -> f(a.value, b.value).some()
         Option.None -> Option.None
      }
      Option.None -> Option.None
   }
}

fun <A, B, C, D, R> applicative(a: Option<A>,
                                b: Option<B>,
                                c: Option<C>,
                                d: Option<D>,
                                f: (A, B, C, D) -> R): Option<R> {
   if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) return Option.None
   return f(a.getUnsafe(), b.getUnsafe(), c.getUnsafe(), d.getUnsafe()).some()
}

fun none() = Option.None
fun <T> T.some(): Option<T> = Option.Some(this)

fun <T> T?.toOption(): Option<T> = this?.some() ?: Option.None

fun <A> List<A>.headOption(): Option<A> = this.firstOrNone()
fun <A> List<A>.firstOrNone(): Option<A> = this.firstOrNull().toOption()

inline fun <T, U : Any> List<T>.flatMapOption(f: (T) -> Option<U>): List<U> = mapNotNull { f(it).orNull() }

/**
 * For an Option of an Option, removees the inner option. If the receiver is a Some(Some(a)), returns Some(a),
 * otherwise returns None.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Option<Option<T>>.flatten(): Option<T> = when (this) {
   is Option.Some<*> -> when (this.value) {
      is Option.Some<*> -> this.value as Option.Some<T>
      else -> Option.None
   }
   else -> Option.None
}