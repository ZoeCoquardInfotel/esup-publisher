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
package org.esupportail.publisher.repository.externals.ldap;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Sets;
import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.externals.ExternalGroupHelper;
import org.esupportail.publisher.domain.externals.IExternalGroup;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.externals.IExternalGroupDao;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional(readOnly = true)
public class LdapServiceTest {

	private static final Logger log = LoggerFactory
			.getLogger(LdapServiceTest.class);

	@Inject
	private IExternalUserDao externalUserDao;

    @Inject
    private IExternalGroupDao externalGroupDao;

    @Inject
    private ExternalGroupHelper externalGroupHelper;

	@Autowired
	private LdapTemplate template;

	@Before
	public void beforeMethod() {
		try {
			externalUserDao.getUsersWithFilter("(ESCOUAI=0410017W)", "ALE");
		} catch (CommunicationException e) {
			log.warn("WARNING no LDAP connection acquired !");
			org.junit.Assume.assumeTrue(
					"WARNING no LDAP connection acquired !", false);
		}
	}

	@Test
	public void testFindUserWithFilter() {
		org.springframework.util.Assert.notNull(externalUserDao);
		List<IExternalUser> lus = externalUserDao.getUsersWithFilter(
				"(ESCOUAI=0410017W)", "ALE");
		Assert.assertNotNull(lus);
		Assert.assertTrue(!lus.isEmpty());
		Assert.assertNotNull(lus.get(0).getId());
		Assert.assertNotEquals(lus.get(0).getId(), "");

		lus = externalUserDao.getUsersWithFilter("(ESCOUAI=0410017W)", "ALE");

		for (IExternalUser lu : lus) {
			log.debug(lu.toString());
		}
	}

