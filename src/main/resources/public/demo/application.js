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
angular.module('vtl', ['ui.codemirror', 'angular.filter'])
    .controller('ExecutionController', ['$scope', '$http', '$q', function ($scope, $http, $q) {
        "use strict";
        $scope.expression = "ssbDataset := get(\"http://data.ssb.no/api/v0/dataset/1102\")\n" +
            "klassDataset := get(\"http://data.ssb.no/api/klass/v1/classifications/20/codes?from=2013-01-01\")";

        $scope.editorOptions = {
            lineWrapping: true,
            lineNumbers: true,
            indentUnit: 4,
            tabSize: 4,
            mode: "vtl",
            gutters: ["CodeMirror-lint-markers"],
            lint: {
                async: true
            },
            extraKeys: {
                "F11": function(cm) {
                    cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                },
                "Esc": function(cm) {
                    if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                }
            }
        };


        $scope.options = {
            limitTuple: 5
        };

        $scope.variables = [];

        $scope.datasets = {};

        $scope.executionError = null;

        $scope.fetchData = function (dataset) {
            if (angular.isUndefined($scope.datasets[dataset])) {
                $scope.datasets[dataset] = {};
            }

            var data = $http.get("/dataset/" + dataset + "/data");

            data.then(function (response) {
                $scope.datasets[dataset]["data"] = response.data;
            },function (response) {
                $scope.datasets[dataset]["error"] = response.data;
            });
        };

        $scope.remove = function (dataset) {
            var data = $http.delete("/dataset/" + dataset);
            data.then(function () {
                delete $scope.datasets[dataset];
                var index = $scope.variables.indexOf(dataset)
                if (index !== -1) {
                    $scope.variables.splice(index, 1);
                }
            });
        };

        $scope.roleOrder = function (variable) {
            switch(variable.role) {
                case "IDENTIFIER":
                    return 1;
                case "MEASURE":
                    return 2;
                case "ATTRIBUTE":
                    return 3;
                default:
                    return 4;
            }
        };

        function normalizeStructure(structure) {
            const normalized = [];
            for (var name in structure) {

                if (!structure.hasOwnProperty(name))
                    continue;

                normalized.push({
                    name: name,
                    type: structure[name].type,
                    role: structure[name].role
                })
            }
            return normalized;
        }

        $scope.execute = function () {
            // Simple GET request example:
            $http({
                data: $scope.expression,
                method: 'POST',
                url: '/execute'
            }).then(function successCallback(response) {

                $scope.executionError = null;
                var datasets = response.data;
                var promises = {};

                $scope.variables = datasets;

                for (var i in datasets) {
                    var dataset = datasets[i];

                    if (angular.isUndefined($scope.datasets[dataset])) {
                        $scope.datasets[dataset] = {};
                    }
                    var promise = $http.get("/dataset/" + dataset + "/structure");
                    promises[dataset] = promise.then(function (response) {
                        const structure = normalizeStructure(response.data.dataStructure);
                        return {
                            variables: structure
                        };
                    },function (response) {
                        return { error: response.data};
                    });
                }

                $q.all(promises).then(function (result) {
                    $scope.datasets = result;
                })

            }, function errorCallback(response) {
                $scope.executionError = response.data;
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });
        }
    }]);
