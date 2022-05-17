package io.euryale.docx.render.cli.command

import org.docx4j.fonts.IdentityPlusMapper
import org.docx4j.fonts.PhysicalFonts
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import java.io.File

abstract class BaseCommand :Runnable{

    companion object{
        fun loadFonts(fontMapper: IdentityPlusMapper, workDir: File?){

            if(workDir != null){
                val fontDir = File(workDir, "fonts")
                if(fontDir.isDirectory){
                    val fontList = fontDir.listFiles().filter {
                        return@filter  it.name.endsWith(".ttf")
                    }
                    fontList.forEach {
                        PhysicalFonts.addPhysicalFont(it.toURI())
                    }
                }
            }

            // 新建一个空的 docx 文档，都会有用到 Calibri 字体
            // 所以在 linux 下使用 carlito 替代  windows 下的 Calibri
            val calibriFont = fontMapper.get("Calibri")

            for(entity in PhysicalFonts.getPhysicalFonts()){
                val fontTriplets = entity.value.embedFontInfo.fontTriplets
                if(fontTriplets != null && fontTriplets.size > 1){
                    for(i in 1 until fontTriplets.size){
                        val fontTriplet = fontTriplets.get(i)
                        if(fontMapper.get(fontTriplet.name) == null){
                            fontMapper.put(fontTriplet.name, entity.value)
                        }
                    }
                }
                if(calibriFont == null){
                    if(entity.key.startsWith("carlito")){
                        val k = entity.key.replace("carlito", "Calibri")
                        fontMapper.put(k, entity.value)
                    }
                }
            }

        }

        fun checkUseFonts(fontMapper: IdentityPlusMapper, wpmlp: WordprocessingMLPackage){

            val fontsInUse = wpmlp.mainDocumentPart.fontsInUse()
            val notFoundFonts = ArrayList<String>(fontsInUse.size)
            for(name in fontsInUse){
                val font = fontMapper.get(name)
                if(font == null){
                    notFoundFonts.add(name)
                }
            }
            if(notFoundFonts.size > 0){
                println("没有找到以下字体: ${notFoundFonts.joinToString()}")
                println("")
                System.exit(-1)
            }
        }

        fun printSupportFonts(fontMapper: IdentityPlusMapper){

            println("支持以下字体:")
            for(entity in PhysicalFonts.getPhysicalFonts()){
                println("字体名字: ${entity.key}")
                print("字体信息: ")
                println(entity.value.embedFontInfo.toString())
                println("")
            }
            println("")
        }
    }

}