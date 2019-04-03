/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.so.adapters.vnfmadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.onap.so.client.RestTemplateConfig.CONFIGURABLE_REST_TEMPLATE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import com.google.gson.Gson;
import java.net.URI;
import java.util.Optional;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.core.StringStartsWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.hamcrest.MockitoHamcrest;
import org.onap.aai.domain.yang.EsrSystemInfo;
import org.onap.aai.domain.yang.EsrSystemInfoList;
import org.onap.aai.domain.yang.EsrVnfm;
import org.onap.aai.domain.yang.EsrVnfmList;
import org.onap.aai.domain.yang.GenericVnf;
import org.onap.aai.domain.yang.Relationship;
import org.onap.aai.domain.yang.RelationshipData;
import org.onap.aai.domain.yang.RelationshipList;
import org.onap.so.adapters.vnfmadapter.VnfmAdapterApplication;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse201;
import org.onap.so.adapters.vnfmadapter.rest.exceptions.VnfmNotFoundException;
import org.onap.so.client.aai.AAIResourcesClient;
import org.onap.so.client.aai.entities.uri.AAIResourceUri;
import org.onap.vnfmadapter.v1.model.CreateVnfRequest;
import org.onap.vnfmadapter.v1.model.CreateVnfResponse;
import org.onap.vnfmadapter.v1.model.DeleteVnfResponse;
import org.onap.vnfmadapter.v1.model.OperationEnum;
import org.onap.vnfmadapter.v1.model.OperationStateEnum;
import org.onap.vnfmadapter.v1.model.QueryJobResponse;
import org.onap.vnfmadapter.v1.model.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = VnfmAdapterApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

public class VnfmAdapterControllerTest {

    private static final OffsetDateTime JAN_1_2019_12_00 =
            OffsetDateTime.of(LocalDateTime.of(2019, 1, 1, 12, 0), ZoneOffset.UTC);
    private static final OffsetDateTime JAN_1_2019_1_00 =
            OffsetDateTime.of(LocalDateTime.of(2019, 1, 1, 1, 0), ZoneOffset.UTC);

    @LocalServerPort
    private int port;
    @Autowired
    @Qualifier(CONFIGURABLE_REST_TEMPLATE)
    private RestTemplate testRestTemplate;
    private MockRestServiceServer mockRestServer;

    @MockBean
    AAIResourcesClient aaiResourcesClient;

