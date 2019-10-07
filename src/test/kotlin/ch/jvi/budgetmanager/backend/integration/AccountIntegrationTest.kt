package ch.jvi.budgetmanager.backend.integration

import ch.jvi.budgetmanager.backend.core.service.AccountService
import ch.jvi.budgetmanager.backend.core.service.TransferService
import ch.jvi.budgetmanager.backend.domain.IDProvider.idcounter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.math.BigDecimal.ONE

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("integrationTest")
internal class AccountIntegrationTest {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var transferService: TransferService

    @Test
    fun testAccountCreation() {
        // Given
        val id = idcounter.toString()
        val balance = BigDecimal.TEN
        val name = "Name"

        // When
        accountService.createAccount(balance, name)
        val account = accountService.find(id)

        // Then
        assertThat(account).satisfies {
            assertThat(it.name).isEqualTo(name)
            assertThat(it.balance).isEqualTo(balance)
        }
    }

    @Test
    fun testAccountUpdate() {
        // Given
        val id = idcounter.toString()
        val balance = BigDecimal.TEN
        val name = "Name"
        val newBalance = balance.add(ONE)
        val newName = "NewName"

        // When
        accountService.createAccount(balance, name)
        accountService.updateAccount(id, newBalance, newName)
        val account = accountService.find(id)

        // Then
        assertThat(account).satisfies {
            assertThat(it.name).isEqualTo(newName)
            assertThat(it.balance).isEqualTo(newBalance)
        }
    }

    @Test
    fun testAccountWithTransactions() {
        // Given
        val senderId = idcounter.toString()
        val initialSenderBalance = BigDecimal.TEN
        val senderName = "Name"
        val recipientId = (idcounter + 1).toString()
        val initialRecipientBalance = BigDecimal.TEN
        val recipientName = "NewName"
        val balanceChange = BigDecimal.valueOf(5)

        // When
        accountService.createAccount(initialSenderBalance, senderName)
        accountService.createAccount(initialRecipientBalance, recipientName)
        transferService.createTransfer(senderId, recipientId, balanceChange)

        val account = accountService.find(senderId)
        val otherAccount = accountService.find(recipientId)

        // Then
        assertThat(account).satisfies {
            assertThat(it.name).isEqualTo(senderName)
            assertThat(it.balance).isEqualTo(initialSenderBalance.subtract(balanceChange))
        }
        assertThat(otherAccount).satisfies {
            assertThat(it.name).isEqualTo(recipientName)
            assertThat(it.balance).isEqualTo(initialRecipientBalance.add(balanceChange))
        }
    }

    @Test
    fun testAccountWithUpdatedTransactions() {
        // Given
        val senderId = idcounter.toString()
        val initialSenderBalance = BigDecimal.TEN
        val senderName = "Name"
        val recipientId = (idcounter + 1).toString()
        val initialRecipientBalance = BigDecimal.TEN
        val recipientName = "NewName"
        val balanceChange = BigDecimal.valueOf(5)
        val transferId = (idcounter + 2).toString()
        val updatedBalanceChange = BigDecimal.valueOf(5)

        // When
        accountService.createAccount(initialSenderBalance, senderName)
        accountService.createAccount(initialRecipientBalance, recipientName)
        transferService.createTransfer(senderId, recipientId, balanceChange)
        transferService.updateTransfer(transferId, senderId, recipientId, updatedBalanceChange)

        val account = accountService.find(senderId)
        val otherAccount = accountService.find(recipientId)

        // Then
        assertThat(account).satisfies {
            assertThat(it.name).isEqualTo(senderName)
            assertThat(it.balance).isEqualTo(initialSenderBalance.subtract(updatedBalanceChange))
        }
        assertThat(otherAccount).satisfies {
            assertThat(it.name).isEqualTo(recipientName)
            assertThat(it.balance).isEqualTo(initialRecipientBalance.add(updatedBalanceChange))
        }
    }

}