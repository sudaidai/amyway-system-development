package com.amway.calculator.domain;

import static org.assertj.core.api.Assertions.*;

import com.amway.calculator.exception.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class DefaultCalculatorTest {
  @Test
  void arithmeticAndClear() {
    DefaultCalculator c = new DefaultCalculator();
    assertThat(c.add(b("10"))).isEqualByComparingTo("10");
    assertThat(c.subtract(b("5"))).isEqualByComparingTo("5");
    assertThat(c.multiply(b("4"))).isEqualByComparingTo("20");
    assertThat(c.divide(b("8"))).isEqualByComparingTo("2.5");
    assertThat(c.clear()).isEqualByComparingTo("0");
  }

  @Test
  void negativeOperandsAndResults() {
    DefaultCalculator c = new DefaultCalculator();
    assertThat(c.add(b("-5"))).isEqualByComparingTo("-5");
    assertThat(c.add(b("3"))).isEqualByComparingTo("-2");
    assertThat(c.multiply(b("-3"))).isEqualByComparingTo("6");
    assertThat(c.subtract(b("8"))).isEqualByComparingTo("-2");
  }

  @Test
  void precisionExamples() {
    for (String divisor : new String[] {"3", "7", "6"}) {
      DefaultCalculator c = new DefaultCalculator();
      c.add(BigDecimal.TEN);
      c.divide(b(divisor));
      assertThat(c.display()).matches("-?\\d+\\.\\d{6}");
    }
  }

  @Test
  void multipleUndoRedoAndInvalidation() {
    DefaultCalculator c = new DefaultCalculator();
    c.add(b("10"));
    c.multiply(b("2"));
    c.subtract(b("5"));
    assertThat(c.undo()).isEqualByComparingTo("20");
    assertThat(c.undo()).isEqualByComparingTo("10");
    assertThat(c.undo()).isEqualByComparingTo("0");
    assertThat(c.redo()).isEqualByComparingTo("10");
    assertThat(c.redo()).isEqualByComparingTo("20");
    c.add(b("5"));
    assertThatThrownBy(c::redo).isInstanceOf(NoRedoHistoryException.class);
  }

  @Test
  void failuresNeverCorruptState() {
    DefaultCalculator c = new DefaultCalculator();
    c.add(BigDecimal.ONE);
    assertThatThrownBy(() -> c.divide(BigDecimal.ZERO)).isInstanceOf(DivisionByZeroException.class);
    assertThat(c.getResult()).isEqualByComparingTo("1");
    c.undo();
    assertThatThrownBy(c::undo).isInstanceOf(NoUndoHistoryException.class);
    assertThatCode(c::redo).doesNotThrowAnyException();
  }

  private static BigDecimal b(String value) {
    return new BigDecimal(value);
  }
}
