/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.jaxws.cxf.processor;

import jakarta.jws.WebService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author gpor89
 * @since 3.0.0
 */
public class JaxWsAnnotationProcessor extends AbstractProcessor {

    private static final Logger LOG = Logger.getLogger(JaxWsAnnotationProcessor.class.getName());

    private Filer filer;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(WebService.class);
        Set<String> wsElementNames = new HashSet<>();
        for (Element e : elements) {
            getElementName(wsElementNames, e);
        }

        try {
            if (!wsElementNames.isEmpty()) {
                AnnotationProcessorUtil.writeFile(wsElementNames, "META-INF/ws/java.lang.Object", filer);
            }
        } catch (IOException e) {
            LOG.warning(e.getMessage());
        }

        return false;
    }

    private void getElementName(Set<String> corsElementNames, Element e) {

        ElementKind elementKind = e.getKind();

        if (elementKind.equals(ElementKind.CLASS)) {
            corsElementNames.add(e.toString());
        }
    }
}

