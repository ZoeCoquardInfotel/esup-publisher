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
package org.esupportail.publisher.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.Organization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@Slf4j
public class FilterRepositoryTest {

	@Inject
	private FilterRepository repository;

	@Inject
	private OrganizationRepository orgRepo;

	final static String FILTER_INDICE_1 = "filter1";
	final static String FILTER_LDAP_PATTERN = "(ESCOUAI=0450822X)";
	final static String FILTER_GROUP_PATTERN = "esco:Etablissements:FICTIF_0450822X";

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());

		Organization o = orgRepo.saveAndFlush(ObjTest
				.newOrganization(FILTER_INDICE_1));

		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testInsertDuplicate() {
		Organization o = orgRepo.findAll().get(0);

		Filter f = ObjTest.newFilterLDAP(FILTER_LDAP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);

	}

	@Test()
	public void testInsert() {
		Organization o = orgRepo.findAll().get(0);

		Filter f = ObjTest.newFilterGroup(FILTER_GROUP_PATTERN, o);
		log.info("Before insert : " + f.toString());
		repository.saveAndFlush(f);

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		Organization o = orgRepo.findAll().get(0);
		repository
				.saveAndFlush(ObjTest.newFilterGroup(FILTER_GROUP_PATTERN, o));
		assertThat(repository.findAll().size(), is(2));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.exists(repository.findAll().get(0).getId()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertTrue(repository.count() == 1);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		long count = repository.count();
		repository.delete(repository.findAll().get(0));
		assertTrue(repository.count() == count - 1);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	public void testDeleteAll() {
		repository.deleteAll();
		assertTrue(repository.count() == 0);
	}

}
