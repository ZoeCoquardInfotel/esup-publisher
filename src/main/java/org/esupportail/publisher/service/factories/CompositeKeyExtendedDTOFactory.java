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
package org.esupportail.publisher.service.factories;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.esupportail.publisher.web.rest.dto.ICompositeExtendedKey;

/**
 * Data Transfer Object factory for converting to and from models.
 * @author GIP RECIA - Julien Gribonvald
 * @param <DTOKEY> DTO type for the corresponding model
 * @param <MODELKEY> Model type
 * @param <VAL> Type of VAL of the composite key
 * @param <ATTR> Type of the Attribute of the composite key
 * @param <TYPE> Enum Type of the composite key
 */
public interface CompositeKeyExtendedDTOFactory<DTOKEY extends ICompositeExtendedKey<VAL, ATTR, TYPE>, MODELKEY extends ICompositeExtendedKey<VAL, ATTR, TYPE>, VAL extends Serializable, ATTR extends Serializable, TYPE extends Serializable> {

	/**
	 * Load model from storage based on the specified identifier.
	 *
	 * @param id
	 *            Load model for this identifier
	 *
	 * @return Model loaded from persistent storage
	 * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
	 */
	MODELKEY convertToModelKey(@NotNull final DTOKEY id);

	Set<MODELKEY> convertToModelKey(@NotNull final Set<DTOKEY> id);

	List<MODELKEY> convertToModelKey(@NotNull final List<DTOKEY> id);

	/**
	 * Load model from storage based on the specified identifier.
	 *
	 * @param set
	 *            Load model for this identifier
	 *
	 * @return Model loaded from persistent storage
	 * //@throws org.esupportail.publisher.service.exceptions.ObjectNotFoundException
	 */
	DTOKEY convertToDTOKey(@NotNull final MODELKEY set);

	Set<DTOKEY> convertToDTOKey(@NotNull final Set<MODELKEY> id);

	List<DTOKEY> convertToDTOKey(@NotNull final List<MODELKEY> id);

}
