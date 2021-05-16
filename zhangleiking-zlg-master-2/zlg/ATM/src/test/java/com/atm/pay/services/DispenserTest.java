package com.atm.pay.services;

import com.atm.pay.model.Cash;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DispenserTest {

    @Test
    public void calculate_shouldReturnFullBalance_whenItCanNotFulfullTheRequest() throws AtmException {
        Dispenser dispenser = new Dispenser(Cash.Note.FIFTY);
        dispenser.addNotes(4);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(4);

        long balance = dispenser.calculate(120);

        assertThat(balance).isEqualTo(120);
    }

    @Test
    public void calculate_willNotUseNotes_whenValueIsSmallerThanNote() throws AtmException {
        Dispenser dispenser = new Dispenser(Cash.Note.FIFTY);
        dispenser.addNotes(2);

        long balance = dispenser.calculate(40);

        assertThat(balance).isEqualTo(40);
    }

    @Test
    public void calculate_shouldCallTheNextHandleInChain() throws AtmException {
        Dispenser next = mock(Dispenser.class);
        Dispenser dispenser = new Dispenser(Cash.Note.TWENTY);

        dispenser.setNextHandle(next);
        dispenser.addNotes(2);
        dispenser.calculate(30);

        verify(next).calculate(10);
    }

    @Test
    public void dispense_shouldUpdateTheNumberOfNotes() throws AtmException {
        Dispenser dispenser = new Dispenser(Cash.Note.FIFTY);
        dispenser.addNotes(3);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);

        dispenser.calculate(100);
        List<Cash> moneyDispensed = dispenser.dispense();

        assertThat(dispenser.getNumberOfNotes()).isEqualTo(1);
        assertThat(moneyDispensed).contains(new Cash(Cash.Note.FIFTY, 2));
    }

    @Test
    public void dispense_shouldNotChangeTheNumberOfNotes_whenItCanNotFullfilTheRequest() throws AtmException {
        Dispenser dispenser = new Dispenser(Cash.Note.FIFTY);
        dispenser.addNotes(3);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);

        dispenser.calculate(120);
        List<Cash> moneyDispensed = dispenser.dispense();

        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);
        assertThat(moneyDispensed).isEmpty();
    }

    @Test
    public void dispense_shouldCallTheNextInChain() throws AtmException {
        Dispenser next = mock(Dispenser.class);
        Dispenser dispenser = new Dispenser(Cash.Note.FIFTY);

        dispenser.setNextHandle(next);
        dispenser.addNotes(3);
        assertThat(dispenser.getNumberOfNotes()).isEqualTo(3);

        dispenser.calculate(120);
        dispenser.dispense();

        verify(next).dispense();
    }
}
