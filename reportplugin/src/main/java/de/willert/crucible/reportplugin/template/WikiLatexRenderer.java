/*
 * Willert Software Tools GmbH
 * Copyright (C) 2017 Willert Software Tools GmbH
 * This file is covered by the LICENSE file in the root of this project.
 */

package de.willert.crucible.reportplugin.template;

import de.no3x.latex.wikitext.builder.LatexSnippetBuilder;
import org.eclipse.mylyn.wikitext.confluence.core.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

import java.io.StringWriter;

/**
 * Created by czoeller on 17.01.17.
 */
public class WikiLatexRenderer {

    private final MarkupParser markupParser;
    private final LatexSnippetBuilder latexSnippetBuilder;

    public WikiLatexRenderer() {
        markupParser = new MarkupParser(new ConfluenceLanguage());
        markupParser.setMarkupLanguage(new ConfluenceLanguage());
        latexSnippetBuilder = new LatexSnippetBuilder(new StringWriter());
        markupParser.setBuilder(latexSnippetBuilder);
    }

    public String markupToLatex(String markup) {
        markupParser.parse(markup);
        latexSnippetBuilder.getWriter().flush();
        final String latexCode = latexSnippetBuilder.getWriter()
                                            .toString();
        latexSnippetBuilder.getWriter().getBuffer().setLength(0);
        return latexCode;
    }
}
