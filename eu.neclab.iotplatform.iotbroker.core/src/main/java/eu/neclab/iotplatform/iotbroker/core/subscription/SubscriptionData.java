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

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElementResponse;

/**
 * Encapsulates data and functionality relevant for a subscription.
 * 
 */
public class SubscriptionData {

	private ThrottlingTask throttlingTask;
	private UnsubscribeTask unsubscribeTask;
	private final Lock lock = new ReentrantLock();
	private Date startTime;

	private List<ContextElementResponse> contextResponseQueue;

	public SubscriptionData(String notificationHandler) {
		this.notificationHandler = notificationHandler;
	}

	/**
	 * This reference normally is the reference defined by the subscription
	 * reference field. But in case of a proxy configuration, e.g. a PEP
	 * component in between, it can be the reference of the middle component
	 */
	private String notificationHandler;

	/**
	 * Carries the information of the originator IP of the subscription request
	 */
	private String originator;

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public void setNotificationHandler(String notificationHandler) {
		this.notificationHandler = notificationHandler;
	}

	public String getNotificationHandler() {
		return notificationHandler;
	}

	/**
	 * @return Since when the subscription has been active.
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Assigns since when the subscription has been active.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return The task for sending notifications.
	 */
	public ThrottlingTask getThrottlingTask() {
		return throttlingTask;
	}

	/**
	 * Assigns the task for sending notifications.
	 */
	public void setThrottlingTask(ThrottlingTask throttlingTask) {
		this.throttlingTask = throttlingTask;
	}

	/**
	 * 
	 * @return The task for unsubscribing.
	 */
	public UnsubscribeTask getUnsubscribeTask() {
		return unsubscribeTask;
	}

	/**
	 * 
	 * Assigns the task for unsubscribing.
	 */
	public void setUnsubscribeTask(UnsubscribeTask unsubscribeTask) {
		this.unsubscribeTask = unsubscribeTask;
	}

	/**
	 * 
	 * @return The queue of yet unsent notifications.
	 */
	public List<ContextElementResponse> getContextResponseQueue() {
		return contextResponseQueue;
	}

	/**
	 * 
	 * Sets the queue of yet unsent notifications.
	 */
	public void setContextResponseQueue(
			List<ContextElementResponse> contextResponseQueue) {
		this.contextResponseQueue = contextResponseQueue;
	}

	/**
	 * @return The synchronization object for this subscription.
	 */
	public Lock getLock() {
		return lock;
	}

	@Override
	public String toString() {
		return "SubscriptionData [throttlingTask=" + throttlingTask
				+ ", unsubscribeTask=" + unsubscribeTask + ", lock=" + lock
				+ ", startTime=" + startTime + ", contextResponseQueue="
				+ contextResponseQueue + "]";
	}

}
