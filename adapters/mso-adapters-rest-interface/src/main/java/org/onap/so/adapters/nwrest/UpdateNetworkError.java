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

package org.onap.so.adapters.nwrest;


import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.onap.so.openstack.exceptions.MsoExceptionCategory;

@XmlRootElement(name = "updateNetworkError")
public class UpdateNetworkError extends NetworkExceptionResponse implements Serializable {
    private static final long serialVersionUID = 46820809807914392L;

    public UpdateNetworkError() {
        super("");
    }

    public UpdateNetworkError(String message) {
        super(message);
    }

    public UpdateNetworkError(String message, MsoExceptionCategory category, boolean unused, String messageid) {
        super(message, category, unused, messageid);
    }
}
