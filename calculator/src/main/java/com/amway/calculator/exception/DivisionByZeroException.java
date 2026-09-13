package com.amway.calculator.exception;

public class DivisionByZeroException extends ArithmeticException {
  public DivisionByZeroException() {
    super("Cannot divide by zero");
  }
}
