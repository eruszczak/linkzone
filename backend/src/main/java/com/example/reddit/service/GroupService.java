package com.example.reddit.service;

import com.example.reddit.dto.GroupCreate;
import com.example.reddit.dto.GroupUpdate;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.permissions.Permissions;
import com.example.reddit.repository.AccountRepository;
import com.example.reddit.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

    public Page<Group> search(Pageable pageable, String name) {
        return groupRepository.findByNameIgnoreCaseContaining(pageable, name);
    }

    public Group create(GroupCreate groupCreate, Account account) {
        Group group = new Group();
        group.setName(groupCreate.getName());
        group.setDescription(groupCreate.getDescription());
        group.setCreator(account);
//        group = save(group);
//        group.addAdministrator(account);
        return save(group);
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

    //    @PreAuthorize("hasPermission(#group, '" + Permissions.UPDATE + "')")
    public void updateGroupBannerUrl(String bannerUrl, Group group) {
        groupRepository.updateGroupBannerUrl(bannerUrl, group.getName());
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
