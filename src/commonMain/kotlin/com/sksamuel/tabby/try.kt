package com.sksamuel.tabby

sealed class Try<out A> {

   data class Success<T>(val value: T) : Try<T>()
   data class Failure(val throwable: Throwable) : Try<Nothing>()

   companion object {
      inline operator fun <T> invoke(f: () -> T): Try<T> = try {
         Success(f())
      } catch (t: Throwable) {
         Failure(t)
      }
   }

   inline fun <E> toEither(f: (Throwable) -> E): Either<E, A> = fold({ f(it).left() }, { it.right() })

   inline fun <B> map(f: (A) -> B): Try<B> = fold({ Failure(it) }, { f(it).success() })

   inline fun <B> flatMap(f: (A) -> Try<B>): Try<B> = fold({ Failure(it) }, { f(it) })

   inline fun <U> fold(ifFailure: (Throwable) -> U, ifSuccess: (A) -> U): U = when (this) {
      is Success -> ifSuccess(this.value)
      is Failure -> ifFailure(throwable)
   }

   inline fun onFailure(f: (Throwable) -> Unit): Try<A> {
      fold({ f(it) }, {})
      return this
   }

   fun orNull(): A? = when (this) {
      is Success -> this.value
      is Failure -> null
   }

   fun toOption(): Option<A> = fold({ none() }, { it.some() })

   fun <E> toValidated(error: E): Validated<E, A> = fold({ error.invalid() }, { it.valid() })
}

fun <A> A.success(): Try<A> = Try.Success(this)
fun <A> Try<A>.getOrElse(value: A): A = fold({ value }, { it })
fun <A> Try<A>.getOrElse(ifFailure: (Throwable) -> A): A = fold(ifFailure, { it })