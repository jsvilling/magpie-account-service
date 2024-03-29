package ch.jvi.magpie.infra.rest.transfer

import ch.jvi.magpie.command.domain.transfer.Transfer
import ch.jvi.magpie.core.domain.transfer.ITransferService
import ch.jvi.magpie.core.domain.transfer.TransferCommand
import ch.jvi.magpie.core.domain.transfer.TransferEvent
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

/**
 * Rest Controller for all Transfer related operations.
 *
 * @author J. Villing
 */
@RestController
@RequestMapping("/api/transfers")
class TransferController(
    private val transferService: ITransferService
) {

    @GetMapping
    fun get(): List<Transfer> {
        return transferService.findAll()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Transfer {
        return transferService.find(id)
    }

    @GetMapping
    fun getForAccount(@RequestParam(required = true) accountId: String): List<Transfer> {
        return transferService.findAllForAccount(accountId)
    }

    @PostMapping
    fun create(
        @RequestParam senderId: String,
        @RequestParam name: String,
        @RequestParam recipientId: String,
        @RequestParam amount: BigDecimal
    ) {
        // TODO: Receive parms in body -> Could directly us transfer command
        transferService.create(senderId, name, recipientId, amount)
    }

    @PutMapping("/{id}")
    fun update(@RequestBody updateTransferCommand: TransferCommand.UpdateTransferCommand) {
        transferService.update(updateTransferCommand)
    }
}
