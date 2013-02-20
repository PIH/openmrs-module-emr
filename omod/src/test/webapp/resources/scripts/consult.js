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
        viewModel.addDiagnosis(options[2]);
        expect(viewModel.primaryDiagnosis()).toBe(options[2]);
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice", function() {
        viewModel.addDiagnosis(options[2]);
        viewModel.addDiagnosis(options[2]);
        expect(viewModel.primaryDiagnosis()).toBe(options[2]);
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should not be able to select the same diagnosis twice with different names", function() {
        viewModel.addDiagnosis(options[0]);
        viewModel.addDiagnosis(options[1]);
        expect(viewModel.primaryDiagnosis()).toBe(options[0]);
        expect(viewModel.secondaryDiagnoses().length).toBe(0);
        expect(viewModel.isValid()).toBe(true);
    });

    it("should select primary and secondary diagnoses", function() {
        viewModel.addDiagnosis(options[1]);
        viewModel.addDiagnosis(options[2]);
        expect(viewModel.primaryDiagnosis()).toBe(options[1]);
        expect(viewModel.secondaryDiagnoses()).toEqual([options[2]]);
        expect(viewModel.isValid()).toBe(true);
    });

});