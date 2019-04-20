package pl.reryk.linkzone.service;

import pl.reryk.linkzone.dto.GroupCreate;
import pl.reryk.linkzone.dto.GroupUpdate;
import pl.reryk.linkzone.dto.IGroupResponseDto;
import pl.reryk.linkzone.exception.NotFoundException;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Group;
import pl.reryk.linkzone.permissions.Permissions;
import pl.reryk.linkzone.repository.AccountRepository;
import pl.reryk.linkzone.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, AccountRepository accountRepository) {
        this.groupRepository = groupRepository;
        this.accountRepository = accountRepository;
    }

    public Page<Group> findAll(Pageable pageable) {
        return groupRepository.findAll(pageable);
    }

    public Page<IGroupResponseDto> search(Pageable pageable, String name, Long accountId) {
        return groupRepository.search("%" + name + "%", accountId, pageable);
    }

    public IGroupResponseDto findDtoByName(String name, Long accountId) {
        return groupRepository.findDtoByName(name, accountId).orElseThrow(() -> new NotFoundException(Group.class, name));
    }

    public List<Group> findDefaultGroups() {
        return groupRepository.findByIsDefaultTrue();
    }

    public Group create(GroupCreate groupCreate, Account account) {
        Group group = new Group();
        group.setName(groupCreate.getName());
        group.setDescription(groupCreate.getDescription());
        group.setCreator(account);
        group.setDefault(false);
//        group = save(group);
//        group.addAdministrator(account);
        return save(group);
    }

    public List<IGroupResponseDto> getSubscribedGroups(Long userId, Long requestUserId) {
        return groupRepository.getSubscribedGroups(userId, requestUserId);
    }

    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @PreAuthorize("hasPermission(#group, '" + Permissions.UPDATE + "')")
    public void update(Group group, GroupUpdate updatedGroup) {
        group.setDescription(updatedGroup.getDescription());
        group.setName(updatedGroup.getName());
        group.setPostTypes(updatedGroup.getPostTypes());
        group.setAdministrators(accountRepository.findAllById(updatedGroup.getAdministrators()));
        group.setModerators(accountRepository.findAllById(updatedGroup.getModerators()));
        group.setTags(updatedGroup.getTags());
        save(group);
    }

    public List<IGroupResponseDto> getManagedGroups(Long userId, Long requestUserId) {
        return groupRepository.getManagedGroups(userId, requestUserId);
    }

    @PreAuthorize("hasPermission(#group, '" + Permissions.UPDATE + "')")
    public void updateGroupBannerUrl(String bannerUrl, Group group) {
        groupRepository.updateGroupBannerUrl(bannerUrl, group.getName());
    }

    @PreAuthorize("hasPermission(#group, '" + Permissions.UPDATE + "')")
    public void updateLogo(String logo, Group group) {
        groupRepository.updateLogo(logo, group.getName());
    }

    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Group.class, id.toString()));
    }

    public boolean existsById(Long aLong) {
        return groupRepository.existsById(aLong);
    }

    @PreAuthorize("hasPermission(#group, '" + Permissions.DELETE + "')")
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public void deleteNoPerm(Group group) {
        groupRepository.delete(group);
    }

    public void deleteAll() {
        groupRepository.deleteAll();
    }

    public Group findByName(String name) {
        return groupRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundException(Group.class, name));
    }

    @Transactional
    public Group findByNameFetchEager(String name) {
        Group g = findByName(name);
        // triggers fetching
        g.getAdministrators().size();
        g.getModerators().size(); // needed only for init in @SpringBootApp
        return g;
    }
}
