/*
 * Copyright (C) 2017, Megatron King
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.megatronking.stringfog.plugin;

import com.github.megatronking.stringfog.plugin.utils.SetUtils;
import com.squareup.javawriter.JavaWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.lang.model.element.Modifier;

/**
 * Generate the <code>StringFog</code> class.
 *
 * @author Megatron King
 * @since 2018/9/20 17:41
 */
public final class StringFogClassGenerator {


    public static void generate(File outputFile, String packageName, String className,
                                String implementation, StringFogMode mode) throws IOException {
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Can not mkdirs the dir: " + outputDir);
        }

        int lastIndexOfDot = implementation.lastIndexOf(".");
        String implementationSimpleClassName = lastIndexOfDot == -1 ? implementation :
                implementation.substring(implementation.lastIndexOf(".") + 1);

        JavaWriter javaWriter = new JavaWriter(new FileWriter(outputFile));
        javaWriter.emitPackage(packageName);
        javaWriter.emitEmptyLine();
        javaWriter.emitImports(implementation);
        javaWriter.emitEmptyLine();
        if (mode == StringFogMode.base64) {
            javaWriter.emitImports("com.github.megatronking.stringfog.Base64");
        }

        javaWriter.emitJavadoc("Generated code from StringFog gradle plugin. Do not modify!");
        javaWriter.beginType(className, "class", SetUtils.fromArray(Modifier.PUBLIC,
                Modifier.FINAL));

        javaWriter.emitField(implementationSimpleClassName, "IMPL",
                SetUtils.fromArray(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL),
                "new " + implementationSimpleClassName + "()");

        javaWriter.emitEmptyLine();
        if (mode == StringFogMode.base64) {
            javaWriter.beginMethod(String.class.getSimpleName(), "decrypt",
                    SetUtils.fromArray(Modifier.PUBLIC, Modifier.STATIC),
                    String.class.getSimpleName(), "value",
                    String.class.getSimpleName(), "key");
            javaWriter.emitStatement("return IMPL.decrypt(Base64.decode(value, Base64.DEFAULT), " +
                    "Base64.decode(key, Base64.DEFAULT))");
            javaWriter.endMethod();
        } else if (mode == StringFogMode.bytes) {
            javaWriter.beginMethod(String.class.getSimpleName(), "decrypt",
                    SetUtils.fromArray(Modifier.PUBLIC, Modifier.STATIC),
                    byte[].class.getSimpleName(), "value",
                    byte[].class.getSimpleName(), "key");
            javaWriter.emitStatement("return IMPL.decrypt(value, key)");
            javaWriter.endMethod();
        }

        javaWriter.emitEmptyLine();
        javaWriter.endType();

        javaWriter.close();
    }
}
