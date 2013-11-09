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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Broker is used to delegate the bind calls for services from a Proxy to
 * the real client object.
 * 
 * @author oliver
 */
public class ServiceBroker {

	protected Map<Class<?>, List<Object>> serviceMap = new HashMap<Class<?>, List<Object>>();

	protected Object client = null;

	/**
	 * Binds the service object to the client, by using the method of name
	 * "bind" and Parameter of type clazz
	 * 
	 * @param clazz
	 *            Class of the parameter of the bind method
	 * @param service
	 */
	public void bind(final Class<?> clazz, final Object service) {
		if (service != null) {
			try {
				synchronized (serviceMap) {
					if (client != null) {
						final Method method = client.getClass().getMethod("bind", clazz);
						if (method == null) {
							Log.error("Service has no method of the given type");
						} else {
							method.invoke(client, service);
						}
					}
					List<Object> services = serviceMap.get(clazz);
					if (services == null) {
						services = new ArrayList<Object>();
						serviceMap.put(clazz, services);
					}
					services.add(service);
				}
			} catch (final Exception e) {
				Log.error("Error occured finding bind method", e);
			}
		}
	}

	/**
	 * Unbinds the service object from the client, by using the method of name
	 * "unbind" and Parameter of type clazz
	 * 
	 * @param clazz
	 *            Class of the parameter of the bind method
	 * @param service
	 */
	public void unbind(final Class<?> clazz, final Object service) {
		if (service != null) {
			try {
				synchronized (serviceMap) {
					final List<Object> services = serviceMap.get(clazz);
					if (client != null && services != null && services.remove(service)) {
						final Method method = client.getClass().getMethod("unbind", clazz);
						if (method == null) {
							Log.error("Service has no method of the given type");
						} else {
							method.invoke(client, service);
						}
					}
				}
			} catch (final Exception e) {
				Log.error("Error occured finding bind method", e);
			}
		}
	}

	/**
	 * binds the client to this broker. If any service was added to this broker
	 * before this method is called it will be bound to the client within this
	 * method call
	 * 
	 * @param client
	 *            client for the broker
	 */
	public void bindClient(final Object client) {
		if (client != null) {
			synchronized (serviceMap) {
				for (final Class<?> clazz : serviceMap.keySet()) {
					try {
						final Method method = client.getClass().getMethod("bind", clazz);
						if (method == null) {
							Log.error("Service has no method of the given type");
						} else {
							final List<Object> services = serviceMap.get(clazz);
							for (final Object service : services) {
								method.invoke(client, service);
							}
						}
					} catch (final Exception e) {
						Log.error("Error occured finding bind method", e);
					}
				}
			}
			this.client = client;
		}
	}

	/**
	 * unbinds the client from this broker. if there are any services that
	 * weren't unbound from the client before this call, they will be unbound
	 * while this function call.
	 * 
	 * @param client
	 *            client for the broker
	 */
	public void unbindClient(final Object client) {
		if (this.client.equals(client)) {
			synchronized (serviceMap) {
				for (final Class<?> clazz : serviceMap.keySet()) {
					try {
						final Method method = client.getClass().getMethod("unbind", clazz);
						if (method == null) {
							Log.error(
									"Service has no method unbind with the given parameter type %s",
									clazz.getName());
						} else {
							final List<Object> services = serviceMap.get(clazz);
							for (final Object service : services) {
								method.invoke(client, service);
							}
						}
					} catch (final Exception e) {
						Log.error("Error occured finding bind method", e);
					}
				}
			}
			this.client = null;
		}
	}
}
