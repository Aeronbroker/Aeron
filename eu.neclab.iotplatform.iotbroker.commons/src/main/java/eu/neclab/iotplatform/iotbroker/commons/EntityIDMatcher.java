/*******************************************************************************
 *   Copyright (c) 2014, NEC Europe Ltd.
 *   All rights reserved.
 *   
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Raihan Ul-Islam - raihan.ul-islam@neclab.eu
 *  
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgement:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of the NEC nor the
 *     names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package eu.neclab.iotplatform.iotbroker.commons;

import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

/**
 * A class for matching entity ids against other entity ids. The actual matching
 * is implemented by a static method.
 * 
 */
public class EntityIDMatcher {

	public EntityIDMatcher() {

	}

	/**
	 * Evaluates whether two {@link EntityIDMatcher}EntityId instances match.
	 * Two EntityId instances are defined to match when both their types match
	 * and their id fields match. <br>
	 * The types of two EntityId instances are defined to match if either the
	 * type of at least one of the instances is not defined, or if both types
	 * are defined and are equal. Sub-type matching is not supported by this
	 * method. <br>
	 * The id fields of two EntityId instances are defined to match if either
	 * none of the instances is a pattern and the id strings are equal, or if
	 * exactly one of the instances is a pattern and the id of the other
	 * instance matches the pattern. When both instances are patterns they are
	 * only matching if the patterns are exactly equal, so matching patterns
	 * against other patterns is in general not supported.
	 * 
	 * @param e1
	 *            The first EntityId instance.
	 * @param e2
	 *            The second EntityId instance.
	 * @return true if the instances match.
	 */
	public static boolean matcher(EntityId e1, EntityId e2) {

		return typeMatcher(e1, e2) || idsMatcher(e1, e2);

	}

	private static boolean typeMatcher(EntityId e1, EntityId e2) {

		boolean status = false;

		if ((e1.getType() == null) || (e2.getType() == null)
				|| (e1.getType().toString().equals(e2.getType().toString()))) {
			status = true;
		}

		return status;

	}

	private static boolean idsMatcher(EntityId e1, EntityId e2) {

		boolean status = false;

		if ((e1.getIsPattern()) && (!e2.getIsPattern())
				&& (e1.getId().matches(e2.getId()))) {
			status = true;
		} else if ((!e1.getIsPattern()) && (e2.getIsPattern())
				&& (e2.getId().matches(e1.getId()))) {
			status = true;
		} else if ((e1.getIsPattern()) && (e2.getIsPattern())
				&& (e2.getId().equals(e1.getId()))) {
			status = true;
		} else if ((!e1.getIsPattern()) && (!e2.getIsPattern())
				&& (e2.getId().equals(e1.getId()))) {
			status = true;
		}
		return status;
	}
}
