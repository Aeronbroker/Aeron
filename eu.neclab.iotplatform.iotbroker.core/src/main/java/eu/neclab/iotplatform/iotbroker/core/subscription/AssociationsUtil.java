/*******************************************************************************
 * Copyright (c) 2015, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * Salvatore Longo - salvatore.longo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
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
package eu.neclab.iotplatform.iotbroker.core.subscription;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.iotbroker.commons.EntityIDMatcher;
import eu.neclab.iotplatform.iotbroker.commons.XmlFactory;
import eu.neclab.iotplatform.ngsi.api.datamodel.AttributeAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadataAssociation;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.QueryContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.ValueAssociation;
import eu.neclab.iotplatform.ngsi.association.datamodel.AssociationDS;
import eu.neclab.iotplatform.ngsi.association.datamodel.EntityAttribute;

/**
 * Utility for working with NGSI associations. Instances of this class
 * are state-less, i.e. one global instance suffices (unless one wants do
 * do fancy things like using different instances assigned to different
 * loggers).
 *
 */
public class AssociationsUtil {

	private static Logger logger = Logger.getLogger(AssociationsUtil.class);

	private static XmlFactory xmlfactory = new XmlFactory();
	private static final String DIV = "<div>";

	/**
	 *
	 * Converts a list of associations represented by a String into a
	 * {@link List} of association objects.
	 *
	 * @param assoc
	 * @return
	 */
	public List<AssociationDS> convertToAssociationDS(String assoc) {
		List<AssociationDS> lAssoc = new ArrayList<AssociationDS>();
		String[] tmpstring = assoc.split(DIV);
		for (String s : tmpstring) {
			lAssoc.add((AssociationDS) xmlfactory.convertStringToXml(s,
					AssociationDS.class));
		}
		return lAssoc;
	}

