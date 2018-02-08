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
/* Example definition of a simple mode that understands a subset of
 * JavaScript:
 */

CodeMirror.defineSimpleMode("vtl", {
    // The start state contains the rules that are initially used
    start: [

        {regex: /\/\*/, token: "comment", next: "comment", indent: true},
        {regex: /"[^"]*"/, token: "string" },

        {regex: /get|put|and|or|xor/, token: "keyword"},

        // Incomplete, escaped quotes ('') are not matched
        {regex: /(:?[a-zAz][a-zA-Z0-9_-]+|'[^'\n\r]+')/, token:"variable-2"},
        {regex: /(:?\.[a-zAz][a-zA-Z0-9_-]+|'[^'\n\r]+')/, token:"variable-3"},

        {regex: /true|false|null/, token: "atom"},
        {regex: /[0-9]+\.[0-9]+[eE]?[+-]?[0-9]+/, token: "number"},
        {regex: /[0-9]+\.[0-9]+/, token: "number"},
        {regex: /[0-9]+/, token: "number"},

        {regex: /\[/, next: "join"},
        {regex: /\{/, indent: true, next: "block" },
    ],

    join: [
        {regex: /inner|outer|cross|on/, token: "keyword" },
        {regex: /(:?[a-zAz][a-zA-Z0-9_-]+|'[^'\n\r]+')/, token:"variable-2"},
        {regex: /]/, next: "start" },

    ],

    block: [
        {regex: /rename|fold|unfold|keep|drop|filter|to/, token: "keyword" },
        {regex: /(:?[a-zAz][a-zA-Z0-9_-]+|'[^'\n\r]+')/, token:"variable-2"},
        {regex: /(:?\.[a-zAz][a-zA-Z0-9_-]+|'[^'\n\r]+')/, token:"variable-3"},

        {regex: /true|false|null/, token: "atom"},
        {regex: /[0-9]+\.[0-9]+[eE]?[+-]?[0-9]+/, token: "number"},
        {regex: /[0-9]+\.[0-9]+/, token: "number"},
        {regex: /[0-9]+/, token: "number"},

        {regex: /}/, dedent: true, next: "start" }
    ],

    // The multi-line comment state.
    comment: [
        {regex: /.*?\*\//, dedent: true, token: "comment", next: "start"},
        {regex: /.*/, token: "comment"}
    ],
    // The meta property contains global information about the mode. It
    // can contain properties like lineComment, which are supported by
    // all modes, and also directives like dontIndentStates, which are
    // specific to simple modes.
    meta: {
        dontIndentStates: ["comment"],
        lineComment: "//"
    }
});
