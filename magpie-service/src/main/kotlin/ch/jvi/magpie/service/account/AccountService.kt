package ch.jvi.magpie.service.account

import ch.jvi.magpie.service.EventBus
import ch.jvi.magpie.core.domain.account.IAccountCommandStore
import ch.jvi.magpie.core.domain.account.Account
import ch.jvi.magpie.core.domain.account.AccountCommand
import ch.jvi.magpie.core.domain.account.AccountCommand.*
import ch.jvi.magpie.core.domain.account.AccountEvent.CreateAccountEvent
import ch.jvi.magpie.core.domain.account.AccountEvent.UpdateAccountEvent
import ch.jvi.magpie.core.domain.account.IAccountService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * This service is used to validate input ralted to accounts and to create and send the corresponding Events.
 *
 * @author J. Villing
 */
@Service
@Transactional(readOnly = true)
class AccountService(
    private val accountCommandStore: IAccountCommandStore,
    private val eventBus: EventBus
) : IAccountService {

    /**
     * @return The account with the requested ID
     * @throws IllegalArgumentException if no Entity with the given ID is found.
     */
    override fun find(entityId: String): Account {
        val creationCommand: CreateAccountCommand = accountCommandStore.findCreationCommand(entityId)
        val account = Account(creationCommand)
        return findAndApplyAllCommands(account)
    }

    override fun findAll(): List<Account> {
        return accountCommandStore.findAllCreationCommands()
            .map { Account(it) }
            .map { findAndApplyAllCommands(it) }
    }

    private fun findAndApplyAllCommands(account: Account): Account {
        try {
            val commands: List<AccountCommand> = accountCommandStore.findUpdateCommands(account.id)
            account.applyAll(commands)
            return account
        } catch (e: Exception) {
            throw IllegalStateException("Failed to apply commands for account $account.id")
        }
    }

    @Transactional
    override fun create(createAccountCommand: CreateAccountCommand) {
        val account = Account(createAccountCommand)
        val createAccountEvent = CreateAccountEvent(account.balance, account.name)
        accountCommandStore.save(createAccountCommand)
        eventBus.send(createAccountEvent)
    }

    @Transactional
    override fun update(updateAccountCommand: UpdateAccountCommand) {
        val updateAccountEvent = find(updateAccountCommand.entityId).apply(updateAccountCommand)
        accountCommandStore.save(updateAccountCommand)
        eventBus.send(updateAccountEvent)
    }

    @Transactional
    override fun updateAccountBalance(id: String, balanceChange: BigDecimal) {
        // TODO: Receive Command instead of params
        val adjustAccountBalanceCommand = AdjustAccountBalanceCommand(balanceChange, id)
        val updateAccountEvent = find(id).apply(adjustAccountBalanceCommand)
        accountCommandStore.save(adjustAccountBalanceCommand)
        eventBus.send(updateAccountEvent)
    }

}
