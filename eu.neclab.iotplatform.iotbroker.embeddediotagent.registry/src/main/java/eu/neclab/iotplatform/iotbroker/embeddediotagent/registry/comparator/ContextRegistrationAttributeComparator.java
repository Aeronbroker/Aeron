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
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;

public class ContextRegistrationAttributeComparator implements
		Comparator<ContextRegistrationAttribute> {

	@Override
	public int compare(ContextRegistrationAttribute o1,
			ContextRegistrationAttribute o2) {

		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		if (o1.getIsDomain() != o2.getIsDomain()) {
			if (!o1.getIsDomain()) {
				return -1;
			} else {
				return 1;
			}
		}

		if (o1.getName() != o2.getName()) {
			if (o1.getName() == null) {
				return -1;
			} else if (o2.getName() == null) {
				return 1;
			} else if (!o1.getName().equals(o2.getName())) {
				return o1.getName().compareTo(o2.getName());
			}
		}

		if (o1.getType() != o2.getType()) {
			if (o1.getType() == null) {
				return -1;
			} else if (o2.getType() == null) {
				return 1;
			} else if (!o1.getType().equals(o2.getType())) {
				return o1.getType().compareTo(o2.getType());
			}
		}

		// List<ContextMetadata> o1ContMetaList = o1.getMetaData();
		// List<ContextMetadata> o2ConteMetaList = o2.getMetaData();
		// if (o1.getMetaData() != o2.getMetaData()){
		// if (o1.getMetaData() == null){
		// return -1;
		// } else if (o2.getMetaData() == null){
		// return 1;
		// } else if (o1.getMetaData().size() != o2.getMetaData().size()){
		// ContextMetadataComparator contextMetadataComparator = new
		// ContextMetadataComparator();
		// Collections.sort(o1.getMetaData(),contextMetadataComparator);
		// Collections.sort(o2.getMetaData(),contextMetadataComparator);
		// Iterator<ContextMetadata> iter1 = o1.getMetaData().iterator();
		// Iterator<ContextMetadata> iter2 = o2.getMetaData().iterator();
		// while (iter1.hasNext()){
		// int comp = contextMetadataComparator.compare(iter1.next(),
		// iter2.next());
		// if (comp != 0){
		// return comp;
		// }
		// }
		// }
		// }

		// if (o1.getMetaData() != o2.getMetaData()){
		// if (o1.getMetaData() == null){
		// return -1;
		// } else if (o2.getMetaData() == null){
		// return 1;
		// } else {
		//
		// if (o1.getMetaData().size() != o2.getMetaData().size()){
		// }
		// ContextMetadataComparator contextMetadataComparator = new
		// ContextMetadataComparator();
		// Collections.sort(o1.getMetaData(),contextMetadataComparator);
		// Collections.sort(o2.getMetaData(),contextMetadataComparator);
		// Iterator<ContextMetadata> bigIter;
		// Iterator<ContextMetadata> smallIter;
		// if (o1.getMetaData().size() > o2.getMetaData().size() ){
		// bigIter = o1.getMetaData().iterator();
		// smallIter = o2.getMetaData().iterator();
		// } else {
		// smallIter = o1.getMetaData().iterator();
		// bigIter = o2.getMetaData().iterator();
		// }
		// while (smallIter.hasNext()){
		// int comp = contextMetadataComparator.compare(smallIter.next(),
		// bigIter.next());
		// if (comp != 0){
		// return comp;
		// }
		// }
		// if (bigIter.hasNext()){
		// return o1.getMetaData().size() - o2.getMetaData().size();
		// }
		// }
		// }

		if (o1.getMetadata() != o2.getMetadata()) {
			List<ContextMetadata> o1List = new ArrayList<ContextMetadata>(o1.getMetadata());
			List<ContextMetadata> o2List = new ArrayList<ContextMetadata>(o2.getMetadata());
			ContextMetadataComparator contextMetadataComparator = new ContextMetadataComparator();
			Collections.sort(o1List,contextMetadataComparator);
			Collections.sort(o2List,contextMetadataComparator);
			int comp = new ListComparator<ContextMetadata>(contextMetadataComparator).compare(o1List,o2List);
			if (comp != 0){
				return comp;
			}
		}

		return 0;

		// if (!arg0.getIsDomain() && arg0.getIsDomain()){
		// return -1;
		// } else if(arg0.getIsDomain() && !arg0.getIsDomain()){
		// return 1;
		// } else {
		// if (arg0.getName() == null && arg1.getName() != null){
		// return -1;
		// } else if (arg0.getName() != null && arg1.getName() == null){
		// return 1;
		// } else if (arg0.getName() == null && arg1.getName() == null ||
		// arg0.getName().compareTo(arg1.getName()) == 0){
		// if(arg0.getType() == null && arg1.getType() != null){
		// return -1;
		// } else if (arg0.getType() != null && arg1.getType() == null){
		// return 1;
		// } else if (arg0.getType() == null && arg1.getType() == null ||
		// arg0.getType().compareTo(arg1.getType()) == 0){
		// if (arg0.get)
		// } else {
		// return arg0.getType().compareTo(arg1.getType());
		// }
		// } else {
		// return arg0.getName().compareTo(arg1.getName());
		// }
		// }
	}
}
