package no.ssb.vtl.tools.termd;

/*-
 * ========================LICENSE_START=================================
 * Java VTL
 * %%
 * Copyright (C) 2016 - 2017 Hadrien Kohl
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParsingResult {
    public Parser parser;
    public ParseTree tree;
    public SyntaxErrorListener syntaxErrorListener;

    public ParsingResult(Parser parser, ParseTree tree, SyntaxErrorListener syntaxErrorListener) {
        this.parser = parser;
        this.tree = tree;
        this.syntaxErrorListener = syntaxErrorListener;
    }
}
