package ch.jvi.magpie.web.account

import ch.jvi.magpie.service.EventBus
import ch.jvi.magpie.core.domain.account.IAccountCommandStore
import ch.jvi.magpie.service.account.AccountService
import ch.jvi.magpie.core.domain.account.AccountCommand
import ch.jvi.magpie.infra.rest.account.AccountController
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.math.BigDecimal.TEN

internal class AccountControllerTest {

    val eventBus = mock(EventBus::class.java)

    val commandStore = mock(IAccountCommandStore::class.java)

    val accountService = spy(
        AccountService(
            commandStore,
            eventBus
        )
    )

    val accountController = AccountController(accountService)

    @Test
    fun testGetAccount() {
        // Given
        val id = "someId"
        val creationCommand = AccountCommand.CreateAccountCommand(TEN, "name", id)
        `when`(commandStore.findCreationCommand(id)).thenReturn(creationCommand)

        // When
        accountController.get(id)

        // Then
        verify(accountService, times(1)).find(id)
    }

    @Test
    fun testCreateAccount() {
        // Given
        val balance = BigDecimal.ONE
        val name = "Name"

        // When
        accountController.create(balance, name)

        // Then
        verify(accountService, times(1)).create(balance, name)
    }

    @Test
    fun testUpdateAccount() {
        // Given
        val id = "id"
        val balance = TEN
        val name = "Name"

        // When
        accountController.update(id, balance, name)

        // Then
        verify(accountService, times(1)).update(id, balance, name)
    }
}
