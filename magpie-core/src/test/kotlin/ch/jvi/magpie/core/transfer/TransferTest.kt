package ch.jvi.magpie.core.transfer

import ch.jvi.magpie.command.domain.transfer.Transfer
import ch.jvi.magpie.core.domain.transfer.TransferCommand
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal.TEN

internal class TransferTest {
    @Test
    fun testTransferCreation() {
        // Given
        val id = "123"
        val recipientId = "321"
        val senderId = "231"
        val amount = TEN
        val createTransferCommand = TransferCommand.CreateTransferCommand(recipientId, senderId, "name", amount, id)

        // When
        val transfer = Transfer(createTransferCommand)

        // Then
        assertThat(transfer).isEqualToIgnoringGivenFields(createTransferCommand, "id", "creationCommand")
    }
}
