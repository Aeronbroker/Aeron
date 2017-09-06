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

package eu.neclab.iotplatform.ngsi.api.datamodel;

import java.net.URI;
import java.util.ArrayList;

/**
 * Implements converters to be used by implementations of the RESTful binding of
 * NGSI 9/10 by FI-WARE.
 * 
 */
public class Converter {

	/**
	 * Creates a message for UPDATING(!) a number of context attribute values.
	 */
	public static UpdateContextRequest toUpdateContextRequest(UpdateContextElementRequest request, String entityId) {

		// create a single Context Element from the ID
		ContextElement ce = createContextElement(entityId);

		// put the attributes domain name inside
		ce.setAttributeDomainName(request.getAttributeDomainName());

		// put the attribute list from the request inside (pointer copy!)
		ce.setContextAttributeList(request.getContextAttributeList());

		// put the metadata list from the request inside (pointer copy!)
		ce.setDomainMetadata(request.getDomainMetadata());

		// create the return object
		UpdateContextRequest returnMe = new UpdateContextRequest();

		// create its element list
		ArrayList<ContextElement> ellist = new ArrayList<ContextElement>();
		returnMe.setContextElement(ellist);
		// and put the element ce inside
		ellist.add(ce);

		returnMe.setUpdateAction(UpdateActionType.UPDATE);

		return returnMe;

	}

	public static UpdateContextRequest toUpdateContextRequest(UpdateContextAttributeRequest request, String entityID,
			String attributeName, UpdateActionType uat) {

		// create an attribute
		ContextAttribute attr = new ContextAttribute(attributeName, request.getType(),
				request.getContextValue().toString());

		// create a single Context Element from the ID
		ContextElement ce = createContextElement(entityID);

		// put the attribute inside
		ce.setContextAttributeList(new ArrayList<ContextAttribute>());
		ce.getContextAttributeList().add(attr);

		// put the metadata inside (pointer copy of list!)
		ce.setDomainMetadata(request.getContextMetadata());

		// create the return object
		UpdateContextRequest returnMe = new UpdateContextRequest();

		// create its element list
		ArrayList<ContextElement> ellist = new ArrayList<ContextElement>();
		returnMe.setContextElement(ellist);
		// and put the element ce inside
		ellist.add(ce);

		returnMe.setUpdateAction(uat);

		return returnMe;

	}

	public static UpdateContextRequest toUpdateContextRequest(UpdateContextAttributeRequest request, String entityID,
			String attributeName, String valueID, UpdateActionType uat) {

		// create UpdateContextRequest like without ID
		UpdateContextRequest response = toUpdateContextRequest(request, entityID, attributeName, uat);

		// but now create Metadata
		ContextMetadata md = new ContextMetadata();
		md.setName("ID");
		md.setValue(valueID);
		// and add it (hopefully the metadata list is already instanciated)
		response.getContextElement().get(0).getDomainMetadata().add(md);

		return null;
	}

	/**
	 * A convenience method for creating a contextElement
	 */
	private static ContextElement createContextElement(String entityId) {

		// create the Id
		EntityId id = new EntityId();
		id.setId(entityId);
		id.setIsPattern(false);
		id.setType(null);

		// put it into a new ContextElement
		ContextElement el = new ContextElement();
		el.setEntityId(id);

		// return the new ContextElement
		return el;
	}

	/**
	 * Creates a message for APPENDING(!) a number of context attribute values.
	 */
	public static UpdateContextRequest toUpdateContextRequest(AppendContextElementRequest request, String entityId) {

		// create a single Context Element from the ID
		ContextElement ce = createContextElement(entityId);

		// put the attributes domain name inside
		ce.setAttributeDomainName(request.getAttributeDomainName());

		// put the attribute list from the request inside (pointer copy!)
		ce.setContextAttributeList(request.getContextAttributeList());

		// put the metadata list from the request inside (pointer copy!)
		ce.setDomainMetadata(request.getDomainMetadata());

		// create the return object
		UpdateContextRequest returnMe = new UpdateContextRequest();

		// create its element list
		ArrayList<ContextElement> ellist = new ArrayList<ContextElement>();
		returnMe.setContextElement(ellist);
		// and put the element ce inside
		ellist.add(ce);

		returnMe.setUpdateAction(UpdateActionType.APPEND);

		return returnMe;
	}

	/**
	 * Creates a deletion(!) request
	 */
	public static UpdateContextRequest toUpdateContextRequest(String entityId) {

		// create a single Context Element from the ID
		ContextElement ce = createContextElement(entityId);

		// create the return object
		UpdateContextRequest returnMe = new UpdateContextRequest();

		// create its element list
		ArrayList<ContextElement> ellist = new ArrayList<ContextElement>();
		returnMe.setContextElement(ellist);
		// and put the element ce inside
		ellist.add(ce);

		returnMe.setUpdateAction(UpdateActionType.DELETE);

		return returnMe;

	}

