/*******************************************************************************
 * Copyright (c) 2016, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * NEC IoT Platform Team - iotplatform@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Gurkan Solmaz - gurkan.solmaz@neclab.eu
 *          * Salvatore Longo
 *          * Raihan Ul-Islam
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the 
 * distribution.
 * 3. All advertising materials mentioning features or use of this 
 * software must display the following acknowledgment: This 
 * product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of NEC nor the names of its contributors may 
 * be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 ******************************************************************************/

package eu.neclab.iotplatform.iotbroker.embeddediotagent.couchdb;

import eu.neclab.iotplatform.iotbroker.embeddediotagent.storage.commons.StorageUtil;

public enum View {

	// @formatter:off
	
	ATTRIBUTE_BY_ID_VIEW(
			"_design/attributesById",
			"function(doc) {" +
				"if (/"+ StorageUtil.LATEST_VALUE_PREFIX +".*/.test(doc._id)) {" +
					"var idAndAttribute = doc._id.split(\\\""+ StorageUtil.PREFIX_TO_ID_SEPARATOR +"\\\")[1].split(\\\""+ StorageUtil.ID_TO_ATTRIBUTENAME_SEPARATOR+"\\\");" +
					"emit(idAndAttribute[0], idAndAttribute[1]);" +
				"}" + 
			"}"),
	
	ID_BY_TYPE_VIEW(
			"_design/idsByType",
			"function(doc) {" +
			    "if (/"+ StorageUtil.LATEST_VALUE_PREFIX +".*/.test(doc._id)) {" +
					"if (doc.entityId){" +
						"if (doc.entityId.type){" +
							"var idAndAttribute = doc._id.split(\\\""+ StorageUtil.PREFIX_TO_ID_SEPARATOR +"\\\")[1];" +
							"emit(doc.entityId.type, idAndAttribute);" +
						"}" +
					"}" +
			    "}" +
			"}");
	
	// @formatter:on

	private String path;
	private String view;

	private View(String path, String view) {
		this.path = path;
		this.view = view;
	}

	public String getPath() {
		return path;
	}

	public String getView() {
		return view;
	}

	public String getReadyToStoreView() {
		return "{\"views\":{\"query\":{\"map\":\"" + this.getView() + "\"}}}";
	}

	public String getQueryPath() {
		return this.getPath() + "/_view/query";

	}

}
