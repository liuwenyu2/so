/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.mso.bpmn.common.adapter.vnf;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "vnfAdapterNotify", targetNamespace = "http://org.openecomp.mso/vnfNotify", wsdlLocation = "/VnfAdapterNotify.wsdl")
public class VnfAdapterNotify_Service extends Service
{

    private final static URL VNFADAPTERNOTIFY_WSDL_LOCATION;
    private final static WebServiceException VNFADAPTERNOTIFY_EXCEPTION;
    private final static QName VNFADAPTERNOTIFY_QNAME = new QName("http://org.openecomp.mso/vnfNotify", "vnfAdapterNotify");

    static {
        VNFADAPTERNOTIFY_WSDL_LOCATION = org.openecomp.mso.bpmn.common.adapter.vnf.VnfAdapterNotify_Service.class.getResource("/VnfAdapterNotify.wsdl");
        WebServiceException e = null;
        if (VNFADAPTERNOTIFY_WSDL_LOCATION == null) {
            e = new WebServiceException("Cannot find '/VnfAdapterNotify.wsdl' wsdl. Place the resource correctly in the classpath.");
        }
        VNFADAPTERNOTIFY_EXCEPTION = e;
    }

    public VnfAdapterNotify_Service() {
        super(__getWsdlLocation(), VNFADAPTERNOTIFY_QNAME);
    }

    public VnfAdapterNotify_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), VNFADAPTERNOTIFY_QNAME, features);
    }

    public VnfAdapterNotify_Service(URL wsdlLocation) {
        super(wsdlLocation, VNFADAPTERNOTIFY_QNAME);
    }

    public VnfAdapterNotify_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, VNFADAPTERNOTIFY_QNAME, features);
    }

    public VnfAdapterNotify_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public VnfAdapterNotify_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns VnfAdapterNotify
     */
    @WebEndpoint(name = "MsoVnfAdapterAsyncImplPort")
    public VnfAdapterNotify getMsoVnfAdapterAsyncImplPort() {
        return super.getPort(new QName("http://org.openecomp.mso/vnfNotify", "MsoVnfAdapterAsyncImplPort"), VnfAdapterNotify.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns VnfAdapterNotify
     */
    @WebEndpoint(name = "MsoVnfAdapterAsyncImplPort")
    public VnfAdapterNotify getMsoVnfAdapterAsyncImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://org.openecomp.mso/vnfNotify", "MsoVnfAdapterAsyncImplPort"), VnfAdapterNotify.class, features);
    }

    private static URL __getWsdlLocation() {
        if (VNFADAPTERNOTIFY_EXCEPTION!= null) {
            throw VNFADAPTERNOTIFY_EXCEPTION;
        }
        return VNFADAPTERNOTIFY_WSDL_LOCATION;
    }

}
