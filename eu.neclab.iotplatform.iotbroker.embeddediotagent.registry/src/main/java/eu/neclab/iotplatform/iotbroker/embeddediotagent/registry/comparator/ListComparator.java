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
import java.util.Iterator;
import java.util.List;

public class ListComparator<T> implements Comparator<List<T>>{

	private Comparator<T> comparator;
	
	public ListComparator(Comparator<T> comparator){
		this.comparator = comparator;
		
	}
	
	/**
	 * Compare two lists. It returns 0 if the two objects are the same list,
	 * or both are null, or both have the same objects. How the elements are stored in the list
	 * matter, hence if you want to check regardless the order of the elements, submit the lists
	 * already sorted.
	 */
	@Override
	public int compare(List<T> o1, List<T> o2) {
		if (o1 != o2){
			if (o1 == null){
				return -1;
			} else if (o2 == null){
				return 1;
			} else {
				
				if (o1.size() != o2.size()){
			}
//				Collections.sort(o1,comparator);
//				Collections.sort(o2,comparator);
				Iterator<T> bigIter;
				Iterator<T> smallIter;
				int factor = 1;
				if (o1.size() > o2.size() ){
					bigIter = o1.iterator();
					smallIter = o2.iterator();
					factor = -1;
				} else {
					smallIter = o1.iterator();
					bigIter = o2.iterator();
				}
				while (smallIter.hasNext()){
					int comp = comparator.compare(smallIter.next(), bigIter.next());
					if (comp != 0){
						return comp*factor;
					}
				}
				if (bigIter.hasNext()){
					return o1.size() - o2.size();
				}
			}
		}
		return 0;
	}

}
