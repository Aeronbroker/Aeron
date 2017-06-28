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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class ContextRegistrationComparator implements Comparator<ContextRegistration>{

	@Override
	public int compare(ContextRegistration o1, ContextRegistration o2) {
		if (o1 == o2){
			return 0;
		} else if (o1 == null){
			return -1;
		} else if (o2 == null){
			return 1;
		}
		
		if (o1.getProvidingApplication() != o2.getProvidingApplication() ){
			if (o1.getProvidingApplication() == null){
				return -1;
			} else if (o2.getProvidingApplication() == null){
				return 1;
			} else if (!o1.getProvidingApplication().equals(o2.getProvidingApplication())){
				return o1.getProvidingApplication().compareTo(o2.getProvidingApplication());
			}
		}
		
		
		if (o1.getListEntityId() != o2.getListEntityId()) {
			List<EntityId> o1List = new ArrayList<EntityId>(o1.getListEntityId());
			List<EntityId> o2List = new ArrayList<EntityId>(o2.getListEntityId());
			EntityIdComparator entityIdComparator = new EntityIdComparator();
			Collections.sort(o1List,entityIdComparator);
			Collections.sort(o2List,entityIdComparator);
			int comp = new ListComparator<EntityId>(entityIdComparator).compare(o1List,o2List);
			if (comp != 0){
				return comp;
			}
		}
		
		
		if (o1.getContextRegistrationAttribute() != o2.getContextRegistrationAttribute()) {
			List<ContextRegistrationAttribute> o1List = new ArrayList<ContextRegistrationAttribute>(o1.getContextRegistrationAttribute());
			List<ContextRegistrationAttribute> o2List = new ArrayList<ContextRegistrationAttribute>(o2.getContextRegistrationAttribute());
			ContextRegistrationAttributeComparator contextRegistrationAttributeComparator = new ContextRegistrationAttributeComparator();
			Collections.sort(o1List,contextRegistrationAttributeComparator);
			Collections.sort(o2List,contextRegistrationAttributeComparator);
			int comp = new ListComparator<ContextRegistrationAttribute>(contextRegistrationAttributeComparator).compare(o1List,o2List);
			if (comp != 0){
				return comp;
			}
		}
		
		
		if (o1.getListContextMetadata() != o2.getListContextMetadata()) {
			List<ContextMetadata> o1List = new ArrayList<ContextMetadata>(o1.getListContextMetadata());
			List<ContextMetadata> o2List = new ArrayList<ContextMetadata>(o2.getListContextMetadata());
			ContextMetadataComparator contextMetadataComparator = new ContextMetadataComparator();
			Collections.sort(o1List,contextMetadataComparator);
			Collections.sort(o2List,contextMetadataComparator);
			int comp = new ListComparator<ContextMetadata>(contextMetadataComparator).compare(o1List,o2List);
			if (comp != 0){
				return comp;
			}
		}
		
		
		return 0;
	}
	
}
