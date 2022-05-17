package io.euryale.docx.render.cli.command

import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.config.Configure
import org.docx4j.Docx4J
import org.docx4j.events.EventFinished
import org.docx4j.events.StartEvent
import org.docx4j.events.WellKnownProcessSteps
import org.docx4j.fonts.IdentityPlusMapper
import picocli.CommandLine
import java.io.File
import java.io.FileOutputStream

@CommandLine.Command(
    name = "pdf",
    description = ["convert docx to pdf"]

)
class PdfCommand : BaseCommand(){

    @CommandLine.Option(
        names=["-f"],
        required = true,
        description = ["dox tpl file"]
    )
    lateinit var docxFile: File

    @CommandLine.Option(
        names=["--logfile"],
        description = ["logfile path"]
    )
    var logFile: File? = null

    @CommandLine.Option(
        names=["--builddir"],
        description = ["build dir path"]
    )
    var buildDir: File? = null

    @CommandLine.Option(
        names=["--data"],
        description = ["data source"]
    )
    var dataSourceList = ArrayList<String>(30)


    override fun run() {

        System.setProperty("org.apache.logging.log4j.simplelog.level", "INFO")

        if(logFile != null){
            System.setProperty("org.apache.logging.log4j.simplelog.logFile", logFile!!.absolutePath)
            println("log file is ${logFile!!.absolutePath}")
        }

        val viewData = HashMap<String, Any>()
        System.getProperties().forEach(){
            viewData.put(it.key as String, it.value)
        }

        println("")
        for(item in dataSourceList){
            val c = item.indexOf("=")
            if(c == -1){
                println("${item} 格式不正确")
                System.exit(-1)
            }

            val key = item.substring(0, c)
            val value = item.substring(c+1)
            viewData.put(key, value)
        }

        val workDir = docxFile.parentFile

        if(buildDir == null){
            buildDir = File(docxFile.parent, "build")
            buildDir = File(buildDir, "docx")
        }

        buildDir!!.mkdirs()

        // render
        val renderDocxFile = File(buildDir, docxFile.name)
        val config = Configure.builder()
            .useSpringEL()
            .build()

        val template = XWPFTemplate.compile(docxFile, config).render(viewData)

        template.writeToFile(renderDocxFile.absolutePath)

        // 生成 pdf

        val fontMapper = IdentityPlusMapper()

        loadFonts(fontMapper, workDir)

        val outputPdfFile = File(buildDir, "${docxFile.nameWithoutExtension}.pdf")
        val wpmlp = Docx4J.load(renderDocxFile)

        checkUseFonts(fontMapper, wpmlp)

        wpmlp.fontMapper = fontMapper

        val outputStream = FileOutputStream(outputPdfFile)

        try{
            Docx4J.toPDF(wpmlp, outputStream)

            println("")
            println("生成 pdf 文档成功, 路径为:")
            println(outputPdfFile.absolutePath)
        }finally {
            outputStream.close()
        }
    }
}