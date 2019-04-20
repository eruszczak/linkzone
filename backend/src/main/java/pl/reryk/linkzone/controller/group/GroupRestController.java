package pl.reryk.linkzone.controller.group;

import pl.reryk.linkzone.dto.*;
import pl.reryk.linkzone.exception.AlreadyExistsException;
import pl.reryk.linkzone.exception.NotFoundException;
import pl.reryk.linkzone.exception.ValidationErrorException;
import pl.reryk.linkzone.model.Account;
import pl.reryk.linkzone.model.Group;
import pl.reryk.linkzone.model.GroupMembership;
import pl.reryk.linkzone.security.CurrentUser;
import pl.reryk.linkzone.security.UserPrincipal;
import pl.reryk.linkzone.service.AccountService;
import pl.reryk.linkzone.service.FileStorageService;
import pl.reryk.linkzone.service.GroupMembershipService;
import pl.reryk.linkzone.service.GroupService;
import pl.reryk.linkzone.utils.MultipartFileValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

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
    public ResponseEntity<?> list(Pageable pageable, @RequestParam(value = "name", required = false) String query, @CurrentUser UserPrincipal currentUser) {
        Long userId = currentUser != null ? currentUser.getAccount().getId() : - 1;
        Page<IGroupResponseDto> groups;
        if (StringUtils.isNotBlank(query)) {
            groups = groupService.search(pageable, query, userId);
        } else {
            groups = groupService.search(pageable, "", userId);
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
        Long userId = currentUser != null ? currentUser.getAccount().getId() : - 1;
        IGroupResponseDto groupDto = groupService.findDtoByName(name, userId);
        Group group = groupService.findByNameFetchEager(name);
        Account account = currentUser == null ? null : currentUser.getAccount();
        return new ResponseEntity<>(new GroupResponse(groupDto, group, account), HttpStatus.OK);
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
                                    Errors errors,
                                    @CurrentUser UserPrincipal currentUser) {
        Group group = groupService.findByNameFetchEager(name);
        // only validate name if it has changed
        if (!group.getName().equals(updatedGroup.getName())) {
            validateGroupName(updatedGroup.getName());
        }
        if (errors.hasErrors()) {
            throw new ValidationErrorException(errors);
        }
        if (currentUser.getAccount().isAdmin()) {
            group.setDefault(updatedGroup.isDefault());
        }
        groupService.update(group, updatedGroup);
        return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
    }

    @PostMapping(value = "/{name}/upload-banner")
    public UploadFileResponse uploadBanner(@RequestParam("data") MultipartFile file,
                                           @PathVariable("name") String groupName) {
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 400, 2000);
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

    @PostMapping(value = "/{name}/upload-logo")
    public UploadFileResponse uploadLogo(@RequestParam("data") MultipartFile file,
                                         @PathVariable("name") String groupName) {
        MultipartFileValidator.validate(file);
        MultipartFileValidator.validateImageDimensions(file, 50, 50);
        MultipartFileValidator.validateImageSize(file, 50);

        String fileName = fileStorageService.storeFile(file);
        Group group = groupService.findByName(groupName);
        groupService.updateLogo(fileName, group);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(fileName)
                .toUriString();

        fileStorageService.removeFile(group.getLogo());

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
        GroupMembership groupMembership = groupMembershipService.subscribe(group, currentUser.getAccount());
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
