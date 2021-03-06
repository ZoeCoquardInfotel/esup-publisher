package org.esupportail.publisher.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Filter;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.FilterType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.externals.IExternalUser;
import org.esupportail.publisher.repository.FilterRepository;
import org.esupportail.publisher.repository.UserRepository;
import org.esupportail.publisher.repository.externals.IExternalUserDao;
import org.esupportail.publisher.repository.predicates.FilterPredicates;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.ContextService;
import org.esupportail.publisher.service.UserService;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import com.mysema.query.types.Predicate;

@RunWith(PowerMockRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserServiceTest {
	
	@Mock
	private IPermissionService permissionService;
	
	@Mock
	private ContextService contextService;
	
	@Mock
	private IExternalUserDao externalUserDao;
	
	@Mock
	private UserDTOFactory userDTOFactory;

	private UserRepository userRepository;

	@Mock
	private FilterRepository filterRepository;
	
	@InjectMocks
	private UserService userService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseSearchIsNull() {
		//GIVEN
		String search = null;
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		assertThat(resultList.size()).isEqualTo(0);
		
	}

	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseSearchIsEmpty() {
		//GIVEN
		String search = "";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		assertThat(resultList.size()).isEqualTo(0);
	}
	
	@Test
	public void getUserFromSearchInCtx_ShouldBeEmpty_becauseLengthSearchSmallerThan3() {
		//GIVEN
		String search = "se";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		assertThat(resultList.size()).isEqualTo(0);
	}
	
	@Test
	public void getUserFromSearchInCtx_ifPermsIsNull_ShouldBeEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext()
				.getAuthentication(), contextKey)).thenReturn(null);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder.getContext()
				.getAuthentication(), contextKey1)).thenReturn(null);
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		assertThat(resultList.size()).isEqualTo(0);
	}
	
	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeADMIN_RootCtxIsNull_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);
		Filter filter = new Filter();
		
		List<IExternalUser> externalUserList = new ArrayList<>();
		List<UserDTO> userList = new ArrayList<>();
		
		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(null);
		when(externalUserDao.getUsersWithFilter(null, search)).thenReturn(externalUserList);
		when(userDTOFactory.asDTOList(externalUserList, false)).thenReturn(userList);
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		verify(externalUserDao).getUsersWithFilter(filter.getPattern(), search);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(userDTOFactory).asDTOList(externalUserList, false);
		assertThat(resultList.size()).isEqualTo(0);
	}
	
	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeADMIN_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		
		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.ADMIN, null);
		Filter filter = new Filter();
		
		List<IExternalUser> externalUserList = new ArrayList<>();
		List<UserDTO> userList = new ArrayList<>();
		
		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		when(contextService.getOrganizationCtxOfCtx(contextKey)).thenReturn(contextKey);
		when(filterRepository.findOne(FilterPredicates.ofTypeOfOrganization(contextKey.getKeyId(),
				FilterType.LDAP))).thenReturn(filter);
		when(externalUserDao.getUsersWithFilter(filter.getPattern(), search)).thenReturn(externalUserList);
		when(userDTOFactory.asDTOList(externalUserList, false)).thenReturn(userList);
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		verify(externalUserDao).getUsersWithFilter(filter.getPattern(), search);
		verify(contextService).getOrganizationCtxOfCtx(contextKey);
		verify(userDTOFactory).asDTOList(externalUserList, false);
		assertThat(resultList.size()).isEqualTo(0);
	}
	
	@Test
	public void getUserFromSearchInCtx__IfPermissionTypeIsCONTRIBUTORAndPermsSecondIsNull_shouldBeIsEmpty() {
		//GIVEN
		String search = "admin";
		ContextKey contextKey = Utils.contextKeyValue(new Long(1), ContextType.ORGANIZATION);
		ContextKey contextKey1 = Utils.contextKeyValue(new Long(2), ContextType.PUBLISHER);
		
		List<ContextKey> subContextKeys = Utils.subContextKeys(new ContextKey[] {contextKey, contextKey1});
		Pair<PermissionType, PermissionDTO> perms = new Pair<>(PermissionType.CONTRIBUTOR, null);
		
		//GIVEN SERVICE
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey)).thenReturn(perms);
		when(permissionService.getPermsOfUserInContext(SecurityContextHolder
				.getContext().getAuthentication(), contextKey1)).thenReturn(perms);
		
		//WHEN
		final List<UserDTO> resultList = userService.getUserFromSearchInCtx(contextKey, subContextKeys, search);
		
		//THEN
		verify(permissionService, times(2));
		assertThat(resultList.size()).isEqualTo(0);
	}

}
