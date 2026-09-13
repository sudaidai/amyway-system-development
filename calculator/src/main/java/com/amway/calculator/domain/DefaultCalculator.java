package com.amway.calculator.domain;

import com.amway.calculator.command.*;
import com.amway.calculator.exception.*;
import java.math.*;
import java.util.*;

public final class DefaultCalculator implements Calculator {
  private final Deque<BigDecimal> undoHistory = new ArrayDeque<>(),
      redoHistory = new ArrayDeque<>();
  private BigDecimal result = BigDecimal.ZERO;

  public synchronized BigDecimal add(BigDecimal n) {
    return execute(new AddCommand(n));
  }

  public synchronized BigDecimal subtract(BigDecimal n) {
    return execute(new SubtractCommand(n));
  }

  public synchronized BigDecimal multiply(BigDecimal n) {
    return execute(new MultiplyCommand(n));
  }

  public synchronized BigDecimal divide(BigDecimal n) {
    return execute(new DivideCommand(n));
  }

  public synchronized BigDecimal clear() {
    return execute(new ClearCommand());
  }

  public synchronized BigDecimal undo() {
    if (undoHistory.isEmpty()) throw new NoUndoHistoryException();
    redoHistory.push(result);
    result = undoHistory.pop();
    return result;
  }

  public synchronized BigDecimal redo() {
    if (redoHistory.isEmpty()) throw new NoRedoHistoryException();
    undoHistory.push(result);
    result = redoHistory.pop();
    return result;
  }

  public synchronized BigDecimal getResult() {
    return result;
  }

  public synchronized String display() {
    return result
        .setScale(CalculatorPrecision.SCALE, CalculatorPrecision.ROUNDING_MODE)
        .toPlainString();
  }

  public synchronized BigDecimal execute(CalculatorCommand command) {
    if (command == null) throw new IllegalArgumentException("Command is required");
    BigDecimal next = command.execute(result);
    undoHistory.push(result);
    result = next;
    redoHistory.clear();
    return result;
  }
}
