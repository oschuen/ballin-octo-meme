/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 17.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.server.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;

import pb.model.FavoriteModel;
import pb.persistence.PbStorage;

/**
 * Proxy that requests all required Services for the Servlet instance
 * 
 * @author oliver
 */
@Component(name = "lb.web.gwt.sr", label = "lb.web.gwt.sr", immediate = true)
@References(value = {
		@Reference(name = "PBStorage", bind = "bind", unbind = "unbind", referenceInterface = PbStorage.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC),
		@Reference(name = "FavoriteModel", bind = "bind", unbind = "unbind", referenceInterface = FavoriteModel.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC) })
public class ServiceRequester {

	protected static ServiceBroker broker = new ServiceBroker();

	/**
	 * Bind method for service component runtime to bind a Storage service to
	 * this proxy
	 * 
	 * @param storage
	 *            instance of the storage service
	 */
	protected void bind(final PbStorage storage) {
		Log.info("Requester received PBStorage");
		bind(PbStorage.class, storage);
	}

	/**
	 * Unbind method for service component runtime to unbind a Storage service
	 * from this proxy
	 * 
	 * @param storage
	 *            instance of the storage service
	 */
	protected void unbind(final PbStorage storage) {
		Log.info("Requester lost PBStorage");
		unbind(PbStorage.class, storage);
	}

	/**
	 * Bind method for service component runtime to bind a FavoriteModel service
	 * to this proxy
	 * 
	 * @param FavoriteModel
	 *            instance of the FavoriteModel service
	 */
	protected void bind(final FavoriteModel model) {
		Log.info("Requester received FavoriteModel");
		bind(FavoriteModel.class, model);
	}

	/**
	 * Unbind method for service component runtime to unbind a FavoriteModel
	 * service from this proxy
	 * 
	 * @param storage
	 *            instance of the model service
	 */
	protected void unbind(final FavoriteModel model) {
		Log.info("Requester lost FavoriteModel");
		unbind(FavoriteModel.class, model);
	}

	/**
	 * Delegate method for broker instance. Binds the service to the client of
	 * the broker
	 * 
	 * @param clazz
	 *            service class
	 * @param service
	 *            the service itself
	 * 
	 * @see pb.web.gwt.server.impl.ServiceBroker#bind(java.lang.Class,
	 *      java.lang.Object)
	 */
	protected void bind(final Class<?> clazz, final Object service) {
		broker.bind(clazz, service);
	}

	/**
	 * Delegate method for the broker instance. Unbinds the service from the
	 * client of the broker
	 * 
	 * @param clazz
	 * @param service
	 * @see pb.web.gwt.server.impl.ServiceBroker#unbind(java.lang.Class,
	 *      java.lang.Object)
	 */
	protected void unbind(final Class<?> clazz, final Object service) {
		broker.unbind(clazz, service);
	}

	/**
	 * binds the client to the broker
	 * 
	 * @param client
	 *            client for the broker, that all the services needs
	 * @see pb.web.gwt.server.impl.ServiceBroker#bindClient(java.lang.Object)
	 */
	public static void bindClient(final Object client) {
		broker.bindClient(client);
	}

	/**
	 * unbinds the client from the broker
	 * 
	 * @param client
	 *            that no longer needs any service
	 * @see pb.web.gwt.server.impl.ServiceBroker#unbindClient(java.lang.Object)
	 */
	public static void unbindClient(final Object client) {
		broker.unbindClient(client);
	}
}
