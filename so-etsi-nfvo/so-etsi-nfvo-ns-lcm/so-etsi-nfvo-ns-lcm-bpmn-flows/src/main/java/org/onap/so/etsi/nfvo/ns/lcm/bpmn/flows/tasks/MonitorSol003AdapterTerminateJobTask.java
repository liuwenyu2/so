/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
package org.onap.so.etsi.nfvo.ns.lcm.bpmn.flows.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.onap.so.adapters.etsisol003adapter.lcm.v1.model.DeleteVnfResponse;
import org.onap.so.adapters.etsisol003adapter.lcm.v1.model.OperationStateEnum;
import org.onap.so.etsi.nfvo.ns.lcm.bpmn.flows.extclients.vnfm.Sol003AdapterServiceProvider;
import org.onap.so.etsi.nfvo.ns.lcm.database.service.DatabaseServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.onap.so.etsi.nfvo.ns.lcm.bpmn.flows.CamundaVariableNameConstants.OPERATION_STATUS_PARAM_NAME;
import static org.onap.so.etsi.nfvo.ns.lcm.bpmn.flows.CamundaVariableNameConstants.DELETE_VNF_RESPONSE_PARAM_NAME;

/**
 * @author Waqas Ikram (waqas.ikram@est.tech)
 * @author Andrew Lamb (andrew.a.lamb@est.tech)
 *
 */
@Component
public class MonitorSol003AdapterTerminateJobTask extends MonitorSol003AdapterJobTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorSol003AdapterTerminateJobTask.class);

    @Autowired
    public MonitorSol003AdapterTerminateJobTask(final Sol003AdapterServiceProvider sol003AdapterServiceProvider,
            final DatabaseServiceProvider databaseServiceProvider) {
        super(sol003AdapterServiceProvider, databaseServiceProvider);
    }

    public void getCurrentOperationStatus(final DelegateExecution execution) {
        try {
            LOGGER.debug("Executing getCurrentOperationStatus  ...");
            final DeleteVnfResponse deleteVnfResponse =
                    (DeleteVnfResponse) execution.getVariable(DELETE_VNF_RESPONSE_PARAM_NAME);
            execution.setVariable(OPERATION_STATUS_PARAM_NAME,
                    getOperationStatus(execution, deleteVnfResponse.getJobId()));
            LOGGER.debug("Finished executing getCurrentOperationStatus ...");
        } catch (final Exception exception) {
            final String message = "Unable to invoke get current Operation status";
            LOGGER.error(message, exception);
            abortOperation(execution, message);

        }
    }

    /**
     * Log and throw exception on timeout for job status
     *
     * @param execution {@link org.onap.so.bpmn.common.DelegateExecutionImpl}
     */
    public void timeOutLogFailure(final DelegateExecution execution) {
        final String message = "Termination operation time out";
        LOGGER.error(message);
        abortOperation(execution, message);
    }

    /**
     * Check the final status of termination throw exception if not completed successfully
     *
     * @param execution {@link org.onap.so.bpmn.common.DelegateExecutionImpl}
     */
    public void checkIfOperationWasSuccessful(final DelegateExecution execution) {
        LOGGER.debug("Executing checkIfOperationWasSuccessful  ...");
        final OperationStateEnum operationStatus =
                (OperationStateEnum) execution.getVariable(OPERATION_STATUS_PARAM_NAME);
        final DeleteVnfResponse deleteVnfResponse =
                (DeleteVnfResponse) execution.getVariable(DELETE_VNF_RESPONSE_PARAM_NAME);

        if (operationStatus == null) {
            final String message =
                    "Unable to terminate, jobId: " + (deleteVnfResponse != null ? deleteVnfResponse.getJobId() : "null")
                            + "Unable to retrieve OperationStatus";
            LOGGER.error(message);
            abortOperation(execution, message);
        }
        if (operationStatus != OperationStateEnum.COMPLETED) {
            final String message =
                    "Unable to terminate, jobId: " + (deleteVnfResponse != null ? deleteVnfResponse.getJobId() : "null")
                            + " OperationStatus: " + operationStatus;
            LOGGER.error(message);
            abortOperation(execution, message);
        }

        LOGGER.debug("Successfully completed termination of job {}", deleteVnfResponse);
    }
}
