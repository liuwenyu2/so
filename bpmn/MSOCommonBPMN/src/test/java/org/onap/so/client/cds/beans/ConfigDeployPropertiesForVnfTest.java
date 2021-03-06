/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2019 TechMahindra
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

package org.onap.so.client.cds.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ConfigDeployPropertiesForVnfTest {
    ConfigDeployPropertiesForVnf configDeployPropertiesForVnf = new ConfigDeployPropertiesForVnf();
    private String serviceInstanceId;
    private String vnfId;
    private String vnfName;
    private String serviceModelUuid;
    private String vnfCustomizationUuid;

    @Test
    public final void testConfigDeployPropertiesForVnf() {
        configDeployPropertiesForVnf.setServiceInstanceId("service-instance-id");
        configDeployPropertiesForVnf.setServiceModelUuid("service-model-uuid");
        configDeployPropertiesForVnf.setVnfCustomizationUuid("vnf-customization-uuid");
        configDeployPropertiesForVnf.setVnfId("vnf-id");
        configDeployPropertiesForVnf.setVnfName("vnf-name");
        assertNotNull(configDeployPropertiesForVnf.getServiceInstanceId());
        assertNotNull(configDeployPropertiesForVnf.getServiceModelUuid());
        assertNotNull(configDeployPropertiesForVnf.getVnfCustomizationUuid());
        assertNotNull(configDeployPropertiesForVnf.getVnfId());
        assertNotNull(configDeployPropertiesForVnf.getVnfName());

        assertEquals("service-instance-id", configDeployPropertiesForVnf.getServiceInstanceId());
        assertEquals("service-model-uuid", configDeployPropertiesForVnf.getServiceModelUuid());
        assertEquals("vnf-customization-uuid", configDeployPropertiesForVnf.getVnfCustomizationUuid());
        assertEquals("vnf-id", configDeployPropertiesForVnf.getVnfId());
        assertEquals("vnf-name", configDeployPropertiesForVnf.getVnfName());
    }

    @Test
    public void testtoString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"service-instance-id\":").append("\"").append(serviceInstanceId).append("\"");
        sb.append(", \"vnf-id\":").append("\"").append(vnfId).append("\"");
        sb.append(", \"vnf-name\":").append("\"").append(vnfName).append("\"");
        sb.append(", \"service-model-uuid\":").append("\"").append(serviceModelUuid).append("\"");
        sb.append(", \"vnf-customization-uuid\":").append("\"").append(vnfCustomizationUuid).append("\"");
        sb.append('}');
        String Expexted = sb.toString();
        assertEquals(Expexted, configDeployPropertiesForVnf.toString());

    }
}
