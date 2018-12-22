package com.example.reddit.controller.group;

import com.example.reddit.dto.*;
import com.example.reddit.exception.AlreadyExistsException;
import com.example.reddit.exception.NotFoundException;
import com.example.reddit.exception.ValidationErrorException;
import com.example.reddit.model.Account;
import com.example.reddit.model.Group;
import com.example.reddit.model.GroupMembership;
import com.example.reddit.security.CurrentUser;
import com.example.reddit.security.UserPrincipal;
import com.example.reddit.service.AccountService;
import com.example.reddit.service.FileStorageService;
import com.example.reddit.service.GroupMembershipService;
import com.example.reddit.service.GroupService;
import com.example.reddit.utils.MultipartFileValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/groups")
public class GroupRestController {

    private final AccountService accountService;
    private final GroupService groupService;
    private final GroupMembershipService groupMembershipService;
    private final FileStorageService fileStorageService;

    @Autowired
    private GroupRestController(AccountService accountService,
                                GroupService groupService,
                                GroupMembershipService groupMembershipService,
                                FileStorageService fileStorageService) {
        this.accountService = accountService;
        this.groupService = groupService;
        this.groupMembershipService = groupMembershipService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> list(Pageable pageable, @RequestParam(value = "name", required = false) String name) {
        Page<Group> groups;
        // todo: sort by isSubbed
        System.out.println(name);
        if (StringUtils.isNotBlank(name)) {
            groups = groupService.search(pageable, name);
        } else {
            groups = groupService.findAll(pageable);
        }
        Page<GroupResponse> response = groups.map(GroupResponse::new);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> create(@Valid @RequestBody GroupCreate groupCreate,
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        validateGroupName(groupCreate.getName());
        groupService.create(groupCreate, currentUser.getAccount());
        return new ResponseEntity<>(groupCreate, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<?> detail(@PathVariable String name, @CurrentUser UserPrincipal currentUser) {
        Group group = groupService.findByNameFetchEager(name);
        boolean isSubbed = false;
        if (currentUser != null) {
            isSubbed = groupMembershipService.isUserSubbedToGroup(currentUser.getAccount().getUsername(), name);
        }
        Account account = currentUser == null ? null : currentUser.getAccount();
        return new ResponseEntity<>(new GroupResponse(group, isSubbed, account), HttpStatus.OK);
    }

    @GetMapping(value = "/checkName/{name}")
    public ResponseEntity<?> checkNameAvailability(@PathVariable("name") String name) {
        boolean isAvailable = true;
        // should be stripped? because that value will be added
        try {
            validateGroupName(name);
        } catch (AlreadyExistsException e) {
            isAvailable = false;
        }
        return new ResponseEntity<>(new IsAvailableResponse(isAvailable), HttpStatus.OK);
    }

    @PutMapping(value = "/{name}")
    public ResponseEntity<?> update(@PathVariable String name,
                                    @Valid @RequestBody GroupUpdate updatedGroup,
                                    Errors errors) {
        Group group = groupService.findByNameFetchEager(name);
        // only validate name if it has changed
        if (!group.getName().equals(updatedGroup.getName())) {
            validateGroupName(updatedGroup.getName());
        }
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        groupService.update(group, updatedGroup);
        return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
    }

    @PostMapping(value = "/{name}/upload-banner")
    public UploadFileResponse uploadBanner(@RequestParam("data") MultipartFile file,
                                           @PathVariable("name") String groupName) {
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 200, 1000);
        MultipartFileValidator.validateImageSize(file, 1000);

        String fileName = fileStorageService.storeFile(file);
        Group group = groupService.findByName(groupName);
        groupService.updateGroupBannerUrl(fileName, group);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(fileName)
                .toUriString();

        fileStorageService.removeFile(group.getBannerUrl());

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        Group group = groupService.findByName(name);
        groupService.delete(group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{name}/membership")
    public ResponseEntity<GroupMembership> subscribeGroup(@PathVariable String name,
                                                          @CurrentUser UserPrincipal currentUser) {
        Group group = groupService.findByName(name);
        GroupMembership groupMembership = new GroupMembership(group, currentUser.getAccount());
        try {
            groupMembershipService.save(groupMembership);
        } catch (DataIntegrityViolationException ignored) {
        }
        return new ResponseEntity<>(groupMembership, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{name}/membership")
    public ResponseEntity<Void> unsubscribeGroup(@PathVariable String name,
                                                 @CurrentUser UserPrincipal currentUser) {
        try {
            GroupMembership groupMembership = groupMembershipService
                    .findByAccountUsernameAndGroupName(currentUser.getUsername(), name);
            groupMembershipService.delete(groupMembership);
        } catch (NotFoundException ignored) {
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateGroupName(String name) {
        try {
            groupService.findByName(name);
            throw new AlreadyExistsException(Group.class, name);
        } catch (NotFoundException e) {

        }
    }
}
