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
package org.esupportail.publisher.web.rest.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@ToString(callSuper=true)
public class ResourceDTO extends ItemDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -9093926804044066760L;

    @Getter
    @Setter
    @NotNull
    @NonNull
    @Size(min=5, max= CstPropertiesLength.URL)
    private String ressourceUrl;

    /**
     * Constructor to use to create the object from the JPA model.
     * @param modelId
     * @param title
     * @param enclosure
     * @param startDate
     * @param endDate
     * @param validatedDate
     * @param validatedBy
     * @param status
     * @param summary
     * @param rssAllowed
     * @param highlight
     * @param organization
     * @param redactor
     * @param creationDate
     * @param lastUpdateDate
     * @param createdBy
     * @param lastUpdateBy
     * @param ressourceUrl
     */
    public ResourceDTO(@NotNull final Long modelId, @NotNull final String title, final String enclosure,
            @NotNull final String ressourceUrl, final LocalDate startDate, final LocalDate endDate,
            final DateTime validatedDate, final SubjectDTO validatedBy, @NotNull final ItemStatus status,
            @NotNull final String summary, final boolean rssAllowed, final boolean highlight, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor, @NotNull final DateTime creationDate, final DateTime lastUpdateDate,
            @NotNull final SubjectDTO createdBy, final SubjectDTO lastUpdateBy) {
        super(modelId, title, enclosure, startDate, endDate, validatedDate,
                validatedBy, status, summary, rssAllowed, highlight, organization, redactor,
                creationDate, lastUpdateDate, createdBy, lastUpdateBy);
        this.ressourceUrl = ressourceUrl;
    }

    /**
     * Constructor to use when creating a new Object.
     * @param createdBy
     * @param organization
     * @param redactor
     */
    public ResourceDTO(@NotNull final SubjectDTO createdBy, @NotNull final OrganizationDTO organization,
            @NotNull final RedactorDTO redactor) {
        super(createdBy, organization, redactor);
    }





}
