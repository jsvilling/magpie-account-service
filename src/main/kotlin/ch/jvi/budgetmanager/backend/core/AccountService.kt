package ch.jvi.budgetmanager.backend.core

import ch.jvi.budgetmanager.backend.api.command.CommandStore
import ch.jvi.budgetmanager.backend.domain.account.Account
import ch.jvi.budgetmanager.backend.api.message.MessageBus
import ch.jvi.budgetmanager.backend.core.message.AccountMessage
import ch.jvi.budgetmanager.backend.domain.account.AccountCommand
import ch.jvi.budgetmanager.backend.domain.account.AccountCommand.CreateAccountCommand
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AccountService(private val messageBus: MessageBus, private val commandStore: CommandStore) {

    fun getAccount(id: String): Account {
        val creationCommand: CreateAccountCommand = commandStore.findCreationCommand(id) as CreateAccountCommand
        val commands: List<AccountCommand> = commandStore.find(id) as List<AccountCommand>
        val account = Account(creationCommand)
        account.applyAll(commands)
        return account
    }

    fun createAccount(balance: BigDecimal, name: String) {
        val createAccountMessage = AccountMessage.CreateAccountMessage(balance, name)
        messageBus.send(createAccountMessage)
    }

    fun updateAccount(id: String, balance: BigDecimal, name: String) {
        val updateAccountMessage = AccountMessage.UpdateAccountMessage(id, balance, name)
        messageBus.send(updateAccountMessage)

    }

}