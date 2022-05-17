package io.euryale.docx.render.cli.command

import org.docx4j.fonts.IdentityPlusMapper
import picocli.CommandLine

@CommandLine.Command(
    name = "list-fonts",
    description = ["列出当前支持的字体"]
)
class ListFontsCommand :BaseCommand(){

    override fun run() {

        val fontMapper = IdentityPlusMapper()
        loadFonts(fontMapper, null)
        this.printSupportFonts(fontMapper)

    }
}