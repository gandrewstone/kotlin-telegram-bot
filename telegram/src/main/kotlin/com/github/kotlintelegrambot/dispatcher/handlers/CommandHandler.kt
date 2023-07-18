package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update

data class CommandHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val message: Message,
    val args: List<String>,
    val isEdited: Boolean
)

class CommandHandler(
    private val command: Array<out String>,
    private val handleCommand: suspend CommandHandlerEnvironment.() -> Unit,
    private val tolower:Boolean = false
) : Handler {

    override fun checkUpdate(update: Update): Boolean
    {
        update.message?.text?.let {
            if (it.length > 0)
            {
                var cmd = it.drop(1).split(" ")[0].split("@")[0]
                if (tolower) cmd = cmd.lowercase()
                return it.startsWith("/") && command.contains(cmd)
            }
        }
        update.editedMessage?.text?.let {
            if (it.length > 0)
            {
                var cmd = it.drop(1).split(" ")[0].split("@")[0]
                if (tolower) cmd = cmd.lowercase()
                return it.startsWith("/") && command.contains(cmd)
            }
        }
        return false
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        val isEdited: Boolean
        val message = when {
            update.message != null -> {
                isEdited = false
                update.message
            }
            update.editedMessage != null -> {
                isEdited = true
                update.editedMessage
            }
            else -> {
                isEdited = false
                null
            }
        }

        checkNotNull(message)
        val messageHandlerEnv = CommandHandlerEnvironment(bot, update, message, update.getCommandArgs(), isEdited)
        handleCommand(messageHandlerEnv)
    }

    private fun Update.getCommandArgs(): List<String> =

        message?.text?.split("\\s+".toRegex())?.drop(1) ?: editedMessage?.text?.split("\\s+".toRegex())?.drop(1) ?: emptyList()
}
