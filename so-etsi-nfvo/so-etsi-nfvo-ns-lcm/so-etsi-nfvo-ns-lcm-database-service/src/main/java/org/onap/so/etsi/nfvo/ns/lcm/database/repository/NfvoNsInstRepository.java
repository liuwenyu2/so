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
package org.onap.so.etsi.nfvo.ns.lcm.database.repository;

import java.util.Optional;
import org.onap.so.etsi.nfvo.ns.lcm.database.beans.NfvoNsInst;
import org.onap.so.etsi.nfvo.ns.lcm.database.beans.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Waqas Ikram (waqas.ikram@est.tech)
 *
 */
public interface NfvoNsInstRepository extends JpaRepository<NfvoNsInst, String> {

    Optional<NfvoNsInst> findByName(final String name);

    Optional<NfvoNsInst> findByNsInstId(final String nsInstId);

    boolean existsNfvoNsInstByName(final String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE NfvoNsInst SET status = (:state) WHERE nsInstId = (:nsInstId)")
    int updateNsInstState(@Param("nsInstId") final String nsInstId, @Param("state") final State state);

}
