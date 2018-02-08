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
(function (mod) {
    if (typeof exports == "object" && typeof module == "object") // CommonJS
        mod(require("../../lib/codemirror"));
    else if (typeof define == "function" && define.amd) // AMD
        define(["../../lib/codemirror"], mod);
    else // Plain browser env
        mod(CodeMirror);
})(function (CodeMirror) {
    "use strict";

    CodeMirror.registerHelper("lint", "vtl", function (text, callback) {
        var found = [];

        // {message, severity, from, to}
        fetch('/check', {
            method: "POST",
            body: text
        }).then(function (response) {
            return response.json();
        }).then(function (json) {
            var errors = [];
            for (var i = 0; i < json.length; i++) {
                errors.push({
                    from: CodeMirror.Pos(json[i].startLine - 1, json[i].startColumn),
                    to: CodeMirror.Pos(json[i].stopLine - 1, json[i].stopColumn),
                    message: json[i].message,
                    severity: "error"
                });
            }
            callback(errors, false);
        }).catch(function (error) {
            // handle network error
            console.log(error);
        });
    });

});
