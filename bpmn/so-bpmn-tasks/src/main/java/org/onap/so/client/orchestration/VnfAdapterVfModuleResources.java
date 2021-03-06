/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 Samsung
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

package org.onap.so.client.orchestration;

import java.io.IOException;
import org.onap.so.adapters.vnfrest.CreateVfModuleRequest;
import org.onap.so.adapters.vnfrest.DeleteVfModuleRequest;
import org.onap.so.bpmn.servicedecomposition.bbobjects.CloudRegion;
import org.onap.so.bpmn.servicedecomposition.bbobjects.GenericVnf;
import org.onap.so.bpmn.servicedecomposition.bbobjects.ServiceInstance;
import org.onap.so.bpmn.servicedecomposition.bbobjects.VfModule;
import org.onap.so.bpmn.servicedecomposition.bbobjects.VolumeGroup;
import org.onap.so.bpmn.servicedecomposition.generalobjects.OrchestrationContext;
import org.onap.so.bpmn.servicedecomposition.generalobjects.RequestContext;
import org.onap.so.client.adapter.vnf.mapper.VnfAdapterVfModuleObjectMapper;
import org.onap.so.client.adapter.vnf.mapper.exceptions.MissingValueTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is used for creating and deleting the request for VfModule.
 *
 */
@Component
public class VnfAdapterVfModuleResources {

    @Autowired
    private VnfAdapterVfModuleObjectMapper vnfAdapterVfModuleObjectMapper;

    /**
     * This method is used for creating the request for the VfModule.
     *
     * This method take these parameter and call the VnfAdapterVfModuleObjectMapper to create the request.
     *
     * @param requestContext
     * @param cloudRegion
     * @param orchestrationContext
     * @param serviceInstance
     * @param genericVnf
     * @param vfModule
     * @param volumeGroup
     * @param sdncVnfQueryResponse
     * @param sdncVfModuleQueryResponse
     * @throws IOException & MissingValueTagException
     * @return
     */
    public CreateVfModuleRequest createVfModuleRequest(RequestContext requestContext, CloudRegion cloudRegion,
            OrchestrationContext orchestrationContext, ServiceInstance serviceInstance, GenericVnf genericVnf,
            VfModule vfModule, VolumeGroup volumeGroup, String sdncVnfQueryResponse, String sdncVfModuleQueryResponse)
            throws IOException, MissingValueTagException {
        return vnfAdapterVfModuleObjectMapper.createVfModuleRequestMapper(requestContext, cloudRegion,
                orchestrationContext, serviceInstance, genericVnf, vfModule, volumeGroup, sdncVnfQueryResponse,
                sdncVfModuleQueryResponse);
    }

    /**
     * This method is used for delete the request for the VfModule.
     *
     * This method take these parameter and call the VnfAdapterVfModuleObjectMapper to delete the request.
     *
     * @param requestContext
     * @param cloudRegion
     * @param serviceInstance
     * @param genericVnf
     * @param vfModule
     * @throws IOException
     * @return
     */
    public DeleteVfModuleRequest deleteVfModuleRequest(RequestContext requestContext, CloudRegion cloudRegion,
            ServiceInstance serviceInstance, GenericVnf genericVnf, VfModule vfModule) throws IOException {
        return vnfAdapterVfModuleObjectMapper.deleteVfModuleRequestMapper(requestContext, cloudRegion, serviceInstance,
                genericVnf, vfModule);
    }
}
