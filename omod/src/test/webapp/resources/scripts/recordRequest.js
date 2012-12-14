describe("Tests for medical record requests", function() {

    function verifySelectedRecords(numberSelected) {
        for (var i = 0; i < numberSelected; i++) {
            expect(viewModel.recordsToPull()[i].selected()).toBe(true);
        }
        for (var i = numberSelected; i < viewModel.recordsToPull().length; i++) {
            expect(viewModel.recordsToPull()[i].selected()).toBe(false);
        }
    }

    function verifyHoveredRecords(numberSelected) {
        for (var i = 0; i < numberSelected; i++) {
            expect(viewModel.recordsToPull()[i].hovered()).toBe(true);
        }
        for (var i = numberSelected; i < viewModel.recordsToPull().length; i++) {
            expect(viewModel.recordsToPull()[i].hovered()).toBe(false);
        }
    }

    var thirdRecord = RecordRequestModel(3, "Darius", 1, "A033", "Lacoline", "12:34 pm");
    var secondRecord = RecordRequestModel(2, "Mark", 2, "A021", "Lacoline", "12:34 pm");

    var recordsToPull = [
        RecordRequestModel(1, "Alex", 3, "A001", "Mirebalais", "12:34 pm"),
        secondRecord,
        thirdRecord,
        RecordRequestModel(4, "Neil", 4, "A045", "Lacoline", "12:34 pm"),
        RecordRequestModel(5, "Mario", 5, "A101", "Lacoline", "12:34 pm"),
        RecordRequestModel(6, "Renee", 6, "A121", "Lacoline", "12:34 pm"),
        RecordRequestModel(7, "Ellen", 7, "A234", "Lacoline", "12:34 pm"),
        RecordRequestModel(8, "Mike", 8, "A235", "Lacoline", "12:34 pm")
    ];
    var viewModel = PullRequestsViewModel(recordsToPull);


    it("should select first three records", function() {
        viewModel.selectRequestToBePulled(thirdRecord);
        verifySelectedRecords(3);
    });

    it("should select first two records after selecting three records", function() {
        viewModel.selectRequestToBePulled(thirdRecord);
        viewModel.selectRequestToBePulled(secondRecord);
        verifySelectedRecords(2);
    });

    it("should select by number", function() {
        viewModel.selectNumber(4);
        verifySelectedRecords(4);
        viewModel.selectNumber(1);
        verifySelectedRecords(1);
    });

    it("should hover by number", function() {
        viewModel.hoverNumber(4);
        verifyHoveredRecords(4);
        viewModel.hoverNumber(1);
        verifyHoveredRecords(1);
    });

    it("should hover all if number is larger than size", function () {
        viewModel.hoverNumber(10);
        verifyHoveredRecords(8);
    });

    it("should select all if number is larger than size", function () {
       viewModel.selectNumber(10);
        verifySelectedRecords(8);
    });

    it("should compute when none are selected", function() {
        viewModel.selectNumber(0);
        expect(viewModel.selectedRequests().length).toBe(0);
    });

    it("should compute when none are hovered", function() {
        viewModel.hoverNumber(0);
        expect(viewModel.hoveredRequests().length).toBe(0);
    });

    it("should compute when some are selected", function() {
        viewModel.selectNumber(2);
        expect(viewModel.selectedRequests().length).toBe(2);
    });

    it("should compute when some are hovered", function() {
        viewModel.hoverNumber(2);
        expect(viewModel.hoveredRequests().length).toBe(2);
    });

    it("should unhover all the rows", function() {
        viewModel.hoverNumber(2);
        expect(viewModel.hoveredRequests().length).toBe(2);
        viewModel.unHoverRecords();
        expect(viewModel.hoveredRequests().length).toBe(0);
    });

    it("should asses that the viewModel without selected records is not valid", function() {
        viewModel.selectNumber(0);
        expect(viewModel.selectedRequests().length).toBe(0);
        expect(viewModel.isValid()).toBe(false);
    });

    it("should asses that the viewModel with selected records is valid", function() {
        viewModel.selectNumber(1);
        expect(viewModel.selectedRequests().length).toBe(1);
        expect(viewModel.isValid()).toBe(true);
    });
})