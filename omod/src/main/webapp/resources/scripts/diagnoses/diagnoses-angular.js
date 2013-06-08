var app = angular.module('diagnoses', []);

function DiagnosesController($scope) {
    $scope.encounterDiagnoses = diagnoses.EncounterDiagnoses();

    $scope.addDiagnosis = function() {
        $scope.encounterDiagnoses.addDiagnosis(diagnoses.Diagnosis(diagnoses.CodedOrFreeTextConceptAnswer($scope.freeTextDiagnosis)));
        $scope.freeTextDiagnosis = '';
    };

    $scope.removeDiagnosis = function(diagnosis) {
        $scope.encounterDiagnoses.removeDiagnosis(diagnosis);
    };

    $scope.valueToSubmit = function() {
        return "[" + _.map($scope.encounterDiagnoses.diagnoses, function(d) {
            return d.valueToSubmit();
        }).join(", ") + "]";
    };
}