	/**
	 * Creates a message for APPENDING(!) a context attribute
	 */
	public static UpdateContextRequest toUpdateContextRequest(AppendContextAttributeRequest request, String entityId,
			String attributeName) {

		// create an contextAttribute
		ContextAttribute ca = createContextAttribute(attributeName);
		// set its value and metadata list
		ca.setContextValue(request.getContextValue());
		ca.setMetadata(request.getMetadata());

		// put ca into a list
		ArrayList<ContextAttribute> cal = new ArrayList<ContextAttribute>();
		cal.add(ca);

		// create a single Context Element from the ID
		ContextElement ce = createContextElement(entityId);

		// put the new attributelist inside
		ce.setContextAttributeList(cal);

		// create the return object
		UpdateContextRequest returnMe = new UpdateContextRequest();

		// create its element list
		ArrayList<ContextElement> ellist = new ArrayList<ContextElement>();
		returnMe.setContextElement(ellist);
		// and put the element ce inside
		ellist.add(ce);

		returnMe.setUpdateAction(UpdateActionType.APPEND);

		return returnMe;

	}

	/**
	 * Create a context attribute from a string (convenience method).
	 */
	private static ContextAttribute createContextAttribute(String attributeName) {

		// Create contextattribute
		ContextAttribute ca = new ContextAttribute();
		ca.setName(attributeName);

		return ca;
	}

	/**
	 * This function assumes that the parameter only relates to a single context
	 * element.
	 */
	public static UpdateContextElementResponse toUpdateContextElementResponse(UpdateContextResponse resp) {

		UpdateContextElementResponse response = new UpdateContextElementResponse();

		// take error code into the response
		response.setErrorCode(resp.getErrorCode());

		// create the new context attribute response
		ContextAttributeResponse ar = new ContextAttributeResponse();
		ar.setContextAttribute(resp.getContextElementResponse().get(0).getContextElement().getContextAttributeList());

		// add the list to the response
		response.setContextAttributeResponse(ar);

		return response;
	}

	/**
	 * This function assumes that the parameter only relates to a single context
	 * element.
	 */
	public static AppendContextElementResponse toAppendContextElementResponse(
			UpdateContextResponse resp) {

		AppendContextElementResponse response = new AppendContextElementResponse();

		// take error code into the response
		response.setErrorCode(resp.getErrorCode());

		ContextAttributeResponse cae = new ContextAttributeResponse(new ArrayList<ContextAttribute>(),null);

		// create the new context attribute response
		for (ContextElementResponse element : resp.getContextElementResponse()) {
			ContextAttributeResponse attrib = new ContextAttributeResponse();
			cae.getContextAttribute().addAll(element.getContextElement().getContextAttributeList());
			cae.setStatusCode(element.getStatusCode());
		}		

		// add the list to the response
		response.setContextAttributeResponse(cae);

		return response;
	}

	/**
	 * Just extracts the status code.
	 */
	public static StatusCode toStatusCode(UpdateContextResponse resp) {
		return resp.getErrorCode();
	}

	/**
	 * Creates a query to an entity/attribute combination
	 */
	public static QueryContextRequest toQueryContextRequest(String id, String attr) {

		// create entity Id
		EntityId eid = new EntityId();
		eid.setId(id);

		// create id list and put id inside
		ArrayList<EntityId> eidl = new ArrayList<EntityId>();
		eidl.add(eid);

		// create attributeList
		ArrayList<String> al = new ArrayList<String>();
		al.add(attr);

		// create request

		QueryContextRequest req = new QueryContextRequest();
		req.setAttributeList(al);
		req.setEntityIdList(eidl);

		return req;

	}

	/**
	 * Operation used only in the case of attributeDomains
	 */
	public static QueryContextRequest toQueryContextRequest_typeBased(URI typeName, String attr, String scopeType,
			String scopeValue) {

		// create entity Id
		EntityId eid = new EntityId();
		eid.setType(typeName);
		eid.setId(".*");
		eid.setIsPattern(true);

		// create id list and put id inside
		ArrayList<EntityId> eidl = new ArrayList<EntityId>();
		eidl.add(eid);

		// create request
		QueryContextRequest req = new QueryContextRequest();
		req.getAttributeList().add(attr);
		req.getEntityIdList().add(eid);

		if (scopeType != null) {

			// create restriction with scope inside
			req.setRestriction(new Restriction());
			req.getRestriction().setOperationScope(new ArrayList<OperationScope>()); // must be initalized!
			req.getRestriction().getOperationScope().add(new OperationScope(scopeType, scopeValue));

		}

		return req;

	}

	/**
	 * Creates a query to an entity/attribute combination of a certain value id.
	 * However, this the value id has to be translated into a metadata restriction,
	 * which is not yet clear how to do.
	 */
	public static QueryContextRequest toQueryContextRequest(String id, String attr, String valueID) {

		// first create request without value id
		QueryContextRequest req = toQueryContextRequest(id, attr);

		// now add the restriction (later)

		// finally return
		return req;
	}

	public static ContextAttributeResponse toContextAttributeResponse(QueryContextResponse regResp) {
		ContextAttributeResponse resp = new ContextAttributeResponse();
		if (regResp.getErrorCode() != null) {

			resp.setStatusCode(regResp.getErrorCode());
			return resp;

		}
		if (regResp.getListContextElementResponse() == null || regResp.getListContextElementResponse().isEmpty()) {
			resp.setStatusCode(new StatusCode(404, "CONTEXT ELEMENT NOT FOUND", null));
			return resp;
		}
		// create response

		resp.setContextAttribute(
				regResp.getListContextElementResponse().get(0).getContextElement().getContextAttributeList());
		resp.setStatusCode(regResp.getListContextElementResponse().get(0).getStatusCode());

		return resp;

	}

}
