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
 * @since 12.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.model.help;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

/**
 * @author oliver
 * 
 */
public class TestComponentContext implements ComponentContext {

	protected Dictionary<String, Object> props = new Hashtable<String, Object>();

	public TestComponentContext() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.component.ComponentContext#getProperties()
	 */
	public TestComponentContext(final Dictionary<String, Object> props) {
		super();
		this.props = props;
	}

	@Override
	public Dictionary<?, ?> getProperties() {
		return props;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.component.ComponentContext#locateService(java.lang.String
	 * )
	 */
	@Override
	public Object locateService(final String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.component.ComponentContext#locateService(java.lang.String
	 * , org.osgi.framework.ServiceReference)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object locateService(final String name, final ServiceReference reference) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.component.ComponentContext#locateServices(java.lang.
	 * String)
	 */
	@Override
	public Object[] locateServices(final String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.component.ComponentContext#getBundleContext()
	 */
	@Override
	public BundleContext getBundleContext() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.component.ComponentContext#getUsingBundle()
	 */
	@Override
	public Bundle getUsingBundle() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.component.ComponentContext#getComponentInstance()
	 */
	@Override
	public ComponentInstance getComponentInstance() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.component.ComponentContext#enableComponent(java.lang
	 * .String)
	 */
	@Override
	public void enableComponent(final String name) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.component.ComponentContext#disableComponent(java.lang
	 * .String)
	 */
	@Override
	public void disableComponent(final String name) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.component.ComponentContext#getServiceReference()
	 */
	@Override
	public ServiceReference<?> getServiceReference() {
		return null;
	}

}
