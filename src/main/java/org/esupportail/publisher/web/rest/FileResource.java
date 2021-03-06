/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.web.rest;

import javax.inject.Inject;

import com.codahale.metrics.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.httpclient.URIException;
import org.esupportail.publisher.security.SecurityConstants;
import org.esupportail.publisher.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jgribonvald on 22/01/16.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class FileResource {
    private static final Base64 decoder = new Base64(true);

    @Inject
    private FileService fileService;

    @RequestMapping(value = "/file/{entityId}/{isPublic}/{fileUri:.*}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(SecurityConstants.IS_ROLE_ADMIN + " || " + SecurityConstants.IS_ROLE_USER
        + " && hasPermission(#entityId,  '" + SecurityConstants.CTX_ORGANIZATION + "', '" + SecurityConstants.PERM_LOOKOVER + "')")
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long entityId, @PathVariable boolean isPublic, @PathVariable("fileUri") String fileUri) throws URIException {
        log.debug("REST request to delete File : {}", fileUri);
        boolean result;
        if (isPublic) {
            result = fileService.deleteInternalResource(StringUtils.newStringUtf8(decoder.decode(fileUri)));
        } else {
            result = fileService.deletePrivateResource(StringUtils.newStringUtf8(decoder.decode(fileUri)));
        }
        if (result)
            return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
