//Uses the namespace pattern from http://stackoverflow.com/a/5947280
(function( encounterTemplates, $, undefined) {
		
	var templates = {};
	var defaultTemplate;
	var encounterIcons = {};
	
	encounterTemplates.setEncounterTemplate = function(uuid, template) {
		templates[uuid] = template;
	};
	
	encounterTemplates.setDefaultEncounterTemplate = function(template) {
		defaultTemplate = template;
	};
	
	encounterTemplates.setEncounterIcon = function(uuid, icon) {
		encounterIcons[uuid] = icon;
	};

	encounterTemplates.displayEncounter = function(encounter) {
		var template;
		if (templates[encounter.encounterType.uuid]) {
			template = templates[encounter.encounterType.uuid];
		} else {
			template = defaultTemplate;
		}
		
		var data = new Object();
		data.encounter = encounter;
		if (encounterIcons[encounter.encounterType.uuid]) {
			data.encounter.icon = encounterIcons[encounter.encounterType.uuid];
		} else {
			data.encounter.icon = "icon-time";
		}
		
		return template(data);
	};
	
}( window.encounterTemplates = window.encounterTemplates || {}, jQuery ));