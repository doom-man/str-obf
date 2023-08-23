package com.github.megatronking.stringfog.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

class StringFogPlugin : Plugin<Project> {

    companion object {
        private const val PLUGIN_NAME = "stringfog"
        private const val FOG_CLASS_NAME = "StringFog"
    }

    override fun apply(project: Project) {
        project.extensions.create(PLUGIN_NAME, StringFogExtension::class.java)

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            // Check stringfog extension
            val stringfog = project.extensions.getByType(StringFogExtension::class.java)
            if (stringfog.implementation.isNullOrEmpty()) {
                throw IllegalArgumentException("Missing stringfog implementation config")
            }
            if (!stringfog.enable) {
                return@onVariants
            }
            //TODO: 从xml解析applicationId改为填入
            val applicationId = "com.example.strobf"
            if (applicationId.isNullOrEmpty()) {
                throw IllegalArgumentException("Unable to resolve applicationId")
            }
//            forEachVariant(extension) { variant ->
//                try {
//                    val stringfogDir = File(project.buildDir, "generated" +
//                            File.separatorChar + "source" + File.separatorChar + "stringFog" + File.separatorChar + variant.name.capitalized().lowercase())
//                    val provider = project.tasks.register("generateStringFog${variant.name.replaceFirstChar {
//                        if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
//                    }}", SourceGeneratingTask::class.java) { task ->
//                        task.genDir.set(stringfogDir)
//                        task.applicationId.set(applicationId)
//                        task.implementation.set(stringfog.implementation)
//                        task.mode.set(stringfog.mode)
//                    }
//                    variant.registerJavaGeneratingTask(provider, stringfogDir)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }

            project.tasks.getByName("preBuild").doLast {
                val variantName = variant.name.capitalized()
                val javaPreCompileTasks = project.getTasksByName("generate${variantName}Resources", true)
                if (javaPreCompileTasks.isEmpty()) {
                    throw IllegalArgumentException("Unable to resolve task javaPreCompile${variantName}")
                }
                javaPreCompileTasks.first().doFirst {
//                    generateStringFogClass(applicationId, project.buildDir, variantName.toLowerCase(), stringfog.implementation!!, stringfog.mode)
                    generateStringFogClass(applicationId, project.buildDir, variantName.toLowerCase(), stringfog.implementation!!, stringfog.mode)

                }
            }

            if(variant.name.capitalized().toLowerCase()== "debug"){
//                return@onVariants //debug模式不加密
                val printFile = File(project.buildDir, "outputs/mapping/${variant.name}/debug.txt")
                debugStringFogTransform.setParameters(stringfog, printFile, "$applicationId.$FOG_CLASS_NAME")
                variant.instrumentation.transformClassesWith(
                    debugStringFogTransform::class.java,
                    InstrumentationScope.PROJECT) {
                }
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
            }
            else if (variant.name.capitalized().toLowerCase()== "release"){
                val printFile = File(project.buildDir, "outputs/mapping/${variant.name}/release.txt")
                StringFogTransform.setParameters(stringfog, printFile, "$applicationId.$FOG_CLASS_NAME")
                variant.instrumentation.transformClassesWith(
                    StringFogTransform::class.java,
                    InstrumentationScope.PROJECT) {
                }
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
            }

        }
    }

    private fun generateStringFogClass(applicationId: String, buildDir: File, variant: String, implementation: String, mode: StringFogMode) {

        val stringfogDir = File(buildDir, "generated" +
                File.separatorChar + "source" + File.separatorChar + "buildConfig" + File.separatorChar + variant)
        val outputFile = File(stringfogDir, applicationId.replace('.', File.separatorChar) + File.separator + "StringFog.java")


        StringFogClassGenerator.generate(outputFile, applicationId, FOG_CLASS_NAME,
            implementation, mode )
    }



}