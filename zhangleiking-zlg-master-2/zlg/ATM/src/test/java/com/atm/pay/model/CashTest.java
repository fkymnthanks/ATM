package com.atm.pay.model;

import org.testng.annotations.Test;

import static com.atm.pay.model.Cash.Note;
import static org.assertj.core.api.Assertions.assertThat;

public class CashTest {
    @Test
    public void notes_shouldBeCreatedInCrescentOrder() {
        int previousValue = 0;
        for (Note note: Note.values()) {
            assertThat(note.getValue()).isGreaterThan(previousValue);
            previousValue = note.getValue();
        }
    }

    @Test
    public void constructor() {
        Cash cash = new Cash(Note.FIFTY, 20);
        assertThat(cash.getNote()).isEqualTo(Note.FIFTY);
        assertThat(cash.getNumberOfNotes()).isEqualTo(20);
    }
}