	/**
	 * Returns the current time in {@link Date} format.
	 */
	public Date currentTime() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();

	}

	/**
	 * Returns the remaining duration, given an initial duration and the
	 * time elapsed so far.
	 *
	 * @param oldDuration
	 *  The initial duration.
	 * @param timeElapsed
	 *  The time elapsed so far.
	 * @return The remaining duration.
	 */
	public Duration newDuration(Duration oldDuration, long timeElapsed) {
		long ss = oldDuration.getTimeInMillis(new GregorianCalendar());
		logger.debug("Old Duration in millisecond:" + ss);
		long remainingDuration = ss > timeElapsed ? ss - timeElapsed : 0;
		logger.debug("timeElapsed in millisecond:" + timeElapsed);
		logger.debug("remainingDuration in millisecond:" + remainingDuration);
		DatatypeFactory df = null;
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.error("Data Type Wrong!",e);
		}
		Duration duration = df.newDuration(remainingDuration);
		logger.debug("remainingDuration in Duration: " + duration);

		return duration;

	}

	/**
	 * Converts a string representing milliseconds into the {@link Duration} format.
	 *
	 * @param milliSeconds String representing a number of milliseconds.
	 * @return The milliseconds in {@link Duration} format.
	 */
	public Duration convertToDuration(long milliSeconds) {

		DatatypeFactory df = null;
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.error("Data Type Wrong!",e);
		}
		Duration duration = df.newDuration(milliSeconds);
		logger.debug("String " + milliSeconds + " to Duration: " + duration);

		return duration;
	}

	/**
	 * Returns a String representation of an association list.
	 *
	 * @param transitiveList The association list.
	 * @return The list in string format.
	 */
	public String convertAssociationToString(List<AssociationDS> transitiveList) {
		StringBuilder buf = new StringBuilder();
		for (AssociationDS ads : transitiveList) {
			buf.append(xmlfactory.convertToXml(ads, AssociationDS.class));
			buf.append(DIV);
		}

		return buf.toString();
	}


	private int checkAssociationType(AssociationDS aDSfrmOriginal,
			AssociationDS aDSfrmQueue) {

		int count = 0;

		if ("".equals(aDSfrmOriginal.getSourceEA().getEntityAttribute())
				&& "".equals(aDSfrmOriginal.getTargetEA().getEntityAttribute())) {
			if ("".equals(aDSfrmQueue.getSourceEA().getEntityAttribute())
					&& "".equals(aDSfrmQueue.getTargetEA().getEntityAttribute()
							)) {
				count = 1;
			} else if (!"".equals(aDSfrmQueue.getSourceEA().getEntityAttribute())
					&& !"".equals(aDSfrmQueue.getTargetEA().getEntityAttribute()
							)) {
				count = 2;
			}
		} else if (!"".equals(aDSfrmOriginal.getSourceEA().getEntityAttribute())
				&& !"".equals(aDSfrmOriginal.getTargetEA().getEntityAttribute()
						)) {
			if ("".equals(aDSfrmQueue.getSourceEA().getEntityAttribute())
					&& "".equals(aDSfrmQueue.getTargetEA().getEntityAttribute()
							)) {
				count = 3;
			} else if (!"".equals(aDSfrmQueue.getSourceEA().getEntityAttribute()
					)
					&& !"".equals(aDSfrmQueue.getTargetEA().getEntityAttribute()
							)) {
				count = 4;
			}
		}
		return count;
	}

	private AssociationDS createNewAssociations(int count,
			AssociationDS aDSfrmOriginal, AssociationDS aDSfrmQueue) {

		logger.debug("checkAssociationType:" + count + " aDSfrmOriginal:"
				+ aDSfrmOriginal + " aDSfrmQueue:" + aDSfrmQueue);
		AssociationDS newAssociationDS = null;
		if (count == 1) {
			newAssociationDS = new AssociationDS(aDSfrmOriginal.getSourceEA(),
					aDSfrmQueue.getTargetEA());
		} else if (count == 2) {
			newAssociationDS = new AssociationDS(new EntityAttribute(
					aDSfrmOriginal.getSourceEA().getEntity(), aDSfrmQueue
					.getSourceEA().getEntityAttribute()),
					new EntityAttribute(aDSfrmQueue.getTargetEA().getEntity(),
							aDSfrmQueue.getSourceEA().getEntityAttribute()));

		} else if (count == 3) {

			newAssociationDS = new AssociationDS(new EntityAttribute(
					aDSfrmOriginal.getSourceEA().getEntity(), aDSfrmOriginal
					.getSourceEA().getEntityAttribute()),
					new EntityAttribute(aDSfrmQueue.getTargetEA().getEntity(),
							aDSfrmOriginal.getSourceEA().getEntityAttribute()));
		} else if (count == 4) {
			logger.debug("aDSfrmOriginal.getTargetEA().getEntityAttribute():"
					+ aDSfrmOriginal.getTargetEA().getEntityAttribute()
					+ " aDSfrmQueue.getSourceEA().getEntityAttribute():"
					+ aDSfrmQueue.getSourceEA().getEntityAttribute());
			if (aDSfrmOriginal.getTargetEA().getEntityAttribute()
					.equals(aDSfrmQueue.getSourceEA().getEntityAttribute())) {
				newAssociationDS = new AssociationDS(
						aDSfrmOriginal.getSourceEA(), aDSfrmQueue.getTargetEA());
			}
		}
		return newAssociationDS;
	}

	/**
	 * This method enriches the targetAssocs set with additional associations
	 * retrieved from concatenating elements of moreAssociations with elements
	 * already in targetAssocs.
	 * <p>
	 * Concatenating association A --> B with association B-->C means to create
	 * association A --> C.
	 * <p>
	 * This is applied until there is nothing left to add. This means that e.g.
	 * when targetAssocs contains C-->D and moreAssociations contains both A-->B and B-->C, then
	 * the returned association set contains C-->D, B-->D, and A-->D.
	 *
	 */
	public List<AssociationDS> transitiveAssociationAnalysisFrQuery(
			List<AssociationDS> moreAssociations, List<AssociationDS> targetAssocs) {
		logger.debug("Association List frm DiscoverContextAvailabilityResponse:"
				+ moreAssociations);
		logger.debug("Association List frm additionalReqList:"
				+ targetAssocs);
		List<AssociationDS> transitiveAssociationDS = new LinkedList<AssociationDS>();
		// this is gonna be the output

		Queue<AssociationDS> queueAssociationDS = new LinkedList<AssociationDS>();

		transitiveAssociationDS.addAll(targetAssocs);
		queueAssociationDS.addAll(targetAssocs);
		AssociationDS newAssoDs = null;
		while (!queueAssociationDS.isEmpty()) {
			AssociationDS currentAssoc = queueAssociationDS.poll();
			logger.debug("Dqueue:" + currentAssoc);
			for (AssociationDS aDS : moreAssociations) {
				if (EntityIDMatcher.matcher(aDS.getTargetEA().getEntity(),
						currentAssoc.getSourceEA().getEntity())) {
					newAssoDs = createNewAssociations(
							checkAssociationType(aDS, currentAssoc), aDS,
							currentAssoc);
					logger.debug("newAssoDs:" + newAssoDs);
					if (newAssoDs != null) {
						if (!queueAssociationDS.contains(newAssoDs)) {
							queueAssociationDS.add(newAssoDs);
							logger.debug("Adding queueAssociationDS:"
									+ queueAssociationDS);
						}
						if (!transitiveAssociationDS.contains(newAssoDs)) {
							transitiveAssociationDS.add(newAssoDs);
							logger.debug("Adding transitiveAssociationDS:"
									+ queueAssociationDS);
						}
					}
				}
			}

		}

		return transitiveAssociationDS;
	}

	/**
	 *
	 * This method extracts from the associations list all associations
	 * whose target match with the given query context request.
	 *
	 * @param qcReq
	 * The query context request to match with
	 * @param assocList
	 * The list of associations from which to extract.
	 */
	public List<AssociationDS> initialLstOfmatchedAssociation(
			QueryContextRequest qcReq, List<AssociationDS> assocList) {


		/*
		 * Initialize the list of associations that will be returned
		 */
		List<AssociationDS> additionalReqList = new LinkedList<AssociationDS>();

		/*
		 * For each entity in the query context request, run through all
		 * associations. For each pair of entity and association, the steps
		 * described below are performed:
		 */

		List<EntityId> entityList = qcReq.getEntityIdList();

		logger.debug("Entity ID list SIZE:"+ entityList.size());

		for (EntityId eID : entityList) {

			logger.debug("EntityId: "+ eID);

			logger.debug("Association List Size: "+ assocList.size());

			for (AssociationDS aDS : assocList) {

				logger.debug("Association Target Id: "+ aDS.getTargetEA().getEntityID());
				/*
				 * First check whether the target entity of the association matches
				 * with the entity id. If this is not the case, then nothing is done
				 * for this pair
				 */

				if (EntityIDMatcher.matcher(aDS.getTargetEA().getEntity(), eID)) {

					logger.debug("Association Target Id matched Entity Id: "+ aDS.getTargetEA().getEntityID() + " = "+ eID.getId());

					/*
					 * Now check whether the association is an attribute association.
					 */

					if ("".equals(aDS.getSourceEA().getEntityAttribute())) {

						/*
						 * If the association is no attribute association, then check
						 * whether in the query context request there is an attribute
						 * specified.
						 */

						if (qcReq.getAttributeList().size() > 0) {


							/*
							 * If attributes are specified in the query request, then the
							 * entity association is converted to a set of attribute associations.
							 *
							 * For each of the attributes in the query, an attribute association
							 * is created where this attribute is both in source and target. These
							 * associations are put into the list of associations to return.
							 */

							for (int i = 0; i < qcReq.getAttributeList().size(); i++) {
								if (aDS.getTargetEA().getEntityAttribute()
										.equals("")) {
									// check not necessary
									// because
									// sourceattribute has
									// already been checked
									// above.
									additionalReqList.add(new AssociationDS(
											new EntityAttribute(aDS
													.getSourceEA().getEntity(),
													qcReq.getAttributeList()
													.get(i)),
													new EntityAttribute(aDS
															.getTargetEA().getEntity(),
															qcReq.getAttributeList()
															.get(i))));
								}
							}
						} else {

							/*
							 * Otherwise, if the association is an entity association and
							 * there is no attribute specified in the query, then the association
							 * is put into the set of associations to return just like it is,
							 */
							additionalReqList.add(new AssociationDS(aDS
									.getSourceEA(), new EntityAttribute(aDS
											.getTargetEA().getEntity(), "")));
						}

					} else if (!"".equals(aDS.getSourceEA().getEntityAttribute()
							)) { // note: "just 'else' is sufficient
						if (qcReq.getAttributeList().size() > 0) {

							/*
							 * We are now at the case that the association is an
							 * attribute association and the query specifies
							 * attributes. What we do here is to search for an
							 * attribute in the query that matches with the
							 * target attribute of the association.
							 * When found, we add the association to the list
							 * of associations to return.
							 */

							for (int i = 0; i < qcReq.getAttributeList().size(); i++) {

								if (aDS.getTargetEA()
										.getEntityAttribute()
										.equals(qcReq.getAttributeList().get(i))) {
									additionalReqList.add(new AssociationDS(aDS
											.getSourceEA(),
											new EntityAttribute(aDS
													.getTargetEA().getEntity(),
													qcReq.getAttributeList()
													.get(i))));
								}
							}
						} else {
							/*
							 * The final case is here: The association is an attribute
							 * association and the query specifies no attribute. In this
							 * case we simply add the association to the list of associations
							 * to return.
							 */
							additionalReqList.add(new AssociationDS(aDS
									.getSourceEA(), aDS.getTargetEA()));
						}
					}
				}
			}
		}

		return additionalReqList;
	}

	/**
	 * Constructs a {@link DiscoverContextAvailabilityResponse} that contains all sources that
	 * have to be queried in order to apply the given associations.
	 *
	 * @param dcaRes
	 * 	The given {@link DiscoverContextAvailabilityResponse} from which the sources are taken.
	 * @param lassociDS
	 * The list of associations.
	 * @return
	 * The constructed DiscoverContextAvailabilityResponse.
	 */
	public DiscoverContextAvailabilityResponse validDiscoverContextAvailabiltyResponse(
			DiscoverContextAvailabilityResponse dcaRes,
			List<AssociationDS> lassociDS) {


		List<ContextRegistrationResponse> lcrr = dcaRes
				.getContextRegistrationResponse();


		List<ContextRegistrationResponse> validlcrr = new LinkedList<ContextRegistrationResponse>();
		DiscoverContextAvailabilityResponse vDCARes = new DiscoverContextAvailabilityResponse();
		if (!lassociDS.isEmpty()) {
			for (AssociationDS aDS : lassociDS) {
				for (ContextRegistrationResponse crr : lcrr) {
					URI uri = crr.getContextRegistration()
							.getProvidingApplication();
					if (uri != null && !"".equals(uri.toString()) && crr.getContextRegistration().getListEntityId() != null) {
						List<EntityId> leid = crr.getContextRegistration()
								.getListEntityId();
						for (EntityId eid : leid) {
							if (EntityIDMatcher.matcher(eid, aDS
									.getSourceEA().getEntity())) {
								List<ContextRegistrationAttribute> lcra = crr
										.getContextRegistration()
										.getContextRegistrationAttribute();
								for (ContextRegistrationAttribute cra : lcra) {
									if ("".equals(aDS.getSourceEA()
											.getEntityAttribute()
											)) {
										if (aDS.getTargetEA()
												.getEntityAttribute()
												.equals(cra.getName())
												|| "".equals(aDS.getTargetEA()
														.getEntityAttribute()
														)) {
											validlcrr.add(crr);
										}
									} else if (aDS.getSourceEA()
											.getEntityAttribute()
											.equals(cra.getName())) {
										validlcrr.add(crr);
									}


									break;
								}
							}
						}
					}
				}
			}
			vDCARes.setContextRegistrationResponse(validlcrr);
		} else {
			vDCARes.setContextRegistrationResponse(dcaRes
					.getContextRegistrationResponse());
		}

		return vDCARes;
	}

	/*
	 * This method retrieves list of associations from
	 * DiscoverContextAvailabilityResponse
	 */
	public List<AssociationDS> retrieveAssociation(
			DiscoverContextAvailabilityResponse dcaRes) {
		List<AssociationDS> lADS = new LinkedList<AssociationDS>();
		List<ContextRegistrationResponse> lcrr = dcaRes
				.getContextRegistrationResponse();

		Iterator<ContextRegistrationResponse> itContextRegistrationResponse = lcrr
				.iterator();

		logger.debug("N. Context Registrations inside the DiscoverContextAvailability Response = " + lcrr.size());

		while (itContextRegistrationResponse.hasNext()) {
			ContextRegistrationResponse crr = itContextRegistrationResponse
					.next();

			List<ContextMetadata> lcmd = crr.getContextRegistration()
					.getListContextMetadata();

			Iterator<ContextMetadata> it1 = lcmd.iterator();
			while (it1.hasNext()) {
				ContextMetadata cmd = it1.next();

				logger.debug("Context Metadata ="+ cmd);
				logger.debug("Context Metadata Name ="+ cmd.getName());

				if ("Association".equals(cmd.getName().toString())) {

					String s = "<value>" + cmd.getValue() + "</value>";
					ContextMetadataAssociation cma = (ContextMetadataAssociation) xmlfactory
							.convertStringToXml(cmd.toString(),
									ContextMetadataAssociation.class);
					XmlFactory xmlFac = new XmlFactory();
					ValueAssociation va = (ValueAssociation) xmlFac
							.convertStringToXml(s, ValueAssociation.class);
					cma.setValue(va);

					logger.debug("Association Target Id ----------------> "+va.getTargetEntity());

					if (va.getAttributeAssociation().isEmpty()) {

						AssociationDS ads = new AssociationDS(
								new EntityAttribute(va.getSourceEntity(), ""),
								new EntityAttribute(va.getTargetEntity(), ""));
						lADS.add(ads);
					} else {
						List<AttributeAssociation> lAttributeAsociations = va
								.getAttributeAssociation();
						for (AttributeAssociation aa : lAttributeAsociations) {

							AssociationDS ads = new AssociationDS(
									new EntityAttribute(va.getSourceEntity(),
											aa.getSourceAttribute()),
											new EntityAttribute(va.getTargetEntity(),
													aa.getTargetAttribute()));
							lADS.add(ads);
						}
					}
				}

			}
		}
		return lADS;

	}

	/**
	 *
	 * @param lads
	 * @param ea
	 * @return
	 */
	public List<EntityAttribute> findA(List<AssociationDS> lads,
			EntityAttribute ea) {
		logger.debug("List of associations");
		for (AssociationDS a : lads) {
			logger.debug(a.toString());
		}
		logger.debug("Source Entity:" + ea.toString());
		LinkedList<EntityAttribute> outputlea = new LinkedList<EntityAttribute>();
		Queue<EntityAttribute> quequeEntityAttribute = new LinkedList<EntityAttribute>();
		outputlea.add(ea);
		quequeEntityAttribute.add(ea);
		while (!quequeEntityAttribute.isEmpty()) {
			EntityAttribute tEa = quequeEntityAttribute.poll();
			logger.debug("Dequeue:" + tEa.toString());
			LinkedList<AssociationDS> tmpLea = new LinkedList<AssociationDS>();
			Iterator<AssociationDS> it = lads.iterator();
			while (it.hasNext()) {
				AssociationDS ads = it.next();

				if (EntityIDMatcher.matcher(ads.getSourceEA().getEntity(),
						tEa.getEntity())) {
					tmpLea.add(ads);
					logger.debug("Adding into temp:" + ads.toString());
				}
			}
			for (AssociationDS a : tmpLea) {
				logger.debug("Getting from the templist:" + a.toString());

				if (a.getSourceEA().getEntityAttribute()
						.equals(tEa.getEntityAttribute())) {
					EntityAttribute f = new EntityAttribute(a.getTargetEA()
							.getEntity(), a.getTargetEA().getEntityAttribute());

					if (!outputlea.contains(f)) {
						logger.debug("Adding in final queue:" + f.toString());
						quequeEntityAttribute.add(f);
						outputlea.add(f);
					}
				}
				if ("".equals(a.getSourceEA().getEntityAttribute())) {
					EntityAttribute f = new EntityAttribute(a.getTargetEA()
							.getEntity(), "".equals(a.getTargetEA().getEntityAttribute())
							? tEa.getEntityAttribute() : a
									.getSourceEA().getEntityAttribute());

					if (!outputlea.contains(f)) {
						logger.debug("Adding in final queue:" + f.toString());
						quequeEntityAttribute.add(f);
						outputlea.add(f);
					}
				}
			}
		}
		Collections.sort(outputlea, new Comparator<EntityAttribute>() {
			@Override
			public int compare(EntityAttribute e1, EntityAttribute e2) {

				return e1.getEntity().getId().compareTo(e2.getEntity().getId());
			}
		});
		return outputlea;
	}
}
