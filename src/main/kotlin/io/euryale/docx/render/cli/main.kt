package io.euryale.docx.render.cli

import io.euryale.docx.render.cli.command.ListFontsCommand
import io.euryale.docx.render.cli.command.PdfCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "docx-render-cli",
    subcommands = [
        PdfCommand::class,
        ListFontsCommand::class,
        CommandLine.HelpCommand::class
    ]
)
class MainCommand : Runnable{

    override fun run() {
    }
}

suspend fun main(args: Array<String>) {

    System.setProperty("log4j2.loggerContextFactory", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory")

    val main = MainCommand()
    val commandLine = CommandLine(main)
    commandLine.execute(*args)
}
