package com.bewize.monitorbackend.enums;

public enum QuestionType {
    ONE_CHOICE,
    MULTI_CHOICE,
    MULTIPLE_QCU, // text with blanks, each blank has multiple choices
    DRAG_DROP_PUZZLE, // text with blanks, and one set of choices
    MATCH,
    SORT,
    TEXT, // Write the answer in a text field
    FILL_BLANK_TEXT, // Write answer in multiple text fields
}
