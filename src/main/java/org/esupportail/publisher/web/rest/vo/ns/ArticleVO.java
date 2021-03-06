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
package org.esupportail.publisher.web.rest.vo.ns;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.esupportail.publisher.domain.util.CustomDateTimeSerializer;
import org.esupportail.publisher.web.rest.util.ISO8601DateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.RFC822DateTimeSerializer;
import org.esupportail.publisher.web.rest.util.RFC822DateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.SeparatorListXMLAdapter;
import org.esupportail.publisher.web.rest.vo.LinkedFileVO;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@JsonTypeName(value = "article")
@XmlRootElement(name = "article")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleVO implements Serializable {

    private String title;

    private String link;

    private String enclosure;

    private String description;

    //Must be RFC2822 format
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = RFC822DateTimeSerializer.class)
    @XmlJavaTypeAdapter(RFC822DateTimeXmlAdapter.class)
    private DateTime pubDate;

    private int guid;

    @XmlElement(name="category")
    @XmlJavaTypeAdapter(SeparatorListXMLAdapter.class)
    private List<String> categories;

    @JsonProperty(value = "dc:creator")
    @XmlElement(name="creator", namespace = "http://purl.org/dc/elements/1.1/")
    private String creator;

    //Must be ISO8601 format
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonProperty(value = "dc:date")
    @XmlElement(name="date", namespace = "http://purl.org/dc/elements/1.1/")
    @XmlJavaTypeAdapter(ISO8601DateTimeXmlAdapter.class)
    private DateTime date;

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    private List<LinkedFileVO> files;

}
