package example

class HelloSuite extends munit.FunSuite {
  test("numbers") {
    val obtained = 43
    val expected = 43
    assertEquals(obtained, expected)
  }

  test("hello") {
    assertEquals("hello", "hello")
  }
}