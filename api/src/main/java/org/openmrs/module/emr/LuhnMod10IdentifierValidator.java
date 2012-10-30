package org.openmrs.module.emr;

/**
 * A IdentifierValidator based on the Luhn Mod-N Algorithm, with "0123456789" as the legal characters.
 * See http://en.wikipedia.org/wiki/Luhn_mod_N_algorithm
 * 
 * This improves on the {@link org.openmrs.patient.impl.LuhnIdentifierValidator} class in the OpenMRS core
 * by not adding a hyphen between the base of the identifier and its check digit. ("24687" instead of "2468-7")
 */
public class LuhnMod10IdentifierValidator extends LuhnModNIdentifierValidator {
	
	/**
	 * @see org.openmrs.module.idgen.validator.LuhnModNIdentifierValidator#getBaseCharacters()
	 */
	@Override
	public String getBaseCharacters() {
		return "0123456789";
	}
	
}