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
package org.esupportail.publisher.service.factories.impl;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.AbstractPermission;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.PermissionOnSubjects;
import org.esupportail.publisher.domain.PermissionOnSubjectsWithClassificationList;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.repository.PermissionOnSubjectsRepository;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.service.factories.CompositeKeyDTOFactory;
import org.esupportail.publisher.service.factories.EvaluatorDTOSelectorFactory;
import org.esupportail.publisher.service.factories.PermOnSubjDTOFactory;
import org.esupportail.publisher.service.factories.SubjectPermKeyDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.PermOnSubjDTO;
import org.esupportail.publisher.web.rest.dto.PermOnSubjWClassifDTO;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.SubjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 23 oct. 2014
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class PermOnSubjDTOFactoryImpl extends PermissionDTOFactoryImpl<PermOnSubjDTO, PermissionOnSubjects>
implements PermOnSubjDTOFactory {

    @Inject
    @Getter
    private transient PermissionOnSubjectsRepository dao;

    @Inject
    private transient EvaluatorDTOSelectorFactory evaluatorFactory;

    @Inject
    @Named("contextKeyDTOFactoryImpl")
    private transient CompositeKeyDTOFactory<ContextKeyDTO, ContextKey, Long, ContextType> contextConverter;

    @Inject
    private transient SubjectPermKeyDTOFactory subjectPermKeyConverter;

    public PermOnSubjDTOFactoryImpl() {
        super(PermOnSubjDTO.class, PermissionOnSubjects.class);
    }

    @Override
    public PermOnSubjDTO from(PermissionOnSubjects model) {
        log.debug("Model to DTO of {}", model);
        if (model != null) {
            SubjectDTO updatedBy = null;
            if (model.getLastModifiedBy() != null ) {
                updatedBy = getSubjectFactory().from(model.getLastModifiedBy());
            }
            return new PermOnSubjDTO(model.getId(),model.getCreatedDate(), model.getLastModifiedDate(),
                    getSubjectFactory().from(model.getCreatedBy()), updatedBy ,
                    contextConverter.convertToDTOKey(model.getContext()), evaluatorFactory.from(model.getEvaluator()),
                    subjectPermKeyConverter.convertToDTOKey(model.getRolesOnSubjects()) );
        }
        return null;
    }

    @Override
    public PermissionOnSubjects from(PermOnSubjDTO dtObject) throws ObjectNotFoundException {
        PermissionOnSubjects model = super.from(dtObject);
        model.setContext(contextConverter.convertToModelKey(dtObject.getContext()));
        model.setEvaluator(evaluatorFactory.from(dtObject.getEvaluator()));
        model.setRolesOnSubjects(subjectPermKeyConverter.convertToModelKey(dtObject.getRolesOnSubjects()));
        return model;
    }

    public boolean isDTOFactoryImpl(PermissionDTO dtoObject) {
        if (dtoObject instanceof PermOnSubjDTO && !(dtoObject instanceof PermOnSubjWClassifDTO)) {
            log.debug("PermOnSubjDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
       }
       return false;
    }

    public boolean isDTOFactoryImpl(AbstractPermission model) {
        if (model instanceof PermissionOnSubjects && !(model instanceof PermissionOnSubjectsWithClassificationList)) {
            log.debug("PermOnSubjDTOFactoryImpl selected : {}", this.getClass().getCanonicalName());
            return true;
        }
        return false;
    }

}
