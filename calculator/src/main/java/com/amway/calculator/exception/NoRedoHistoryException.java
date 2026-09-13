package com.amway.calculator.exception;

import java.util.NoSuchElementException;

public class NoRedoHistoryException extends NoSuchElementException {
  public NoRedoHistoryException() {
    super("Nothing to redo");
  }
}
