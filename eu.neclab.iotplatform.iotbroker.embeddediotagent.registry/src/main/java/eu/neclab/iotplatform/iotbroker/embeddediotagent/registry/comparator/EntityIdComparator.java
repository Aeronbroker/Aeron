/*******************************************************************************
 *   Copyright (c) 2015, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Flavio Cirillo - flavio.cirillo@neclab.eu
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgment:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of NEC nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific 
 *   prior written permission.
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

package eu.neclab.iotplatform.iotbroker.embeddediotagent.registry.comparator;

import java.util.Comparator;

import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class EntityIdComparator implements Comparator<EntityId>{

	/**
	 * The order of the Segment objects takes into account, in order:
	 * isPattern -> Id -> Type
	 * 
	 * Null object is order before a not-null object.
	 * If o1.Id is null and o2.Id is not-null the former is ordered
	 * before the latter. Similarly works for the other fields.
	 * 
	 */
	@Override
	public int compare(EntityId o1, EntityId o2) {
		if (o1 == o2){
			return 0;
		} else if (o1 == null){
			return -1;
		} else if (o2 == null){
			return 1;
		}
		
		if (o1.getIsPattern() != o2.getIsPattern()){
			if(!o1.getIsPattern()){
				return -1;
			} else {
				return 1;
			}
		}
		
		if (o1.getId() != o2.getId() ){
			if (o1.getId() == null){
				return -1;
			} else if (o2.getId() == null){
				return 1;
			} else if (!o1.getId().equals(o2.getId())){
				return o1.getId().compareTo(o2.getId());
			}
		}
		
		if (o1.getType() != o2.getType() ){
			if (o1.getType() == null){
				return -1;
			} else if (o2.getType() == null){
				return 1;
			} else if (!o1.getType().equals(o2.getType())){
				return o1.getType().compareTo(o2.getType());
			}
		}
		
		return 0;
	}
	
}
