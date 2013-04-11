describe("Outpatient consult form", function() {

    var options = [
        {
            "conceptName": {
                "id": 840,
                "conceptNameType": 'FULLY_SPECIFIED',
                "name": "Malaria"
            },
            "concept": {
                "id": 167,
                "conceptMappings": [
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "123",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    },
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "MALARIA",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    },
                    {
                        "conceptMapType": "NARROWER-THAN",
                        "conceptReferenceTerm": {
                            "code": "B54",
                            "name": null,
                            "conceptSource": {
                                "name": "ICD-10-WHO"
                            }
                        }
                    }
                ],
                "preferredName": "Malaria"
            },
            "nameIsPreferred": true
        },
        {
            "conceptName": {
                "id": 834,
                "conceptNameType": null,
                "name": "CLINICAL MALARIA"
            },
            "concept": {
                "id": 167,
                "conceptMappings": [
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "123",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    },
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "MALARIA",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    },
                    {
                        "conceptMapType": "NARROWER-THAN",
                        "conceptReferenceTerm": {
                            "code": "B54",
                            "name": null,
                            "conceptSource": {
                                "name": "ICD-10-WHO"
                            }
                        }
                    }
                ],
                "preferredName": "Malaria"
            },
            "nameIsPreferred": false
        },
        {
            "conceptName": {
                "id": 670,
                "conceptNameType": "FULLY_SPECIFIED",
                "name": "Confirmed malaria"
            },
            "concept": {
                "id": 136,
                "conceptMappings": [
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "7646",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    },
                    {
                        "conceptMapType": "NARROWER-THAN",
                        "conceptReferenceTerm": {
                            "code": "B53.8",
                            "name": null,
                            "conceptSource": {
                                "name": "ICD-10-WHO"
                            }
                        }
                    },
                    {
                        "conceptMapType": "SAME-AS",
                        "conceptReferenceTerm": {
                            "code": "Confirmed malaria",
                            "name": null,
                            "conceptSource": {
                                "name": "PIH"
                            }
                        }
                    }
                ],
                "preferredName": "Confirmed malaria"
            },
            "nameIsPreferred": true
        }
    ];

    var viewModel;

    beforeEach(function() {
        viewModel = ConsultFormViewModel();
    });

    it("should setup correctly", function() {
        expect(viewModel.primaryDiagnosis()).toBeUndefined();
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(false);
    });

    it("should select primary diagnosis", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        expect(viewModel.primaryDiagnosis().diagnosis).toBe(options[2]);
        expect(viewModel.primaryDiagnosis().certainty).toBe("presumed");
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        expect(viewModel.primaryDiagnosis().diagnosis).toEqual(ConceptSearchResult(options[2]));
        expect(viewModel.primaryDiagnosis().certainty).toBe("presumed");
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice with different names", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[0])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[1])));
        expect(viewModel.primaryDiagnosis().diagnosis).toEqual(ConceptSearchResult(options[0]));
        expect(viewModel.primaryDiagnosis().certainty).toBe("presumed");
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select primary and secondary diagnoses", function() {
        viewModel.addDiagnosis(ConceptSearchResult(options[1]));
        viewModel.addDiagnosis(ConceptSearchResult(options[2]));
        expect(viewModel.primaryDiagnosis()).toEqual(ConceptSearchResult(options[1]));
        expect(viewModel.secondaryDiagnoses()).toEqual([ ConceptSearchResult(options[2]) ]);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should support non-coded diagnoses", function() {
        this.addMatchers({
           toBeNonCodedDiagnosis: function(expected) {
               var actual = this.actual;
               return !actual.concept && !actual.conceptId && actual.valueToSubmit() === "Non-Coded:" + expected;
           }
        });
        var diagnosis1 = "Never seen it before";
        var diagnosis2 = "Haven't seen this either";
        viewModel.addDiagnosis(FreeTextListItem(diagnosis1));
        viewModel.addDiagnosis(FreeTextListItem(diagnosis2));
        expect(viewModel.primaryDiagnosis()).toBeNonCodedDiagnosis(diagnosis1);
        expect(viewModel.secondaryDiagnoses().length).toBe(1);
        expect(viewModel.secondaryDiagnoses()[0]).toBeNonCodedDiagnosis(diagnosis2);
        expect(viewModel.isValid()).toBe(true);
    });

});