    @Test
    public void testFindGroupWithFilter() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        List<IExternalGroup> lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:FICTIF_0450822X:*)", "Profs Principaux", true);
        Assert.assertNotNull(lgps);
        Assert.assertTrue(!lgps.isEmpty());
        Assert.assertNotNull(lgps.get(0).getId());
        Assert.assertNotEquals(lgps.get(0).getId(), "");

        lgps = externalGroupDao.getGroupsWithFilter("(cn=esco:Etablissements:FICTIF_0450822X:*)", "Profs Principaux", true);

        for (IExternalGroup lgp : lgps) {
            log.debug(lgp.toString());
        }
    }

	@Test
	public void testFindUserUid() {
		org.springframework.util.Assert.notNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("F1000ugr");
		Assert.assertNotNull(lu);
		log.debug(lu.toString());

		Assert.assertNotNull(lu.getId());
		Assert.assertNotEquals(lu.getId(), "");
		Assert.assertNotNull(lu.getDisplayName());
		Assert.assertNotEquals(lu.getDisplayName(), "");
		Assert.assertNotNull(lu.getEmail());
		Assert.assertNotEquals(lu.getEmail(), "");
        log.debug("ESCOUAI : {}", lu.getAttribute("ESCOUAI"));
		Assert.assertNotNull(lu.getAttribute("ESCOUAI"));
		Assert.assertNotEquals(lu.getAttribute("ESCOUAI"), "");
	}

    @Test
    public void testFindGroupId() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs", true);
        Assert.assertNotNull(group);
        log.debug(group.toString());

        Assert.assertNotNull(group.getId());
        Assert.assertNotEquals(group.getId(), "");
        Assert.assertNotNull(group.getDisplayName());
        Assert.assertNotEquals(group.getDisplayName(), "");
        Assert.assertNotNull(group.getGroupMembers());
        Assert.assertTrue(!group.getGroupMembers().isEmpty());
        Assert.assertNotNull(group.getAttribute(externalGroupHelper.getGroupMembersAttribute()));
        Assert.assertTrue(!group.getAttribute(externalGroupHelper.getGroupMembersAttribute()).isEmpty());
    }


    @Test
    public void testFindGroupIdNotFound() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        IExternalGroup group = externalGroupDao.getGroupById("XXXXXXXX", false);
        Assert.assertNull(group);
    }

	@Test
	public void testFindUserUidNotFound() {
		org.springframework.util.Assert.notNull(externalUserDao);
		IExternalUser lu = externalUserDao.getUserByUid("XXXXXXX");
		Assert.assertNull(lu);

	}

    @Test
    public void testIsUserFoundFromFilter() {
        org.springframework.util.Assert.notNull(externalUserDao);
        final String StringFilter = "(isMemberOf=esco:Etablissements:FICTIF_0450822X*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F1000ugr");
        Assert.assertTrue(ret);
    }

    @Test
    public void testIsNotUserFoundFromFilter() {
        org.springframework.util.Assert.notNull(externalUserDao);
        final String StringFilter = "(isMemberOf=esco:Etablissements:FICTIF_0450822X*)";
        boolean ret = externalUserDao.isUserFoundWithFilter(StringFilter, "F08001ut");
        Assert.assertTrue(!ret);
    }

    @Test
    public void testGroupIsMemberOfGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:DIRECTION","esco:Applications:Publication_contenus:FICTIF_0450822X");
        Assert.assertTrue(ret);

        ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:DIRECTION","esco:Applications:Publication_contenus");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupWithDesigner() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:FICTIF_0450822X:PREMIERE GENERALE et TECHNO YC BT:Profs_1 S1", "esco:Etablissements:FICTIF_0450822X:Profs");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilter() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:FICTIF_0450822X:Tous_FICTIF*)(cn=esco:Applications:Publication_contenus:FICTIF_0450822X))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:FICTIF_0450822X:Administratifs:ORIENTATION");
        Assert.assertTrue(ret);

        ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:FICTIF_0450822X:Tous_FICTIF");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsMemberOfGroupFromFilterWithDesigner() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:FICTIF_0450822X:Tous_FICTIF)(cn=esco:Applications:Publication_contenus:FICTIF_0450822X))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "esco:Etablissements:FICTIF_0450822X:PREMIERE GENERALE et TECHNO YC BT:Profs_1 S1");
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsNotMemberOfGroupFromFilter() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        final String StringFilter = "(|(cn=esco:Etablissements:FICTIF_0450822X:Tous_FICTIF*)(cn=esco:Applications:Publication_contenus:FICTIF_0450822X))";
        boolean ret = externalGroupDao.isGroupMemberOfGroupFilter(StringFilter, "cfa:Etablissements:FICTIF_0333333Y:Administratifs:Tous_Administratifs");
        Assert.assertTrue(!ret);

    }

    @Test
    public void testGroupIsNotMemberOfGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        boolean ret = externalGroupDao.isGroupMemberOfGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs","esco:admin:central");
        Assert.assertFalse(ret);
    }

    @Test
    public void testGroupIsMemberOfAtLeastOneGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:FICTIF_0450822X","esco:Applications:GRR:FICTIF_0450822X");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs", groups);
        Assert.assertTrue(ret);

        groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:FICTIF_0450822X","esco:Etablissements:FICTIF_0450822X");
        ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs",groups);
        Assert.assertTrue(ret);
    }

    @Test
    public void testGroupIsNotMemberOfAtLeastOneGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:FICTIF_0450822X", "esco:Etablissements:FICTIF_0450822X:Profs");
        boolean ret = externalGroupDao.isGroupMemberOfAtLeastOneGroup("esco:Etablissements:FICTIF_0450822X:Administratifs:Tous_Administratifs", groups);
        Assert.assertFalse(ret);
    }

    @Test
    public void testUserIsMemberOfGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        boolean ret = externalGroupDao.isUserMemberOfGroup("F1000ugr", "esco:Applications:Publication_contenus:FICTIF_0450822X");
        Assert.assertTrue(ret);
    }
    @Test
    public void testUserIsNotMemberOfGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        boolean ret = externalGroupDao.isUserMemberOfGroup("F08001ut", "esco:Applications:Publication_contenus:FICTIF_0450822X");
        Assert.assertFalse(ret);
    }

    @Test
    public void testUserIsMemberOfAtLeastOneGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:admin:central","esco:Applications:mail:FICTIF_0450822X","esco:Applications:Publication_contenus:FICTIF_0450822X");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F1000ugr", groups);
        Assert.assertTrue(ret);
    }

    @Test
    public void testUserIsNotMemberOfAtLeastOneGroup() {
        org.springframework.util.Assert.notNull(externalGroupDao);
        Set<String> groups = Sets.newHashSet("esco:Applications:mail:FICTIF_0450822X","esco:Applications:Publication_contenus:FICTIF_0450822X","esco:Applications:GRR:FICTIF_0450822X");
        boolean ret = externalGroupDao.isUserMemberOfAtLeastOneGroup("F08001ut", groups);
        Assert.assertFalse(ret);
    }

}
