package com.amway.calculator.exception;

import java.util.NoSuchElementException;

public class NoUndoHistoryException extends NoSuchElementException {
  public NoUndoHistoryException() {
    super("Nothing to undo");
  }
}
