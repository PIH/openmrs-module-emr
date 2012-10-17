describe("X-ray studies selection", function() {
    var viewModel = StudiesViewModel([
        {"value": 1, "label": "First Study"},
        {"value": 2, "label": "Second Study"},
        {"value": 3, "label": "Third Study"}]);

    it("should initialize correctly", function() {
        expect(viewModel.studies().length).toBe(3);
        expect(viewModel.selectedStudies().length).toBe(0);
    });

    it("should select and deselect a study", function() {
        var firstStudy = Study(1, "First Study");
        viewModel.selectStudy(firstStudy);

        expect(viewModel.studies().length).toBe(2);
        expect(viewModel.selectedStudies().length).toBe(1);
        expect(viewModel.selectedStudies()[0].name).toBe("First Study");

        viewModel.unselectStudy(firstStudy);
        expect(viewModel.studies().length).toBe(3);
        expect(viewModel.selectedStudies().length).toBe(0);
    });

})