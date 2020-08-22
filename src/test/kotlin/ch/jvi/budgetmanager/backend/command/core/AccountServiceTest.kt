package ch.jvi.budgetmanager.backend.command.core

import ch.jvi.budgetmanager.backend.command.api.event.EventBus
import ch.jvi.budgetmanager.backend.command.domain.account.Account
import ch.jvi.budgetmanager.backend.command.domain.account.command.AccountCommand.CreateAccountCommand
import ch.jvi.budgetmanager.backend.command.domain.account.command.AccountCommand.UpdateAccountCommand
import ch.jvi.budgetmanager.backend.command.domain.account.event.AccountEvent.CreateAccountEvent
import ch.jvi.budgetmanager.backend.command.domain.account.persistance.store.AccountCommandStore
import ch.jvi.budgetmanager.backend.command.domain.account.service.AccountService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
import java.math.BigDecimal

internal class AccountServiceTest {

    private val eventBus = mock(EventBus::class.java)
    private val commandStore = mock(AccountCommandStore::class.java)
    private val accountService =
        AccountService(commandStore, eventBus)

    @Test
    fun testGetAccount_OnlyCreationCommand() {
        // Given
        val balance = BigDecimal.TEN
        val name = "name"
        val id = "id"
        val creationCommand = CreateAccountCommand(balance, name, id)
        doReturn(creationCommand).`when`(commandStore).findCreationCommand(id)

        // When
        val account = accountService.find(id)

        // Then
        verify(commandStore, times(1)).findCreationCommand(id)
        assertThat(account).isEqualToComparingFieldByField(Account(creationCommand))
    }

    @Test
    fun testGetAccount_UpdateAccountApplied() {
        // Given
        val balance = BigDecimal.TEN
        val name = "name"
        val id = "id"
        val newBalace = balance.add(BigDecimal.ONE)
        val newName = "newnewnewname"
        val creationCommand = CreateAccountCommand(balance, name, id)
        val updateCommand = UpdateAccountCommand(newBalace, newName, id)
        doReturn(creationCommand).`when`(commandStore).findCreationCommand(id)
        doReturn(listOf(updateCommand)).`when`(commandStore).findUpdateCommands(id)

        // When
        val account = accountService.find(id)

        // Then
        assertThat(account).isEqualToIgnoringGivenFields(updateCommand, "id", "creationCommand")
    }

    @Test
    fun testCreateAccount() {
        // Given
        val balance = BigDecimal.TEN
        val name = "name"
        val creationEvent = CreateAccountEvent(balance, name)

        // When
        accountService.createAccount(balance, name)

        // Then
        verify(eventBus, times(1)).send(creationEvent)
    }
}