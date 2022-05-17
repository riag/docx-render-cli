package io.euryale.docx.render.test

import io.euryale.docx.render.cli.command.BaseCommand
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.status.StatusLogger
import org.docx4j.Docx4J
import org.docx4j.fonts.IdentityPlusMapper
import org.docx4j.fonts.PhysicalFont
import org.docx4j.fonts.PhysicalFonts
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream

class Docx2PdfTester {

    @Test
    fun test(){

        System.setProperty("log4j2.loggerContextFactory", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory")
        System.setProperty("org.apache.logging.log4j.simplelog.level", "WARN")

        val context = LogManager.getContext()
        //System.setProperty("log4j2.debug", "true")

        val docxFile = this.javaClass.getResource("/tpl.docx")!!.file

        val outputDirFile = File(this.javaClass.getResource("/")!!.file, "output")
        if(!outputDirFile.isDirectory){
            outputDirFile.mkdirs()
        }

        val outputPdfFile = File(outputDirFile, "tpl.pdf")
        println("pdf file path is ${outputPdfFile.absolutePath}")

        val fontMapper = IdentityPlusMapper()

        val wpmlp = Docx4J.load(File(docxFile))
        val fontsInUse = wpmlp.mainDocumentPart.fontsInUse()
        print("fontsInUse: ")
        println(fontsInUse)


        BaseCommand.loadFonts(fontMapper, null)
        BaseCommand.printSupportFonts(fontMapper)
        BaseCommand.checkUseFonts(fontMapper, wpmlp)

        wpmlp.setFontMapper(fontMapper)

        val outputStream = FileOutputStream(outputPdfFile)


        try{
            Docx4J.toPDF(wpmlp, outputStream)
        }finally {
            outputStream.close()
        }
    }
}