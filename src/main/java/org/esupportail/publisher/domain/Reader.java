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
package org.esupportail.publisher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.ItemTypeConverter;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 14 Juin 2014
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "name")
@ToString(callSuper = true, exclude = "organizationReaderRedactors")
@Entity
@Table(name = "T_READER")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Reader extends AbstractAutoGeneratedIdEntity implements Serializable {
	/** Serial ID. */
	private static final long serialVersionUID = 6035827413367362449L;

	/** This field corresponds to the database column name. */
	@NotNull
	@NonNull
	@Size(min = 3, max = CstPropertiesLength.NAME)
	@Column(unique = true, nullable = false, length = CstPropertiesLength.NAME)
	private String name;

	/** This field corresponds to the database column displayName. */
	@NotNull
	@NonNull
	@Size(min = 3, max = CstPropertiesLength.DISPLAYNAME)
	@Column(name = "display_name", nullable = false, length = CstPropertiesLength.DISPLAYNAME)
	private String displayName;

	/** This field corresponds to the database column description. */
	@NotNull
	@NonNull
	@Size(min = 3, max = CstPropertiesLength.DESCRIPTION)
	@Column(nullable = false, length = CstPropertiesLength.DESCRIPTION)
	private String description;

	@OneToMany(mappedBy = "context.reader", fetch = FetchType.LAZY)
	@JsonIgnore
    @JsonBackReference("reader-key")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<Publisher> organizationReaderRedactors = new HashSet<Publisher>();

    @NotNull
    @NonNull
    @Column(name="item_type", nullable = false, length = 255)
    @Convert(converter = ItemTypeConverter.class)
    //@JsonSerialize(using = CustomEnumSetSerializer.class)
    private Set<ItemType> authorizedTypes;

}
