package ch.jvi.budgetmanager.backend.command.domain.account.service

import ch.jvi.budgetmanager.backend.command.api.command.CreationCommand
import ch.jvi.budgetmanager.backend.command.api.command.store.CommandStore
import ch.jvi.budgetmanager.backend.command.api.event.EventBus
import ch.jvi.budgetmanager.backend.command.api.service.EntityService
import ch.jvi.budgetmanager.backend.command.domain.account.Account
import ch.jvi.budgetmanager.backend.command.domain.account.command.AccountCommand
import ch.jvi.budgetmanager.backend.command.domain.account.command.AccountCommand.CreateAccountCommand
import ch.jvi.budgetmanager.backend.command.domain.account.event.AccountEvent
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * This service is used to validate input ralted to accounts and to create and send the corresponding Events.
 *
 * @author J. Villing
 */
@Service
class AccountService(
    private val eventBus: EventBus,
    private val commandStore: CommandStore
) : EntityService<Account> {

    /**
     * @return The account with the requested ID
     * @throws IllegalArgumentException if no Entity with the given ID is found.
     */
    override fun find(entityId: String): Account {
        val creationCommand: CreateAccountCommand = commandStore.findCreationCommand(entityId) as CreateAccountCommand
        val account = Account(creationCommand)
        return applyCommands(account)
    }

    override fun findAll(): List<Account> {
        return commandStore.findCreationCommands(this::isAccountCreationCommand)
            .map { Account(it as CreateAccountCommand) }
            .map { applyCommands(it) }
    }

    private fun applyCommands(account: Account): Account {
        val commands: List<AccountCommand> = commandStore.findAccountCommands(account.id)
        account.applyAll(commands)
        return account
    }

    fun isAccountCreationCommand(command: CreationCommand): Boolean {
        return command is CreateAccountCommand
    }

    /**
     * Creates and sends a CreateAccountEvent with the given balance and name
     */
    fun createAccount(balance: BigDecimal, name: String) {
        val createAccountEvent = AccountEvent.CreateAccountEvent(balance, name)
        eventBus.send(createAccountEvent)
    }

    /**
     * Creates and sends an UpdateAccountEvent with the given id, balance and name.
     */
    fun updateAccount(id: String, balance: BigDecimal, name: String) {
        val updateAccountEvent = AccountEvent.UpdateAccountEvent(id, balance, name)
        eventBus.send(updateAccountEvent)
    }
}