    @Autowired
    VnfmAdapterController controller;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        mockRestServer = MockRestServiceServer.bindTo(testRestTemplate).build();
    }

    @Test
    public void createVnf_ValidRequest_Returns202AndJobId() throws Exception {
        final Tenant tenant =
                new Tenant().cloudOwner("myTestCloudOwner").regionName("myTestRegion").tenantId("myTestTenantId");
        final CreateVnfRequest createVnfRequest = new CreateVnfRequest().name("myTestName").tenant(tenant);

        final GenericVnf genericVnf = new GenericVnf();
        genericVnf.setVnfId("myTestVnfId");
        genericVnf.setNfType("vnfmType2");

        doReturn(Optional.of(genericVnf)).when(aaiResourcesClient).get(eq(GenericVnf.class), MockitoHamcrest
                .argThat(new AaiResourceUriMatcher("/network/generic-vnfs/generic-vnf/myTestVnfId?depth=1")));

        final EsrSystemInfo esrSystemInfo1 = new EsrSystemInfo();
        esrSystemInfo1.setServiceUrl("http://vnfm1:8080");
        esrSystemInfo1.setType("vnfmType1");
        esrSystemInfo1.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList1 = new EsrSystemInfoList();
        esrSystemInfoList1.getEsrSystemInfo().add(esrSystemInfo1);

        final EsrVnfm esrVnfm1 = new EsrVnfm();
        esrVnfm1.setVnfmId("vnfm1");
        esrVnfm1.setEsrSystemInfoList(esrSystemInfoList1);
        esrVnfm1.setResourceVersion("1234");

        final EsrSystemInfo esrSystemInfo2 = new EsrSystemInfo();
        esrSystemInfo2.setServiceUrl("http://vnfm2:8080");
        esrSystemInfo2.setType("vnfmType2");
        esrSystemInfo2.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList2 = new EsrSystemInfoList();
        esrSystemInfoList2.getEsrSystemInfo().add(esrSystemInfo2);

        final EsrVnfm esrVnfm2 = new EsrVnfm();
        esrVnfm2.setVnfmId("vnfm2");
        esrVnfm2.setEsrSystemInfoList(esrSystemInfoList2);
        esrVnfm2.setResourceVersion("1234");

        final EsrVnfmList esrVnfmList = new EsrVnfmList();
        esrVnfmList.getEsrVnfm().add(esrVnfm1);
        esrVnfmList.getEsrVnfm().add(esrVnfm2);

        doReturn(Optional.of(esrVnfmList)).when(aaiResourcesClient).get(eq(EsrVnfmList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher("/external-system/esr-vnfm-list")));

        doReturn(Optional.of(esrSystemInfoList1)).when(aaiResourcesClient).get(eq(EsrSystemInfoList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher(
                        "/external-system/esr-vnfm-list/esr-vnfm/vnfm1/esr-system-info-list")));
        doReturn(Optional.of(esrSystemInfoList2)).when(aaiResourcesClient).get(eq(EsrSystemInfoList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher(
                        "/external-system/esr-vnfm-list/esr-vnfm/vnfm2/esr-system-info-list")));

        final InlineResponse200 firstOperationQueryResponse = createOperationQueryResponse(
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationEnum.INSTANTIATE,
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationStateEnum.PROCESSING);
        mockRestServer.expect(requestTo(new StringStartsWith("http://vnfm2:8080/vnf_lcm_op_occs")))
                .andRespond(withSuccess(gson.toJson(firstOperationQueryResponse), MediaType.APPLICATION_JSON));

        final InlineResponse200 secondOperationQueryReponse = createOperationQueryResponse(
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationEnum.INSTANTIATE,
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationStateEnum.COMPLETED);
        mockRestServer.expect(requestTo(new StringStartsWith("http://vnfm2:8080/vnf_lcm_op_occs")))
                .andRespond(withSuccess(gson.toJson(secondOperationQueryReponse), MediaType.APPLICATION_JSON));

        // Invoke the create request

        final ResponseEntity<CreateVnfResponse> createVnfResponse =
                controller.vnfCreate("myTestVnfId", createVnfRequest, "asadas", "so", "1213");
        assertEquals(HttpStatus.ACCEPTED, createVnfResponse.getStatusCode());
        assertNotNull(createVnfResponse.getBody().getJobId());

        final ArgumentCaptor<GenericVnf> genericVnfArgument = ArgumentCaptor.forClass(GenericVnf.class);
        final ArgumentCaptor<AAIResourceUri> uriArgument = ArgumentCaptor.forClass(AAIResourceUri.class);

        verify(aaiResourcesClient).update(uriArgument.capture(), genericVnfArgument.capture());

        assertEquals("/network/generic-vnfs/generic-vnf/myTestVnfId", uriArgument.getValue().build().toString());

        assertEquals("myTestVnfId", genericVnfArgument.getValue().getVnfId());
        assertEquals(1, genericVnfArgument.getValue().getRelationshipList().getRelationship().size());
        final Relationship createdRelationship =
                genericVnfArgument.getValue().getRelationshipList().getRelationship().get(0);
        assertEquals("esr-vnfm", createdRelationship.getRelatedTo());
        assertEquals("tosca.relationships.DependsOn", createdRelationship.getRelationshipLabel());
        assertEquals("/aai/v15/external-system/esr-vnfm-list/esr-vnfm/vnfm2", createdRelationship.getRelatedLink());

        // check the job status

        final ResponseEntity<QueryJobResponse> firstJobQueryResponse =
                controller.jobQuery(createVnfResponse.getBody().getJobId(), "", "so", "1213");
        assertEquals(OperationEnum.INSTANTIATE, firstJobQueryResponse.getBody().getOperation());
        assertEquals(OperationStateEnum.PROCESSING, firstJobQueryResponse.getBody().getOperationState());
        assertEquals(JAN_1_2019_12_00, firstJobQueryResponse.getBody().getStartTime());
        assertEquals(JAN_1_2019_1_00, firstJobQueryResponse.getBody().getStateEnteredTime());

        final ResponseEntity<QueryJobResponse> secondJobQueryResponse =
                controller.jobQuery(createVnfResponse.getBody().getJobId(), "", "so", "1213");
        assertEquals(OperationEnum.INSTANTIATE, secondJobQueryResponse.getBody().getOperation());
        assertEquals(OperationStateEnum.COMPLETED, secondJobQueryResponse.getBody().getOperationState());
        assertEquals(JAN_1_2019_12_00, secondJobQueryResponse.getBody().getStartTime());
        assertEquals(JAN_1_2019_1_00, secondJobQueryResponse.getBody().getStateEnteredTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createVnf_VnfAlreadyExistsOnVnfm_ThrowsIllegalArgumentException() throws Exception {
        final Tenant tenant =
                new Tenant().cloudOwner("myTestCloudOwner").regionName("myTestRegion").tenantId("myTestTenantId");
        final CreateVnfRequest createVnfRequest = new CreateVnfRequest().name("myTestName").tenant(tenant);

        final GenericVnf genericVnf = new GenericVnf();
        genericVnf.setVnfId("myTestVnfId");
        genericVnf.setNfType("vnfmType");
        genericVnf.setSelflink("http://vnfm:8080/vnfs/myTestVnfIdOnVnfm");

        doReturn(Optional.of(genericVnf)).when(aaiResourcesClient).get(eq(GenericVnf.class), MockitoHamcrest
                .argThat(new AaiResourceUriMatcher("/network/generic-vnfs/generic-vnf/myTestVnfId?depth=1")));

        final EsrSystemInfo esrSystemInfo = new EsrSystemInfo();
        esrSystemInfo.setServiceUrl("http://vnfm:8080");
        esrSystemInfo.setType("vnfmType");
        esrSystemInfo.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList = new EsrSystemInfoList();
        esrSystemInfoList.getEsrSystemInfo().add(esrSystemInfo);

        final EsrVnfm esrVnfm = new EsrVnfm();
        esrVnfm.setVnfmId("vnfm");
        esrVnfm.setEsrSystemInfoList(esrSystemInfoList);
        esrVnfm.setResourceVersion("1234");

        final EsrVnfmList esrVnfmList = new EsrVnfmList();
        esrVnfmList.getEsrVnfm().add(esrVnfm);

        final InlineResponse201 reponse = new InlineResponse201();
        mockRestServer.expect(requestTo(new URI("http://vnfm:8080/vnfs/myTestVnfIdOnVnfm")))
                .andRespond(withSuccess(gson.toJson(reponse), MediaType.APPLICATION_JSON));

        doReturn(Optional.of(esrVnfmList)).when(aaiResourcesClient).get(eq(EsrVnfmList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher("/external-system/esr-vnfm-list")));

        controller.vnfCreate("myTestVnfId", createVnfRequest, "asadas", "so", "1213");
    }

    @Test(expected = VnfmNotFoundException.class)
    public void createVnf_NoMatchingVnfmFound_ThrowsException() throws Exception {
        final Tenant tenant =
                new Tenant().cloudOwner("myTestCloudOwner").regionName("myTestRegion").tenantId("myTestTenantId");
        final CreateVnfRequest createVnfRequest = new CreateVnfRequest().name("myTestName").tenant(tenant);

        final GenericVnf genericVnf = new GenericVnf();
        genericVnf.setVnfId("myTestVnfId");
        genericVnf.setNfType("anotherType");

        doReturn(Optional.of(genericVnf)).when(aaiResourcesClient).get(eq(GenericVnf.class), MockitoHamcrest
                .argThat(new AaiResourceUriMatcher("/network/generic-vnfs/generic-vnf/myTestVnfId?depth=1")));

        final EsrSystemInfo esrSystemInfo1 = new EsrSystemInfo();
        esrSystemInfo1.setServiceUrl("http://vnfm1:8080");
        esrSystemInfo1.setType("vnfmType1");
        esrSystemInfo1.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList1 = new EsrSystemInfoList();
        esrSystemInfoList1.getEsrSystemInfo().add(esrSystemInfo1);

        final EsrVnfm esrVnfm1 = new EsrVnfm();
        esrVnfm1.setVnfmId("vnfm1");
        esrVnfm1.setEsrSystemInfoList(esrSystemInfoList1);
        esrVnfm1.setResourceVersion("1234");

        final EsrSystemInfo esrSystemInfo2 = new EsrSystemInfo();
        esrSystemInfo2.setServiceUrl("http://vnfm2:8080");
        esrSystemInfo2.setType("vnfmType2");
        esrSystemInfo2.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList2 = new EsrSystemInfoList();
        esrSystemInfoList2.getEsrSystemInfo().add(esrSystemInfo2);

        final EsrVnfm esrVnfm2 = new EsrVnfm();
        esrVnfm2.setVnfmId("vnfm2");
        esrVnfm2.setEsrSystemInfoList(esrSystemInfoList2);
        esrVnfm2.setResourceVersion("1234");

        final EsrVnfmList esrVnfmList = new EsrVnfmList();
        esrVnfmList.getEsrVnfm().add(esrVnfm1);
        esrVnfmList.getEsrVnfm().add(esrVnfm2);

        doReturn(Optional.of(esrVnfmList)).when(aaiResourcesClient).get(eq(EsrVnfmList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher("/external-system/esr-vnfm-list")));


        doReturn(Optional.of(esrSystemInfoList1)).when(aaiResourcesClient).get(eq(EsrSystemInfoList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher(
                        "/external-system/esr-vnfm-list/esr-vnfm/vnfm1/esr-system-info-list")));

        doReturn(Optional.of(esrSystemInfoList2)).when(aaiResourcesClient).get(eq(EsrSystemInfoList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher(
                        "/external-system/esr-vnfm-list/esr-vnfm/vnfm2/esr-system-info-list")));

        controller.vnfCreate("myTestVnfId", createVnfRequest, "asadas", "so", "1213");
    }

    @Test
    public void createVnf_VnfmAlreadyAssociatedWithVnf_Returns202AndJobId() throws Exception {
        final Tenant tenant =
                new Tenant().cloudOwner("myTestCloudOwner").regionName("myTestRegion").tenantId("myTestTenantId");
        final CreateVnfRequest createVnfRequest = new CreateVnfRequest().name("myTestName").tenant(tenant);

        final GenericVnf genericVnf = new GenericVnf();
        genericVnf.setVnfId("myTestVnfId");
        genericVnf.setNfType("vnfmType2");

        final Relationship relationshipToVnfm = new Relationship();
        relationshipToVnfm.setRelatedLink("/aai/v15/external-system/esr-vnfm-list/esr-vnfm/vnfm1");
        relationshipToVnfm.setRelatedTo("esr-vnfm");
        final RelationshipData relationshipData = new RelationshipData();
        relationshipData.setRelationshipKey("esr-vnfm.vnfm-id");
        relationshipData.setRelationshipValue("vnfm1");
        relationshipToVnfm.getRelationshipData().add(relationshipData);

        final RelationshipList relationshipList = new RelationshipList();
        relationshipList.getRelationship().add(relationshipToVnfm);
        genericVnf.setRelationshipList(relationshipList);

        doReturn(Optional.of(genericVnf)).when(aaiResourcesClient).get(eq(GenericVnf.class), MockitoHamcrest
                .argThat(new AaiResourceUriMatcher("/network/generic-vnfs/generic-vnf/myTestVnfId?depth=1")));

        final EsrSystemInfo esrSystemInfo1 = new EsrSystemInfo();
        esrSystemInfo1.setServiceUrl("http://vnfm1:8080");
        esrSystemInfo1.setType("vnfmType1");
        esrSystemInfo1.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList1 = new EsrSystemInfoList();
        esrSystemInfoList1.getEsrSystemInfo().add(esrSystemInfo1);

        final EsrVnfm esrVnfm1 = new EsrVnfm();
        esrVnfm1.setVnfmId("vnfm1");
        esrVnfm1.setEsrSystemInfoList(esrSystemInfoList1);
        esrVnfm1.setResourceVersion("1234");

        doReturn(Optional.of(esrVnfm1)).when(aaiResourcesClient).get(eq(EsrVnfm.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher("/external-system/esr-vnfm-list/esr-vnfm/vnfm1")));

        final ResponseEntity<CreateVnfResponse> response =
                controller.vnfCreate("myTestVnfId", createVnfRequest, "asadas", "so", "1213");
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody().getJobId());
    }

    @Test
    public void createVnf_UnauthorizedUser_Returns401() throws Exception {
        final TestRestTemplate restTemplateWrongPassword = new TestRestTemplate("test", "wrongPassword");
        final Tenant tenant =
                new Tenant().cloudOwner("myTestCloudOwner").regionName("myTestRegion").tenantId("myTestTenantId");
        final CreateVnfRequest createVnfRequest = new CreateVnfRequest().name("myTestName").tenant(tenant);

        final RequestEntity<CreateVnfRequest> request =
                RequestEntity.post(new URI("http://localhost:" + port + "/so/vnfm-adapter/v1/vnfs/myVnfId"))
                        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .header("X-ONAP-RequestId", "myRequestId").header("X-ONAP-InvocationID", "myInvocationId")
                        .body(createVnfRequest);
        final ResponseEntity<CreateVnfResponse> response =
                restTemplateWrongPassword.exchange(request, CreateVnfResponse.class);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    public void deleteVnf_ValidRequest_Returns202AndJobId() throws Exception {
        final TestRestTemplate restTemplate = new TestRestTemplate("test", "test");
        final RequestEntity<Void> request = RequestEntity
                .delete(new URI("http://localhost:" + port + "/so/vnfm-adapter/v1/vnfs/myVnfId"))
                .accept(MediaType.APPLICATION_JSON).header("X-ONAP-RequestId", "myRequestId")
                .header("X-ONAP-InvocationID", "myInvocationId").header("Content-Type", "application/json").build();
        final ResponseEntity<DeleteVnfResponse> deleteVnfResponse =
                restTemplate.exchange(request, DeleteVnfResponse.class);
        assertEquals(202, deleteVnfResponse.getStatusCode().value());
        assertNotNull(deleteVnfResponse.getBody().getJobId());


        final EsrSystemInfo esrSystemInfo = new EsrSystemInfo();
        esrSystemInfo.setServiceUrl("http://vnfm:8080");
        esrSystemInfo.setType("vnfmType");
        esrSystemInfo.setSystemType("VNFM");
        final EsrSystemInfoList esrSystemInfoList = new EsrSystemInfoList();
        esrSystemInfoList.getEsrSystemInfo().add(esrSystemInfo);

        doReturn(Optional.of(esrSystemInfoList)).when(aaiResourcesClient).get(eq(EsrSystemInfoList.class),
                MockitoHamcrest.argThat(new AaiResourceUriMatcher("/external-system/esr-vnfm-list/esr-vnfm/...")));

        final InlineResponse200 firstOperationQueryResponse = createOperationQueryResponse(
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationEnum.TERMINATE,
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationStateEnum.PROCESSING);
        mockRestServer.expect(requestTo(new StringStartsWith("http://vnfm:8080/vnf_lcm_op_occs")))
                .andRespond(withSuccess(gson.toJson(firstOperationQueryResponse), MediaType.APPLICATION_JSON));


        final InlineResponse200 secondOperationQueryReponse = createOperationQueryResponse(
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationEnum.TERMINATE,
                org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationStateEnum.COMPLETED);
        mockRestServer.expect(requestTo(new StringStartsWith("http://vnfm:8080/vnf_lcm_op_occs")))
                .andRespond(withSuccess(gson.toJson(secondOperationQueryReponse), MediaType.APPLICATION_JSON));


        final ResponseEntity<QueryJobResponse> firstJobQueryResponse =
                controller.jobQuery(deleteVnfResponse.getBody().getJobId(), "", "so", "1213");
        assertEquals(OperationEnum.TERMINATE, firstJobQueryResponse.getBody().getOperation());
        assertEquals(OperationStateEnum.PROCESSING, firstJobQueryResponse.getBody().getOperationState());
        assertEquals(JAN_1_2019_12_00, firstJobQueryResponse.getBody().getStartTime());
        assertEquals(JAN_1_2019_1_00, firstJobQueryResponse.getBody().getStateEnteredTime());

        final ResponseEntity<QueryJobResponse> secondJobQueryResponse =
                controller.jobQuery(deleteVnfResponse.getBody().getJobId(), "", "so", "1213");
        assertEquals(OperationEnum.TERMINATE, secondJobQueryResponse.getBody().getOperation());
        assertEquals(OperationStateEnum.PROCESSING, secondJobQueryResponse.getBody().getOperationState());
        assertEquals(JAN_1_2019_12_00, secondJobQueryResponse.getBody().getStartTime());
        assertEquals(JAN_1_2019_1_00, secondJobQueryResponse.getBody().getStateEnteredTime());
    }

    private InlineResponse200 createOperationQueryResponse(
            final org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationEnum operation,
            final org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse200.OperationStateEnum operationState) {
        final InlineResponse200 response = new InlineResponse200();
        response.setId("9876");
        response.setOperation(operation);
        response.setOperationState(operationState);
        response.setStartTime(JAN_1_2019_12_00);
        response.setStateEnteredTime(JAN_1_2019_1_00);
        response.setVnfInstanceId("myVnfInstanceId");
        return response;
    }

    private class AaiResourceUriMatcher extends BaseMatcher<AAIResourceUri> {

        final String uriAsString;

        public AaiResourceUriMatcher(final String uriAsString) {
            this.uriAsString = uriAsString;
        }

        @Override
        public boolean matches(final Object item) {
            if (item instanceof AAIResourceUri) {
                if (uriAsString.endsWith("...")) {
                    return ((AAIResourceUri) item).build().toString()
                            .startsWith(uriAsString.substring(0, uriAsString.indexOf("...")));
                }
                return ((AAIResourceUri) item).build().toString().equals(uriAsString);
            }
            return false;
        }

        @Override
        public void describeTo(final Description description) {}

    }


}