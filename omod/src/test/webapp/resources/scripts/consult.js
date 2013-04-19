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
        expect(viewModel.diagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(false);
    });

    it("should select primary diagnosis", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        expect(viewModel.diagnoses().length).toBe(1);
        expect(viewModel.diagnoses()[0].diagnosis()).toBe(options[2]);
        expect(viewModel.diagnoses()[0].confirmed()).toBe(false);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select primary diagnosis and mark it confirmed", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        viewModel.diagnoses()[0].confirmed(true);
        expect(viewModel.diagnoses().length).toBe(1);
        expect(viewModel.diagnoses()[0].diagnosis()).toBe(options[2]);
        expect(viewModel.diagnoses()[0].confirmed()).toBe(true);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        expect(viewModel.diagnoses().length).toBe(1);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[2]));
        expect(viewModel.diagnoses()[0].confirmed()).toBe(false);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice with different names", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[0])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[1])));
        expect(viewModel.diagnoses().length).toBe(1);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[0]));
        expect(viewModel.diagnoses()[0].confirmed()).toBe(false);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select primary and secondary diagnoses", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[1])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));

        expect(viewModel.diagnoses().length).toBe(2);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[1]));
        expect(viewModel.diagnoses()[0].confirmed()).toBe(false);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.diagnoses()[1].diagnosis()).toEqual(ConceptSearchResult(options[2]));
        expect(viewModel.diagnoses()[1].confirmed()).toBe(false);
        expect(viewModel.diagnoses()[1].primary()).toBe(false);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select two primary diagnoses", function() {
        var d1 = Diagnosis(ConceptSearchResult(options[1]));
        d1.primary(true);
        var d2 = Diagnosis(ConceptSearchResult(options[2]));
        d2.primary(true);
        viewModel.addDiagnosis(d1);
        viewModel.addDiagnosis(d2);
        expect(viewModel.diagnoses().length).toBe(2);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[1]))
        expect(viewModel.diagnoses()[1].primary()).toBe(true);
        expect(viewModel.diagnoses()[1].diagnosis()).toEqual(ConceptSearchResult(options[2]))
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select a primary and secondary diagnosis and make them both primary", function() {
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[1])));
        viewModel.addDiagnosis(Diagnosis(ConceptSearchResult(options[2])));
        expect(viewModel.diagnoses().length).toBe(2);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[1]))
        expect(viewModel.diagnoses()[1].primary()).toBe(false);
        expect(viewModel.diagnoses()[1].diagnosis()).toEqual(ConceptSearchResult(options[2]))
        expect(viewModel.isValid()).toBe(true);

        viewModel.diagnoses()[1].primary(true);
        expect(viewModel.diagnoses().length).toBe(2);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.diagnoses()[0].diagnosis()).toEqual(ConceptSearchResult(options[1]))
        expect(viewModel.diagnoses()[1].primary()).toBe(true);
        expect(viewModel.diagnoses()[1].diagnosis()).toEqual(ConceptSearchResult(options[2]))
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
        viewModel.addDiagnosis(Diagnosis(FreeTextListItem(diagnosis1)));
        viewModel.addDiagnosis(Diagnosis(FreeTextListItem(diagnosis2)));
        expect(viewModel.diagnoses().length).toBe(2);
        expect(viewModel.diagnoses()[0].diagnosis()).toBeNonCodedDiagnosis(diagnosis1);
        expect(viewModel.diagnoses()[0].confirmed()).toBe(false);
        expect(viewModel.diagnoses()[0].primary()).toBe(true);
        expect(viewModel.diagnoses()[1].diagnosis()).toBeNonCodedDiagnosis(diagnosis2);
        expect(viewModel.diagnoses()[1].confirmed()).toBe(false);
        expect(viewModel.diagnoses()[1].primary()).toBe(false);
        expect(viewModel.isValid()).toBe(true);
    });

});