package com.sksamuel.tabby

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ZipTest : FunSpec() {
   init {
      test("zip should invoke combinator on two successful inputs") {
         IO.success("foo").zip(IO.success("bar")) { a, b -> a + b }.run() shouldBe "foobar".right()
      }
      test("zip should fail on left") {
         IO.success("foo").zip(IO.effect<String> { error("bar") }) { a, b -> a + b }.run().shouldBeInstanceOf<Either.Left<*>>()
         IO.effect<String> { error("bar") }.zip(IO.success("foo")) { a, b -> a + b }.run().shouldBeInstanceOf<Either.Left<*>>()
      }
   }
}
